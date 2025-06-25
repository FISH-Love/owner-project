package com.zyg.takeaway.common.dto.dish;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 编辑、数据回显返回对象
 */
@Data
public class DishGetResultDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    // 菜品名称
    private String name;
    // 菜品分类id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;  // 后端只需要返回分类id，前端处理
    // 菜品价格
    private BigDecimal price;
    // 图片
    private String image;
    // 描述信息
    private String description;
    // 0 停售 1 起售
    private Integer status;
    // 顺序
    private Integer sort;
    // 口味数据列表
    private List<DishFlavorDTO> flavors;
}