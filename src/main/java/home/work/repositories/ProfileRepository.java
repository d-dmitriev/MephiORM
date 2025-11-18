package home.work.repositories;

import home.work.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями Profile.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    /**
     * Находит профиль по идентификатору пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Опциональный профиль.
     */
    Optional<Profile> findByUserId(Long userId);

    /**
     * Проверяет, существует ли профиль для указанного идентификатора пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return true, если профиль существует, иначе false.
     */
    boolean existsByUserId(Long userId);

    /**
     * Находит профиль по идентификатору пользователя вместе с информацией о пользователе.
     *
     * @param userId Идентификатор пользователя.
     * @return Опциональный профиль с информацией о пользователе.
     */
    @Query("SELECT p FROM Profile p JOIN FETCH p.user WHERE p.user.id = :userId")
    Optional<Profile> findByUserIdWithUser(Long userId);
}