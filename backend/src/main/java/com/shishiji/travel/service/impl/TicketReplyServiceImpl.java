package com.shishiji.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shishiji.travel.mapper.TicketReplyMapper;
import com.shishiji.travel.model.ticket.TicketReply;
import com.shishiji.travel.service.TicketReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketReplyServiceImpl implements TicketReplyService {

    private final TicketReplyMapper ticketReplyMapper;

    @Override
    public List<TicketReply> listByTicketId(Long ticketId) {
        return ticketReplyMapper.selectList(
                new LambdaQueryWrapper<TicketReply>()
                        .eq(TicketReply::getTicketId, ticketId)
                        .orderByAsc(TicketReply::getCreatedAt));
    }

    @Override
    public TicketReply addReply(TicketReply reply) {
        reply.setCreatedAt(LocalDateTime.now());
        ticketReplyMapper.insert(reply);
        return reply;
    }
}
