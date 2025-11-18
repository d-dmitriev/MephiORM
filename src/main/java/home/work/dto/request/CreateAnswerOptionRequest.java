package home.work.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для создания нового варианта ответа
 */
@Data
public class CreateAnswerOptionRequest {
    @NotEmpty
    private String text;
    @NotNull
    private Boolean isCorrect;
}
