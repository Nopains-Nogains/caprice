package com.cyh.caprice.capriceservice.util;

/**
 * @Author: cuiyuhao
 * @Description: 全局捕获异常增强类
 * @Data: Created in 10:31 2021/10/13
 **/

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler(value = Exception.class)
    Message handlerException(Exception e) {
        Message message = new Message();
        message.setMessage(e.getMessage());
        return message;
    }
}
