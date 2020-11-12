package cn.trajectories.babytun.dao;

import cn.trajectories.babytun.entity.GoodsCover;

import java.util.List;

public interface GoodsCoverDao {
    public List<GoodsCover> findByGoodsId(Long goodsId);
}
