-- Sample Tasks, Reports, and Comments Data Script
-- This script populates tasks, task_assignees, project_members, reports, and comments tables
-- IMPORTANT: Run sample_users.sql first before running this script
--
-- NOTES:
-- 1. Tasks use Many-to-Many relationship with Users via task_assignees junction table
-- 2. Reports belong to tasks and are submitted by project members
-- 3. Comments can be on tasks or reports
-- 4. All dates are realistic and follow project/milestone timeline
-- 5. Project members must be added before creating tasks/reports

-- ==========================================
-- PROJECT MEMBERS
-- ==========================================
-- Project 1001: PoseCare Telehealth Platform (Instructor: 3, Students: 11, 12, 13)
INSERT INTO project_members (id, project_id, user_id, role, joined_at, is_active, created_at, updated_at) VALUES
(3001, 1001, 3, 'INSTRUCTOR', '2025-01-08 09:00:00', TRUE, NOW(), NOW()),
(3002, 1001, 11, 'STUDENT', '2025-01-08 09:30:00', TRUE, NOW(), NOW()),
(3003, 1001, 12, 'STUDENT', '2025-01-08 09:30:00', TRUE, NOW(), NOW()),
(3004, 1001, 13, 'STUDENT', '2025-01-08 09:30:00', TRUE, NOW(), NOW()),

-- Project 1002: Campus Navigator AR (Instructor: 4, Students: 15, 17, 18)
(3005, 1002, 4, 'INSTRUCTOR', '2025-01-15 09:00:00', TRUE, NOW(), NOW()),
(3006, 1002, 15, 'STUDENT', '2025-01-15 09:30:00', TRUE, NOW(), NOW()),
(3007, 1002, 17, 'STUDENT', '2025-01-15 09:30:00', TRUE, NOW(), NOW()),
(3008, 1002, 18, 'STUDENT', '2025-01-15 09:30:00', TRUE, NOW(), NOW()),

-- Project 1003: Smart Studio Scheduler (Instructor: 5, Students: 22, 23, 24)
(3009, 1003, 5, 'INSTRUCTOR', '2025-01-20 09:00:00', TRUE, NOW(), NOW()),
(3010, 1003, 22, 'STUDENT', '2025-01-20 09:30:00', TRUE, NOW(), NOW()),
(3011, 1003, 23, 'STUDENT', '2025-01-20 09:30:00', TRUE, NOW(), NOW()),
(3012, 1003, 24, 'STUDENT', '2025-01-20 09:30:00', TRUE, NOW(), NOW()),

-- Project 1004: AI Posture Coach (Instructor: 6, Students: 25, 26, 27)
(3013, 1004, 6, 'INSTRUCTOR', '2025-01-25 09:00:00', TRUE, NOW(), NOW()),
(3014, 1004, 25, 'STUDENT', '2025-01-25 09:30:00', TRUE, NOW(), NOW()),
(3015, 1004, 26, 'STUDENT', '2025-01-25 09:30:00', TRUE, NOW(), NOW()),
(3016, 1004, 27, 'STUDENT', '2025-01-25 09:30:00', TRUE, NOW(), NOW()),

-- Project 1005: Virtual Lab Companion (Instructor: 7, Students: 28, 29, 30)
(3017, 1005, 7, 'INSTRUCTOR', '2025-02-01 09:00:00', TRUE, NOW(), NOW()),
(3018, 1005, 28, 'STUDENT', '2025-02-01 09:30:00', TRUE, NOW(), NOW()),
(3019, 1005, 29, 'STUDENT', '2025-02-01 09:30:00', TRUE, NOW(), NOW()),
(3020, 1005, 30, 'STUDENT', '2025-02-01 09:30:00', TRUE, NOW(), NOW()),

-- Project 1006: Sustainability Tracker (Instructor: 8, Students: 31, 32, 33) - COMPLETED
(3021, 1006, 8, 'INSTRUCTOR', '2024-09-05 09:00:00', TRUE, NOW(), NOW()),
(3022, 1006, 31, 'STUDENT', '2024-09-05 09:30:00', TRUE, NOW(), NOW()),
(3023, 1006, 32, 'STUDENT', '2024-09-05 09:30:00', TRUE, NOW(), NOW()),
(3024, 1006, 33, 'STUDENT', '2024-09-05 09:30:00', TRUE, NOW(), NOW()),

-- Project 1007: Motion Capture Analytics (Instructor: 9, Students: 34, 35, 36)
(3025, 1007, 9, 'INSTRUCTOR', '2024-09-10 09:00:00', TRUE, NOW(), NOW()),
(3026, 1007, 34, 'STUDENT', '2024-09-10 09:30:00', TRUE, NOW(), NOW()),
(3027, 1007, 35, 'STUDENT', '2024-09-10 09:30:00', TRUE, NOW(), NOW()),
(3028, 1007, 36, 'STUDENT', '2024-09-10 09:30:00', TRUE, NOW(), NOW()),

-- Project 1008: Learning Journey Hub (Instructor: 10, Students: 37, 38, 39)
(3029, 1008, 10, 'INSTRUCTOR', '2024-09-15 09:00:00', TRUE, NOW(), NOW()),
(3030, 1008, 37, 'STUDENT', '2024-09-15 09:30:00', TRUE, NOW(), NOW()),
(3031, 1008, 38, 'STUDENT', '2024-09-15 09:30:00', TRUE, NOW(), NOW()),
(3032, 1008, 39, 'STUDENT', '2024-09-15 09:30:00', TRUE, NOW(), NOW()),

-- Project 1009: Pose Robotics Interface (Instructor: 3, Students: 40, 41, 42)
(3033, 1009, 3, 'INSTRUCTOR', '2024-09-20 09:00:00', TRUE, NOW(), NOW()),
(3034, 1009, 40, 'STUDENT', '2024-09-20 09:30:00', TRUE, NOW(), NOW()),
(3035, 1009, 41, 'STUDENT', '2024-09-20 09:30:00', TRUE, NOW(), NOW()),
(3036, 1009, 42, 'STUDENT', '2024-09-20 09:30:00', TRUE, NOW(), NOW()),

-- Project 1010: Research Paper CoPilot (Instructor: 4, Students: 43, 44, 45)
(3037, 1010, 4, 'INSTRUCTOR', '2024-10-01 09:00:00', TRUE, NOW(), NOW()),
(3038, 1010, 43, 'STUDENT', '2024-10-01 09:30:00', TRUE, NOW(), NOW()),
(3039, 1010, 44, 'STUDENT', '2024-10-01 09:30:00', TRUE, NOW(), NOW()),
(3040, 1010, 45, 'STUDENT', '2024-10-01 09:30:00', TRUE, NOW(), NOW()),

-- Project 1011: Fitness Gamification Portal (Instructor: 5, Students: 46, 47, 48) - COMPLETED
(3041, 1011, 5, 'INSTRUCTOR', '2024-10-05 09:00:00', TRUE, NOW(), NOW()),
(3042, 1011, 46, 'STUDENT', '2024-10-05 09:30:00', TRUE, NOW(), NOW()),
(3043, 1011, 47, 'STUDENT', '2024-10-05 09:30:00', TRUE, NOW(), NOW()),
(3044, 1011, 48, 'STUDENT', '2024-10-05 09:30:00', TRUE, NOW(), NOW()),

-- Project 1012: Accessibility Insight Dashboard (Instructor: 6, Students: 11, 14, 16)
(3045, 1012, 6, 'INSTRUCTOR', '2024-10-10 09:00:00', TRUE, NOW(), NOW()),
(3046, 1012, 11, 'STUDENT', '2024-10-10 09:30:00', TRUE, NOW(), NOW()),
(3047, 1012, 14, 'STUDENT', '2024-10-10 09:30:00', TRUE, NOW(), NOW()),
(3048, 1012, 16, 'STUDENT', '2024-10-10 09:30:00', TRUE, NOW(), NOW()),

-- Project 1013: Smart Attendance Vision (Instructor: 7, Students: 12, 15, 19)
(3049, 1013, 7, 'INSTRUCTOR', '2024-10-15 09:00:00', TRUE, NOW(), NOW()),
(3050, 1013, 12, 'STUDENT', '2024-10-15 09:30:00', TRUE, NOW(), NOW()),
(3051, 1013, 15, 'STUDENT', '2024-10-15 09:30:00', TRUE, NOW(), NOW()),
(3052, 1013, 19, 'STUDENT', '2024-10-15 09:30:00', TRUE, NOW(), NOW()),

-- Project 1014: Peer Mentorship Hub (Instructor: 8, Students: 13, 17, 20)
(3053, 1014, 8, 'INSTRUCTOR', '2024-10-20 09:00:00', TRUE, NOW(), NOW()),
(3054, 1014, 13, 'STUDENT', '2024-10-20 09:30:00', TRUE, NOW(), NOW()),
(3055, 1014, 17, 'STUDENT', '2024-10-20 09:30:00', TRUE, NOW(), NOW()),
(3056, 1014, 20, 'STUDENT', '2024-10-20 09:30:00', TRUE, NOW(), NOW()),

-- Project 1015: Wellness Pulse Monitor (Instructor: 9, Students: 18, 21, 22)
(3057, 1015, 9, 'INSTRUCTOR', '2024-11-01 09:00:00', TRUE, NOW(), NOW()),
(3058, 1015, 18, 'STUDENT', '2024-11-01 09:30:00', TRUE, NOW(), NOW()),
(3059, 1015, 21, 'STUDENT', '2024-11-01 09:30:00', TRUE, NOW(), NOW()),
(3060, 1015, 22, 'STUDENT', '2024-11-01 09:30:00', TRUE, NOW(), NOW()),

-- Project 1016: Collaborative Whiteboard AI (Instructor: 10, Students: 23, 24, 25) - LOCKED
(3061, 1016, 10, 'INSTRUCTOR', '2024-11-05 09:00:00', TRUE, NOW(), NOW()),
(3062, 1016, 23, 'STUDENT', '2024-11-05 09:30:00', TRUE, NOW(), NOW()),
(3063, 1016, 24, 'STUDENT', '2024-11-05 09:30:00', TRUE, NOW(), NOW()),
(3064, 1016, 25, 'STUDENT', '2024-11-05 09:30:00', TRUE, NOW(), NOW()),

-- Project 1017: Capstone Portfolio Vault (Instructor: 3, Students: 26, 27, 28)
(3065, 1017, 3, 'INSTRUCTOR', '2024-11-10 09:00:00', TRUE, NOW(), NOW()),
(3066, 1017, 26, 'STUDENT', '2024-11-10 09:30:00', TRUE, NOW(), NOW()),
(3067, 1017, 27, 'STUDENT', '2024-11-10 09:30:00', TRUE, NOW(), NOW()),
(3068, 1017, 28, 'STUDENT', '2024-11-10 09:30:00', TRUE, NOW(), NOW()),

-- Project 1018: Lab Inventory Assistant (Instructor: 4, Students: 29, 30, 31)
(3069, 1018, 4, 'INSTRUCTOR', '2024-11-15 09:00:00', TRUE, NOW(), NOW()),
(3070, 1018, 29, 'STUDENT', '2024-11-15 09:30:00', TRUE, NOW(), NOW()),
(3071, 1018, 30, 'STUDENT', '2024-11-15 09:30:00', TRUE, NOW(), NOW()),
(3072, 1018, 31, 'STUDENT', '2024-11-15 09:30:00', TRUE, NOW(), NOW()),

-- Project 1019: AI Feedback Studio (Instructor: 5, Students: 32, 33, 34)
(3073, 1019, 5, 'INSTRUCTOR', '2024-11-20 09:00:00', TRUE, NOW(), NOW()),
(3074, 1019, 32, 'STUDENT', '2024-11-20 09:30:00', TRUE, NOW(), NOW()),
(3075, 1019, 33, 'STUDENT', '2024-11-20 09:30:00', TRUE, NOW(), NOW()),
(3076, 1019, 34, 'STUDENT', '2024-11-20 09:30:00', TRUE, NOW(), NOW()),

-- Project 1020: Internship Matching Engine (Instructor: 6, Students: 35, 36, 37)
(3077, 1020, 6, 'INSTRUCTOR', '2024-12-01 09:00:00', TRUE, NOW(), NOW()),
(3078, 1020, 35, 'STUDENT', '2024-12-01 09:30:00', TRUE, NOW(), NOW()),
(3079, 1020, 36, 'STUDENT', '2024-12-01 09:30:00', TRUE, NOW(), NOW()),
(3080, 1020, 37, 'STUDENT', '2024-12-01 09:30:00', TRUE, NOW(), NOW()),

-- Project 1021: Digital Campus Library System (Instructor: 3, Students: 38, 39, 40)
(3081, 1021, 3, 'INSTRUCTOR', '2025-02-10 09:00:00', TRUE, NOW(), NOW()),
(3082, 1021, 38, 'STUDENT', '2025-02-10 09:30:00', TRUE, NOW(), NOW()),
(3083, 1021, 39, 'STUDENT', '2025-02-10 09:30:00', TRUE, NOW(), NOW()),
(3084, 1021, 40, 'STUDENT', '2025-02-10 09:30:00', TRUE, NOW(), NOW()),

-- Project 1022: Smart Parking Management (Instructor: 4, Students: 41, 42, 43)
(3085, 1022, 4, 'INSTRUCTOR', '2025-02-12 09:00:00', TRUE, NOW(), NOW()),
(3086, 1022, 41, 'STUDENT', '2025-02-12 09:30:00', TRUE, NOW(), NOW()),
(3087, 1022, 42, 'STUDENT', '2025-02-12 09:30:00', TRUE, NOW(), NOW()),
(3088, 1022, 43, 'STUDENT', '2025-02-12 09:30:00', TRUE, NOW(), NOW()),

-- Project 1023: Virtual Classroom Platform (Instructor: 5, Students: 44, 45, 46)
(3089, 1023, 5, 'INSTRUCTOR', '2025-02-15 09:00:00', TRUE, NOW(), NOW()),
(3090, 1023, 44, 'STUDENT', '2025-02-15 09:30:00', TRUE, NOW(), NOW()),
(3091, 1023, 45, 'STUDENT', '2025-02-15 09:30:00', TRUE, NOW(), NOW()),
(3092, 1023, 46, 'STUDENT', '2025-02-15 09:30:00', TRUE, NOW(), NOW()),

-- Project 1024: Campus Event Management App (Instructor: 6, Students: 47, 48, 11)
(3093, 1024, 6, 'INSTRUCTOR', '2025-02-18 09:00:00', TRUE, NOW(), NOW()),
(3094, 1024, 47, 'STUDENT', '2025-02-18 09:30:00', TRUE, NOW(), NOW()),
(3095, 1024, 48, 'STUDENT', '2025-02-18 09:30:00', TRUE, NOW(), NOW()),
(3096, 1024, 11, 'STUDENT', '2025-02-18 09:30:00', TRUE, NOW(), NOW()),

-- Project 1025: Food Waste Reduction Tracker (Instructor: 7, Students: 12, 13, 15)
(3097, 1025, 7, 'INSTRUCTOR', '2025-02-20 09:00:00', TRUE, NOW(), NOW()),
(3098, 1025, 12, 'STUDENT', '2025-02-20 09:30:00', TRUE, NOW(), NOW()),
(3099, 1025, 13, 'STUDENT', '2025-02-20 09:30:00', TRUE, NOW(), NOW()),
(3100, 1025, 15, 'STUDENT', '2025-02-20 09:30:00', TRUE, NOW(), NOW()),

-- Project 1026: Student Mental Health Portal (Instructor: 8, Students: 17, 18, 22)
(3101, 1026, 8, 'INSTRUCTOR', '2025-02-22 09:00:00', TRUE, NOW(), NOW()),
(3102, 1026, 17, 'STUDENT', '2025-02-22 09:30:00', TRUE, NOW(), NOW()),
(3103, 1026, 18, 'STUDENT', '2025-02-22 09:30:00', TRUE, NOW(), NOW()),
(3104, 1026, 22, 'STUDENT', '2025-02-22 09:30:00', TRUE, NOW(), NOW()),

-- Project 1027: Automated Quiz Generator (Instructor: 9, Students: 23, 24, 25)
(3105, 1027, 9, 'INSTRUCTOR', '2025-02-25 09:00:00', TRUE, NOW(), NOW()),
(3106, 1027, 23, 'STUDENT', '2025-02-25 09:30:00', TRUE, NOW(), NOW()),
(3107, 1027, 24, 'STUDENT', '2025-02-25 09:30:00', TRUE, NOW(), NOW()),
(3108, 1027, 25, 'STUDENT', '2025-02-25 09:30:00', TRUE, NOW(), NOW()),

-- Project 1028: Campus Energy Monitor (Instructor: 10, Students: 26, 27, 28)
(3109, 1028, 10, 'INSTRUCTOR', '2025-02-28 09:00:00', TRUE, NOW(), NOW()),
(3110, 1028, 26, 'STUDENT', '2025-02-28 09:30:00', TRUE, NOW(), NOW()),
(3111, 1028, 27, 'STUDENT', '2025-02-28 09:30:00', TRUE, NOW(), NOW()),
(3112, 1028, 28, 'STUDENT', '2025-02-28 09:30:00', TRUE, NOW(), NOW()),

-- Project 1029: Peer Review System (Instructor: 3, Students: 29, 30, 31)
(3113, 1029, 3, 'INSTRUCTOR', '2025-03-03 09:00:00', TRUE, NOW(), NOW()),
(3114, 1029, 29, 'STUDENT', '2025-03-03 09:30:00', TRUE, NOW(), NOW()),
(3115, 1029, 30, 'STUDENT', '2025-03-03 09:30:00', TRUE, NOW(), NOW()),
(3116, 1029, 31, 'STUDENT', '2025-03-03 09:30:00', TRUE, NOW(), NOW()),

-- Project 1030: Smart Lost & Found Service (Instructor: 4, Students: 32, 33, 34)
(3117, 1030, 4, 'INSTRUCTOR', '2025-03-05 09:00:00', TRUE, NOW(), NOW()),
(3118, 1030, 32, 'STUDENT', '2025-03-05 09:30:00', TRUE, NOW(), NOW()),
(3119, 1030, 33, 'STUDENT', '2025-03-05 09:30:00', TRUE, NOW(), NOW()),
(3120, 1030, 34, 'STUDENT', '2025-03-05 09:30:00', TRUE, NOW(), NOW()),

-- Project 1031: Campus Transportation App (Instructor: 5, Students: 35, 36, 37)
(3121, 1031, 5, 'INSTRUCTOR', '2025-03-08 09:00:00', TRUE, NOW(), NOW()),
(3122, 1031, 35, 'STUDENT', '2025-03-08 09:30:00', TRUE, NOW(), NOW()),
(3123, 1031, 36, 'STUDENT', '2025-03-08 09:30:00', TRUE, NOW(), NOW()),
(3124, 1031, 37, 'STUDENT', '2025-03-08 09:30:00', TRUE, NOW(), NOW()),

-- Project 1032: Study Group Matching Platform (Instructor: 6, Students: 38, 39, 40)
(3125, 1032, 6, 'INSTRUCTOR', '2025-03-10 09:00:00', TRUE, NOW(), NOW()),
(3126, 1032, 38, 'STUDENT', '2025-03-10 09:30:00', TRUE, NOW(), NOW()),
(3127, 1032, 39, 'STUDENT', '2025-03-10 09:30:00', TRUE, NOW(), NOW()),
(3128, 1032, 40, 'STUDENT', '2025-03-10 09:30:00', TRUE, NOW(), NOW()),

-- Project 1033: Lab Equipment Booking System (Instructor: 7, Students: 41, 42, 43)
(3129, 1033, 7, 'INSTRUCTOR', '2025-03-12 09:00:00', TRUE, NOW(), NOW()),
(3130, 1033, 41, 'STUDENT', '2025-03-12 09:30:00', TRUE, NOW(), NOW()),
(3131, 1033, 42, 'STUDENT', '2025-03-12 09:30:00', TRUE, NOW(), NOW()),
(3132, 1033, 43, 'STUDENT', '2025-03-12 09:30:00', TRUE, NOW(), NOW()),

