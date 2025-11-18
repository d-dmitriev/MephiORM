package home.work.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для создания нового вопроса
 */
@Data
public class CreateQuestionRequest {
    @NotEmpty
    private String text;
    @NotEmpty
    private String type;
    @NotNull
    @Min(0)
    private Integer points;
}
