package home.work.dto.simple;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Упрощённый DTO для сущности CourseReview.
 */
@Data
public class CourseReviewSimple {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
