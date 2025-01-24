package org.game.common.http;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record HttpResp<T>(int code, String msg, T data) {

    public static final int SUCCESS_CODE = 0;

    public static final HttpResp<Void> SUCCESS = new HttpResp<>(SUCCESS_CODE, "成功");

    public HttpResp(int code, String msg) {
        this(code, msg, null);
    }

    public static <T> HttpResp<T> success(T data) {
        return new HttpResp<>(SUCCESS_CODE, "成功", data);
    }

    public static <T> HttpResp<T> fail(int code, String msg) {
        return new HttpResp<>(code, msg, null);
    }

    public static <T> HttpResp<T> fail(int code) {
        return fail(code, "失败");
    }

    @JsonIgnore
    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }
}