package com.shishiji.travel.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单营收统计
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRevenueStats {

    private Long totalOrders;
    private Long activeOrders;
    private Long paidOrders;
    private Long pendingReceiptOrders;
    private BigDecimal totalPlannedIncome;
    private BigDecimal totalActualIncome;
    private String weeklyTrendDirection;
    private BigDecimal weeklyChangeRate;
    private String monthlyTrendDirection;
    private BigDecimal monthlyChangeRate;
    private List<OrderRevenueTrendPoint> weeklyTrend;
    private List<OrderRevenueTrendPoint> monthlyTrend;
    private List<OrderRevenueDistributionItem> statusDistribution;
    private OrderRevenuePlanActual planActual;
    private List<OrderRevenueRiskItem> collectionRisks;
}
