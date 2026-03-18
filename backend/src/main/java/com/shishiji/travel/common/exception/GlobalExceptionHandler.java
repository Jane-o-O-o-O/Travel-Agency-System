package com.shishiji.travel.common.exception;

import com.shishiji.travel.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResponse<?> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("数据完整性违反: {}", e.getMessage(), e);
        String message = "数据操作失败";
        if (e.getMessage() != null) {
            if (e.getMessage().contains("Duplicate")) {
                message = "数据已存在（可能是手机号重复）";
            } else if (e.getMessage().contains("Cannot add")) {
                message = "违反外键约束";
            } else if (e.getMessage().contains("NOT NULL")) {
                message = "必填字段不能为空";
            }
        }
        return ApiResponse.fail(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数验证失败";
        return ApiResponse.fail(message);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        log.error("系统异常", e);
        return ApiResponse.fail("系统错误: " + e.getMessage());
    }
}

