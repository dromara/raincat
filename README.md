happylifeplat-transaction
================

碧桂园旺生活平台强一致性分布式事务，是基于三阶段提交+本地事务补偿机制来实现。[原理介绍](http://www.hollischuang.com/archives/681)

基于java语言来开发（JDK1.8），事务发起者，参与者与协调者底层基于netty长连接通信.
支持dubbo，springcloud进行分布式事务。

 # Features

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
 #### 架构设计

 #### 流程图原理详解
首先用户发起request请求进入consume端（也可以是controller层），controller层进入AOP切面，开启分布式事务
 * 会与txManager通信，创建事务组
 * 发起业务方法调用point.proceed(),这一步是分布式事务调用RPC方法的入口，会一个一个调用RPC方法
 * 主线程调用provder1 提供的RPC方法，同样会进入切面，此时，会启动另一个线程去调用该RPC方法，当前线程wait
  ```
  调用失败，调用业务方法线程，会唤醒等待的主线程，并把异常信息返回，走的就是one fial rollback这条线，消费方获取异常信息，自动回滚当前事务，分布式事务结束。

  调用成功  业务线程，将唤醒主线程，并返回数据。业务线程同txManager 通信 将自己加入到当前事务组中，业务线程进入等待，等待txManager指令，commit or rollback 即 图中的4,5,6
  ```
 * 主线程继续调用provider2提供的RPC方法 ，同样会进入AOP切面，当前主线程等待，启动一个业务线程去调用业务方法
```
调用失败，调用业务方法线程，会唤醒等待的主线程，并把异常信息返回，走的就是two fial rollback这条线，消费方获取异常信息，自动回滚当前事务，并与txManager进行通信（two TransactionGroup fail 这条线），txManager
发起rollback指令（two fail rollback 这条线），通知provider1 rollback

调用成功  业务线程，将唤醒主线程，并返回数据。业务线程同txManager 通信 将自己加入到当前事务组中，业务线程进入等待，等待txManager指令，commit or rollback 即 图中的9,10,11
```
* 主线程point.proceed()方法成功执行，会向txManager发出commit请求，txManager接收请求后，会检查各模块的网络通信等状态，符合条件后，发出commit指令，即 14。通知各个模块提交自己的事务。

#### 三阶段提交最大的问题，在于当txManager发出commit指令后，业务方down机，或者网络通信断了。怎么解决事务一致性的问题。我们这么想，当txManager 发出commit指令的时候，其实业务方法都没有任何异常，都是执行成功的，那么数据就是需要提交的。那么我们本地的补偿机制正是为了该场景而考虑，如果down机，或者网络异常。本地服务恢复后，会执行补偿，达到数据的一致性。如果业务方是集群部署的话，会立刻执行补偿，达到数据的一致性。

####  重要提醒，如果调用服务方是timeout，那么会判断当前事务不成功，这次分布式事务统一不会执行。如需了解更多细节，请您参考源码，欢迎大家一起探讨，指正。



#   Configuration

   #### TxManager配置详解
  *  application.properties 主要是配置tmManager的http服务端口，redis信息,netty相关信息
      注意：因为现在tmManager 是自己向自己注册，所以http端口（server.port）应该要与eureka的端口一致。
        ```
         server.port=8761   txManager  的http端口
         tx.manager.netty.port=9998  对业务方提供的TCP 端口。
         tx.manager.netty.serialize=kryo  netty 序列化方式，注意应该要与业务方的序列化方式一致。
         ```


* bootstrap.yml  主要是配置eureka的相关属性，比如renew时间，注册地址等
*    部署集群配置：
            1.修改application.properties中的 server.port  如：
              第一份服务为 server.port=8761    tx.manager.netty.port=9998;
              第二份服务为：server.port=8762   tx.manager.netty.port=9999;
           2. 修改bootstrap.yml中的eureka:client:serviceUrl:defaultZone:http://localhost:8761/eureka/,http://localhost:8762/eureka/
              再依次启动


 #### 业务方配置详解
 ```
  @TxTransaction  该注解为分布式事务的切面（AOP point），如果业务方的service服务需要参与分布式事务，则需要加上此注解
 ```
 ######  applicationContext.xml 详解：
```
   <!-- Aspect 切面配置，是否开启AOP切面-->
   <aop:aspectj-autoproxy expose-proxy="true"/>
   <!--扫描分布式事务的包-->
   <context:component-scan base-package="com.happylifeplat.transaction.*"/>
   <!--启动类属性配置-->
   <bean id="txTransactionBootstrap" class="com.happylifeplat.transaction.core.bootstrap.TxTransactionBootstrap">
       <property name="txManagerUrl" value="http://192.168.1.66:8761"/>
       <property name="serializer" value="kryo"/>
       <property name="nettySerializer" value="kryo"/>
       <property name="blockingQueueType" value="Linked"/>
       <property name="compensation" value="true"/>
       <property name="compensationCacheType" value="db"/>
       <property name="txDbConfig">
           <bean class="com.happylifeplat.transaction.core.config.TxDbConfig">
               <property name="url"
                         value="jdbc:mysql://192.168.1.78:3306/order?useUnicode=true&amp;characterEncoding=utf8"/>
               <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
               <property name="password" value="Wgj@555888"/>
               <property name="username" value="xiaoyu"/>
           </bean>
       </property>
   </bean>

```
###### TxTransactionBootstrap 详解（具体参见com.happylifeplat.transaction.core.config.TxConfig）：
```
   <!--这里配置的TxManager http请求的IP:PORT (如果TxManager有改动，这里要跟着改动)-->
   <property name="txManagerUrl" value="http://192.168.1.66:8761"/>

   <!-- 与txManager通信的序列化方式，spi扩展支持 kroy，hessian protostuff 推荐使用kroy-->
   <property name="nettySerializer" value="kryo"/>

   <!--  线程池中的队列类型 spi扩展支持 Linked Array SynchronousQueue-->
   <property name="blockingQueueType" value="Linked"/>

   <!--线程池中的拒绝策略 spi扩展支持 Abort Blocking CallerRuns Discarded Rejected-->
   <property name="rejectPolicy" value="Abort"/>

   <!--开启本地补偿（默认开启）-->
   <property name="compensation" value="true"/>  
   <!--本地数据序列化方式  spi扩展支持 java kroy，hessian protostuff 推荐使用kroy-->
   <property name="serializer" value="kryo"/>
```
###### 本地数据保存配置与详解(spi扩展支持db，redis，zookeeper，mongodb，file),详情配置请参照sample工程:
1. 本地数据存储为数据库（数据库支持mysql，oracle ，sqlServer），当业务模块为集群时，推荐使用
   会自动创建表，表名称为 tx_transaction_模块名称（applicationName）
```      
        <!--配置补偿类型为db-->
        <property name="compensationCacheType" value="db"/>
        <property name="txDbConfig">
            <bean class="com.happylifeplat.transaction.core.config.TxDbConfig">
                <!--数据库url-->
                <property name="url"
                          value="jdbc:mysql://192.168.1.78:3306/order?useUnicode=true&amp;characterEncoding=utf8"/>
                <!--数据库驱动名称 -->          
                <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
                <property name="password" value="Wgj@555888"/>
                <property name="username" value="xiaoyu"/>
            </bean>
        </property>
```
2. 本地数据存储为redis，当业务模块为集群时，推荐使用。（更多配置请参考 com.happylifeplat.transaction.core.config.TxRedisConfig）
```  
     <!--配置补偿类型为reids-->
     <property name="compensationCacheType" value="redis"/>
     <property name="txRedisConfig">
        <bean class="com.happylifeplat.transaction.core.config.TxRedisConfig">
          <!--redis host-->
          <property name="hostName"  value="192.168.1.78"/>
          <!--redis port-->
          <property name="port" value="6379"/>
          <!--redis 密码 （有密码就配置，无密码则不需要配置）-->
          <property name="password" value=""/>    
       </bean>
    </property>
```

3. 本地数据存储为zookeeper，当业务模块为集群时，推荐使用
```  
      <!--配置补偿类型为zookeeper-->
      <property name="compensationCacheType" value="zookeeper"/>
      <property name="txZookeeperConfig">
          <bean class="com.happylifeplat.transaction.core.config.TxZookeeperConfig">
              <!--zookeeper host：port-->
              <property name="host"  value="192.168.1.66:2181"/>
              <!--zookeeper  session过期时间-->
              <property name="sessionTimeOut" value="2000"/>
              <!--zookeeper  根节点路径-->
              <property name="rootPath" value="/tx"/>
          </bean>
      </property>
```
4. 本地数据存储为mongodb，当业务模块为单节点时，可以使用。会自动创建集合，集合名称为 tx_transaction_模块名称（applicationName）
   这里mongdb连接方式采用3.4.0版本推荐使用的Sha1,不是CR模式，同时mongdb应该开启权限认证，使用者需要注意
```  
        <!--配置补偿类型为mongodb-->
        <property name="compensationCacheType" value="mongodb"/>
         <property name="txMongoConfig">
            <bean class="com.happylifeplat.transaction.core.config.TxMongoConfig">
                <!--mongodb url-->
                <property name="mongoDbUrl"  value="192.168.1.78:27017"/>
                <!--mongodb 数据库-->
                <property name="mongoDbName" value="happylife"/>
                <!--mongodb 用户名-->
                <property name="mongoUserName" value="xiaoyu"/>
                <!--mongodb 密码-->
                <property name="mongoUserPwd" value="123456"/>
            </bean>
        </property>
```
5. 本地数据存储为file，当业务模块为单节点时，可以使用。创建的文件名称TX_  +  prefix配置 + 模块名称
```  
      <!--配置补偿类型为file-->
      <property name="compensationCacheType" value="file"/>
      <property name="txFileConfig">
             <bean class="com.happylifeplat.transaction.core.config.TxFileConfig">
                 <!--指定文件路径（可填可不填，不填时候，默认就是当前项目所在的路径）-->
                 <property name="path"  value=""/>
                 <!--指定文件前缀，生成文件名称-->
                 <property name="prefix" value="consume"/>
             </bean>
       </property>
```

# Usage  

   ### 快速体检，运行dubbo-sample（ 使用者JDK必须为1.8）
##### 步揍一：
  配置txManaager, 修改application.properties中你自己的redis配置    
  启动TxManagerApplication
#####   注意：如果需要修改服务端口，则应该保证与eureka:client:serviceUrl:defaultZone中的端口一致

#### 步揍二：
1.  引入依赖包（sample已经引入）
    ```
    <dependency>
          <groupId>com.happylifeplat.transaction</groupId>
          <artifactId>happylifeplat-transaction-tx-dubbo</artifactId>
      </dependency>
    ```
2.   执行 happylifeplat-transaction-tx-dubbo-sample 工程  sql文件 dubbo-sample.sql
3.   在每个工程下的application.yml 中配置您的数据库连接(只需要改ip和端口)
4.   在工程下的spring-dubbo.xml 中配置您的zookeeper注册中心
5.   在每个工程下 applicationContext.xml中的TxDbConfig 配置您的补偿数据库连接，最好与模块数据库一致
6.   在需要做分布式事务的接口上加上注解  @TxTransaction （sample已经加上）
7.   依次启动order模块，stock 模块 ,consume模块
8.   发起consume模块中的rest请求
     ```
     http://consume的ip:port/consume/order/save 可以看到三个数据库均插入数据
     http://consume的ip:port/consume/order/orderFail 此时t_test 表不会新增数据 order表不会新增数据 stock则不执行
     具体的请看demo中代码和描述
     ```

     ### 快速体检，运行springcloud-sample（ 使用者JDK必须为1.8）
  ##### 步揍一：
    配置txManaager, 修改application.properties中你自己的redis配置    
    启动TxManagerApplication
  #####   注意：如果需要修改服务端口，则应该保证与eureka:client:serviceUrl:defaultZone中的端口一致

  #### 步揍二：
  1.  引入依赖包（sample已经引入）
      ```
      <dependency>
             <groupId>com.happylifeplat.transaction</groupId>
             <artifactId>happylifeplat-transaction-tx-springcloud</artifactId>
             <exclusions>
                 <exclusion>
                     <groupId>org.mongodb</groupId>
                     <artifactId>mongo-java-driver</artifactId>
                 </exclusion>
             </exclusions>
         </dependency>
      ```
  2.   执行 happylifeplat-transaction-tx-springcloud-sample 工程  sql文件 springcloud-sample.sql
  3.   在每个工程下 application.yml 中配置您的数据库连接(只需要改ip和端口)
  4.   在每个工程下 applicationContext.xml中的TxDbConfig 配置您的补偿数据库连接，最好与模块数据库一致  
  5.   在需要做分布式事务的接口上加上注解  @TxTransaction （sample已经加上）
  6.   依次启动AliPayApplication，WechatApplication ,PayApplication
  7.   发起pay模块中的rest请求
       ```
       http://pay的ip:port/pay/orderPay 可以看到三个数据库均插入数据
       http://pay的ip:port/pay//aliPayFail 当alipay支付异常的时候，pay表的数据不会新增 alipay表不会新增 wechat表不会新增
       具体的请看项目中代码和描述
     ```





 # Support


 # Contribution
