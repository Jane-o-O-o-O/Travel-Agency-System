package com.shishiji.travel.model.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.shishiji.travel.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 登录名
     */
    private String username;
    
    /**
     * 密码（加密）
     */
    private String password;
    
    /**
     * 真实名字
     */
    private String realName;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 状态(0=禁用，1=启用)
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
}
