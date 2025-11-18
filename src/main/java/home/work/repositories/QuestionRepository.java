package home.work.repositories;

import home.work.entities.Question;
import home.work.entities.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями Question.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    /**
     * Находит все вопросы по идентификатору викторины.
     *
     * @param quizId Идентификатор викторины.
     * @return Список вопросов для указанной викторины.
     */
    List<Question> findByQuizId(Long quizId);

    /**
     * Находит все вопросы по идентификатору викторины, упорядоченные по идентификатору вопроса.
     *
     * @param quizId Идентификатор викторины.
     * @return Список вопросов для указанной викторины, упорядоченных по идентификатору вопроса.
     */
    List<Question> findByQuizIdOrderById(Long quizId);

    /**
     * Находит все вопросы по типу вопроса.
     *
     * @param type Тип вопроса.
     * @return Список вопросов указанного типа.
     */
    List<Question> findByType(QuestionType type);

    /**
     * Находит вопрос по идентификатору вместе с вариантами ответов.
     *
     * @param id Идентификатор вопроса.
     * @return Опциональный вопрос с вариантами ответов.
     */
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.answerOptions WHERE q.id = :id")
    Optional<Question> findByIdWithAnswerOptions(Long id);

    /**
     * Находит все вопросы по идентификатору викторины вместе с вариантами ответов.
     *
     * @param quizId Идентификатор викторины.
     * @return Список вопросов с вариантами ответов для указанной викторины.
     */
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.answerOptions WHERE q.quiz.id = :quizId")
    List<Question> findByQuizIdWithAnswerOptions(Long quizId);

    /**
     * Подсчитывает количество вопросов для заданной викторины.
     *
     * @param quizId Идентификатор викторины.
     * @return Количество вопросов.
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.quiz.id = :quizId")
    Long countByQuizId(Long quizId);
}
