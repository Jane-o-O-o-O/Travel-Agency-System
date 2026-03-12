package com.shishiji.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shishiji.travel.mapper.SysUserMapper;
import com.shishiji.travel.model.system.SysUser;
import com.shishiji.travel.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 系统用户Service实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {
    
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public SysUser getById(Long id) {
        return sysUserMapper.selectById(id);
    }
    
    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(wrapper);
    }
    
    @Override
    public SysUser create(SysUser user) {
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 默认启用
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        sysUserMapper.insert(user);
        return user;
    }
    
    @Override
    public void update(SysUser user) {
        // 如果密码不为空，则加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        sysUserMapper.updateById(user);
    }
    
    @Override
    public void delete(Long id) {
        sysUserMapper.deleteById(id);
    }
}
