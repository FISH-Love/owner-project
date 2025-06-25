package com.zyg.takeaway.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.dish.DishAddDTO;
import com.zyg.takeaway.common.dto.dish.DishGetResultDTO;
import com.zyg.takeaway.common.dto.dish.DishResultDTO;
import com.zyg.takeaway.entity.Category;
import com.zyg.takeaway.service.CategoryService;
import com.zyg.takeaway.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * 菜品表现层
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    /**
     * 菜品修改数据回显
     * @param id 菜品id
     * @return 菜品参数
     */
    @GetMapping("/{id}")
    public R<DishGetResultDTO> findById(@PathVariable Long id) {
        return dishService.findById(id);
    }
    /**
     * 新增菜品
     *
     * @param dto 封装实体
     * @return 是否成功
     */
    @PostMapping
    public R addDish(@RequestBody DishAddDTO dto) {
        return dishService.addDish(dto);
    }

    @GetMapping("/page")
    public R<Page<DishResultDTO>> page(
            @RequestParam @Validated @Min(value = 1, message = "页码不能小于1") Long page,
            @RequestParam @Validated @Min(value = 1, message = "每页记录数不能小于1") Long pageSize,
            @RequestParam(required = false) String name) {

        // 调用服务层方法进行分页查询
        return dishService.findDishByPage(page, pageSize, name);
    }
    /**
     * 修改菜品
     * @param dto 更新参数
     * @return 是否成功
     */
    @PutMapping
    public R<String> updateDish(@RequestBody DishGetResultDTO dto) {
        return dishService.updateDish(dto);
    }
    /**
     * 启用/禁用
     * @param status 修改状态
     * @param ids 被修改的id集合
     * @return 修改的结果
     */
    @PostMapping("/status/{status}")
    public R<String> switchStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        return dishService.switchStatus(status, ids);
    }
    /**
     * 菜品物理删除
     * @param ids 被删除的ID
     * @return 删除结果
     */
    @DeleteMapping
    public R<String> deletes(@RequestParam List<Long> ids) {
        // 物理删除
        return dishService.deleteList(ids);
        // 加分项 逻辑删除(分页查询) +1
    }
    /**
     * 添加菜品和套餐分类字段
     *
     * @param type 分类类型
     * @return 类型数据
     */
    @Autowired
    CategoryService categoryService;
//    @GetMapping("/list")
//    public  R<List<Category>> getCategoryByType(Long id) {
//        return categoryService.getCategoryByType(type);
//    }
}

