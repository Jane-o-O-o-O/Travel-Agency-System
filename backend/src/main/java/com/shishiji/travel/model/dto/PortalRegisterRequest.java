package com.shishiji.travel.model.dto;

import lombok.Data;

@Data
public class PortalRegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String realName;
    private String phone;
    private String email;
}
