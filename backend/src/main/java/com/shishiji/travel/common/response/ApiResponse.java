package com.shishiji.travel.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应结构
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    
    /**
     * 状态码：0=成功，非0=业务错误
     */
    private Integer code;
    
    /**
     * 提示信息
     */
    private String message;
    
    /**
     * 业务数据
     */
    private T data;
    
    /**
     * 链路追踪ID
     */
    private String traceId;
    
    /**
     * 请求时间戳(ms)
     */
    private Long timestamp;
    
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .code(0)
                .message("ok")
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(0)
                .message("ok")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static <T> ApiResponse<T> fail(Integer code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .code(1)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
