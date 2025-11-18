package home.work.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateTagRequest {
    @NotEmpty
    private String name;
}
