package home.work.mappers;

import home.work.dto.request.CreateCategoryRequest;
import home.work.dto.request.UpdateCategoryRequest;
import home.work.dto.simple.CategorySimple;
import home.work.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Маппер для сущности Category и её DTO.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Category toEntity(CreateCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "courses", ignore = true)
    void updateCategory(UpdateCategoryRequest categoryDetails, @MappingTarget Category profile);

    CategorySimple toSimple(Category category);
}
