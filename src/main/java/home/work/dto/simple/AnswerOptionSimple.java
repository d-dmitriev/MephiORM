package home.work.dto.simple;

import lombok.Data;

@Data
public class AnswerOptionSimple {
    private Long id;
    private String text;
    private Boolean isCorrect;
}
