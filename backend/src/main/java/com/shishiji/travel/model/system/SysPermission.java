package com.shishiji.travel.model.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 系统权限
 */
@Data
@TableName("sys_permission")
public class SysPermission {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 权限名称
     */
    private String name;
    
    /**
     * 权限编码(resource:action)
     */
    private String code;
    
    /**
     * 菜单/功能权限标识
     */
    private String resource;
    
    /**
     * 操作标识(view/create/update/delete/confirm/cancel等)
     */
    private String action;
    
    /**
     * 权限类型(MENU/BUTTON/API)
     */
    private String type;
    
    /**
     * 描述
     */
    private String description;
}
