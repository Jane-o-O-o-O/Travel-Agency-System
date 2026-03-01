package com.shishiji.travel.model.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.shishiji.travel.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 角色名
     */
    private String name;
    
    /**
     * 角色编码(ADMIN/OPERATOR/PLANNER/CS/FINANCE)
     */
    private String code;
    
    /**
     * 状态(0=禁用，1=启用)
     */
    private Integer status;
    
    /**
     * 描述
     */
    private String description;
}
