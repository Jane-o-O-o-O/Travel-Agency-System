package com.shishiji.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shishiji.travel.model.ticket.Ticket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
}
