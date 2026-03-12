package com.shishiji.travel.controller.product;

import com.shishiji.travel.common.response.ApiResponse;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.product.TourProduct;
import com.shishiji.travel.service.TourProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 旅游产品管理API
 */
@RestController
@RequestMapping("/product")
@Tag(name = "产品管理")
@RequiredArgsConstructor
public class ProductController {
    
    private final TourProductService tourProductService;
    
    @Operation(summary = "分页查询产品")
    @GetMapping("/list")
    public ApiResponse<PageResult<TourProduct>> list(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String searchKey) {
        
        PageResult<TourProduct> result = tourProductService.list(pageNo, pageSize, searchKey);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取产品详情")
    @GetMapping("/{id}")
    public ApiResponse<TourProduct> getById(@PathVariable Long id) {
        TourProduct product = tourProductService.getById(id);
        if (product == null) {
            return ApiResponse.fail("产品不存在");
        }
        return ApiResponse.success(product);
    }
    
    @Operation(summary = "创建产品")
    @PostMapping
    public ApiResponse<TourProduct> create(@RequestBody TourProduct product) {
        TourProduct created = tourProductService.create(product);
        return ApiResponse.success(created);
    }
    
    @Operation(summary = "更新产品")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody TourProduct product) {
        product.setId(id);
        tourProductService.update(product);
        return ApiResponse.success(null);
    }
    
    @Operation(summary = "删除产品")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        tourProductService.delete(id);
        return ApiResponse.success(null);
    }
}
