package com.wzh.miaosha.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzh.miaosha.entity.Order;
import org.apache.ibatis.annotations.Select;

public interface OrderDao extends BaseMapper<Order> {

    @Select("select * from orders where user_id = #{userId} and goods_id = #{goodsId}")
    Order selectByUserIdAndGoodsId(Long userId,Long goodsId);

}
