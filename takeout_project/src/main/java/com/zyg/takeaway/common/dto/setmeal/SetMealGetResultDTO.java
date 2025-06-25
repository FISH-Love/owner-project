package com.zyg.takeaway.common.dto.setmeal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zyg.takeaway.entity.SetMealDish;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 修改套餐回显实体类
 */
@Data
public class SetMealGetResultDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    // 分类ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId; // 后端只需要返回分类id，前端处理
    // 套餐名称
    private String name;
    // 套餐价格
    private BigDecimal price;
    // 状态 0:停用 1:启用
    private Integer status;
    // 图片
    private String image;
    // 描述信息
    private String description;
    // 套餐菜品集合 - setMealDishes必须与前端保持一致
    private List<SetMealDishDTO> setMealDishes;
}