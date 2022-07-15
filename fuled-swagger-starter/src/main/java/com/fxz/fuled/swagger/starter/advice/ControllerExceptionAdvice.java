package com.fxz.fuled.swagger.starter.advice;

import com.fxz.fuled.common.utils.Result;
import com.fxz.fuled.common.utils.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author fxz
 * <p>
 * 简单的异常全局异常捕获切面
 * <p>
 * 不能直接将堆栈抛到前端
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionAdvice {

    @ExceptionHandler
    @ResponseBody
    public Result exceptionHandler(Throwable e) {
        log.error("UncaughtException  e->{}", e);
        String message = e.getMessage();
        return Result.fail(ResultEnum.UNCAUGHT_EXCEPTION);
    }
}
