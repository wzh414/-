package com.wzh.miaosha.rabbitmq;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wzh.miaosha.Service.GoodsService;
import com.wzh.miaosha.Service.OrderService;
import com.wzh.miaosha.entity.Goods;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;



    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "order", type = "topic"),
                    key = {"user.order"}  //指定接收符合通配符的key
            )
    })
    void receive(String message) {
        System.out.println("rabbitmq接收："+message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        Message mqMessage = JSON.toJavaObject(jsonObject,Message.class);


        Goods goods = goodsService.selectGoodsById(mqMessage.getGoodsId());
        if (goods.getStock() <=0)return;

        if (orderService.selectOrder(mqMessage.getUser().getId(),mqMessage.getGoodsId())) return;

        orderService.createAOrder(mqMessage.getGoodsId(),mqMessage.getUser().getId());


    }

}
