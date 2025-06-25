package com.zyg.takeaway.service.impl;

import com.aliyun.core.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.category.CategoryAddDTO;
import com.zyg.takeaway.common.dto.category.CategoryUpdateDTO;
import com.zyg.takeaway.common.exception.MyException;
import com.zyg.takeaway.entity.Category;
import com.zyg.takeaway.entity.Dish;
import com.zyg.takeaway.entity.SetMeal;
import com.zyg.takeaway.mapper.CategoryMapper;
import com.zyg.takeaway.mapper.DishMapper;
import com.zyg.takeaway.mapper.SetMealMapper;
import com.zyg.takeaway.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


/**
 * 分类业务接口实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    // 导入菜品持久层
    private final DishMapper dishMapper;
    // 导入套餐持久层
    private final SetMealMapper setmealMapper;

    public CategoryServiceImpl(DishMapper dishMapper, SetMealMapper setmealMapper) {
        this.dishMapper = dishMapper;
        this.setmealMapper = setmealMapper;
    }

    @Override
    @Transactional
    public R<String> addCategory(CategoryAddDTO dto) {

        // 1.参数校验
        if (Objects.isNull(dto.getType())) {
            return R.error("参数非法！");
        }
        // StringUtils.isEmpty查看源码是过时的方法
        // if (StringUtils.isEmpty(dto.getName())){
        // }
        // 推荐使用 ObjectUtils.isEmpty 记的修改其它过时方法
        if (ObjectUtils.isEmpty(dto.getName())) {
            return R.error("参数非法！");
        }

        // 2. 业务处理 （现根据分类名称查询 ：存在 不添加 ，不存在添加）
        // 去复制员工新增判断用户是否重复的逻辑
        LambdaQueryWrapper<Category> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Category::getName, dto.getName());
        Category category = this.getOne(wrapper);
        // 判断这条数据是否为null(空) 是空 表示不存在直接新增
        if (!Objects.isNull(category)) {
            // !是空 --> 表示不是空 非空表示已存在
            // 判断是否被逻辑删除 查看is_delete字段是否为 0 已删除
            if (category.getIsDelete() == 1) {
                return R.error("名称重复请重试！");
            } else {
                // 获取的is_delete不是 1 那就是被逻辑删除了
                // 为了不影响新增分类 这里做物理删除
                this.delById(category.getId());
            }
        }
        Category entity = new Category();
        // 拷贝数据
        BeanUtils.copyProperties(dto, entity);
        // 其余字段自动插入
        this.save(entity);

        // 3. 数据封装
        return R.success("添加分类成功！");
    }
    @Override
    @Transactional // 多表操作开启事务
    public void delById(Long id) {

        //  1. 参数校验
        if (Objects.isNull(id)) {
            R.error("参数非法！");
            return;
        }

        // 2. 核心业务逻辑
        // 2.1 如果当前分类关联了  菜品表数据  不允许删除  条件 cattegory——id
        LambdaQueryWrapper<Dish> wrapper1 = Wrappers.lambdaQuery();
        wrapper1.eq(Dish::getCategoryId, id);
        // SELECT COUNT(id) FROM dish WHERE category_id=1397844263642378242
        Integer count1 = dishMapper.selectCount(wrapper1);
        if (count1 > 0) {
            // return R.error("当前分类关联菜品不能删除！");
            throw new MyException("当前分类下关联了菜品，不能删除");
        }
        // 2.2 如果当前分类关联了  套餐表的数据  不允许删除
        LambdaQueryWrapper<SetMeal> wrapper2 = Wrappers.lambdaQuery();
        wrapper2.eq(SetMeal::getCategoryId, id);
        Integer count2 = setmealMapper.selectCount(wrapper2);
        if (count2 > 0) {
            // return R.error("当前分类关联套餐不能删除！");
            throw new MyException("当前分类下关联了套餐，不能删除");
        }
        // 2.3 删除分类数据
        // 物理删除
        this.removeById(id);  // 操作分类表

        // 3. 数据封装
        R.success("删除分类成功！");
    }
    @Override
    @Transactional // 多表操作开启事务
    public R<String> updateById(Long id) {

        //  1.参数校验
        if (Objects.isNull(id)) {
            return R.error("参数非法！");
        }

        // 2. 核心业务逻辑
        // 2.1 如果当前分类关联了  菜品表数据  不允许删除  条件 cattegory——id
        LambdaQueryWrapper<Dish> wrapper1 = Wrappers.lambdaQuery();
        wrapper1.eq(Dish::getCategoryId, id);
        // SELECT COUNT(id) FROM dish WHERE category_id=1397844263642378242
        Integer count1 = dishMapper.selectCount(wrapper1);
        if (count1 > 0) {
            throw new MyException("当前分类下关联了菜品，不能删除");
        }
        // 2.2 如果当前分类关联了  套餐表的数据  不允许删除
        LambdaQueryWrapper<SetMeal> wrapper2 = Wrappers.lambdaQuery();
        wrapper2.eq(SetMeal::getCategoryId, id);
        Integer count2 = setmealMapper.selectCount(wrapper2);
        if (count2 > 0) {
            throw new MyException("当前分类下关联了套餐，不能删除");
        }
        // 2.3 删除分类数据
        // 逻辑删除（其本身就是修改 需要在查询中加上判断字段）
        Category category = new Category();
        category.setId(id);

        // 第一种方式 使用原数据库字段修改type
        // category.setType(0);
        // 第二种方式 使用沉余字段数据库字段修改 新增字段-isDelete
        // 在数据库中新增字段is_delete 在实体类中新增字段 isDelete = 1 默认为 1
        category.setIsDelete(1);
        this.saveOrUpdate(category);

        // 3. 数据封装
        return R.success("删除分类成功！");
    }
    @Override
    public R<Page<Category>> findByPage(Long page, Long pageSize) {

        // 1.参数校验
        if (Objects.isNull(page)) {
            return R.error("参数非法！");
        }
        if (Objects.isNull(pageSize)) {
            return R.error("参数非法！");
        }
        if (page <= 0) {
            page = 1L;
        }
        if (pageSize <= 0) {
            pageSize = 10L;
        }
        // 2 .业务处理 参考员工分页处理 获取分页插件
        IPage<Category> pageQuery = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = Wrappers.lambdaQuery();
        // 投影查询 ，指定查询列 ： select  name，type...
        wrapper.select(
                Category::getName,
                Category::getType,
                Category::getUpdateTime,
                Category::getSort,
                Category::getId
        ).ne(Category::getIsDelete, 1);

        // 排序 升序
        // wrapper.orderByAsc(Category::getSort);
        // 排序 降序
        wrapper.orderByDesc(Category::getUpdateTime);
        // 进行数据分页处理
        Page<Category> result = (Page<Category>) this.page(pageQuery, wrapper);

        // 3. 数据封装返回
        return R.success(result);
        // 解决时间格式问题 LocalDateTime
    }
    @Override
    public R<String> updateCategory(CategoryUpdateDTO dto) {

        // 1. 参数校验
        if (Objects.isNull(dto.getId())) {
            return R.error("参数非法！");
        }
        if (StringUtils.isBlank(dto.getName())) {
            return R.error("分类名称不能为空！");
        }
        if (Objects.isNull(dto.getId())) {
            return R.error("参数非法！");
        }
        if (dto.getSort() <= 0) {
            dto.setSort(10);
        }

        // 2. 处理核心逻辑
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        this.updateById(category);

        // 3. 数据封装
        return R.success("更新成功！");
    }

    @Override
    public R getCategoryByType(Integer type) {
        // 1.校验参数
        if (ObjectUtils.isEmpty(type)) {
            return R.error("非法参数!");
        }
        // 2.业务逻辑
        LambdaQueryWrapper<Category> wrapper = Wrappers.lambdaQuery();
        wrapper.select(
                Category::getId,
                Category::getName,
                Category::getType,
                Category::getSort,
                Category::getUpdateTime
        );
        wrapper.eq(Category::getType,type);
        // 按照sort排序字段进行升序排序，如果sort相同，按照修改时间倒序排序
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        // 3.封装返回数据
        List<Category> list = this.list(wrapper);
        return R.success(list);
    }



}
