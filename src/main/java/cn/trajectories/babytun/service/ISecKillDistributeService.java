package cn.trajectories.babytun.service;

import cn.trajectories.babytun.entity.JsonRespDTO;

public interface ISecKillDistributeService {

    /**
     * 初始化Redis中秒杀的库存信息
     * @param gid
     */
    void initializeRedis(long gid);

    /**
     * 秒杀结束后，将Redis中秒杀信息写入到数据库中进行保存
     */
    void updateDateBaseFromRedis(long gid);

    /**
     * 不加锁，最原始的处理
     * @param killId
     * @param userId
     * @return
     */
    JsonRespDTO handleWithOutLock(long killId, long userId);

    /**
     * case1:基于Redisson的分布式锁，正常
     * @param killId
     * @param userId
     * @return
     */
    JsonRespDTO handleWithRedisson(long killId, long userId);

    /**
     * case2:Zookeeper分布式锁，正常
     * @param killId
     * @param userId
     * @return
     */
    JsonRespDTO handleWithZk(long killId, long userId);

    /**
     * case3:Redis分布式队列-订阅消费,正常
     * @param killId
     * @param userId
     * @param num
     * @return
     */
    JsonRespDTO handleWithRedisList(long killId, long userId);

}
