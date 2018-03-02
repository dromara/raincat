/*
SQLyog Ultimate v12.2.6 (64 bit)
MySQL - 5.7.19-0ubuntu0.16.04.1 : Database - test_1
*********************************************************************
*/

CREATE DATABASE /*!32312 IF NOT EXISTS*/`test_1` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `test_1`;

/*Table structure for table `t_test` */

DROP TABLE IF EXISTS `t_test`;

CREATE TABLE `t_test` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10082 DEFAULT CHARSET=utf8mb4;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`stock` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `stock`;

/*Table structure for table `stock` */

DROP TABLE IF EXISTS `stock`;

CREATE TABLE `stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `number` bigint(15) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8191 DEFAULT CHARSET=utf8mb4;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`order` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `order`;

/*Table structure for table `t_order` */

DROP TABLE IF EXISTS `t_order`;

CREATE TABLE `t_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `number` varchar(20) NOT NULL,
  `type` int(4) DEFAULT NULL,
  `status` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8304 DEFAULT CHARSET=utf8mb4;