package com.zyg.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.setmeal.SetMealAddDTO;
import com.zyg.takeaway.common.dto.setmeal.SetMealDishDTO;
import com.zyg.takeaway.common.dto.setmeal.SetMealGetResultDTO;
import com.zyg.takeaway.common.dto.setmeal.SetMealResultDTO;
import com.zyg.takeaway.entity.Category;
import com.zyg.takeaway.entity.SetMeal;
import com.zyg.takeaway.entity.SetMealDish;
import com.zyg.takeaway.mapper.SetMealMapper;
import com.zyg.takeaway.service.CategoryService;
import com.zyg.takeaway.service.SetMealDishService;
import com.zyg.takeaway.service.SetMealService;
import com.zyg.takeaway.util.QiNiuUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 套餐业务层接口
 */
@Slf4j
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService; // 确保添加了这一行
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetMealMapper setMealMapper;

    @Override
    @Transactional
    public R<String> addSetMeal(SetMealAddDTO dto) {
        // 1.参数校验
        if (ObjectUtils.isEmpty(dto.getName())) {
            return R.error("参数非法！");
        }
        if (ObjectUtils.isEmpty(dto.getPrice())) {
            return R.error("参数非法！");
        }
        // 判断套餐名称是否可以重复
        // LambdaQueryWrapper<Setmeal> wrapper1 = Wrappers.lambdaQuery();
        // wrapper1.eq(Setmeal::getName,dto.getName());
        // int count = this.count(wrapper1);
        int count = this.count(Wrappers.<SetMeal>lambdaQuery().eq(SetMeal::getName, dto.getName()));
        if (count > 0) {
            return R.error("套餐名称不可重复！");
        }
        // 2.业务处理
        // 2.1 保存套餐数据
        SetMeal setmeal = new SetMeal();
        BeanUtils.copyProperties(dto, setmeal);
        // 直接保存套餐数据
        this.save(setmeal);
        // 2.2 保存套餐中的菜品数据 -> 集合
        // 获取套餐中的菜品集合
        List<SetMealDishDTO> setMealDishes = dto.getSetMealDishes();
        // 创建套餐菜品中间表集合 用来存储数据
        ArrayList<SetMealDish> setMealDishArrayList = new ArrayList<>();
        // 拿到前端传递的每一条菜品数据
        for (SetMealDishDTO setMealDishDTO : setMealDishes) {
            // 补全套餐菜品中间表setMeal_dish的字段进行新增
            SetMealDish setMealDish = new SetMealDish();
            // 开始补全字段
            BeanUtils.copyProperties(setMealDishDTO, setMealDish);
            // 补全套餐ID
            setMealDish.setSetMealId(setmeal.getId());
            // 需要处理补全后的数据
            setMealDishArrayList.add(setMealDish);
        }
        // 开始新增套餐中的菜品数据 -> 套餐菜品中间表

        setMealDishService.saveBatch(setMealDishArrayList);
        // 3.返回结果
        return R.success("添加套餐成功！");
    }
    @Override
    public R<Page<SetMealResultDTO>> findSetMealByPage(Long page, Long pageSize, String name) {
        // 1.参数校验
        if (ObjectUtils.isEmpty(page)) {
            return R.error("参数非法！");
        }
        if (ObjectUtils.isEmpty(pageSize)) {
            return R.error("参数非法！");
        }
        if (page <= 0) {
            page = 1L;
        }
        if (pageSize <= 0) {
            pageSize = 10L;
        }
        // 2.业务处理
        // 2.1 套餐数据
        LambdaQueryWrapper<SetMeal> wrapper = Wrappers.lambdaQuery();
        // 判断name值是否为空 是空不做处理
        wrapper.like(!ObjectUtils.isEmpty(name), SetMeal::getName, name);
        // 根据修改时间进行排序
        wrapper.orderByDesc(SetMeal::getUpdateTime);
        // 开始第一次分页
        Page<SetMeal> setMealPage = new Page<>();
        Page<SetMeal> result = this.page(setMealPage, wrapper);
        // 缺少套餐分类名称需要根据套餐中的分类ID获取分类名称
        List<SetMeal> records = result.getRecords(); // 获取的是当前分页的套餐数据 10条数据
        // 创建前端需要的响应集合
        List<SetMealResultDTO> resultDTOS = new ArrayList<>();
        // 业务处理
        for (SetMeal record : records) {
            // 获取分页后当前页的套餐中的分类ID
            Long categoryId = record.getCategoryId();
            // 根据分页后的套餐中的分类ID获取分类数据
            Category category = categoryService.getById(categoryId);
            // 在获取分类名称
            String categoryName = category.getName();
            // 开始做响应数据
            SetMealResultDTO setMealResultDTO = new SetMealResultDTO();
            // 开始拷贝数据 套餐
            BeanUtils.copyProperties(record, setMealResultDTO);
            // 分类名称
            setMealResultDTO.setCategoryName(categoryName);
            // 前端想要的参数类型为分页实体集合
            resultDTOS.add(setMealResultDTO);
        }
        // 再次分页 第一次套餐分页只是为了获取套餐分页后的分类名称
        Page<SetMealResultDTO> resultDTOPage = new Page<>();
        // 获取最后的数据
        BeanUtils.copyProperties(result, resultDTOPage);
        // 更新菜品分页的分类数据
        resultDTOPage.setRecords(resultDTOS);
        // 3.数据封装
        return R.success(resultDTOPage);
    }
    @Override
    public R<String> deleteList(List<Long> ids) {
        // 1.参数校验
        if (ObjectUtils.isEmpty(ids)) {
            return R.error("参数异常！");
        }
        // 2.业务处理
        // 2.1 创建可删除集合
        ArrayList<Long> deleteListId = new ArrayList<>();
        // 获取可删除图片名称集合
        ArrayList<String> deleteListImg = new ArrayList<>();
        // 遍历获取到的需要删除的集合id
        for (Long id : ids) {
            // 获取套餐所有数据
            SetMeal dish = this.getById(id);
            // 获取套餐状态
            Integer status = dish.getStatus();
            // 获取套餐图片名称
            String image = dish.getImage();
            // 判断状态是否在售
            if (status == 0) {
                // 停售
                deleteListId.add(id); // 可删除套餐id集合
                deleteListImg.add(image); // 可删除套餐在七牛云中的图片集合
                // QiNiuUtil.deleteImg(image); // 单个删除七牛云中的文件
            }
        }
        // 批量删除套餐信息
        this.removeByIds(deleteListId);
        // 批量删除七牛云图片
        QiNiuUtil.deletesImg(deleteListImg);
        // 2.2 删除关联套餐菜品中间表 套餐菜品中间表中有关联字段为 set_meal_id - 套餐ID
        for (Long setMealId : deleteListId) {
            LambdaQueryWrapper<SetMealDish> wrapper1 = Wrappers.lambdaQuery();
            wrapper1.eq(SetMealDish::getSetMealId, setMealId);
            setMealDishService.remove(wrapper1);
        }
        // 3.返回结果 - 静态响应
        if (deleteListId.isEmpty()) {
            return R.error("启售套餐无法删除！");
        }
        return R.success("已删除停售套餐！");
    }
    @Override
    @Transactional
    public R<String> logicDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.error("删除ID不能为空");
        }

        // 构造更新条件
        LambdaUpdateWrapper<SetMeal> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.in(SetMeal::getId, ids)
