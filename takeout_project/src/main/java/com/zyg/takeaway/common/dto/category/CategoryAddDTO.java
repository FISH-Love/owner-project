package com.zyg.takeaway.common.dto.category;


import lombok.Data;

/**
 * 添加分类实体类
 */
@Data
public class CategoryAddDTO {
    /**
     * 类型 1 菜品分类 2 套餐分类
     */
    private Integer type;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 顺序
     */
    private Integer sort;
}
