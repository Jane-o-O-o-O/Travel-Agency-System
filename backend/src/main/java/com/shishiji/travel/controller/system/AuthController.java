package com.shishiji.travel.controller.system;

import com.shishiji.travel.common.response.ApiResponse;
import com.shishiji.travel.model.dto.LoginRequest;
import com.shishiji.travel.model.dto.RegisterRequest;
import com.shishiji.travel.model.system.SysUser;
import com.shishiji.travel.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证API
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理")
@RequiredArgsConstructor
public class AuthController {
    
    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public ApiResponse<Object> login(@RequestBody LoginRequest request) {
        
        // 优先从数据库查询用户
        SysUser user = sysUserService.getByUsername(request.getUsername());
        if (user != null && user.getStatus() == 1 && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.success(new Object() {
                public String token = "demo-token-" + System.currentTimeMillis();
                public Object userInfo = new Object() {
                    public Long id = user.getId();
                    public String username = user.getUsername();
                    public String realName = user.getRealName();
                    public String[] roles = {"USER"};
                };
            });
        }
        
        // 演示登录：admin/admin123
        if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
            return ApiResponse.success(new Object() {
                public String token = "demo-token-" + System.currentTimeMillis();
                public Object userInfo = new Object() {
                    public Long id = 1L;
                    public String username = "admin";
                    public String realName = "系统管理员";
                    public String[] roles = {"ADMIN"};
                };
            });
        }
        
        return ApiResponse.fail("用户名或密码错误");
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public ApiResponse<Object> register(@RequestBody RegisterRequest request) {
        
        // 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ApiResponse.fail("两次输入的密码不相同");
        }
        
        // 检查用户名是否已存在
        if (sysUserService.getByUsername(request.getUsername()) != null) {
            return ApiResponse.fail("用户名已存在");
        }
        
        // 创建新用户
        SysUser newUser = new SysUser();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(request.getPassword()); // 在service中加密
        newUser.setRealName(request.getRealName());
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhone());
        newUser.setStatus(1); // 启用
        
        try {
            sysUserService.create(newUser);
            return ApiResponse.success(new Object() {
                public String message = "注册成功！请使用新账号登录";
                public String username = request.getUsername();
            });
        } catch (Exception e) {
            return ApiResponse.fail("注册失败，请稍后重试");
        }
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
