package home.work.dto.request;

import lombok.Data;

/**
 * DTO для обновления категории
 */
@Data
public class UpdateCategoryRequest {
    private String name;
    private String description;
}
