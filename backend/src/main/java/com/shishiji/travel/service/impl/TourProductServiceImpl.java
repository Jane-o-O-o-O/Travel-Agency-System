package com.shishiji.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.mapper.TourProductMapper;
import com.shishiji.travel.model.product.TourProduct;
import com.shishiji.travel.service.TourProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 旅游产品Service实现类
 */
@Service
@RequiredArgsConstructor
public class TourProductServiceImpl implements TourProductService {
    
    private final TourProductMapper tourProductMapper;
    
    @Override
    public PageResult<TourProduct> list(int pageNo, int pageSize, String searchKey) {
        LambdaQueryWrapper<TourProduct> wrapper = new LambdaQueryWrapper<>();
        if (searchKey != null && !searchKey.isEmpty()) {
            wrapper.like(TourProduct::getName, searchKey)
                    .or()
                    .like(TourProduct::getCode, searchKey);
        }
        wrapper.orderByDesc(TourProduct::getCreatedAt);
        
        Page<TourProduct> page = tourProductMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResult.<TourProduct>builder()
                .total(page.getTotal())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .list(page.getRecords())
                .build();
    }
    
    @Override
    public TourProduct getById(Long id) {
        return tourProductMapper.selectById(id);
    }
    
    @Override
    public TourProduct create(TourProduct product) {
        tourProductMapper.insert(product);
        return product;
    }
    
    @Override
    public void update(TourProduct product) {
        tourProductMapper.updateById(product);
    }
    
    @Override
    public void delete(Long id) {
        tourProductMapper.deleteById(id);
    }
}
