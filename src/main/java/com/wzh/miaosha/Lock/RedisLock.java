package com.wzh.miaosha.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * redis分布式锁
 */

@Component
public class RedisLock {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 加锁
     * @param lockKey 加锁的Key
     * @param timeStamp 时间戳：当前时间+超时时间（long time = System.currentTimeMillis() + TIMEOUT;）
     * @return
     */
    public boolean lock(String lockKey,String timeStamp){
        //如果redis不存在lockKey，则加入这个key代表加锁成功
        if(stringRedisTemplate.opsForValue().setIfAbsent(lockKey, timeStamp)){
            return true;
        }

        //加锁失败
        // 判断锁是否超时，即防止原来的操作异常，没有运行解锁操作 ，防止死锁
        String currentLock = stringRedisTemplate.opsForValue().get(lockKey);//获取当前锁的超时时间
        // 如果锁过期，即currentLock不为空且小于当前时间
        if(!StringUtils.isEmpty(currentLock) && Long.parseLong(currentLock) < System.currentTimeMillis()){
            //如果lockKey对应的锁已经存在，获取上一次设置的时间戳之后并重置lockKey对应的锁的时间戳
            String preLock = stringRedisTemplate.opsForValue().getAndSet(lockKey, timeStamp);

            //注：
            //假设两个线程同时进来这里，因为key被占用了，而且锁过期了。
            //获取的值currentLock=A(get取的旧的值肯定是一样的),两个线程的timeStamp都是B,key都是K。
            //而这里面的getAndSet一次只会一个执行，也就是一个执行之后，上一个的timeStamp已经变成了B。
            //只有一个线程获取的上一个值会是A，另一个线程再拿到的值已经是B了。
            //所以不会出现同时的preLock.equals(currentLock)为true

            //代表获取锁成功
            if(!StringUtils.isEmpty(preLock) && preLock.equals(currentLock)){
                return true;
            }
        }

        return false;
    }

    /**
     * 释放锁
     * @param lockKey
     * @param timeStamp
     */
    public void release(String lockKey,String timeStamp){
        try {
            String currentValue = stringRedisTemplate.opsForValue().get(lockKey);
            if(!StringUtils.isEmpty(currentValue) && currentValue.equals(timeStamp) ){
                // 删除锁状态
                stringRedisTemplate.opsForValue().getOperations().delete(lockKey);
            }
        } catch (Exception e) {
            System.out.println("警报！警报！警报！解锁异常");
        }
    }
}
