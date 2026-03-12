package com.shishiji.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.mapper.CustomerMapper;
import com.shishiji.travel.model.customer.Customer;
import com.shishiji.travel.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 客户Service实现类
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    
    private final CustomerMapper customerMapper;
    
    @Override
    public PageResult<Customer> list(int pageNo, int pageSize, String searchKey) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (searchKey != null && !searchKey.isEmpty()) {
            wrapper.like(Customer::getName, searchKey)
                    .or()
                    .like(Customer::getPhone, searchKey);
        }
        wrapper.orderByDesc(Customer::getCreatedAt);
        
        Page<Customer> page = customerMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResult.<Customer>builder()
                .total(page.getTotal())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .list(page.getRecords())
                .build();
    }
    
    @Override
    public Customer getById(Long id) {
        return customerMapper.selectById(id);
    }
    
    @Override
    public Customer create(Customer customer) {
        customerMapper.insert(customer);
        return customer;
    }
    
    @Override
    public void update(Customer customer) {
        customerMapper.updateById(customer);
    }
    
    @Override
    public void delete(Long id) {
        customerMapper.deleteById(id);
    }
}
