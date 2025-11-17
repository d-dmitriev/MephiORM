package home.work.dto.request;

import lombok.Data;

@Data
public class CreateCourseRequest {
    private String title;
    private String description;
    private Long teacherId;        // только ID
    private Long categoryId;       // только ID
}
