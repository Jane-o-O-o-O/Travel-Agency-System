package com.shishiji.travel.service.impl;

import com.shishiji.travel.mapper.PortalUserMapper;
import com.shishiji.travel.model.portal.PortalUser;
import com.shishiji.travel.service.PortalUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PortalUserServiceImpl implements PortalUserService {

    private final PortalUserMapper portalUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PortalUser getByUsername(String username) {
        return portalUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PortalUser>()
                        .eq(PortalUser::getUsername, username));
    }

    @Override
    public PortalUser getById(Long id) {
        return portalUserMapper.selectById(id);
    }

    @Override
    public PortalUser register(PortalUser user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setStatus(1);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        portalUserMapper.insert(user);
        return user;
    }
}
