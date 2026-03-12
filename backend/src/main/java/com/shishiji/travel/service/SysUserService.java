package com.shishiji.travel.service;

import com.shishiji.travel.model.system.SysUser;

/**
 * 系统用户Service
 */
public interface SysUserService {
    
    /**
     * 根据ID查询
     */
    SysUser getById(Long id);
    
    /**
     * 根据用户名查询
     */
    SysUser getByUsername(String username);
    
    /**
     * 新增用户
     */
    SysUser create(SysUser user);
    
    /**
     * 更新用户
     */
    void update(SysUser user);
    
    /**
     * 删除用户
     */
    void delete(Long id);
}
