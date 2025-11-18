package home.work.repositories;

import home.work.entities.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Интерфейс репозитория для управления сущностями AnswerOption.
 */
@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    /**
     * Находит все варианты ответов, связанные с заданным вопросом.
     *
     * @param questionId Идентификатор вопроса.
     * @return Список вариантов ответов.
     */
    List<AnswerOption> findByQuestionId(Long questionId);

    /**
     * Находит варианты ответов по идентификатору вопроса и признаку правильности.
     *
     * @param questionId Идентификатор вопроса.
     * @param isCorrect  Признак правильности ответа.
     * @return Список вариантов ответов, соответствующих критериям.
     */
    List<AnswerOption> findByQuestionIdAndIsCorrect(Long questionId, Boolean isCorrect);

    /**
     * Находит все варианты ответов, связанные с заданным викториной.
     *
     * @param quizId Идентификатор викторины.
     * @return Список вариантов ответов.
     */
    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.quiz.id = :quizId")
    List<AnswerOption> findByQuizId(Long quizId);

    /**
     * Находит все правильные варианты ответов для заданной викторины.
     *
     * @param quizId Идентификатор викторины.
     * @return Список правильных вариантов ответов.
     */
    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.quiz.id = :quizId AND ao.isCorrect = true")
    List<AnswerOption> findCorrectAnswersByQuizId(Long quizId);

    /**
     * Подсчитывает количество вариантов ответов для заданного вопроса.
     *
     * @param questionId Идентификатор вопроса.
     * @return Количество вариантов ответов.
     */
    Long countByQuestionId(Long questionId);
}
