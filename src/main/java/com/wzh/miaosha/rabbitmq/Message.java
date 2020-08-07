package com.wzh.miaosha.rabbitmq;

import com.wzh.miaosha.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message {

    private User user;
    private Long goodsId;


}
