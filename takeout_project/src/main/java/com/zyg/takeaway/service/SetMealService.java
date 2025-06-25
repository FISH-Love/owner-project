package com.zyg.takeaway.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.setmeal.SetMealAddDTO;
import com.zyg.takeaway.common.dto.setmeal.SetMealGetResultDTO;
import com.zyg.takeaway.common.dto.setmeal.SetMealResultDTO;
import com.zyg.takeaway.entity.SetMeal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 套餐业务层接口
 */
@Service
public interface SetMealService extends IService<SetMeal> {
    /**
     * 新增套餐
     * @param dto 新增数据
     * @return 新增结果
     */
    R<String> addSetMeal(SetMealAddDTO dto);
    R<Page<SetMealResultDTO>> findSetMealByPage(Long page, Long pageSize, String name);
    R<String> deleteList(List<Long> ids);
    R<String> logicDelete(List<Long> ids);
    R<String> switchStatus(Integer status, List<Long> ids);

    @Transactional
    R<String> switchSetMeal(Integer status, List<Long> ids);
    R<SetMealGetResultDTO> findById(Long id);
    R<String> updateSetMeal(SetMealGetResultDTO dto);
}