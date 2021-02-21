package cn.trajectories.babytun.common.queue.jvm;

import cn.trajectories.babytun.entity.JsonRespDTO;
import cn.trajectories.babytun.entity.SuccessKilled;
import cn.trajectories.babytun.service.ISecKillNDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消费秒杀队列
 */
@Component
public class BlockingQueueConsumer implements ApplicationRunner {

    private final static Logger logger = LoggerFactory.getLogger(BlockingQueueConsumer.class);
    /** 获取cpu核心的数量 */
    private static int corePoolSize = 1;
    // 创建线程池
    private static ThreadPoolExecutor executor =
            new ThreadPoolExecutor(corePoolSize,
                    corePoolSize+1,
                    10l,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000));

    @Autowired
    private ISecKillNDService secKillNDService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Runnable task = () -> {
            logger.info("准备消费阻塞队列，处理秒杀");
            while(true) {
                try {
                    SuccessKilled kill = SeckillQueue.getSkillQueue().consume();
                    if(null != kill) {
                        // 这里使用串行消费，所以直接利用不加锁的方法就可以了
                        JsonRespDTO result = secKillNDService
                                .handleSecKill(kill.getGoodsId(), kill.getUserId());
                        if(null != result){
                            logger.info("用户:{}{}", kill.getUserId(), result.getMessage());
                        }else{
                            logger.info("用户:{}{}", kill.getUserId(), "抢购火爆,请稍后！");
                        }
                    }
                } catch (InterruptedException e) {
                    logger.error("秒杀队列消费时异常", e);
                }
            }
        };
        executor.execute(task);
    }
}
