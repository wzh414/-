package com.wzh.miaosha.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    Long id;

    String username;
    String password;

}
