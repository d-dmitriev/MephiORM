package home.work.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для создания нового урока
 */
@Data
public class CreateLessonRequest {
    @NotEmpty
    private String title;
    private String content;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
    @NotNull
    private Long moduleId;
}
