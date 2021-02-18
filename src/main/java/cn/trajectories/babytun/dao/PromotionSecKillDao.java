package cn.trajectories.babytun.dao;

import cn.trajectories.babytun.entity.PromotionSecKill;

import java.util.List;
import java.util.Map;

public interface PromotionSecKillDao {
    List<PromotionSecKill> findUnstartSecKill();

    void update(PromotionSecKill promotionSecKill);

    void updateCountByGoodsId(long goodsId);

    long selectCountByGoodsId(long goodsId);

    void reduceStockByGoodsId(Map<String, Long> data);
}
