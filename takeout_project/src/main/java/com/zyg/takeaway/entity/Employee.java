package com.zyg.takeaway.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 员工实体类
 * <p>
 * Serializable数据在网络中需要把流进行反序列化
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    // 处理long类型精度丢失简单方法！
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    // 驼峰命名法 ---> 映射的字段名为 id_number
    private String idNumber;

    private Integer status;

    // @TableField自动获取字段插入数据库
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 自动插入当前时间
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}