package com.practice.fcm.common.exception;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
@Component
@RequiredArgsConstructor
public class CustomExceptionAdvice {
    // custom error
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<CustomException> handleCustomException(CustomException e) {
        log.error("Custom Exception occured: {}", e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatus()).body(null);
    }
}
