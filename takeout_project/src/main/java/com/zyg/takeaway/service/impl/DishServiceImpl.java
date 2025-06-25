package com.zyg.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.dish.DishAddDTO;
import com.zyg.takeaway.common.dto.dish.DishFlavorDTO;
import com.zyg.takeaway.common.dto.dish.DishGetResultDTO;
import com.zyg.takeaway.common.dto.dish.DishResultDTO;
import com.zyg.takeaway.entity.Category;
import com.zyg.takeaway.entity.Dish;
import com.zyg.takeaway.entity.DishFlavor;
import com.zyg.takeaway.mapper.DishMapper;
import com.zyg.takeaway.service.CategoryService;
import com.zyg.takeaway.service.DishFlavorService;
import com.zyg.takeaway.service.DishService;
import com.zyg.takeaway.util.QiNiuUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜品业务层接口实现类
 */
@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    // 单一构造注入多个服务
private final DishFlavorService dishFlavorService;
    private final CategoryService categoryService;

    public DishServiceImpl(
            DishFlavorService dishFlavorService,
            CategoryService categoryService) {
        this.dishFlavorService = dishFlavorService;
        this.categoryService = categoryService;
    }

    @Override
    public R<DishGetResultDTO> findById(Long id) {
        // 1.参数校验
        if (ObjectUtils.isEmpty(id)) {
            return R.error("非法参数！");
        }
        // 2.核心业务 数据返回-菜品信息、菜品口味
        // 2.1菜品信息
        // 根据菜品id获取所有菜品
        Dish dish = this.getById(id);
        // 先返回菜品数据
        DishGetResultDTO resultDTO = new DishGetResultDTO();
        // 把菜品数据拷贝到返回的实体上
        BeanUtils.copyProperties(dish, resultDTO);
        // 2.2菜品口味 - 在菜品口味中包含字段 dish_id
        LambdaQueryWrapper<DishFlavor> wrapper = Wrappers.lambdaQuery();
        // SELECT * FROM `dish_flavor` WHERE dish_id = 1397849739276890114
        wrapper.eq(DishFlavor::getDishId, id);
        // 菜品有多条口味 接收需要用集合

        List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper);
        // 先创建口味数据回显集合 把获取到的口味数据拷贝到这里
        ArrayList<DishFlavorDTO> dishFlavorDTOS = new ArrayList<>();
        // 回显口味参数 先判断是否有口味
        if (!ObjectUtils.isEmpty(dishFlavorList) || dishFlavorList.isEmpty()) {
            // 没有菜品口味不做操作 取反有口味数据
            for (DishFlavor dishFlavor : dishFlavorList) {
                // dishFlavor表示获取的是菜品口味的一条参数
                DishFlavorDTO dishFlavorDTO = new DishFlavorDTO();
                // 拷贝一条菜品口味数据到返回的口味数据实体类中
                BeanUtils.copyProperties(dishFlavor, dishFlavorDTO);
                // 把拷贝好的一条菜品口味数据添加到返回的口味数据实体类中
                dishFlavorDTOS.add(dishFlavorDTO);
            }
        }
        // 取反 没有口味不做处理
        // 把口味数据跟新到响应实体中
        resultDTO.setFlavors(dishFlavorDTOS);
        // 3.返回参数
        return R.success(resultDTO);
    }
    @Override
    @Transactional
    public R addDish(DishAddDTO dto) {
        // 1.参数校验
        if (ObjectUtils.isEmpty(dto.getName()) || ObjectUtils.isEmpty(dto.getPrice())) {
            return R.error("参数非法！！！");
        }
        // 2.业务逻辑 添加两个表 菜品/菜品口味
        // 2.1 菜品
        LambdaQueryWrapper<Dish> wrapper1 = Wrappers.lambdaQuery();
        wrapper1.eq(Dish::getName, dto.getName());
        int count = this.count(wrapper1);
        if (count > 0) {
            return R.error("菜品名称已存在！");
        }
        // 保存菜品 拷贝
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);
        // 补全字段参数
        dish.setSort(0);
        // 保存菜品成功之后会给我们返回一个参数 dish主键id 主键返回
        this.save(dish);
        // 2.2 菜品口味
        // 2.2.1 获取的口味数据
        // 批量添加操作
        batchAddFlavor(dto.getFlavors(), dish);
        // 3.返回结果
        return R.success("添加菜品成功！");
    }
    /**
     * 批量添加菜品口味
     * @param flavorDTOS 口味DTO列表
     * @param dish 菜品实体（包含ID）
     */
    private void batchAddFlavor(List<DishFlavorDTO> flavorDTOS, Dish dish) {
        if (flavorDTOS == null || flavorDTOS.isEmpty()) {
            return; // 没有口味数据，直接返回
        }

        // 将DTO转换为实体列表，并设置菜品ID
        List<DishFlavor> flavorList = flavorDTOS.stream()
                .filter(flavor -> !ObjectUtils.isEmpty(flavor.getName())) // 过滤无效数据
                .map(flavorDTO -> {
                    DishFlavor flavor = new DishFlavor();
                    BeanUtils.copyProperties(flavorDTO, flavor);
                    flavor.setDishId(dish.getId()); // 设置菜品ID
                    return flavor;
                })
                .collect(Collectors.toList());

        // 使用MyBatis-Plus的批量插入方法
        dishFlavorService.saveBatch(flavorList);
    }
    @Override
    public R<Page<DishResultDTO>> findDishByPage(@Min(value = 1L) Long page, @Min(value = 1L) Long pageSize, String name) {
        // 1.参数校验 已使用注解完成 -> spring-boot-starter-validation
        // 2.业务处理
        // 2.1 菜品数据
        LambdaQueryWrapper<Dish> wrapper = Wrappers.lambdaQuery();
        // 判断name值是否为空
        // if (!ObjectUtils.isEmpty(name)) {
        //     // 不是空做菜品名称的模糊查询
        //     wrapper.like(Dish::getName, name);
        // }
        // 是空不做处理
        wrapper.like(!ObjectUtils.isEmpty(name), Dish::getName, name);
        // 根据修改时间进行排序
        wrapper.orderByDesc(Dish::getUpdateTime);
        // 开始分页-制定分页规则
        Page<Dish> dishPage = new Page<>(page, pageSize);
        // 分页规则 被分页数据
        Page<Dish> result = this.page(dishPage, wrapper);
        // 2.2 分类数据 缺少分类字段-菜品分类名称
        List<Dish> records = result.getRecords(); // 获取的是当前分页的菜品数据
        // 创建前端需要的响应集合
        List<DishResultDTO> resultDTOS = new ArrayList<>();
        // 业务处理
        for (Dish record : records) {
            // 获取分页后当前页的菜品中的分类ID
            Long categoryId = record.getCategoryId();
            // 根据分页后的菜品ID获取分类数据
            Category category = categoryService.getById(categoryId);
            // 在获取分类名称
            String categoryName = category.getName();
            // 开始做响应数据
            DishResultDTO dishResultDTO = new DishResultDTO();
            // 开始拷贝数据 菜品
            BeanUtils.copyProperties(record, dishResultDTO);
            // 分类名称
            dishResultDTO.setCategoryName(categoryName);
            // 前端想要的参数类型为分页实体集合
            resultDTOS.add(dishResultDTO);
        }
        // 再次分页 第一次菜品分页只是为了获取菜品分页后的分类名称
        Page<DishResultDTO> resultDTOPage = new Page<>();
        // 获取最后的数据
        BeanUtils.copyProperties(result, resultDTOPage);
        // 跟新菜品分页的分类数据
        resultDTOPage.setRecords(resultDTOS);
        // 3.数据封装
        return R.success(resultDTOPage);
    }
    @Override
    @Transactional
    public R<String> updateDish(DishGetResultDTO dto) {
        // 1.参数校验
        if (ObjectUtils.isEmpty(dto.getName())) {
            return R.error("菜品名称不能为空！");
        }
        if (ObjectUtils.isEmpty(dto.getId())) {
            return R.error("参数有误，请重试！");
        }
        if (ObjectUtils.isEmpty(dto.getCategoryId())) {
            return R.error("请选择菜品分类！");
        }
        if (ObjectUtils.isEmpty(dto.getImage())) {
            return R.error("菜品图片不能为空！");
        }
        if (dto.getStatus() != 0) {
            dto.setStatus(1);
        }
        if (dto.getSort() < 0) {
            dto.setSort(1);
        }
        // 没实现
//        if (isNameNull(dto.getName())) {
//            return R.error("菜品名称不可重复");
//        }
        // 2.业务处理
        // 直接修改菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);
        this.updateById(dish);
        // 处理口味数据
        // 要先删除菜品口味信息 dish_id-菜品中的id
        LambdaQueryWrapper<DishFlavor> wrapper1 = Wrappers.lambdaQuery();
        wrapper1.eq(DishFlavor::getDishId, dto.getId());
        dishFlavorService.remove(wrapper1);
        // 添加新的数据到菜品口味表中
        batchAddFlavor(dto.getFlavors(), dish);
        // 3.返回消息
        return R.success("菜品修改成功！");
    }
    @Override
    @Transactional
    public R<String> switchStatus(Integer status, List<Long> ids) {
        // 1.参数校验
        if (ObjectUtils.isEmpty(status)) {
            return R.error("目标有误！");
        }
        if (!(status == 0 || status == 1)) {
            return R.error("目标有误！");
        }
        // 2.核心业务
        // 2.1如何处理选中的ID中包含启售和停售两种状态的数据
        // 2.1.1 不用管直接修改 - 不建议
        // 2.1.2 先判断后修改 可以创建一个可以修改的集合
        ArrayList<Long> upList = new ArrayList<>(); // 可以直接修改的ID集合
        ArrayList<Long> unUpList = new ArrayList<>(); // 本身就是这个状态的数据集合 不做处理
        // 2.2 获取可修改和不修改的ID 遍历集合获取前端传递的每一个ID
        for (Long dishId : ids) {
            // 根据菜品ID获取菜品所有数据
            Dish dish = this.getById(dishId);
            // 把获取到数据库中的状态和发起的请求做比较
            if (status.equals(dish.getStatus())) {
                // 如果发起请求中的状态与数据库中的保持一致 就没必要去修改
                unUpList.add(dishId);
            } else {
                // 把需要修改的id添加到upList中及可
                upList.add(dishId);
            }
        }
        // 2.3 开始修改 遍历可修改的集合进行修改
        for (Long upDishId : upList) {
            // 先获取菜品实体类
            Dish dish = new Dish();
            // 开始赋值
            dish.setId(upDishId);
            dish.setStatus(status);
            // 在修改时需要根据ID修改
            this.saveOrUpdate(dish);
        }
        // 3.数据返回
        // 做动态数据响应
        R<Map<String, Object>> r = new R<>();
        r.getMap().put("菜品状态被修改的ID有：", upList);
        r.getMap().put("菜品状态无需修改的ID有：", unUpList);
        // 静态响应
        if (upList.isEmpty()) {
            // if (status == 1){
            //     return  R.error("菜品已是起售状态,请勿重复操作！！");
            // }
            // return  R.error("菜品已是停售状态,请勿重复操作！！");
            return R.error("菜品已是" + (status == 1 ? "起售状态" : "停售状态") + ",请勿重复操作！！");
        }
        return R.success("修改成功！！！");
    }
    @Override
    @Transactional
    public R<String> deleteList(List<Long> ids) {
        // 1.参数校验
        if (ids.isEmpty()) {
            return R.error("非法参数！");
        }
        // 2.业务处理
        // 2.1 创建可删除集合
        ArrayList<Long> deleteListId = new ArrayList<>();
        // 获取可删除图片名称集合
        ArrayList<String> deleteListImg = new ArrayList<>();
        // 遍历获取到的需要删除的集合id
        for (Long id : ids) {
            // 获取菜品所有数据
            Dish dish = this.getById(id);
            // 获取菜品状态
            Integer status = dish.getStatus();
            // 获取菜品图片名称
            String image = dish.getImage();
            // 判断状态是否在售
            if (status == 0) {
                // 停售
                deleteListId.add(id); // 可删除菜品id集合
                deleteListImg.add(image); // 可删除菜品在七牛云中的图片集合
                // QiNiuUtil.deleteImg(image); // 单个删除七牛云中的文件
            }
        }
        // 批量删除菜品信息
        this.removeByIds(deleteListId);
        // 批量删除七牛云图片
        QiNiuUtil.deletesImg(deleteListImg);
        // 2.2删除关联口味数据 菜品口味表中有关联字段为 dish_id - 菜品ID
        for (Long dishId : deleteListId) {
            LambdaQueryWrapper<DishFlavor> wrapper1 = Wrappers.lambdaQuery();
            wrapper1.eq(DishFlavor::getDishId, dishId);
            dishFlavorService.remove(wrapper1);
        }
        // 3.返回结果 - 静态响应
        if (deleteListId.isEmpty()) {
            return R.error("启售菜品无法删除！");
        }
        return R.success("已删除停售菜品！");
    }
    @Override
    public R<List<Category>> getCategoryByType(Integer type) {
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
        List<Category> list = categoryService.list(wrapper);
        return R.success(list);
    }




}