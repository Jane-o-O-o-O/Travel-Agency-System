package com.shishiji.travel.service;

import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.product.TourProduct;

/**
 * 旅游产品Service
 */
public interface TourProductService {
    
    /**
     * 分页查询
     */
    PageResult<TourProduct> list(int pageNo, int pageSize, String searchKey);
    
    /**
     * 根据ID查询
     */
    TourProduct getById(Long id);
    
    /**
     * 新增产品
     */
    TourProduct create(TourProduct product);
    
    /**
     * 更新产品
     */
    void update(TourProduct product);
    
    /**
     * 删除产品
     */
    void delete(Long id);
}
