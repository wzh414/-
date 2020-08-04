package com.wzh.miaosha.controller;

import com.wzh.miaosha.Service.GoodsService;
import com.wzh.miaosha.entity.Goods;
import com.wzh.miaosha.result.Result;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * InitializingBean接口为bean提供了初始化方法的方式，它只包括afterPropertiesSet方法，凡是继承该接口的类，在初始化bean的时候都会执行该方法。
 */
@RestController
public class SpikeController {

    @Autowired
    GoodsService goodsService;

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    //将抢购商品存入内存中，防止redis访问量过大
//    HashMap<Long,Boolean> judgeMap = new HashMap<>();
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        List<Goods> goodsList = goodsService.searchAllGoods();
//        if (goodsList.size() == 0)return;
//        for (Goods goods:goodsList){
//            stringRedisTemplate.opsForValue().set(goods.getId().toString(),goods.getStock().toString(),1, TimeUnit.DAYS);
//            judgeMap.put(goods.getId(),false);
//        }
//    }

    @GetMapping("/spike/{id}")
    public Result<Integer> spikeGoods(@PathVariable("id") Long id){
//        Integer update = goodsService.update(id);
        Integer update = goodsService.update2(id);
        return Result.success(update);
    }

}
