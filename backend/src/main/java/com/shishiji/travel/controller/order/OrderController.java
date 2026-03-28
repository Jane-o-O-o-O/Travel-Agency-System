package com.shishiji.travel.controller.order;

import com.shishiji.travel.common.response.ApiResponse;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.order.OrderRevenueStats;
import com.shishiji.travel.model.order.TourOrder;
import com.shishiji.travel.service.TourOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理API
 */
@RestController
@RequestMapping("/order")
@Tag(name = "订单管理")
@RequiredArgsConstructor
public class OrderController {
    
    private final TourOrderService tourOrderService;
    
    @Operation(summary = "分页查询订单")
    @GetMapping("/list")
    public ApiResponse<PageResult<TourOrder>> list(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId) {
        
        PageResult<TourOrder> result = tourOrderService.list(pageNo, pageSize, status, customerId);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public ApiResponse<TourOrder> getById(@PathVariable Long id) {
        TourOrder order = tourOrderService.getDetail(id);
        if (order == null) {
            return ApiResponse.fail("订单不存在");
        }
        return ApiResponse.success(order);
    }

    @Operation(summary = "订单营收统计")
    @GetMapping("/revenue/stats")
    public ApiResponse<OrderRevenueStats> revenueStats() {
        return ApiResponse.success(tourOrderService.getRevenueStats());
    }
    
    @Operation(summary = "创建订单")
    @PostMapping
    public ApiResponse<TourOrder> create(@RequestBody TourOrder order) {
        TourOrder created = tourOrderService.create(order);
        return ApiResponse.success(created);
    }
    
    @Operation(summary = "更新订单")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody TourOrder order) {
        order.setId(id);
        tourOrderService.update(order);
        return ApiResponse.success(null);
    }
    
    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        tourOrderService.cancelOrder(id);
        return ApiResponse.success(null);
    }
}
