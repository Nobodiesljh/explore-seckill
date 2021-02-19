# 初探高并发编程：秒杀系统

这里是个人尝试通过秒杀系统学习并发编程知识的一个小项目。希望能够对您有帮助。

本项目主要结合 小柒2012/spring-boot-seckill开源项目 与 祁老师秒杀实战视频教程 来进行学习以及复现改进。资源如下

1）[spring-boot-seckill开源项目](https://gitee.com/52itstyle/spring-boot-seckill)

2）[祁老师秒杀实战全套百度云:tz8v](https://pan.baidu.com/s/1XhHbPSLpoJYXx4WgkIlDzA)

### 一、最原始网页

#### 1. 开发环境

- IDEA
- Spring-Boot 2.2.0+
- MySQL5.6+
- jdk8+
- MyBatis
- Freemarker
- Redis 5.0.3
- swagger2.0

本项目的所有接口api可以查看：http://.../swagger-ui.html



#### 2. 部署环境

1）除MySQL、Redis外，所有的网页、中间件等都部署在本地win10电脑，配置如下

![](pic/seckill_3.PNG)

2）MySQL5.7 和 Redis 5.0.3 部署在了本地的centos7虚拟机上



#### 3. 创建数据库/创建项目工程

- 导入数据库脚本，建立好数据库
- 数据库脚本见数据库脚本文件夹中
- 创建工程，配置好环境



#### 4. 搭建初始商品购买网页

原本静态的商品购买页面，用从数据库中读取各个商品的数据进行填充，实现不同商品信息的动态查询展示。此时的项目结构如下图

![](pic/seckill_1.PNG)

访问网址例如：http://localhost:8080/good?gid=739

- gid=商品编号

#### 5. 第一次JMeter压测：仅测试访问商品的信息页面

JMeter的参数设置见JMeter文件夹

模拟100个用户，每个用户作100次的访问

![](pic/seckill_2.PNG)

- 吞吐量每秒83.1次

- 平均延迟1.175秒


### 二、商品信息静态数据优化

在访问商品信息页面的时候，存在很多静态的、不怎么改动的静态资源信息需要从数据库中读取出来，这部分可以利用缓存等技术进行优化，减少对数据库的访问，提高访问性能

#### 1. Redis缓存 feature/staticResImpro-Redis分支
在pox.xml导入redis和cache的依赖，然后在启动文件中加上@EnableCaching的注解，并在service文件中相应的方法上加入@Cacheable引入缓存

#### 2. 第二次访问商品信息页面JMeter压测：加入Redis缓存后
JMeter的参数设置见JMeter文件夹

模拟100个用户，每个用户作100次的访问

1）一次
![](pic/seckill_4.PNG)

2）再一次
![](pic/seckill_5.PNG)

- 可以从吞吐量等指标看出，加入redis缓存对静态资源访问的优化效果较为明显

#### 3. 静态化处理等
对于访问静态页面数据的优化方法还有很多，比如页面的静态化处理、动静分离等，这里就不赘述了

### 三、(单机)秒杀系统 feature/seckillNotDistribute分支

#### 0. 准备
1）为了方便，准备了一张静态页面来作为秒杀商品:static/seckill.html

2）注意数据库中的 t_promotion_seckill 秒杀信息表; t_success_killed 秒杀成功表

3）加入秒杀使用的entity、mapper、dao等

4）使用任务调度，实现项目启动时，开启秒杀活动，即将秒杀信息表中的status置为开启

#### 1. case1：不加锁,出现超卖现象
接口：/seckill/handle?gid=1197

接口内模拟了skillNum个用户进行秒杀，观察控制台日志输出的信息。

可以从t_success_killed表中秒杀订单数量 与 t_promotion_seckill表中商品的剩余数量可以看出，出现了超卖

#### 2. case2: 加ReentrantLock,秒杀正常
接口：/seckill/handleWithLock?gid=1197

这里在ReentrantLock和synchronized中选择ReentrantLock，主要是因为synchronized是非公平锁，而ReentrantLock能选择公平与非公平。这里秒杀是要先到先得，因此设置为公平锁

这里注意要用锁把整个事务都包裹起来，不然会因为事务还没提交就把锁资源释放而出现的超卖现象

小柒2012/spring-boot-seckill项目中保留了出现超卖现象的代码块，并且提供一种利用自定义注解来解决bug的方法，见下面的case

#### 3. case3: 自定义注解+AOP,正常

[从构建分布式秒杀系统聊聊Lock锁使用中的坑](https://blog.52itstyle.vip/archives/2952/)
 
 自定义@ServiceLock注解，并自定义LockAspect切面，切给注解，实现一个加锁的操作
 
 #### 4. case4: 