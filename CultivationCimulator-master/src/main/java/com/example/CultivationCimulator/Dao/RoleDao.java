package com.example.CultivationCimulator.Dao;

import com.example.CultivationCimulator.POJO.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleDao extends JpaRepository<Role, Long> {
    //查询所有角色名字的方法
    @Query("SELECT r.name FROM Role r") // JPQL 语法，直接指定查询字段
    List<String> findAllRoleName();

    //查询所有角色的方法
    @Query("SELECT r FROM Role r") // JPQL 语法，直接指定查询字段
    List<Role> findAllRole();


}
