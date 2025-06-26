package com.example.CultivationCimulator.POJO;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * 角色实体类，对应数据库中的role表
 */
@Data
@Entity
@Table(name = "role", indexes = {
        @Index(name = "idx_name", columnList = "name", unique = true)
})
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "hp", nullable = false)
    private Integer hp = 100;

    @Column(name = "def", nullable = false)
    private Integer def = 10;

    @Column(name = "atk", nullable = false)
    private Integer atk = 10;

    @Column(name = "cultivation", nullable = false)
    private Integer cultivation = 1;

    @Column(name = "exp", nullable = false)
    private Integer exp = 0;

    @Column(name = "life_span", nullable = false)
    private Integer lifeSpan = 100;
}