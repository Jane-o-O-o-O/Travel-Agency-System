package com.shishiji.travel.model.customer;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.shishiji.travel.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer")
public class Customer extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 客户名
     */
    private String name;
    
    /**
     * 手机号（唯一）
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 客户来源(抖音/小红书/老客推荐/线下等)
     */
    private String source;
    
    /**
     * 预算档(LOW/MID/HIGH)
     */
    private String budgetLevel;
    
    /**
     * 备注
     */
    private String note;
    
    /**
     * 最后跟进时间
     */
    private String lastFollowUp;
}
