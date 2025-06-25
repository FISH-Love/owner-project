package com.zyg.takeaway.common.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 下单实体类
 */
@Data
public class PlaceOrderDTO {
    // 备注
    private String remark;
    // 地址id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long addressBookId;
    // 支付方式 1微信,2支付宝 默认 1
    private Integer payMethod;
}
