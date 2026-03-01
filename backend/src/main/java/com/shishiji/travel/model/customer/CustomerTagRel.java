package com.shishiji.travel.model.customer;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 客户标签关联
 */
@Data
@TableName("customer_tag_rel")
public class CustomerTagRel {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 标签ID
     */
    private Long tagId;
}
