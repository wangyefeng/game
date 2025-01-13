package org.game.common.http;

public record HttpResp<T>(int code, String msg, T data) {

    public static final HttpResp SUCCESS = new HttpResp(0, "OK");

    public HttpResp(int code, String msg) {
        this(code, msg, null);
    }

    public static <T> HttpResp<T> success(T data) {
        HttpResp result = new HttpResp(0, "OK", data);
        return result;
    }

    public static <T> HttpResp<T> fail(int code, String msg) {
        return new HttpResp(code, msg);
    }

    public boolean isSuccess() {
        return code == 0;
    }
}