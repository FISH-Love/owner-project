package com.example.CultivationCimulator.Service.ServiceImpl;


import com.example.CultivationCimulator.Dao.MonsterDao;
import com.example.CultivationCimulator.POJO.Monster;
import com.example.CultivationCimulator.Service.MonsterService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class MonsterServiceImpl implements MonsterService {

    @Resource
    private MonsterDao monsterDao;
    private final Random random = new Random();

    @Override
    public List<Monster> getAllMonsters() {
        return monsterDao.findAllActive();
    }

    @Override
    public Optional<Monster> getMonsterById(Long id) {
        return monsterDao.findById(id);
    }

    @Override
    public List<Monster> getMonstersByType(Integer type) {
        return monsterDao.findByType(type);
    }

    @Override
    public Monster getRandomMonster() {
        List<Monster> monsterList = getAllMonsters();
        if (monsterList.isEmpty()) {
            throw new IllegalStateException("没有可用的怪物");
        }
        int randomIndex = random.nextInt(monsterList.size());
        return monsterList.get(randomIndex);
    }

    @Override
    public Monster getRandomMonsterByType(Integer type) {
        List<Monster> monsterList = getMonstersByType(type);
        if (monsterList.isEmpty()) {
            throw new IllegalStateException("没有该类型的怪物");
        }
        int randomIndex = random.nextInt(monsterList.size());
        return monsterList.get(randomIndex);
    }
}