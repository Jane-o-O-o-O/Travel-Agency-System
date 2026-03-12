package com.shishiji.travel.model.ticket;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单回复（用户与客服交互）
 */
@Data
@TableName("ticket_reply")
public class TicketReply {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long ticketId;
    /** 来源: PORTAL=用户端, ADMIN=管理端 */
    private String fromType;
    private Long fromUserId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
