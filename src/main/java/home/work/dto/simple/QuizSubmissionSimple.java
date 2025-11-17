package home.work.dto.simple;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizSubmissionSimple {
    private Long id;
    private Integer score;
    private LocalDateTime takenAt;
    private Integer attemptNumber;
}
