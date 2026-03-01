package com.shishiji.travel.model.itinerary;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 行程模板明细
 */
@Data
@TableName("itinerary_template_item")
public class ItineraryTemplateItem {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 模板ID
     */
    private Long templateId;
    
    /**
     * 第几天(从1开始)
     */
    private Integer dayNo;
    
    /**
     * 序号(同一天内的顺序)
     */
    private Integer seq;
    
    /**
     * 时间段(MORNING/AFTERNOON/EVENING)
     */
    private String timeSlot;
    
    /**
     * 标题/名称
     */
    private String title;
    
    /**
     * 类型(SPOT/MEAL/TRAFFIC/HOTEL/FREE)
     */
    private String type;
    
    /**
     * 详情描述
     */
    private String detail;
    
    /**
     * 预估成本
     */
    private BigDecimal estimatedCost;
}
