package home.work.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAssignmentRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
}
