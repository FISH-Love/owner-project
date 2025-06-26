package com.example.CultivationCimulator.Service;

import com.example.CultivationCimulator.POJO.Relic;
import java.util.List;

public interface RelicService {

    /**
     * 根据怪物类型获取掉落的遗物
     * @param monsterTypeId 怪物类型ID(1=小怪, 2=精英, 3=BOSS)
     * @return 掉落的遗物列表
     */
    List<Relic> getDroppedRelics(int monsterTypeId);

    /**
     * 根据ID获取遗物详情
     * @param relicId 遗物ID
     * @return 遗物详情
     */
    Relic getRelicById(Long relicId);

    /**
     * 保存或更新遗物
     * @param relic 遗物实体
     * @return 保存后的遗物
     */
    Relic saveRelic(Relic relic);

    /**
     * 删除遗物
     * @param relicId 遗物ID
     */
    void deleteRelic(Long relicId);
}