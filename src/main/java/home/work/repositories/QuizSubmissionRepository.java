package home.work.repositories;

import home.work.entities.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями QuizSubmission.
 */
@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    List<QuizSubmission> findByQuizId(Long quizId);

    List<QuizSubmission> findByStudentId(Long studentId);

    List<QuizSubmission> findByStudentIdAndQuizId(Long studentId, Long quizId);

    Optional<QuizSubmission> findFirstByStudentIdAndQuizIdOrderByAttemptNumberDesc(Long studentId, Long quizId);

    @Query("SELECT qs FROM QuizSubmission qs JOIN FETCH qs.quiz JOIN FETCH qs.student WHERE qs.quiz.id = :quizId")
    List<QuizSubmission> findByQuizIdWithDetails(Long quizId);

    @Query("SELECT qs FROM QuizSubmission qs JOIN FETCH qs.quiz JOIN FETCH qs.student WHERE qs.student.id = :studentId")
    List<QuizSubmission> findByStudentIdWithDetails(Long studentId);

    @Query("SELECT MAX(qs.attemptNumber) FROM QuizSubmission qs WHERE qs.student.id = :studentId AND qs.quiz.id = :quizId")
    Integer findMaxAttemptNumber(Long studentId, Long quizId);

    @Query("SELECT AVG(qs.score) FROM QuizSubmission qs WHERE qs.quiz.id = :quizId")
    Double findAverageScoreByQuizId(Long quizId);

    Long countByQuizId(Long quizId);
}
