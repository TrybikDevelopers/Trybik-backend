-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: db
-- Generation Time: Lis 16, 2025 at 01:29 PM
-- Wersja serwera: 9.4.0
-- Wersja PHP: 8.2.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `pktt`
--
CREATE DATABASE IF NOT EXISTS `pktt` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `pktt`;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `admin_keys`
--

DROP TABLE IF EXISTS `admin_keys`;
CREATE TABLE `admin_keys` (
  `key_id` int NOT NULL,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `admin_keys`
--

INSERT INTO `admin_keys` (`key_id`, `value`, `description`) VALUES
(6, '$2a$10$AF/3/7aVlFk4Ypqk7Te/uuLGhtXPmrkESNn3.kzcCoRDW8FRGBRu2', 'mikolaj'),
(8, '$2a$10$EQU7/.muQM/e1aZtw.FVK.UAk/4SsRGeUIzLSsplrPi/JnAYHF8V2', 'patryk');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `api_keys`
--

DROP TABLE IF EXISTS `api_keys`;
CREATE TABLE `api_keys` (
  `key_id` int NOT NULL,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `api_keys`
--

INSERT INTO `api_keys` (`key_id`, `value`, `description`) VALUES
(4, '$2a$10$uUvJtEEewxJsdUvI5kE0Iuvcv8MeixlfMML.Jx0XicXKT2AtMHP32', 'mobile app'),
(5, '$2a$10$VuoisZPoCWNBXtdQEEGQXO.T4SK1mQGXeA6JSM1KW4MUQ.JSuy7C2', 'web app');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `bug_reports`
--

DROP TABLE IF EXISTS `bug_reports`;
CREATE TABLE `bug_reports` (
  `report_id` int NOT NULL,
  `user_groups` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `issued_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `events`
--

DROP TABLE IF EXISTS `events`;
CREATE TABLE `events` (
  `event_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` int NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `events_superior_group`
--

DROP TABLE IF EXISTS `events_superior_group`;
CREATE TABLE `events_superior_group` (
  `row_id` int NOT NULL,
  `event_id` int NOT NULL,
  `superior_group_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `event_types`
--

DROP TABLE IF EXISTS `event_types`;
CREATE TABLE `event_types` (
  `event_type_id` int NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Zrzut danych tabeli `event_types`
--

INSERT INTO `event_types` (`event_type_id`, `name`) VALUES
(2, 'Academic calendar'),
(3, 'Day off');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exams`
--

DROP TABLE IF EXISTS `exams`;
CREATE TABLE `exams` (
  `exam_id` int NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `exam_date` datetime NOT NULL,
  `exam_type_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exams_groups`
--

DROP TABLE IF EXISTS `exams_groups`;
CREATE TABLE `exams_groups` (
  `exam_group_id` int NOT NULL,
  `exam_id` int NOT NULL,
  `group_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exam_types`
--

DROP TABLE IF EXISTS `exam_types`;
CREATE TABLE `exam_types` (
  `exam_type_id` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `exam_types`
--

INSERT INTO `exam_types` (`exam_type_id`, `name`) VALUES
(1, 'Kolokwium'),
(2, 'Egzamin końcowy'),
(3, 'Projekt');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `moderators`
--

DROP TABLE IF EXISTS `moderators`;
CREATE TABLE `moderators` (
  `moderator_id` varchar(36) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Zrzut danych tabeli `moderators`
--

INSERT INTO `moderators` (`moderator_id`, `password`, `role`) VALUES
('20caa1cc-4897-471d-a7cf-aa763d569b2e', '$2a$10$DGguCtLbZXE1gj6P2uns8OLNmB5s3ok50RZTBNMkVhgpLreU5/1um', 'MODERATOR');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `moderator_refresh_tokens`
--

DROP TABLE IF EXISTS `moderator_refresh_tokens`;
CREATE TABLE `moderator_refresh_tokens` (
  `token_id` bigint NOT NULL,
  `token` char(64) NOT NULL,
  `moderator_id` varchar(36) NOT NULL,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `refresh_token`
--

DROP TABLE IF EXISTS `refresh_token`;
CREATE TABLE `refresh_token` (
  `token_id` bigint NOT NULL,
  `token` char(64) NOT NULL,
  `user_id` int NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `representatives`
--

DROP TABLE IF EXISTS `representatives`;
CREATE TABLE `representatives` (
  `representative_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `superior_group_id` int NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `student_codes`
--

DROP TABLE IF EXISTS `student_codes`;
CREATE TABLE `student_codes` (
  `student_code_id` int NOT NULL,
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `expire` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `superior_group_id` int NOT NULL,
  `usage_count` int NOT NULL,
  `usage_limit` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `student_groups`
--

DROP TABLE IF EXISTS `student_groups`;
CREATE TABLE `student_groups` (
  `group_id` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `superior_groups`
--

DROP TABLE IF EXISTS `superior_groups`;
CREATE TABLE `superior_groups` (
  `superior_group_id` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `user_refresh_tokens`
--

DROP TABLE IF EXISTS `user_refresh_tokens`;
CREATE TABLE `user_refresh_tokens` (
  `token_id` bigint NOT NULL,
  `token` char(64) NOT NULL,
  `representative_id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `utils_kv`
--

DROP TABLE IF EXISTS `utils_kv`;
CREATE TABLE `utils_kv` (
  `id` int NOT NULL,
  `property_key` varchar(191) NOT NULL,
  `property_value` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `value_type` varchar(20) NOT NULL DEFAULT 'string',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Zrzut danych tabeli `utils_kv`
--

INSERT INTO `utils_kv` (`id`, `property_key`, `property_value`, `value_type`, `updated_at`) VALUES
(16, 'endOfSemester', '2026-02-28', 'date', '2025-10-20 18:26:50');

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `admin_keys`
--
ALTER TABLE `admin_keys`
  ADD PRIMARY KEY (`key_id`),
  ADD UNIQUE KEY `unique_value` (`value`);

--
-- Indeksy dla tabeli `api_keys`
--
ALTER TABLE `api_keys`
  ADD PRIMARY KEY (`key_id`),
  ADD UNIQUE KEY `unique_value` (`value`);

--
-- Indeksy dla tabeli `bug_reports`
--
ALTER TABLE `bug_reports`
  ADD PRIMARY KEY (`report_id`);

--
-- Indeksy dla tabeli `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`event_id`),
  ADD KEY `events_types` (`type`) USING BTREE;

--
-- Indeksy dla tabeli `events_superior_group`
--
ALTER TABLE `events_superior_group`
  ADD PRIMARY KEY (`row_id`),
  ADD KEY `index_superior_group` (`superior_group_id`) USING BTREE,
  ADD KEY `index_event` (`event_id`);

--
-- Indeksy dla tabeli `event_types`
--
ALTER TABLE `event_types`
  ADD PRIMARY KEY (`event_type_id`);

--
-- Indeksy dla tabeli `exams`
--
ALTER TABLE `exams`
  ADD PRIMARY KEY (`exam_id`),
  ADD KEY `exam_type_id_idx` (`exam_type_id`);

--
-- Indeksy dla tabeli `exams_groups`
--
ALTER TABLE `exams_groups`
  ADD PRIMARY KEY (`exam_group_id`),
  ADD KEY `exam_id_idx` (`exam_id`),
  ADD KEY `group_id_idx` (`group_id`);

--
-- Indeksy dla tabeli `exam_types`
--
ALTER TABLE `exam_types`
  ADD PRIMARY KEY (`exam_type_id`);

--
-- Indeksy dla tabeli `moderators`
--
ALTER TABLE `moderators`
  ADD PRIMARY KEY (`moderator_id`);

--
-- Indeksy dla tabeli `moderator_refresh_tokens`
--
ALTER TABLE `moderator_refresh_tokens`
  ADD PRIMARY KEY (`token_id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `idx_moderator_id` (`moderator_id`);

--
-- Indeksy dla tabeli `representatives`
--
ALTER TABLE `representatives`
  ADD PRIMARY KEY (`representative_id`),
  ADD KEY `general_group_id_idx` (`superior_group_id`);

--
-- Indeksy dla tabeli `student_codes`
--
ALTER TABLE `student_codes`
  ADD PRIMARY KEY (`student_code_id`),
  ADD KEY `general_group_id_idx` (`superior_group_id`);

--
-- Indeksy dla tabeli `student_groups`
--
ALTER TABLE `student_groups`
  ADD PRIMARY KEY (`group_id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indeksy dla tabeli `superior_groups`
--
ALTER TABLE `superior_groups`
  ADD PRIMARY KEY (`superior_group_id`);

--
-- Indeksy dla tabeli `user_refresh_tokens`
--
ALTER TABLE `user_refresh_tokens`
  ADD PRIMARY KEY (`token_id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `idx_representative_id` (`representative_id`);

--
-- Indeksy dla tabeli `utils_kv`
--
ALTER TABLE `utils_kv`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `property_key` (`property_key`);

--
-- AUTO_INCREMENT dla zrzuconych tabel
--

--
-- AUTO_INCREMENT dla tabeli `admin_keys`
--
ALTER TABLE `admin_keys`
  MODIFY `key_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT dla tabeli `api_keys`
--
ALTER TABLE `api_keys`
  MODIFY `key_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT dla tabeli `bug_reports`
--
ALTER TABLE `bug_reports`
  MODIFY `report_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT dla tabeli `events`
--
ALTER TABLE `events`
  MODIFY `event_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT dla tabeli `events_superior_group`
--
ALTER TABLE `events_superior_group`
  MODIFY `row_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT dla tabeli `event_types`
--
ALTER TABLE `event_types`
  MODIFY `event_type_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT dla tabeli `exams`
--
ALTER TABLE `exams`
  MODIFY `exam_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT dla tabeli `exams_groups`
--
ALTER TABLE `exams_groups`
  MODIFY `exam_group_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT dla tabeli `exam_types`
--
ALTER TABLE `exam_types`
  MODIFY `exam_type_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT dla tabeli `moderator_refresh_tokens`
--
ALTER TABLE `moderator_refresh_tokens`
  MODIFY `token_id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT dla tabeli `student_codes`
--
ALTER TABLE `student_codes`
  MODIFY `student_code_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT dla tabeli `student_groups`
--
ALTER TABLE `student_groups`
  MODIFY `group_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT dla tabeli `superior_groups`
--
ALTER TABLE `superior_groups`
  MODIFY `superior_group_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT dla tabeli `user_refresh_tokens`
--
ALTER TABLE `user_refresh_tokens`
  MODIFY `token_id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT dla tabeli `utils_kv`
--
ALTER TABLE `utils_kv`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `events`
--
ALTER TABLE `events`
  ADD CONSTRAINT `events_ibfk_1` FOREIGN KEY (`type`) REFERENCES `event_types` (`event_type_id`);

--
-- Ograniczenia dla tabeli `events_superior_group`
--
ALTER TABLE `events_superior_group`
  ADD CONSTRAINT `events_superior_group_ibfk_1` FOREIGN KEY (`superior_group_id`) REFERENCES `superior_groups` (`superior_group_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `events_superior_group_ibfk_2` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Ograniczenia dla tabeli `exams`
--
ALTER TABLE `exams`
  ADD CONSTRAINT `exams_ibfk_1` FOREIGN KEY (`exam_type_id`) REFERENCES `exam_types` (`exam_type_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `exams_groups`
--
ALTER TABLE `exams_groups`
  ADD CONSTRAINT `exams_groups_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`exam_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `exams_groups_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `student_groups` (`group_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `moderator_refresh_tokens`
--
ALTER TABLE `moderator_refresh_tokens`
  ADD CONSTRAINT `moderator_refresh_tokens_ibfk_1` FOREIGN KEY (`moderator_id`) REFERENCES `moderators` (`moderator_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Ograniczenia dla tabeli `representatives`
--
ALTER TABLE `representatives`
  ADD CONSTRAINT `representatives_ibfk_1` FOREIGN KEY (`superior_group_id`) REFERENCES `superior_groups` (`superior_group_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `student_codes`
--
ALTER TABLE `student_codes`
  ADD CONSTRAINT `student_codes_ibfk_1` FOREIGN KEY (`superior_group_id`) REFERENCES `superior_groups` (`superior_group_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `user_refresh_tokens`
--
ALTER TABLE `user_refresh_tokens`
  ADD CONSTRAINT `user_refresh_tokens_ibfk_1` FOREIGN KEY (`representative_id`) REFERENCES `representatives` (`representative_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
