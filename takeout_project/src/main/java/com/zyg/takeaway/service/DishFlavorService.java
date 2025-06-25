package com.zyg.takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyg.takeaway.entity.DishFlavor;

/**
 * 菜品口味业务层接口
 * 继承 IService<DishFlavor> 以使用 MyBatis-Plus 通用服务方法
 */
public interface DishFlavorService extends IService<DishFlavor> {
    // 可根据业务需求添加自定义方法，例如：
    // List<DishFlavor> getByDishId(Long dishId);
}