-- Project 1034: Campus Marketplace (Instructor: 8, Students: 44, 45, 46)
(3133, 1034, 8, 'INSTRUCTOR', '2025-03-15 09:00:00', TRUE, NOW(), NOW()),
(3134, 1034, 44, 'STUDENT', '2025-03-15 09:30:00', TRUE, NOW(), NOW()),
(3135, 1034, 45, 'STUDENT', '2025-03-15 09:30:00', TRUE, NOW(), NOW()),
(3136, 1034, 46, 'STUDENT', '2025-03-15 09:30:00', TRUE, NOW(), NOW()),

-- Project 1035: Virtual Lab Simulation (Instructor: 9, Students: 47, 48, 11)
(3137, 1035, 9, 'INSTRUCTOR', '2025-03-18 09:00:00', TRUE, NOW(), NOW()),
(3138, 1035, 47, 'STUDENT', '2025-03-18 09:30:00', TRUE, NOW(), NOW()),
(3139, 1035, 48, 'STUDENT', '2025-03-18 09:30:00', TRUE, NOW(), NOW()),
(3140, 1035, 11, 'STUDENT', '2025-03-18 09:30:00', TRUE, NOW(), NOW()),

-- Project 1036: Academic Integrity Checker (Instructor: 10, Students: 12, 13, 15)
(3141, 1036, 10, 'INSTRUCTOR', '2025-03-20 09:00:00', TRUE, NOW(), NOW()),
(3142, 1036, 12, 'STUDENT', '2025-03-20 09:30:00', TRUE, NOW(), NOW()),
(3143, 1036, 13, 'STUDENT', '2025-03-20 09:30:00', TRUE, NOW(), NOW()),
(3144, 1036, 15, 'STUDENT', '2025-03-20 09:30:00', TRUE, NOW(), NOW()),

-- Project 1037: Campus Safety Alert System (Instructor: 3, Students: 17, 18, 22)
(3145, 1037, 3, 'INSTRUCTOR', '2025-03-22 09:00:00', TRUE, NOW(), NOW()),
(3146, 1037, 17, 'STUDENT', '2025-03-22 09:30:00', TRUE, NOW(), NOW()),
(3147, 1037, 18, 'STUDENT', '2025-03-22 09:30:00', TRUE, NOW(), NOW()),
(3148, 1037, 22, 'STUDENT', '2025-03-22 09:30:00', TRUE, NOW(), NOW()),

-- Project 1038: Career Path Advisor (Instructor: 4, Students: 23, 24, 25)
(3149, 1038, 4, 'INSTRUCTOR', '2025-03-25 09:00:00', TRUE, NOW(), NOW()),
(3150, 1038, 23, 'STUDENT', '2025-03-25 09:30:00', TRUE, NOW(), NOW()),
(3151, 1038, 24, 'STUDENT', '2025-03-25 09:30:00', TRUE, NOW(), NOW()),
(3152, 1038, 25, 'STUDENT', '2025-03-25 09:30:00', TRUE, NOW(), NOW()),

-- Project 1039: Campus Wi-Fi Analytics (Instructor: 5, Students: 26, 27, 28)
(3153, 1039, 5, 'INSTRUCTOR', '2025-03-28 09:00:00', TRUE, NOW(), NOW()),
(3154, 1039, 26, 'STUDENT', '2025-03-28 09:30:00', TRUE, NOW(), NOW()),
(3155, 1039, 27, 'STUDENT', '2025-03-28 09:30:00', TRUE, NOW(), NOW()),
(3156, 1039, 28, 'STUDENT', '2025-03-28 09:30:00', TRUE, NOW(), NOW()),

-- Project 1040: Student Budget Planner (Instructor: 6, Students: 29, 30, 32)
(3157, 1040, 6, 'INSTRUCTOR', '2025-03-30 09:00:00', TRUE, NOW(), NOW()),
(3158, 1040, 29, 'STUDENT', '2025-03-30 09:30:00', TRUE, NOW(), NOW()),
(3159, 1040, 30, 'STUDENT', '2025-03-30 09:30:00', TRUE, NOW(), NOW()),
(3160, 1040, 32, 'STUDENT', '2025-03-30 09:30:00', TRUE, NOW(), NOW());


-- ==========================================
-- TASKS FOR MILESTONE 2001 (Project 1001)
-- ==========================================
INSERT INTO tasks (id, project_id, milestone_id, title, description, status, start_date, end_date, is_locked, locked_by_id, locked_at, created_by_id, created_at, updated_at) VALUES
(4001, 1001, 2001, 'Requirements Gathering', 'Interview stakeholders and document functional requirements for the telehealth platform.', 'COMPLETED', '2025-01-08', '2025-01-20', FALSE, NULL, NULL, 3, '2025-01-08 10:00:00', NOW()),
(4002, 1001, 2001, 'Technology Stack Selection', 'Research and select appropriate technologies for video streaming and data security.', 'COMPLETED', '2025-01-21', '2025-02-05', FALSE, NULL, NULL, 3, '2025-01-21 09:00:00', NOW()),
(4003, 1001, 2001, 'System Architecture Design', 'Design overall system architecture including microservices and database schema.', 'IN_PROGRESS', '2025-02-06', '2025-02-22', FALSE, NULL, NULL, 3, '2025-02-06 09:00:00', NOW()),

-- ==========================================
-- TASKS FOR MILESTONE 2002 (Project 1001)
-- ==========================================
(4004, 1001, 2002, 'Backend API Development', 'Implement RESTful APIs for user authentication, video sessions, and health records.', 'IN_PROGRESS', '2025-02-23', '2025-03-20', FALSE, NULL, NULL, 3, '2025-02-23 09:00:00', NOW()),
(4005, 1001, 2002, 'Frontend Development', 'Build responsive UI with React including video call interface and dashboard.', 'IN_PROGRESS', '2025-03-01', '2025-04-15', FALSE, NULL, NULL, 3, '2025-03-01 09:00:00', NOW()),
(4006, 1001, 2002, 'Security Implementation', 'Implement end-to-end encryption and HIPAA compliance measures.', 'IN_PROGRESS', '2025-03-25', '2025-04-30', FALSE, NULL, NULL, 3, '2025-03-25 09:00:00', NOW()),

-- ==========================================
-- TASKS FOR MILESTONE 2003 (Project 1002)
-- ==========================================
(4007, 1002, 2003, 'AR Framework Research', 'Evaluate AR.js, A-Frame, and Unity AR Foundation for indoor navigation.', 'COMPLETED', '2025-01-15', '2025-01-28', FALSE, NULL, NULL, 4, '2025-01-15 10:00:00', NOW()),
(4008, 1002, 2003, 'Beacon Infrastructure Planning', 'Design beacon placement strategy for optimal indoor positioning accuracy.', 'COMPLETED', '2025-01-29', '2025-02-15', FALSE, NULL, NULL, 4, '2025-01-29 09:00:00', NOW()),
(4009, 1002, 2003, 'Accessibility Requirements', 'Define accessible route requirements and user interface guidelines.', 'IN_PROGRESS', '2025-02-16', '2025-03-01', FALSE, NULL, NULL, 4, '2025-02-16 09:00:00', NOW()),

-- ==========================================
-- TASKS FOR MILESTONE 2004 (Project 1002)
-- ==========================================
(4010, 1002, 2004, 'AR Navigation Module', 'Develop core AR navigation engine with path finding algorithms.', 'IN_PROGRESS', '2025-03-02', '2025-04-10', FALSE, NULL, NULL, 4, '2025-03-02 09:00:00', NOW()),
(4011, 1002, 2004, 'Mobile App Development', 'Create cross-platform mobile app with AR camera integration.', 'IN_PROGRESS', '2025-03-15', '2025-04-30', FALSE, NULL, NULL, 4, '2025-03-15 09:00:00', NOW()),

-- ==========================================
-- TASKS FOR MILESTONE 2011-2012 (Project 1006 - COMPLETED)
-- ==========================================
(4012, 1006, 2011, 'Sustainability Metrics Research', 'Research industry standards for measuring carbon footprint and waste metrics.', 'COMPLETED', '2024-09-05', '2024-09-25', FALSE, NULL, NULL, 8, '2024-09-05 10:00:00', NOW()),
(4013, 1006, 2011, 'Database Design', 'Design PostgreSQL schema for events, metrics, and scoring data.', 'COMPLETED', '2024-09-26', '2024-10-10', FALSE, NULL, NULL, 8, '2024-09-26 09:00:00', NOW()),
(4014, 1006, 2012, 'Survey Module Implementation', 'Implement automated survey capture with QR code integration.', 'COMPLETED', '2024-10-21', '2024-11-20', FALSE, NULL, NULL, 8, '2024-10-21 09:00:00', NOW()),
(4015, 1006, 2012, 'Analytics Dashboard', 'Build real-time analytics dashboard with charts and scoreboard.', 'COMPLETED', '2024-11-21', '2024-12-30', FALSE, NULL, NULL, 8, '2024-11-21 09:00:00', NOW()),
(4016, 1006, 2012, 'Mobile App Polish', 'Final UI/UX improvements and performance optimization.', 'COMPLETED', '2025-01-05', '2025-01-30', FALSE, NULL, NULL, 8, '2025-01-05 09:00:00', NOW()),

-- ==========================================
-- TASKS FOR MILESTONE 2013-2014 (Project 1007)
-- ==========================================
(4017, 1007, 2013, 'Motion Capture Hardware Setup', 'Configure motion capture cameras and calibration procedures.', 'COMPLETED', '2024-09-10', '2024-09-30', FALSE, NULL, NULL, 9, '2024-09-10 10:00:00', NOW()),
(4018, 1007, 2013, 'Biomechanics Database', 'Compile database of gold standard movement patterns from professionals.', 'COMPLETED', '2024-10-01', '2024-10-25', FALSE, NULL, NULL, 9, '2024-10-01 09:00:00', NOW()),
(4019, 1007, 2014, 'Video Analysis Engine', 'Develop computer vision pipeline for motion analysis and comparison.', 'IN_PROGRESS', '2024-10-26', '2024-12-15', FALSE, NULL, NULL, 9, '2024-10-26 09:00:00', NOW()),
(4020, 1007, 2014, 'Heatmap Visualization', 'Create delta heatmap visualization for movement differences.', 'IN_PROGRESS', '2024-12-16', '2025-01-20', FALSE, NULL, NULL, 9, '2024-12-16 09:00:00', NOW()),

-- ==========================================
-- TASKS FOR MILESTONE 2015-2016 (Project 1008)
-- ==========================================
(4021, 1008, 2015, 'Competency Framework Design', 'Map competency requirements to learning outcomes.', 'IN_PROGRESS', '2024-09-15', '2024-10-10', FALSE, NULL, NULL, 10, '2024-09-15 10:00:00', NOW()),
(4022, 1008, 2015, 'Evidence Upload System', 'Build file upload system with categorization and tagging.', 'IN_PROGRESS', '2024-10-11', '2024-10-30', FALSE, NULL, NULL, 10, '2024-10-11 09:00:00', NOW()),
(4023, 1008, 2016, 'AI Summary Generator', 'Integrate GPT-4 for generating reflection summaries from evidence.', 'IN_PROGRESS', '2024-10-31', '2024-12-15', FALSE, NULL, NULL, 10, '2024-10-31 09:00:00', NOW()),
(4024, 1008, 2016, 'Accreditation Export', 'Implement PDF export with institutional templates for accreditation.', 'IN_PROGRESS', '2024-12-16', '2025-01-30', FALSE, NULL, NULL, 10, '2024-12-16 09:00:00', NOW());

-- Tasks for Project 1003 (Smart Studio Scheduler - Milestones 2005-2006)
INSERT INTO tasks (id, project_id, milestone_id, title, description, status, start_date, end_date, is_locked, locked_by_id, locked_at, created_by_id, created_at, updated_at) VALUES
(4025, 1003, 2005, 'Studio Resource Survey', 'Survey all studio spaces, equipment inventory, and current usage patterns.', 'COMPLETED', '2025-01-20', '2025-02-05', FALSE, NULL, NULL, 5, '2025-01-20 10:00:00', NOW()),
(4026, 1003, 2005, 'Scheduling Algorithm Design', 'Design optimization algorithm for room allocation considering multiple constraints.', 'COMPLETED', '2025-02-06', '2025-02-25', FALSE, NULL, NULL, 5, '2025-02-06 09:00:00', NOW()),
(4027, 1003, 2005, 'User Interface Mockups', 'Create UI mockups for booking interface and admin dashboard.', 'IN_PROGRESS', '2025-02-26', '2025-03-06', FALSE, NULL, NULL, 5, '2025-02-26 09:00:00', NOW()),
(4028, 1003, 2006, 'Backend API Development', 'Implement REST APIs for booking, resource management, and analytics.', 'IN_PROGRESS', '2025-03-07', '2025-04-15', FALSE, NULL, NULL, 5, '2025-03-07 09:00:00', NOW()),
(4029, 1003, 2006, 'Integration with IoT Sensors', 'Connect to occupancy sensors and lighting control systems.', 'IN_PROGRESS', '2025-04-16', '2025-05-20', FALSE, NULL, NULL, 5, '2025-04-16 09:00:00', NOW()),

-- Tasks for Project 1004 (AI Posture Coach - Milestones 2007-2008)
(4030, 1004, 2007, 'Pose Estimation Research', 'Evaluate MediaPipe, OpenPose, and other pose estimation libraries.', 'COMPLETED', '2025-01-25', '2025-02-10', FALSE, NULL, NULL, 6, '2025-01-25 10:00:00', NOW()),
(4031, 1004, 2007, 'Exercise Database Creation', 'Compile database of exercises with correct form demonstrations.', 'COMPLETED', '2025-02-11', '2025-03-01', FALSE, NULL, NULL, 6, '2025-02-11 09:00:00', NOW()),
(4032, 1004, 2007, 'Scoring Rubric Development', 'Define scoring criteria for posture accuracy and movement quality.', 'IN_PROGRESS', '2025-03-02', '2025-03-11', FALSE, NULL, NULL, 6, '2025-03-02 09:00:00', NOW()),
(4033, 1004, 2008, 'Computer Vision Pipeline', 'Build real-time pose detection and scoring pipeline.', 'IN_PROGRESS', '2025-03-12', '2025-04-20', FALSE, NULL, NULL, 6, '2025-03-12 09:00:00', NOW()),
(4034, 1004, 2008, 'Mobile App Development', 'Create mobile app with camera integration and feedback interface.', 'IN_PROGRESS', '2025-04-21', '2025-06-01', FALSE, NULL, NULL, 6, '2025-04-21 09:00:00', NOW()),

-- Tasks for Project 1005 (Virtual Lab Companion - Milestones 2009-2010)
(4035, 1005, 2009, 'Lab Protocol Documentation', 'Digitize all lab protocols and safety procedures.', 'COMPLETED', '2025-02-01', '2025-02-20', FALSE, NULL, NULL, 7, '2025-02-01 10:00:00', NOW()),
(4036, 1005, 2009, 'AR Framework Selection', 'Evaluate Vuforia, ARKit, and ARCore for lab environment.', 'IN_PROGRESS', '2025-02-21', '2025-03-10', FALSE, NULL, NULL, 7, '2025-02-21 09:00:00', NOW()),
(4037, 1005, 2009, 'Safety Alert System Design', 'Design real-time safety alert and equipment monitoring system.', 'IN_PROGRESS', '2025-03-11', '2025-03-18', FALSE, NULL, NULL, 7, '2025-03-11 09:00:00', NOW()),
(4038, 1005, 2010, 'AR Overlay Development', 'Develop AR overlays for equipment instructions and safety zones.', 'IN_PROGRESS', '2025-03-19', '2025-05-01', FALSE, NULL, NULL, 7, '2025-03-19 09:00:00', NOW()),
(4039, 1005, 2010, 'Voice Command Integration', 'Implement hands-free voice controls for lab operations.', 'IN_PROGRESS', '2025-05-02', '2025-06-10', FALSE, NULL, NULL, 7, '2025-05-02 09:00:00', NOW()),

-- Tasks for Project 1009 (Pose Robotics Interface - Milestones 2017-2018)
(4040, 1009, 2017, 'ROS Environment Setup', 'Configure ROS environment and simulation tools.', 'COMPLETED', '2024-09-20', '2024-10-05', FALSE, NULL, NULL, 3, '2024-09-20 10:00:00', NOW()),
(4041, 1009, 2017, 'Robot Safety Envelope Design', 'Define safety boundaries and emergency stop procedures.', 'COMPLETED', '2024-10-06', '2024-10-25', FALSE, NULL, NULL, 3, '2024-10-06 09:00:00', NOW()),
(4042, 1009, 2017, 'Motion Capture Integration', 'Connect motion capture system to ROS pipeline.', 'IN_PROGRESS', '2024-10-26', '2024-11-04', FALSE, NULL, NULL, 3, '2024-10-26 09:00:00', NOW()),
(4043, 1009, 2018, 'Robot Control Interface', 'Build web interface for robot control and monitoring.', 'IN_PROGRESS', '2024-11-05', '2024-12-20', FALSE, NULL, NULL, 3, '2024-11-05 09:00:00', NOW()),
(4044, 1009, 2018, 'Lesson Plan Templates', 'Create reusable lesson templates for robotics education.', 'IN_PROGRESS', '2024-12-21', '2025-03-01', FALSE, NULL, NULL, 3, '2024-12-21 09:00:00', NOW()),

-- Tasks for Project 1010 (Research Paper CoPilot - Milestones 2019-2020)
(4045, 1010, 2019, 'Citation Database Setup', 'Setup database for tracking citations and references.', 'COMPLETED', '2024-10-01', '2024-10-20', FALSE, NULL, NULL, 4, '2024-10-01 10:00:00', NOW()),
(4046, 1010, 2019, 'AI Model Selection', 'Evaluate GPT-4, Claude, and other LLMs for writing assistance.', 'COMPLETED', '2024-10-21', '2024-11-05', FALSE, NULL, NULL, 4, '2024-10-21 09:00:00', NOW()),
(4047, 1010, 2019, 'Plagiarism Detection Integration', 'Integrate plagiarism detection and academic integrity checks.', 'IN_PROGRESS', '2024-11-06', '2024-11-15', FALSE, NULL, NULL, 4, '2024-11-06 09:00:00', NOW()),
(4048, 1010, 2020, 'Grammar Enhancement Engine', 'Build real-time grammar and style checking.', 'IN_PROGRESS', '2024-11-16', '2025-01-15', FALSE, NULL, NULL, 4, '2024-11-16 09:00:00', NOW()),
(4049, 1010, 2020, 'LaTeX Export Feature', 'Implement export functionality to LaTeX format.', 'IN_PROGRESS', '2025-01-16', '2025-03-15', FALSE, NULL, NULL, 4, '2025-01-16 09:00:00', NOW()),

-- Tasks for Project 1011 (Fitness Gamification Portal - COMPLETED - Milestones 2021-2022)
(4050, 1011, 2021, 'Badge System Design', 'Design achievement badges and reward mechanics.', 'COMPLETED', '2024-10-05', '2024-10-25', FALSE, NULL, NULL, 5, '2024-10-05 10:00:00', NOW()),
(4051, 1011, 2021, 'Sensor Integration Planning', 'Plan integration with wearables and gym equipment sensors.', 'COMPLETED', '2024-10-26', '2024-11-10', FALSE, NULL, NULL, 5, '2024-10-26 09:00:00', NOW()),
(4052, 1011, 2021, 'Leaderboard Algorithm', 'Develop fair leaderboard ranking algorithm.', 'COMPLETED', '2024-11-11', '2024-11-19', FALSE, NULL, NULL, 5, '2024-11-11 09:00:00', NOW()),
(4053, 1011, 2022, 'Mobile App Implementation', 'Build iOS and Android fitness tracking apps.', 'COMPLETED', '2024-11-20', '2025-01-20', FALSE, NULL, NULL, 5, '2024-11-20 09:00:00', NOW()),
(4054, 1011, 2022, 'Social Features Development', 'Implement peer challenges and team competitions.', 'COMPLETED', '2025-01-21', '2025-03-22', FALSE, NULL, NULL, 5, '2025-01-21 09:00:00', NOW()),

