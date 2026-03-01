package com.shishiji.travel.model.itinerary;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.shishiji.travel.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 行程模板
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("itinerary_template")
public class ItineraryTemplate extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 模板名称
     */
    private String name;
    
    /**
     * 主题(摄影/亲子/徒步/海滨等)
     */
    private String theme;
    
    /**
     * 行程天数
     */
    private Integer days;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 适用人群
     */
    private String applicableCrowd;
    
    /**
     * 状态(DRAFT/PUBLISHED)
     */
    private String status;
    
    /**
     * 使用次数(用于热门排行)
     */
    private Integer useCount;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 模板明细(不存储)
     */
    @TableField(exist = false)
    private List<ItineraryTemplateItem> items;
}
