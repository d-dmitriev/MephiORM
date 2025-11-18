package home.work.repositories;

import home.work.entities.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    List<AnswerOption> findByQuestionId(Long questionId);

    List<AnswerOption> findByQuestionIdAndIsCorrect(Long questionId, Boolean isCorrect);

    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.quiz.id = :quizId")
    List<AnswerOption> findByQuizId(Long quizId);

    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.quiz.id = :quizId AND ao.isCorrect = true")
    List<AnswerOption> findCorrectAnswersByQuizId(Long quizId);

    Long countByQuestionId(Long questionId);
}
