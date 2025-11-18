package home.work.dto.request;

import lombok.Data;

/**
 * DTO для обновления модуля
 */
@Data
public class UpdateModuleRequest {
    private String title;
    private String description;
    private Integer orderIndex;
}
