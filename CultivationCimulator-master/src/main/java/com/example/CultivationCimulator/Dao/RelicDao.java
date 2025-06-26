package com.example.CultivationCimulator.Dao;


import com.example.CultivationCimulator.POJO.Relic;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelicDao {

    // 根据遗物类型查询遗物
    List<Relic> findByRelicType(int relicType);

    // 随机获取一个指定类型的遗物
    Relic findRandomByType(int relicType);
    Optional<Relic> findById(Long id);
    Relic save(Relic relic);
    void deleteById(Long id);
}