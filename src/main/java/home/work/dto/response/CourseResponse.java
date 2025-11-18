package home.work.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private LocalDateTime startDate;
    private LocalDateTime createdAt;
    private Long teacherId;
    private String teacherName;
    private Long categoryId;
    private String categoryName;
}
