package com.shishiji.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shishiji.travel.model.order.TourOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 */
@Mapper
public interface TourOrderMapper extends BaseMapper<TourOrder> {
}
