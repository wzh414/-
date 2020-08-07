package com.wzh.miaosha.Service;

import com.wzh.miaosha.dao.UserDao;
import com.wzh.miaosha.entity.User;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private static final String count = "COUNT_";

    @Autowired
    UserDao userDao;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public int insertAUser(User user){
        return userDao.insert(user);
    }

    public User searchById(String id){
        return userDao.selectById(id);
    }

    public void saveUserCount(String id){
        String number = stringRedisTemplate.opsForValue().get(count + id);
        if (number == null){
            stringRedisTemplate.opsForValue().set(count+id,"1",180, TimeUnit.SECONDS);
        }else {
            int limit = Integer.parseInt(number) + 1;
            Long expire = stringRedisTemplate.getExpire(count + id, TimeUnit.SECONDS);
            stringRedisTemplate.opsForValue().set(count+id,String.valueOf(limit),expire, TimeUnit.SECONDS);
        }
    }

    public boolean getUserCount(String id){
        String number = stringRedisTemplate.opsForValue().get(count + id);
        if (number == null)return true;
        else {
            return Integer.parseInt(number) > 8;
        }
    }

}
