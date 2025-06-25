package com.zyg.takeaway.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.category.CategoryAddDTO;
import com.zyg.takeaway.common.dto.category.CategoryUpdateDTO;
import com.zyg.takeaway.common.exception.MyException;
import com.zyg.takeaway.entity.Category;
import com.zyg.takeaway.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 分类管理表现层
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    // 引入分类业务层
    // @Autowired // 不建议
    // 建议通过构造获取
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 分类新增
     *
     * @param dto 分类数据
     * @return 是否成功
     */
    @PostMapping
    public R<String> save(@RequestBody CategoryAddDTO dto) {
        // 无需理会分类类型 前端会设计好 在前端传递的字段中 1表示菜品 2表示套餐
        return categoryService.addCategory(dto);
    }
    /**
     * 删除分类套餐
     *
     * @param id 删除ID
     * @return 结果
     */
    @DeleteMapping()
    public R<String> deleteById(Long id) {
        R<String> r;
        try {
            // 逻辑删除
            r = categoryService.updateById(id);
        } catch (MyException e) {
            return R.error(e.getMessage());
        }
        return r;
    }
    // 新增分页查询方法
    @GetMapping("/page")
    public R<Page<Category>> page(
            @RequestParam("page") Long page,
            @RequestParam("pageSize") Long pageSize) {
        return categoryService.findByPage(page, pageSize);
    }
    /**
     * 修改分类信息
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody CategoryUpdateDTO dto) {
        return categoryService.updateCategory(dto);
    }
    /**
     * 添加菜品和套餐分类字段
     *
     * @param type 分类类型
     * @return 类型数据
     */
    @GetMapping("/list")
    public R getCategoryByType(Integer type) {
        return categoryService.getCategoryByType(type);
    }



}
