# 初探高并发编程：秒杀系统



### 一、最原始网页

#### 1. 开发环境

- IDEA
- Spring-Boot 2.2.0+
- MySQL5.6+
- jdk8+
- MyBatis
- Freemarker



#### 2. 部署环境

1）除MySQL外，所有的网页、中间件等都部署在本地win10电脑，配置如下

![3](C:\Users\93672\Desktop\blog\blogs-documents\初探高并发编程：秒杀系统\3.PNG)

2）MySQL5.7 部署在了本地的centos7虚拟机上



#### 3. 创建数据库/创建项目工程

- 导入数据库脚本，建立好数据库
- 数据库脚本在sql文件夹中
- 创建工程，配置好环境



#### 4. 搭建初始商品购买网页

利用Service->Controller->Dao的结构，把原本静态的商品购买页面，用从数据库中读取各个商品的数据进行填充，实现不同商品信息的动态查询展示。此时的项目结构如下图

![1](C:\Users\93672\Desktop\blog\blogs-documents\初探高并发编程：秒杀系统\1.PNG)



#### 5. 第一次JMeter压测：仅测试访问商品的信息页面

![2](C:\Users\93672\Desktop\blog\blogs-documents\初探高并发编程：秒杀系统\2.PNG)

- 吞吐量每秒83.1次

- 平均延迟1.175秒