package home.work.services;

import home.work.dto.composite.UserWithProfile;
import home.work.dto.request.CreateUserRequest;
import home.work.dto.request.UpdateUserProfileRequest;
import home.work.dto.simple.ProfileInfo;
import home.work.dto.simple.UserSimple;
import home.work.entities.*;
import home.work.mappers.ProfileMapper;
import home.work.mappers.UserMapper;
import home.work.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления пользователями и их профилями.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;

    @Transactional
    public UserSimple createUser(CreateUserRequest user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        User createdUser = userRepository.save(userMapper.toEntity(user));
        return userMapper.toSimple(createdUser);
    }

//    @Transactional
//    public User createUserWithProfile(User user, Profile profile) {
//        User savedUser = createUser(user);
//        profile.setUser(savedUser);
//        profileRepository.save(profile);
//        return savedUser;
//    }

    public UserWithProfile getUserById(Long userId) {
        return userRepository.findByIdWithProfile(userId).map(userMapper::toFull)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public UserSimple getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toSimple)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public ProfileInfo getUserProfile(Long userId) {
        return profileRepository.findByUserId(userId).map(profileMapper::toInfo)
                .orElseThrow(() -> new RuntimeException("Profile not found for user id: " + userId));
    }

    @Transactional
    public ProfileInfo updateUserProfile(Long userId, UpdateUserProfileRequest profileDetails) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user id: " + userId));

        profileMapper.updateProfile(profileDetails, profile);
        return profileMapper.toInfo(profileRepository.save(profile));
    }

    public List<UserSimple> getTeachers() {
        return userRepository.findByRole(UserRole.TEACHER).stream().map(userMapper::toSimple).toList();
    }

    public List<UserSimple> getStudents() {
        return userRepository.findByRole(UserRole.STUDENT).stream().map(userMapper::toSimple).toList();
    }

//    public List<User> getStudentsByCourse(Long courseId) {
//        return userRepository.findStudentsByCourseId(courseId);
//    }

    public List<Enrollment> getUserEnrollments(Long userId) {
        return enrollmentRepository.findByStudentId(userId);
    }

    public List<Submission> getUserSubmissions(Long userId) {
        return submissionRepository.findByStudentIdWithDetails(userId);
    }

//    public List<QuizSubmission> getUserQuizSubmissions(Long userId) {
//        return quizSubmissionRepository.findByStudentIdWithDetails(userId);
//    }
//
//    public List<CourseReview> getUserCourseReviews(Long userId) {
//        return courseReviewRepository.findByStudentId(userId);
//    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Проверяем можно ли удалить пользователя
        if (user.getRole() == UserRole.TEACHER && !user.getCoursesTaught().isEmpty()) {
            throw new RuntimeException("Cannot delete teacher who is assigned to courses");
        }

        // Удаляем связанные сущности в правильном порядке
        profileRepository.findByUserId(user.getId()).ifPresent(profileRepository::delete);

        userRepository.delete(user);
    }

    public boolean isUserEnrolledInCourse(Long userId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(userId, courseId);
    }

    public Double getUserAverageScore(Long userId) {
        List<Submission> submissions = submissionRepository.findByStudentId(userId);
        if (submissions.isEmpty()) {
            return 0.0;
        }

        return submissions.stream()
                .filter(s -> s.getScore() != null)
                .mapToInt(Submission::getScore)
                .average()
                .orElse(0.0);
    }
}
