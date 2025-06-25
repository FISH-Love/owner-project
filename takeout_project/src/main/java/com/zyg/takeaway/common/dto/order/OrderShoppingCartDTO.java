package com.zyg.takeaway.common.dto.order;

import com.zyg.takeaway.entity.ShoppingCart;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车中有关下单数据实体
 */
@Data
public class OrderShoppingCartDTO {
    // 商品总和
    private BigDecimal total;
    // 购物车实体类集合
    private List<ShoppingCart> shoppingCartList;
}
