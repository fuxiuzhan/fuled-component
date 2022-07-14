package com.fxz.fuled.common.utils;

import lombok.Data;
import lombok.NonNull;

@Data
public class Result<T> {
    private String code;
    private String message;
    private T data;

    private Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result success() {
        return new Result(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), null);
    }

    public static <T> Result success(T data) {
        return new Result(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), data);
    }

    public static Result fail(@NonNull ResultEnum resultEnum) {
        return fail(resultEnum, null);
    }

    public static <T> Result fail(@NonNull ResultEnum resultEnum, T data) {
        return fail(resultEnum.getCode(), resultEnum.getMessage(), data);
    }

    public static <T> Result fail(@NonNull String code, @NonNull String message, T data) {
        return new Result(code, message, data);
    }

    public static <T> Result fail(@NonNull String code, @NonNull String message) {
        return new Result(code, message, null);
    }

    public boolean isSuccess() {
        return ResultEnum.SUCCESS.getCode().equals(this.code);
    }
}
