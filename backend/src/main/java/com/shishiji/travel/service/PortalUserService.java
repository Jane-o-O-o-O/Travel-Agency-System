package com.shishiji.travel.service;

import com.shishiji.travel.model.portal.PortalUser;

/**
 * 门户用户Service
 */
public interface PortalUserService {
    
    PortalUser getByUsername(String username);
    
    PortalUser getById(Long id);
    
    PortalUser register(PortalUser user, String rawPassword);
}
