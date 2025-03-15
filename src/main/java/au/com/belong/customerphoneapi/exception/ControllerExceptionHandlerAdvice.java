package au.com.belong.customerphoneapi.exception;

import au.com.belong.customerphoneapi.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import reactor.core.publisher.Mono;

@Slf4j
@Order(1)  // Highest priority among the controller advices
@RestControllerAdvice
public class ControllerExceptionHandlerAdvice {

    // Handle org.springframework.web.method.annotation.HandlerMethodValidationException exceptions
    @ExceptionHandler(HandlerMethodValidationException.class)
    public Mono<ResponseEntity<ErrorDTO>> handle(HandlerMethodValidationException exception) {
        log.warn("Handling method argument invalid exception: {}", exception.getMessage());
        ResponseEntity<ErrorDTO> notFoundResponse = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorDTO.of(exception));
        return Mono.just(notFoundResponse);
    }

    // Handle au.com.belong.customerphoneapi.exception.ResourceNotFoundException exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorDTO>> handle(ResourceNotFoundException exception) {
        log.warn("Handling resource not found exception: {}", exception.getMessage());
        ResponseEntity<ErrorDTO> notFoundResponse = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorDTO.of(exception));
        return Mono.just(notFoundResponse);
    }


    // Handle au.com.belong.customerphoneapi.exception.ResourceStateConflictException exceptions
    @ExceptionHandler(ResourceStateConflictException.class)
    public Mono<ResponseEntity<ErrorDTO>> handle(ResourceStateConflictException exception) {
        log.warn("Handling resource state conflict exception: {}", exception.getMessage());
        ResponseEntity<ErrorDTO> notFoundResponse = ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorDTO.of(exception));
        return Mono.just(notFoundResponse);
    }

    // Handle all uncaught exceptions
    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ErrorDTO>> handle(Throwable exception) {
        log.warn("Handling uncaught exception: {}", exception.getMessage());
        ResponseEntity<ErrorDTO> notFoundResponse = ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorDTO.of(exception));
        return Mono.just(notFoundResponse);
    }}
