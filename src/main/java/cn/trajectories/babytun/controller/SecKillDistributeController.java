package cn.trajectories.babytun.controller;

import cn.trajectories.babytun.entity.JsonRespDTO;
import cn.trajectories.babytun.service.ISecKillDistributeService;
import cn.trajectories.babytun.service.ISecKillNDService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Api(tags ="分布式秒杀")
@RestController
@RequestMapping("/seckillDistributed")
public class SecKillDistributeController {

    private final static Logger logger = LoggerFactory.getLogger(SecKillDistributeController.class);
    // 获取cpu核心的数量
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    // 创建线程池
    private static ThreadPoolExecutor executor =
            new ThreadPoolExecutor(corePoolSize,
                    corePoolSize+1,
                    10l,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(10000));

    @Autowired
    private ISecKillNDService secKillService;
    @Autowired
    private ISecKillDistributeService secKillDistributeService;


    @ApiOperation(value = "case1:基于Redisson的分布式锁，正常")
    @GetMapping("/handleWithRedisson")
    public JsonRespDTO handleWithRedisson(long gid) {
        int skillNum = 76;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        // 数据库中的商品、秒杀信息初始化
        secKillService.initializeSecKill(gid);

        final long killId = gid;
        logger.info("case1:基于Redisson的分布式锁，正常");
        // 模拟skillNum个用户在秒杀
        for(int i = 0; i < skillNum; i++){
            final long userId = i;
            Runnable task = () -> {
                try{
                    JsonRespDTO result = secKillDistributeService.handleWithRedisson(killId, userId);
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
        return JsonRespDTO.fail("秒杀系统正常");
    }



}
