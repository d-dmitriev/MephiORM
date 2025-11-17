package home.work.dto.request;

import lombok.Data;

@Data
public class CreateQuizRequest {
    private String title;
    private String description;
    private Long moduleId;
}
