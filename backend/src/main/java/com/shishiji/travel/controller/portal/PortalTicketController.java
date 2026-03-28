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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门户工单提交与查询接口。
 * 请求头需带 Authorization: Bearer portal-xxx，这里简化为从 token 解析 userId。
 */
@RestController
@RequestMapping("/portal/ticket")
@Tag(name = "门户工单")
@RequiredArgsConstructor
public class PortalTicketController {

    private final TicketService ticketService;
    private final TicketReplyService ticketReplyService;

    /**
     * 从 token 解析门户用户 ID，简化 token 格式为 portal-timestamp-userId。
     */
    private Long getPortalUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7);
        if (!token.startsWith("portal-")) {
            return null;
        }
        String[] parts = token.split("-");
        if (parts.length >= 3) {
            try {
                return Long.parseLong(parts[2]);
            } catch (NumberFormatException ignored) {
                return null;
            }
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

        String title = trimToNull(request.getTitle());
        String theme = trimToNull(request.getTheme());
        String specialRequirement = trimToNull(request.getSpecialRequirement());
        String contactInfo = trimToNull(request.getContactInfo());

        if (!hasText(title) && !hasText(theme) && !hasText(specialRequirement)) {
            return ApiResponse.fail("工单内容不能为空");
        }
        if (!hasText(theme)) {
            return ApiResponse.fail("意向主题不能为空");
        }
        if (request.getPeopleCount() == null || request.getPeopleCount() <= 0) {
            return ApiResponse.fail("出行人数必须大于 0");
        }
        if (request.getDays() == null || request.getDays() <= 0) {
            return ApiResponse.fail("出行天数必须大于 0");
        }
        if (!hasText(contactInfo)) {
            return ApiResponse.fail("联系方式不能为空");
        }

        String resolvedTitle = hasText(title) ? title : "意向：" + theme;
        String summary = buildSummary(
                theme,
                request.getPeopleCount(),
                request.getDays(),
                request.getExpectedDate(),
                specialRequirement,
                contactInfo,
                resolvedTitle
        );

        Ticket ticket = new Ticket();
        ticket.setPortalUserId(portalUserId);
        ticket.setCustomerId(null);
        ticket.setOrderId(null);
        ticket.setTitle(resolvedTitle);
        ticket.setTheme(theme);
        ticket.setPeopleCount(request.getPeopleCount());
        ticket.setDays(request.getDays());
        ticket.setExpectedDate(request.getExpectedDate());
        ticket.setSpecialRequirement(specialRequirement);
        ticket.setContactInfo(contactInfo);
        ticket.setProblemType("CONSULT");
        ticket.setPriority("MID");
        ticket.setStatus("OPEN");
        ticket.setDescription(summary);
        Ticket created = ticketService.create(ticket);

        if (hasText(summary)) {
            TicketReply reply = new TicketReply();
            reply.setTicketId(created.getId());
            reply.setFromType("PORTAL");
            reply.setFromUserId(portalUserId);
            reply.setContent(summary);
            ticketReplyService.addReply(reply);
        }
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

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String buildSummary(
            String theme,
            Integer peopleCount,
            Integer days,
            LocalDate expectedDate,
            String specialRequirement,
            String contactInfo,
            String fallbackTitle) {
        List<String> parts = new ArrayList<>();
        if (hasText(theme)) {
            parts.add("意向主题：" + theme);
        }
        if (peopleCount != null) {
            parts.add("出行人数：" + peopleCount);
        }
        if (days != null) {
            parts.add("出行天数：" + days);
        }
        if (expectedDate != null) {
            parts.add("期望日期：" + expectedDate);
        }
        if (hasText(contactInfo)) {
            parts.add("联系方式：" + contactInfo);
        }
        if (hasText(specialRequirement)) {
            parts.add("特殊要求：" + specialRequirement);
        }
        if (parts.isEmpty()) {
            return fallbackTitle;
        }
        return String.join("；", parts);
    }
}
