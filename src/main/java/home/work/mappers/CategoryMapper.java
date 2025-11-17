package home.work.mappers;

import home.work.dto.request.CreateCategoryRequest;
import home.work.dto.request.UpdateCategoryRequest;
import home.work.dto.simple.CategorySimple;
import home.work.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CreateCategoryRequest request);

    @Mapping(target = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategory(UpdateCategoryRequest categoryDetails, @MappingTarget Category profile);

    CategorySimple toSimple(Category category);
}
