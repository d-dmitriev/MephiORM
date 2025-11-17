package home.work;

import home.work.entities.User;
import home.work.entities.UserRole;
import home.work.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuditingIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testAuditingFieldsAreAutomaticallySet() {
        User user = new User();
        user.setName("Audit Test User");
        user.setEmail("audit.test@example.com");
        user.setRole(UserRole.STUDENT);

        // Сохраняем пользователя
        User savedUser = userRepository.save(user);

        // Принудительно сбрасываем контекст persistence
        userRepository.flush();

        // Проверяем, что поля аудита установлены
        assertNotNull(savedUser.getCreatedAt(), "createdAt should not be null");
        assertNotNull(savedUser.getUpdatedAt(), "updatedAt should not be null");

        // Проверяем, что даты разумные (в пределах последних 5 секунд)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(savedUser.getCreatedAt().isBefore(now.plusSeconds(1)),
                "createdAt should be before now");
        assertTrue(savedUser.getCreatedAt().isAfter(now.minusSeconds(5)),
                "createdAt should be recent");

        // Проверяем, что created и updated одинаковы при создании
        assertEquals(savedUser.getCreatedAt(), savedUser.getUpdatedAt(),
                "createdAt and updatedAt should be equal on creation");
    }

    @Test
    void testUpdatedAtChangesOnUpdate() throws InterruptedException {
        User user = new User();
        user.setName("Update Test User");
        user.setEmail("update.test@example.com");
        user.setRole(UserRole.STUDENT);

        User savedUser = userRepository.save(user);
        LocalDateTime originalCreatedAt = savedUser.getCreatedAt();
        LocalDateTime originalUpdatedAt = savedUser.getUpdatedAt();

        // Явно очищаем persistence context чтобы гарантировать обновление
        userRepository.flush();

        // Обновляем пользователя
        savedUser.setName("Updated Name");
        User updatedUser = userRepository.save(savedUser);
        userRepository.flush(); // Принудительно сохраняем изменения

        // Проверяем, что createdAt не изменился
        assertEquals(originalCreatedAt, updatedUser.getCreatedAt(),
                "createdAt should not change on update");

        // Проверяем, что updatedAt изменился
        assertNotEquals(originalUpdatedAt, updatedUser.getUpdatedAt(),
                "updatedAt should change on update");
        assertTrue(updatedUser.getUpdatedAt().isAfter(originalUpdatedAt),
                "updatedAt should be after the original updatedAt");
    }

    @Test
    void testCreatedAtIsSetOnFirstSave() {
        User user = new User();
        user.setName("New User");
        user.setEmail("new@example.com");
        user.setRole(UserRole.STUDENT);

        assertNull(user.getCreatedAt(), "createdAt should be null before save");
        assertNull(user.getUpdatedAt(), "updatedAt should be null before save");

        User savedUser = userRepository.save(user);
        userRepository.flush();

        assertNotNull(savedUser.getCreatedAt(), "createdAt should be set after save");
        assertNotNull(savedUser.getUpdatedAt(), "updatedAt should be set after save");

        // При первом сохранении created_at и updated_at должны быть одинаковыми
        assertEquals(savedUser.getCreatedAt(), savedUser.getUpdatedAt(),
                "createdAt and updatedAt should be equal on first save");
    }
}
