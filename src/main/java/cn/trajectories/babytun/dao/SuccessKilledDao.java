package cn.trajectories.babytun.dao;

import cn.trajectories.babytun.entity.SuccessKilled;

public interface SuccessKilledDao {
    void deleteByGoodsId(long goodsId);

    void creatOrder(SuccessKilled killed);

    long getKilledCountByGoodsId(long goodsId);
}
