package com.zyg.takeaway.common.dto.addressBook;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 设置默认收获地址实体类
 */
@Data
public class AddressBookDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
}
