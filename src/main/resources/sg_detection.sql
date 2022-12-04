SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

CREATE DATABASE IF NOT EXISTS `sg_detection` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `sg_detection`;

DROP TABLE IF EXISTS `data_item`;
CREATE TABLE IF NOT EXISTS `data_item` (
    `id` varchar(50) NOT NULL,
    `image_name` varchar(200) NOT NULL,
    `image_2_name` varchar(200) DEFAULT NULL,
    `label_name` varchar(200) NOT NULL,
    `sample_id` varchar(50) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `data_item_ibfk_1` (`sample_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `mission`;
CREATE TABLE IF NOT EXISTS `mission` (
    `id` varchar(50) NOT NULL,
    `name` varchar(200) NOT NULL,
    `image_name` varchar(200) NOT NULL,
    `image_2_name` varchar(200) DEFAULT NULL,
    `label_name` varchar(200) NOT NULL,
    `finish` tinyint(1) NOT NULL,
    `project_id` varchar(50) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `project_id` (`project_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `model`;
CREATE TABLE IF NOT EXISTS `model` (
    `id` varchar(50) NOT NULL,
    `name` varchar(200) NOT NULL,
    `version` varchar(50) NOT NULL,
    `description` varchar(1000) DEFAULT NULL,
    `sample_id` varchar(50) NOT NULL,
    `chip_size` int(11) NOT NULL DEFAULT '300',
    `epoch` int(11) NOT NULL DEFAULT '50',
    `learning_rate` decimal(10,10) NOT NULL DEFAULT '0.00001',
    `batch_size` int(11) NOT NULL DEFAULT '8',
    `model` varchar(50) NOT NULL DEFAULT 'fcn_resnet50',
    `backbone` varchar(50) NOT NULL DEFAULT 'resnet50',
    PRIMARY KEY (`id`),
    KEY `sample_id` (`sample_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `project`;
CREATE TABLE IF NOT EXISTS `project` (
    `id` varchar(50) NOT NULL,
    `name` varchar(200) NOT NULL,
    `type` varchar(20) NOT NULL,
    `clip_size` int(11) NOT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `sample`;
CREATE TABLE IF NOT EXISTS `sample` (
    `id` varchar(50) NOT NULL,
    `name` varchar(200) NOT NULL,
    `type` varchar(20) NOT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `data_item`
    ADD CONSTRAINT `data_item_ibfk_1` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `mission`
    ADD CONSTRAINT `mission_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `model`
    ADD CONSTRAINT `model_ibfk_1` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
