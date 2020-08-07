package com.wzh.miaosha.Service;

import com.wzh.miaosha.Lock.RedisLock;
import com.wzh.miaosha.Utils.MD5Util;
import com.wzh.miaosha.dao.GoodsDao;
import com.wzh.miaosha.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    RedisLock redisLock;

    @Autowired
    UserService userService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 超时时间3s
     */
    private static final int TIMEOUT = 3*1000;

    public void insertAGoods(Goods goods){
        goodsDao.insert(goods);
    }

    public List<Goods> searchAllGoods(){
        return goodsDao.selectList(null);
    }

    public Goods selectGoodsById(Long id){
        return goodsDao.selectById(id);
    }


    /**
     * 数据库sql乐观锁（版本号控制）
     * @param id
     * @return
     */
    public boolean update(Long id){

        System.out.println(id);

        Goods goods = goodsDao.selectById(id);
        if (goods == null){
            return false;
        }
        if(goods.getStock() <=0){
            stringRedisTemplate.opsForValue().set(goods.getId().toString(),goods.getStock().toString(),1, TimeUnit.DAYS);
            return false;
        }
        int update = goodsDao.updateByIdAndVersion(goods);
        if (update<=0)return false;

        return true;
    }


    /**
     * redis分布式锁
     * @param id
     * @return
     */
    public int update2(Long id){
        long time = System.currentTimeMillis() + TIMEOUT;
        boolean lock = redisLock.lock(id.toString(), String.valueOf(time));
        if(!lock){
            //获取锁失败
            return -2;
        }
        System.out.println("获取锁成功");
        Goods goods = goodsDao.selectById(id);
        if(goods.getStock() <= goods.getVersion()){
            redisLock.release(id.toString(), String.valueOf(time));
            System.out.println("不足释放锁");
            return -2;
        }
        int update = goodsDao.updateStock(goods);

        redisLock.release(id.toString(), String.valueOf(time));
        System.out.println("释放锁");
        return update;
    }

    public String getSpikePath(String id){
        String path = MD5Util.getMD5(id);
        stringRedisTemplate.opsForValue().set("path_"+id,path,3,TimeUnit.MINUTES);
        return path;
    }

}
