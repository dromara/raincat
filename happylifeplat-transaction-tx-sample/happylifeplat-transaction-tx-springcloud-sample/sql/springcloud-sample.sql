/*
SQLyog Ultimate v12.2.6 (64 bit)
MySQL - 5.7.19-0ubuntu0.16.04.1 : Database - alipay
*********************************************************************
*/


CREATE DATABASE /*!32312 IF NOT EXISTS*/`alipay` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `alipay`;

/*Table structure for table `alipay` */

DROP TABLE IF EXISTS `alipay`;

CREATE TABLE `alipay` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `amount` decimal(4,0) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4;


CREATE DATABASE /*!32312 IF NOT EXISTS*/`pay` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `pay`;

/*Table structure for table `pay` */

DROP TABLE IF EXISTS `pay`;

CREATE TABLE `pay` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `total_amount` decimal(6,0) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`wechat` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `wechat`;

/*Table structure for table `wechat` */

DROP TABLE IF EXISTS `wechat`;

CREATE TABLE `wechat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `amount` decimal(4,0) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4;


