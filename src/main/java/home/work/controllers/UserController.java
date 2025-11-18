package home.work.controllers;

import home.work.dto.request.CreateUserRequest;
import home.work.dto.request.UpdateUserProfileRequest;
import home.work.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления пользователями.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Создать нового пользователя.
     *
     * @param user Данные пользователя
     * @return Созданный пользователь
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    /**
     * Получить пользователя по идентификатору.
     *
     * @param id Идентификатор пользователя
     * @return Пользователь
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Получить пользователя по email.
     *
     * @param email Email пользователя
     * @return Пользователь
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Получить профиль пользователя по идентификатору.
     *
     * @param id Идентификатор пользователя
     * @return Профиль пользователя
     */
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    /**
     * Обновить профиль пользователя.
     *
     * @param id             Идентификатор пользователя
     * @param profileDetails Обновленные данные профиля
     * @return Обновленный профиль пользователя
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserProfileRequest profileDetails) {
        return ResponseEntity.ok(userService.updateUserProfile(id, profileDetails));
    }

    /**
     * Получить всех учителей.
     *
     * @return Список учителей
     */
    @GetMapping("/teachers")
    public ResponseEntity<?> getTeachers() {
        return ResponseEntity.ok(userService.getTeachers());
    }

    /**
     * Получить всех студентов.
     *
     * @return Список студентов
     */
    @GetMapping("/students")
    public ResponseEntity<?> getStudents() {
        return ResponseEntity.ok(userService.getStudents());
    }

    /**
     * Получить все курсы, на которые записан пользователь.
     *
     * @param id Идентификатор пользователя
     * @return Список курсов
     */
    @GetMapping("/{id}/enrollments")
    public ResponseEntity<?> getUserEnrollments(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserEnrollments(id));
    }

    /**
     * Получить все отправленные задания пользователя.
     *
     * @param id Идентификатор пользователя
     * @return Список отправленных заданий
     */
    @GetMapping("/{id}/submissions")
    public ResponseEntity<?> getUserSubmissions(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserSubmissions(id));
    }

    /**
     * Удалить пользователя по идентификатору.
     *
     * @param id Идентификатор пользователя
     * @return Ответ об успешном удалении
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
