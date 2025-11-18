package home.work.services;

import home.work.dto.request.CreateCourseRequest;
import home.work.dto.response.CourseResponse;
import home.work.dto.response.CourseWithModulesResponse;
import home.work.dto.response.ModuleResponse;
import home.work.dto.simple.CourseReviewSimple;
import home.work.dto.simple.CourseSimple;
import home.work.dto.simple.EnrollmentSimple;
import home.work.entities.*;
import home.work.mappers.CourseMapper;
import home.work.mappers.EnrollmentMapper;
import home.work.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления курсами, модулями, уроками и связанными сущностями.
 */
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CategoryRepository categoryRepository;
    private final CourseReviewRepository courseReviewRepository;

    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;

    @Transactional
    public CourseSimple createCourse(CreateCourseRequest course) {
        User teacher = userRepository.findById(course.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Category category = categoryRepository.findById(course.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return courseMapper.toSimple(courseRepository.save(courseMapper.toEntity(course, teacher, category)));
    }

    @Transactional
    public EnrollmentSimple enrollStudent(Long courseId, Long studentId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new RuntimeException("Student already enrolled in this course");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);

        return enrollmentMapper.toSimple(enrollmentRepository.save(enrollment));
    }

    public CourseResponse getCourseWithLazyModules(Long courseId) {
        return getCourseDTO(courseId);
    }

    @Transactional
    public List<CourseReviewSimple> getCourseReviewWithStudent(Long courseId) {
        return courseReviewRepository.findByCourseIdWithStudent(courseId).stream().map(courseMapper::toSimple).toList();
    }

    @Transactional
    public CourseWithModulesResponse getCourseFullStructure(Long courseId) {
        return getCourseWithModulesDTO(courseId);
    }

    @Transactional
    public CourseResponse getCourseDTO(Long courseId) {
        Course course = courseRepository.findByIdWithTeacherAndCategory(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getStartDate(),
                course.getCreatedAt(),
                course.getTeacher().getId(),
                course.getTeacher().getName(),
                course.getCategory().getId(),
                course.getCategory().getName()
        );
    }

    @Transactional
    public CourseWithModulesResponse getCourseWithModulesDTO(Long courseId) {
        Course course = courseRepository.findByIdWithModules(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        CourseWithModulesResponse dto = new CourseWithModulesResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getStartDate(),
                course.getCreatedAt(),
                course.getTeacher().getId(),
                course.getTeacher().getName(),
                course.getCategory().getId(),
                course.getCategory().getName()
        );

        List<ModuleResponse> moduleDTOs = course.getModules().stream()
                .map(module -> new ModuleResponse(
                        module.getId(),
                        module.getTitle(),
                        module.getDescription(),
                        module.getOrderIndex()
                ))
                .toList();

        dto.setModules(moduleDTOs);
        return dto;
    }

    public List<CourseSimple> getAllCourses() {
        return courseRepository.findAll().stream().map(courseMapper::toSimple).toList();
    }

    public List<CourseSimple> getCoursesByCategory(String categoryName) {
        return courseRepository.findByCategoryName(categoryName).stream().map(courseMapper::toSimple).toList();
    }

    public List<CourseSimple> getCoursesByTag(String tagName) {
        return courseRepository.findByTagName(tagName).stream().map(courseMapper::toSimple).toList();
    }

    @Transactional
    public CourseReviewSimple addCourseReview(Long courseId, Long studentId, Integer rating, String comment) {
        if (courseReviewRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
            throw new RuntimeException("Student has already reviewed this course");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setStudent(student);
        review.setRating(rating);
        review.setComment(comment);

        return courseMapper.toSimple(courseReviewRepository.save(review));
    }

    public Double getCourseAverageRating(Long courseId) {
        return courseReviewRepository.findAverageRatingByCourseId(courseId);
    }
}