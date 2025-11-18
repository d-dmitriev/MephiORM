package home.work.dto.simple;

import lombok.Data;

/**
 * Упрощённый DTO для сущности Module.
 */
@Data
public class ModuleSimple {
    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
}
