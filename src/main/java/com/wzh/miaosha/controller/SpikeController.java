package com.wzh.miaosha.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.wzh.miaosha.Service.GoodsService;
import com.wzh.miaosha.Service.OrderService;
import com.wzh.miaosha.Service.UserService;
import com.wzh.miaosha.entity.Goods;
import com.wzh.miaosha.entity.User;
import com.wzh.miaosha.rabbitmq.Message;
import com.wzh.miaosha.rabbitmq.Sender;
import com.wzh.miaosha.result.CodeMsg;
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
public class SpikeController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    Sender sender;

    @Autowired
    OrderService orderService;

    //创建令牌桶，实现限流，控制每秒只放行100个请求
    private RateLimiter rateLimiter = RateLimiter.create(100);

    //将抢购商品存入内存中，防止redis访问量过大
    HashMap<Long,Boolean> judgeMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Goods> goodsList = goodsService.searchAllGoods();
        if (goodsList.size() == 0)return;
        for (Goods goods:goodsList){
            stringRedisTemplate.opsForValue().set(goods.getId().toString(),goods.getStock().toString(),1, TimeUnit.DAYS);
            judgeMap.put(goods.getId(),false);
        }
        System.out.println("初始化完毕");
    }

    @GetMapping("/spike/{id}/{userId}")
    public Result<Integer> spikeGoods(@PathVariable("id") Long id,@PathVariable("userId") Long userId){

        //设置令牌桶等待时间，如果在规定时间内未成功获取token，则返回失败
        if (!rateLimiter.tryAcquire(5,TimeUnit.SECONDS)){
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }

        //控制用户访问次数
        userService.saveUserCount(id.toString());
        if (userService.getUserCount(id.toString())){
            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }


        //内存标记，减少redis访问
        if(!judgeMap.containsKey(id) || judgeMap.get(id))return Result.error(CodeMsg.MIAO_SHA_OVER);

        //访问redis缓存
        String stock = stringRedisTemplate.opsForValue().get(id.toString());
        if (Integer.valueOf(stock) <=0){
            judgeMap.put(id,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

//        Integer update = goodsService.update2(id);

        User user = userService.searchById(userId.toString());
        Message message = Message.builder().user(user).goodsId(id).build();

        //判断是否已存在订单
        if (orderService.selectOrder(message.getUser().getId(),message.getGoodsId())){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        boolean update = goodsService.update(id);
        if (update){
            stringRedisTemplate.boundValueOps(id.toString()).increment(-1);
            sender.sendOrderMessage(message);
        }else {
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }

        return Result.success(0);
    }

    //将秒杀接口隐藏
    @GetMapping("/path/{userId}")
    String getSpikePath(@PathVariable("userId") Long userId){

        String spikePath = goodsService.getSpikePath(userId.toString());

        return spikePath;
    }



}
