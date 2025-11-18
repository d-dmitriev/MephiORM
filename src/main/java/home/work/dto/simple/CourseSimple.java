package home.work.dto.simple;

import lombok.Data;

/**
 * Упрощённый DTO для сущности Course.
 */
@Data
public class CourseSimple {
    private Long id;
    private String title;
    private String description;
}
