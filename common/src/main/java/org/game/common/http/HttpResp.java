package org.game.common.http;

public class HttpResp<T> {

    public static final HttpResp SUCCESS = new HttpResp(0, "OK");

    private int code;
    private String msg;
    private T data;

    public HttpResp() {
    }

    public HttpResp(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

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

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return code == 0;
    }
}