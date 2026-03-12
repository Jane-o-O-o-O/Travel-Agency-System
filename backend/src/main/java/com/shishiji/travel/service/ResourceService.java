package com.shishiji.travel.service;

import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.resource.Resource;

/**
 * 资源Service
 */
public interface ResourceService {
    
    /**
     * 分页查询
     */
    PageResult<Resource> list(int pageNo, int pageSize, String searchKey);
    
    /**
     * 根据ID查询
     */
    Resource getById(Long id);
    
    /**
     * 新增资源
     */
    Resource create(Resource resource);
    
    /**
     * 更新资源
     */
    void update(Resource resource);
    
    /**
     * 删除资源
     */
    void delete(Long id);
}
