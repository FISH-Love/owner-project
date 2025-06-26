package com.example.CultivationCimulator.Service;

import com.example.CultivationCimulator.POJO.Monster;

import java.util.List;
import java.util.Optional;

public interface MonsterService {
    // 获取所有怪物
    List<Monster> getAllMonsters();

    // 根据ID获取怪物
    Optional<Monster> getMonsterById(Long id);

    // 根据类型获取怪物
    List<Monster> getMonstersByType(Integer type);

    // 随机获取一个怪物
    Monster getRandomMonster();

    // 随机获取指定类型的怪物
    Monster getRandomMonsterByType(Integer type);
}
