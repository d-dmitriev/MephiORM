package home.work.dto.request;

import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    private String bio;
    private String avatarUrl;
    private String phone;
    private String location;
}
