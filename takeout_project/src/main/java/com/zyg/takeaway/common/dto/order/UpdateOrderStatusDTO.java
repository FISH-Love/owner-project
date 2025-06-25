package com.zyg.takeaway.common.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 修改订单状态实体类
 */
@Data
public class UpdateOrderStatusDTO {
    // 主键ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    // 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
    private Integer status;
}
