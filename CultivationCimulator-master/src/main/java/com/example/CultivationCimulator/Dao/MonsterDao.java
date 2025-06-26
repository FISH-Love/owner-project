package com.example.CultivationCimulator.Dao;

import com.example.CultivationCimulator.POJO.Monster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonsterDao extends JpaRepository<Monster, Long>, JpaSpecificationExecutor<Monster> {
    //根据类型查询怪物
    List<Monster> findByType(Integer type);
    // 获取所有怪物
    @Query("SELECT m FROM Monster m ")
    List<Monster> findAllActive();
}