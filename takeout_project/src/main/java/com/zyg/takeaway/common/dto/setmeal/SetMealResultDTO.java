package com.zyg.takeaway.common.dto.setmeal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 分页查询响应实体类
 */
@Data
public class SetMealResultDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    // 分类名称
    private String categoryName;
    // 套餐名称
    private String name;
    // 套餐价格
    private BigDecimal price;
    // 状态 0:停用 1:启用
    private Integer status;
    // 图片
    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}