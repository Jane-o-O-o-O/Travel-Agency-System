package com.shishiji.travel.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * 用户注册请求
 */
@Data
public class RegisterRequest {
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    @NotBlank(message = "姓名不能为空")
    private String realName;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String phone;
}
