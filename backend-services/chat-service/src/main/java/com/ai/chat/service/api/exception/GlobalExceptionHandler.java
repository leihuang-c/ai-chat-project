package com.ai.chat.service.api.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ai.chat.service.domain.exception.ChatDomainException;

/**
 * 全局异常处理器
 * 统一处理控制器层抛出的异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理领域异常
     */
    @ExceptionHandler(ChatDomainException.class)
    public ResponseEntity<Map<String, Object>> handleChatDomainException(ChatDomainException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", ErrCode.BAD_REQUEST);
        response.put("message", e.getMessage());
        response.put("data", null);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", ErrCode.INTERNAL_ERROR);
        response.put("message", "系统内部错误: " + e.getMessage());
        response.put("data", null);
        return ResponseEntity.internalServerError().body(response);
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", ErrCode.INTERNAL_ERROR);
        response.put("message", "系统异常: " + e.getMessage());
        response.put("data", null);
        return ResponseEntity.internalServerError().body(response);
    }
}