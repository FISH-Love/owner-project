package com.example.CultivationCimulator.Service;

import com.example.CultivationCimulator.POJO.Role;

import java.util.List;

public interface RoleService {
    /**
     * 查询所有角色名字的方法
     */
    List<String> findAllRoleNames();
    /**
     * 查新所有角色的方法
     */
    List<Role> findAllRoles();
}
