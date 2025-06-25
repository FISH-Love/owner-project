package com.zyg.takeaway.common.dto.dish;

import lombok.Data;


/**
 * 新增菜品口味实体类
 */
@Data
public class DishFlavorDTO {

    /**
     * 口味名称
     */
    private String name;
    /**
     * 口味数据list
     */
    private String value;
    /**
     * 数据回显
     */
    private Boolean showOption;

}
