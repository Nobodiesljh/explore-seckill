package cn.trajectories.babytun.service;

import cn.trajectories.babytun.entity.JsonRespDTO;

public interface ISecKillNDService {

    /**
     * 处理数据库中的两个秒杀有关的表，初始化秒杀信息
     * @param goodsId
     */
    void initializeSecKill(long goodsId);

    /**
     * 查询秒杀售卖出的商品数量
     * @param gid
     * @return
     */
    long getKilledCount(long gid);

    /**
     * case1:不加锁,出现超卖现象
     * @param killId
     * @param userId
     * @return
     */
    JsonRespDTO handleSecKill(long killId, long userId);

    /**
     * case2:加ReentrantLock,正常
     * @param killId
     * @param userId
     * @return
     */
    JsonRespDTO handleSecKillWithLock(long killId, long userId);
}
