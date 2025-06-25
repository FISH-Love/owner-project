package com.zyg.takeaway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.zyg.takeaway.entity.Employee;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    /**
     * 分类持久层接口
     */
    }