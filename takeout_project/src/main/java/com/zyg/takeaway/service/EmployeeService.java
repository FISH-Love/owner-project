package com.zyg.takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.employee.EmployeeAddDTO;
import com.zyg.takeaway.common.dto.employee.EmployeeLoginDTO;
import com.zyg.takeaway.common.dto.employee.EmployeeUpdateDTO;
import com.zyg.takeaway.entity.Employee;



public interface EmployeeService extends IService<Employee> {
    R<Employee> login(EmployeeLoginDTO dto);
    /**
     * 新增员工
     *
     * @param dto 新增参数
     * @return 新增结果
     */
    R<String> addEmployee(EmployeeAddDTO dto);
    R findByPage(Long page, Long pageSize, String name);
    R editStatus(EmployeeUpdateDTO dto);
    R getOneById(Long id);
    R updates(EmployeeUpdateDTO dto);

    }


