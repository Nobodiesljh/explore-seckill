package cn.trajectories.babytun.service.impl;


import cn.trajectories.babytun.dao.PromotionSecKillDao;
import cn.trajectories.babytun.dao.SuccessKilledDao;
import cn.trajectories.babytun.distributedlock.redis.RedissLockUtil;
import cn.trajectories.babytun.distributedlock.zookeeper.ZkLockUtil;
import cn.trajectories.babytun.entity.JsonRespDTO;
import cn.trajectories.babytun.entity.PromotionSecKill;
import cn.trajectories.babytun.entity.SuccessKilled;
import cn.trajectories.babytun.service.ISecKillDistributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service("SecKillDistributeService")
@Primary
public class SecKillDistributeServiceImpl implements ISecKillDistributeService {

    private final static Logger logger = LoggerFactory.getLogger(SecKillDistributeServiceImpl.class);

    @Autowired(required = false)
    private PromotionSecKillDao promotionSecKillDao;
    @Autowired(required = false)
    private SuccessKilledDao successKilledDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void initializeRedis(long gid) {
        PromotionSecKill ps = promotionSecKillDao.selectGoodByGoodsId(gid);
        // 删掉以前重复的活动任务
        stringRedisTemplate.delete("seckill:count:" + ps.getGoodsId());
        stringRedisTemplate.delete("seckill:users" + ps.getGoodsId());
        stringRedisTemplate.delete("seckill:value:" + gid + "-num");
        // 有几件库存商品，则初始化几个list对象
        for (int i = 0; i < ps.getPsCount(); i++) {
            stringRedisTemplate.opsForList().rightPush("seckill:count:" + ps.getGoodsId(), ps.getPsId() + "");
        }
    }

    @Override
    public void updateDateBaseFromRedis(long goodsId, String flag) {
        try {
            Long size = 0l;
            if ("list".equals(flag)) {
                size = stringRedisTemplate.opsForList().size("seckill:count:" + goodsId);
            } else if ("incr".equals(flag)) {
                String s = stringRedisTemplate.opsForValue().get("seckill:value:" + goodsId + "-num");
                size = Long.parseLong(s) >= 0 ? Long.parseLong(s) : 0;
            }
            // 1.扣库存
            Map<String, Long> data = new HashMap<>();
            data.put("psCount", size);
            data.put("goodsId", goodsId);
            promotionSecKillDao.reduceStockByGoodsId(data);

            // 创建订单
            Set<String> userIds = stringRedisTemplate.opsForSet().members("seckill:users" + goodsId);
            for (String userId : userIds) {
                SuccessKilled killed = new SuccessKilled();
                killed.setGoodsId(goodsId);
                killed.setUserId(Long.parseLong(userId));
                killed.setState((short) 0);
                killed.setCreateTime(new Timestamp(System.currentTimeMillis()));
                successKilledDao.creatOrder(killed);
            }
        } catch (Exception e) {
            logger.error("redis数据写入数据库发生异常", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonRespDTO handleWithOutLock(long goodsId, long userId) {
        JsonRespDTO result = new JsonRespDTO();
        try {
            // 校验库存
            long psCount = promotionSecKillDao.selectCountByGoodsId(goodsId);
            if (psCount > 0) {
                // 1.扣库存
                Map<String, Long> data = new HashMap<>();
                data.put("psCount", psCount - 1);
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
    @Transactional(rollbackFor = Exception.class)
    public JsonRespDTO handleWithRedisson(long goodsId, long userId) {
        JsonRespDTO result = new JsonRespDTO();
        boolean flag = false;
        try {
            flag = RedissLockUtil.tryLock(goodsId + "", TimeUnit.SECONDS, 3, 20);
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
                RedissLockUtil.unlock(goodsId + "");
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonRespDTO handleWithZk(long goodsId, long userId) {
        JsonRespDTO result = new JsonRespDTO();
        boolean flag = false;
        try {
            flag = ZkLockUtil.acquire(3, TimeUnit.SECONDS);
            if (flag) {
                result = this.handleWithOutLock(goodsId, userId);
            } else {
                logger.error("没有成功加上Zookeeper锁");
            }
        } catch (Exception e) {
            logger.error("秒杀异常", e);
            result.setMessage("秒杀系统出现异常");
            result.setStatus(JsonRespDTO.STATUS_ERROR);
        } finally {
            if (flag) {
                // 释放锁
                ZkLockUtil.release();
            }
        }
        return result;
    }

    @Override
    public JsonRespDTO handleWithRedisList(long goodsId, long userId) {
        JsonRespDTO result = new JsonRespDTO();
        try {
            // 校验库存
            long psCount = promotionSecKillDao.selectCountByGoodsId(goodsId);
            if (psCount > 0) {
                // 弹出库存
                String leftPop = stringRedisTemplate.opsForList().leftPop("seckill:count:" + goodsId);
                if (null != leftPop) {
                    stringRedisTemplate.opsForSet().add("seckill:users" + goodsId, userId + "");
                    result.setMessage("下单成功");
                } else {
                    result.setMessage("抱歉，商品已抢光");
                    result.setStatus(JsonRespDTO.STATUS_FAILURE);
                }
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
    public JsonRespDTO handleWithRedisIncr(long goodsId, long userId) {
        JsonRespDTO result = new JsonRespDTO();
        try {
            // 扣库存
            Long psCount = stringRedisTemplate.opsForValue().increment("seckill:value:" + goodsId + "-num", -1);
            // 校验库存
            if (psCount >= 0) {
                stringRedisTemplate.opsForSet().add("seckill:users" + goodsId, userId + "");
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
}
