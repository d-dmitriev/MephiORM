package home.work.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCourseRequest {
    @NotEmpty
    private String title;
    private String description;
    @NotNull
    private Long categoryId;       // только ID
    @NotNull
    private Long teacherId;        // только ID
}
