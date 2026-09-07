package com.manage.student.common;

public class Result<T> {
    private int code;
    private String message;
    private T data;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "成功", data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    // 新增：带状态码的错误返回
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    // Getter / Setter (手动或 Lombok)
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}