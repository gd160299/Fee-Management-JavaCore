package org.pj.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;
    private final LocalDateTime timestamp;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }
}
