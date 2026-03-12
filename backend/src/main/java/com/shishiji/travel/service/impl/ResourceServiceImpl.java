package com.shishiji.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.mapper.ResourceMapper;
import com.shishiji.travel.model.resource.Resource;
import com.shishiji.travel.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 资源Service实现类
 */
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    
    private final ResourceMapper resourceMapper;
    
    @Override
    public PageResult<Resource> list(int pageNo, int pageSize, String searchKey) {
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        if (searchKey != null && !searchKey.isEmpty()) {
            wrapper.like(Resource::getName, searchKey)
                    .or()
                    .like(Resource::getCode, searchKey);
        }
        wrapper.orderByDesc(Resource::getCreatedAt);
        
        Page<Resource> page = resourceMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResult.<Resource>builder()
                .total(page.getTotal())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .list(page.getRecords())
                .build();
    }
    
    @Override
    public Resource getById(Long id) {
        return resourceMapper.selectById(id);
    }
    
    @Override
    public Resource create(Resource resource) {
        resourceMapper.insert(resource);
        return resource;
    }
    
    @Override
    public void update(Resource resource) {
        resourceMapper.updateById(resource);
    }
    
    @Override
    public void delete(Long id) {
        resourceMapper.deleteById(id);
    }
}
