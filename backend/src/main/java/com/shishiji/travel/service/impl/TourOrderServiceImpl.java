package com.shishiji.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.mapper.TourOrderMapper;
import com.shishiji.travel.model.order.TourOrder;
import com.shishiji.travel.model.resource.ResourceBooking;
import com.shishiji.travel.service.ResourceBookingService;
import com.shishiji.travel.service.TourOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class TourOrderServiceImpl implements TourOrderService {
    
    private final TourOrderMapper tourOrderMapper;
    private final ResourceBookingService resourceBookingService;
    
    @Override
    public PageResult<TourOrder> list(int pageNo, int pageSize, String status, Long customerId) {
        Page<TourOrder> page = new Page<>(pageNo, pageSize);
        
        QueryWrapper<TourOrder> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (customerId != null) {
            wrapper.eq("customer_id", customerId);
        }
        wrapper.orderByDesc("created_at");
        
        Page<TourOrder> result = tourOrderMapper.selectPage(page, wrapper);
        
        PageResult<TourOrder> pageResult = PageResult.<TourOrder>builder()
                .total(result.getTotal())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .list(result.getRecords())
                .build();
        
        pageResult.calcPages();
        return pageResult;
    }
    
    @Override
    @Transactional
    public TourOrder create(TourOrder order) {
        order.setStatus("DRAFT");
        order.setOrderNo(generateOrderNo());
        order.setPayStatus("UNPAID");
        tourOrderMapper.insert(order);
        return order;
    }
    
    @Override
    @Transactional
    public void update(TourOrder order) {
        tourOrderMapper.updateById(order);
    }
    
    @Override
    public TourOrder getDetail(Long orderId) {
        return tourOrderMapper.selectById(orderId);
    }
    
    @Override
    @Transactional
    public boolean confirmOrder(Long orderId, List<ResourceBooking> bookings) {
        // 1. 检测冲突
        List<ResourceBooking> conflicts = detectConflicts(orderId, bookings);
        if (!conflicts.isEmpty()) {
            return false;
        }
        
        // 2. 创建资源占用记录（事务内强一致）
        for (ResourceBooking booking : bookings) {
            booking.setOrderId(orderId);
            resourceBookingService.create(booking);
        }
        
        // 3. 更新订单状态为已确认
        TourOrder order = new TourOrder();
        order.setId(orderId);
        order.setStatus("CONFIRMED");
        tourOrderMapper.updateById(order);
        
        return true;
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        // 1. 查询该订单的所有资源占用
        List<ResourceBooking> bookings = resourceBookingService.getByOrderId(orderId);
        
        // 2. 释放所有资源占用
        for (ResourceBooking booking : bookings) {
            resourceBookingService.cancel(booking.getId());
        }
        
        // 3. 更新订单状态为已取消
        TourOrder order = new TourOrder();
        order.setId(orderId);
        order.setStatus("CANCELLED");
        tourOrderMapper.updateById(order);
    }
    
    @Override
    public List<ResourceBooking> detectConflicts(Long orderId, List<ResourceBooking> bookings) {
        List<ResourceBooking> conflicts = new ArrayList<>();
        
        for (ResourceBooking booking : bookings) {
            // 检测该资源在指定时间段内是否有冲突
            List<ResourceBooking> conflictList = resourceBookingService.checkConflicts(
                booking.getResourceId(),
                booking.getStartTime(),
                booking.getEndTime()
            );
            conflicts.addAll(conflictList);
        }
        
        return conflicts;
    }
    
    /**
     * 生成订单号：20240227001
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
