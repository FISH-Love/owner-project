package com.zyg.takeaway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyg.takeaway.entity.SetMeal;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 套餐持久层接口
 */
@Mapper
public interface SetMealMapper extends BaseMapper<SetMeal> {
    /**
     * 根据ID获取所有数据
     * @param id 套餐ID
     * @return 数据
     */
    SetMeal getById(Long id);
    /**
     * 修改状态
     * @param status 修改数字
     * @param ids 被修改ID集合
     * @return 修改几条
     */
    int switchSetMeal(Integer status, List<Long> ids);

}