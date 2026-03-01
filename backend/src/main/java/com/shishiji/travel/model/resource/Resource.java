package com.shishiji.travel.model.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.shishiji.travel.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源(导游/车辆/酒店等)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("resource")
public class Resource extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 资源名称
     */
    private String name;
    
    /**
     * 资源编码
     */
    private String code;
    
    /**
     * 资源类型(GUIDE/VEHICLE/HOTEL/OTHER)
     */
    private String type;
    
    /**
     * 详细描述
     */
    private String description;
    
    /**
     * 状态(ACTIVE/INACTIVE)
     */
    private String status;
    
    /**
     * 联系信息(电话/邮箱等)
     */
    private String contact;
    
    /**
     * 备注
     */
    private String remark;
}
