package com.shishiji.travel.controller.resource;

import com.shishiji.travel.common.response.ApiResponse;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.resource.Resource;
import com.shishiji.travel.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 资源管理API
 */
@RestController
@RequestMapping("/resource")
@Tag(name = "资源管理")
@RequiredArgsConstructor
public class ResourceController {
    
    private final ResourceService resourceService;
    
    @Operation(summary = "分页查询资源")
    @GetMapping("/list")
    public ApiResponse<PageResult<Resource>> list(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String searchKey) {
        
        PageResult<Resource> result = resourceService.list(pageNo, pageSize, searchKey);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取资源详情")
    @GetMapping("/{id}")
    public ApiResponse<Resource> getById(@PathVariable Long id) {
        Resource resource = resourceService.getById(id);
        if (resource == null) {
            return ApiResponse.fail("资源不存在");
        }
        return ApiResponse.success(resource);
    }
    
    @Operation(summary = "创建资源")
    @PostMapping
    public ApiResponse<Resource> create(@RequestBody Resource resource) {
        Resource created = resourceService.create(resource);
        return ApiResponse.success(created);
    }
    
    @Operation(summary = "更新资源")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Resource resource) {
        resource.setId(id);
        resourceService.update(resource);
        return ApiResponse.success(null);
    }
    
    @Operation(summary = "删除资源")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ApiResponse.success(null);
    }
}
