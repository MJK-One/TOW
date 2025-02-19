# database name = tow
CREATE SCHEMA IF NOT EXISTS `TOW` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE TOW;

#table > event_sites
CREATE TABLE `event_sites` (
  `id` int NOT NULL AUTO_INCREMENT,
  `event_id` int NOT NULL,
  `sales_site` varchar(50) NOT NULL,
  `detail_link` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `event_id` (`event_id`),
  CONSTRAINT `event_sites_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `tickets` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

#table > tickets
CREATE TABLE `tickets` (
  `id` int NOT NULL AUTO_INCREMENT,
  `event_name` varchar(255) NOT NULL,
  `registration_date` date NOT NULL,
  `ticket_open_date` datetime NOT NULL,
  `pre_sale_date` datetime DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `basic_info` text,
  `event_description` text,
  `agency_info` text,
  `genre` varchar(50) DEFAULT NULL,
  `event_start_date` date DEFAULT NULL,
  `event_end_date` date DEFAULT NULL,
  `venue` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `region` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=940 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#table > ticket_views (조회수 분리)
CREATE TABLE `ticket_views` (
  `id` int NOT NULL AUTO_INCREMENT,
  `view_cnt` int DEFAULT '0',
  `ticket_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ticket_views_id` (`ticket_id`),
  CONSTRAINT `fk_ticket_views_id` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#table > naver_user
CREATE TABLE `naver_user` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `age` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;