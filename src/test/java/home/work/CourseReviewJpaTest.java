package home.work;

import home.work.entities.Course;
import home.work.entities.CourseReview;
import home.work.entities.User;
import home.work.repositories.CourseRepository;
import home.work.repositories.CourseReviewRepository;
import home.work.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CourseReviewJpaTest {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseReviewRepository courseReviewRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createCourseReview() {
        Course course = courseRepository.findById(1L).orElseThrow();
        User student = userRepository.findById(3L).orElseThrow();

        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setStudent(student);
        review.setRating(1);
        review.setComment("Test comment");

        courseReviewRepository.save(review);
        // Проверяем что дата заполнилась
        assertNotNull(review.getCreatedAt());

        // --- Добавленные проверки ---
        // id присвоен
        assertNotNull(review.getId());

        // данные действительно сохранены и читаются из репозитория
        CourseReview fromDb = courseReviewRepository.findById(review.getId()).orElseThrow();
        assertEquals(1, fromDb.getRating());
        assertEquals("Test comment", fromDb.getComment());
        assertEquals(student.getId(), fromDb.getStudent().getId());
        assertEquals(course.getId(), fromDb.getCourse().getId());
        assertNotNull(fromDb.getCreatedAt());
        // createdAt не позднее чем сейчас + небольшая дельта
        assertFalse(fromDb.getCreatedAt().isAfter(LocalDateTime.now().plusSeconds(1)));

        // Проверка уникального ограничения (дублирование student+course) — ожидаем исключение при flush
        CourseReview dup = new CourseReview();
        dup.setCourse(course);
        dup.setStudent(student);
        dup.setRating(2);
        dup.setComment("Duplicate test");
        assertThrows(DataIntegrityViolationException.class, () -> {
            courseReviewRepository.saveAndFlush(dup);
        });
    }

    @Test
    void findByStudentIdAndCourseId_returnsSavedReview() {
        Course course = courseRepository.findById(1L).orElseThrow();
        User student = userRepository.findById(3L).orElseThrow();

        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setStudent(student);
        review.setRating(4);
        review.setComment("Find test");

        courseReviewRepository.save(review);

        var opt = courseReviewRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
        assertTrue(opt.isPresent());
        assertEquals(4, opt.get().getRating());
        assertNotNull(opt.get().getCreatedAt());
    }

    @Test
    void findByCourseIdWithStudent_fetchesStudent() {
        Course course = courseRepository.findById(1L).orElseThrow();
        User s2 = userRepository.findById(2L).orElseThrow();
        User s3 = userRepository.findById(3L).orElseThrow();

        CourseReview r1 = new CourseReview();
        r1.setCourse(course);
        r1.setStudent(s2);
        r1.setRating(5);
        r1.setComment("WithStudent 1");

        CourseReview r2 = new CourseReview();
        r2.setCourse(course);
        r2.setStudent(s3);
        r2.setRating(3);
        r2.setComment("WithStudent 2");

        courseReviewRepository.saveAll(Arrays.asList(r1, r2));

        List<CourseReview> list = courseReviewRepository.findByCourseIdWithStudent(course.getId());
        assertFalse(list.isEmpty());
        // проверяем, что среди результата есть отзыв от s2 и что студент загружен (можно обратиться к id)
        assertTrue(list.stream().anyMatch(cr -> cr.getStudent() != null && cr.getStudent().getId().equals(s2.getId())));
    }

    @Test
    void averageAndCountAndFindCourseIdsWithAverageAbove() {
        Course course = courseRepository.findById(1L).orElseThrow();
        User s2 = userRepository.findById(2L).orElseThrow();
        User s3 = userRepository.findById(3L).orElseThrow();

        // Создаём два отзыва для курса: рейтинги 5 и 3 => среднее 4.0
        CourseReview r1 = new CourseReview();
        r1.setCourse(course);
        r1.setStudent(s2);
        r1.setRating(5);
        r1.setComment("Avg test 1");

        CourseReview r2 = new CourseReview();
        r2.setCourse(course);
        r2.setStudent(s3);
        r2.setRating(3);
        r2.setComment("Avg test 2");

        courseReviewRepository.saveAll(Arrays.asList(r1, r2));

        Double avg = courseReviewRepository.findAverageRatingByCourseId(course.getId());
        assertNotNull(avg);
        assertEquals(4.0, avg, 0.001);

        Long count = courseReviewRepository.countByCourseId(course.getId());
        assertEquals(2L, count);

        List<Object[]> res = courseReviewRepository.findCourseIdsWithAverageRatingAbove(3.9);
        assertFalse(res.isEmpty());
        // каждый элемент: [courseId, avg]
        boolean containsCourse = res.stream().anyMatch(o -> ((Number) o[0]).longValue() == course.getId());
        assertTrue(containsCourse);
    }
}
