package com.shishiji.travel.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 计划与实收对比
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRevenuePlanActual {

    private BigDecimal plannedIncome;
    private BigDecimal actualIncome;
    private BigDecimal gapIncome;
    private Integer completionRate;
}