-- Tasks for Project 1012 (Accessibility Insight Dashboard - Milestones 2023-2024)
(4055, 1012, 2023, 'Accessibility Audit', 'Conduct comprehensive campus accessibility audit.', 'COMPLETED', '2024-10-10', '2024-10-30', FALSE, NULL, NULL, 6, '2024-10-10 10:00:00', NOW()),
(4056, 1012, 2023, 'IoT Sensor Deployment Plan', 'Design sensor network for tracking accessibility metrics.', 'COMPLETED', '2024-10-31', '2024-11-15', FALSE, NULL, NULL, 6, '2024-10-31 09:00:00', NOW()),
(4057, 1012, 2023, 'Data Visualization Design', 'Create dashboard mockups with heatmaps and charts.', 'COMPLETED', '2024-11-16', '2024-11-24', FALSE, NULL, NULL, 6, '2024-11-16 09:00:00', NOW()),
(4058, 1012, 2024, 'Backend Data Pipeline', 'Build ETL pipeline for sensor data and ticket systems.', 'IN_PROGRESS', '2024-11-25', '2025-01-30', FALSE, NULL, NULL, 6, '2024-11-25 09:00:00', NOW()),
(4059, 1012, 2024, 'Interactive Dashboard', 'Develop interactive dashboard with filtering and drill-down.', 'IN_PROGRESS', '2025-01-31', '2025-03-30', FALSE, NULL, NULL, 6, '2025-01-31 09:00:00', NOW()),

-- Tasks for Project 1013 (Smart Attendance Vision - Milestones 2025-2026)
(4060, 1013, 2025, 'Gait Recognition Research', 'Research gait recognition algorithms and privacy implications.', 'COMPLETED', '2024-10-15', '2024-11-05', FALSE, NULL, NULL, 7, '2024-10-15 10:00:00', NOW()),
(4061, 1013, 2025, 'Camera Infrastructure Setup', 'Install cameras in rehearsal rooms with proper lighting.', 'IN_PROGRESS', '2024-11-06', '2024-11-20', FALSE, NULL, NULL, 7, '2024-11-06 09:00:00', NOW()),
(4062, 1013, 2025, 'Privacy Framework Design', 'Design privacy-preserving data collection and storage.', 'IN_PROGRESS', '2024-11-21', '2024-11-29', FALSE, NULL, NULL, 7, '2024-11-21 09:00:00', NOW()),
(4063, 1013, 2026, 'Gait Analysis Engine', 'Implement gait signature extraction and matching.', 'IN_PROGRESS', '2024-11-30', '2025-02-15', FALSE, NULL, NULL, 7, '2024-11-30 09:00:00', NOW()),
(4064, 1013, 2026, 'LMS Integration', 'Connect attendance system to Learning Management System.', 'IN_PROGRESS', '2025-02-16', '2025-04-05', FALSE, NULL, NULL, 7, '2025-02-16 09:00:00', NOW()),

-- Tasks for Project 1014 (Peer Mentorship Hub - Milestones 2027-2028)
(4065, 1014, 2027, 'Matching Algorithm Research', 'Research peer matching algorithms and best practices.', 'IN_PROGRESS', '2024-10-20', '2024-11-10', FALSE, NULL, NULL, 8, '2024-10-20 10:00:00', NOW()),
(4066, 1014, 2027, 'Mentorship Framework Design', 'Define mentorship program structure and guidelines.', 'IN_PROGRESS', '2024-11-11', '2024-11-25', FALSE, NULL, NULL, 8, '2024-11-11 09:00:00', NOW()),
(4067, 1014, 2027, 'Learning Playlist Creation', 'Curate learning resources for mentor training.', 'IN_PROGRESS', '2024-11-26', '2024-12-04', FALSE, NULL, NULL, 8, '2024-11-26 09:00:00', NOW()),
(4068, 1014, 2028, 'Matching Engine Implementation', 'Build automated mentor-mentee pairing system.', 'IN_PROGRESS', '2024-12-05', '2025-02-15', FALSE, NULL, NULL, 8, '2024-12-05 09:00:00', NOW()),
(4069, 1014, 2028, 'Progress Tracking Dashboard', 'Create dashboard for tracking mentorship progress.', 'IN_PROGRESS', '2025-02-16', '2025-04-12', FALSE, NULL, NULL, 8, '2025-02-16 09:00:00', NOW()),

-- Tasks for Project 1015 (Wellness Pulse Monitor - Milestones 2029-2030)
(4070, 1015, 2029, 'Wearable Integration Research', 'Evaluate compatibility with Fitbit, Apple Watch, etc.', 'COMPLETED', '2024-11-01', '2024-11-20', FALSE, NULL, NULL, 9, '2024-11-01 10:00:00', NOW()),
(4071, 1015, 2029, 'Mental Health Metrics Design', 'Define wellness metrics and mood tracking system.', 'COMPLETED', '2024-11-21', '2024-12-10', FALSE, NULL, NULL, 9, '2024-11-21 09:00:00', NOW()),
(4072, 1015, 2029, 'Privacy and Consent Framework', 'Design GDPR-compliant data collection and consent.', 'COMPLETED', '2024-12-11', '2024-12-16', FALSE, NULL, NULL, 9, '2024-12-11 09:00:00', NOW()),
(4073, 1015, 2030, 'Data Aggregation Service', 'Build service to aggregate data from multiple sources.', 'IN_PROGRESS', '2024-12-17', '2025-02-28', FALSE, NULL, NULL, 9, '2024-12-17 09:00:00', NOW()),
(4074, 1015, 2030, 'Proactive Intervention System', 'Implement AI-driven wellness recommendations.', 'IN_PROGRESS', '2025-03-01', '2025-04-25', FALSE, NULL, NULL, 9, '2025-03-01 09:00:00', NOW()),

-- Tasks for Project 1016 (Collaborative Whiteboard AI - LOCKED - Milestones 2031-2032)
(4075, 1016, 2031, 'Sketch Recognition Research', 'Research sketch-to-text and diagram recognition.', 'COMPLETED', '2024-11-05', '2024-11-25', FALSE, NULL, NULL, 10, '2024-11-05 10:00:00', NOW()),
(4076, 1016, 2031, 'Real-time Collaboration Framework', 'Design WebSocket-based real-time collaboration.', 'COMPLETED', '2024-11-26', '2024-12-15', FALSE, NULL, NULL, 10, '2024-11-26 09:00:00', NOW()),
(4077, 1016, 2031, 'AI Clustering Algorithm', 'Develop AI to cluster related ideas and concepts.', 'IN_PROGRESS', '2024-12-16', '2024-12-20', FALSE, NULL, NULL, 10, '2024-12-16 09:00:00', NOW()),
(4078, 1016, 2032, 'Whiteboard Canvas Development', 'Build infinite canvas with drawing tools.', 'IN_PROGRESS', '2024-12-21', '2025-03-15', FALSE, NULL, NULL, 10, '2024-12-21 09:00:00', NOW()),
(4079, 1016, 2032, 'Sprint Board Integration', 'Connect to Jira/Trello for action item sync.', 'IN_PROGRESS', '2025-03-16', '2025-05-05', TRUE, 2, '2024-12-15 09:00:00', 10, '2025-03-16 09:00:00', NOW()),

-- Tasks for Project 1017 (Capstone Portfolio Vault - Milestones 2033-2034)
(4080, 1017, 2033, 'Portfolio Template Design', 'Create professional portfolio templates.', 'IN_PROGRESS', '2024-11-10', '2024-12-01', FALSE, NULL, NULL, 3, '2024-11-10 10:00:00', NOW()),
(4081, 1017, 2033, 'Evidence Upload System', 'Build secure file upload with version control.', 'IN_PROGRESS', '2024-12-02', '2024-12-20', FALSE, NULL, NULL, 3, '2024-12-02 09:00:00', NOW()),
(4082, 1017, 2033, 'Rubric Alignment Tool', 'Create tool to map evidence to rubric criteria.', 'IN_PROGRESS', '2024-12-21', '2024-12-25', FALSE, NULL, NULL, 3, '2024-12-21 09:00:00', NOW()),
(4083, 1017, 2034, 'Approval Workflow Engine', 'Implement multi-stage approval process.', 'IN_PROGRESS', '2024-12-26', '2025-03-15', FALSE, NULL, NULL, 3, '2024-12-26 09:00:00', NOW()),
(4084, 1017, 2034, 'Public Showcase Portal', 'Build public-facing portfolio showcase.', 'IN_PROGRESS', '2025-03-16', '2025-05-12', FALSE, NULL, NULL, 3, '2025-03-16 09:00:00', NOW()),

-- Tasks for Project 1018 (Lab Inventory Assistant - Milestones 2035-2036)
(4085, 1018, 2035, 'Barcode System Setup', 'Implement barcode/QR code labeling system.', 'IN_PROGRESS', '2024-11-15', '2024-12-05', FALSE, NULL, NULL, 4, '2024-11-15 10:00:00', NOW()),
(4086, 1018, 2035, 'Inventory Database Design', 'Design database schema for equipment tracking.', 'IN_PROGRESS', '2024-12-06', '2024-12-20', FALSE, NULL, NULL, 4, '2024-12-06 09:00:00', NOW()),
(4087, 1018, 2035, 'Expiry Policy Framework', 'Define rules for handling expired materials.', 'IN_PROGRESS', '2024-12-21', '2024-12-30', FALSE, NULL, NULL, 4, '2024-12-21 09:00:00', NOW()),
(4088, 1018, 2036, 'Scanner App Development', 'Build mobile app for scanning and updates.', 'IN_PROGRESS', '2024-12-31', '2025-03-20', FALSE, NULL, NULL, 4, '2024-12-31 09:00:00', NOW()),
(4089, 1018, 2036, 'Predictive Restock System', 'Implement ML-based restock predictions.', 'IN_PROGRESS', '2025-03-21', '2025-05-20', FALSE, NULL, NULL, 4, '2025-03-21 09:00:00', NOW()),

-- Tasks for Project 1019 (AI Feedback Studio - Milestones 2037-2038)
(4090, 1019, 2037, 'Audio Analysis Research', 'Research audio analysis for music feedback.', 'IN_PROGRESS', '2024-11-20', '2024-12-10', FALSE, NULL, NULL, 5, '2024-11-20 10:00:00', NOW()),
(4091, 1019, 2037, 'Feedback Rubric Design', 'Create rubrics for timing, tone, coordination.', 'IN_PROGRESS', '2024-12-11', '2024-12-28', FALSE, NULL, NULL, 5, '2024-12-11 09:00:00', NOW()),
(4092, 1019, 2037, 'File Upload Interface', 'Build interface for uploading audio/video files.', 'IN_PROGRESS', '2024-12-29', '2025-01-04', FALSE, NULL, NULL, 5, '2024-12-29 09:00:00', NOW()),
(4093, 1019, 2038, 'AI Analysis Engine', 'Implement AI-powered music analysis.', 'IN_PROGRESS', '2025-01-05', '2025-03-30', FALSE, NULL, NULL, 5, '2025-01-05 09:00:00', NOW()),
(4094, 1019, 2038, 'Feedback Delivery System', 'Build system for delivering combined AI and faculty feedback.', 'IN_PROGRESS', '2025-03-31', '2025-05-28', FALSE, NULL, NULL, 5, '2025-03-31 09:00:00', NOW()),

-- Tasks for Project 1020 (Internship Matching Engine - Milestones 2039-2040)
(4095, 1020, 2039, 'Employer Database Setup', 'Build database of internship opportunities.', 'IN_PROGRESS', '2024-12-01', '2024-12-20', FALSE, NULL, NULL, 6, '2024-12-01 10:00:00', NOW()),
(4096, 1020, 2039, 'Skill Assessment Tool', 'Create skill evaluation questionnaires.', 'IN_PROGRESS', '2024-12-21', '2025-01-05', FALSE, NULL, NULL, 6, '2024-12-21 09:00:00', NOW()),
(4097, 1020, 2039, 'Visa Requirements Module', 'Build module for tracking visa requirements.', 'IN_PROGRESS', '2025-01-06', '2025-01-15', FALSE, NULL, NULL, 6, '2025-01-06 09:00:00', NOW()),
(4098, 1020, 2040, 'Matching Algorithm', 'Develop intelligent student-internship matching.', 'IN_PROGRESS', '2025-01-16', '2025-04-15', FALSE, NULL, NULL, 6, '2025-01-16 09:00:00', NOW()),
(4099, 1020, 2040, 'Application Tracking System', 'Build system to track application status.', 'IN_PROGRESS', '2025-04-16', '2025-06-05', FALSE, NULL, NULL, 6, '2025-04-16 09:00:00', NOW()),

-- Tasks for Projects 1021-1040 (Semester 1, Batch 2)
-- Project 1021: Digital Campus Library System
(4100, 1021, 2041, 'Library System Requirements', 'Gather requirements from librarians and students.', 'COMPLETED', '2025-02-10', '2025-02-28', FALSE, NULL, NULL, 3, '2025-02-10 10:00:00', NOW()),
(4101, 1021, 2041, 'RFID Technology Research', 'Research RFID readers and tag systems.', 'IN_PROGRESS', '2025-03-01', '2025-03-20', FALSE, NULL, NULL, 3, '2025-03-01 09:00:00', NOW()),
(4102, 1021, 2041, 'AI Recommendation Engine', 'Design book recommendation algorithm.', 'IN_PROGRESS', '2025-03-21', '2025-03-26', FALSE, NULL, NULL, 3, '2025-03-21 09:00:00', NOW()),
(4103, 1021, 2042, 'Digital Catalog Development', 'Build searchable digital library catalog.', 'IN_PROGRESS', '2025-03-27', '2025-05-15', FALSE, NULL, NULL, 3, '2025-03-27 09:00:00', NOW()),
(4104, 1021, 2042, 'Self-Checkout System', 'Implement automated checkout kiosks.', 'IN_PROGRESS', '2025-05-16', '2025-06-15', FALSE, NULL, NULL, 3, '2025-05-16 09:00:00', NOW()),

-- Project 1022: Smart Parking Management
(4105, 1022, 2043, 'Parking Lot Survey', 'Survey all campus parking facilities.', 'COMPLETED', '2025-02-12', '2025-03-05', FALSE, NULL, NULL, 4, '2025-02-12 10:00:00', NOW()),
(4106, 1022, 2043, 'IoT Sensor Selection', 'Evaluate parking sensors and cameras.', 'IN_PROGRESS', '2025-03-06', '2025-03-25', FALSE, NULL, NULL, 4, '2025-03-06 09:00:00', NOW()),
(4107, 1022, 2043, 'Payment Gateway Design', 'Design integrated payment system.', 'IN_PROGRESS', '2025-03-26', '2025-03-28', FALSE, NULL, NULL, 4, '2025-03-26 09:00:00', NOW()),
(4108, 1022, 2044, 'Mobile Navigation App', 'Build app to guide drivers to available spots.', 'IN_PROGRESS', '2025-03-29', '2025-05-20', FALSE, NULL, NULL, 4, '2025-03-29 09:00:00', NOW()),
(4109, 1022, 2044, 'Real-time Dashboard', 'Create monitoring dashboard for parking management.', 'IN_PROGRESS', '2025-05-21', '2025-06-20', FALSE, NULL, NULL, 4, '2025-05-21 09:00:00', NOW()),

-- Project 1023: Virtual Classroom Platform
(4110, 1023, 2045, 'Video Platform Research', 'Evaluate Zoom SDK, WebRTC, Agora.', 'COMPLETED', '2025-02-15', '2025-03-05', FALSE, NULL, NULL, 5, '2025-02-15 10:00:00', NOW()),
(4111, 1023, 2045, 'Breakout Room Design', 'Design breakout room functionality.', 'IN_PROGRESS', '2025-03-06', '2025-03-25', FALSE, NULL, NULL, 5, '2025-03-06 09:00:00', NOW()),
(4112, 1023, 2045, 'Whiteboard Integration', 'Plan collaborative whiteboard features.', 'IN_PROGRESS', '2025-03-26', '2025-04-01', FALSE, NULL, NULL, 5, '2025-03-26 09:00:00', NOW()),
(4113, 1023, 2046, 'Video Conferencing Module', 'Implement core video conferencing.', 'IN_PROGRESS', '2025-04-02', '2025-05-20', FALSE, NULL, NULL, 5, '2025-04-02 09:00:00', NOW()),
(4114, 1023, 2046, 'Engagement Analytics', 'Build student engagement tracking.', 'IN_PROGRESS', '2025-05-21', '2025-06-25', FALSE, NULL, NULL, 5, '2025-05-21 09:00:00', NOW()),

-- Project 1024: Campus Event Management App
(4115, 1024, 2047, 'Event Discovery Features', 'Design event search and filtering.', 'IN_PROGRESS', '2025-02-18', '2025-03-10', FALSE, NULL, NULL, 6, '2025-02-18 10:00:00', NOW()),
(4116, 1024, 2047, 'Calendar Integration', 'Integrate with Google/Apple calendars.', 'IN_PROGRESS', '2025-03-11', '2025-03-30', FALSE, NULL, NULL, 6, '2025-03-11 09:00:00', NOW()),
(4117, 1024, 2047, 'Registration System', 'Build event registration and ticketing.', 'IN_PROGRESS', '2025-03-31', '2025-04-04', FALSE, NULL, NULL, 6, '2025-03-31 09:00:00', NOW()),
(4118, 1024, 2048, 'Event Creation Tools', 'Create tools for organizers to post events.', 'IN_PROGRESS', '2025-04-05', '2025-05-25', FALSE, NULL, NULL, 6, '2025-04-05 09:00:00', NOW()),
(4119, 1024, 2048, 'Notification System', 'Implement push notifications for events.', 'IN_PROGRESS', '2025-05-26', '2025-06-30', FALSE, NULL, NULL, 6, '2025-05-26 09:00:00', NOW()),

-- Project 1025: Food Waste Reduction Tracker
(4120, 1025, 2049, 'Cafeteria Data Analysis', 'Analyze current food waste patterns.', 'IN_PROGRESS', '2025-02-20', '2025-03-10', FALSE, NULL, NULL, 7, '2025-02-20 10:00:00', NOW()),
(4121, 1025, 2049, 'Sensor Hardware Setup', 'Install weight sensors in waste bins.', 'IN_PROGRESS', '2025-03-11', '2025-03-30', FALSE, NULL, NULL, 7, '2025-03-11 09:00:00', NOW()),
(4122, 1025, 2049, 'Data Collection Pipeline', 'Build pipeline for waste data collection.', 'IN_PROGRESS', '2025-03-31', '2025-04-06', FALSE, NULL, NULL, 7, '2025-03-31 09:00:00', NOW()),
(4123, 1025, 2050, 'Analytics Dashboard', 'Create waste analytics dashboard.', 'IN_PROGRESS', '2025-04-07', '2025-06-01', FALSE, NULL, NULL, 7, '2025-04-07 09:00:00', NOW()),
(4124, 1025, 2050, 'Recommendation Engine', 'Build AI recommendations for waste reduction.', 'IN_PROGRESS', '2025-06-02', '2025-07-05', FALSE, NULL, NULL, 7, '2025-06-02 09:00:00', NOW()),

