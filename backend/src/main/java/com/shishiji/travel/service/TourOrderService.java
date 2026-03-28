package com.shishiji.travel.service;

import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.order.OrderRevenueStats;
import com.shishiji.travel.model.order.TourOrder;
import com.shishiji.travel.model.resource.ResourceBooking;
import java.util.List;

/**
 * 订单Service
 */
public interface TourOrderService {
    
    /**
     * 分页查询
     */
    PageResult<TourOrder> list(int pageNo, int pageSize, String status, Long customerId);
    
    /**
     * 新建订单（草稿）
     */
    TourOrder create(TourOrder order);
    
    /**
     * 更新订单基本信息
     */
    void update(TourOrder order);
    
    /**
     * 获取订单详情
     */
    TourOrder getDetail(Long orderId);
    
    /**
     * 确认订单（关键：检测冲突+写入资源占用）
     * @return 确认成功返回true；发现冲突返回false
     */
    boolean confirmOrder(Long orderId, List<ResourceBooking> bookings);
    
    /**
     * 取消订单（释放资源占用）
     */
    void cancelOrder(Long orderId);
    
    /**
     * 检测冲突
     */
    List<ResourceBooking> detectConflicts(Long orderId, List<ResourceBooking> bookings);

    /**
     * 营收统计看板
     */
    OrderRevenueStats getRevenueStats();
}
