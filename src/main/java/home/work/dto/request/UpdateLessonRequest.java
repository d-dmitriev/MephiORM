package home.work.dto.request;

import lombok.Data;

/**
 * DTO для обновления урока
 */
@Data
public class UpdateLessonRequest {
    private String title;
    private String content;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
}