-- Project 1026: Student Mental Health Portal
(4125, 1026, 2051, 'Mental Health Resources', 'Compile mental health resources and services.', 'IN_PROGRESS', '2025-02-22', '2025-03-15', FALSE, NULL, NULL, 8, '2025-02-22 10:00:00', NOW()),
(4126, 1026, 2051, 'Anonymous Chat System', 'Design anonymous counseling chat.', 'IN_PROGRESS', '2025-03-16', '2025-03-31', FALSE, NULL, NULL, 8, '2025-03-16 09:00:00', NOW()),
(4127, 1026, 2051, 'Self-Assessment Tools', 'Create mental health screening tools.', 'IN_PROGRESS', '2025-04-01', '2025-04-08', FALSE, NULL, NULL, 8, '2025-04-01 09:00:00', NOW()),
(4128, 1026, 2052, 'Secure Messaging Platform', 'Build HIPAA-compliant messaging.', 'IN_PROGRESS', '2025-04-09', '2025-06-05', FALSE, NULL, NULL, 8, '2025-04-09 09:00:00', NOW()),
(4129, 1026, 2052, 'Crisis Intervention Module', 'Implement crisis detection and alerts.', 'IN_PROGRESS', '2025-06-06', '2025-07-10', FALSE, NULL, NULL, 8, '2025-06-06 09:00:00', NOW()),

-- Project 1027: Automated Quiz Generator
(4130, 1027, 2053, 'NLP Framework Selection', 'Evaluate spaCy, NLTK, transformers.', 'COMPLETED', '2025-02-25', '2025-03-15', FALSE, NULL, NULL, 9, '2025-02-25 10:00:00', NOW()),
(4131, 1027, 2053, 'Question Generation Research', 'Research question generation techniques.', 'IN_PROGRESS', '2025-03-16', '2025-04-05', FALSE, NULL, NULL, 9, '2025-03-16 09:00:00', NOW()),
(4132, 1027, 2053, 'Answer Evaluation Design', 'Design auto-grading algorithms.', 'IN_PROGRESS', '2025-04-06', '2025-04-11', FALSE, NULL, NULL, 9, '2025-04-06 09:00:00', NOW()),
(4133, 1027, 2054, 'Quiz Generation Engine', 'Implement AI-powered quiz generator.', 'IN_PROGRESS', '2025-04-12', '2025-06-10', FALSE, NULL, NULL, 9, '2025-04-12 09:00:00', NOW()),
(4134, 1027, 2054, 'LMS Integration', 'Connect to Canvas/Moodle/Blackboard.', 'IN_PROGRESS', '2025-06-11', '2025-07-15', FALSE, NULL, NULL, 9, '2025-06-11 09:00:00', NOW()),

-- Project 1028: Campus Energy Monitor
(4135, 1028, 2055, 'Energy Audit', 'Conduct comprehensive campus energy audit.', 'IN_PROGRESS', '2025-02-28', '2025-03-20', FALSE, NULL, NULL, 10, '2025-02-28 10:00:00', NOW()),
(4136, 1028, 2055, 'Sensor Network Design', 'Design IoT sensor network for buildings.', 'IN_PROGRESS', '2025-03-21', '2025-04-05', FALSE, NULL, NULL, 10, '2025-03-21 09:00:00', NOW()),
(4137, 1028, 2055, 'Data Visualization Planning', 'Plan energy consumption visualizations.', 'IN_PROGRESS', '2025-04-06', '2025-04-14', FALSE, NULL, NULL, 10, '2025-04-06 09:00:00', NOW()),
(4138, 1028, 2056, 'Real-time Monitoring Dashboard', 'Build live energy monitoring dashboard.', 'IN_PROGRESS', '2025-04-15', '2025-06-15', FALSE, NULL, NULL, 10, '2025-04-15 09:00:00', NOW()),
(4139, 1028, 2056, 'Optimization Recommendations', 'Implement AI-driven energy savings tips.', 'IN_PROGRESS', '2025-06-16', '2025-07-20', FALSE, NULL, NULL, 10, '2025-06-16 09:00:00', NOW()),

-- Project 1029: Peer Review System
(4140, 1029, 2057, 'Review Workflow Design', 'Design peer review workflow process.', 'IN_PROGRESS', '2025-03-03', '2025-03-25', FALSE, NULL, NULL, 3, '2025-03-03 10:00:00', NOW()),
(4141, 1029, 2057, 'Rubric Framework', 'Create customizable review rubrics.', 'IN_PROGRESS', '2025-03-26', '2025-04-10', FALSE, NULL, NULL, 3, '2025-03-26 09:00:00', NOW()),
(4142, 1029, 2057, 'Anonymous Review System', 'Implement blind peer review option.', 'IN_PROGRESS', '2025-04-11', '2025-04-18', FALSE, NULL, NULL, 3, '2025-04-11 09:00:00', NOW()),
(4143, 1029, 2058, 'Assignment Submission Portal', 'Build file upload and management system.', 'IN_PROGRESS', '2025-04-19', '2025-06-20', FALSE, NULL, NULL, 3, '2025-04-19 09:00:00', NOW()),
(4144, 1029, 2058, 'Quality Control Mechanisms', 'Implement review quality scoring.', 'IN_PROGRESS', '2025-06-21', '2025-07-25', FALSE, NULL, NULL, 3, '2025-06-21 09:00:00', NOW()),

-- Project 1030: Smart Lost & Found Service
(4145, 1030, 2059, 'Image Recognition Research', 'Research image matching algorithms.', 'IN_PROGRESS', '2025-03-05', '2025-03-25', FALSE, NULL, NULL, 4, '2025-03-05 10:00:00', NOW()),
(4146, 1030, 2059, 'Item Database Design', 'Design database for lost/found items.', 'IN_PROGRESS', '2025-03-26', '2025-04-10', FALSE, NULL, NULL, 4, '2025-03-26 09:00:00', NOW()),
(4147, 1030, 2059, 'Notification System Design', 'Design alert system for matches.', 'IN_PROGRESS', '2025-04-11', '2025-04-20', FALSE, NULL, NULL, 4, '2025-04-11 09:00:00', NOW()),
(4148, 1030, 2060, 'AI Image Matching', 'Implement computer vision matching.', 'IN_PROGRESS', '2025-04-21', '2025-06-20', FALSE, NULL, NULL, 4, '2025-04-21 09:00:00', NOW()),
(4149, 1030, 2060, 'Mobile App Development', 'Build mobile app for reporting items.', 'IN_PROGRESS', '2025-06-21', '2025-07-30', FALSE, NULL, NULL, 4, '2025-06-21 09:00:00', NOW()),

-- Project 1031: Campus Transportation App
(4150, 1031, 2061, 'Route Analysis', 'Analyze current shuttle routes and usage.', 'IN_PROGRESS', '2025-03-08', '2025-03-28', FALSE, NULL, NULL, 5, '2025-03-08 10:00:00', NOW()),
(4151, 1031, 2061, 'GPS Tracking Setup', 'Install GPS trackers on buses.', 'IN_PROGRESS', '2025-03-29', '2025-04-15', FALSE, NULL, NULL, 5, '2025-03-29 09:00:00', NOW()),
(4152, 1031, 2061, 'Route Optimization Algorithm', 'Design dynamic route optimization.', 'IN_PROGRESS', '2025-04-16', '2025-04-23', FALSE, NULL, NULL, 5, '2025-04-16 09:00:00', NOW()),
(4153, 1031, 2062, 'Real-time Tracking App', 'Build real-time bus tracking app.', 'IN_PROGRESS', '2025-04-24', '2025-07-01', FALSE, NULL, NULL, 5, '2025-04-24 09:00:00', NOW()),
(4154, 1031, 2062, 'Arrival Prediction Engine', 'Implement ML-based ETA predictions.', 'IN_PROGRESS', '2025-07-02', '2025-08-05', FALSE, NULL, NULL, 5, '2025-07-02 09:00:00', NOW()),

-- Project 1032: Study Group Matching Platform
(4155, 1032, 2063, 'Student Profile System', 'Design comprehensive student profiles.', 'IN_PROGRESS', '2025-03-10', '2025-03-30', FALSE, NULL, NULL, 6, '2025-03-10 10:00:00', NOW()),
(4156, 1032, 2063, 'Matching Algorithm', 'Develop compatibility matching algorithm.', 'IN_PROGRESS', '2025-03-31', '2025-04-18', FALSE, NULL, NULL, 6, '2025-03-31 09:00:00', NOW()),
(4157, 1032, 2063, 'Schedule Coordination', 'Build shared schedule finder.', 'IN_PROGRESS', '2025-04-19', '2025-04-25', FALSE, NULL, NULL, 6, '2025-04-19 09:00:00', NOW()),
(4158, 1032, 2064, 'Group Communication Tools', 'Implement chat and video features.', 'IN_PROGRESS', '2025-04-26', '2025-07-05', FALSE, NULL, NULL, 6, '2025-04-26 09:00:00', NOW()),
(4159, 1032, 2064, 'Study Session Tracking', 'Build study session logging and analytics.', 'IN_PROGRESS', '2025-07-06', '2025-08-10', FALSE, NULL, NULL, 6, '2025-07-06 09:00:00', NOW()),

-- Project 1033: Lab Equipment Booking System
(4160, 1033, 2065, 'Equipment Inventory', 'Create comprehensive equipment catalog.', 'IN_PROGRESS', '2025-03-12', '2025-04-05', FALSE, NULL, NULL, 7, '2025-03-12 10:00:00', NOW()),
(4161, 1033, 2065, 'Booking Rules Engine', 'Define booking policies and constraints.', 'IN_PROGRESS', '2025-04-06', '2025-04-20', FALSE, NULL, NULL, 7, '2025-04-06 09:00:00', NOW()),
(4162, 1033, 2065, 'Calendar Interface Design', 'Design booking calendar UI.', 'IN_PROGRESS', '2025-04-21', '2025-04-28', FALSE, NULL, NULL, 7, '2025-04-21 09:00:00', NOW()),
(4163, 1033, 2066, 'Booking Management System', 'Build complete reservation system.', 'IN_PROGRESS', '2025-04-29', '2025-07-10', FALSE, NULL, NULL, 7, '2025-04-29 09:00:00', NOW()),
(4164, 1033, 2066, 'Usage Analytics', 'Implement equipment usage tracking.', 'IN_PROGRESS', '2025-07-11', '2025-08-15', FALSE, NULL, NULL, 7, '2025-07-11 09:00:00', NOW()),

-- Project 1034: Campus Marketplace
(4165, 1034, 2067, 'Marketplace Business Rules', 'Define listing and transaction rules.', 'IN_PROGRESS', '2025-03-15', '2025-04-05', FALSE, NULL, NULL, 8, '2025-03-15 10:00:00', NOW()),
(4166, 1034, 2067, 'Payment Gateway Integration', 'Integrate Stripe/PayPal payments.', 'IN_PROGRESS', '2025-04-06', '2025-04-25', FALSE, NULL, NULL, 8, '2025-04-06 09:00:00', NOW()),
(4167, 1034, 2067, 'Safety and Moderation', 'Design content moderation system.', 'IN_PROGRESS', '2025-04-26', '2025-05-01', FALSE, NULL, NULL, 8, '2025-04-26 09:00:00', NOW()),
(4168, 1034, 2068, 'Listing Management', 'Build item listing and search features.', 'IN_PROGRESS', '2025-05-02', '2025-07-15', FALSE, NULL, NULL, 8, '2025-05-02 09:00:00', NOW()),
(4169, 1034, 2068, 'Rating and Review System', 'Implement buyer/seller ratings.', 'IN_PROGRESS', '2025-07-16', '2025-08-20', FALSE, NULL, NULL, 8, '2025-07-16 09:00:00', NOW()),

-- Project 1035: Virtual Lab Simulation
(4170, 1035, 2069, '3D Lab Environment Design', 'Design virtual lab layouts and equipment.', 'IN_PROGRESS', '2025-03-18', '2025-04-10', FALSE, NULL, NULL, 9, '2025-03-18 10:00:00', NOW()),
(4171, 1035, 2069, 'Physics Engine Setup', 'Configure physics simulation engine.', 'IN_PROGRESS', '2025-04-11', '2025-04-30', FALSE, NULL, NULL, 9, '2025-04-11 09:00:00', NOW()),
(4172, 1035, 2069, 'Experiment Scenarios', 'Create interactive experiment scenarios.', 'IN_PROGRESS', '2025-05-01', '2025-05-05', FALSE, NULL, NULL, 9, '2025-05-01 09:00:00', NOW()),
(4173, 1035, 2070, 'Interactive Lab Simulation', 'Build complete 3D lab simulator.', 'IN_PROGRESS', '2025-05-06', '2025-08-01', FALSE, NULL, NULL, 9, '2025-05-06 09:00:00', NOW()),
(4174, 1035, 2070, 'Data Collection and Grading', 'Implement experiment data capture.', 'IN_PROGRESS', '2025-08-02', '2025-08-25', FALSE, NULL, NULL, 9, '2025-08-02 09:00:00', NOW()),

-- Project 1036: Academic Integrity Checker
(4175, 1036, 2071, 'Plagiarism Database Setup', 'Setup comprehensive plagiarism database.', 'IN_PROGRESS', '2025-03-20', '2025-04-10', FALSE, NULL, NULL, 10, '2025-03-20 10:00:00', NOW()),
(4176, 1036, 2071, 'Text Analysis Algorithms', 'Develop advanced text comparison algorithms.', 'IN_PROGRESS', '2025-04-11', '2025-05-01', FALSE, NULL, NULL, 10, '2025-04-11 09:00:00', NOW()),
(4177, 1036, 2071, 'Citation Analysis', 'Build citation checking and validation.', 'IN_PROGRESS', '2025-05-02', '2025-05-08', FALSE, NULL, NULL, 10, '2025-05-02 09:00:00', NOW()),
(4178, 1036, 2072, 'Similarity Detection Engine', 'Implement multi-source similarity checking.', 'IN_PROGRESS', '2025-05-09', '2025-08-05', FALSE, NULL, NULL, 10, '2025-05-09 09:00:00', NOW()),
(4179, 1036, 2072, 'Detailed Reporting', 'Create comprehensive plagiarism reports.', 'IN_PROGRESS', '2025-08-06', '2025-08-30', FALSE, NULL, NULL, 10, '2025-08-06 09:00:00', NOW()),

-- Project 1037: Campus Safety Alert System
(4180, 1037, 2073, 'Emergency Protocol Design', 'Design emergency response protocols.', 'IN_PROGRESS', '2025-03-22', '2025-04-12', FALSE, NULL, NULL, 3, '2025-03-22 10:00:00', NOW()),
(4181, 1037, 2073, 'Geofencing Setup', 'Configure location-based alert zones.', 'IN_PROGRESS', '2025-04-13', '2025-05-01', FALSE, NULL, NULL, 3, '2025-04-13 09:00:00', NOW()),
(4182, 1037, 2073, 'Mass Notification System', 'Design multi-channel alert system.', 'IN_PROGRESS', '2025-05-02', '2025-05-10', FALSE, NULL, NULL, 3, '2025-05-02 09:00:00', NOW()),
(4183, 1037, 2074, 'Emergency Alert App', 'Build mobile emergency alert app.', 'IN_PROGRESS', '2025-05-11', '2025-08-10', FALSE, NULL, NULL, 3, '2025-05-11 09:00:00', NOW()),
(4184, 1037, 2074, 'Safety Check-in Feature', 'Implement safety status reporting.', 'IN_PROGRESS', '2025-08-11', '2025-09-05', FALSE, NULL, NULL, 3, '2025-08-11 09:00:00', NOW()),

-- Project 1038: Career Path Advisor
(4185, 1038, 2075, 'Career Data Collection', 'Gather industry career path data.', 'IN_PROGRESS', '2025-03-25', '2025-04-15', FALSE, NULL, NULL, 4, '2025-03-25 10:00:00', NOW()),
(4186, 1038, 2075, 'Personality Assessment', 'Design career personality tests.', 'IN_PROGRESS', '2025-04-16', '2025-05-05', FALSE, NULL, NULL, 4, '2025-04-16 09:00:00', NOW()),
(4187, 1038, 2075, 'Skill Evaluation Framework', 'Create skill assessment tools.', 'IN_PROGRESS', '2025-05-06', '2025-05-13', FALSE, NULL, NULL, 4, '2025-05-06 09:00:00', NOW()),
(4188, 1038, 2076, 'AI Career Matching', 'Implement AI-based career recommendations.', 'IN_PROGRESS', '2025-05-14', '2025-08-15', FALSE, NULL, NULL, 4, '2025-05-14 09:00:00', NOW()),
(4189, 1038, 2076, 'Industry Insights Dashboard', 'Build career trends and insights dashboard.', 'IN_PROGRESS', '2025-08-16', '2025-09-10', FALSE, NULL, NULL, 4, '2025-08-16 09:00:00', NOW()),

-- Project 1039: Campus Wi-Fi Analytics
(4190, 1039, 2077, 'Network Infrastructure Audit', 'Audit campus Wi-Fi infrastructure.', 'IN_PROGRESS', '2025-03-28', '2025-04-18', FALSE, NULL, NULL, 5, '2025-03-28 10:00:00', NOW()),
(4191, 1039, 2077, 'Monitoring Tool Selection', 'Evaluate network monitoring tools.', 'IN_PROGRESS', '2025-04-19', '2025-05-08', FALSE, NULL, NULL, 5, '2025-04-19 09:00:00', NOW()),
(4192, 1039, 2077, 'Analytics Framework', 'Design network analytics framework.', 'IN_PROGRESS', '2025-05-09', '2025-05-16', FALSE, NULL, NULL, 5, '2025-05-09 09:00:00', NOW()),
(4193, 1039, 2078, 'Performance Dashboard', 'Build real-time network performance dashboard.', 'IN_PROGRESS', '2025-05-17', '2025-08-20', FALSE, NULL, NULL, 5, '2025-05-17 09:00:00', NOW()),
(4194, 1039, 2078, 'Optimization Recommendations', 'Implement AI-driven optimization tips.', 'IN_PROGRESS', '2025-08-21', '2025-09-15', FALSE, NULL, NULL, 5, '2025-08-21 09:00:00', NOW()),

-- Project 1040: Student Budget Planner
(4195, 1040, 2079, 'Student Finance Survey', 'Survey student spending patterns.', 'IN_PROGRESS', '2025-03-30', '2025-04-20', FALSE, NULL, NULL, 6, '2025-03-30 10:00:00', NOW()),
(4196, 1040, 2079, 'Budget Template Design', 'Create customizable budget templates.', 'IN_PROGRESS', '2025-04-21', '2025-05-10', FALSE, NULL, NULL, 6, '2025-04-21 09:00:00', NOW()),
(4197, 1040, 2079, 'Bank Integration Planning', 'Plan bank account integration.', 'IN_PROGRESS', '2025-05-11', '2025-05-18', FALSE, NULL, NULL, 6, '2025-05-11 09:00:00', NOW()),
(4198, 1040, 2080, 'Expense Tracking App', 'Build expense categorization and tracking.', 'IN_PROGRESS', '2025-05-19', '2025-08-25', FALSE, NULL, NULL, 6, '2025-05-19 09:00:00', NOW()),
(4199, 1040, 2080, 'Financial Insights Engine', 'Implement AI spending insights.', 'IN_PROGRESS', '2025-08-26', '2025-09-20', FALSE, NULL, NULL, 6, '2025-08-26 09:00:00', NOW());



-- ==========================================
-- TASK ASSIGNEES (Many-to-Many)
-- ==========================================
-- Task 4001: All students on project 1001
INSERT INTO task_assignees (task_id, user_id) VALUES
(4001, 11), (4001, 12), (4001, 13),

-- Task 4002: Students 11 and 12
(4002, 11), (4002, 12),

-- Task 4003: Student 13
(4003, 13),

-- Task 4004: Students 11 and 13
(4004, 11), (4004, 13),

-- Task 4005: All students
(4005, 11), (4005, 12), (4005, 13),

-- Task 4006: Student 12
(4006, 12),

-- Task 4007: Students 15 and 17
(4007, 15), (4007, 17),

-- Task 4008: Student 18
(4008, 18),

-- Task 4009: All students on project 1002
(4009, 15), (4009, 17), (4009, 18),

-- Task 4010: Students 15 and 18
(4010, 15), (4010, 18),

