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
    Optional<Profile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("SELECT p FROM Profile p JOIN FETCH p.user WHERE p.user.id = :userId")
    Optional<Profile> findByUserIdWithUser(Long userId);
}