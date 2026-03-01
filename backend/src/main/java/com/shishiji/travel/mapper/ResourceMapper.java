package com.shishiji.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shishiji.travel.model.resource.Resource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {
}
