package com.shishiji.travel.model.ticket;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.shishiji.travel.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工单系统
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ticket")
public class Ticket extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 工单编号
     */
    private String ticketNo;
    
    /**
     * 关联订单ID
     */
    private Long orderId;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 问题类型(COMPLAINT/MODIFY/REFUND/CONSULT)
     */
    private String problemType;
    
    /**
     * 优先级(LOW/MID/HIGH/URGENT)
     */
    private String priority;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 负责人ID
     */
    private Long handlerUserId;
    
    /**
     * 工单状态(OPEN/IN_PROGRESS/RESOLVED/CLOSED)
     */
    private String status;
    
    /**
     * SLA首次响应时间(分钟,可选)
     */
    private Integer firstResponseTime;
    
    /**
     * SLA解决时长(分钟,可选)
     */
    private Integer resolutionTime;
}
