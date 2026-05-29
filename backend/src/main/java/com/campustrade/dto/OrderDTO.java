package com.campustrade.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDTO {
    @NotNull(message = "商品ID不能为空")
    private Long goodsId;
    private String remark;
}
