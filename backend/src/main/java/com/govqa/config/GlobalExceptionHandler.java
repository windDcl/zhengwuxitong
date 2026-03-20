package com.govqa.config;

import com.govqa.common.ApiResponse;
import com.govqa.common.BizException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBiz(BizException e) {
        return ApiResponse.fail(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class,
            HttpMessageNotReadableException.class})
    public ApiResponse<Void> handleValidation(Exception e) {
        return ApiResponse.fail("请求参数错误: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleUnknown(Exception e) {
        return ApiResponse.fail("系统异常: " + e.getMessage());
    }
}
