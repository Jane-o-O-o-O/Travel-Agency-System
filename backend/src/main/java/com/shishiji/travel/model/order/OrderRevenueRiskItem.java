package com.shishiji.travel.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 未收款风险订单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRevenueRiskItem {

    private Long id;
    private String orderNo;
    private Long customerId;
    private String status;
    private String payStatus;
    private BigDecimal amount;
    private Integer progress;
    private Long overdueDays;
}
