package com.zyg.takeaway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyg.takeaway.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类持久层接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}