-- Task 4011: Student 17
(4011, 17),

-- Task 4012: All students on project 1006
(4012, 31), (4012, 32), (4012, 33),

-- Task 4013: Student 31
(4013, 31),

-- Task 4014: Students 32 and 33
(4014, 32), (4014, 33),

-- Task 4015: Student 31 and 32
(4015, 31), (4015, 32),

-- Task 4016: All students
(4016, 31), (4016, 32), (4016, 33),

-- Task 4017: Students 34 and 35
(4017, 34), (4017, 35),

-- Task 4018: Student 36
(4018, 36),

-- Task 4019: Students 34 and 36
(4019, 34), (4019, 36),

-- Task 4020: Student 35
(4020, 35),

-- Task 4021: Students 37 and 38
(4021, 37), (4021, 38),

-- Task 4022: Student 39
(4022, 39),

-- Task 4023: Students 37 and 39
(4023, 37), (4023, 39),

-- Task 4024: Student 38
(4024, 38),

-- Task assignees for Project 1003 tasks
(4025, 22), (4025, 23), (4025, 24),
(4026, 22), (4026, 23),
(4027, 24),
(4028, 22), (4028, 24),
(4029, 23), (4029, 24),

-- Task assignees for Project 1004 tasks
(4030, 25), (4030, 26),
(4031, 27),
(4032, 25), (4032, 27),
(4033, 26), (4033, 27),
(4034, 25), (4034, 26), (4034, 27),

-- Task assignees for Project 1005 tasks
(4035, 28), (4035, 29),
(4036, 30),
(4037, 28), (4037, 30),
(4038, 29), (4038, 30),
(4039, 28), (4039, 29), (4039, 30),

-- Task assignees for Project 1009 tasks
(4040, 40), (4040, 41),
(4041, 42),
(4042, 40), (4042, 42),
(4043, 41), (4043, 42),
(4044, 40), (4044, 41), (4044, 42),

-- Task assignees for Project 1010 tasks
(4045, 43), (4045, 44),
(4046, 45),
(4047, 43), (4047, 45),
(4048, 44), (4048, 45),
(4049, 43), (4049, 44), (4049, 45),

-- Task assignees for Project 1011 tasks (COMPLETED)
(4050, 46), (4050, 47), (4050, 48),
(4051, 46), (4051, 47),
(4052, 48),
(4053, 46), (4053, 48),
(4054, 47), (4054, 48),

-- Task assignees for Project 1012 tasks
(4055, 11), (4055, 14),
(4056, 16),
(4057, 11), (4057, 16),
(4058, 14), (4058, 16),
(4059, 11), (4059, 14), (4059, 16),

-- Task assignees for Project 1013 tasks
(4060, 12), (4060, 15),
(4061, 19),
(4062, 12), (4062, 19),
(4063, 15), (4063, 19),
(4064, 12), (4064, 15), (4064, 19),

-- Task assignees for Project 1014 tasks
(4065, 13), (4065, 17),
(4066, 20),
(4067, 13), (4067, 20),
(4068, 17), (4068, 20),
(4069, 13), (4069, 17), (4069, 20),

-- Task assignees for Project 1015 tasks
(4070, 18), (4070, 21),
(4071, 22),
(4072, 18), (4072, 22),
(4073, 21), (4073, 22),
(4074, 18), (4074, 21), (4074, 22),

-- Task assignees for Project 1016 tasks (LOCKED)
(4075, 23), (4075, 24),
(4076, 25),
(4077, 23), (4077, 25),
(4078, 24), (4078, 25),
(4079, 23), (4079, 24), (4079, 25),

-- Task assignees for Project 1017 tasks
(4080, 26), (4080, 27),
(4081, 28),
(4082, 26), (4082, 28),
(4083, 27), (4083, 28),
(4084, 26), (4084, 27), (4084, 28),

-- Task assignees for Project 1018 tasks
(4085, 29), (4085, 30),
(4086, 31),
(4087, 29), (4087, 31),
(4088, 30), (4088, 31),
(4089, 29), (4089, 30), (4089, 31),

-- Task assignees for Project 1019 tasks
(4090, 32), (4090, 33),
(4091, 34),
(4092, 32), (4092, 34),
(4093, 33), (4093, 34),
(4094, 32), (4094, 33), (4094, 34),

-- Task assignees for Project 1020 tasks
(4095, 35), (4095, 36),
(4096, 37),
(4097, 35), (4097, 37),
(4098, 36), (4098, 37),
(4099, 35), (4099, 36), (4099, 37),

-- Task assignees for Project 1021 tasks
(4100, 38), (4100, 39),
(4101, 40),
(4102, 38), (4102, 40),
(4103, 39), (4103, 40),
(4104, 38), (4104, 39), (4104, 40),

-- Task assignees for Project 1022 tasks
(4105, 41), (4105, 42),
(4106, 43),
(4107, 41), (4107, 43),
(4108, 42), (4108, 43),
(4109, 41), (4109, 42), (4109, 43),

-- Task assignees for Project 1023 tasks
(4110, 44), (4110, 45),
(4111, 46),
(4112, 44), (4112, 46),
(4113, 45), (4113, 46),
(4114, 44), (4114, 45), (4114, 46),

-- Task assignees for Project 1024 tasks
(4115, 47), (4115, 48),
(4116, 11),
(4117, 47), (4117, 11),
(4118, 48), (4118, 11),
(4119, 47), (4119, 48), (4119, 11),

-- Task assignees for Project 1025 tasks
(4120, 12), (4120, 13),
(4121, 15),
(4122, 12), (4122, 15),
(4123, 13), (4123, 15),
(4124, 12), (4124, 13), (4124, 15),

-- Task assignees for Project 1026 tasks
(4125, 17), (4125, 18),
(4126, 22),
(4127, 17), (4127, 22),
(4128, 18), (4128, 22),
(4129, 17), (4129, 18), (4129, 22),

-- Task assignees for Project 1027 tasks
(4130, 23), (4130, 24),
(4131, 25),
(4132, 23), (4132, 25),
(4133, 24), (4133, 25),
(4134, 23), (4134, 24), (4134, 25),

-- Task assignees for Project 1028 tasks
(4135, 26), (4135, 27),
(4136, 28),
(4137, 26), (4137, 28),
(4138, 27), (4138, 28),
(4139, 26), (4139, 27), (4139, 28),

-- Task assignees for Project 1029 tasks
(4140, 29), (4140, 30),
(4141, 31),
(4142, 29), (4142, 31),
(4143, 30), (4143, 31),
(4144, 29), (4144, 30), (4144, 31),

-- Task assignees for Project 1030 tasks
(4145, 32), (4145, 33),
(4146, 34),
(4147, 32), (4147, 34),
(4148, 33), (4148, 34),
(4149, 32), (4149, 33), (4149, 34),

-- Task assignees for Project 1031 tasks
(4150, 35), (4150, 36),
(4151, 37),
(4152, 35), (4152, 37),
(4153, 36), (4153, 37),
(4154, 35), (4154, 36), (4154, 37),

-- Task assignees for Project 1032 tasks
(4155, 38), (4155, 39),
(4156, 40),
(4157, 38), (4157, 40),
(4158, 39), (4158, 40),
(4159, 38), (4159, 39), (4159, 40),

-- Task assignees for Project 1033 tasks
(4160, 41), (4160, 42),
(4161, 43),
(4162, 41), (4162, 43),
(4163, 42), (4163, 43),
(4164, 41), (4164, 42), (4164, 43),

-- Task assignees for Project 1034 tasks
(4165, 44), (4165, 45),
(4166, 46),
(4167, 44), (4167, 46),
(4168, 45), (4168, 46),
(4169, 44), (4169, 45), (4169, 46),

-- Task assignees for Project 1035 tasks
(4170, 47), (4170, 48),
(4171, 11),
(4172, 47), (4172, 11),
(4173, 48), (4173, 11),
(4174, 47), (4174, 48), (4174, 11),

-- Task assignees for Project 1036 tasks
(4175, 12), (4175, 13),
(4176, 15),
(4177, 12), (4177, 15),
(4178, 13), (4178, 15),
(4179, 12), (4179, 13), (4179, 15),

-- Task assignees for Project 1037 tasks
(4180, 17), (4180, 18),
(4181, 22),
(4182, 17), (4182, 22),
(4183, 18), (4183, 22),
(4184, 17), (4184, 18), (4184, 22),

-- Task assignees for Project 1038 tasks
(4185, 23), (4185, 24),
(4186, 25),
(4187, 23), (4187, 25),
(4188, 24), (4188, 25),
(4189, 23), (4189, 24), (4189, 25),

-- Task assignees for Project 1039 tasks
(4190, 26), (4190, 27),
(4191, 28),
(4192, 26), (4192, 28),
(4193, 27), (4193, 28),
(4194, 26), (4194, 27), (4194, 28),

-- Task assignees for Project 1040 tasks
(4195, 29), (4195, 30),
(4196, 32),
(4197, 29), (4197, 32),
(4198, 30), (4198, 32),
(4199, 29), (4199, 30), (4199, 32);

