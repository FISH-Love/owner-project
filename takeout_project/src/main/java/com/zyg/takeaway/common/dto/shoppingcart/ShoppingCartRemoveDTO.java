package com.zyg.takeaway.common.dto.shoppingcart;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 删减购物车商品实体类
 */
@Data
public class ShoppingCartRemoveDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    // 菜品id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dishId;
    // 套餐id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setMealId;
}
