package com.zyg.takeaway.common.dto.dish;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品分页响应实体
 */
@Data
public class DishResultDTO {
    // 主键ID
    // @JsonSerialize(using = TokenBufferSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    // 菜品名称
    private String name;
    // 菜品分类名称
    // private String CategoryName;
    private String categoryName;
    // 菜品价格
    private BigDecimal price;
    // 图片
    private String image;
    // 0 停售 1 起售
    private Integer status;

    // @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
