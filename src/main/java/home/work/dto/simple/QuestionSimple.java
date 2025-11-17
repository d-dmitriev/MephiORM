package home.work.dto.simple;

import home.work.entities.QuestionType;
import lombok.Data;

@Data
public class QuestionSimple {
    private Long id;
    private String text;
    private QuestionType type;
    private Integer points;
}
