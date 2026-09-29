package com.manage.student.common;

/**
 * 业务异常。
 * <p>
 * 用于表达"请求本身没问题，但业务规则不允许"的情况（如参数缺失、数据不存在）。
 * 由 {@code GlobalExceptionHandler} 统一捕获并转换成 {@link Result}，
 * 避免业务代码里到处 return Result.error(...)。
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        this(400, message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
