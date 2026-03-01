package com.shishiji.travel.model.product;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.shishiji.travel.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.List;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 旅游产品
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tour_product")
public class TourProduct extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 产品名称
     */
    private String name;
    
    /**
     * 产品编码
     */
    private String code;
    
    /**
     * 模板ID(来源)
     */
    private Long templateId;
    
    /**
     * 行程天数
     */
    private Integer days;
    
    /**
     * 基础价格
     */
    private BigDecimal basePrice;
    
    /**
     * 成本结构说明
     */
    private String costDescription;
    
    /**
     * 包含内容
     */
    private String included;
    
    /**
     * 不含内容
     */
    private String excluded;
    
    /**
     * 注意事项
     */
    private String notes;
    
    /**
     * 成团规则
     */
    private String groupRule;
    
    /**
     * 产品亮点
     */
    private String highlights;
    
    /**
     * 状态(DRAFT/PUBLISHED/ON_SALE/OFF_SALE)
     */
    private String status;
    
    /**
     * 访问次数
     */
    private Integer viewCount;
    
    /**
     * 产品明细(不存储)
     */
    @TableField(exist = false)
    private List<TourProductItem> items;
}
