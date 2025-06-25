package com.zyg.takeaway.common.dto.user;

import lombok.Data;

/**
 * 获取用户手机号验证码
 */
@Data
public class UserGetCodeDTO {
    private String phone;
}
