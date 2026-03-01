package com.shishiji.travel.model.customer;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.shishiji.travel.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户标签
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tag")
public class Tag extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 标签名称
     */
    private String name;
    
    /**
     * 标签分类(INTEREST/BUDGET/PREFERENCE/SEASON)
     */
    private String category;
    
    /**
     * 描述
     */
    private String description;
}
