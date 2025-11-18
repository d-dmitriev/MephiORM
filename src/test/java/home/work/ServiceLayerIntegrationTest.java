package home.work;

import home.work.dto.request.*;
import home.work.dto.simple.*;
import home.work.services.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ServiceLayerIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private QuizService quizService;

    @Test
    void testCompleteUserWorkflow() {
        // Create teacher
        CreateUserRequest teacher = new CreateUserRequest();
        teacher.setName("Service Test Teacher");
        teacher.setEmail("service.teacher@example.com");
        teacher.setRole("TEACHER");
        UserSimple teacherCreated = userService.createUser(teacher);

        // Create student
        CreateUserRequest student = new CreateUserRequest();
        student.setName("Service Test Student");
        student.setEmail("service.student@example.com");
        student.setRole("STUDENT");
        UserSimple studentCreated = userService.createUser(student);

        // Create category
        CreateCategoryRequest category = new CreateCategoryRequest();
        category.setName("Service Test Category");
        CategorySimple categoryCreated = categoryService.createCategory(category);

        // Create course
        CreateCourseRequest course = new CreateCourseRequest();
        course.setTitle("Service Test Course");
        course.setDescription("Service Test Course Description");
        course.setCategoryId(categoryCreated.getId());
        course.setTeacherId(teacherCreated.getId());
        CourseSimple courseCreated = courseService.createCourse(course);

        assertNotNull(courseCreated.getId());

        // Enroll student
        EnrollmentSimple enrollment = enrollmentService.enrollStudentInCourse(courseCreated.getId(), studentCreated.getId());
        assertNotNull(enrollment.getId());
        assertEquals("ACTIVE", enrollment.getStatus());

        // Verify enrollment via userService helper
        assertTrue(userService.isUserEnrolledInCourse(studentCreated.getId(), courseCreated.getId()));

        // Надёжно получаем enrollment: сначала через getEnrollment, при ошибке — fallback по списку зачислений курса
        EnrollmentSimple fetchedEnrollment;
        try {
            fetchedEnrollment = enrollmentService.getEnrollment(studentCreated.getId(), courseCreated.getId());
        } catch (RuntimeException ex) {
            fetchedEnrollment = enrollmentService.getCourseEnrollments(courseCreated.getId()).stream()
                    .filter(e -> e.getId().equals(enrollment.getId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Enrollment not found after create", ex));
        }
        assertNotNull(fetchedEnrollment);
        assertEquals(enrollment.getId(), fetchedEnrollment.getId());
        assertEquals("ACTIVE", fetchedEnrollment.getStatus());

        // --- Добавленные проверки ---
        // Проверяем DTO курса через сервис
        var courseDto = courseService.getCourseDTO(courseCreated.getId());
        assertNotNull(courseDto);
        assertEquals(courseCreated.getId(), courseDto.getId());
        assertEquals(teacherCreated.getId(), courseDto.getTeacherId());
        assertEquals(categoryCreated.getId(), courseDto.getCategoryId());

        // Получаем enrollment через EnrollmentService.getEnrollment (studentId, courseId)
        EnrollmentSimple fetchedEnrollment2 = enrollmentService.getEnrollment(studentCreated.getId(), courseCreated.getId());
        assertNotNull(fetchedEnrollment2);
        assertEquals(enrollment.getId(), fetchedEnrollment2.getId());
        assertEquals("ACTIVE", fetchedEnrollment2.getStatus());

        // Добавляем ревью от студента и проверяем средний рейтинг курса
        var review = courseService.addCourseReview(courseCreated.getId(), studentCreated.getId(), 5, "Great course");
        assertNotNull(review);
        Double avg = courseService.getCourseAverageRating(courseCreated.getId());
        assertNotNull(avg);
        assertEquals(5.0, avg, 0.001);

        // Проверяем расчёт прогресса (на данном этапе ожидаем 0.0 или значение в диапазоне)
        Double progress = enrollmentService.calculateStudentProgress(studentCreated.getId(), courseCreated.getId());
        assertNotNull(progress);
        assertTrue(progress >= 0.0 && progress <= 1.0);
    }

    @Test
    void testAssignmentWorkflow() {
        // Setup: create course structure
        CreateUserRequest teacher = new CreateUserRequest();
        teacher.setName("Assignment Teacher");
        teacher.setEmail("assignment.teacher@example.com");
        teacher.setRole("TEACHER");
        UserSimple teacherCreated = userService.createUser(teacher);

        CreateUserRequest student = new CreateUserRequest();
        student.setName("Assignment Student");
        student.setEmail("assignment.student@example.com");
        student.setRole("STUDENT");
        UserSimple studentCreated = userService.createUser(student);

        CreateCategoryRequest category = new CreateCategoryRequest();
        category.setName("Assignment Category");
        CategorySimple categoryCreated = categoryService.createCategory(category);

        CreateCourseRequest course = new CreateCourseRequest();
        course.setTitle("Assignment Course");
        course.setTeacherId(teacherCreated.getId());
        course.setCategoryId(categoryCreated.getId());
        CourseSimple courseCreated  = courseService.createCourse(course);

        CreateModuleRequest module = new CreateModuleRequest();
        module.setTitle("Assignment Module");
        module.setCourseId(courseCreated.getId());
        ModuleSimple moduleCreated = moduleService.createModule(module);

        CreateLessonRequest lesson = new CreateLessonRequest();
        lesson.setTitle("Assignment Lesson");
        lesson.setModuleId(moduleCreated.getId());
        LessonSimple lessonCreated = lessonService.createLesson(lesson);

        // Create assignment
        CreateAssignmentRequest assignment = new CreateAssignmentRequest();
        assignment.setTitle("Service Test Assignment");
        assignment.setDescription("Test Description");
        assignment.setDueDate(LocalDateTime.now().plusDays(7));
        AssignmentSimple assignmentCreated = assignmentService.createAssignment(lessonCreated.getId(), assignment);

        assertNotNull(assignmentCreated.getId());

        // Submit assignment
        SubmissionSimple submission = assignmentService.submitAssignment(
                assignmentCreated.getId(), studentCreated.getId(), "My submission content");

        assertEquals("My submission content", submission.getContent());

        // --- Добавленные проверки ---
        // Оценим сабмит (grader)
        SubmissionSimple graded = assignmentService.gradeSubmission(submission.getId(), 95, "Excellent work");
        assertNotNull(graded);
        assertEquals(95, graded.getScore());
        assertEquals("Excellent work", graded.getFeedback());

        // Проверим, что в списке сабмитов студента присутствует этот сабмит
        var studentSubmissions = assignmentService.getStudentSubmissions(studentCreated.getId());
        assertFalse(studentSubmissions.isEmpty());
        assertTrue(studentSubmissions.stream().anyMatch(s -> s.getId().equals(submission.getId())));

        // Проверим прогресс студента по курсу (должен быть в диапазоне [0,1])
        Double progressAfter = enrollmentService.calculateStudentProgress(studentCreated.getId(), courseCreated.getId());
        assertNotNull(progressAfter);
        assertTrue(progressAfter >= 0.0 && progressAfter <= 1.0);
    }
}
