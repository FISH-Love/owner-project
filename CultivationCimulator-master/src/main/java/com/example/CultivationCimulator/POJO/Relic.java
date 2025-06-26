package com.example.CultivationCimulator.POJO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "relics")
public class Relic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "type")
    private int relicType;  // 1=普通, 2=罕见, 3=稀有, 4=BOSS

    private int hp;         // 生命值修正
    private int atk;        // 攻击力修正
    private int def;        // 防御力修正

    private String desc;    // 遗物描述
}