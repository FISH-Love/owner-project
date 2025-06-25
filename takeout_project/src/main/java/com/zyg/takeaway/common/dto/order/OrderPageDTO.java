package com.zyg.takeaway.common.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zyg.takeaway.entity.OrderDetail;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单分页响应实体类
 */
@Data
public class OrderPageDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    // 主键ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    // 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
    private Integer status;
    // 下单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
    // 实收金额
    private BigDecimal amount;
    // 订单明细实体类集合
    private List<OrderDetail> orderDetails;
}
