package home.work.mappers;

import home.work.dto.request.CreateTagRequest;
import home.work.dto.simple.TagSimple;
import home.work.entities.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для сущности Tag и её DTO.
 */
@Mapper(componentModel = "spring")
public interface TagMapper {
    TagSimple toSimple(Tag tag);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Tag toEntity(CreateTagRequest request);
}
