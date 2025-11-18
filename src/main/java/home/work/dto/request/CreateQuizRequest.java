package home.work.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для создания новой викторины
 */
@Data
public class CreateQuizRequest {
    @NotEmpty
    private String title;
    private String description;
    @NotNull
    private Long moduleId;
}
