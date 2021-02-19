package cn.trajectories.babytun.controller;

import cn.trajectories.babytun.entity.JsonRespDTO;
import cn.trajectories.babytun.service.ISecKillNDService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Api(tags ="(单机)秒杀")
@RestController
@RequestMapping("/seckill")
public class SecKillNotDistributeController {

    private final static Logger logger = LoggerFactory.getLogger(SecKillNotDistributeController.class);
    // 获取cpu核心的数量
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    // 创建线程池
    private static ThreadPoolExecutor executor =
            new ThreadPoolExecutor(corePoolSize,
                    corePoolSize+1,
                    10l,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000));

    @Autowired
    private ISecKillNDService secKillService;

    @ApiOperation(value = "case1:不加锁,出现超卖现象")
    @GetMapping("/handle")
    public JsonRespDTO handle(long gid) {
        int skillNum = 20;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        // 数据库中的商品、秒杀信息初始化
        secKillService.initializeSecKill(gid);

        final long killId = gid;
        logger.info("case1:不加锁,出现超卖现象");
        // 模拟skillNum个用户在秒杀
        for(int i = 0; i < skillNum; i++){
            final long userId = i;
            Runnable task = () -> {
                try{
                    JsonRespDTO result = secKillService.handleSecKill(killId, userId);
                    if(null != result){
                        logger.info("用户:{}{}",userId,result.getMessage());
                    }else{
                        logger.info("用户:{}{}",userId,"抢购火爆,请稍后！");
                    }
                }catch (Exception e){
                    logger.error("秒杀系统出错", e);
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await(); // 等待所有人任务结束
            long killedCount = secKillService.getKilledCount(gid);
            logger.info("一共秒杀出{}件商品",killedCount);
        } catch (Exception e) {
            logger.error("秒杀系统出错", e);
        }
        return JsonRespDTO.fail("会存在超卖现象");
    }

    @ApiOperation(value = "case2:加ReentrantLock,正常")
    @GetMapping("/handleWithLock")
    public JsonRespDTO handleWithLock(long gid) {
        int skillNum = 56;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        // 数据库中的商品、秒杀信息初始化
        secKillService.initializeSecKill(gid);

        final long killId = gid;
        logger.info("case2:加ReentrantLock,正常");
        // 模拟skillNum个用户在秒杀
        for(int i = 0; i < skillNum; i++){
            final long userId = i;
            Runnable task = () -> {
                try{
                    JsonRespDTO result = secKillService.handleSecKillWithLock(killId, userId);
                    if(null != result){
                        logger.info("用户:{}{}",userId,result.getMessage());
                    }else{
                        logger.info("用户:{}{}",userId,"抢购火爆,请稍后！");
                    }
                }catch (Exception e){
                    logger.error("秒杀系统出错", e);
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await(); // 等待所有人任务结束
            long killedCount = secKillService.getKilledCount(gid);
            logger.info("一共秒杀出{}件商品",killedCount);
        } catch (Exception e) {
            logger.error("秒杀系统出错", e);
        }
        return JsonRespDTO.success("秒杀正常");
    }

    @ApiOperation(value = "case3:自定义加ReentrantLock的注解,正常")
    @GetMapping("/handleWithAop")
    public JsonRespDTO handleWithAop(long gid) {
        int skillNum = 71;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        // 数据库中的商品、秒杀信息初始化
        secKillService.initializeSecKill(gid);

        final long killId = gid;
        logger.info("case3:自定义加ReentrantLock的注解,正常");
        // 模拟skillNum个用户在秒杀
        for(int i = 0; i < skillNum; i++){
            final long userId = i;
            Runnable task = () -> {
                try{
                    JsonRespDTO result = secKillService.handleSecKillWithAopLock(killId, userId);
                    if(null != result){
                        logger.info("用户:{}{}",userId,result.getMessage());
                    }else{
                        logger.info("用户:{}{}",userId,"抢购火爆,请稍后！");
                    }
                }catch (Exception e){
                    logger.error("秒杀系统出错", e);
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await(); // 等待所有人任务结束
            long killedCount = secKillService.getKilledCount(gid);
            logger.info("一共秒杀出{}件商品",killedCount);
        } catch (Exception e) {
            logger.error("秒杀系统出错", e);
        }
        return JsonRespDTO.success("秒杀正常");
    }

}
