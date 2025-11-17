package home.work.mappers;

import home.work.dto.request.CreateLessonRequest;
import home.work.dto.request.UpdateLessonRequest;
import home.work.dto.simple.LessonSimple;
import home.work.entities.Lesson;
import home.work.entities.Module;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    LessonSimple toSimple(Lesson module);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "orderIndex", source = "request.orderIndex")
    @Mapping(target = "assignments", ignore = true)
    Lesson toEntity(CreateLessonRequest request, Module module);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "content", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "videoUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "duration", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "orderIndex", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "module", ignore = true)
    void updateLesson(UpdateLessonRequest moduleDetails, @MappingTarget Lesson profile);
}
