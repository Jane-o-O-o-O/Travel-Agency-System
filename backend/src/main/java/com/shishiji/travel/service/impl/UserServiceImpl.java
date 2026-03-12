package com.shishiji.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shishiji.travel.mapper.SysUserMapper;
import com.shishiji.travel.model.system.SysUser;
import com.shishiji.travel.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public SysUser register(String username, String password, String realName, String email, String phone) {
        // 检查用户是否已存在
        if (existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 创建新用户
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(realName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setStatus(1); // 激活状态
        
        // 保存用户
        userMapper.insert(user);
        
        log.info("用户 {} 注册成功", username);
        return user;
    }
    
    @Override
    public SysUser login(String username, String password) {
        SysUser user = getUserByUsername(username);
        
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        if (user.getStatus() == 0) {
            throw new IllegalArgumentException("用户已被禁用");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("用户 {} 登录失败: 密码错误", username);
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        log.info("用户 {} 登录成功", username);
        return user;
    }
    
    @Override
    public SysUser getUserByUsername(String username) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("deleted", 0);
        return userMapper.selectOne(wrapper);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return getUserByUsername(username) != null;
    }
}
