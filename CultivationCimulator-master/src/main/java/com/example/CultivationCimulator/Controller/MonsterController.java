package com.example.CultivationCimulator.Controller;


import com.example.CultivationCimulator.POJO.Monster;
import com.example.CultivationCimulator.Service.MonsterService;
import com.example.CultivationCimulator.Util.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/monsters")
public class MonsterController {

    @Resource
    private MonsterService monsterService;

    /**
     * 获取所有怪物
     */
    @GetMapping("/getAllMonsters")
    public Result<List<Monster>> getAllMonsters() {
        List<Monster> monsterList = monsterService.getAllMonsters();
        return Result.success(monsterList, "获取怪物列表成功");
    }

    /**
     * 根据ID获取怪物
     */
    @GetMapping("/{id}")
    public Result<Monster> getMonsterById(@PathVariable Long id) {
        return monsterService.getMonsterById(id)
                .map(monster -> Result.success(monster, "获取怪物成功"))
                .orElseGet(() -> Result.error("怪物不存在"));
    }

    /**
     * 根据类型获取怪物
     */
    @GetMapping("/type/{type}")
    public Result<List<Monster>> getMonstersByType(@PathVariable Integer type) {
        List<Monster> monsterList = monsterService.getMonstersByType(type);
        return Result.success(monsterList, "获取指定类型怪物成功");
    }

    /**
     * 随机获取一个怪物
     */
    @GetMapping("/random")
    public Result<Monster> getRandomMonster() {
        try {
            Monster randomMonster = monsterService.getRandomMonster();
            return Result.success(randomMonster, "成功获取随机怪物");
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 随机获取指定类型的怪物
     */
    @GetMapping("/random/type/{type}")
    public Result<Monster> getRandomMonsterByType(@PathVariable Integer type) {
        try {
            Monster randomMonster = monsterService.getRandomMonsterByType(type);
            return Result.success(randomMonster, "成功获取指定类型的随机怪物");
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }
}