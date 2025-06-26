package com.example.CultivationCimulator.POJO;

import lombok.*;
import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skill")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "type", nullable = false)
    private Integer type; // 1:普攻,2:元素战技,3:元素爆发

    @Column(name = "damage", nullable = false)
    private Integer damage;

    @Column(name = "description", length = 255)
    private String description;

    // 关联角色技能表
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoleSkill> roleSkills;

    // 技能类型枚举
    public enum SkillType {
        NORMAL_ATTACK(1, "普攻"),
        ELEMENTAL_SKILL(2, "元素战技"),
        ULTIMATE_SKILL(3, "元素爆发");

        private final int code;
        private final String description;

        SkillType(int code, String description) {
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