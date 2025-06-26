package com.example.CultivationCimulator.POJO;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户实体类，对应数据库中的user表
 */
@Data
@Entity
@Table(name = "user", indexes = {
        @Index(name = "idx_username", columnList = "username", unique = true),
        @Index(name = "idx_role_id", columnList = "role_id")
})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "role_id")
    private Long roleId;
}
