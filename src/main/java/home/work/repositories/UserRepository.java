package home.work.repositories;

import home.work.entities.User;
import home.work.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Находит пользователя по его электронной почте.
     *
     * @param email Электронная почта пользователя.
     * @return Опциональный пользователь, если он существует.
     */
    Optional<User> findByEmail(String email);

    /**
     * Находит всех пользователей по их роли.
     *
     * @param role Роль пользователя.
     * @return Список пользователей с указанной ролью.
     */
    List<User> findByRole(UserRole role);

    /**
     * Проверяет существование пользователя по электронной почте.
     *
     * @param email Электронная почта пользователя.
     * @return true, если пользователь с данной электронной почтой существует, иначе false.
     */
    boolean existsByEmail(String email);

    /**
     * Находит пользователя по идентификатору вместе с его профилем.
     *
     * @param id Идентификатор пользователя.
     * @return Опциональный пользователь с профилем, если он существует.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = :id")
    Optional<User> findByIdWithProfile(Long id);

    /**
     * Находит всех студентов, записанных на курс по идентификатору курса.
     *
     * @param courseId Идентификатор курса.
     * @return Список студентов, записанных на указанный курс.
     */
    @Query("SELECT u FROM User u JOIN u.enrollments e WHERE e.course.id = :courseId")
    List<User> findStudentsByCourseId(Long courseId);
}
