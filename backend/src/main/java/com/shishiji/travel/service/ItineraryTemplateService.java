package com.shishiji.travel.service;

import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.itinerary.ItineraryTemplate;

/**
 * 行程模板Service
 */
public interface ItineraryTemplateService {
    
    /**
     * 分页查询
     */
    PageResult<ItineraryTemplate> list(int pageNo, int pageSize, String searchKey);
    
    /**
     * 根据ID查询
     */
    ItineraryTemplate getById(Long id);
    
    /**
     * 新增模板
     */
    ItineraryTemplate create(ItineraryTemplate template);
    
    /**
     * 更新模板
     */
    void update(ItineraryTemplate template);
    
    /**
     * 删除模板
     */
    void delete(Long id);
}
