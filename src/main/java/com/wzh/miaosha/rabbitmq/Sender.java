package com.wzh.miaosha.rabbitmq;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Sender {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendOrderMessage(Message message){
        String mqMessage = JSON.toJSONString(message);
        rabbitTemplate.convertAndSend("order","user.order",mqMessage);
    }
}
