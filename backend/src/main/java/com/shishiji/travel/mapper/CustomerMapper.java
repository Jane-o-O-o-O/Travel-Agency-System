package com.shishiji.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shishiji.travel.model.customer.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
