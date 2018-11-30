Raincat
================
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5873cc1f5e2f44979aa1f64696fecb40)](https://www.codacy.com/app/yu199195/Raincat?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=yu199195/Raincat&amp;utm_campaign=Badge_Grade)
[![Total lines](https://tokei.rs/b1/github/yu199195/raincat?category=lines)](https://github.com/yu199195/raincat)
[![License](https://img.shields.io/cran/l/devtools.svg)](https://github.com/yu199195/Raincat/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/org.dromara/Raincat.svg?label=maven%20central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.dromara%22%20AND%20Raincat)
[![Javadocs](http://www.javadoc.io/badge/org.dromara/Raincat.svg)](http://www.javadoc.io/doc/org.dromara/Raincat)
[![Build Status](https://travis-ci.org/yu199195/Raincat.svg?branch=master)](https://travis-ci.org/yu199195/Raincat)

强一致性分布式事务，是基于二阶段提交+本地事务补偿机制来实现。[原理介绍](http://www.hollischuang.com/archives/681)

基于java语言来开发（JDK1.8），支持dubbo,motan,springcloud进行分布式事务。

 # Features

  * **框架特性**

      * 无缝集成spring 或 spring boot。

      * 支持dubbo,motan,springcloud,等rpc框架进行分布式事务。

      * 事务发起者，参与者与协调者底层基于netty长连接通信,稳定高效。

      * 协调者采用eureka做注册中心，支持集群模式。

      * 采用Aspect AOP 切面思想与Spring无缝集成。

      * 配置简单，集成简单，源码简洁，稳定性高，已在生产环境使用。

      * 内置经典的分布式事务场景demo工程，并有swagger-ui可视化界面可以快速体验。


 * ***事务角色***

   * 事务发起者（可理解为消费者 如：dubbo的消费者,springcloud的调用方）,发起分布式事务

   * 事务参与者（可理解为提供者 如：dubbo的提供者,springcloud的rest服务提供者),参与事务发起者的事务

   * 事务协调者（tx-manager），协调分布式事务的提交，回滚等。

 * ***技术方案***

   * 协调者（tx-manager）采用eureka作为注册中心，集群配置，达到服务的高可用，采用redis集群来分布式存储事务数据, springboot 提供rest服务，采用netty与参与者，发起者进行长连接通信。

   * 发起者与协调者，采用Aspect AOP 切面思想，SPI，多线程，异步回调，线程池，netty通信等技术。


 * ***SPI扩展***
     * 本地事务恢复，支持redis，mogondb，zookeeper，file，mysql等关系型数据库
     * 本地事务序列化保存，支持java，hessian，kryo，protostuff
     * netty通信序列化方式，支持 hessian，kryo，protostuff


# 官网

 ## http://dromara.org  或者 https://dromara.org 有时候https打不开。
 

# 文档 
 
 ##  http://dromara.org/website/zh-cn/docs/raincat/index.html

# 视频源码分析

 ### [环境搭建](http://www.iqiyi.com/w_19s0ngjah5.html)
 
 ### [启动过程](http://www.iqiyi.com/w_19s0ndc5vh.html)
 
 ### [事务提交](http://www.iqiyi.com/w_19s0ndc8f1.html)
 
 ### [回滚恢复](http://www.iqiyi.com/w_19s0nmod9t.html)
 
 ### [管理后台](http://www.iqiyi.com/w_19s0nj1bjx.html)
 
# 流程图

 ![](https://yu199195.github.io/images/Raincat/2pc.png)


# FAQ

* ### 为什么我下载的代码后，用idea打开没有相应的get set 方法呢？
   ##### 答：因为框架使用了Lombok包，它是在编译的时期，自动生成get set方法，并不影响运行，如果觉得提示错误难受，请自行下载lombok包插件，[lombok官网](http://projectlombok.org/)

* ### 为什么我运行demo工程，找不到applicationContent.xml呢？
  ##### 答：请设置项目的资源文件夹。

* ### 为什么我启动admin项目的时候，会报mongo 集群连接错误呢？
  ##### 答：这是因为项目里面有mongo代码，spring boot会自动配置，该错误没有关系，只要admin项目能正常启动就行。


# Support

 * ###  如有任何问题欢迎加入QQ群进行讨论
   ![](https://yu199195.github.io/images/qq.png)

 * ###  微信公众号
   ![](https://yu199195.github.io/images/public.jpg)

 # Contribution
