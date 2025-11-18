package home.work.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO ответа для сущности Module с её уроками.
 */
@Data
@RequiredArgsConstructor
public class ModuleResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final Integer orderIndex;
    private List<LessonResponse> lessons = new ArrayList<>();
}
