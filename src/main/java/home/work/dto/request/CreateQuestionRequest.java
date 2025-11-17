package home.work.dto.request;

import lombok.Data;

@Data
public class CreateQuestionRequest {
    private String text;
    private String type;
    private Integer points;
}
