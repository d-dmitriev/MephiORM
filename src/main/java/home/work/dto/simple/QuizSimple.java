package home.work.dto.simple;

import lombok.Data;

/**
 * Упрощённый DTO для сущности Quiz.
 */
@Data
public class QuizSimple {
    private Long id;
    private String title;
    private String description;
}
