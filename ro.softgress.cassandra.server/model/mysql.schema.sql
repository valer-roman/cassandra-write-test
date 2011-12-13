CREATE DATABASE `demo` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;

USE `demo`;

CREATE TABLE IF NOT EXISTS `metrics` (
  `timestamp` bigint(11) NOT NULL,
  `application_id` int(4) NOT NULL,
  `system_id` int(4) NOT NULL,
  `value` varchar(100) NOT NULL,
  KEY `timestamp` (`timestamp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;