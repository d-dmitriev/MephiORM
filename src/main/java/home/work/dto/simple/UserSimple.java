package home.work.dto.simple;

import lombok.Data;

/**
 * Упрощённый DTO для сущности User.
 */
@Data
public class UserSimple {
    private Long id;
    private String name;
    private String email;
    private String role;
}
