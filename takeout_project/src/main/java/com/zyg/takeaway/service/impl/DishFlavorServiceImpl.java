package com.zyg.takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyg.takeaway.entity.DishFlavor;
import com.zyg.takeaway.mapper.DishFlavorMapper;
import com.zyg.takeaway.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * 菜品口味业务层接口实现类
 * 继承 ServiceImpl<DishFlavorMapper, DishFlavor> 以匹配接口泛型
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
    // 无需额外代码，直接继承即可使用 IService 通用方法
    // 如需自定义逻辑，可在此添加方法实现
}