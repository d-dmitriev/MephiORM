package home.work.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ErrorResponse {
    private final int status;
    private final String message;
    private long timestamp = System.currentTimeMillis();
}
