package home.work.mappers;

import home.work.dto.request.CreateAssignmentRequest;
import home.work.dto.simple.AssignmentSimple;
import home.work.entities.Assignment;
import home.work.entities.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * * Маппер для сущности Assignment и её DTO.
 */
@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    AssignmentSimple toSimple(Assignment assignment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "lesson", source = "lesson")
    @Mapping(target = "submissions", ignore = true)
    @Mapping(target = "maxScore", ignore = true)
    Assignment toEntity(CreateAssignmentRequest request, Lesson lesson);
}
