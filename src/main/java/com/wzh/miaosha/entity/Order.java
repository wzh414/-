package com.wzh.miaosha.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName(value = "orders")
public class Order {

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "goods_id")
    private Long goodsId;

    @TableField(value = "order_id")
    private Long orderId;
}
