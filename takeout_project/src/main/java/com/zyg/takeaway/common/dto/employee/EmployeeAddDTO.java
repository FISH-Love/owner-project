package com.zyg.takeaway.common.dto.employee;

import lombok.Data;

/**
 * 添加员工实体类
 */
@Data
public class EmployeeAddDTO {
    private String name;
    private String username;
    private String phone;
    private String idNumber;
    private String sex;
}