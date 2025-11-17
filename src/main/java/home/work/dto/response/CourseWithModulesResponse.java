package home.work.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class CourseWithModulesResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final Integer duration;
    private final LocalDateTime startDate;
    private final LocalDateTime createdAt;
    private final Long teacherId;
    private final String teacherName;
    private final Long categoryId;
    private final String categoryName;
    private List<ModuleResponse> modules = new ArrayList<>();
}
