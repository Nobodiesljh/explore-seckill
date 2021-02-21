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

    long selectCountByGoodsIdPcc(long goodsId);

    int reduceByGoodsId(long goodsId);

    int reduceStockByOcc(Map<String, Long> data);

    PromotionSecKill selectGoodByGoodsId(long goodsId);
}
