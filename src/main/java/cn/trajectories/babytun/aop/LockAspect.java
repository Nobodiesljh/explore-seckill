package cn.trajectories.babytun.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 同步锁 AOP
 */
@Component
@Scope
@Aspect
//order越小越是最先执行，但更重要的是最先执行的最后结束。order默认值是2147483647
@Order(1)
public class LockAspect {

    private static Lock lock = new ReentrantLock(true);

    @Pointcut("@annotation(cn.trajectories.babytun.aop.ServiceLock)")
    public void lockAspect() {

    }

    @Around("lockAspect()")
    public Object around(ProceedingJoinPoint joinPoint) {
        lock.lock();
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
        return obj;
    }

}
