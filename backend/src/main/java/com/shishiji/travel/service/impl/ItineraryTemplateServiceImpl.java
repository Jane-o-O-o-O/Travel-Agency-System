package com.shishiji.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.mapper.ItineraryTemplateMapper;
import com.shishiji.travel.model.itinerary.ItineraryTemplate;
import com.shishiji.travel.service.ItineraryTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 行程模板Service实现类
 */
@Service
@RequiredArgsConstructor
public class ItineraryTemplateServiceImpl implements ItineraryTemplateService {
    
    private final ItineraryTemplateMapper itineraryTemplateMapper;
    
    @Override
    public PageResult<ItineraryTemplate> list(int pageNo, int pageSize, String searchKey) {
        LambdaQueryWrapper<ItineraryTemplate> wrapper = new LambdaQueryWrapper<>();
        if (searchKey != null && !searchKey.isEmpty()) {
            wrapper.like(ItineraryTemplate::getName, searchKey)
                    .or()
                    .like(ItineraryTemplate::getTheme, searchKey);
        }
        wrapper.orderByDesc(ItineraryTemplate::getCreatedAt);
        
        Page<ItineraryTemplate> page = itineraryTemplateMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResult.<ItineraryTemplate>builder()
                .total(page.getTotal())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .list(page.getRecords())
                .build();
    }
    
    @Override
    public ItineraryTemplate getById(Long id) {
        return itineraryTemplateMapper.selectById(id);
    }
    
    @Override
    public ItineraryTemplate create(ItineraryTemplate template) {
        itineraryTemplateMapper.insert(template);
        return template;
    }
    
    @Override
    public void update(ItineraryTemplate template) {
        itineraryTemplateMapper.updateById(template);
    }
    
    @Override
    public void delete(Long id) {
        itineraryTemplateMapper.deleteById(id);
    }
}
