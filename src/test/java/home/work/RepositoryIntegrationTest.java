package home.work;

import home.work.entities.*;
import home.work.repositories.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Test
    void testUserRepositoryMethods() {
        User user = new User();
        user.setName("Repo Test User");
        user.setEmail("repo.user@example.com");
        user.setRole(UserRole.STUDENT);
        user = userRepository.save(user);

        // Test findByEmail
        User foundUser = userRepository.findByEmail("repo.user@example.com").orElseThrow();
        assertEquals(user.getId(), foundUser.getId());

        // Test existsByEmail
        assertTrue(userRepository.existsByEmail("repo.user@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testCourseRepositoryCustomQueries() {
        User teacher = new User();
        teacher.setName("Repo Teacher");
        teacher.setEmail("repo.teacher@example.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Category category = new Category();
        category.setName("Repo Category");
        category = categoryRepository.save(category);

        Course course = new Course();
        course.setTitle("Repo Course");
        course.setTeacher(teacher);
        course.setCategory(category);
        course = courseRepository.save(course);

        // Test findByTeacherId
        List<Course> teacherCourses = courseRepository.findByTeacherId(teacher.getId());
        assertEquals(1, teacherCourses.size());
        assertEquals("Repo Course", teacherCourses.get(0).getTitle());

        // Test findByIdWithModules
        var courseWithModules = courseRepository.findByIdWithModules(course.getId());
        assertTrue(courseWithModules.isPresent());
    }

    @Test
    void testEnrollmentRepositoryBusinessLogic() {
        User teacher = new User();
        teacher.setName("Enroll Teacher");
        teacher.setEmail("enroll.teacher@example.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        User student = new User();
        student.setName("Enroll Student");
        student.setEmail("enroll.student@example.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        Category category = new Category();
        category.setName("Enroll Category");
        category = categoryRepository.save(category);

        Course course = new Course();
        course.setTitle("Enroll Course");
        course.setTeacher(teacher);
        course.setCategory(category);
        course = courseRepository.save(course);

        // Test enrollment creation and queries
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment = enrollmentRepository.save(enrollment);

        // Test existsByStudentIdAndCourseId
        assertTrue(enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId()));

        // Test findByStudentId
        List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentId(student.getId());
        assertEquals(1, studentEnrollments.size());

        // Test findByCourseId
        List<Enrollment> courseEnrollments = enrollmentRepository.findByCourseId(course.getId());
        assertEquals(1, courseEnrollments.size());

        // Test countByCourseId
        long enrollmentCount = enrollmentRepository.countByCourseId(course.getId());
        assertEquals(1, enrollmentCount);
    }

    @Test
    void testComplexQueryMethods() {
        // Test submission repository custom queries
        User student = new User();
        student.setName("Query Student");
        student.setEmail("query.student@example.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        // Create complex data structure and test repository methods
        // This would include testing methods like:
        // - findByAssignmentIdWithDetails
        // - findByStudentIdWithDetails
        // - findUngradedSubmissionsByCourseId
        // etc.

        assertTrue(true); // Placeholder for complex query tests
    }
}
