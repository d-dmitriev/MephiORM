package home.work.repositories;

import home.work.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями Quiz.
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    /**
     * Находит викторину по идентификатору модуля.
     *
     * @param moduleId Идентификатор модуля.
     * @return Опциональная викторина.
     */
    Optional<Quiz> findByModuleId(Long moduleId);

    /**
     * Находит все викторины по идентификатору курса.
     *
     * @param courseId Идентификатор курса.
     * @return Список викторин для указанного курса.
     */
    List<Quiz> findByModuleCourseId(Long courseId);

    /**
     * Находит викторину по ее идентификатору вместе с ее связанными вопросами.
     *
     * @param id Идентификатор викторины.
     * @return Опциональная викторина с ее вопросами.
     */
    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :id")
    Optional<Quiz> findByIdWithQuestions(Long id);

    /**
     * Находит все викторины по идентификатору курса вместе с их связанными вопросами.
     *
     * @param courseId Идентификатор курса.
     * @return Список викторин с их вопросами для указанного курса.
     */
    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.module.course.id = :courseId")
    List<Quiz> findByCourseIdWithQuestions(Long courseId);

    /**
     * Подсчитывает количество викторин для заданного курса.
     *
     * @param courseId Идентификатор курса.
     * @return Количество викторин для указанного курса.
     */
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.module.course.id = :courseId")
    Long countByCourseId(Long courseId);
}
