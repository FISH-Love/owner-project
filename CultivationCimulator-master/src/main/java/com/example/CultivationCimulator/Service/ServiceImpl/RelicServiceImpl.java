package com.example.CultivationCimulator.Service.ServiceImpl;


import com.example.CultivationCimulator.Dao.RelicDao;
import com.example.CultivationCimulator.POJO.Relic;
import com.example.CultivationCimulator.Service.RelicService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class RelicServiceImpl implements RelicService {

    @Autowired
    private RelicDao relicDao;

    // 掉落规则：怪物类型ID -> 可掉落的遗物类型列表
    private static final int[][] DROP_RULES = {
            {},                      // 索引0不使用
            {1},                     // 小怪(1)掉落普通遗物(1)
            {2},                     // 精英怪(2)掉落罕见遗物(2)
            {3, 4}                   // BOSS(3)掉落稀有(3)和BOSS(4)遗物
    };

    // 掉落概率：遗物类型 -> 掉落概率(%)
    private static final double[] DROP_PROBABILITIES = {
            0.0,   // 索引0不使用
            100.0, // 普通遗物(1) 100%掉落
            100.0, // 罕见遗物(2) 100%掉落
            100.0, // 稀有遗物(3) 100%掉落
            100.0  // BOSS遗物(4) 100%掉落
    };

    @Override
    public List<Relic> getDroppedRelics(int monsterTypeId) {
        // 验证怪物类型ID
        if (monsterTypeId < 1 || monsterTypeId >= DROP_RULES.length) {
            throw new IllegalArgumentException("无效的怪物类型ID: " + monsterTypeId);
        }

        List<Relic> droppedRelics = new ArrayList<>();
        Random random = new Random();

        // 获取该怪物类型对应的遗物掉落规则
        int[] possibleRelicTypes = DROP_RULES[monsterTypeId];

        for (int relicType : possibleRelicTypes) {
            // 检查是否触发掉落（根据概率）
            if (random.nextDouble() * 100 < DROP_PROBABILITIES[relicType]) {
                // 从数据库随机获取一个该类型的遗物
                Relic relic = relicDao.findRandomByType(relicType);
                if (relic != null) {
                    droppedRelics.add(relic);
                }
            }
        }

        return droppedRelics;
    }

    @Override
    public Relic getRelicById(Long relicId) {
        return relicDao.findById(relicId).orElse(null);
    }

    @Override
    public Relic saveRelic(Relic relic) {
        return relicDao.save(relic);
    }

    @Override
    public void deleteRelic(Long relicId) {
        relicDao.deleteById(relicId);
    }
}