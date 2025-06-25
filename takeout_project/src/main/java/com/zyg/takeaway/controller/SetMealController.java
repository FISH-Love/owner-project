package com.zyg.takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.setmeal.SetMealAddDTO;
import com.zyg.takeaway.common.dto.setmeal.SetMealGetResultDTO;
import com.zyg.takeaway.common.dto.setmeal.SetMealResultDTO;
import com.zyg.takeaway.entity.SetMeal;
import com.zyg.takeaway.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理表现层
 */
@RestController
@RequestMapping("/setMeal")
@Slf4j
public class SetMealController {
    // 引入套餐业务层接口
    private final SetMealService setMealService;
    public SetMealController(SetMealService setMealService) {
        this.setMealService = setMealService;
    }
    /**
     * 新增套餐
     * @param dto 新增参数
     * @return 新增结果
     */
    @PostMapping
    public R<String> saveSetMeal(@RequestBody SetMealAddDTO dto) {
        return setMealService.addSetMeal(dto);
    }
    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    /**
     * 套餐分页
     * @param page 当前页数
     * @param pageSize 每页数据
     * @param name 名称
     * @return 分页后的数据
     */
    @GetMapping("/page")
    public R<Page<SetMealResultDTO>> findSetMealByPage(Long page, Long pageSize, String name) {
        return setMealService.findSetMealByPage(page, pageSize, name);
    }
    /**
     * 套餐物理删除
     *
     * @param ids 被删除的ID集合
     * @return 删除结果
     */
    @DeleteMapping
    public R<String> deletes(@RequestParam List<Long> ids) {
        // 物理删除
        return setMealService.deleteList(ids);
        // 加分项 逻辑删除(分页查询) +1
//        return setMealService.logicDelete(ids);
    }
    /**
     * 启用/禁用
     * @param status 修改状态
     * @param ids 被修改的id集合
     * @return 修改的结果
     */
    @PostMapping("/status/{status}")
    public R<String> switchStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        // return setMealService.switchStatus(status, ids);
        return setMealService.switchSetMeal(status, ids);
    }
    /**
     * 套餐修改数据回显
     * @param id 套餐id
     * @return 套餐参数
     */
    @GetMapping("/{id}")
    public R<SetMealGetResultDTO> findById(@PathVariable Long id) {
        return setMealService.findById(id);
    }
    /**
     * 修改套餐
     * @param dto 更新参数
     * @return 是否成功
     */
    @PutMapping
    public R<String> updateDish(@RequestBody SetMealGetResultDTO dto) {
        return setMealService.updateSetMeal(dto);
    }

}    