package com.zyg.takeaway.common.dto.addressBook;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增地址簿实现类
 */
@Data
public class AddressBookAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    //收货人
    private String consignee;
    //手机号
    private String phone;
    //性别 0 女 1 男
    private String sex;
    //详细地址
    private String detail;
    //标签
    private String label;
}
