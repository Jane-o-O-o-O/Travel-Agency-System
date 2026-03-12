package com.shishiji.travel.service;

import com.shishiji.travel.model.ticket.TicketReply;

import java.util.List;

/**
 * 工单回复Service
 */
public interface TicketReplyService {
    
    List<TicketReply> listByTicketId(Long ticketId);
    
    TicketReply addReply(TicketReply reply);
}
