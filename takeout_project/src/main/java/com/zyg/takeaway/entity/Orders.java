package com.zyg.takeaway.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
// @TableName("orders") // 指定数据库表名
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;

    // 主键ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    // 订单号
    private String number;
    // 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
    private Integer status;
    // 下单用户id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    // 地址id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long addressBookId;
    // 下单时间
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
    // 结账时间
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkoutTime;
    // 支付方式 1微信，2支付宝
    private Integer payMethod;
    // 实收金额
    private BigDecimal amount;
    // 备注
    private String remark;
    // 用户名
    private String userName;
    // 手机号
    private String phone;
    // 地址
    private String address;
    // 收货人
    private String consignee;
}