package com.shishiji.travel.model.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.shishiji.travel.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 旅游订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tour_order")
public class TourOrder extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 订单号(唯一)
     */
    private String orderNo;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 客户名
     */
    @TableField(exist = false)
    private String customerName;
    
    /**
     * 产品ID(可为空，纯定制)
     */
    private Long productId;
    
    /**
     * 出发日期
     */
    private LocalDate startDate;
    
    /**
     * 返回日期
     */
    private LocalDate endDate;
    
    /**
     * 人数
     */
    private Integer peopleCount;
    
    /**
     * 订单金额
     */
    private BigDecimal amount;
    
    /**
     * 支付状态(UNPAID/PARTIAL/PAID)
     */
    private String payStatus;
    
    /**
     * 订单状态(DRAFT/PENDING_CONFIRM/CONFIRMED/COMPLETED/CANCELLED)
     */
    private String status;
    
    /**
     * 负责人(运营/计调)
     */
    private Long ownerUserId;
    
    /**
     * 负责人名字(不存储)
     */
    @TableField(exist = false)
    private String ownerUserName;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 订单行程明细(不存储)
     */
    @TableField(exist = false)
    private List<TourOrderItem> items;
}
