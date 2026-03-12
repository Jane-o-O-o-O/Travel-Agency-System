package com.shishiji.travel.controller.itinerary;

import com.shishiji.travel.common.response.ApiResponse;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.itinerary.ItineraryTemplate;
import com.shishiji.travel.service.ItineraryTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 行程模板管理API
 */
@RestController
@RequestMapping("/template")
@Tag(name = "行程模板管理")
@RequiredArgsConstructor
public class TemplateController {
    
    private final ItineraryTemplateService itineraryTemplateService;
    
    @Operation(summary = "分页查询行程模板")
    @GetMapping("/list")
    public ApiResponse<PageResult<ItineraryTemplate>> list(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String searchKey) {
        
        PageResult<ItineraryTemplate> result = itineraryTemplateService.list(pageNo, pageSize, searchKey);
        return ApiResponse.success(result);
    }
    
    @Operation(summary = "获取行程模板详情")
    @GetMapping("/{id}")
    public ApiResponse<ItineraryTemplate> getById(@PathVariable Long id) {
        ItineraryTemplate template = itineraryTemplateService.getById(id);
        if (template == null) {
            return ApiResponse.fail("行程模板不存在");
        }
        return ApiResponse.success(template);
    }
    
    @Operation(summary = "创建行程模板")
    @PostMapping
    public ApiResponse<ItineraryTemplate> create(@RequestBody ItineraryTemplate template) {
        ItineraryTemplate created = itineraryTemplateService.create(template);
        return ApiResponse.success(created);
    }
    
    @Operation(summary = "更新行程模板")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody ItineraryTemplate template) {
        template.setId(id);
        itineraryTemplateService.update(template);
        return ApiResponse.success(null);
    }
    
    @Operation(summary = "删除行程模板")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        itineraryTemplateService.delete(id);
        return ApiResponse.success(null);
    }
}
