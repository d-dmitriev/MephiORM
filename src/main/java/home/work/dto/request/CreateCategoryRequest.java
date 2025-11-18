package home.work.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    @NotEmpty
    private String name;
    private String description;
}
