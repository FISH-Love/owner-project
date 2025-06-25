package com.zyg.takeaway.common.dto.shoppingcart;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 添加到套餐实体类
 */
@Data
public class ShoppingCartAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 名称
    private String name;
    // 菜品id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dishId;
    // 套餐id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setMealId;
    // 口味
    private String dishFlavor;
    // 数量
    private Integer number;
    // 金额
    private BigDecimal amount;
    // 图片
    private String image;
}
