package com.shishiji.travel.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户端提交工单请求
 */
@Data
public class PortalTicketSubmitRequest {
    private String theme;           // 意向主题：非遗/亲子/摄影等
    private Integer peopleCount;   // 出行人数
    private Integer days;          // 出行天数
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedDate; // 期望日期
    private String specialRequirement; // 特殊要求
    private String contactInfo;    // 联系方式
    private String title;          // 工单标题(可选，可自动生成)
}
