package cn.trajectories.babytun.service.impl;

import cn.trajectories.babytun.aop.ServiceLock;
import cn.trajectories.babytun.common.queue.jvm.SeckillQueue;
import cn.trajectories.babytun.controller.SecKillNotDistributeController;
import cn.trajectories.babytun.dao.PromotionSecKillDao;
import cn.trajectories.babytun.dao.SuccessKilledDao;
import cn.trajectories.babytun.entity.JsonRespDTO;
import cn.trajectories.babytun.entity.PromotionSecKill;
import cn.trajectories.babytun.entity.SuccessKilled;
import cn.trajectories.babytun.service.ISecKillNDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service("secKillNDService")
@Primary
public class SecKillNDServiceImpl implements ISecKillNDService {

    private final static Logger logger = LoggerFactory.getLogger(SecKillNDServiceImpl.class);

    // 选用公平锁,因为是需要先到先得
    private Lock lock = new ReentrantLock(true);

    @Autowired(required = false)
    private PromotionSecKillDao promotionSecKillDao;
    @Autowired(required = false)
    private SuccessKilledDao successKilledDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initializeSecKill(long goodsId) {
        try {
            successKilledDao.deleteByGoodsId(goodsId);
            promotionSecKillDao.updateCountByGoodsId(goodsId);
        } catch (Exception e) {
            logger.error("秒杀数据库信息初始化出错", e);
        }
    }

    @Override
    public long getKilledCount(long goodsId) {
        try {
            return successKilledDao.getKilledCountByGoodsId(goodsId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0l;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonRespDTO handleSecKill(long goodsId, long userId) {
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
    @Transactional(rollbackFor = Exception.class)
    public JsonRespDTO handleSecKillWithLock(long goodsId, long userId) {
        // 这里注意要用锁把整个事务都包裹起来，不然会出现超卖现象
        // 小柒2012/spring-boot-seckill源代码中目前还没有做修改，会存在超卖
        lock.lock();
        JsonRespDTO result = new JsonRespDTO();
        try {
            result = this.handleSecKill(goodsId, userId);
        } catch (Exception e) {
            logger.error("秒杀异常", e);
            result.setMessage("秒杀系统出现异常");
            result.setStatus(JsonRespDTO.STATUS_ERROR);
        }finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    @ServiceLock
    @Transactional(rollbackFor = Exception.class)
    public JsonRespDTO handleSecKillWithAopLock(long goodsId, long userId) {
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
    @Transactional(rollbackFor = Exception.class)
    public JsonRespDTO handleSecKillWithPccOne(long goodsId, long userId) {
        JsonRespDTO result = new JsonRespDTO();
        try {
            // 校验库存
            long psCount = promotionSecKillDao.selectCountByGoodsIdPcc(goodsId);
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
    @Transactional(rollbackFor = Exception.class)
    public JsonRespDTO handleSecKillWithPccTwo(long goodsId, long userId) {
        JsonRespDTO result = new JsonRespDTO();
        try {
            // 单用户抢购一件商品没有问题、但是抢购多件商品不建议这种写法 UPDATE锁表
            // 1.扣库存,加锁
            int count = promotionSecKillDao.reduceByGoodsId(goodsId);

            if(count > 0){
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
    public JsonRespDTO handleSecKillWithOcc(long goodsId, long userId, long num) {
        JsonRespDTO result = new JsonRespDTO();
        try {
            // 校验库存
            PromotionSecKill promotionSecKill = promotionSecKillDao.selectGoodByGoodsId(goodsId);
            if(promotionSecKill.getPsCount() >= num){
                // 1.扣库存
                Map<String, Long> data = new HashMap<>();
                data.put("num", num);
                data.put("goodsId", goodsId);
                data.put("version", promotionSecKill.getVersion());
                int count = promotionSecKillDao.reduceStockByOcc(data);

                // 2.创建订单
                if(count > 0) {
                    SuccessKilled killed = new SuccessKilled();
                    killed.setGoodsId(goodsId);
                    killed.setUserId(userId);
                    killed.setState((short) 0);
                    killed.setCreateTime(new Timestamp(System.currentTimeMillis()));
                    successKilledDao.creatOrder(killed);

                    // 3.返回支付页面
                    result.setMessage("下单成功");
                } else {
                    result.setMessage("下单失败");
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
    public boolean handleSecKillWithQueue(long goodsId, long userId) {
        boolean result = false;
        try {
            SuccessKilled kill = new SuccessKilled();
            kill.setGoodsId(goodsId);
            kill.setUserId(userId);
            result = SeckillQueue.getSkillQueue().produce(kill);
        } catch (Exception e) {
            logger.error("秒杀异常", e);
        }
        return result;
    }
}
