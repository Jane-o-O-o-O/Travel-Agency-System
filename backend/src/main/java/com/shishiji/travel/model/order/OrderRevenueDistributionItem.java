package com.shishiji.travel.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单营收状态分布项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRevenueDistributionItem {

    private String label;
    private Long count;
    private BigDecimal amount;
    private String color;
}
