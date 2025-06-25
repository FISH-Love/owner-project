package com.zyg.takeaway.common.dto.employee;

import lombok.Data;

/**
 * 员工更新（修改）实体
 */
@Data
public class EmployeeUpdateDTO {
    private Long id;
    private String name;
    private String username;
    private String phone;
    private String idNumber;
    private String sex;
    private Integer status;
}