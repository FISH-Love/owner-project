package com.zyg.takeaway.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.dish.DishAddDTO;
import com.zyg.takeaway.common.dto.dish.DishGetResultDTO;
import com.zyg.takeaway.common.dto.dish.DishResultDTO;
import com.zyg.takeaway.entity.Category;
import com.zyg.takeaway.entity.Dish;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * 菜品业务层接口
 */
public interface DishService extends IService<Dish> {
    R<DishGetResultDTO> findById(Long id);
    R addDish(DishAddDTO dto);
    R<String> updateDish(DishGetResultDTO dto);
    R<Page<DishResultDTO>> findDishByPage(@Min(value = 1L) Long page, @Min(value = 1L) Long pageSize, String name);
    R<String> switchStatus(Integer status, List<Long> ids);
    R<String> deleteList(List<Long> ids);
    R<List<Category>> getCategoryByType(Integer type);
}