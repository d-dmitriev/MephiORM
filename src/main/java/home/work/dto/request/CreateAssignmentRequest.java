package home.work.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для создания нового задания
 */
@Data
public class CreateAssignmentRequest {
    @NotEmpty
    private String title;
    private String description;
    @NotNull
    @Future
    private LocalDateTime dueDate;
    @NotNull
    private Long lessonId;
}
