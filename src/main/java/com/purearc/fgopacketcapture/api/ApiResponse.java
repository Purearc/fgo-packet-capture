package com.purearc.fgopacketcapture.api;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public ApiResponse() {}

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(true, "ok", data); }
    public static <T> ApiResponse<T> fail(String message) { return new ApiResponse<>(false, message, null); }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
