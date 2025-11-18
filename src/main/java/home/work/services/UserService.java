package home.work.services;

import home.work.dto.composite.UserWithProfile;
import home.work.dto.request.CreateUserRequest;
import home.work.dto.request.UpdateUserProfileRequest;
import home.work.dto.simple.ProfileInfo;
import home.work.dto.simple.UserSimple;
import home.work.entities.*;
import home.work.mappers.ProfileMapper;
import home.work.mappers.UserMapper;
import home.work.repositories.EnrollmentRepository;
import home.work.repositories.ProfileRepository;
import home.work.repositories.SubmissionRepository;
import home.work.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления пользователями и их профилями.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;

    /**
     * Создает нового пользователя.
     *
     * @param user данные для создания пользователя
     * @return созданный пользователь в упрощенном виде
     */
    @Transactional
    public UserSimple createUser(CreateUserRequest user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.error("User with email {} already exists", user.getEmail());
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        User createdUser = userRepository.save(userMapper.toEntity(user));
        return userMapper.toSimple(createdUser);
    }

    /**
     * Получает пользователя по его идентификатору вместе с профилем.
     *
     * @param userId идентификатор пользователя
     * @return пользователь с профилем
     */
    public UserWithProfile getUserById(Long userId) {
        return userRepository.findByIdWithProfile(userId).map(userMapper::toFull)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });
    }

    /**
     * Получает пользователя по его email.
     *
     * @param email email пользователя
     * @return пользователь в упрощенном виде
     */
    public UserSimple getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toSimple)
                .orElseThrow(() -> {
                    log.error("User with email {} not found", email);
                    return new RuntimeException("User not found with email: " + email);
                });
    }

    /**
     * Получает профиль пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return информация о профиле пользователя
     */
    public ProfileInfo getUserProfile(Long userId) {
        return profileRepository.findByUserId(userId).map(profileMapper::toInfo)
                .orElseThrow(() -> {
                    log.error("Profile not found for user id {}", userId);
                    return new RuntimeException("Profile not found for user id: " + userId);
                });
    }

    /**
     * Обновляет профиль пользователя.
     *
     * @param userId         идентификатор пользователя
     * @param profileDetails данные для обновления профиля
     * @return обновленная информация о профиле пользователя
     */
    @Transactional
    public ProfileInfo updateUserProfile(Long userId, UpdateUserProfileRequest profileDetails) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Profile not found for user id {}", userId);
                    return new RuntimeException("Profile not found for user id: " + userId);
                });

        profileMapper.updateProfile(profileDetails, profile);
        return profileMapper.toInfo(profileRepository.save(profile));
    }

    /**
     * Получает список всех преподавателей.
     *
     * @return список преподавателей в упрощенном виде
     */
    public List<UserSimple> getTeachers() {
        return userRepository.findByRole(UserRole.TEACHER).stream().map(userMapper::toSimple).toList();
    }

    /**
     * Получает список всех студентов.
     *
     * @return список студентов в упрощенном виде
     */
    public List<UserSimple> getStudents() {
        return userRepository.findByRole(UserRole.STUDENT).stream().map(userMapper::toSimple).toList();
    }

    /**
     * Получает список всех записей пользователя на курсы.
     *
     * @param userId идентификатор пользователя
     * @return список записей на курсы
     */
    public List<Enrollment> getUserEnrollments(Long userId) {
        return enrollmentRepository.findByStudentId(userId);
    }

    /**
     * Получает список всех отправленных пользователем заданий.
     *
     * @param userId идентификатор пользователя
     * @return список отправленных заданий
     */
    public List<Submission> getUserSubmissions(Long userId) {
        return submissionRepository.findByStudentIdWithDetails(userId);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });

        // Проверяем можно ли удалить пользователя
        if (user.getRole() == UserRole.TEACHER && !user.getCoursesTaught().isEmpty()) {
            log.error("Cannot delete teacher who is assigned to courses");
            throw new RuntimeException("Cannot delete teacher who is assigned to courses");
        }

        // Удаляем связанные сущности в правильном порядке
        profileRepository.findByUserId(user.getId()).ifPresent(profileRepository::delete);

        userRepository.delete(user);
    }

    /**
     * Проверяет, записан ли пользователь на определенный курс.
     *
     * @param userId   идентификатор пользователя
     * @param courseId идентификатор курса
     * @return true, если пользователь записан на курс, иначе false
     */
    public boolean isUserEnrolledInCourse(Long userId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(userId, courseId);
    }
}
