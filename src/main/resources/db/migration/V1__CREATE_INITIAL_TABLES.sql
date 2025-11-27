/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;


CREATE TABLE IF NOT EXISTS `category`
(
    `id`   int(11)      NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `category_has_topic`
(
    `category_id` int(11) NOT NULL,
    `topic_id`    int(11) NOT NULL,
    PRIMARY KEY (`category_id`, `topic_id`),
    KEY `fk_category_has_topic_category1_idx` (`category_id`),
    KEY `fk_category_has_topic_topic1_idx` (`topic_id`),
    CONSTRAINT `fk_category_has_topic_category1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_category_has_topic_topic1` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `comments`
(
    `id`         int(11)   NOT NULL AUTO_INCREMENT,
    `topic_id`   int(11)   NOT NULL,
    `user_id`    int(11)   NOT NULL,
    `content`    longtext  NOT NULL,
    `created_at` timestamp NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`id`),
    KEY `fk_topic_has_user_topic2_idx` (`topic_id`),
    KEY `fk_topic_has_user_user2_idx` (`user_id`),
    CONSTRAINT `fk_topic_has_user_topic2` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_topic_has_user_user2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `likes`
(
    `topic_id` int(11) NOT NULL,
    `user_id`  int(11) NOT NULL,
    PRIMARY KEY (`topic_id`, `user_id`),
    KEY `fk_topic_has_user_topic1_idx` (`topic_id`),
    KEY `fk_topic_has_user_user1_idx` (`user_id`),
    CONSTRAINT `fk_topic_has_user_topic1` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_topic_has_user_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `profile`
(
    `user_id` int(11)      NOT NULL,
    `name`    varchar(255) NOT NULL,
    `photo`   longtext DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    CONSTRAINT `fk_profile_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `topic`
(
    `id`         int(11)      NOT NULL AUTO_INCREMENT,
    `title`      varchar(255) NOT NULL,
    `content`    longtext     NOT NULL,
    `user_id`    int(11)      NOT NULL,
    `created_at` timestamp    NULL DEFAULT current_timestamp(),
    `updated_at` timestamp    NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`id`),
    KEY `fk_topic_user_idx` (`user_id`),
    CONSTRAINT `fk_topic_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `user`
(
    `id`         int(11)     NOT NULL AUTO_INCREMENT,
    `email`      varchar(255)     DEFAULT NULL,
    `password`   varchar(64) NOT NULL,
    `created_at` timestamp   NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`id`),
    UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

/*!40103 SET TIME_ZONE = IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE = IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS = IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES = IFNULL(@OLD_SQL_NOTES, 1) */;
