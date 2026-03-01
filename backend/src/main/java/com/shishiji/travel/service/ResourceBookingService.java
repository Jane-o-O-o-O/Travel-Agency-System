package com.shishiji.travel.service;

import com.shishiji.travel.model.resource.ResourceBooking;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源占用Service（核心服务，处理冲突检测）
 */
public interface ResourceBookingService {
    
    /**
     * 检测资源在时间段内是否有冲突
     * @return 冲突的占用记录列表
     */
    List<ResourceBooking> checkConflicts(Long resourceId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 创建资源占用
     */
    void create(ResourceBooking booking);
    
    /**
     * 取消资源占用
     */
    void cancel(Long bookingId);
    
    /**
     * 查询订单的所有资源占用
     */
    List<ResourceBooking> getByOrderId(Long orderId);
}
