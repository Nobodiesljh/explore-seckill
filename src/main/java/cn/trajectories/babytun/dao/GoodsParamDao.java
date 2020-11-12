package cn.trajectories.babytun.dao;

import cn.trajectories.babytun.entity.GoodsDetail;
import cn.trajectories.babytun.entity.GoodsParam;

import java.util.List;

public interface GoodsParamDao {
    public List<GoodsParam> findByGoodsId(Long goodsId);
}
