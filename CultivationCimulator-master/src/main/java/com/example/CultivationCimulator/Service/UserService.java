package com.example.CultivationCimulator.Service;

import com.example.CultivationCimulator.POJO.User;

import java.util.List;

public interface UserService {
    /**
     * 登录服务
     * @param username
     * @param password
     * @return
     */
    User loginService(String username,String password);

    /**
     * 注册服务
     * @param user
     * @return
     */
    User registerService(User user);
    /**
     *检查用户是否绑定解决的接口服务
     */
    boolean checkUserHasRole(String username);
    /**
     * 为角色绑定角色的方法
     */
    boolean bindRoleToUser(String username, Long roleId);
    /**
     * 根据用户名查用户信息的方法
     */
    User findUserByUsername(String username);
}
