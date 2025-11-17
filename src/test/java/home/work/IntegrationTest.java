package home.work;

import home.work.entities.Course;
import home.work.entities.User;
import home.work.entities.UserRole;
import home.work.repositories.CourseRepository;
import home.work.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class IntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Test
    void testCreateCourseAndModules() {
        // Create teacher
        User teacher = new User();
        teacher.setName("John Teacher");
        teacher.setEmail("teacher@example.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        assertNotNull(teacher.getId());
        assertEquals(UserRole.TEACHER, teacher.getRole());
        assertEquals(5, teacher.getId());
    }

    @Test
    void testUserSelect() {
        Optional<User> user = userRepository.findById(3L);
        assertTrue(user.isPresent());
        assertTrue(Hibernate.isInitialized(user.get().getProfile()));
        assertEquals("Experienced Java developer and instructor", user.get().getProfile().getBio());
    }

    @Test
    @Transactional
    void testCourseSelect() {
        Optional<Course> course = courseRepository.findById(1L);
        assertTrue(course.isPresent());
        assertFalse(Hibernate.isInitialized(course.get().getTeacher()));
        assertEquals("Alice Johnson", course.get().getTeacher().getName());
    }

    @Test
    void testCourseSelectError() {
        Optional<Course> course = courseRepository.findById(1L);
        assertTrue(course.isPresent());
        assertFalse(Hibernate.isInitialized(course.get().getTeacher()));
        assertThrows(LazyInitializationException.class, () -> {
            assertEquals("Alice Johnson", course.get().getTeacher().getName());
        });
    }
}
