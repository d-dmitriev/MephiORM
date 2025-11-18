package home.work.dto.simple;

import lombok.Data;

/**
 * Упрощённый DTO для сущности ProfileInfo.
 */
@Data
public class ProfileInfo {
    private String bio;
    private String avatarUrl;
    private String phone;
    private String location;
}
