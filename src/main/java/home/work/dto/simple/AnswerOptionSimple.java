package home.work.dto.simple;

import lombok.Data;

/**
 * Упрощённый DTO для сущности AnswerOption.
 */
@Data
public class AnswerOptionSimple {
    private Long id;
    private String text;
    private Boolean isCorrect;
}
