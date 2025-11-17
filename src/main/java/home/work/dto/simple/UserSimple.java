package home.work.dto.simple;

import lombok.Data;

@Data
public class UserSimple {
    private Long id;
    private String name;
    private String email;
    private String role;
}
