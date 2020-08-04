package com.wzh.miaosha.Service;

import com.wzh.miaosha.Lock.RedisLock;
import com.wzh.miaosha.dao.GoodsDao;
import com.wzh.miaosha.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.List;

@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    RedisLock redisLock;

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


    /**
     * 数据库sql乐观锁（版本号控制）
     * @param id
     * @return
     */
    public int update(Long id){
        Goods goods = goodsDao.selectById(id);
//        if (goods == null){
//
//        }
        if(goods.getStock() <=0){
            return -2;
        }
        return goodsDao.updateByIdAndVersion(goods);
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
//        try{
//             //为了更好的测试多线程同时进行库存扣减，在进行数据更新之后先等1秒，让多个线程同时竞争资源
//             Thread.sleep(1000);
//        }catch (InterruptedException e){
//             e.printStackTrace();
//        }
        int update = goodsDao.updateStock(goods);

        redisLock.release(id.toString(), String.valueOf(time));
        System.out.println("释放锁");
        return update;
    }

}
