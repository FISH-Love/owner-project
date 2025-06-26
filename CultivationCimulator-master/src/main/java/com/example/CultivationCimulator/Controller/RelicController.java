package com.example.CultivationCimulator.Controller;


import com.example.CultivationCimulator.Service.RelicService;
import com.example.CultivationCimulator.POJO.Relic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relics")
public class RelicController {

    @Autowired
    private RelicService relicService;

    // 获取战斗奖励
    @PostMapping("/rewards")
    public ResponseEntity<List<Relic>> getBattleRewards(@RequestBody int monsterTypeId) {
        try {
            List<Relic> rewards = relicService.getDroppedRelics(monsterTypeId);
            return ResponseEntity.ok(rewards);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 获取单个遗物
    @GetMapping("/{id}")
    public ResponseEntity<Relic> getRelic(@PathVariable Long id) {
        Relic relic = relicService.getRelicById(id);
        return relic != null ?
                ResponseEntity.ok(relic) :
                ResponseEntity.notFound().build();
    }

    // 创建或更新遗物
    @PostMapping
    public ResponseEntity<Relic> saveRelic(@RequestBody Relic relic) {
        Relic savedRelic = relicService.saveRelic(relic);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRelic);
    }

    // 删除遗物
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelic(@PathVariable Long id) {
        relicService.deleteRelic(id);
        return ResponseEntity.noContent().build();
    }
}