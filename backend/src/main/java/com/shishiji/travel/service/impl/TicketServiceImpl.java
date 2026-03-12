package com.shishiji.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.mapper.TicketMapper;
import com.shishiji.travel.model.ticket.Ticket;
import com.shishiji.travel.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 工单Service实现类
 */
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    
    private final TicketMapper ticketMapper;
    
    @Override
    public PageResult<Ticket> list(int pageNo, int pageSize, String searchKey) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        if (searchKey != null && !searchKey.isEmpty()) {
            wrapper.like(Ticket::getTitle, searchKey);
        }
        wrapper.orderByDesc(Ticket::getCreatedAt);
        
        Page<Ticket> page = ticketMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResult.<Ticket>builder()
                .total(page.getTotal())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .list(page.getRecords())
                .build();
    }
    
    @Override
    public Ticket getById(Long id) {
        return ticketMapper.selectById(id);
    }
    
    @Override
    public Ticket create(Ticket ticket) {
        // 工单编号 NOT NULL UNIQUE：为空时自动生成，避免重复和约束冲突
        if (ticket.getTicketNo() == null || ticket.getTicketNo().trim().isEmpty()) {
            ticket.setTicketNo("TKT" + System.currentTimeMillis());
        }
        ticketMapper.insert(ticket);
        return ticket;
    }
    
    @Override
    public void update(Ticket ticket) {
        ticketMapper.updateById(ticket);
    }
    
    @Override
    public void delete(Long id) {
        ticketMapper.deleteById(id);
    }
    
    @Override
    public PageResult<Ticket> listByPortalUserId(Long portalUserId, int pageNo, int pageSize) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ticket::getPortalUserId, portalUserId);
        wrapper.orderByDesc(Ticket::getCreatedAt);
        Page<Ticket> page = ticketMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResult.<Ticket>builder()
                .total(page.getTotal())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .list(page.getRecords())
                .build();
    }
}
