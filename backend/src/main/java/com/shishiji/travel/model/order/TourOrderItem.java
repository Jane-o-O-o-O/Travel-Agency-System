package com.shishiji.travel.model.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单行程明细
 */
@Data
@TableName("tour_order_item")
public class TourOrderItem {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 第几天
     */
    private Integer dayNo;
    
    /**
     * 序号
     */
    private Integer seq;
    
    /**
     * 时间段
     */
    private String timeSlot;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 类型
     */
    private String type;
    
    /**
     * 详情
     */
    private String detail;
    
    /**
     * 成本/金额
     */
    private BigDecimal cost;
}
