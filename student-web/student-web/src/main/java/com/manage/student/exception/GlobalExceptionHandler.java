package com.manage.student.exception;

import com.manage.student.common.BizException;
import com.manage.student.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器。
 * <p>
 * 没有它的话，运行时异常的响应是 Spring 默认的错误 JSON，与业务接口的 {@link Result}
 * 结构不一致，前端要写两套解析逻辑，而且会把异常堆栈暴露给调用方。
 * 有了它，所有响应（无论成功失败）都是统一的 {code, message, data}。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常：可预期，按异常自带的 code 返回，不打印堆栈。 */
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    /** 参数校验失败：由 @Valid 触发。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(400, msg);
    }

    /** 兜底：未预期的异常统一返回 500，堆栈只进日志不出接口。 */
    @ExceptionHandler(Exception.class)
    public Result<?> handleOtherException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统异常，请稍后重试");
    }
}
