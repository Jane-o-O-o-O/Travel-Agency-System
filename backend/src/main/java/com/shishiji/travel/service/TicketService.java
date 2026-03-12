package com.shishiji.travel.service;

import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.ticket.Ticket;

/**
 * 工单Service
 */
public interface TicketService {
    
    /**
     * 分页查询
     */
    PageResult<Ticket> list(int pageNo, int pageSize, String searchKey);
    
    /**
     * 根据ID查询
     */
    Ticket getById(Long id);
    
    /**
     * 新增工单
     */
    Ticket create(Ticket ticket);
    
    /**
     * 更新工单
     */
    void update(Ticket ticket);
    
    /**
     * 删除工单
     */
    void delete(Long id);
    
    /**
     * 门户用户：我提交的工单列表
     */
    PageResult<Ticket> listByPortalUserId(Long portalUserId, int pageNo, int pageSize);
}
