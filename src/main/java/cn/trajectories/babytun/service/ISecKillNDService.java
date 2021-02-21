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

    /**
     * case3:自定义加ReentrantLock的注解,正常
     * @param killId
     * @param userId
     * @return
     */
    JsonRespDTO handleSecKillWithAopLock(long killId, long userId);

    /**
     * case4:数据库悲观锁(方法一),正常
     * @param killId
     * @param userId
     * @return
     */
    JsonRespDTO handleSecKillWithPccOne(long killId, long userId);

    /**
     * case5:数据库悲观锁(方法二),正常
     * @param killId
     * @param userId
     * @return
     */
    JsonRespDTO handleSecKillWithPccTwo(long killId, long userId);

    /**
     * case6:数据库乐观锁，正常
     * @param killId
     * @param userId
     * @return
     */
    JsonRespDTO handleSecKillWithOcc(long killId, long userId, long num);

    /**
     * case7:JDK自带阻塞队列，正常
     * @param killId
     * @param userId
     * @return
     */
    boolean handleSecKillWithQueue(long killId, long userId);
}
