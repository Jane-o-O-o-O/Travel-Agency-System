package com.shishiji.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.mapper.TourOrderMapper;
import com.shishiji.travel.mapper.TourProductMapper;
import com.shishiji.travel.model.order.OrderRevenueDistributionItem;
import com.shishiji.travel.model.order.OrderRevenuePlanActual;
import com.shishiji.travel.model.order.OrderRevenueRiskItem;
import com.shishiji.travel.model.order.OrderRevenueStats;
import com.shishiji.travel.model.order.OrderRevenueTrendPoint;
import com.shishiji.travel.model.order.TourOrder;
import com.shishiji.travel.model.product.TourProduct;
import com.shishiji.travel.model.resource.ResourceBooking;
import com.shishiji.travel.service.ResourceBookingService;
import com.shishiji.travel.service.TourOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class TourOrderServiceImpl implements TourOrderService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final TourOrderMapper tourOrderMapper;
    private final TourProductMapper tourProductMapper;
    private final ResourceBookingService resourceBookingService;
    
    @Override
    public PageResult<TourOrder> list(int pageNo, int pageSize, String status, Long customerId) {
        Page<TourOrder> page = new Page<>(pageNo, pageSize);
        
        QueryWrapper<TourOrder> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (customerId != null) {
            wrapper.eq("customer_id", customerId);
        }
        wrapper.orderByDesc("created_at");
        
        Page<TourOrder> result = tourOrderMapper.selectPage(page, wrapper);
        
        List<TourOrder> records = result.getRecords();
        records.forEach(this::populateAmountFromProductIfMissing);

        PageResult<TourOrder> pageResult = PageResult.<TourOrder>builder()
                .total(result.getTotal())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .list(records)
                .build();
        
        pageResult.calcPages();
        return pageResult;
    }
    
    @Override
    @Transactional
    public TourOrder create(TourOrder order) {
        populateAmountFromProductIfMissing(order);
        order.setStatus("DRAFT");
        order.setOrderNo(generateOrderNo());
        order.setPayStatus("UNPAID");
        tourOrderMapper.insert(order);
        return order;
    }
    
    @Override
    @Transactional
    public void update(TourOrder order) {
        populateAmountFromProductIfMissing(order);
        tourOrderMapper.updateById(order);
    }
    
    @Override
    public TourOrder getDetail(Long orderId) {
        TourOrder order = tourOrderMapper.selectById(orderId);
        populateAmountFromProductIfMissing(order);
        return order;
    }
    
    @Override
    @Transactional
    public boolean confirmOrder(Long orderId, List<ResourceBooking> bookings) {
        // 1. 检测冲突
        List<ResourceBooking> conflicts = detectConflicts(orderId, bookings);
        if (!conflicts.isEmpty()) {
            return false;
        }
        
        // 2. 创建资源占用记录（事务内强一致）
        for (ResourceBooking booking : bookings) {
            booking.setOrderId(orderId);
            resourceBookingService.create(booking);
        }
        
        // 3. 更新订单状态为已确认
        TourOrder order = new TourOrder();
        order.setId(orderId);
        order.setStatus("CONFIRMED");
        tourOrderMapper.updateById(order);
        
        return true;
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        // 1. 查询该订单的所有资源占用
        List<ResourceBooking> bookings = resourceBookingService.getByOrderId(orderId);
        
        // 2. 释放所有资源占用
        for (ResourceBooking booking : bookings) {
            resourceBookingService.cancel(booking.getId());
        }
        
        // 3. 更新订单状态为已取消
        TourOrder order = new TourOrder();
        order.setId(orderId);
        order.setStatus("CANCELLED");
        tourOrderMapper.updateById(order);
    }
    
    @Override
    public List<ResourceBooking> detectConflicts(Long orderId, List<ResourceBooking> bookings) {
        List<ResourceBooking> conflicts = new ArrayList<>();
        
        for (ResourceBooking booking : bookings) {
            // 检测该资源在指定时间段内是否有冲突
            List<ResourceBooking> conflictList = resourceBookingService.checkConflicts(
                booking.getResourceId(),
                booking.getStartTime(),
                booking.getEndTime()
            );
            conflicts.addAll(conflictList);
        }
        
        return conflicts;
    }

    @Override
    public OrderRevenueStats getRevenueStats() {
        List<TourOrder> allOrders = tourOrderMapper.selectList(new QueryWrapper<TourOrder>().orderByDesc("created_at"));
        List<TourOrder> activeOrders = allOrders.stream()
                .filter(order -> !"CANCELLED".equals(order.getStatus()))
                .toList();

        BigDecimal totalPlannedIncome = sumAmounts(activeOrders);
        BigDecimal totalActualIncome = sumAmounts(activeOrders.stream()
                .filter(order -> "PAID".equals(order.getPayStatus()))
                .toList());

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        List<TourOrder> currentMonthOrders = activeOrders.stream()
                .filter(order -> isBetween(extractCreatedDate(order), monthStart, monthEnd))
                .toList();
        BigDecimal monthPlannedIncome = sumAmounts(currentMonthOrders);
        BigDecimal monthActualIncome = sumAmounts(currentMonthOrders.stream()
                .filter(order -> "PAID".equals(order.getPayStatus()))
                .toList());
        BigDecimal gapIncome = amount(monthPlannedIncome.subtract(monthActualIncome));
        Integer completionRate = percent(monthActualIncome, monthPlannedIncome);

        return OrderRevenueStats.builder()
                .totalOrders((long) allOrders.size())
                .activeOrders((long) activeOrders.size())
                .paidOrders(activeOrders.stream().filter(order -> "PAID".equals(order.getPayStatus())).count())
                .pendingReceiptOrders(activeOrders.stream().filter(order -> !"PAID".equals(order.getPayStatus())).count())
                .totalPlannedIncome(totalPlannedIncome)
                .totalActualIncome(totalActualIncome)
                .weeklyTrendDirection(calculateTrendDirection(activeOrders, 7))
                .weeklyChangeRate(calculateTrendRate(activeOrders, 7))
                .monthlyTrendDirection(calculateTrendDirection(activeOrders, 30))
                .monthlyChangeRate(calculateTrendRate(activeOrders, 30))
                .weeklyTrend(buildTrend(activeOrders, 7))
                .monthlyTrend(buildTrend(activeOrders, 30))
                .statusDistribution(buildStatusDistribution(activeOrders))
                .planActual(OrderRevenuePlanActual.builder()
                        .plannedIncome(monthPlannedIncome)
                        .actualIncome(monthActualIncome)
                        .gapIncome(gapIncome)
                        .completionRate(completionRate)
                        .build())
                .collectionRisks(buildRiskItems(activeOrders))
                .build();
    }
    
    /**
     * 生成订单号：20240227001
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private List<OrderRevenueTrendPoint> buildTrend(List<TourOrder> orders, int days) {
        LocalDate today = LocalDate.now();
        List<OrderRevenueTrendPoint> points = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate currentDate = today.minusDays(i);
            BigDecimal plannedIncome = sumAmounts(orders.stream()
                    .filter(order -> currentDate.equals(extractCreatedDate(order)))
                    .toList());
            BigDecimal actualIncome = sumAmounts(orders.stream()
                    .filter(order -> "PAID".equals(order.getPayStatus()))
                    .filter(order -> currentDate.equals(extractActualIncomeDate(order)))
                    .toList());
            points.add(OrderRevenueTrendPoint.builder()
                    .label(currentDate.getMonthValue() + "/" + currentDate.getDayOfMonth())
                    .plannedIncome(plannedIncome)
                    .actualIncome(actualIncome)
                    .build());
        }
        return points;
    }

    private String calculateTrendDirection(List<TourOrder> orders, int days) {
        BigDecimal rate = calculateTrendRate(orders, days);
        int compare = rate.compareTo(BigDecimal.ZERO);
        if (compare > 0) {
            return "UP";
        }
        if (compare < 0) {
            return "DOWN";
        }
        return "FLAT";
    }

    private BigDecimal calculateTrendRate(List<TourOrder> orders, int days) {
        LocalDate today = LocalDate.now();
        LocalDate currentStart = today.minusDays(days - 1L);
        LocalDate previousStart = currentStart.minusDays(days);
        LocalDate previousEnd = currentStart.minusDays(1);

        BigDecimal currentIncome = sumAmounts(orders.stream()
                .filter(order -> isBetween(extractCreatedDate(order), currentStart, today))
                .toList());
        BigDecimal previousIncome = sumAmounts(orders.stream()
                .filter(order -> isBetween(extractCreatedDate(order), previousStart, previousEnd))
                .toList());

        if (previousIncome.compareTo(BigDecimal.ZERO) == 0) {
            return currentIncome.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("100.00") : ZERO;
        }

        return amount(currentIncome.subtract(previousIncome)
                .multiply(new BigDecimal("100"))
                .divide(previousIncome, 2, RoundingMode.HALF_UP));
    }

    private List<OrderRevenueDistributionItem> buildStatusDistribution(List<TourOrder> orders) {
        List<OrderRevenueDistributionItem> distribution = new ArrayList<>();
        distribution.add(buildDistributionItem("已收款", "#2aa876", orders.stream()
                .filter(order -> "PAID".equals(order.getPayStatus()))
                .toList()));
        distribution.add(buildDistributionItem("部分收款", "#f59f00", orders.stream()
                .filter(order -> "PARTIAL".equals(order.getPayStatus()))
                .toList()));
        distribution.add(buildDistributionItem("已对账", "#4c6ef5", orders.stream()
                .filter(order -> !"PAID".equals(order.getPayStatus()))
                .filter(order -> !"PARTIAL".equals(order.getPayStatus()))
                .filter(order -> "COMPLETED".equals(order.getStatus()))
                .toList()));
        distribution.add(buildDistributionItem("待收款", "#e76f51", orders.stream()
                .filter(order -> !"PAID".equals(order.getPayStatus()))
                .filter(order -> !"PARTIAL".equals(order.getPayStatus()))
                .filter(order -> !"COMPLETED".equals(order.getStatus()))
                .toList()));
        return distribution;
    }

    private OrderRevenueDistributionItem buildDistributionItem(String label, String color, List<TourOrder> orders) {
        return OrderRevenueDistributionItem.builder()
                .label(label)
                .count((long) orders.size())
                .amount(sumAmounts(orders))
                .color(color)
                .build();
    }

    private List<OrderRevenueRiskItem> buildRiskItems(List<TourOrder> orders) {
        LocalDate today = LocalDate.now();
        return orders.stream()
                .filter(order -> "CONFIRMED".equals(order.getStatus()) || "COMPLETED".equals(order.getStatus()))
                .filter(order -> !"PAID".equals(order.getPayStatus()))
                .sorted(Comparator.comparing(TourOrder::getAmount, Comparator.nullsLast(BigDecimal::compareTo)).reversed())
                .limit(8)
                .map(order -> OrderRevenueRiskItem.builder()
                        .id(order.getId())
                        .orderNo(order.getOrderNo())
                        .customerId(order.getCustomerId())
                        .status(order.getStatus())
                        .payStatus(order.getPayStatus())
                        .amount(amount(order.getAmount()))
                        .progress(resolveProgress(order.getPayStatus()))
                        .overdueDays(extractCreatedDate(order) == null ? 0L : java.time.temporal.ChronoUnit.DAYS.between(extractCreatedDate(order), today))
                        .build())
                .toList();
    }

    private Integer resolveProgress(String payStatus) {
        return switch (payStatus) {
            case "PARTIAL" -> 60;
            case "PAID" -> 100;
            default -> 0;
        };
    }

    private BigDecimal sumAmounts(List<TourOrder> orders) {
        return amount(orders.stream()
                .map(TourOrder::getAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private BigDecimal amount(BigDecimal value) {
        return value == null ? ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private Integer percent(BigDecimal actual, BigDecimal planned) {
        if (planned == null || planned.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return actual.multiply(new BigDecimal("100"))
                .divide(planned, 0, RoundingMode.HALF_UP)
                .intValue();
    }

    private LocalDate extractCreatedDate(TourOrder order) {
        return order.getCreatedAt() == null ? null : order.getCreatedAt().toLocalDate();
    }

    private LocalDate extractActualIncomeDate(TourOrder order) {
        if (order.getUpdatedAt() != null) {
            return order.getUpdatedAt().toLocalDate();
        }
        return extractCreatedDate(order);
    }

    private boolean isBetween(LocalDate value, LocalDate start, LocalDate end) {
        return value != null && (value.isEqual(start) || value.isAfter(start)) && (value.isEqual(end) || value.isBefore(end));
    }

    private void populateAmountFromProductIfMissing(TourOrder order) {
        if (order == null || order.getAmount() != null || order.getProductId() == null) {
            return;
        }
        TourProduct product = tourProductMapper.selectById(order.getProductId());
        if (product != null && product.getBasePrice() != null) {
            order.setAmount(amount(product.getBasePrice()));
        }
    }
}
