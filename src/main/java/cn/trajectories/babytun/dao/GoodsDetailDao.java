package cn.trajectories.babytun.dao;

import cn.trajectories.babytun.entity.GoodsCover;
import cn.trajectories.babytun.entity.GoodsDetail;

import java.util.List;

public interface GoodsDetailDao {
    public List<GoodsDetail> findByGoodsId(Long goodsId);
}
