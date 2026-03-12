package com.shishiji.travel.controller.customer;

import com.shishiji.travel.common.response.ApiResponse;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.customer.Customer;
import com.shishiji.travel.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 客户管理API
 */
@RestController
@RequestMapping("/customer")
@Tag(name = "客户管理")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;
    
    @Operation(summary = "分页查询客户")
    @GetMapping("/list")
    public ApiResponse<PageResult<Customer>> list(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String searchKey) {
        
        PageResult<Customer> result = customerService.list(pageNo, pageSize, searchKey);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取客户详情")
    @GetMapping("/{id}")
    public ApiResponse<Customer> getById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        if (customer == null) {
            return ApiResponse.fail("客户不存在");
        }
        return ApiResponse.success(customer);
    }
    
    @Operation(summary = "创建客户")
    @PostMapping
    public ApiResponse<Customer> create(@RequestBody Customer customer) {
        Customer created = customerService.create(customer);
        return ApiResponse.success(created);
    }
    
    @Operation(summary = "更新客户")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        customerService.update(customer);
        return ApiResponse.success(null);
    }
    
    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ApiResponse.success(null);
    }
}
