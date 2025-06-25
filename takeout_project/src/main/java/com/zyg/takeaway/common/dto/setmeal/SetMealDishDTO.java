package com.zyg.takeaway.common.dto.setmeal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 套餐关联菜品接收实体类
 */
@Data
public class SetMealDishDTO {
    // 菜品id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dishId;
    // 菜品名称 （冗余字段）
    private String name;
    // 菜品原价
    private BigDecimal price;
    // 份数
    private Integer copies;
    // 移动端查看套餐详细对应图片字段
    private String image;
}