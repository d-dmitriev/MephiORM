package home.work.mappers;

import home.work.dto.request.CreateCourseRequest;
import home.work.dto.simple.CourseReviewSimple;
import home.work.dto.simple.CourseSimple;
import home.work.entities.Category;
import home.work.entities.Course;
import home.work.entities.CourseReview;
import home.work.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для сущности Course и её DTO.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseSimple toSimple(Course course);

    CourseReviewSimple toSimple(CourseReview review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "duration", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "teacher", source = "teacher")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "modules", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Course toEntity(CreateCourseRequest request, User teacher, Category category);
}
