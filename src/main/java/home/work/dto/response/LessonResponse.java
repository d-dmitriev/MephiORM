package home.work.dto.response;

import lombok.Data;

@Data
public class LessonResponse {
    private Long id;
    private String title;
    private String content;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
}
