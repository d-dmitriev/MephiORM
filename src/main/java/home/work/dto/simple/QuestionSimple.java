package home.work.dto.simple;

import lombok.Data;

@Data
public class QuestionSimple {
    private Long id;
    private String text;
    private String type;
    private Integer points;
}
