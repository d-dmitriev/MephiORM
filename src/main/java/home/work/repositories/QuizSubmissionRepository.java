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
    /**
     * Находит все отправки викторины по идентификатору викторины.
     *
     * @param quizId Идентификатор викторины.
     * @return Список отправок для указанной викторины.
     */
    List<QuizSubmission> findByQuizId(Long quizId);

    /**
     * Находит все отправки викторины по идентификатору студента.
     *
     * @param studentId Идентификатор студента.
     * @return Список отправок для указанного студента.
     */
    List<QuizSubmission> findByStudentId(Long studentId);

    /**
     * Находит отправки викторины по идентификаторам студента и викторины.
     *
     * @param studentId Идентификатор студента.
     * @param quizId    Идентификатор викторины.
     * @return Список отправок, соответствующих критериям.
     */
    List<QuizSubmission> findByStudentIdAndQuizId(Long studentId, Long quizId);

    /**
     * Находит последнюю отправку викторины по идентификаторам студента и викторины.
     *
     * @param studentId Идентификатор студента.
     * @param quizId    Идентификатор викторины.
     * @return Опциональная последняя отправка, если она существует.
     */
    Optional<QuizSubmission> findFirstByStudentIdAndQuizIdOrderByAttemptNumberDesc(Long studentId, Long quizId);

    /**
     * Находит все отправки викторины вместе с информацией о викторине и студенте.
     *
     * @param quizId Идентификатор викторины.
     * @return Список отправок с деталями викторины и студента.
     */
    @Query("SELECT qs FROM QuizSubmission qs JOIN FETCH qs.quiz JOIN FETCH qs.student WHERE qs.quiz.id = :quizId")
    List<QuizSubmission> findByQuizIdWithDetails(Long quizId);

    /**
     * Находит все отправки викторины вместе с информацией о викторине и студенте.
     *
     * @param studentId Идентификатор студента.
     * @return Список отправок с деталями викторины и студента.
     */
    @Query("SELECT qs FROM QuizSubmission qs JOIN FETCH qs.quiz JOIN FETCH qs.student WHERE qs.student.id = :studentId")
    List<QuizSubmission> findByStudentIdWithDetails(Long studentId);

    /**
     * Находит максимальный номер попытки для заданного студента и викторины.
     *
     * @param studentId Идентификатор студента.
     * @param quizId    Идентификатор викторины.
     * @return Максимальный номер попытки.
     */
    @Query("SELECT MAX(qs.attemptNumber) FROM QuizSubmission qs WHERE qs.student.id = :studentId AND qs.quiz.id = :quizId")
    Integer findMaxAttemptNumber(Long studentId, Long quizId);

    /**
     * Вычисляет средний балл для указанной викторины.
     *
     * @param quizId Идентификатор викторины.
     * @return Средний балл викторины.
     */
    @Query("SELECT AVG(qs.score) FROM QuizSubmission qs WHERE qs.quiz.id = :quizId")
    Double findAverageScoreByQuizId(Long quizId);

    /**
     * Подсчитывает количество отправок для указанной викторины.
     *
     * @param quizId Идентификатор викторины.
     * @return Количество отправок для викторины.
     */
    Long countByQuizId(Long quizId);
}
