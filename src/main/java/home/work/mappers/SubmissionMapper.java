package home.work.mappers;

import home.work.dto.simple.SubmissionSimple;
import home.work.entities.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для сущности Submission и её DTO.
 */
@Mapper(componentModel = "spring")
public interface SubmissionMapper {
    @Mapping(target = "assignmentId", source = "submission.assignment.id")
    @Mapping(target = "studentId", source = "submission.student.id")
    SubmissionSimple toSimple(Submission submission);
}
