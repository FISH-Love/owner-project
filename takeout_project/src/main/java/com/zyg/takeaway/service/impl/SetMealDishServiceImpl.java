package com.zyg.takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyg.takeaway.entity.SetMealDish;
import com.zyg.takeaway.mapper.SetMealDishMapper;
import com.zyg.takeaway.service.SetMealDishService;
import org.springframework.stereotype.Service;

/**
 * 套餐菜品中间表业务层实现类
 */
@Service
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetMealDish> implements SetMealDishService {
}