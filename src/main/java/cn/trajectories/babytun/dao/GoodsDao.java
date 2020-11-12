package cn.trajectories.babytun.dao;

import cn.trajectories.babytun.entity.Goods;

public interface GoodsDao {
    public Goods findById(long goodsId);
}
