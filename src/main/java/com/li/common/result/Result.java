package com.li.common.result;


import com.li.common.constant.StatusCodeConstant;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;

    public Result() {
    }
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public static<T> Result<T> success(T data) {
        return new Result<T>(StatusCodeConstant.SUCCESS_CODE, "success", data);
    }
    public static<T> Result<T> success() {
        return new Result<T>(StatusCodeConstant.SUCCESS_CODE, "success");
    }
    public static<T> Result<T> fail(String message) {
        return new Result<T>(StatusCodeConstant.FAIL_CODE, message);
    }
}
