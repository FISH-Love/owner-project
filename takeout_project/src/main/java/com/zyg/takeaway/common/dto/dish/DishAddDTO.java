package com.zyg.takeaway.common.dto.dish;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 新增菜品实体类
 */
@Data
public class DishAddDTO {

    // 菜品名称
    private String name;
    // 菜品分类id
    private Long categoryId;
    // 菜品价格
    private BigDecimal price;
    // 商品码
    private String code;
    // 图片
    private String image;
    // 描述信息
    private String description;
    // 0 停售 1 起售
    private Integer status;
    // 口味实体类集合
    private List<DishFlavorDTO> flavors;
}
