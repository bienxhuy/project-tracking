-- Sample Users Data Script
-- Password for all users: 123456 (hashed with BCrypt)
-- Hash: $2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S

-- Insert 50 sample users
INSERT INTO users (id, username, password, email, display_name, avatar, role, account_status, level, login_type, created_at, updated_at) VALUES
-- Admin users (2)
(1, 'sarah.admin', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'sarah.anderson@pose.edu', 'Sarah Anderson', NULL, 'ADMIN', 'ACTIVE', 10.0, 'LOCAL', NOW(), NOW()),
(2, 'david.admin', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'david.thompson@pose.edu', 'David Thompson', NULL, 'ADMIN', 'ACTIVE', 9.8, 'LOCAL', NOW(), NOW()),

-- Instructors (8)
(3, 'michael.roberts', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'michael.roberts@pose.edu', 'Dr. Michael Roberts', NULL, 'INSTRUCTOR', 'ACTIVE', 9.2, 'LOCAL', NOW(), NOW()),
(4, 'jennifer.martin', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'jennifer.martin@pose.edu', 'Prof. Jennifer Martin', NULL, 'INSTRUCTOR', 'ACTIVE', 9.5, 'LOCAL', NOW(), NOW()),
(5, 'james.wilson', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'james.wilson@pose.edu', 'Dr. James Wilson', NULL, 'INSTRUCTOR', 'ACTIVE', 8.9, 'LOCAL', NOW(), NOW()),
(6, 'emily.davis', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'emily.davis@pose.edu', 'Dr. Emily Davis', NULL, 'INSTRUCTOR', 'ACTIVE', 9.1, 'LOCAL', NOW(), NOW()),
(7, 'robert.garcia', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'robert.garcia@pose.edu', 'Prof. Robert Garcia', NULL, 'INSTRUCTOR', 'ACTIVE', 8.7, 'LOCAL', NOW(), NOW()),
(8, 'linda.martinez', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'linda.martinez@pose.edu', 'Dr. Linda Martinez', NULL, 'INSTRUCTOR', 'ACTIVE', 9.3, 'LOCAL', NOW(), NOW()),
(9, 'william.brown', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'william.brown@pose.edu', 'Dr. William Brown', NULL, 'INSTRUCTOR', 'ACTIVE', 8.8, 'LOCAL', NOW(), NOW()),
(10, 'patricia.johnson', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'patricia.johnson@pose.edu', 'Prof. Patricia Johnson', NULL, 'INSTRUCTOR', 'ACTIVE', 9.0, 'LOCAL', NOW(), NOW()),

