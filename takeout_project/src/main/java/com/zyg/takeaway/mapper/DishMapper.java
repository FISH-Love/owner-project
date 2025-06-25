package com.zyg.takeaway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyg.takeaway.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品持久层接口
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}