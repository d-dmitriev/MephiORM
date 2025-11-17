package home.work.dto.simple;

import lombok.Data;

@Data
public class LessonSimple {
    private Long id;
    private String title;
    private String content;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
}
