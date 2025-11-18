package home.work.mappers;

import home.work.dto.composite.UserWithProfile;
import home.work.dto.request.CreateUserRequest;
import home.work.dto.simple.UserSimple;
import home.work.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserSimple toSimple(User user);

    UserWithProfile toFull(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "coursesTaught", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    @Mapping(target = "quizSubmissions", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    User toEntity(CreateUserRequest user);
}
