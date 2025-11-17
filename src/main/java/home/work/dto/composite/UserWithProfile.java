package home.work.dto.composite;

import home.work.dto.simple.ProfileInfo;
import lombok.Data;

@Data
public class UserWithProfile {
    private Long id;
    private String name;
    private String email;
    private String role;
    private ProfileInfo profile; // вложенный DTO
}
