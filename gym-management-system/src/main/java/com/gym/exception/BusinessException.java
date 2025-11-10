package com.gym.exception;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}