//                .set(SetMeal::getIsDeleted, 1)  // 这个字段没有
                .set(SetMeal::getUpdateTime, LocalDateTime.now());  // 更新时间

        // 执行更新
        boolean result = this.update(updateWrapper);

        return result ? R.success("逻辑删除成功") : R.error("逻辑删除失败");
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
        // 先判断后修改 可以创建一个可以修改的集合
        ArrayList<Long> upList = new ArrayList<>(); // 可以直接修改的ID集合
        // 2.2 获取可修改和不修改的ID 遍历集合获取前端传递的每一个ID
        for (Long setMealId : ids) {
            // 根据套餐ID获取套餐所有数据
            SetMeal setMeal = this.getById(setMealId);
            // 把获取到数据库中的状态和发起的请求做比较
            if (!status.equals(setMeal.getStatus())) {
                // 把需要修改的id添加到upList中及可
                upList.add(setMealId);
            }
        }
        // 2.3 开始修改 遍历可修改的集合进行修改
        for (Long upDishId : upList) {
            // 先获取套餐实体类
            SetMeal setMeal = new SetMeal();
            // 开始赋值
            setMeal.setId(upDishId);
            setMeal.setStatus(status);
            // 在修改时需要根据ID修改
            this.saveOrUpdate(setMeal);
        }
        // 3.数据返回 静态响应
        if (upList.isEmpty()) {
            return R.error("套餐已是" + (status == 1 ? "起售状态" : "停售状态") + ",请勿重复操作！！");
        }
        return R.success("修改成功！！！");
    }
    @Transactional
    @Override
    public R<String> switchSetMeal(Integer status, List<Long> ids) {
        // 1.参数校验
        if (ObjectUtils.isEmpty(status)) {
            return R.error("目标有误！");
        }
        if (!(status == 0 || status == 1)) {
            return R.error("目标有误！");
        }
        // 2.核心业务
        // 先判断后修改 可以创建一个可以修改的集合
        ArrayList<Long> upList = new ArrayList<>(); // 可以直接修改的ID集合
        // 2.2 获取可修改和不修改的ID 遍历集合获取前端传递的每一个ID
        for (Long setMealId : ids) {
            // 根据套餐ID获取套餐所有数据
            // SetMeal setMeal = this.getById(setMealId);
            SetMeal setMeal = setMealMapper.getById(setMealId);
            // 把获取到数据库中的状态和发起的请求做比较
            if (!status.equals(setMeal.getStatus())) {
                // 把需要修改的id添加到upList中及可
                upList.add(setMealId);
            }
        }
        // 把集合转换为数组
        Long[] array = ids.toArray(new Long[0]);
        int count = setMealMapper.switchSetMeal(status, ids);
        if (ObjectUtils.isEmpty(count)) {
            return R.error("修改失败！");
        }
        // 3.数据返回 静态响应
        if (upList.isEmpty()) {
            return R.error("套餐已是" + (status == 1 ? "起售状态" : "停售状态") + ",请勿重复操作！！");
        }
        return R.success("修改成功！！！");
    }
    @Override
    public R<SetMealGetResultDTO> findById(Long id) {
        // 1.参数校验
        if (ObjectUtils.isEmpty(id)) {
            return R.error("非法参数！");
        }
        // 2.核心业务 数据返回-套餐信息、套餐菜品中间表
        // 2.1套餐信息
        // 根据套餐id获取所有套餐数据
        SetMeal setMeal = this.getById(id);
        // 先返回套餐数据
        SetMealGetResultDTO setMealGetResultDTO = new SetMealGetResultDTO();
        // 把套餐数据拷贝到返回的实体上
        BeanUtils.copyProperties(setMeal, setMealGetResultDTO);
        // 2.套餐菜品中间表 - 根据套餐ID获取套餐菜品中间表的数据
        LambdaQueryWrapper<SetMealDish> wrapper = Wrappers.lambdaQuery();
        // SELECT * FROM `set_meal_dish` WHERE set_meal_id = 1397849739276890114
        wrapper.eq(SetMealDish::getSetMealId, id);
        // 套餐中有多条菜品 接收需要用集合
        List<SetMealDish> setMealDishes = setMealDishService.list(wrapper);
        // 先创建菜品数据回显集合 把获取到数据库中的数据拷贝到这里
        List<SetMealDishDTO> setMealDishDTOS = new ArrayList<>();
        // 在套餐中回显菜品参数 先判断是否有菜品
        if (!ObjectUtils.isEmpty(setMealDishes) || !setMealDishes.isEmpty()) {
            // 没有菜品不做操作 取反有菜品
            for (SetMealDish setMealDish : setMealDishes) {
                // setMealDish表示获取的是套餐中的一条菜品数据
                SetMealDishDTO setMealDishDTO = new SetMealDishDTO();
                // 拷贝一条菜品数据到返回的菜品DTO数据实体类中
                BeanUtils.copyProperties(setMealDish, setMealDishDTO);
                // 把拷贝好的一条菜品口数据添加到返回的菜品数据实体类中
                setMealDishDTOS.add(setMealDishDTO);
            }
        }
        // 取反 没有菜品不做处理
        // 把菜品数据跟新到响应实体中
        setMealGetResultDTO.setSetMealDishes(setMealDishDTOS);
        // 3.返回参数
        return R.success(setMealGetResultDTO);
    }
    @Override
    public R<String> updateSetMeal(SetMealGetResultDTO dto) {
        // 1.参数校验
        if (ObjectUtils.isEmpty(dto.getName())) {
            return R.error("菜品名称不能为空！");
        }
        if (ObjectUtils.isEmpty(dto.getId())) {
            return R.error("参数有误，请重试！");
        }
        if (ObjectUtils.isEmpty(dto.getCategoryId())) {
            return R.error("请选择套餐分类！");
        }
        if (ObjectUtils.isEmpty(dto.getImage())) {
            return R.error("套餐图片不能为空！");
        }
        if (ObjectUtils.isEmpty(dto.getPrice())) {
            return R.error("套餐价格不能为空！");
        }
        int count = this.count(Wrappers.<SetMeal>lambdaQuery().eq(SetMeal::getName, dto.getName()));
        if (count > 0) {
            return R.error("套餐名称不可重复");
        }
        // 2.业务处理
        // 直接修改套餐
        SetMeal setMeal = new SetMeal();
        BeanUtils.copyProperties(dto, setMeal);
        this.updateById(setMeal);
        // 处理套餐中的菜品数据
        // 要先删除菜品信息 - 中间表中的套餐ID删除中间表的多条数据
        LambdaQueryWrapper<SetMealDish> wrapper1 = Wrappers.lambdaQuery();
        wrapper1.eq(SetMealDish::getSetMealId, dto.getId());
        setMealDishService.remove(wrapper1);
        // 获取当前传递的菜品数据集合 批量添加
        ArrayList<SetMealDish> setMealDishesList = new ArrayList<>();
        for (SetMealDishDTO setMealDishDTO : dto.getSetMealDishes()) {
            SetMealDish setMealDish = new SetMealDish();
            BeanUtils.copyProperties(setMealDishDTO, setMealDish);
            setMealDish.setSetMealId(setMeal.getId());
            // 新增一条
            // setMealDishService.save(setMealDish);
            setMealDishesList.add(setMealDish);
        }
        // 添加新的数据到套餐菜品中间表中 - 新增多条
        setMealDishService.saveBatch(setMealDishesList);
        // 3.返回消息
        return R.success("套餐修改成功！");
    }


}