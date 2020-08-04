package com.wzh.miaosha.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName(value = "order_info")
public class OrderInfo {

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    Long id;

    @TableField(value = "order_price")
    private Integer price;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "create_date")
    private Date createDate;

    @TableField(value = "pay_date")
    private Date payDate;

}
