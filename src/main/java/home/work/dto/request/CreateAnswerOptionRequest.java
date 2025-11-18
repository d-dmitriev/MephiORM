package home.work.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAnswerOptionRequest {
    @NotEmpty
    private String text;
    @NotNull
    private Boolean isCorrect;
}
