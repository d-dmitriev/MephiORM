package home.work.mappers;

import home.work.dto.request.CreateTagRequest;
import home.work.dto.simple.TagSimple;
import home.work.entities.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagSimple toSimple(Tag tag);
    Tag toEntity(CreateTagRequest request);
}
