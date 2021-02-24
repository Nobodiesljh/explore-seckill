package cn.trajectories.babytun.service.impl;


import cn.trajectories.babytun.dao.PromotionSecKillDao;
import cn.trajectories.babytun.dao.SuccessKilledDao;
import cn.trajectories.babytun.distributedlock.redis.RedissLockUtil;
import cn.trajectories.babytun.entity.JsonRespDTO;
import cn.trajectories.babytun.entity.SuccessKilled;
import cn.trajectories.babytun.service.ISecKillDistributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service("SecKillDistributeService")
@Primary
public class SecKillDistributeServiceImpl implements ISecKillDistributeService {

    private final static Logger logger = LoggerFactory.getLogger(SecKillDistributeServiceImpl.class);

    @Autowired(required = false)
    private PromotionSecKillDao promotionSecKillDao;
    @Autowired(required = false)
    private SuccessKilledDao successKilledDao;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonRespDTO handleWithOutLock(long goodsId, long userId) {
        JsonRespDTO result = new JsonRespDTO();
        try {
            // 校验库存
            long psCount = promotionSecKillDao.selectCountByGoodsId(goodsId);
            if(psCount > 0){
                // 1.扣库存
                Map<String, Long> data = new HashMap<>();
                data.put("psCount", psCount-1);
                data.put("goodsId", goodsId);
                promotionSecKillDao.reduceStockByGoodsId(data);

                // 2.创建订单
                SuccessKilled killed = new SuccessKilled();
                killed.setGoodsId(goodsId);
                killed.setUserId(userId);
                killed.setState((short) 0);
                killed.setCreateTime(new Timestamp(System.currentTimeMillis()));
                successKilledDao.creatOrder(killed);

                // 3.返回支付页面
                result.setMessage("下单成功");
            } else {
                result.setMessage("库存不够了");
                result.setStatus(JsonRespDTO.STATUS_FAILURE);
            }
        } catch (Exception e) {
            logger.error("秒杀异常", e);
            result.setMessage("秒杀系统出现异常");
            result.setStatus(JsonRespDTO.STATUS_ERROR);
        }
        return result;
    }

    @Override
    public JsonRespDTO handleWithRedisson(long goodsId, long userId) {
        JsonRespDTO result = new JsonRespDTO();
        boolean flag = false;
        try {
            flag = RedissLockUtil.tryLock(goodsId+"", TimeUnit.SECONDS, 3, 20);
            if (flag) {
                result = this.handleWithOutLock(goodsId, userId);
            } else {
                logger.error("没有成功加上RedisLock");
            }
        } catch (Exception e) {
            logger.error("秒杀异常", e);
            result.setMessage("秒杀系统出现异常");
            result.setStatus(JsonRespDTO.STATUS_ERROR);
        } finally {
            if (flag) {
                // 释放锁
                RedissLockUtil.unlock(goodsId+"");
            }
        }
        return result;
    }

}
