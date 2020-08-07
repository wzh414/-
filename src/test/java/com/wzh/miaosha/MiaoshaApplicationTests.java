package com.wzh.miaosha;

import com.wzh.miaosha.Service.GoodsService;
import com.wzh.miaosha.Service.UserService;
import com.wzh.miaosha.Utils.MD5Util;
import com.wzh.miaosha.dao.OrderInfoDao;
import com.wzh.miaosha.entity.Goods;
import com.wzh.miaosha.entity.OrderInfo;
import com.wzh.miaosha.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Date;
import java.util.List;

@SpringBootTest
class MiaoshaApplicationTests {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    UserService userService;

    @Autowired
    OrderInfoDao orderInfoDao;



    @Test
    void contextLoads() {
        Date date = new Date();
        Date endDate = new Date(date.getTime() + 60000);
        Goods goods = Goods.builder().price(30).stock(10).startTime(date).endTime(endDate).version(0).build();
        goodsService.insertAGoods(goods);
    }

    @Test
    void test(){
        OrderInfo orderInfo = OrderInfo.builder().createDate(new Date()).price(30).status(0).build();
        orderInfoDao.insert(orderInfo);
        System.out.println(orderInfo.getId());
    }

    @Test
    void insertUser(){
        User user = User.builder().username("wzh").password("123456").build();
        userService.insertAUser(user);
    }


    @Test
    void test2(){
        String md5 = MD5Util.getMD5("1290995413846978561");
        System.out.println(md5);
    }
}
