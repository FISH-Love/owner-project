package com.zyg.takeaway.common.dto.category;


import lombok.Data;

/**
 * 修改分类实体类
 */
@Data
public class CategoryUpdateDTO {
    private Long id;
    private String name;
    private Integer sort;
}
