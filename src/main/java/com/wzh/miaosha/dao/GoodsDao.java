package com.wzh.miaosha.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzh.miaosha.entity.Goods;
import org.apache.ibatis.annotations.Update;

public interface GoodsDao extends BaseMapper<Goods> {

    @Update("update goods set stock = stock-1,version = version+1 where id = #{id} and version = #{version}")
    int updateByIdAndVersion(Goods goods);

    @Update("update goods set version = version+1 where id = #{id}")
    int updateStock(Goods goods);
}
