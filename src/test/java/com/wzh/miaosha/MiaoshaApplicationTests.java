package com.wzh.miaosha;

import com.wzh.miaosha.Service.GoodsService;
import com.wzh.miaosha.entity.Goods;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class MiaoshaApplicationTests {

    @Autowired
    private GoodsService goodsService;

    @Test
    void contextLoads() {
        Date date = new Date();
        Date endDate = new Date(date.getTime() + 60000);
        Goods goods = Goods.builder().price(30).stock(10).startTime(date).endTime(endDate).version(0).build();
        goodsService.insertAGoods(goods);
    }

    @Test
    void test(){

    }

}
