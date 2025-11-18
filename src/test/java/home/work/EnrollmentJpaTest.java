package home.work;

import home.work.entities.Course;
import home.work.entities.Enrollment;
import home.work.entities.EnrollmentStatus;
import home.work.entities.User;
import home.work.repositories.CourseRepository;
import home.work.repositories.EnrollmentRepository;
import home.work.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class EnrollmentJpaTest {
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;

    private User student;
    private Course course;

    @BeforeEach
    void initEntities() {
        // получаем или создаём студента
        student = userRepository.findById(3L).orElseGet(() -> {
            User u = new User();
            u.setName("Auto Student");
            u.setEmail("auto_student@example.com");
            return userRepository.save(u);
        });

        // получаем или создаём курс
        course = courseRepository.findById(1L).orElseGet(() -> {
            Course c = new Course();
            c.setTitle("Auto Course");
            c.setDescription("Auto-created course for tests");
            return courseRepository.save(c);
        });
    }

    @Test
    void createEnrollment_and_verifyPersistence() {
        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(course);

        enrollmentRepository.save(e);

        assertNotNull(e.getId());
        assertNotNull(e.getEnrollDate());
        assertEquals(EnrollmentStatus.ACTIVE, e.getStatus());
        assertEquals(0.0, e.getProgress());

        Enrollment fromDb = enrollmentRepository.findById(e.getId()).orElseThrow();
        assertEquals(student.getId(), fromDb.getStudent().getId());
        assertEquals(course.getId(), fromDb.getCourse().getId());
        // enrollDate не позже текущего времени + маленькая дельта
        assertFalse(fromDb.getEnrollDate().isAfter(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void findByStudentIdAndCourseId_returnsSavedEnrollment() {
        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(course);
        enrollmentRepository.save(e);

        Optional<Enrollment> opt = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
        assertTrue(opt.isPresent());
        assertEquals(student.getId(), opt.get().getStudent().getId());
    }

    @Test
    void existsAndCountAndFindLists() {
        // создаём ещё одного студента и записываем его на тот же курс
        User other = userRepository.findById(4L).orElseGet(() -> {
            User u = new User();
            u.setName("Other Student");
            u.setEmail("other_student@example.com");
            return userRepository.save(u);
        });

        Enrollment e1 = new Enrollment();
        e1.setStudent(student);
        e1.setCourse(course);

        Enrollment e2 = new Enrollment();
        e2.setStudent(other);
        e2.setCourse(course);

        enrollmentRepository.save(e1);
        enrollmentRepository.save(e2);

        assertTrue(enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId()));
        long cnt = enrollmentRepository.countByCourseId(course.getId());
        assertTrue(cnt >= 2);

        List<Enrollment> byStudent = enrollmentRepository.findByStudentId(student.getId());
        assertFalse(byStudent.isEmpty());
        List<Enrollment> byCourse = enrollmentRepository.findByCourseId(course.getId());
        assertTrue(byCourse.size() >= 2);
    }

    @Test
    void uniqueConstraint_duplicateEnrollment_throwsException() {
        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(course);
        enrollmentRepository.save(e);

        Enrollment dup = new Enrollment();
        dup.setStudent(student);
        dup.setCourse(course);

        assertThrows(DataIntegrityViolationException.class, () -> {
            enrollmentRepository.saveAndFlush(dup);
        });
    }
}

