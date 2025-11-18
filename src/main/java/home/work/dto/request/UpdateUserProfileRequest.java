package home.work.dto.request;

import lombok.Data;

/**
 * DTO для обновления профиля пользователя
 */
@Data
public class UpdateUserProfileRequest {
    private String bio;
    private String avatarUrl;
    private String phone;
    private String location;
}
