package home.work.dto.simple;

import lombok.Data;

/**
 * Упрощённый DTO для сущности Question.
 */
@Data
public class QuestionSimple {
    private Long id;
    private String text;
    private String type;
    private Integer points;
}
