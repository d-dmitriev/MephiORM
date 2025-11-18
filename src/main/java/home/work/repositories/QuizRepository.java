package home.work.repositories;

import home.work.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByModuleId(Long moduleId);

    List<Quiz> findByModuleCourseId(Long courseId);

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :id")
    Optional<Quiz> findByIdWithQuestions(Long id);

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.module.course.id = :courseId")
    List<Quiz> findByCourseIdWithQuestions(Long courseId);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.module.course.id = :courseId")
    Long countByCourseId(Long courseId);
}
