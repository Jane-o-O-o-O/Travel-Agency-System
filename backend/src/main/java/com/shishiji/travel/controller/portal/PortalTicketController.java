package com.shishiji.travel.controller.portal;

import com.shishiji.travel.common.response.ApiResponse;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.dto.PortalTicketSubmitRequest;
import com.shishiji.travel.model.ticket.Ticket;
import com.shishiji.travel.model.ticket.TicketReply;
import com.shishiji.travel.service.TicketReplyService;
import com.shishiji.travel.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户端门户 - 工单提交与查询
 * 请求头需带 Authorization: Bearer portal-xxx，此处简化从 header 取 userId（实际可用 JWT 解析）
 */
@RestController
@RequestMapping("/portal/ticket")
@Tag(name = "门户工单")
@RequiredArgsConstructor
public class PortalTicketController {

    private final TicketService ticketService;
    private final TicketReplyService ticketReplyService;

    /** 从 token 解析门户用户ID（简化：token 格式 portal-timestamp-userId） */
    private Long getPortalUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) return null;
        String token = authorization.substring(7);
        if (!token.startsWith("portal-")) return null;
        String[] parts = token.split("-");
        if (parts.length >= 3) {
            try {
                return Long.parseLong(parts[2]);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    @Operation(summary = "提交工单")
    @PostMapping
    public ApiResponse<Ticket> submit(
            @RequestBody PortalTicketSubmitRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long portalUserId = getPortalUserIdFromToken(authorization);
        if (portalUserId == null) {
            return ApiResponse.fail("请先登录");
        }
        Ticket ticket = new Ticket();
        ticket.setPortalUserId(portalUserId);
        ticket.setCustomerId(null);
        ticket.setOrderId(null);
        ticket.setTitle(request.getTitle() != null && !request.getTitle().isEmpty()
                ? request.getTitle()
                : "意向：" + (request.getTheme() != null ? request.getTheme() : "定制"));
        ticket.setTheme(request.getTheme());
        ticket.setPeopleCount(request.getPeopleCount());
        ticket.setDays(request.getDays());
        ticket.setExpectedDate(request.getExpectedDate());
        ticket.setSpecialRequirement(request.getSpecialRequirement());
        ticket.setContactInfo(request.getContactInfo());
        ticket.setProblemType("CONSULT");
        ticket.setPriority("MID");
        ticket.setStatus("OPEN");
        ticket.setDescription(request.getSpecialRequirement());
        Ticket created = ticketService.create(ticket);
        return ApiResponse.success(created);
    }

    @Operation(summary = "我的工单列表")
    @GetMapping("/my")
    public ApiResponse<PageResult<Ticket>> myList(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long portalUserId = getPortalUserIdFromToken(authorization);
        if (portalUserId == null) {
            return ApiResponse.fail("请先登录");
        }
        PageResult<Ticket> result = ticketService.listByPortalUserId(portalUserId, pageNo, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "工单详情（含回复）")
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getDetail(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long portalUserId = getPortalUserIdFromToken(authorization);
        if (portalUserId == null) {
            return ApiResponse.fail("请先登录");
        }
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) {
            return ApiResponse.fail("工单不存在");
        }
        if (!portalUserId.equals(ticket.getPortalUserId())) {
            return ApiResponse.fail("无权查看该工单");
        }
        List<TicketReply> replies = ticketReplyService.listByTicketId(id);
        Map<String, Object> data = new HashMap<>();
        data.put("ticket", ticket);
        data.put("replies", replies);
        return ApiResponse.success(data);
    }

    @Operation(summary = "用户回复工单")
    @PostMapping("/{id}/reply")
    public ApiResponse<TicketReply> addReply(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long portalUserId = getPortalUserIdFromToken(authorization);
        if (portalUserId == null) {
            return ApiResponse.fail("请先登录");
        }
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) {
            return ApiResponse.fail("工单不存在");
        }
        if (!portalUserId.equals(ticket.getPortalUserId())) {
            return ApiResponse.fail("无权操作该工单");
        }
        String content = body != null ? body.get("content") : null;
        if (content == null || content.trim().isEmpty()) {
            return ApiResponse.fail("回复内容不能为空");
        }
        TicketReply reply = new TicketReply();
        reply.setTicketId(id);
        reply.setFromType("PORTAL");
        reply.setFromUserId(portalUserId);
        reply.setContent(content.trim());
        ticketReplyService.addReply(reply);
        return ApiResponse.success(reply);
    }
}
