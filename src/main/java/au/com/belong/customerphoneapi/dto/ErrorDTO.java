package au.com.belong.customerphoneapi.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ErrorDTO {
    private final List<String> errors;
    private final String message;
    private final String requestId;

    public static ErrorDTO of(Throwable throwable) {
        return ErrorDTO.builder()
                .message(throwable.getMessage())
                .requestId(UUID.randomUUID().toString())
                .build();
    }

    public static ErrorDTO of(HandlerMethodValidationException exception) {
        List<String> badInputErrors = exception.getAllErrors()
                .stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList();
        return ErrorDTO.builder()
                .errors(badInputErrors)
                .message("Validation errors found.")
                .requestId(UUID.randomUUID().toString())
                .build();
    }
}
