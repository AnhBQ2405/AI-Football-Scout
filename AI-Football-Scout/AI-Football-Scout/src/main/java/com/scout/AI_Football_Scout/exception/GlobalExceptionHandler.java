package com.scout.AI_Football_Scout.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice 
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        System.err.println("🚨 HỆ THỐNG BÁO ĐỘNG ĐỎ: " + ex.getMessage());
        String errorMessage = "Hệ thống AI đang tạm thời mất kết nối. Vui lòng thử lại sau ít phút nhé!";
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
    String errorMsg = "Lỗi: Bạn quên truyền tham số '" + ex.getParameterName() + "' rồi!";
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
    }
}