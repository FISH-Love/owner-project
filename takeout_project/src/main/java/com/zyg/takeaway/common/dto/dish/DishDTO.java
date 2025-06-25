package com.zyg.takeaway.common.dto.dish;

import com.zyg.takeaway.entity.Dish;
import com.zyg.takeaway.entity.DishFlavor;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class DishDTO extends Dish {
    // 口味数据列表
    private List<DishFlavor> flavors;
}
