package home.work;

import home.work.dto.response.CourseResponse;
import home.work.dto.request.CreateAssignmentRequest;
import home.work.dto.request.CreateCategoryRequest;
import home.work.dto.request.CreateCourseRequest;
import home.work.dto.request.CreateUserRequest;
import home.work.dto.simple.*;
import home.work.entities.Course;
import home.work.entities.Lesson;
import home.work.entities.Module;
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
//        assertEquals(teacherCreated.getId(), courseCreated.getTeacher().getId());

        // Enroll student
        EnrollmentSimple enrollment = enrollmentService.enrollStudentInCourse(courseCreated.getId(), studentCreated.getId());
        assertNotNull(enrollment.getId());
        assertEquals("ACTIVE", enrollment.getStatus());

        // Verify enrollment
        assertTrue(userService.isUserEnrolledInCourse(studentCreated.getId(), courseCreated.getId()));
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

        Module module = new Module();
        module.setTitle("Assignment Module");
//        module.setCourse(courseCreated);
        module = moduleService.createModule(courseCreated.getId(), module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Assignment Lesson");
        lesson.setModule(module);
        lesson = lessonService.createLesson(module.getId(), lesson);

        // Create assignment
        CreateAssignmentRequest assignment = new CreateAssignmentRequest();
        assignment.setTitle("Service Test Assignment");
        assignment.setDescription("Test Description");
        assignment.setDueDate(LocalDateTime.now().plusDays(7));
//        assignment.setMaxScore(100);
        AssignmentSimple assignmentCreated = assignmentService.createAssignment(lesson.getId(), assignment);

        assertNotNull(assignmentCreated.getId());
//        assertEquals(lesson.getId(), assignmentCreated.getLesson().getId());

        // Submit assignment
        SubmissionSimple submission = assignmentService.submitAssignment(
                assignmentCreated.getId(), studentCreated.getId(), "My submission content");

//        assertNotNull(submission.getId());
        assertEquals("My submission content", submission.getContent());

        // Grade assignment
//        Submission gradedSubmission = assignmentService.gradeSubmission(
//                submission.getId(), 85, "Good work!");

//        assertEquals(85, gradedSubmission.getScore());
//        assertEquals("Good work!", gradedSubmission.getFeedback());
    }

//    @Test
//    void testLazyLoadingScenario() {
//        // This test demonstrates the LazyInitializationException scenario
//        CreateUserRequest teacher = new CreateUserRequest();
//        teacher.setName("Lazy Teacher");
//        teacher.setEmail("lazy.teacher@example.com");
//        teacher.setRole("TEACHER");
//        UserSimple teacherCreated = userService.createUser(teacher);
//
//        CreateCategoryRequest category = new CreateCategoryRequest();
//        category.setName("Lazy Category");
//        CategorySimple categoryCreated = categoryService.createCategory(category);
//
//        CreateCourseRequest course = new CreateCourseRequest();
//        course.setTitle("Lazy Course");
//        course.setTeacherId(teacherCreated.getId());
//        course.setCategoryId(categoryCreated.getId());
//        CourseSimple courseCreated = courseService.createCourse(course);
//
//        // Add modules to course
//        Module module = new Module();
//        module.setTitle("Lazy Module");
////        module.setCourse(courseCreated);
//        moduleService.createModule(courseCreated.getId(), module);
//
//        // Get course without initializing modules (this would cause LazyInitializationException)
//        CourseResponse lazyCourse = courseService.getCourseWithLazyModules(courseCreated.getId());
//
//        // Accessing modules outside transaction should cause exception
////        assertThrows(org.hibernate.LazyInitializationException.class, () -> {
////            lazyCourse.getModules().size();
////        });
//
//        // But this should work fine within transactional context
//        Course eagerCourse = courseService.getCourseWithModules(courseCreated.getId());
//        assertEquals(1, eagerCourse.getModules().size());
//    }
}
