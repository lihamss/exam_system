package com.exam.config;

import com.exam.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<?>> handleRuntime(RuntimeException e) {
        return ResponseEntity.ok(Result.fail(e.getMessage()));
    }
}
