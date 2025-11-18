package home.work.dto.simple;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Упрощённый DTO для сущности Assignment.
 */
@Data
public class AssignmentSimple {
    private Long id;
    private String title;
    private LocalDateTime dueDate;
}
