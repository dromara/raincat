happylifeplat-transaction
================

碧桂园旺生活平台强一致性分布式事务，是基于二阶段提交+本地事务补偿机制来实现。[原理介绍](http://www.hollischuang.com/archives/681)

基于java语言来开发（JDK1.8），支持dubbo,motan,springcloud进行分布式事务。

#####  因为文件名太长，大家在拉取代码的时候执git命令：git config --global core.longpaths true

 # Features

  * **框架特性**

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

# Design
 ### [架构设计](https://github.com/yu199195/happylifeplat-transaction/wiki/design)

#   Configuration

  ###  [配置详解](https://github.com/yu199195/happylifeplat-transaction/wiki/configuration%EF%BC%88%E9%85%8D%E7%BD%AE%E8%AF%A6%E8%A7%A3%EF%BC%89)


# Prerequisite

  *   #### JDK 1.8+

  *   #### Maven 3.2.x

  *   #### Git

  *   ####  RPC framework dubbo or motan or springcloud。

# Quick Start

   * ### Clone & Build
      ```
      > git clone https://github.com/yu199195/happylifeplat-transaction.git
   
      > cd happylifeplat-transaction
   
      > mvn -DskipTests clean install -U
      ```

   ### [快速体验(dubbo)](https://github.com/yu199195/happylifeplat-transaction/wiki/quick-start-%EF%BC%88dubbo%EF%BC%89)

   ### [快速体验(springcloud)](https://github.com/yu199195/happylifeplat-transaction/wiki/quick-start-%EF%BC%88springcloud%EF%BC%89)


# User Guide

###  [dubbo 用户](https://github.com/yu199195/happylifeplat-transaction/wiki/dubbo%E7%94%A8%E6%88%B7%E6%8C%87%E5%8D%97)

###  [springcloud 用户](https://github.com/yu199195/happylifeplat-transaction/wiki/springcloud%E7%94%A8%E6%88%B7%E6%8C%87%E5%8D%97)






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


 # Contribution
