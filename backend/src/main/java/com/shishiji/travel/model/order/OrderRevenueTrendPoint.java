package com.shishiji.travel.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 营收趋势点
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRevenueTrendPoint {

    private String label;
    private BigDecimal plannedIncome;
    private BigDecimal actualIncome;
}
