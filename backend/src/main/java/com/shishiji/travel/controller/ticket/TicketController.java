package com.shishiji.travel.controller.ticket;

import com.shishiji.travel.common.response.ApiResponse;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.ticket.Ticket;
import com.shishiji.travel.model.ticket.TicketReply;
import com.shishiji.travel.service.TicketReplyService;
import com.shishiji.travel.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工单管理API
 */
@RestController
@RequestMapping("/ticket")
@Tag(name = "工单管理")
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    private final TicketReplyService ticketReplyService;
    
    @Operation(summary = "分页查询工单")
    @GetMapping("/list")
    public ApiResponse<PageResult<Ticket>> list(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String searchKey) {
        
        PageResult<Ticket> result = ticketService.list(pageNo, pageSize, searchKey);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取工单详情")
    @GetMapping("/{id}")
    public ApiResponse<Ticket> getById(@PathVariable Long id) {
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) {
            return ApiResponse.fail("工单不存在");
        }
        return ApiResponse.success(ticket);
    }
    
    @Operation(summary = "工单回复列表")
    @GetMapping("/{id}/replies")
    public ApiResponse<List<TicketReply>> getReplies(@PathVariable Long id) {
        List<TicketReply> replies = ticketReplyService.listByTicketId(id);
        return ApiResponse.success(replies);
    }
    
    @Operation(summary = "管理端回复工单")
    @PostMapping("/{id}/reply")
    public ApiResponse<TicketReply> addReply(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> body) {
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) {
            return ApiResponse.fail("工单不存在");
        }
        String content = body != null && body.get("content") != null ? body.get("content").toString() : null;
        if (content == null || content.trim().isEmpty()) {
            return ApiResponse.fail("回复内容不能为空");
        }
        Long adminUserId = body != null && body.get("handlerUserId") != null
                ? Long.valueOf(body.get("handlerUserId").toString()) : 1L;
        TicketReply reply = new TicketReply();
        reply.setTicketId(id);
        reply.setFromType("ADMIN");
        reply.setFromUserId(adminUserId);
        reply.setContent(content.trim());
        ticketReplyService.addReply(reply);
        return ApiResponse.success(reply);
    }
    
    @Operation(summary = "创建工单")
    @PostMapping
    public ApiResponse<Ticket> create(@RequestBody Ticket ticket) {
        Ticket created = ticketService.create(ticket);
        return ApiResponse.success(created);
    }
    
    @Operation(summary = "更新工单")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Ticket ticket) {
        ticket.setId(id);
        ticketService.update(ticket);
        return ApiResponse.success(null);
    }
    
    @Operation(summary = "删除工单")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ApiResponse.success(null);
    }
}