-- Students (40)
(11, 'alexander.nguyen', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'alexander.nguyen@student.pose.edu', 'Alexander Nguyen', NULL, 'STUDENT', 'ACTIVE', 8.5, 'LOCAL', NOW(), NOW()),
(12, 'sophia.patel', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'sophia.patel@student.pose.edu', 'Sophia Patel', NULL, 'STUDENT', 'ACTIVE', 7.9, 'LOCAL', NOW(), NOW()),
(13, 'ethan.kim', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'ethan.kim@student.pose.edu', 'Ethan Kim', NULL, 'STUDENT', 'ACTIVE', 8.2, 'LOCAL', NOW(), NOW()),
(14, 'olivia.chen', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'olivia.chen@student.pose.edu', 'Olivia Chen', NULL, 'STUDENT', 'INACTIVE', 8.8, 'LOCAL', NOW(), NOW()),
(15, 'noah.singh', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'noah.singh@student.pose.edu', 'Noah Singh', NULL, 'STUDENT', 'ACTIVE', 7.4, 'LOCAL', NOW(), NOW()),
(16, 'emma.rodriguez', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'emma.rodriguez@student.pose.edu', 'Emma Rodriguez', NULL, 'STUDENT', 'INACTIVE', 8.1, 'LOCAL', NOW(), NOW()),
(17, 'liam.lee', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'liam.lee@student.pose.edu', 'Liam Lee', NULL, 'STUDENT', 'ACTIVE', 7.7, 'LOCAL', NOW(), NOW()),
(18, 'ava.jackson', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'ava.jackson@student.pose.edu', 'Ava Jackson', NULL, 'STUDENT', 'ACTIVE', 8.4, 'LOCAL', NOW(), NOW()),
(19, 'lucas.taylor', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'lucas.taylor@student.pose.edu', 'Lucas Taylor', NULL, 'STUDENT', 'INACTIVE', 7.2, 'LOCAL', NOW(), NOW()),
(20, 'isabella.white', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'isabella.white@student.pose.edu', 'Isabella White', NULL, 'STUDENT', 'INACTIVE', 8.6, 'LOCAL', NOW(), NOW()),
(21, 'mason.harris', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'mason.harris@student.pose.edu', 'Mason Harris', NULL, 'STUDENT', 'INACTIVE', 7.5, 'LOCAL', NOW(), NOW()),
(22, 'mia.thompson', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'mia.thompson@student.pose.edu', 'Mia Thompson', NULL, 'STUDENT', 'ACTIVE', 8.3, 'LOCAL', NOW(), NOW()),
(23, 'jacob.anderson', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'jacob.anderson@student.pose.edu', 'Jacob Anderson', NULL, 'STUDENT', 'ACTIVE', 7.8, 'LOCAL', NOW(), NOW()),
(24, 'charlotte.moore', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'charlotte.moore@student.pose.edu', 'Charlotte Moore', NULL, 'STUDENT', 'ACTIVE', 8.7, 'LOCAL', NOW(), NOW()),
(25, 'daniel.clark', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'daniel.clark@student.pose.edu', 'Daniel Clark', NULL, 'STUDENT', 'ACTIVE', 7.3, 'LOCAL', NOW(), NOW()),
(26, 'amelia.lopez', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'amelia.lopez@student.pose.edu', 'Amelia Lopez', NULL, 'STUDENT', 'ACTIVE', 8.0, 'LOCAL', NOW(), NOW()),
(27, 'matthew.gonzalez', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'matthew.gonzalez@student.pose.edu', 'Matthew Gonzalez', NULL, 'STUDENT', 'ACTIVE', 7.6, 'LOCAL', NOW(), NOW()),
(28, 'harper.hill', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'harper.hill@student.pose.edu', 'Harper Hill', NULL, 'STUDENT', 'ACTIVE', 8.5, 'LOCAL', NOW(), NOW()),
(29, 'benjamin.wright', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'benjamin.wright@student.pose.edu', 'Benjamin Wright', NULL, 'STUDENT', 'ACTIVE', 7.1, 'LOCAL', NOW(), NOW()),
(30, 'evelyn.scott', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'evelyn.scott@student.pose.edu', 'Evelyn Scott', NULL, 'STUDENT', 'ACTIVE', 8.4, 'LOCAL', NOW(), NOW()),
(31, 'logan.green', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'logan.green@student.pose.edu', 'Logan Green', NULL, 'STUDENT', 'ACTIVE', 7.9, 'LOCAL', NOW(), NOW()),
(32, 'abigail.adams', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'abigail.adams@student.pose.edu', 'Abigail Adams', NULL, 'STUDENT', 'ACTIVE', 8.2, 'LOCAL', NOW(), NOW()),
(33, 'jackson.baker', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'jackson.baker@student.pose.edu', 'Jackson Baker', NULL, 'STUDENT', 'ACTIVE', 7.4, 'LOCAL', NOW(), NOW()),
(34, 'ella.nelson', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'ella.nelson@student.pose.edu', 'Ella Nelson', NULL, 'STUDENT', 'ACTIVE', 8.6, 'LOCAL', NOW(), NOW()),
(35, 'sebastian.carter', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'sebastian.carter@student.pose.edu', 'Sebastian Carter', NULL, 'STUDENT', 'ACTIVE', 7.7, 'LOCAL', NOW(), NOW()),
(36, 'scarlett.mitchell', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'scarlett.mitchell@student.pose.edu', 'Scarlett Mitchell', NULL, 'STUDENT', 'ACTIVE', 8.3, 'LOCAL', NOW(), NOW()),
(37, 'aiden.perez', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'aiden.perez@student.pose.edu', 'Aiden Perez', NULL, 'STUDENT', 'ACTIVE', 7.5, 'LOCAL', NOW(), NOW()),
(38, 'victoria.roberts', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'victoria.roberts@student.pose.edu', 'Victoria Roberts', NULL, 'STUDENT', 'ACTIVE', 8.8, 'LOCAL', NOW(), NOW()),
(39, 'carter.turner', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'carter.turner@student.pose.edu', 'Carter Turner', NULL, 'STUDENT', 'ACTIVE', 7.2, 'LOCAL', NOW(), NOW()),
(40, 'grace.phillips', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'grace.phillips@student.pose.edu', 'Grace Phillips', NULL, 'STUDENT', 'ACTIVE', 8.1, 'LOCAL', NOW(), NOW()),
(41, 'wyatt.campbell', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'wyatt.campbell@student.pose.edu', 'Wyatt Campbell', NULL, 'STUDENT', 'ACTIVE', 7.6, 'LOCAL', NOW(), NOW()),
(42, 'chloe.parker', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'chloe.parker@student.pose.edu', 'Chloe Parker', NULL, 'STUDENT', 'ACTIVE', 8.4, 'LOCAL', NOW(), NOW()),
(43, 'jack.evans', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'jack.evans@student.pose.edu', 'Jack Evans', NULL, 'STUDENT', 'ACTIVE', 7.3, 'LOCAL', NOW(), NOW()),
(44, 'zoey.edwards', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'zoey.edwards@student.pose.edu', 'Zoey Edwards', NULL, 'STUDENT', 'ACTIVE', 8.7, 'LOCAL', NOW(), NOW()),
(45, 'owen.collins', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'owen.collins@student.pose.edu', 'Owen Collins', NULL, 'STUDENT', 'ACTIVE', 7.8, 'LOCAL', NOW(), NOW()),
(46, 'lily.stewart', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'lily.stewart@student.pose.edu', 'Lily Stewart', NULL, 'STUDENT', 'ACTIVE', 8.5, 'LOCAL', NOW(), NOW()),
(47, 'grayson.sanchez', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'grayson.sanchez@student.pose.edu', 'Grayson Sanchez', NULL, 'STUDENT', 'ACTIVE', 7.4, 'LOCAL', NOW(), NOW()),
(48, 'hannah.morris', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'hannah.morris@student.pose.edu', 'Hannah Morris', NULL, 'STUDENT', 'ACTIVE', 8.2, 'LOCAL', NOW(), NOW()),
(49, 'luke.ramirez', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'luke.ramirez@student.pose.edu', 'Luke Ramirez', NULL, 'STUDENT', 'INACTIVE', 6.5, 'LOCAL', NOW(), NOW()),
(50, 'aria.james', '$2a$12$Iy6.4cKcgUtFOMeUGRTuw.IPoVuBvPS2e8a4qbz/mzdB7chDFbJ2S', 'aria.james@student.pose.edu', 'Aria James', NULL, 'STUDENT', 'INACTIVE', 6.2, 'LOCAL', NOW(), NOW());
