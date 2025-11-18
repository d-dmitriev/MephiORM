package home.work.mappers;

import home.work.dto.simple.EnrollmentSimple;
import home.work.entities.Enrollment;
import home.work.entities.EnrollmentStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

/**
 * Маппер для сущности Enrollment и её DTO.
 */
@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    @Mapping(target = "studentId", source = "enrollment.student.id")
    @Mapping(target = "courseId", source = "enrollment.course.id")
    EnrollmentSimple toSimple(Enrollment enrollment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "enrollDate", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "progress", ignore = true)
    @Mapping(target = "status", source = "status")
    void updateEnrollmentStatus(String status, @MappingTarget Enrollment enrollment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "enrollDate", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "progress", source = "progress")
    void updateEnrollmentProgress(Double progress, @MappingTarget Enrollment enrollment);

    @AfterMapping
    default void validateAndUpdate(@MappingTarget Enrollment enrollment) {
        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            enrollment.setCompletedAt(LocalDateTime.now());
            enrollment.setProgress(1.0);
        }
        if (enrollment.getProgress() >= 1.0) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
        }
    }
}
