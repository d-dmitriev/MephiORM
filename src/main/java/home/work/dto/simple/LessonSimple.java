package home.work.dto.simple;

import lombok.Data;

/**
 * Упрощённый DTO для сущности Lesson.
 */
@Data
public class LessonSimple {
    private Long id;
    private String title;
    private String content;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
}
