package cn.trajectories.babytun.service;

import cn.trajectories.babytun.entity.JsonRespDTO;

public interface ISecKillDistributeService {

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
}
