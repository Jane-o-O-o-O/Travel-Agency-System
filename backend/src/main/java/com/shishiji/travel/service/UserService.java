package com.shishiji.travel.service;

import com.shishiji.travel.model.system.SysUser;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    SysUser register(String username, String password, String realName, String email, String phone);
    
    /**
     * 用户登录（验证用户名密码）
     */
    SysUser login(String username, String password);
    
    /**
     * 获取用户信息
     */
    SysUser getUserByUsername(String username);
    
    /**
     * 用户名是否存在
     */
    boolean existsByUsername(String username);
}
