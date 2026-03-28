package com.shishiji.travel.controller.portal;

import com.shishiji.travel.common.response.ApiResponse;
import com.shishiji.travel.model.dto.PortalLoginRequest;
import com.shishiji.travel.model.dto.PortalProfileUpdateRequest;
import com.shishiji.travel.model.dto.PortalRegisterRequest;
import com.shishiji.travel.model.portal.PortalUser;
import com.shishiji.travel.service.PortalUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户端门户 - 登录/注册
 */
@RestController
@RequestMapping("/portal/auth")
@Tag(name = "门户认证")
@RequiredArgsConstructor
public class PortalAuthController {

    private final PortalUserService portalUserService;
    private final PasswordEncoder passwordEncoder;

    private Long getPortalUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7);
        if (!token.startsWith("portal-")) {
            return null;
        }
        String[] parts = token.split("-");
        if (parts.length < 3) {
            return null;
        }
        try {
            return Long.parseLong(parts[2]);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Map<String, Object> toUserInfo(PortalUser user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        return userInfo;
    }

    @Operation(summary = "用户端登录")
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody PortalLoginRequest request) {
        PortalUser user = portalUserService.getByUsername(request.getUsername());
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            return ApiResponse.fail("用户名或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.fail("用户名或密码错误");
        }
        String token = "portal-" + System.currentTimeMillis() + "-" + user.getId();
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", toUserInfo(user));
        return ApiResponse.success(data);
    }

    @Operation(summary = "用户端注册")
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody PortalRegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ApiResponse.fail("请输入用户名");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ApiResponse.fail("密码至少6位");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ApiResponse.fail("两次密码不一致");
        }
        if (portalUserService.getByUsername(request.getUsername()) != null) {
            return ApiResponse.fail("用户名已存在");
        }
        PortalUser user = new PortalUser();
        user.setUsername(request.getUsername().trim());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        portalUserService.register(user, request.getPassword());
        Map<String, Object> data = new HashMap<>();
        data.put("message", "注册成功，请登录");
        data.put("username", user.getUsername());
        return ApiResponse.success(data);
    }

    @Operation(summary = "获取门户用户资料")
    @GetMapping("/profile")
    public ApiResponse<Map<String, Object>> profile(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long portalUserId = getPortalUserIdFromToken(authorization);
        if (portalUserId == null) {
            return ApiResponse.fail("请先登录");
        }
        PortalUser user = portalUserService.getById(portalUserId);
        if (user == null) {
            return ApiResponse.fail("用户不存在");
        }
        return ApiResponse.success(toUserInfo(user));
    }

    @Operation(summary = "更新门户用户资料")
    @PutMapping("/profile")
    public ApiResponse<Map<String, Object>> updateProfile(
            @RequestBody PortalProfileUpdateRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long portalUserId = getPortalUserIdFromToken(authorization);
        if (portalUserId == null) {
            return ApiResponse.fail("请先登录");
        }

        PortalUser profile = new PortalUser();
        profile.setRealName(request.getRealName());
        profile.setPhone(request.getPhone());
        profile.setEmail(request.getEmail());
        PortalUser updated = portalUserService.updateProfile(portalUserId, profile);
        if (updated == null) {
            return ApiResponse.fail("用户不存在");
        }
        return ApiResponse.success(toUserInfo(updated));
    }
}
