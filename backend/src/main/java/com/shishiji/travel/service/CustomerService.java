package com.shishiji.travel.service;

import com.shishiji.travel.common.response.PageResult;
import com.shishiji.travel.model.customer.Customer;

/**
 * 客户Service
 */
public interface CustomerService {
    
    /**
     * 分页查询
     */
    PageResult<Customer> list(int pageNo, int pageSize, String searchKey);
    
    /**
     * 根据ID查询
     */
    Customer getById(Long id);
    
    /**
     * 新增客户
     */
    Customer create(Customer customer);
    
    /**
     * 更新客户
     */
    void update(Customer customer);
    
    /**
     * 删除客户
     */
    void delete(Long id);
}
