package com.gym.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理器
 * 捕获并处理应用中的各类异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     * @param e 业务异常
     * @param model 模型对象，用于传递错误信息
     * @return 错误页面视图名
     */
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model) {
        // 将异常消息传入model
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    /**
     * 处理通用异常
     * @param e 通用异常
     * @param model 模型对象，用于传递错误信息
     * @return 错误页面视图名
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        // 记录异常日志
        logger.error("系统异常: {}", e.getMessage(), e);
        // 设置通用错误消息
        model.addAttribute("message", "系统发生异常，请稍后重试");
        return "error";
    }
}