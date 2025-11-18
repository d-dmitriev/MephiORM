package home.work.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для создания нового модуля
 */
@Data
public class CreateModuleRequest {
    @NotBlank
    private String title;
    private String description;
    private Integer orderIndex;
    @NotNull
    private Long courseId;
}
