package home.work;

import home.work.entities.Quiz;
import home.work.entities.QuizSubmission;
import home.work.entities.User;
import home.work.repositories.QuizRepository;
import home.work.repositories.QuizSubmissionRepository;
import home.work.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class QuizSubmissionJpaTest {
    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private UserRepository userRepository;

    private Quiz quiz;
    private User s2;
    private User s3;

    @BeforeEach
    void initEntities() {
        // загрузим или создадим quiz
        quiz = quizRepository.findById(1L).orElseGet(() -> {
            Quiz q = new Quiz();
            q.setTitle("Auto Quiz");
            q.setDescription("Auto-created quiz for tests");
            return quizRepository.save(q);
        });

        // загрузим или создадим студентов с id=2 и id=3 (если их нет)
        s2 = userRepository.findById(2L).orElseGet(() -> {
            User u = new User();
            u.setName("Student2");
            u.setEmail("s2@example.com");
            return userRepository.save(u);
        });

        s3 = userRepository.findById(3L).orElseGet(() -> {
            User u = new User();
            u.setName("Student3");
            u.setEmail("s3@example.com");
            return userRepository.save(u);
        });
    }

    @Test
    void createQuizSubmission_and_verifyPersistence() {
        QuizSubmission qs = new QuizSubmission();
        qs.setQuiz(quiz);
        qs.setStudent(s3);
        qs.setScore(90);
        qs.setAttemptNumber(1);

        quizSubmissionRepository.save(qs);

        assertNotNull(qs.getId());
        assertNotNull(qs.getTakenAt());
        QuizSubmission fromDb = quizSubmissionRepository.findById(qs.getId()).orElseThrow();
        assertEquals(90, fromDb.getScore());
        assertEquals(1, fromDb.getAttemptNumber());
        assertEquals(s3.getId(), fromDb.getStudent().getId());
        assertEquals(quiz.getId(), fromDb.getQuiz().getId());
    }

    @Test
    void findByStudentIdAndQuizId_and_findFirstByStudentOrderByAttemptDesc() {
        QuizSubmission a1 = new QuizSubmission();
        a1.setQuiz(quiz);
        a1.setStudent(s2);
        a1.setScore(70);
        a1.setAttemptNumber(1);

        QuizSubmission a2 = new QuizSubmission();
        a2.setQuiz(quiz);
        a2.setStudent(s2);
        a2.setScore(85);
        a2.setAttemptNumber(2);

        quizSubmissionRepository.save(a1);
        quizSubmissionRepository.save(a2);

        List<QuizSubmission> list = quizSubmissionRepository.findByStudentIdAndQuizId(s2.getId(), quiz.getId());
        assertFalse(list.isEmpty());
        Optional<QuizSubmission> last = quizSubmissionRepository.findFirstByStudentIdAndQuizIdOrderByAttemptNumberDesc(s2.getId(), quiz.getId());
        assertTrue(last.isPresent());
        assertEquals(2, last.get().getAttemptNumber());
        assertEquals(85, last.get().getScore());
    }

    @Test
    void findByQuizIdWithDetails_and_findByStudentIdWithDetails() {
        QuizSubmission s = new QuizSubmission();
        s.setQuiz(quiz);
        s.setStudent(s3);
        s.setScore(78);
        s.setAttemptNumber(1);
        quizSubmissionRepository.save(s);

        List<QuizSubmission> byQuiz = quizSubmissionRepository.findByQuizIdWithDetails(quiz.getId());
        assertFalse(byQuiz.isEmpty());
        assertTrue(byQuiz.stream().allMatch(x -> x.getQuiz() != null && x.getStudent() != null));

        List<QuizSubmission> byStudent = quizSubmissionRepository.findByStudentIdWithDetails(s3.getId());
        assertFalse(byStudent.isEmpty());
        assertTrue(byStudent.stream().allMatch(x -> x.getQuiz() != null && x.getStudent() != null));
    }

    @Test
    void findMaxAttempt_averageScore_and_countByQuiz() {
        // создаём несколько записей для агрегатов
        QuizSubmission u1 = new QuizSubmission();
        u1.setQuiz(quiz);
        u1.setStudent(s2);
        u1.setScore(60);
        u1.setAttemptNumber(1);

        QuizSubmission u2 = new QuizSubmission();
        u2.setQuiz(quiz);
        u2.setStudent(s2);
        u2.setScore(80);
        u2.setAttemptNumber(3);

        QuizSubmission u3 = new QuizSubmission();
        u3.setQuiz(quiz);
        u3.setStudent(s3);
        u3.setScore(90);
        u3.setAttemptNumber(1);

        quizSubmissionRepository.save(u1);
        quizSubmissionRepository.save(u2);
        quizSubmissionRepository.save(u3);

        Integer maxAttempt = quizSubmissionRepository.findMaxAttemptNumber(s2.getId(), quiz.getId());
        assertNotNull(maxAttempt);
        assertEquals(3, maxAttempt);

        Double avg = quizSubmissionRepository.findAverageScoreByQuizId(quiz.getId());
        assertNotNull(avg);
        // среднее из 60,80,90 = 76.666...
        assertTrue(avg > 76.0 && avg < 77.5);

        Long cnt = quizSubmissionRepository.countByQuizId(quiz.getId());
        assertNotNull(cnt);
        assertTrue(cnt >= 3);
    }
}

