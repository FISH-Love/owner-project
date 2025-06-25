package com.zyg.takeaway.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.category.CategoryAddDTO;
import com.zyg.takeaway.common.dto.category.CategoryUpdateDTO;
import com.zyg.takeaway.entity.Category;

/**
 * 分类业务接口
 */
public interface CategoryService extends IService<Category> {
    R<String> updateById(Long id);
    void delById(Long id);
    R<Page<Category>> findByPage(Long page, Long pageSize);
    R<String> addCategory(CategoryAddDTO dto);
    R<String> updateCategory(CategoryUpdateDTO dto);
    R getCategoryByType(Integer type);

}