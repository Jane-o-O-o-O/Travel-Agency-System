package com.shishiji.travel.model.ticket;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.shishiji.travel.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

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
     * 客户ID(管理端关联，门户提交可空)
     */
    private Long customerId;
    
    /**
     * 门户用户ID
     */
    private Long portalUserId;
    
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
     * 意向主题(非遗/亲子/摄影等)
     */
    private String theme;
    
    /**
     * 出行人数
     */
    private Integer peopleCount;
    
    /**
     * 出行天数
     */
    private Integer days;
    
    /**
     * 期望日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedDate;
    
    /**
     * 特殊要求
     */
    private String specialRequirement;
    
    /**
     * 联系方式
     */
    private String contactInfo;
    
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
