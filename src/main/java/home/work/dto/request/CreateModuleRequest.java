package home.work.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateModuleRequest {
    @NotBlank
    private String title;
    private String description;
    private Integer orderIndex;
    private Long courseId;
}
