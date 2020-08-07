package com.wzh.miaosha.Service;

import com.wzh.miaosha.dao.OrderDao;
import com.wzh.miaosha.dao.OrderInfoDao;
import com.wzh.miaosha.entity.Order;
import com.wzh.miaosha.entity.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderInfoDao orderInfoDao;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public boolean selectOrder(Long userId,Long goodsId){
        boolean exist = stringRedisTemplate.hasKey(userId+"_"+goodsId);
        if (exist)return true;
        Order order = orderDao.selectByUserIdAndGoodsId(userId,goodsId);
        if (order != null)return true;
        return false;
    }


    public void createAOrder(Long goodsId,Long userId){

        OrderInfo orderInfo = OrderInfo.builder().createDate(new Date()).price(30).status(0).build();
        orderInfoDao.insert(orderInfo);
        System.out.println(orderInfo.getId());
        Order order = Order.builder().orderId(orderInfo.getId()).goodsId(goodsId).userId(userId).build();
        orderDao.insert(order);
        stringRedisTemplate.opsForValue().set(userId+"_"+goodsId,"1");


    }


}
