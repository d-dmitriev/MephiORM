package home.work.dto.request;

import lombok.Data;

@Data
public class UpdateLessonRequest {
    private String title;
    private String content;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
}
