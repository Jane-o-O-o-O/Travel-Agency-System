package com.shishiji.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shishiji.travel.model.resource.ResourceBooking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源占用 Mapper（冲突检测核心）
 */
@Mapper
public interface ResourceBookingMapper extends BaseMapper<ResourceBooking> {
    
    /**
     * 查询资源在时间段内的冲突占用记录
     * 冲突判定: startA < endB AND endA > startB
     */
    @Select({
        "SELECT * FROM resource_booking",
        "WHERE resource_id = #{resourceId}",
        "  AND status = 'ACTIVE'",
        "  AND start_time < #{endTime}",
        "  AND end_time > #{startTime}"
    })
    List<ResourceBooking> findConflictBookings(
        Long resourceId,
        LocalDateTime startTime,
        LocalDateTime endTime
    );
}
