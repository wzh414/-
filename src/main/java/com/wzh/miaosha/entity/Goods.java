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
@TableName(value = "goods")
public class Goods {

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    Long id;

    @TableField(value = "stock")
    Integer stock;

    @TableField(value = "price")
    Integer price;

    @TableField(value = "version")
    private Integer version;

    @TableField(value = "start_date")
    private Date startTime;

    @TableField(value = "end_date")
    private Date endTime;

}
