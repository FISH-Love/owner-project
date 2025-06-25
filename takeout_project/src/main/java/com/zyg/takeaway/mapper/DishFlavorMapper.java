package com.zyg.takeaway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyg.takeaway.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
    // 无需实现，BaseMapper 已提供通用方法
}