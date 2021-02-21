package cn.trajectories.babytun.common.queue.jvm;

import cn.trajectories.babytun.entity.SuccessKilled;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// 秒杀队列
public class SeckillQueue {
	 //队列大小
    static final int QUEUE_MAX_SIZE   = 100;
    /** 用于多线程间下单的队列 */
    static BlockingQueue<SuccessKilled> blockingQueue = new LinkedBlockingQueue<>(QUEUE_MAX_SIZE);
    
    /**
     * 私有的默认构造子，保证外界无法直接实例化
     */
    private SeckillQueue(){};

    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
     */
    private static class SingletonHolder{
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
		private  static SeckillQueue queue = new SeckillQueue();
    }

    /**
     * 单例队列
     * @return
     */
    public static SeckillQueue getSkillQueue(){
        return SingletonHolder.queue;
    }

    /**
     * 生产入队
     * @param kill
     * @throws InterruptedException
     * add(e) 队列未满时，返回true；队列满则抛出IllegalStateException(“Queue full”)异常——AbstractQueue 
     * put(e) 队列未满时，直接插入没有返回值；队列满时会阻塞等待，一直等到队列未满时再插入。
     * offer(e) 队列未满时，返回true；队列满时返回false。非阻塞立即返回。
     * offer(e, time, unit) 设定等待的时间，如果在指定时间内还不能往队列中插入数据则返回false，插入成功返回true。 
     */
    public Boolean produce(SuccessKilled kill) {
    	return blockingQueue.offer(kill);
    }

    /**
     * 消费出队
     * poll() 获取并移除队首元素，在指定的时间内去轮询队列看有没有首元素有则返回，否者超时后返回null
     * take() 与带超时时间的poll类似不同在于take时候如果当前队列空了它会一直等待其他线程调用notEmpty.signal()才会被唤醒
     */
    public  SuccessKilled consume() throws InterruptedException {
        return blockingQueue.take();
    }

    /**
     * 获取队列大小
     * @return
     */
    public int size() {
        return blockingQueue.size();
    }
}
