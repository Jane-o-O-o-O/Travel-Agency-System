package com.shishiji.travel.controller.system;

import com.shishiji.travel.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证API
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理")
@RequiredArgsConstructor
public class AuthController {

    @Operation(summary = "登录")
    @PostMapping("/login")
    public ApiResponse<Object> login(
            @RequestParam String username,
            @RequestParam String password) {
        
        // 演示登录：admin/admin123
        if ("admin".equals(username) && "admin123".equals(password)) {
            return ApiResponse.success(new Object() {
                public String token = "demo-token-" + System.currentTimeMillis();
                public Object user = new Object() {
                    public Long id = 1L;
                    public String username = "admin";
                    public String realName = "系统管理员";
                    public String[] roles = {"ADMIN"};
                };
            });
        }
        
        return ApiResponse.fail("用户名或密码错误");
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public ApiResponse<Object> getCurrentUser() {
        return ApiResponse.success(new Object() {
            public Long id = 1L;
            public String username = "admin";
            public String realName = "系统管理员";
        });
    }
}
