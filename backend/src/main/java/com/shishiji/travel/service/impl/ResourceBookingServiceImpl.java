package com.shishiji.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shishiji.travel.mapper.ResourceBookingMapper;
import com.shishiji.travel.model.resource.ResourceBooking;
import com.shishiji.travel.service.ResourceBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源占用服务实现
 */
@Service
@RequiredArgsConstructor
public class ResourceBookingServiceImpl implements ResourceBookingService {
    
    private final ResourceBookingMapper resourceBookingMapper;
    
    @Override
    public List<ResourceBooking> checkConflicts(Long resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        return resourceBookingMapper.findConflictBookings(resourceId, startTime, endTime);
    }
    
    @Override
    @Transactional
    public void create(ResourceBooking booking) {
        booking.setStatus("ACTIVE");
        booking.setCreatedAt(LocalDateTime.now());
        resourceBookingMapper.insert(booking);
    }
    
    @Override
    @Transactional
    public void cancel(Long bookingId) {
        ResourceBooking booking = new ResourceBooking();
        booking.setId(bookingId);
        booking.setStatus("CANCELLED");
        resourceBookingMapper.updateById(booking);
    }
    
    @Override
    public List<ResourceBooking> getByOrderId(Long orderId) {
        return resourceBookingMapper.selectList(
            new QueryWrapper<ResourceBooking>()
                .eq("order_id", orderId)
                .eq("status", "ACTIVE")
        );
    }
}
