package com.shishiji.travel.model.dto;

import lombok.Data;

/**
 * 门户用户资料更新请求
 */
@Data
public class PortalProfileUpdateRequest {

    private String realName;
    private String phone;
    private String email;
}
