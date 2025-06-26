package com.example.CultivationCimulator.POJO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 怪物信息实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "monster")
public class Monster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "min_hp", nullable = false)
    private Integer minHp;

    @Column(name = "max_hp", nullable = false)
    private Integer maxHp;

    @Column(name = "min_def", nullable = false)
    private Integer minDef;

    @Column(name = "max_def", nullable = false)
    private Integer maxDef;

    @Column(name = "min_atk", nullable = false)
    private Integer minAtk;

    @Column(name = "max_atk", nullable = false)
    private Integer maxAtk;

    @Column(name = "exp_reward", nullable = false)
    private Integer expReward;

    @Column(name = "description", length = 255)
    private String description;

    // 怪物类型枚举
    public enum MonsterType {
        NORMAL(1, "普通怪"),
        ELITE(2, "精英怪"),
        BOSS(3, "Boss");

        private final int code;
        private final String description;

        MonsterType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}