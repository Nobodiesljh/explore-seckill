package cn.trajectories.babytun.scheduler;

import cn.trajectories.babytun.dao.PromotionSecKillDao;
import cn.trajectories.babytun.entity.PromotionSecKill;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SecKillTask {

    @Resource
    private PromotionSecKillDao promotionSecKillDao;

    @Scheduled(cron = "0/5 * * * * ?")
    public void startSecKill(){
        List<PromotionSecKill> list = promotionSecKillDao.findUnstartSecKill();
        for(PromotionSecKill ps: list){
            System.out.println(ps.getPsId() + "秒杀活动已启动");
            ps.setStatus(1);
            promotionSecKillDao.update(ps);
        }
    }
}