-- ==========================================
-- REPORTS
-- ==========================================
-- Reports for Task 4001 (COMPLETED task, should have multiple reports)
INSERT INTO reports (id, project_id, milestone_id, task_id, author_id, submitted_by_id, title, content, status, start_date, end_date, is_locked, locked_by_id, locked_at, created_by_id, submitted_at, report_date, created_at, updated_at) VALUES
(5001, 1001, 2001, 4001, 11, 11, 'Week 1 Progress - Stakeholder Interviews', 'Completed 5 stakeholder interviews with physiotherapists and patients. Key findings include the need for secure video quality and easy-to-use interface for elderly patients.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 11, '2025-01-13 16:30:00', '2025-01-13 16:00:00', '2025-01-13 16:30:00', NOW()),
(5002, 1001, 2001, 4001, 12, 12, 'Requirements Documentation Draft', 'Drafted initial functional requirements document covering authentication, video sessions, and patient records management. Total of 28 functional requirements identified.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 12, '2025-01-17 14:20:00', '2025-01-17 14:00:00', '2025-01-17 14:20:00', NOW()),
(5003, 1001, 2001, 4001, 13, 13, 'Final Requirements Report', 'Consolidated all requirements from team members. Completed requirements gathering phase with sign-off from instructor. Ready to move to technology selection.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 13, '2025-01-20 17:45:00', '2025-01-20 17:00:00', '2025-01-20 17:45:00', NOW()),

-- Reports for Task 4002 (COMPLETED task)
(5004, 1001, 2001, 4002, 11, 11, 'Technology Research - Video Streaming', 'Researched WebRTC, Agora, and Twilio for video streaming. WebRTC appears most suitable for our use case due to peer-to-peer capabilities and cost effectiveness.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 11, '2025-01-28 15:00:00', '2025-01-28 14:30:00', '2025-01-28 15:00:00', NOW()),
(5005, 1001, 2001, 4002, 12, 12, 'Security Technology Evaluation', 'Evaluated encryption options including AES-256 and TLS 1.3. Reviewed HIPAA compliance requirements and selected technologies that meet healthcare standards.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 12, '2025-02-03 16:30:00', '2025-02-03 16:00:00', '2025-02-03 16:30:00', NOW()),

-- Reports for Task 4003 (IN_PROGRESS)
(5006, 1001, 2001, 4003, 13, 13, 'Architecture Design Progress', 'Completed microservices breakdown: Auth Service, Video Service, Patient Records Service, Analytics Service. Working on API gateway design and inter-service communication.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 13, '2025-02-15 15:30:00', '2025-02-15 15:00:00', '2025-02-15 15:30:00', NOW()),

-- Reports for Task 4004 (IN_PROGRESS)
(5007, 1001, 2002, 4004, 11, 11, 'Backend API Sprint 1', 'Implemented user authentication endpoints with JWT tokens. Added password hashing with bcrypt. All authentication tests passing. Next: video session management APIs.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 11, '2025-03-05 17:00:00', '2025-03-05 16:30:00', '2025-03-05 17:00:00', NOW()),
(5008, 1001, 2002, 4004, 13, 13, 'Video Session API Development', 'Working on WebRTC signaling server integration. Implemented room creation and peer connection endpoints. Encountering some NAT traversal issues that need TURN server configuration.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 13, '2025-03-15 18:30:00', '2025-03-15 18:00:00', '2025-03-15 18:30:00', NOW()),

-- Reports for Task 4005 (IN_PROGRESS)
(5009, 1001, 2002, 4005, 12, 12, 'Frontend UI Components', 'Created reusable React components for video player, chat interface, and dashboard widgets. Using Material-UI for consistent design. Mobile responsiveness implemented.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 12, '2025-03-20 16:00:00', '2025-03-20 15:30:00', '2025-03-20 16:00:00', NOW()),

-- Reports for Task 4007 (COMPLETED - Project 1002)
(5010, 1002, 2003, 4007, 15, 15, 'AR Framework Comparison Report', 'Evaluated three AR frameworks. A-Frame chosen for web-based approach with good documentation. Unity AR Foundation powerful but overkill for our needs. AR.js lacks features.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 15, '2025-01-25 14:30:00', '2025-01-25 14:00:00', '2025-01-25 14:30:00', NOW()),

-- Reports for Task 4008 (COMPLETED - Project 1002)
(5011, 1002, 2003, 4008, 18, 18, 'Beacon Placement Strategy', 'Designed optimal beacon placement for 5 main campus buildings. Calculated 15-meter spacing for 95% accuracy. Created floor plans with beacon locations and signal overlap zones.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 18, '2025-02-10 17:15:00', '2025-02-10 17:00:00', '2025-02-10 17:15:00', NOW()),

-- Reports for Task 4009 (IN_PROGRESS - Project 1002)
(5012, 1002, 2003, 4009, 17, 17, 'Accessibility Analysis', 'Researched WCAG 2.1 AA standards for AR interfaces. Identified need for audio cues, high contrast mode, and alternative text-based navigation for visually impaired users.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 17, '2025-02-25 15:45:00', '2025-02-25 15:00:00', '2025-02-25 15:45:00', NOW()),

-- Reports for Completed Project 1006 tasks
(5013, 1006, 2011, 4012, 31, 31, 'Sustainability Metrics Standards', 'Compiled comprehensive list of carbon footprint calculation methods from GHG Protocol and ISO 14064. Documented waste measurement standards and transportation impact formulas.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 31, '2024-09-20 16:00:00', '2024-09-20 15:30:00', '2024-09-20 16:00:00', NOW()),
(5014, 1006, 2011, 4013, 31, 31, 'Database Schema Design', 'Designed normalized PostgreSQL schema with tables for events, participants, metrics, and scores. Included indexes for performance optimization. Ready for implementation.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 31, '2024-10-08 17:30:00', '2024-10-08 17:00:00', '2024-10-08 17:30:00', NOW()),
(5015, 1006, 2012, 4014, 32, 32, 'Survey Module Implementation', 'Completed automated survey capture with QR code generation. Integrated with Google Forms API for easy survey creation. Response rate tracking implemented.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 32, '2024-11-15 16:45:00', '2024-11-15 16:00:00', '2024-11-15 16:45:00', NOW()),
(5016, 1006, 2012, 4015, 31, 31, 'Analytics Dashboard Complete', 'Finished real-time dashboard with Chart.js visualizations. Implemented live scoreboard with WebSocket updates. Performance optimized for 1000+ concurrent users.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 31, '2024-12-28 18:00:00', '2024-12-28 17:30:00', '2024-12-28 18:00:00', NOW()),
(5017, 1006, 2012, 4016, 33, 33, 'Final Mobile App Polish', 'Completed final UI/UX improvements based on beta testing feedback. Fixed 15 minor bugs. Performance improved by 40%. App ready for production deployment.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 33, '2025-01-28 19:00:00', '2025-01-28 18:30:00', '2025-01-28 19:00:00', NOW()),

-- Reports for Project 1007 tasks
(5018, 1007, 2013, 4017, 34, 34, 'Motion Capture Setup Complete', 'Successfully configured 8 OptiTrack cameras. Completed calibration with sub-millimeter accuracy. Tested with sample movements - tracking quality excellent.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 34, '2024-09-28 15:30:00', '2024-09-28 15:00:00', '2024-09-28 15:30:00', NOW()),
(5019, 1007, 2013, 4018, 36, 36, 'Biomechanics Database v1', 'Captured and processed 50 gold standard movement sequences from professional dancers and athletes. Database includes metadata, joint angles, and velocity profiles.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 36, '2024-10-23 16:30:00', '2024-10-23 16:00:00', '2024-10-23 16:30:00', NOW()),
(5020, 1007, 2014, 4019, 34, 34, 'Video Analysis Engine Progress', 'Implemented OpenPose integration for skeleton detection. Working on comparison algorithms between captured movements and gold standards. Accuracy currently at 87%.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 34, '2024-11-20 17:45:00', '2024-11-20 17:00:00', '2024-11-20 17:45:00', NOW()),

-- Reports for Project 1008 tasks  
(5021, 1008, 2015, 4021, 37, 37, 'Competency Framework Draft', 'Mapped 15 core competencies to learning outcomes. Created hierarchical structure with skill levels (beginner, intermediate, advanced). Reviewed by instructor.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 37, '2024-10-05 16:00:00', '2024-10-05 15:30:00', '2024-10-05 16:00:00', NOW()),
(5022, 1008, 2015, 4022, 39, 39, 'Evidence Upload System Beta', 'Built file upload with drag-and-drop support. Implemented automatic file type detection and thumbnail generation for images. Working on video preview feature.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 39, '2024-10-25 18:00:00', '2024-10-25 17:30:00', '2024-10-25 18:00:00', NOW()),
(5023, 1008, 2016, 4023, 37, 37, 'AI Summary Integration', 'Integrated OpenAI GPT-4 API for reflection summary generation. Implemented prompt engineering for educational context. Testing shows 92% instructor satisfaction rate.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 37, '2024-12-10 16:30:00', '2024-12-10 16:00:00', '2024-12-10 16:30:00', NOW()),

-- Reports for Project 1003 tasks
(5024, 1003, 2005, 4025, 22, 22, 'Studio Resource Survey Complete', 'Completed survey of all 12 studio spaces. Documented 45 pieces of equipment and analyzed usage patterns from last semester. Peak usage is 2-5 PM on weekdays.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 22, '2025-02-05 17:00:00', '2025-02-05 16:30:00', '2025-02-05 17:00:00', NOW()),
(5025, 1003, 2005, 4026, 22, 22, 'Scheduling Algorithm Design', 'Designed constraint satisfaction algorithm considering room capacity, equipment needs, lighting requirements, and time preferences. Initial testing shows 85% optimal allocation.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 22, '2025-02-25 18:00:00', '2025-02-25 17:30:00', '2025-02-25 18:00:00', NOW()),
(5026, 1003, 2005, 4027, 24, 24, 'UI Mockups Progress', 'Created responsive mockups for booking interface using Figma. Implemented calendar view, filter options, and equipment availability indicators. Ready for user testing.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 24, '2025-03-06 16:00:00', '2025-03-06 15:30:00', '2025-03-06 16:00:00', NOW()),

-- Reports for Project 1004 tasks
(5027, 1004, 2007, 4030, 25, 25, 'Pose Estimation Evaluation', 'Evaluated MediaPipe, OpenPose, and PoseNet. MediaPipe chosen for best mobile performance and accuracy. Achieves 30 FPS on mid-range smartphones with 95% landmark detection accuracy.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 25, '2025-02-10 17:30:00', '2025-02-10 17:00:00', '2025-02-10 17:30:00', NOW()),
(5028, 1004, 2007, 4031, 27, 27, 'Exercise Database v1', 'Compiled database of 50 common exercises with video demonstrations. Included correct form criteria, common mistakes, and modification options for different fitness levels.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 27, '2025-03-01 16:45:00', '2025-03-01 16:00:00', '2025-03-01 16:45:00', NOW()),
(5029, 1004, 2007, 4032, 25, 25, 'Scoring Rubric Development', 'Developed comprehensive scoring rubric evaluating joint angles, movement speed, range of motion, and form stability. Weighted scoring system with real-time feedback.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 25, '2025-03-11 15:30:00', '2025-03-11 15:00:00', '2025-03-11 15:30:00', NOW()),

-- Reports for Project 1005 tasks
(5030, 1005, 2009, 4035, 28, 28, 'Lab Protocol Documentation', 'Digitized 30 lab protocols with step-by-step instructions, safety procedures, and equipment checklists. All documents now in searchable database with multimedia support.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 28, '2025-02-20 17:00:00', '2025-02-20 16:30:00', '2025-02-20 17:00:00', NOW()),
(5031, 1005, 2009, 4036, 30, 30, 'AR Framework Evaluation', 'Evaluated Vuforia, ARKit, and ARCore. Vuforia selected for best cross-platform support and industrial lab use cases. Supports both marker-based and markerless tracking.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 30, '2025-03-10 16:30:00', '2025-03-10 16:00:00', '2025-03-10 16:30:00', NOW()),

-- Reports for Project 1009 tasks
(5032, 1009, 2017, 4040, 40, 40, 'ROS Environment Setup Complete', 'Configured ROS Noetic environment with Gazebo simulation. Successfully tested basic robot movements and sensor integration. All team members have working development environments.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 40, '2024-10-05 17:30:00', '2024-10-05 17:00:00', '2024-10-05 17:30:00', NOW()),
(5033, 1009, 2017, 4041, 42, 42, 'Safety Envelope Defined', 'Defined 3D safety boundaries for robot workspace. Implemented emergency stop procedures with redundant safety systems. Documented safety protocols for student interactions.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 42, '2024-10-25 16:45:00', '2024-10-25 16:00:00', '2024-10-25 16:45:00', NOW()),

-- Reports for Project 1010 tasks
(5034, 1010, 2019, 4045, 43, 43, 'Citation Database Ready', 'Setup PostgreSQL database for tracking citations with full text search. Integrated with Zotero and BibTeX formats. Supports automatic citation style formatting.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 43, '2024-10-20 17:00:00', '2024-10-20 16:30:00', '2024-10-20 17:00:00', NOW()),
(5035, 1010, 2019, 4046, 45, 45, 'AI Model Comparison', 'Evaluated GPT-4, Claude, and Llama for writing assistance. GPT-4 selected for best academic writing quality and citation accuracy. Implemented API integration with rate limiting.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 45, '2024-11-05 16:30:00', '2024-11-05 16:00:00', '2024-11-05 16:30:00', NOW()),

-- Reports for Project 1011 tasks (COMPLETED)
(5036, 1011, 2021, 4050, 46, 46, 'Badge System Design Complete', 'Designed 50 achievement badges across categories: consistency, intensity, form quality, and challenges. Implemented progressive difficulty tiers with visual variety.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 46, '2024-10-25 17:30:00', '2024-10-25 17:00:00', '2024-10-25 17:30:00', NOW()),
(5037, 1011, 2021, 4051, 46, 46, 'Sensor Integration Plan', 'Completed integration planning for Fitbit, Apple Watch, and gym equipment sensors. Defined data synchronization protocols and real-time verification methods.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 46, '2024-11-10 16:45:00', '2024-11-10 16:00:00', '2024-11-10 16:45:00', NOW()),
(5038, 1011, 2021, 4052, 48, 48, 'Leaderboard Algorithm', 'Developed fair ranking algorithm considering workout consistency, form quality, and challenge completion. Prevents gaming through verification requirements.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 48, '2024-11-19 17:00:00', '2024-11-19 16:30:00', '2024-11-19 17:00:00', NOW()),
(5039, 1011, 2022, 4053, 46, 46, 'Mobile Apps Released', 'Completed iOS and Android fitness tracking apps. Features include workout logging, real-time feedback, social features, and badge display. Beta testing with 100 users successful.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 46, '2025-01-20 18:00:00', '2025-01-20 17:30:00', '2025-01-20 18:00:00', NOW()),
(5040, 1011, 2022, 4054, 47, 47, 'Social Features Complete', 'Implemented peer challenges, team competitions, and social feed. Users can create/join workout groups. Engagement metrics show 70% daily active users.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 47, '2025-03-22 19:00:00', '2025-03-22 18:30:00', '2025-03-22 19:00:00', NOW()),

-- Reports for Project 1012 tasks
(5041, 1012, 2023, 4055, 11, 11, 'Accessibility Audit Report', 'Conducted comprehensive audit of 15 campus buildings. Identified 127 accessibility barriers including ramps, signage, and equipment issues. Created prioritized remediation plan.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 11, '2024-10-30 17:30:00', '2024-10-30 17:00:00', '2024-10-30 17:30:00', NOW()),
(5042, 1012, 2023, 4056, 16, 16, 'IoT Sensor Network Design', 'Designed sensor network with 200+ IoT devices tracking door usage, elevator wait times, and pathway traffic. Power and connectivity requirements documented.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 16, '2024-11-15 16:45:00', '2024-11-15 16:00:00', '2024-11-15 16:45:00', NOW()),
(5043, 1012, 2023, 4057, 11, 11, 'Dashboard Mockups Ready', 'Created interactive dashboard mockups with heatmaps showing accessibility bottlenecks, real-time equipment status, and historical trend analysis. User-tested with facilities team.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 11, '2024-11-24 17:00:00', '2024-11-24 16:30:00', '2024-11-24 17:00:00', NOW()),

-- Reports for Project 1013 tasks
(5044, 1013, 2025, 4060, 12, 12, 'Gait Recognition Research', 'Researched gait recognition algorithms with privacy focus. Identified OpenGait library as best option. Designed privacy-preserving feature extraction that stores only anonymized signatures.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 12, '2024-11-05 17:30:00', '2024-11-05 17:00:00', '2024-11-05 17:30:00', NOW()),
(5045, 1013, 2025, 4061, 19, 19, 'Camera Installation Progress', 'Installed 8 cameras in 4 rehearsal rooms with optimal lighting setup. Configured camera angles for full body capture. Testing shows 98% gait capture success rate.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 19, '2024-11-20 16:45:00', '2024-11-20 16:00:00', '2024-11-20 16:45:00', NOW()),

-- Reports for Project 1014 tasks
(5046, 1014, 2027, 4065, 13, 13, 'Matching Algorithm Research', 'Researched peer matching algorithms from dating apps and professional networking. Identified key factors: course overlap, learning style compatibility, availability, and goals.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 13, '2024-11-10 17:00:00', '2024-11-10 16:30:00', '2024-11-10 17:00:00', NOW()),
(5047, 1014, 2027, 4066, 20, 20, 'Mentorship Framework', 'Defined structured mentorship program with 12-week cycles, weekly check-ins, and milestone tracking. Created guidelines for mentor training and mentee expectations.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 20, '2024-11-25 16:30:00', '2024-11-25 16:00:00', '2024-11-25 16:30:00', NOW()),

-- Reports for Project 1015 tasks
(5048, 1015, 2029, 4070, 18, 18, 'Wearable Integration Analysis', 'Evaluated Fitbit, Apple Watch, Garmin APIs. All three support HR, sleep, activity data. Implemented unified data ingestion pipeline supporting multiple device types.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 18, '2024-11-20 17:30:00', '2024-11-20 17:00:00', '2024-11-20 17:30:00', NOW()),
(5049, 1015, 2029, 4071, 22, 22, 'Mental Health Metrics', 'Defined wellness metrics including PHQ-9 depression scale, GAD-7 anxiety scale, daily mood check-ins, and sleep quality indicators. Validated with counseling team.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 22, '2024-12-10 16:45:00', '2024-12-10 16:00:00', '2024-12-10 16:45:00', NOW()),
(5050, 1015, 2029, 4072, 18, 18, 'Privacy Framework Complete', 'Designed GDPR/HIPAA compliant data collection with explicit consent workflows. Implemented data anonymization for research use and automatic PII deletion after graduation.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 18, '2024-12-16 17:00:00', '2024-12-16 16:30:00', '2024-12-16 17:00:00', NOW()),

-- Reports for Project 1016 tasks (LOCKED)
(5051, 1016, 2031, 4075, 23, 23, 'Sketch Recognition Research', 'Researched Google QuickDraw, Sketch-RNN, and custom CNN approaches. Tested accuracy on whiteboard sketches achieving 82% recognition rate for common diagrams.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 23, '2024-11-25 17:30:00', '2024-11-25 17:00:00', '2024-11-25 17:30:00', NOW()),
(5052, 1016, 2031, 4076, 25, 25, 'WebSocket Collaboration', 'Implemented WebSocket-based real-time collaboration supporting 50+ concurrent users per board. Conflict resolution and cursor tracking working smoothly.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 25, '2024-12-15 16:45:00', '2024-12-15 16:00:00', '2024-12-15 16:45:00', NOW()),

-- Reports for Project 1017 tasks
(5053, 1017, 2033, 4080, 26, 26, 'Portfolio Templates', 'Created 5 professional portfolio templates for different disciplines: engineering, design, business, arts, sciences. All templates mobile-responsive and ADA compliant.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 26, '2024-12-01 17:00:00', '2024-12-01 16:30:00', '2024-12-01 17:00:00', NOW()),
(5054, 1017, 2033, 4081, 28, 28, 'Evidence Upload System', 'Built secure upload system supporting images, videos, documents, and links. Implemented version control with S3 storage. Max file size 100MB with progress indicators.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 28, '2024-12-20 16:30:00', '2024-12-20 16:00:00', '2024-12-20 16:30:00', NOW()),

-- Reports for Project 1018 tasks
(5055, 1018, 2035, 4085, 29, 29, 'Barcode System Deployed', 'Implemented QR code labeling for all lab equipment. Generated 500+ unique codes. Mobile scanning app tested with 99.8% scan success rate.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 29, '2024-12-05 17:30:00', '2024-12-05 17:00:00', '2024-12-05 17:30:00', NOW()),
(5056, 1018, 2035, 4086, 31, 31, 'Inventory Database Design', 'Designed comprehensive database schema tracking equipment, chemicals, consumables, and maintenance records. Includes location tracking and checkout history.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 31, '2024-12-20 16:45:00', '2024-12-20 16:00:00', '2024-12-20 16:45:00', NOW()),

-- Reports for Project 1019 tasks
(5057, 1019, 2037, 4090, 32, 32, 'Audio Analysis Research', 'Researched Essentia, Librosa, and commercial music analysis APIs. Identified key metrics: tempo accuracy, pitch consistency, dynamics control, and ensemble synchronization.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 32, '2024-12-10 17:00:00', '2024-12-10 16:30:00', '2024-12-10 17:00:00', NOW()),
(5058, 1019, 2037, 4091, 34, 34, 'Feedback Rubrics', 'Created detailed rubrics for timing (tempo consistency, rhythmic accuracy), tone (intonation, timbre quality), and coordination (ensemble balance, cueing). Faculty-reviewed.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 34, '2024-12-28 16:30:00', '2024-12-28 16:00:00', '2024-12-28 16:30:00', NOW()),

-- Reports for Project 1020 tasks
(5059, 1020, 2039, 4095, 35, 35, 'Employer Database v1', 'Compiled database of 200+ employers with 500+ internship positions. Includes company profiles, requirements, application deadlines, and past student placements.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 35, '2024-12-20 17:30:00', '2024-12-20 17:00:00', '2024-12-20 17:30:00', NOW()),
(5060, 1020, 2039, 4096, 37, 37, 'Skill Assessment Tool', 'Created comprehensive skill evaluation covering technical skills, soft skills, and industry-specific competencies. Automated scoring with 85% accuracy vs manual review.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 37, '2025-01-05 16:45:00', '2025-01-05 16:00:00', '2025-01-05 16:45:00', NOW()),

-- Reports for Project 1021 tasks
(5061, 1021, 2041, 4100, 38, 38, 'Library Requirements Report', 'Interviewed 15 librarians and 50 students. Key requirements: fast search, book availability notifications, study room booking, and personalized recommendations. Prioritized features documented.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 38, '2025-02-28 17:00:00', '2025-02-28 16:30:00', '2025-02-28 17:00:00', NOW()),
(5062, 1021, 2041, 4101, 40, 40, 'RFID Technology Research', 'Evaluated RFID readers from Zebra, Alien, and Impinj. Tested reading ranges and accuracy. Selected Zebra FX9600 for best performance with library materials.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 40, '2025-03-20 16:30:00', '2025-03-20 16:00:00', '2025-03-20 16:30:00', NOW()),

-- Reports for Project 1022 tasks
(5063, 1022, 2043, 4105, 41, 41, 'Parking Survey Complete', 'Surveyed 8 parking lots with 2,500 total spaces. Peak occupancy 85% during class hours. Identified problem areas and optimal sensor placement locations.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 41, '2025-03-05 17:30:00', '2025-03-05 17:00:00', '2025-03-05 17:30:00', NOW()),
(5064, 1022, 2043, 4106, 43, 43, 'Sensor Selection', 'Evaluated ultrasonic, magnetometer, and camera-based sensors. Selected magnetometer sensors for best accuracy and battery life. Cost-effective at $50 per space.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 43, '2025-03-25 16:45:00', '2025-03-25 16:00:00', '2025-03-25 16:45:00', NOW()),

-- Reports for Project 1023 tasks
(5065, 1023, 2045, 4110, 44, 44, 'Video Platform Comparison', 'Evaluated Zoom SDK ($2000 setup), WebRTC (free but complex), Agora ($99/mo). Selected Agora for best balance of features, cost, and implementation complexity.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 44, '2025-03-05 17:00:00', '2025-03-05 16:30:00', '2025-03-05 17:00:00', NOW()),
(5066, 1023, 2045, 4111, 46, 46, 'Breakout Room Design', 'Designed breakout room functionality supporting up to 50 concurrent rooms. Features include auto-assignment, timer-based rotation, and instructor monitoring capabilities.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 46, '2025-03-25 16:30:00', '2025-03-25 16:00:00', '2025-03-25 16:30:00', NOW()),

-- Reports for Project 1024 tasks
(5067, 1024, 2047, 4115, 47, 47, 'Event Discovery Design', 'Designed event discovery with filters for category, date, location, and organizer. Implemented tag-based search and trending events algorithm based on registration velocity.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 47, '2025-03-10 17:30:00', '2025-03-10 17:00:00', '2025-03-10 17:30:00', NOW()),
(5068, 1024, 2047, 4116, 11, 11, 'Calendar Integration Complete', 'Integrated with Google Calendar and Apple Calendar APIs. Events automatically sync with user calendars. Push notifications for event reminders working across platforms.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 11, '2025-03-30 16:45:00', '2025-03-30 16:00:00', '2025-03-30 16:45:00', NOW()),

-- Reports for Project 1025 tasks
(5069, 1025, 2049, 4120, 12, 12, 'Food Waste Analysis', 'Analyzed 3 months of cafeteria data. Average 150kg daily food waste with peaks on Mondays. Main sources: overproduction (40%), plate waste (35%), expired items (25%).', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 12, '2025-03-10 17:00:00', '2025-03-10 16:30:00', '2025-03-10 17:00:00', NOW()),
(5070, 1025, 2049, 4121, 15, 15, 'Sensor Installation', 'Installed weight sensors in 12 waste bins across 3 cafeterias. Real-time data transmission working. Accuracy within 2% compared to manual weighing.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 15, '2025-03-30 16:30:00', '2025-03-30 16:00:00', '2025-03-30 16:30:00', NOW()),

-- Reports for Project 1026 tasks
(5071, 1026, 2051, 4125, 17, 17, 'Mental Health Resources', 'Compiled comprehensive resource library: 50+ articles, 20 guided meditations, crisis hotlines, campus counseling info, and peer support groups. Categorized by concern type.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 17, '2025-03-15 17:30:00', '2025-03-15 17:00:00', '2025-03-15 17:30:00', NOW()),
(5072, 1026, 2051, 4126, 22, 22, 'Anonymous Chat Design', 'Designed anonymous chat system with end-to-end encryption. Counselors verified through secure login. Chat transcripts only stored with user consent for continuity of care.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 22, '2025-03-31 16:45:00', '2025-03-31 16:00:00', '2025-03-31 16:45:00', NOW()),

-- Reports for Project 1027 tasks
(5073, 1027, 2053, 4130, 23, 23, 'NLP Framework Selected', 'Evaluated and selected Hugging Face Transformers with BERT model. Achieves 90% accuracy in extracting key concepts from lecture materials. Processing speed 100 pages/minute.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 23, '2025-03-15 17:00:00', '2025-03-15 16:30:00', '2025-03-15 17:00:00', NOW()),
(5074, 1027, 2053, 4131, 25, 25, 'Question Generation Progress', 'Implemented question generation for multiple choice, true/false, and short answer. Generates appropriate distractors for MCQ. Needs further refinement for complex topics.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 25, '2025-04-05 16:30:00', '2025-04-05 16:00:00', '2025-04-05 16:30:00', NOW()),

-- Reports for Project 1028 tasks
(5075, 1028, 2055, 4135, 26, 26, 'Energy Audit Report', 'Completed audit of 20 buildings. Annual energy consumption 15M kWh. Identified 30% saving potential through HVAC optimization, lighting upgrades, and occupancy-based controls.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 26, '2025-03-20 17:30:00', '2025-03-20 17:00:00', '2025-03-20 17:30:00', NOW()),
(5076, 1028, 2055, 4136, 28, 28, 'Sensor Network Plan', 'Designed IoT sensor network with 500+ sensors for power, temperature, occupancy, and lighting. Estimated cost $75K with 2-year ROI from energy savings.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 28, '2025-04-05 16:45:00', '2025-04-05 16:00:00', '2025-04-05 16:45:00', NOW()),

-- Reports for Project 1029 tasks
(5077, 1029, 2057, 4140, 29, 29, 'Review Workflow Design', 'Designed 3-stage workflow: submission, peer review (3 reviewers), instructor feedback. Included calibration phase to train students on quality reviews.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 29, '2025-03-25 17:00:00', '2025-03-25 16:30:00', '2025-03-25 17:00:00', NOW()),
(5078, 1029, 2057, 4141, 31, 31, 'Rubric Framework', 'Created customizable rubric builder supporting weighted criteria, likert scales, and text feedback. Pre-built templates for common assignment types.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 31, '2025-04-10 16:30:00', '2025-04-10 16:00:00', '2025-04-10 16:30:00', NOW()),

-- Reports for Project 1030 tasks
(5079, 1030, 2059, 4145, 32, 32, 'Image Recognition Research', 'Researched SIFT, SURF, and deep learning approaches. Selected ResNet-based model for image similarity. Achieves 85% match accuracy in test dataset of 1000+ items.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 32, '2025-03-25 17:30:00', '2025-03-25 17:00:00', '2025-03-25 17:30:00', NOW()),
(5080, 1030, 2059, 4146, 34, 34, 'Item Database Schema', 'Designed database for lost/found items with fields: description, category, location, date, image embeddings, status. Includes full-text search and image similarity search.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 34, '2025-04-10 16:45:00', '2025-04-10 16:00:00', '2025-04-10 16:45:00', NOW()),

-- Reports for Project 1031 tasks
(5081, 1031, 2061, 4150, 35, 35, 'Route Analysis Complete', 'Analyzed 6 shuttle routes with hourly ridership data. Peak usage 8-9 AM and 4-5 PM. Identified route optimization opportunities to reduce wait times by 20%.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 35, '2025-03-28 17:00:00', '2025-03-28 16:30:00', '2025-03-28 17:00:00', NOW()),
(5082, 1031, 2061, 4151, 37, 37, 'GPS Tracking Installed', 'Installed GPS trackers on all 12 campus shuttle buses. Real-time location updates every 10 seconds. Battery life 7 days with 4G connectivity.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 37, '2025-04-15 16:30:00', '2025-04-15 16:00:00', '2025-04-15 16:30:00', NOW()),

-- Reports for Project 1032 tasks
(5083, 1032, 2063, 4155, 38, 38, 'Student Profile System', 'Designed comprehensive profiles including courses, learning preferences, availability, goals, and personality traits. Privacy controls allow students to choose what to share.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 38, '2025-03-30 17:30:00', '2025-03-30 17:00:00', '2025-03-30 17:30:00', NOW()),
(5084, 1032, 2063, 4156, 40, 40, 'Matching Algorithm v1', 'Implemented compatibility scoring algorithm considering course overlap (40%), schedule alignment (30%), learning style match (20%), and shared goals (10%).', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 40, '2025-04-18 16:45:00', '2025-04-18 16:00:00', '2025-04-18 16:45:00', NOW()),

-- Reports for Project 1033 tasks
(5085, 1033, 2065, 4160, 41, 41, 'Equipment Catalog', 'Created catalog of 150+ lab equipment items with specifications, capacity, required training, and booking policies. Includes photos and usage instructions for each item.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 41, '2025-04-05 17:00:00', '2025-04-05 16:30:00', '2025-04-05 17:00:00', NOW()),
(5086, 1033, 2065, 4161, 43, 43, 'Booking Rules Engine', 'Defined booking rules: max duration, advance booking window, cancellation policies, and priority for different user groups. Conflict resolution algorithms implemented.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 43, '2025-04-20 16:30:00', '2025-04-20 16:00:00', '2025-04-20 16:30:00', NOW()),

-- Reports for Project 1034 tasks
(5087, 1034, 2067, 4165, 44, 44, 'Marketplace Rules', 'Defined marketplace policies: student verification required, prohibited items list, pricing guidelines, transaction dispute resolution, and seller reputation system.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 44, '2025-04-05 17:30:00', '2025-04-05 17:00:00', '2025-04-05 17:30:00', NOW()),
(5088, 1034, 2067, 4166, 46, 46, 'Payment Integration', 'Integrated Stripe payment gateway with 2.9% + $0.30 transaction fee. Implemented secure payment processing, escrow system for high-value items, and refund handling.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 46, '2025-04-25 16:45:00', '2025-04-25 16:00:00', '2025-04-25 16:45:00', NOW()),

-- Reports for Project 1035 tasks
(5089, 1035, 2069, 4170, 47, 47, '3D Lab Design', 'Designed virtual lab environment using Unity3D. Modeled 20 common lab equipment pieces with realistic physics. Supports VR headsets and desktop mode.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 47, '2025-04-10 17:00:00', '2025-04-10 16:30:00', '2025-04-10 17:00:00', NOW()),
(5090, 1035, 2069, 4171, 11, 11, 'Physics Engine Setup', 'Configured PhysX engine for realistic liquid pouring, heating, mixing, and chemical reactions. Calibrated physics parameters based on real lab experiments.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 11, '2025-04-30 16:30:00', '2025-04-30 16:00:00', '2025-04-30 16:30:00', NOW()),

-- Reports for Project 1036 tasks
(5091, 1036, 2071, 4175, 12, 12, 'Plagiarism Database', 'Setup database with access to 50M+ academic papers, student submissions archive, and web scraping capabilities. Indexed for fast similarity search.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 12, '2025-04-10 17:30:00', '2025-04-10 17:00:00', '2025-04-10 17:30:00', NOW()),
(5092, 1036, 2071, 4176, 15, 15, 'Text Analysis Algorithms', 'Implemented advanced text comparison using TF-IDF, LSA, and transformer-based semantic similarity. Detects paraphrasing with 92% accuracy.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 15, '2025-05-01 16:45:00', '2025-05-01 16:00:00', '2025-05-01 16:45:00', NOW()),

-- Reports for Project 1037 tasks
(5093, 1037, 2073, 4180, 17, 17, 'Emergency Protocols', 'Defined protocols for 8 emergency types: active threat, fire, medical, weather, chemical spill, power outage, earthquake, suspicious activity. Alert templates created.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 17, '2025-04-12 17:00:00', '2025-04-12 16:30:00', '2025-04-12 17:00:00', NOW()),
(5094, 1037, 2073, 4181, 22, 22, 'Geofencing Setup', 'Configured geofencing for 50 campus zones including buildings, parking lots, and outdoor spaces. Alert targeting based on user location with 20-meter accuracy.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 22, '2025-05-01 16:30:00', '2025-05-01 16:00:00', '2025-05-01 16:30:00', NOW()),

-- Reports for Project 1038 tasks
(5095, 1038, 2075, 4185, 23, 23, 'Career Data Collection', 'Collected career path data for 100+ professions including required skills, typical progression, salary ranges, and job market trends. Sourced from BLS, LinkedIn, and alumni surveys.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 23, '2025-04-15 17:30:00', '2025-04-15 17:00:00', '2025-04-15 17:30:00', NOW()),
(5096, 1038, 2075, 4186, 25, 25, 'Personality Assessment', 'Created career personality test based on Holland Code (RIASEC) and Big Five traits. 50 questions taking 10 minutes. Validated against professional career assessment tools.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 25, '2025-05-05 16:45:00', '2025-05-05 16:00:00', '2025-05-05 16:45:00', NOW()),

-- Reports for Project 1039 tasks
(5097, 1039, 2077, 4190, 26, 26, 'Network Audit Report', 'Audited 500+ Wi-Fi access points across campus. Average coverage 85% with weak spots in 5 buildings. Peak usage 10,000 concurrent devices during class hours.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 26, '2025-04-18 17:00:00', '2025-04-18 16:30:00', '2025-04-18 17:00:00', NOW()),
(5098, 1039, 2077, 4191, 28, 28, 'Monitoring Tool Selection', 'Evaluated SolarWinds, PRTG, and Nagios. Selected PRTG for best balance of features and cost. Supports SNMP, NetFlow, and custom sensors for comprehensive monitoring.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 28, '2025-05-08 16:30:00', '2025-05-08 16:00:00', '2025-05-08 16:30:00', NOW()),

-- Reports for Project 1040 tasks
(5099, 1040, 2079, 4195, 29, 29, 'Student Finance Survey', 'Surveyed 500 students on spending patterns. Average monthly spend $1200: rent (45%), food (25%), transportation (10%), entertainment (10%), other (10%). 60% struggle with budgeting.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 29, '2025-04-20 17:30:00', '2025-04-20 17:00:00', '2025-04-20 17:30:00', NOW()),
(5100, 1040, 2079, 4196, 32, 32, 'Budget Templates', 'Created 5 budget templates: tight budget, moderate spending, high income, international student, commuter student. Each template includes recommended allocation percentages.', 'SUBMITTED', NULL, NULL, FALSE, NULL, NULL, 32, '2025-05-10 16:45:00', '2025-05-10 16:00:00', '2025-05-10 16:45:00', NOW());

-- ==========================================
-- COMMENTS ON REPORTS
-- ==========================================
-- Comments on Report 5001 (Task 4001 report by student 11)
INSERT INTO comments (id, project_id, milestone_id, task_id, report_id, author_id, content, parent_comment_id, start_date, end_date, is_locked, locked_by_id, locked_at, created_by_id, created_at, updated_at) VALUES
(6001, 1001, 2001, 4001, 5001, 3, 'Excellent work on the stakeholder interviews! The insights about elderly patients are particularly valuable. Make sure to document specific UI requirements based on their feedback.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-01-14 09:00:00', NOW()),
(6002, 1001, 2001, 4001, 5001, 11, 'Thank you! I will create a separate document for elderly-specific UI requirements by next week.', 6001, NULL, NULL, FALSE, NULL, NULL, 11, '2025-01-14 10:30:00', NOW()),

-- Comments on Report 5002 (Task 4001 report by student 12)
(6003, 1001, 2001, 4001, 5002, 3, 'Good progress on requirements documentation. I notice you have 28 functional requirements - let''s prioritize them into must-have vs nice-to-have for our MVP scope.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-01-18 10:15:00', NOW()),
(6004, 1001, 2001, 4001, 5002, 12, 'I''ll work with the team to create a prioritization matrix using MoSCoW method.', 6003, NULL, NULL, FALSE, NULL, NULL, 12, '2025-01-18 11:00:00', NOW()),
(6005, 1001, 2001, 4001, 5002, 13, 'I can help with the prioritization. Let''s schedule a team meeting this Friday.', 6004, NULL, NULL, FALSE, NULL, NULL, 13, '2025-01-18 14:30:00', NOW()),

-- Comments on Report 5003 (Final report for Task 4001)
(6006, 1001, 2001, 4001, 5003, 3, 'Perfect! This is a comprehensive requirements document. Sign-off approved. You can proceed to the technology selection phase.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-01-21 09:00:00', NOW()),

-- Comments on Report 5004 (Task 4002 - Technology Research)
(6007, 1001, 2001, 4002, 5004, 3, 'WebRTC is a solid choice. However, consider implementing a fallback mechanism using TURN servers for users behind restrictive firewalls. Also research Jitsi as an open-source option.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-01-29 09:30:00', NOW()),
(6008, 1001, 2001, 4002, 5004, 11, 'Great suggestion! I''ll research TURN server setup and Jitsi integration options.', 6007, NULL, NULL, FALSE, NULL, NULL, 11, '2025-01-29 14:00:00', NOW()),

-- Comments on Report 5005 (Task 4002 - Security)
(6009, 1001, 2001, 4002, 5005, 3, 'Thorough security analysis! Make sure to also consider data encryption at rest, not just in transit. We need to comply with HIPAA''s physical safeguards requirements.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-02-04 10:00:00', NOW()),

-- Comments on Report 5006 (Task 4003 - Architecture)
(6010, 1001, 2001, 4003, 5006, 3, 'Good microservices breakdown. I recommend adding a Message Queue (RabbitMQ or Kafka) for async communication between services. This will improve scalability and reliability.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-02-16 09:45:00', NOW()),
(6011, 1001, 2001, 4003, 5006, 13, 'Thank you! I''ll include message queue in the architecture diagram and research RabbitMQ vs Kafka trade-offs.', 6010, NULL, NULL, FALSE, NULL, NULL, 13, '2025-02-16 15:00:00', NOW()),
(6012, 1001, 2001, 4003, 5006, 12, 'Consider using Redis for caching frequently accessed patient records. This can significantly improve response times.', NULL, NULL, NULL, FALSE, NULL, NULL, 12, '2025-02-17 10:00:00', NOW()),

-- Comments on Report 5007 (Task 4004 - Backend API)
(6013, 1001, 2002, 4004, 5007, 3, 'Excellent progress on authentication! Make sure to implement refresh token rotation for enhanced security. Also add rate limiting to prevent brute force attacks.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-03-06 09:00:00', NOW()),
(6014, 1001, 2002, 4004, 5007, 11, 'I''ll implement refresh token rotation and use express-rate-limit middleware for API rate limiting.', 6013, NULL, NULL, FALSE, NULL, NULL, 11, '2025-03-06 14:30:00', NOW()),

-- Comments on Report 5008 (Task 4004 - Video Session API)
(6015, 1001, 2002, 4004, 5008, 3, 'NAT traversal is a common challenge. I recommend using Coturn as your TURN server. Let me know if you need help with the configuration.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-03-16 10:00:00', NOW()),
(6016, 1001, 2002, 4004, 5008, 13, 'Thanks! I''ll try Coturn. The documentation looks comprehensive.', 6015, NULL, NULL, FALSE, NULL, NULL, 13, '2025-03-16 15:45:00', NOW()),
(6017, 1001, 2002, 4004, 5008, 11, 'I had similar issues in my authentication work. Happy to pair program on this if needed.', NULL, NULL, NULL, FALSE, NULL, NULL, 11, '2025-03-17 09:00:00', NOW()),

-- Comments on Report 5009 (Task 4005 - Frontend)
(6018, 1001, 2002, 4005, 5009, 3, 'Great work on the UI components! Make sure to implement proper error boundaries in React to handle video failures gracefully. Also test on various screen sizes.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-03-21 09:30:00', NOW()),
(6019, 1001, 2002, 4005, 5009, 12, 'I''ll add error boundaries and create a responsive testing matrix for different devices.', 6018, NULL, NULL, FALSE, NULL, NULL, 12, '2025-03-21 14:00:00', NOW()),

-- Comments on Report 5010 (Project 1002 - AR Framework)
(6020, 1002, 2003, 4007, 5010, 4, 'A-Frame is a good choice for web-based AR. Make sure to test performance on mid-range smartphones, as AR can be resource-intensive.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2025-01-26 10:00:00', NOW()),
(6021, 1002, 2003, 4007, 5010, 15, 'I''ll conduct performance tests on Samsung Galaxy A52 and iPhone 12 as our baseline devices.', 6020, NULL, NULL, FALSE, NULL, NULL, 15, '2025-01-26 15:30:00', NOW()),

-- Comments on Report 5011 (Project 1002 - Beacon Placement)
(6022, 1002, 2003, 4008, 5011, 4, 'Excellent beacon placement strategy! 15-meter spacing should work well. Consider adding a few extra beacons in areas with metal structures that might cause signal interference.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2025-02-11 09:00:00', NOW()),
(6023, 1002, 2003, 4008, 5011, 18, 'Good point! I''ll mark potential interference zones on the floor plans and add 3 additional beacons.', 6022, NULL, NULL, FALSE, NULL, NULL, 18, '2025-02-11 14:00:00', NOW()),

-- Comments on Report 5012 (Project 1002 - Accessibility)
(6024, 1002, 2003, 4009, 5012, 4, 'Great accessibility research! Also consider haptic feedback for turn-by-turn navigation. This can help visually impaired users.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2025-02-26 10:30:00', NOW()),
(6025, 1002, 2003, 4009, 5012, 17, 'Excellent idea! I''ll research haptic feedback patterns and add them to the requirements.', 6024, NULL, NULL, FALSE, NULL, NULL, 17, '2025-02-26 16:00:00', NOW()),
(6026, 1002, 2003, 4009, 5012, 15, 'We should also test with actual visually impaired students to get real feedback on the accessibility features.', NULL, NULL, NULL, FALSE, NULL, NULL, 15, '2025-02-27 09:00:00', NOW()),

-- Comments on completed project reports (Project 1006)
(6027, 1006, 2011, 4012, 5013, 8, 'Comprehensive sustainability metrics research! This will serve as an excellent foundation for our app. Well done!', NULL, NULL, NULL, FALSE, NULL, NULL, 8, '2024-09-21 09:00:00', NOW()),
(6028, 1006, 2011, 4013, 5014, 8, 'Database schema looks solid. Make sure to add proper indexes on frequently queried columns for performance.', NULL, NULL, NULL, FALSE, NULL, NULL, 8, '2024-10-09 10:00:00', NOW()),
(6029, 1006, 2012, 4014, 5015, 8, 'Survey module working perfectly! The QR code integration is smooth. Great work!', NULL, NULL, NULL, FALSE, NULL, NULL, 8, '2024-11-16 09:30:00', NOW()),
(6030, 1006, 2012, 4015, 5016, 8, 'The analytics dashboard looks professional and the WebSocket updates are lightning fast. Excellent implementation!', NULL, NULL, NULL, FALSE, NULL, NULL, 8, '2024-12-29 09:00:00', NOW()),
(6031, 1006, 2012, 4016, 5017, 8, 'Final app is impressive! The performance improvements are noticeable. Ready for production. Outstanding work team!', NULL, NULL, NULL, FALSE, NULL, NULL, 8, '2025-01-29 09:00:00', NOW()),

-- Comments on Project 1007 reports
(6032, 1007, 2013, 4017, 5018, 9, 'Sub-millimeter accuracy is outstanding! This will provide excellent data quality for analysis.', NULL, NULL, NULL, FALSE, NULL, NULL, 9, '2024-09-29 10:00:00', NOW()),
(6033, 1007, 2013, 4018, 5019, 9, 'Good progress on the biomechanics database. Consider adding more diverse body types to ensure the system works well for all users.', NULL, NULL, NULL, FALSE, NULL, NULL, 9, '2024-10-24 09:30:00', NOW()),
(6034, 1007, 2014, 4019, 5020, 9, '87% accuracy is a good start. Let''s aim for 95%+ before deployment. Try refining the joint angle comparison algorithms.', NULL, NULL, NULL, FALSE, NULL, NULL, 9, '2024-11-21 10:00:00', NOW()),
(6035, 1007, 2014, 4019, 5020, 34, 'I''ll experiment with different weighting schemes for joint angles and add smoothing filters to reduce noise.', 6034, NULL, NULL, FALSE, NULL, NULL, 34, '2024-11-21 15:30:00', NOW()),

-- Comments on Project 1008 reports
(6036, 1008, 2015, 4021, 5021, 10, 'Excellent competency framework! The hierarchical structure with skill levels will make it easy for students to track their progress.', NULL, NULL, NULL, FALSE, NULL, NULL, 10, '2024-10-06 09:00:00', NOW()),
(6037, 1008, 2015, 4022, 5022, 10, 'Upload system is looking good! The video preview feature will be very helpful. Make sure to implement file size limits to prevent storage issues.', NULL, NULL, NULL, FALSE, NULL, NULL, 10, '2024-10-26 09:30:00', NOW()),
(6038, 1008, 2016, 4023, 5023, 10, '92% satisfaction rate is impressive! The AI summaries are saving instructors a lot of time. Great implementation!', NULL, NULL, NULL, FALSE, NULL, NULL, 10, '2024-12-11 09:00:00', NOW()),
(6039, 1008, 2016, 4023, 5023, 37, 'Thank you! I''m working on fine-tuning the prompts to handle different types of evidence (videos, documents, projects) more effectively.', 6038, NULL, NULL, FALSE, NULL, NULL, 37, '2024-12-11 14:00:00', NOW());


-- ==========================================
-- COMMENTS ON TASKS (without reports)
-- ==========================================
-- General task-level comments (not tied to specific reports)
INSERT INTO comments (id, project_id, milestone_id, task_id, report_id, author_id, content, parent_comment_id, start_date, end_date, is_locked, locked_by_id, locked_at, created_by_id, created_at, updated_at) VALUES
(6040, 1001, 2002, 4006, NULL, 3, 'Security implementation is critical. Please prioritize this task and ensure thorough testing. Schedule a security review meeting before moving to production.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-03-26 09:00:00', NOW()),
(6041, 1001, 2002, 4006, NULL, 12, 'Understood. I''ll schedule the security review for April 20th and prepare a comprehensive test plan.', 6040, NULL, NULL, FALSE, NULL, NULL, 12, '2025-03-26 14:30:00', NOW()),
(6042, 1002, 2004, 4010, NULL, 4, 'The AR navigation module is the core of our project. Make sure to handle edge cases like GPS signal loss and beacon failures gracefully.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2025-03-03 10:00:00', NOW()),
(6043, 1002, 2004, 4010, NULL, 15, 'I''ll implement offline mode and beacon redundancy to handle these edge cases.', 6042, NULL, NULL, FALSE, NULL, NULL, 15, '2025-03-03 15:00:00', NOW()),
(6044, 1002, 2004, 4011, NULL, 4, 'Cross-platform development can be tricky. Consider using React Native with AR libraries to share code between iOS and Android.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2025-03-16 09:30:00', NOW()),
(6045, 1007, 2014, 4020, NULL, 9, 'Heatmap visualization will be the most visible feature to users. Spend extra time on making it intuitive and visually appealing.', NULL, NULL, NULL, FALSE, NULL, NULL, 9, '2024-12-17 10:00:00', NOW()),
(6046, 1008, 2016, 4024, NULL, 10, 'The accreditation export feature needs to match institutional templates exactly. Coordinate with the registrar''s office for the correct format.', NULL, NULL, NULL, FALSE, NULL, NULL, 10, '2024-12-17 09:00:00', NOW()),
(6047, 1008, 2016, 4024, NULL, 38, 'I''ve already contacted the registrar''s office and received their PDF template guidelines. Working on implementation.', 6046, NULL, NULL, FALSE, NULL, NULL, 38, '2024-12-17 14:00:00', NOW()),

-- ==========================================
-- ADDITIONAL COMMENTS FOR NEW REPORTS
-- ==========================================
-- Comments on Project 1003 reports
(6048, 1003, 2005, 4025, 5024, 5, 'Excellent survey work! The peak usage data will be crucial for scheduling optimization. Make sure to also track equipment usage patterns.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2025-02-06 09:00:00', NOW()),
(6049, 1003, 2005, 4025, 5024, 22, 'Will do! I''ll add equipment tracking to the survey and create usage heat maps.', 6048, NULL, NULL, FALSE, NULL, NULL, 22, '2025-02-06 14:00:00', NOW()),
(6050, 1003, 2005, 4026, 5025, 5, 'Great algorithm design! 85% optimal allocation is a solid start. Consider adding a priority system for senior projects vs regular coursework.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2025-02-26 09:30:00', NOW()),
(6051, 1003, 2005, 4027, 5026, 5, 'The mockups look professional! The filter options are comprehensive. User testing should help us refine the interface further.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2025-03-07 10:00:00', NOW()),

-- Comments on Project 1004 reports
(6052, 1004, 2007, 4030, 5027, 6, 'MediaPipe is an excellent choice! 30 FPS on mid-range phones is impressive. Make sure to optimize for battery consumption as well.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2025-02-11 09:00:00', NOW()),
(6053, 1004, 2007, 4030, 5027, 25, 'Good point! I''ll implement frame skipping and adaptive resolution to optimize battery life.', 6052, NULL, NULL, FALSE, NULL, NULL, 25, '2025-02-11 15:00:00', NOW()),
(6054, 1004, 2007, 4031, 5028, 6, '50 exercises is a great starting point. Consider adding progression levels for beginners to advanced users.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2025-03-02 09:30:00', NOW()),
(6055, 1004, 2007, 4032, 5029, 6, 'Comprehensive scoring rubric! The weighted system will provide nuanced feedback. Test it with actual users to validate the weights.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2025-03-12 10:00:00', NOW()),

-- Comments on Project 1005 reports
(6056, 1005, 2009, 4035, 5030, 7, 'Excellent work digitizing all protocols! The multimedia support will make them much more accessible to students. Well done!', NULL, NULL, NULL, FALSE, NULL, NULL, 7, '2025-02-21 09:00:00', NOW()),
(6057, 1005, 2009, 4036, 5031, 7, 'Vuforia is a solid choice for industrial applications. Make sure to test marker tracking under various lighting conditions in the lab.', NULL, NULL, NULL, FALSE, NULL, NULL, 7, '2025-03-11 09:30:00', NOW()),
(6058, 1005, 2009, 4036, 5031, 30, 'I''ll conduct lighting tests in different lab conditions and document the optimal marker placement guidelines.', 6057, NULL, NULL, FALSE, NULL, NULL, 30, '2025-03-11 14:00:00', NOW()),

-- Comments on Project 1009 reports
(6059, 1009, 2017, 4040, 5032, 3, 'Great job setting up the ROS environment! Having everyone on the same setup will prevent compatibility issues down the road.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2024-10-06 09:00:00', NOW()),
(6060, 1009, 2017, 4041, 5033, 3, 'Safety is paramount when working with robots. The redundant safety systems are essential. Good thinking!', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2024-10-26 09:30:00', NOW()),
(6061, 1009, 2017, 4041, 5033, 42, 'Thank you! I''m also working on clear visual indicators for the safety zones to help students understand the boundaries.', 6060, NULL, NULL, FALSE, NULL, NULL, 42, '2024-10-26 14:00:00', NOW()),

-- Comments on Project 1010 reports
(6062, 1010, 2019, 4045, 5034, 4, 'PostgreSQL with full text search is a great foundation. The Zotero integration will make it easy for researchers to import existing libraries.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2024-10-21 09:00:00', NOW()),
(6063, 1010, 2019, 4046, 5035, 4, 'GPT-4 is the right choice for academic writing. The rate limiting will help manage costs. Consider implementing caching for common queries.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2024-11-06 09:30:00', NOW()),
(6064, 1010, 2019, 4046, 5035, 45, 'Excellent suggestion! I''ll implement a caching layer for frequently requested writing assistance patterns.', 6062, NULL, NULL, FALSE, NULL, NULL, 45, '2024-11-06 14:00:00', NOW()),

-- Comments on Project 1011 reports (COMPLETED)
(6065, 1011, 2021, 4050, 5036, 5, '50 badges across different categories is impressive! The progressive difficulty will keep users engaged long-term.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2024-10-26 09:00:00', NOW()),
(6066, 1011, 2021, 4051, 5037, 5, 'Multi-device integration is complex but essential. Real-time verification will prevent cheating and ensure data accuracy.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2024-11-11 09:30:00', NOW()),
(6067, 1011, 2022, 4053, 5039, 5, '100 successful beta testers is a great validation! The 70% daily active user rate shows strong engagement. Excellent work!', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2025-01-21 09:00:00', NOW()),
(6068, 1011, 2022, 4054, 5040, 5, 'Outstanding! 70% daily active users is exceptional for a fitness app. The social features are clearly driving engagement.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2025-03-23 10:00:00', NOW()),

-- Comments on Project 1012 reports
(6069, 1012, 2023, 4055, 5041, 6, 'Comprehensive audit! 127 barriers across 15 buildings gives us a clear roadmap. The prioritized plan will help allocate resources effectively.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2024-10-31 09:00:00', NOW()),
(6070, 1012, 2023, 4056, 5042, 6, '200+ IoT devices is ambitious but will provide excellent data coverage. Make sure to plan for maintenance and battery replacement.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2024-11-16 09:30:00', NOW()),
(6071, 1012, 2023, 4057, 5043, 6, 'The dashboard mockups are very intuitive. The heatmaps will make it easy to identify problem areas at a glance. Great work!', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2024-11-25 10:00:00', NOW()),

-- Comments on Project 1013 reports
(6072, 1013, 2025, 4060, 5044, 7, 'Excellent privacy focus! Storing only anonymized signatures is the right approach. This will be crucial for student acceptance.', NULL, NULL, NULL, FALSE, NULL, NULL, 7, '2024-11-06 09:00:00', NOW()),
(6073, 1013, 2025, 4061, 5045, 7, '98% capture success rate is excellent! The optimal lighting setup is paying off. Document the lighting requirements for future installations.', NULL, NULL, NULL, FALSE, NULL, NULL, 7, '2024-11-21 09:30:00', NOW()),

-- Comments on Project 1014 reports
(6074, 1014, 2027, 4065, 5046, 8, 'Drawing from dating apps and professional networking is smart! These systems have been refined through years of user data.', NULL, NULL, NULL, FALSE, NULL, NULL, 8, '2024-11-11 09:00:00', NOW()),
(6075, 1014, 2027, 4066, 5047, 8, '12-week cycles with weekly check-ins strike a good balance. The structured approach will help ensure program success.', NULL, NULL, NULL, FALSE, NULL, NULL, 8, '2024-11-26 09:30:00', NOW()),

-- Comments on Project 1015 reports
(6076, 1015, 2029, 4070, 5048, 9, 'Supporting multiple device types is essential for broad adoption. The unified pipeline will make it easy to add new devices later.', NULL, NULL, NULL, FALSE, NULL, NULL, 9, '2024-11-21 09:00:00', NOW()),
(6077, 1015, 2029, 4071, 5049, 9, 'Using validated scales like PHQ-9 and GAD-7 is important for credibility. Good collaboration with the counseling team!', NULL, NULL, NULL, FALSE, NULL, NULL, 9, '2024-12-11 09:30:00', NOW()),
(6078, 1015, 2029, 4072, 5050, 9, 'GDPR/HIPAA compliance is non-negotiable for health data. The automatic deletion after graduation is a smart privacy feature.', NULL, NULL, NULL, FALSE, NULL, NULL, 9, '2024-12-17 10:00:00', NOW()),

-- Comments on Project 1016 reports (LOCKED)
(6079, 1016, 2031, 4075, 5051, 10, '82% recognition rate for whiteboard sketches is impressive! This will make the collaborative experience much more fluid.', NULL, NULL, NULL, FALSE, NULL, NULL, 10, '2024-11-26 09:00:00', NOW()),
(6080, 1016, 2031, 4076, 5052, 10, '50+ concurrent users with smooth collaboration is excellent performance! The conflict resolution is working well.', NULL, NULL, NULL, FALSE, NULL, NULL, 10, '2024-12-16 09:30:00', NOW()),

-- Comments on Project 1017 reports
(6081, 1017, 2033, 4080, 5053, 3, '5 discipline-specific templates show good attention to different needs. Mobile-responsive and ADA compliant is essential!', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2024-12-02 09:00:00', NOW()),
(6082, 1017, 2033, 4081, 5054, 3, 'Version control in the upload system is smart! Students can track their portfolio evolution. 100MB limit should handle most files.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2024-12-21 09:30:00', NOW()),

-- Comments on Project 1018 reports
(6083, 1018, 2035, 4085, 5055, 4, '99.8% scan success rate is excellent! QR codes are the right choice for this application. Easy to print and durable.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2024-12-06 09:00:00', NOW()),
(6084, 1018, 2035, 4086, 5056, 4, 'Comprehensive schema covering all asset types. The checkout history will be valuable for usage analytics.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2024-12-21 09:30:00', NOW()),

-- Comments on Project 1019 reports
(6085, 1019, 2037, 4090, 5057, 5, 'Identifying the right metrics for music feedback is crucial. Tempo, pitch, dynamics, and synchronization cover the essentials well.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2024-12-11 09:00:00', NOW()),
(6086, 1019, 2037, 4091, 5058, 5, 'Detailed rubrics will ensure consistent feedback. Faculty review ensures the rubrics align with teaching standards.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2024-12-29 09:30:00', NOW()),

-- Comments on Project 1020 reports
(6087, 1020, 2039, 4095, 5059, 6, '200+ employers with 500+ positions is a substantial database! Past placement data will help students make informed decisions.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2024-12-21 09:00:00', NOW()),
(6088, 1020, 2039, 4096, 5060, 6, '85% accuracy vs manual review is solid for automated scoring. This will save advisors significant time in initial screening.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2025-01-06 09:30:00', NOW()),

-- Comments on Project 1021 reports
(6089, 1021, 2041, 4100, 5061, 3, 'Interviewing 15 librarians and 50 students provides excellent user perspective. The prioritized features will guide development.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-03-01 09:00:00', NOW()),
(6090, 1021, 2041, 4101, 5062, 3, 'Zebra FX9600 is a solid enterprise-grade choice. The investment in quality hardware will pay off in reliability.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-03-21 09:30:00', NOW()),

-- Comments on Project 1022 reports
(6091, 1022, 2043, 4105, 5063, 4, '85% peak occupancy means we definitely need this system! The survey data will be valuable for planning sensor deployment.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2025-03-06 09:00:00', NOW()),
(6092, 1022, 2043, 4106, 5064, 4, 'Magnetometer sensors are the right choice for durability and battery life. $50 per space is very cost-effective!', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2025-03-26 09:30:00', NOW()),

-- Comments on Project 1023 reports
(6093, 1023, 2045, 4110, 5065, 5, 'Agora is a great balance of features and complexity. The $99/month is manageable for our budget. Good analysis!', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2025-03-06 09:00:00', NOW()),
(6094, 1023, 2045, 4111, 5066, 5, '50 concurrent breakout rooms is impressive! Auto-assignment and instructor monitoring are must-have features.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2025-03-26 09:30:00', NOW()),

-- Comments on Project 1024 reports
(6095, 1024, 2047, 4115, 5067, 6, 'Tag-based search and trending algorithm will make event discovery much better. Registration velocity is a clever metric!', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2025-03-11 09:00:00', NOW()),
(6096, 1024, 2047, 4116, 5068, 6, 'Excellent work on calendar integration! Automatic sync and reminders will significantly improve event attendance.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2025-03-31 09:30:00', NOW()),

-- Comments on Project 1025 reports
(6097, 1025, 2049, 4120, 5069, 7, '150kg daily waste is significant! The breakdown by source (overproduction, plate waste, expired) gives clear action items.', NULL, NULL, NULL, FALSE, NULL, NULL, 7, '2025-03-11 09:00:00', NOW()),
(6098, 1025, 2049, 4121, 5070, 7, '2% accuracy is excellent for weight sensors! Real-time data will enable immediate interventions to reduce waste.', NULL, NULL, NULL, FALSE, NULL, NULL, 7, '2025-03-31 09:30:00', NOW()),

-- Comments on Project 1026 reports
(6099, 1026, 2051, 4125, 5071, 8, 'Comprehensive resource library! Categorization by concern type will help students find relevant resources quickly.', NULL, NULL, NULL, FALSE, NULL, NULL, 8, '2025-03-16 09:00:00', NOW()),
(6100, 1026, 2051, 4126, 5072, 8, 'End-to-end encryption and counselor verification are essential for trust. Optional transcript storage respects student autonomy.', NULL, NULL, NULL, FALSE, NULL, NULL, 8, '2025-04-01 09:30:00', NOW()),

-- Comments on Project 1027 reports
(6101, 1027, 2053, 4130, 5073, 9, 'BERT model with 90% accuracy and 100 pages/minute is impressive! This will scale well to large lecture materials.', NULL, NULL, NULL, FALSE, NULL, NULL, 9, '2025-03-16 09:00:00', NOW()),
(6102, 1027, 2053, 4131, 5074, 9, 'Question generation for multiple formats is great! The distractor generation for MCQ is particularly challenging - nice work!', NULL, NULL, NULL, FALSE, NULL, NULL, 9, '2025-04-06 09:30:00', NOW()),

-- Comments on Project 1028 reports
(6103, 1028, 2055, 4135, 5075, 10, '30% potential savings is substantial! HVAC optimization and occupancy-based controls are proven strategies.', NULL, NULL, NULL, FALSE, NULL, NULL, 10, '2025-03-21 09:00:00', NOW()),
(6104, 1028, 2055, 4136, 5076, 10, '$75K investment with 2-year ROI is very attractive! The energy savings will pay for the system quickly.', NULL, NULL, NULL, FALSE, NULL, NULL, 10, '2025-04-06 09:30:00', NOW()),

-- Comments on Project 1029 reports
(6105, 1029, 2057, 4140, 5077, 3, '3-stage workflow with calibration phase is smart! Training students to give quality reviews is essential.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-03-26 09:00:00', NOW()),
(6106, 1029, 2057, 4141, 5078, 3, 'Customizable rubric builder will make this system flexible for different courses. Pre-built templates are a nice time-saver.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-04-11 09:30:00', NOW()),

-- Comments on Project 1030 reports
(6107, 1030, 2059, 4145, 5079, 4, '85% match accuracy for 1000+ items is solid! ResNet-based approach should generalize well to different item types.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2025-03-26 09:00:00', NOW()),
(6108, 1030, 2059, 4146, 5080, 4, 'Image embeddings with similarity search is the right approach! Full-text search combined with visual search is powerful.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2025-04-11 09:30:00', NOW()),

-- Comments on Project 1031 reports
(6109, 1031, 2061, 4150, 5081, 5, '20% wait time reduction would significantly improve the student experience! Good route analysis work.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2025-03-29 09:00:00', NOW()),
(6110, 1031, 2061, 4151, 5082, 5, '10-second GPS updates with 7-day battery life is excellent! 4G connectivity ensures reliable tracking.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2025-04-16 09:30:00', NOW()),

-- Comments on Project 1032 reports
(6111, 1032, 2063, 4155, 5083, 6, 'Privacy controls are important for student comfort. Letting them choose what to share will increase participation.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2025-03-31 09:00:00', NOW()),
(6112, 1032, 2063, 4156, 5084, 6, 'The weighted scoring algorithm makes sense! Course overlap should definitely be the highest weight for study groups.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2025-04-19 09:30:00', NOW()),

-- Comments on Project 1033 reports
(6113, 1033, 2065, 4160, 5085, 7, '150+ equipment catalog with photos and instructions will be very helpful for students. Comprehensive documentation!', NULL, NULL, NULL, FALSE, NULL, NULL, 7, '2025-04-06 09:00:00', NOW()),
(6114, 1033, 2065, 4161, 5086, 7, 'Clear booking rules and conflict resolution will prevent scheduling chaos. Priority system for different user groups is fair.', NULL, NULL, NULL, FALSE, NULL, NULL, 7, '2025-04-21 09:30:00', NOW()),

-- Comments on Project 1034 reports
(6115, 1034, 2067, 4165, 5087, 8, 'Well-defined marketplace policies will create trust and safety. Dispute resolution process is particularly important.', NULL, NULL, NULL, FALSE, NULL, NULL, 8, '2025-04-06 09:00:00', NOW()),
(6116, 1034, 2067, 4166, 5088, 8, 'Stripe integration with escrow for high-value items is smart! This will protect both buyers and sellers.', NULL, NULL, NULL, FALSE, NULL, NULL, 8, '2025-04-26 09:30:00', NOW()),

-- Comments on Project 1035 reports
(6117, 1035, 2069, 4170, 5089, 9, 'Unity3D with VR and desktop support gives flexibility! 20 equipment pieces is a good starting set for a virtual lab.', NULL, NULL, NULL, FALSE, NULL, NULL, 9, '2025-04-11 09:00:00', NOW()),
(6118, 1035, 2069, 4171, 5090, 9, 'PhysX calibrated with real experiments will make simulations realistic! Students will get authentic lab experience.', NULL, NULL, NULL, FALSE, NULL, NULL, 9, '2025-05-01 09:30:00', NOW()),

-- Comments on Project 1036 reports
(6119, 1036, 2071, 4175, 5091, 10, '50M+ papers with web scraping is a comprehensive database! Fast similarity search will enable quick checks.', NULL, NULL, NULL, FALSE, NULL, NULL, 10, '2025-04-11 09:00:00', NOW()),
(6120, 1036, 2071, 4176, 5092, 10, '92% accuracy in detecting paraphrasing is excellent! Transformer-based semantic similarity is state-of-the-art.', NULL, NULL, NULL, FALSE, NULL, NULL, 10, '2025-05-02 09:30:00', NOW()),

-- Comments on Project 1037 reports
(6121, 1037, 2073, 4180, 5093, 3, '8 emergency types with specific protocols is thorough! Alert templates will enable fast response in crises.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-04-13 09:00:00', NOW()),
(6122, 1037, 2073, 4181, 5094, 3, '50 geofenced zones with 20-meter accuracy enables precise targeting! This will prevent alert fatigue.', NULL, NULL, NULL, FALSE, NULL, NULL, 3, '2025-05-02 09:30:00', NOW()),

-- Comments on Project 1038 reports
(6123, 1038, 2075, 4185, 5095, 4, '100+ professions with comprehensive data will give students many options to explore. Great data collection!', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2025-04-16 09:00:00', NOW()),
(6124, 1038, 2075, 4186, 5096, 4, 'Using validated instruments like Holland Code and Big Five ensures scientific rigor. 10-minute completion is reasonable.', NULL, NULL, NULL, FALSE, NULL, NULL, 4, '2025-05-06 09:30:00', NOW()),

-- Comments on Project 1039 reports
(6125, 1039, 2077, 4190, 5097, 5, '500+ access points serving 10,000 concurrent devices is impressive scale! The weak spot identification is actionable.', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2025-04-19 09:00:00', NOW()),
(6126, 1039, 2077, 4191, 5098, 5, 'PRTG with SNMP, NetFlow, and custom sensors will provide comprehensive visibility. Good tool selection!', NULL, NULL, NULL, FALSE, NULL, NULL, 5, '2025-05-09 09:30:00', NOW()),

-- Comments on Project 1040 reports
(6127, 1040, 2079, 4195, 5099, 6, '500 student responses is a great sample size! 60% struggling with budgeting shows clear need for this app.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2025-04-21 09:00:00', NOW()),
(6128, 1040, 2079, 4196, 5100, 6, '5 budget templates for different student situations shows good user research! Recommended allocations will guide students.', NULL, NULL, NULL, FALSE, NULL, NULL, 6, '2025-05-11 09:30:00', NOW());
