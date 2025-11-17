package home.work.mappers;

import home.work.dto.request.CreateModuleRequest;
import home.work.dto.request.UpdateModuleRequest;
import home.work.dto.simple.ModuleSimple;
import home.work.entities.Course;
import home.work.entities.Module;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    ModuleSimple toSimple(Module module);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "course", source = "course")
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "quiz", ignore = true)
    Module toEntity(CreateModuleRequest request, Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "orderIndex", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "quiz", ignore = true)
    void updateModule(UpdateModuleRequest moduleDetails, @MappingTarget Module profile);
}
