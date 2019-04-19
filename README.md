[Raincat](https://dromara.org)
================
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5873cc1f5e2f44979aa1f64696fecb40)](https://www.codacy.com/app/yu199195/Raincat?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=yu199195/Raincat&amp;utm_campaign=Badge_Grade)
[![Total lines](https://tokei.rs/b1/github/yu199195/raincat?category=lines)](https://github.com/yu199195/raincat)
[![License](https://img.shields.io/cran/l/devtools.svg)](https://github.com/yu199195/Raincat/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/org.dromara/raincat.svg?label=maven%20central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.dromara%22%20AND%20raincat)
[![Build Status](https://travis-ci.org/yu199195/Raincat.svg?branch=master)](https://travis-ci.org/yu199195/Raincat)
[![QQ群](https://img.shields.io/badge/chat-on%20QQ-ff69b4.svg?style=flat-square)](https://shang.qq.com/wpa/qunwpa?idkey=2e9e353fa10924812bc58c10ab46de0ca6bef80e34168bccde275f7ca0cafd85)

#### A strongly consistent distributed transaction framework


# Modules

  * raincat-admin: Transaction log management background
  
  * raincat-annotation : Framework common annotation

  * raincat-common :  Framework common class
  
  * raincat-core : Framework core package (annotation processing, log storage...)              
  
  * raincat-dashboard : Management background front-end
  
  * raincat-dubbo : Support for the dubbo framework Less than 2.7 version
  
  * raincat-motan : Support for the motan rpc framework
  
  * raincat-springcloud : Support for the spring cloud rpc framework
  
  * raincat-spring-boot-starter : Support for the spring boot starter
  
  * raincat-sample : Examples using the raincat framework


#  Features
   
   *  All spring versions are supported and Seamless integration
   
   *  Provides support for the springcloud dubbo motan RPC framework
   
   *  Provides integration of the spring boot starter approach
   
   *  Support Nested transaction 
   
   *  Local transaction storage support :  redis mongodb zookeeper file mysql
   
   *  Transaction log serialization support : java hessian kryo protostuff
   
   *  Spi extension : Users can customize the storage of serialization and transaction logs

# Transaction Role

  * Transaction starter : `@TxTransaction` for The entry point of the first section
  
  * Transaction participant : Rpc invoker in the method (Add `@TxTransaction`)
  
  * Transaction coordinator : Coordinate the rollback of commit transactions
   

# Raincat-Manager
 
  It is the coordinator of the transaction and USES netty communication framework to communicate with participants and initiators.
  
  Use eureka as a registry to support cluster deployment.
  
  Use redis to store transaction information.
  
  It has to start early.


# Prerequisite 

  * You must use jdk1.8 +
  
  * You must be a user of the spring framework
  
  * You must use one of the dubbo, motan, and springcloud RPC frameworks
  

# About
 
  raincat is A strongly consistent distributed transaction framework.
  
  Good concurrency support, blocking spring transaction thread commit.
  
  When the execution of the slice is complete and there is no exception, 
  
  the submission command is sent asynchronously by the coordinator to achieve strong consistency.

  If you want to use it or get a quick look at it. [Quick Start](http://dromara.org/website/zh-cn/docs/raincat/index.html)

# Stargazers 
[![Stargazers over time](https://starchart.cc/yu199195/Raincat.svg)](https://starchart.cc/yu199195/Raincat)

 
# Flow 

 ![](https://yu199195.github.io/images/Raincat/2pc.png)


# Support
  ![](https://yu199195.github.io/images/qq.png)  ![](https://yu199195.github.io/images/public.jpg)
   
 

