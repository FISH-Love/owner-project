package com.zyg.takeaway.common.dto.employee;

import lombok.Data;

/**
 * 员工登录实体类
 */
@Data
public class EmployeeLoginDTO {
    private String username;
    private String password;
}