package com.zyg.takeaway.common.dto.setmeal;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 新增套餐实体类
 */
@Data
public class SetMealAddDTO {
    // 分类id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    // 套餐名称
    private String name;
    // 套餐价格
    private BigDecimal price;
    // 状态 0:停用 1:启用
    private Integer status;
    // 编码
    private String code;
    // 描述信息
    private String description;
    // 图片
    private String image;
    // 套餐菜品集合 - setMealDishes必须与前端保持一致
    private List<SetMealDishDTO> setMealDishes;
}