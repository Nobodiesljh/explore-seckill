package cn.trajectories.babytun.service;

import cn.trajectories.babytun.dao.GoodsCoverDao;
import cn.trajectories.babytun.dao.GoodsDao;
import cn.trajectories.babytun.dao.GoodsDetailDao;
import cn.trajectories.babytun.dao.GoodsParamDao;
import cn.trajectories.babytun.entity.Goods;
import cn.trajectories.babytun.entity.GoodsCover;
import cn.trajectories.babytun.entity.GoodsDetail;
import cn.trajectories.babytun.entity.GoodsParam;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GoodsService {
    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsCoverDao goodsCoverDao;

    @Resource
    private GoodsDetailDao goodsDetailDao;

    @Resource
    private GoodsParamDao goodsParamDao;

    //第一次查询后将数据放入缓存，第二次查询时直接从缓存中读取数据
    @Cacheable(value = "goods", key = "#goodsId")
    public Goods getGoods(Long goodsId){
        return goodsDao.findById(goodsId);
    }

    @Cacheable(value = "covers", key = "#goodsId")
    public List<GoodsCover> findCovers(Long goodsId){
        return goodsCoverDao.findByGoodsId(goodsId);
    }

    @Cacheable(value = "details", key = "#goodsId")
    public List<GoodsDetail> findDetails(Long goodsId){
        return goodsDetailDao.findByGoodsId(goodsId);
    }

    @Cacheable(value = "params", key = "#goodsId")
    public List<GoodsParam> findParams(Long goodsId){
        return goodsParamDao.findByGoodsId(goodsId);
    }
}
