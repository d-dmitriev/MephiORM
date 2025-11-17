package home.work.dto.request;

import lombok.Data;

@Data
public class CreateAnswerOptionRequest {
    private String text;
    private Boolean isCorrect;
}
