package com.practice.fcm.common.exception;

import lombok.*;
import org.springframework.http.*;

@Getter
@RequiredArgsConstructor
public class ErrorType {

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
