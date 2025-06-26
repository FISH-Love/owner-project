package com.example.CultivationCimulator.Dao;

import com.example.CultivationCimulator.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    User findByUsername(String username); //通过用户名uname查找用户，注意要按照JPA的格式使用驼峰命名法
    User findByUsernameAndPassword(String username, String password);//通过用户名uname和密码查找用户
    // 查询用户是否存在且roleId不为空
    boolean existsByUsernameAndRoleIdNotNull(String username);

    // 添加根据用户名更新角色ID的方法
    @Modifying
    @Query("UPDATE User u SET u.roleId = :roleId WHERE u.username = :username")
    int updateRoleIdByUsername(@Param("username") String username, @Param("roleId") Long roleId);
    //根据用户名查用户信息的方法
}