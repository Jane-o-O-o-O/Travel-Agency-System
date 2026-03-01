package com.shishiji.travel.model.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 资源占用（关键：订单确认时写入，用于冲突检测）
 */
@Data
@TableName("resource_booking")
public class ResourceBooking {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 资源ID
     */
    private Long resourceId;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    /**
     * 状态(ACTIVE/CANCELLED)
     */
    private String status;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
