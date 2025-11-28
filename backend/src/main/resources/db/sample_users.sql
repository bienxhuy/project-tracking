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

-- Projects (20 entries)
INSERT INTO projects (id, title, content, objectives, year, semester, faculty, batch, status, completion_percentage, is_only_des_locked, instructor_id, start_date, end_date, is_locked, locked_by_id, locked_at, created_by_id, created_at, updated_at) VALUES
    (1001, 'PoseCare Telehealth Platform', 'Telehealth platform connecting physiotherapy students with remote clinicians for rapid posture feedback.', 'Deliver secure video visits, symptom journaling, and adherence analytics within one dashboard.', 2025, 1, 'Health Sciences', 'POSE-25A', 'ACTIVE', 42.5, FALSE, 3, '2025-01-08', '2025-05-01', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1002, 'Campus Navigator AR', 'Augmented reality assistant that guides new cohorts to labs, studios, and student services.', 'Ship indoor positioning beacons, AR overlays, and accessibility friendly routes.', 2025, 1, 'Engineering', 'POSE-25A', 'ACTIVE', 38.0, FALSE, 4, '2025-01-15', '2025-05-10', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1003, 'Smart Studio Scheduler', 'Resource optimizer that balances occupancy, lighting, and equipment need for design studios.', 'Predict bottlenecks, auto assign rooms, and surface maintenance alerts.', 2025, 1, 'Creative Media', 'POSE-25A', 'ACTIVE', 33.5, FALSE, 5, '2025-01-20', '2025-05-20', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1004, 'AI Posture Coach', 'Computer vision pipeline that scores posture drills and recommends corrective micro exercises.', 'Blend pose estimation, scoring rubrics, and push notifications for daily practice.', 2025, 1, 'Health Sciences', 'POSE-25B', 'ACTIVE', 47.0, FALSE, 6, '2025-01-25', '2025-06-01', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1005, 'Virtual Lab Companion', 'Guided lab assistant that streams protocols, timers, and safety cues through mixed reality.', 'Digitize reagent logs, enable touchless controls, and capture quick reflections for grading.', 2025, 2, 'Biomedical Engineering', 'POSE-25B', 'ACTIVE', 29.0, TRUE, 7, '2025-02-01', '2025-06-10', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1006, 'Sustainability Tracker', 'Mobile app that scores campus events for waste, transport, and energy impact.', 'Automate survey capture, offer nudges, and publish transparent scoreboards.', 2025, 2, 'Environmental Science', 'POSE-24C', 'COMPLETED', 100.0, FALSE, 8, '2024-09-05', '2025-01-30', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1007, 'Motion Capture Analytics', 'Analytics service that compares rehearsal footage against gold standard biomechanics.', 'Produce delta heatmaps, personalized drills, and shareable highlight reels.', 2025, 2, 'Data Science', 'POSE-24C', 'ACTIVE', 58.0, FALSE, 9, '2024-09-10', '2025-02-05', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1008, 'Learning Journey Hub', 'Reflection hub that fuses project evidence, mentor notes, and competency maps.', 'Integrate rubric templates, AI summaries, and export ready accreditation packets.', 2025, 2, 'Education', 'POSE-24C', 'ACTIVE', 44.0, FALSE, 10, '2024-09-15', '2025-02-15', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1009, 'Pose Robotics Interface', 'Low latency control wall that lets instructors replay capture data into collaborative robots.', 'Bridge ROS pipelines, define safety envelopes, and support tactile lesson plans.', 2025, 2, 'Robotics', 'POSE-24D', 'ACTIVE', 36.0, FALSE, 3, '2024-09-20', '2025-03-01', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1010, 'Research Paper CoPilot', 'AI assistant that tracks citations, drafts outlines, and enforces academic integrity policies.', 'Provide live grammar guidance, claim verification, and export ready LaTeX templates.', 2025, 2, 'Library Science', 'POSE-24D', 'ACTIVE', 52.0, FALSE, 4, '2024-10-01', '2025-03-15', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1011, 'Fitness Gamification Portal', 'Social fitness layer that awards badges for precise posture routines logged on campus.', 'Blend leaderboards, peer alliances, and automated verification from sensors.', 2024, 1, 'Health Sciences', 'POSE-24E', 'COMPLETED', 100.0, TRUE, 5, '2024-10-05', '2025-03-22', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1012, 'Accessibility Insight Dashboard', 'Dashboard that visualizes barrier reports, wayfinding heatmaps, and equipment uptime.', 'Centralize data from surveys, IoT counters, and service tickets for fast decisions.', 2024, 1, 'Urban Planning', 'POSE-24E', 'ACTIVE', 61.0, FALSE, 6, '2024-10-10', '2025-03-30', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1013, 'Smart Attendance Vision', 'Edge vision box that validates attendance using gait signatures in rehearsal rooms.', 'Respect privacy budgets, emit encrypted logs, and sync with LMS rosters.', 2024, 1, 'Computer Engineering', 'POSE-24F', 'ACTIVE', 48.0, FALSE, 7, '2024-10-15', '2025-04-05', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1014, 'Peer Mentorship Hub', 'Matching engine that pairs advanced cohorts with mentees plus structured learning playlists.', 'Automate pairing rules, coach notes, and survey loops for semester long mentoring.', 2024, 1, 'Education', 'POSE-24F', 'ACTIVE', 54.0, FALSE, 8, '2024-10-20', '2025-04-12', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1015, 'Wellness Pulse Monitor', 'Wearable friendly service that aggregates vitals, mood check ins, and recovery plans.', 'Issue proactive nudges, triage to counselors, and power anonymized cohort trends.', 2024, 1, 'Behavioral Science', 'POSE-24F', 'ACTIVE', 57.5, TRUE, 9, '2024-11-01', '2025-04-25', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1016, 'Collaborative Whiteboard AI', 'Real time whiteboard that transcribes sketches, clusters ideas, and syncs to sprint boards.', 'Detect action items, recommend assets, and preserve gesture playback.', 2024, 2, 'Product Design', 'POSE-24G', 'LOCKED', 65.0, FALSE, 10, '2024-11-05', '2025-05-05', TRUE, 2, '2024-12-15 09:00:00', 1, NOW(), NOW()),
    (1017, 'Capstone Portfolio Vault', 'Secure evidence locker that tracks approvals, reviewers, and final showcase readiness.', 'Offer drag and drop templates, rubric alignment, and timed publishing windows.', 2024, 2, 'Creative Media', 'POSE-24G', 'ACTIVE', 49.0, FALSE, 3, '2024-11-10', '2025-05-12', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1018, 'Lab Inventory Assistant', 'Inventory bot that scans barcodes, enforces expiry policies, and predicts restock windows.', 'Unify purchase orders, automate alerts, and expose API hooks to schedulers.', 2024, 2, 'Biomedical Engineering', 'POSE-24G', 'ACTIVE', 53.0, TRUE, 4, '2024-11-15', '2025-05-20', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1019, 'AI Feedback Studio', 'Feedback environment where ensembles upload takes and receive AI assisted critique alongside faculty notes.', 'Score timing, tone, and coordination plus recommend micro lessons.', 2024, 2, 'Music Technology', 'POSE-24H', 'ACTIVE', 46.0, FALSE, 5, '2024-11-20', '2025-05-28', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (1020, 'Internship Matching Engine', 'Placement service that blends skill telemetry, employer briefs, and visa readiness steps.', 'Automate outreach cadences, alert advisors, and surface reflection prompts.', 2024, 2, 'Business Analytics', 'POSE-24H', 'ACTIVE', 51.0, FALSE, 6, '2024-12-01', '2025-06-05', FALSE, NULL, NULL, 1, NOW(), NOW());

-- Milestones (40 entries)
INSERT INTO milestones (id, project_id, title, description, status, order_number, completion_percentage, start_date, end_date, is_locked, locked_by_id, locked_at, created_by_id, created_at, updated_at) VALUES
    (2001, 1001, 'Discovery & Planning', 'Kickoff research plan for PoseCare Telehealth Platform.', 'IN_PROGRESS', 1, 42.5, '2025-01-08', '2025-02-22', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2002, 1001, 'Implementation & Review', 'Deliver prototype and retrospective for PoseCare Telehealth Platform.', 'IN_PROGRESS', 2, 42.5, '2025-02-23', '2025-05-01', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2003, 1002, 'Discovery & Planning', 'Kickoff research plan for Campus Navigator AR.', 'IN_PROGRESS', 1, 38.0, '2025-01-15', '2025-03-01', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2004, 1002, 'Implementation & Review', 'Deliver prototype and retrospective for Campus Navigator AR.', 'IN_PROGRESS', 2, 38.0, '2025-03-02', '2025-05-10', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2005, 1003, 'Discovery & Planning', 'Kickoff research plan for Smart Studio Scheduler.', 'IN_PROGRESS', 1, 33.5, '2025-01-20', '2025-03-06', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2006, 1003, 'Implementation & Review', 'Deliver prototype and retrospective for Smart Studio Scheduler.', 'IN_PROGRESS', 2, 33.5, '2025-03-07', '2025-05-20', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2007, 1004, 'Discovery & Planning', 'Kickoff research plan for AI Posture Coach.', 'IN_PROGRESS', 1, 47.0, '2025-01-25', '2025-03-11', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2008, 1004, 'Implementation & Review', 'Deliver prototype and retrospective for AI Posture Coach.', 'IN_PROGRESS', 2, 47.0, '2025-03-12', '2025-06-01', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2009, 1005, 'Discovery & Planning', 'Kickoff research plan for Virtual Lab Companion.', 'IN_PROGRESS', 1, 29.0, '2025-02-01', '2025-03-18', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2010, 1005, 'Implementation & Review', 'Deliver prototype and retrospective for Virtual Lab Companion.', 'IN_PROGRESS', 2, 29.0, '2025-03-19', '2025-06-10', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2011, 1006, 'Discovery & Planning', 'Kickoff research plan for Sustainability Tracker.', 'COMPLETED', 1, 60.0, '2024-09-05', '2024-10-20', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2012, 1006, 'Implementation & Review', 'Deliver prototype and retrospective for Sustainability Tracker.', 'COMPLETED', 2, 100.0, '2024-10-21', '2025-01-30', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2013, 1007, 'Discovery & Planning', 'Kickoff research plan for Motion Capture Analytics.', 'COMPLETED', 1, 58.0, '2024-09-10', '2024-10-25', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2014, 1007, 'Implementation & Review', 'Deliver prototype and retrospective for Motion Capture Analytics.', 'IN_PROGRESS', 2, 58.0, '2024-10-26', '2025-02-05', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2015, 1008, 'Discovery & Planning', 'Kickoff research plan for Learning Journey Hub.', 'IN_PROGRESS', 1, 44.0, '2024-09-15', '2024-10-30', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2016, 1008, 'Implementation & Review', 'Deliver prototype and retrospective for Learning Journey Hub.', 'IN_PROGRESS', 2, 44.0, '2024-10-31', '2025-02-15', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2017, 1009, 'Discovery & Planning', 'Kickoff research plan for Pose Robotics Interface.', 'IN_PROGRESS', 1, 36.0, '2024-09-20', '2024-11-04', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2018, 1009, 'Implementation & Review', 'Deliver prototype and retrospective for Pose Robotics Interface.', 'IN_PROGRESS', 2, 36.0, '2024-11-05', '2025-03-01', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2019, 1010, 'Discovery & Planning', 'Kickoff research plan for Research Paper CoPilot.', 'IN_PROGRESS', 1, 52.0, '2024-10-01', '2024-11-15', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2020, 1010, 'Implementation & Review', 'Deliver prototype and retrospective for Research Paper CoPilot.', 'IN_PROGRESS', 2, 52.0, '2024-11-16', '2025-03-15', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2021, 1011, 'Discovery & Planning', 'Kickoff research plan for Fitness Gamification Portal.', 'COMPLETED', 1, 60.0, '2024-10-05', '2024-11-19', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2022, 1011, 'Implementation & Review', 'Deliver prototype and retrospective for Fitness Gamification Portal.', 'COMPLETED', 2, 100.0, '2024-11-20', '2025-03-22', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2023, 1012, 'Discovery & Planning', 'Kickoff research plan for Accessibility Insight Dashboard.', 'COMPLETED', 1, 60.0, '2024-10-10', '2024-11-24', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2024, 1012, 'Implementation & Review', 'Deliver prototype and retrospective for Accessibility Insight Dashboard.', 'IN_PROGRESS', 2, 61.0, '2024-11-25', '2025-03-30', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2025, 1013, 'Discovery & Planning', 'Kickoff research plan for Smart Attendance Vision.', 'IN_PROGRESS', 1, 48.0, '2024-10-15', '2024-11-29', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2026, 1013, 'Implementation & Review', 'Deliver prototype and retrospective for Smart Attendance Vision.', 'IN_PROGRESS', 2, 48.0, '2024-11-30', '2025-04-05', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2027, 1014, 'Discovery & Planning', 'Kickoff research plan for Peer Mentorship Hub.', 'IN_PROGRESS', 1, 54.0, '2024-10-20', '2024-12-04', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2028, 1014, 'Implementation & Review', 'Deliver prototype and retrospective for Peer Mentorship Hub.', 'IN_PROGRESS', 2, 54.0, '2024-12-05', '2025-04-12', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2029, 1015, 'Discovery & Planning', 'Kickoff research plan for Wellness Pulse Monitor.', 'COMPLETED', 1, 57.5, '2024-11-01', '2024-12-16', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2030, 1015, 'Implementation & Review', 'Deliver prototype and retrospective for Wellness Pulse Monitor.', 'IN_PROGRESS', 2, 57.5, '2024-12-17', '2025-04-25', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2031, 1016, 'Discovery & Planning', 'Kickoff research plan for Collaborative Whiteboard AI.', 'IN_PROGRESS', 1, 60.0, '2024-11-05', '2024-12-20', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2032, 1016, 'Implementation & Review', 'Deliver prototype and retrospective for Collaborative Whiteboard AI.', 'LOCKED', 2, 65.0, '2024-12-21', '2025-05-05', TRUE, 2, '2024-12-15 09:00:00', 1, NOW(), NOW()),
    (2033, 1017, 'Discovery & Planning', 'Kickoff research plan for Capstone Portfolio Vault.', 'IN_PROGRESS', 1, 49.0, '2024-11-10', '2024-12-25', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2034, 1017, 'Implementation & Review', 'Deliver prototype and retrospective for Capstone Portfolio Vault.', 'IN_PROGRESS', 2, 49.0, '2024-12-26', '2025-05-12', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2035, 1018, 'Discovery & Planning', 'Kickoff research plan for Lab Inventory Assistant.', 'IN_PROGRESS', 1, 53.0, '2024-11-15', '2024-12-30', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2036, 1018, 'Implementation & Review', 'Deliver prototype and retrospective for Lab Inventory Assistant.', 'IN_PROGRESS', 2, 53.0, '2024-12-31', '2025-05-20', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2037, 1019, 'Discovery & Planning', 'Kickoff research plan for AI Feedback Studio.', 'IN_PROGRESS', 1, 46.0, '2024-11-20', '2025-01-04', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2038, 1019, 'Implementation & Review', 'Deliver prototype and retrospective for AI Feedback Studio.', 'IN_PROGRESS', 2, 46.0, '2025-01-05', '2025-05-28', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2039, 1020, 'Discovery & Planning', 'Kickoff research plan for Internship Matching Engine.', 'IN_PROGRESS', 1, 51.0, '2024-12-01', '2025-01-15', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (2040, 1020, 'Implementation & Review', 'Deliver prototype and retrospective for Internship Matching Engine.', 'IN_PROGRESS', 2, 51.0, '2025-01-16', '2025-06-05', FALSE, NULL, NULL, 1, NOW(), NOW());

-- Tasks (40 entries)
INSERT INTO tasks (id, project_id, milestone_id, title, description, status, assigned_to_id, start_date, end_date, is_locked, locked_by_id, locked_at, created_by_id, created_at, updated_at) VALUES
    (3001, 1001, 2001, 'Plan workflows for PoseCare Telehealth Platform', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 11, '2025-01-08', '2025-01-29', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3002, 1001, 2002, 'Build demo for PoseCare Telehealth Platform', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 12, '2025-02-23', '2025-03-25', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3003, 1002, 2003, 'Plan workflows for Campus Navigator AR', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 13, '2025-01-15', '2025-02-05', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3004, 1002, 2004, 'Build demo for Campus Navigator AR', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 14, '2025-03-02', '2025-04-01', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3005, 1003, 2005, 'Plan workflows for Smart Studio Scheduler', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 15, '2025-01-20', '2025-02-10', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3006, 1003, 2006, 'Build demo for Smart Studio Scheduler', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 16, '2025-03-07', '2025-04-06', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3007, 1004, 2007, 'Plan workflows for AI Posture Coach', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 17, '2025-01-25', '2025-02-15', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3008, 1004, 2008, 'Build demo for AI Posture Coach', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 18, '2025-03-12', '2025-04-11', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3009, 1005, 2009, 'Plan workflows for Virtual Lab Companion', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 19, '2025-02-01', '2025-02-22', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3010, 1005, 2010, 'Build demo for Virtual Lab Companion', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 20, '2025-03-19', '2025-04-18', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3011, 1006, 2011, 'Plan workflows for Sustainability Tracker', 'Consolidate requirements, constraints, and initial wireframes.', 'COMPLETED', 21, '2024-09-05', '2024-09-26', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3012, 1006, 2012, 'Build demo for Sustainability Tracker', 'Implement core features, capture QA notes, and prep showcase clip.', 'COMPLETED', 22, '2024-10-21', '2024-11-20', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3013, 1007, 2013, 'Plan workflows for Motion Capture Analytics', 'Consolidate requirements, constraints, and initial wireframes.', 'COMPLETED', 23, '2024-09-10', '2024-10-01', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3014, 1007, 2014, 'Build demo for Motion Capture Analytics', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 24, '2024-10-26', '2024-11-25', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3015, 1008, 2015, 'Plan workflows for Learning Journey Hub', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 25, '2024-09-15', '2024-10-06', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3016, 1008, 2016, 'Build demo for Learning Journey Hub', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 26, '2024-10-31', '2024-11-30', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3017, 1009, 2017, 'Plan workflows for Pose Robotics Interface', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 27, '2024-09-20', '2024-10-11', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3018, 1009, 2018, 'Build demo for Pose Robotics Interface', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 28, '2024-11-05', '2024-12-05', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3019, 1010, 2019, 'Plan workflows for Research Paper CoPilot', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 29, '2024-10-01', '2024-10-22', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3020, 1010, 2020, 'Build demo for Research Paper CoPilot', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 30, '2024-11-16', '2024-12-16', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3021, 1011, 2021, 'Plan workflows for Fitness Gamification Portal', 'Consolidate requirements, constraints, and initial wireframes.', 'COMPLETED', 31, '2024-10-05', '2024-10-26', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3022, 1011, 2022, 'Build demo for Fitness Gamification Portal', 'Implement core features, capture QA notes, and prep showcase clip.', 'COMPLETED', 32, '2024-11-20', '2024-12-20', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3023, 1012, 2023, 'Plan workflows for Accessibility Insight Dashboard', 'Consolidate requirements, constraints, and initial wireframes.', 'COMPLETED', 33, '2024-10-10', '2024-10-31', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3024, 1012, 2024, 'Build demo for Accessibility Insight Dashboard', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 34, '2024-11-25', '2024-12-25', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3025, 1013, 2025, 'Plan workflows for Smart Attendance Vision', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 35, '2024-10-15', '2024-11-05', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3026, 1013, 2026, 'Build demo for Smart Attendance Vision', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 36, '2024-11-30', '2024-12-30', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3027, 1014, 2027, 'Plan workflows for Peer Mentorship Hub', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 37, '2024-10-20', '2024-11-10', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3028, 1014, 2028, 'Build demo for Peer Mentorship Hub', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 38, '2024-12-05', '2025-01-04', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3029, 1015, 2029, 'Plan workflows for Wellness Pulse Monitor', 'Consolidate requirements, constraints, and initial wireframes.', 'COMPLETED', 39, '2024-11-01', '2024-11-22', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3030, 1015, 2030, 'Build demo for Wellness Pulse Monitor', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 40, '2024-12-17', '2025-01-16', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3031, 1016, 2031, 'Plan workflows for Collaborative Whiteboard AI', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 41, '2024-11-05', '2024-11-26', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3032, 1016, 2032, 'Build demo for Collaborative Whiteboard AI', 'Implement core features, capture QA notes, and prep showcase clip.', 'LOCKED', 42, '2024-12-21', '2025-01-20', TRUE, 2, '2024-12-15 09:00:00', 1, NOW(), NOW()),
    (3033, 1017, 2033, 'Plan workflows for Capstone Portfolio Vault', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 43, '2024-11-10', '2024-12-01', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3034, 1017, 2034, 'Build demo for Capstone Portfolio Vault', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 44, '2024-12-26', '2025-01-25', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3035, 1018, 2035, 'Plan workflows for Lab Inventory Assistant', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 45, '2024-11-15', '2024-12-06', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3036, 1018, 2036, 'Build demo for Lab Inventory Assistant', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 46, '2024-12-31', '2025-01-30', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3037, 1019, 2037, 'Plan workflows for AI Feedback Studio', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 47, '2024-11-20', '2024-12-11', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3038, 1019, 2038, 'Build demo for AI Feedback Studio', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 48, '2025-01-05', '2025-02-04', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3039, 1020, 2039, 'Plan workflows for Internship Matching Engine', 'Consolidate requirements, constraints, and initial wireframes.', 'IN_PROGRESS', 49, '2024-12-01', '2024-12-22', FALSE, NULL, NULL, 1, NOW(), NOW()),
    (3040, 1020, 2040, 'Build demo for Internship Matching Engine', 'Implement core features, capture QA notes, and prep showcase clip.', 'IN_PROGRESS', 50, '2025-01-16', '2025-02-15', FALSE, NULL, NULL, 1, NOW(), NOW());

-- Project members (60 entries)
INSERT INTO project_members (id, project_id, user_id, role, joined_at, is_active, created_at, updated_at) VALUES
    (4001, 1001, 3, 'INSTRUCTOR', '2024-12-25 09:00:00', TRUE, NOW(), NOW()),
    (4002, 1001, 11, 'STUDENT', '2025-01-01 10:00:00', TRUE, NOW(), NOW()),
    (4003, 1001, 12, 'STUDENT', '2025-01-03 11:00:00', TRUE, NOW(), NOW()),
    (4004, 1002, 4, 'INSTRUCTOR', '2025-01-01 09:00:00', TRUE, NOW(), NOW()),
    (4005, 1002, 13, 'STUDENT', '2025-01-08 10:00:00', TRUE, NOW(), NOW()),
    (4006, 1002, 14, 'STUDENT', '2025-01-10 11:00:00', TRUE, NOW(), NOW()),
    (4007, 1003, 5, 'INSTRUCTOR', '2025-01-06 09:00:00', TRUE, NOW(), NOW()),
    (4008, 1003, 15, 'STUDENT', '2025-01-13 10:00:00', TRUE, NOW(), NOW()),
    (4009, 1003, 16, 'STUDENT', '2025-01-15 11:00:00', TRUE, NOW(), NOW()),
    (4010, 1004, 6, 'INSTRUCTOR', '2025-01-11 09:00:00', TRUE, NOW(), NOW()),
    (4011, 1004, 17, 'STUDENT', '2025-01-18 10:00:00', TRUE, NOW(), NOW()),
    (4012, 1004, 18, 'STUDENT', '2025-01-20 11:00:00', TRUE, NOW(), NOW()),
    (4013, 1005, 7, 'INSTRUCTOR', '2025-01-18 09:00:00', TRUE, NOW(), NOW()),
    (4014, 1005, 19, 'STUDENT', '2025-01-25 10:00:00', TRUE, NOW(), NOW()),
    (4015, 1005, 20, 'STUDENT', '2025-01-27 11:00:00', TRUE, NOW(), NOW()),
    (4016, 1006, 8, 'INSTRUCTOR', '2024-08-22 09:00:00', TRUE, NOW(), NOW()),
    (4017, 1006, 21, 'STUDENT', '2024-08-29 10:00:00', TRUE, NOW(), NOW()),
    (4018, 1006, 22, 'STUDENT', '2024-08-31 11:00:00', TRUE, NOW(), NOW()),
    (4019, 1007, 9, 'INSTRUCTOR', '2024-08-27 09:00:00', TRUE, NOW(), NOW()),
    (4020, 1007, 23, 'STUDENT', '2024-09-03 10:00:00', TRUE, NOW(), NOW()),
    (4021, 1007, 24, 'STUDENT', '2024-09-05 11:00:00', TRUE, NOW(), NOW()),
    (4022, 1008, 10, 'INSTRUCTOR', '2024-09-01 09:00:00', TRUE, NOW(), NOW()),
    (4023, 1008, 25, 'STUDENT', '2024-09-08 10:00:00', TRUE, NOW(), NOW()),
    (4024, 1008, 26, 'STUDENT', '2024-09-10 11:00:00', TRUE, NOW(), NOW()),
    (4025, 1009, 3, 'INSTRUCTOR', '2024-09-06 09:00:00', TRUE, NOW(), NOW()),
    (4026, 1009, 27, 'STUDENT', '2024-09-13 10:00:00', TRUE, NOW(), NOW()),
    (4027, 1009, 28, 'STUDENT', '2024-09-15 11:00:00', TRUE, NOW(), NOW()),
    (4028, 1010, 4, 'INSTRUCTOR', '2024-09-17 09:00:00', TRUE, NOW(), NOW()),
    (4029, 1010, 29, 'STUDENT', '2024-09-24 10:00:00', TRUE, NOW(), NOW()),
    (4030, 1010, 30, 'STUDENT', '2024-09-26 11:00:00', TRUE, NOW(), NOW()),
    (4031, 1011, 5, 'INSTRUCTOR', '2024-09-21 09:00:00', TRUE, NOW(), NOW()),
    (4032, 1011, 31, 'STUDENT', '2024-09-28 10:00:00', TRUE, NOW(), NOW()),
    (4033, 1011, 32, 'STUDENT', '2024-09-30 11:00:00', TRUE, NOW(), NOW()),
    (4034, 1012, 6, 'INSTRUCTOR', '2024-09-26 09:00:00', TRUE, NOW(), NOW()),
    (4035, 1012, 33, 'STUDENT', '2024-10-03 10:00:00', TRUE, NOW(), NOW()),
    (4036, 1012, 34, 'STUDENT', '2024-10-05 11:00:00', TRUE, NOW(), NOW()),
    (4037, 1013, 7, 'INSTRUCTOR', '2024-10-01 09:00:00', TRUE, NOW(), NOW()),
    (4038, 1013, 35, 'STUDENT', '2024-10-08 10:00:00', TRUE, NOW(), NOW()),
    (4039, 1013, 36, 'STUDENT', '2024-10-10 11:00:00', TRUE, NOW(), NOW()),
    (4040, 1014, 8, 'INSTRUCTOR', '2024-10-06 09:00:00', TRUE, NOW(), NOW()),
    (4041, 1014, 37, 'STUDENT', '2024-10-13 10:00:00', TRUE, NOW(), NOW()),
    (4042, 1014, 38, 'STUDENT', '2024-10-15 11:00:00', TRUE, NOW(), NOW()),
    (4043, 1015, 9, 'INSTRUCTOR', '2024-10-18 09:00:00', TRUE, NOW(), NOW()),
    (4044, 1015, 39, 'STUDENT', '2024-10-25 10:00:00', TRUE, NOW(), NOW()),
    (4045, 1015, 40, 'STUDENT', '2024-10-27 11:00:00', TRUE, NOW(), NOW()),
    (4046, 1016, 10, 'INSTRUCTOR', '2024-10-22 09:00:00', TRUE, NOW(), NOW()),
    (4047, 1016, 41, 'STUDENT', '2024-10-29 10:00:00', TRUE, NOW(), NOW()),
    (4048, 1016, 42, 'STUDENT', '2024-10-31 11:00:00', TRUE, NOW(), NOW()),
    (4049, 1017, 3, 'INSTRUCTOR', '2024-10-27 09:00:00', TRUE, NOW(), NOW()),
    (4050, 1017, 43, 'STUDENT', '2024-11-03 10:00:00', TRUE, NOW(), NOW()),
    (4051, 1017, 44, 'STUDENT', '2024-11-05 11:00:00', TRUE, NOW(), NOW()),
    (4052, 1018, 4, 'INSTRUCTOR', '2024-11-01 09:00:00', TRUE, NOW(), NOW()),
    (4053, 1018, 45, 'STUDENT', '2024-11-08 10:00:00', TRUE, NOW(), NOW()),
    (4054, 1018, 46, 'STUDENT', '2024-11-10 11:00:00', TRUE, NOW(), NOW()),
    (4055, 1019, 5, 'INSTRUCTOR', '2024-11-06 09:00:00', TRUE, NOW(), NOW()),
    (4056, 1019, 47, 'STUDENT', '2024-11-13 10:00:00', TRUE, NOW(), NOW()),
    (4057, 1019, 48, 'STUDENT', '2024-11-15 11:00:00', TRUE, NOW(), NOW()),
    (4058, 1020, 6, 'INSTRUCTOR', '2024-11-17 09:00:00', TRUE, NOW(), NOW()),
    (4059, 1020, 49, 'STUDENT', '2024-11-24 10:00:00', TRUE, NOW(), NOW()),
    (4060, 1020, 50, 'STUDENT', '2024-11-26 11:00:00', TRUE, NOW(), NOW());

-- Reports (60 entries)
-- Reports can be linked to projects, milestones, or tasks
-- Status: SUBMITTED or LOCKED
INSERT INTO reports (id, title, content, status, submitted_at, submitted_by_id, project_id, milestone_id, task_id, author_id, report_date, start_date, end_date, is_locked, locked_by_id, locked_at, created_by_id, created_at, updated_at) VALUES
    -- Reports for Project 1001 (PoseCare Telehealth Platform)
    (5001, 'Weekly Progress Report - Week 1', 'Completed initial requirements gathering and stakeholder interviews. Identified key features for video consultations.', 'SUBMITTED', '2025-01-15 17:00:00', 11, 1001, 2001, 3001, 11, '2025-01-15', '2025-01-08', '2025-01-15', FALSE, NULL, NULL, 11, '2025-01-15 10:00:00', NOW()),
    (5002, 'Weekly Progress Report - Week 2', 'Finished wireframe designs for dashboard. Started implementation of user authentication module.', 'SUBMITTED', '2025-01-22 17:00:00', 12, 1001, 2001, 3001, 12, '2025-01-22', '2025-01-15', '2025-01-22', FALSE, NULL, NULL, 12, '2025-01-22 10:00:00', NOW()),
    (5003, 'Milestone Review Report', 'Completed Discovery & Planning phase. Ready to proceed with implementation.', 'SUBMITTED', '2025-02-20 16:00:00', 11, 1001, 2001, NULL, 11, '2025-02-20', '2025-02-15', '2025-02-22', FALSE, NULL, NULL, 11, '2025-02-20 10:00:00', NOW()),
    (5004, 'Implementation Progress Report', 'Core video consultation feature is 70% complete. Integration with payment gateway pending.', 'SUBMITTED', '2025-03-10 17:00:00', 12, 1001, 2002, 3002, 12, '2025-03-10', '2025-03-01', '2025-03-15', FALSE, NULL, NULL, 12, '2025-03-10 10:00:00', NOW()),
    
    -- Reports for Project 1002 (Campus Navigator AR)
    (5005, 'Initial Planning Report', 'Completed campus mapping and identified key navigation points. AR overlay designs approved.', 'SUBMITTED', '2025-01-22 17:00:00', 13, 1002, 2003, 3003, 13, '2025-01-22', '2025-01-15', '2025-01-22', FALSE, NULL, NULL, 13, '2025-01-22 10:00:00', NOW()),
    (5006, 'AR Prototype Report', 'Successfully implemented basic AR navigation. Testing with focus groups scheduled for next week.', 'SUBMITTED', '2025-03-20 17:00:00', 14, 1002, 2004, 3004, 14, '2025-03-20', '2025-03-10', '2025-03-25', FALSE, NULL, NULL, 14, '2025-03-20 10:00:00', NOW()),
    
    -- Reports for Project 1003 (Smart Studio Scheduler)
    (5007, 'Requirements Analysis Report', 'Gathered requirements from 15 studio users. Identified key scheduling conflicts.', 'SUBMITTED', '2025-01-27 17:00:00', 15, 1003, 2005, 3005, 15, '2025-01-27', '2025-01-20', '2025-01-27', FALSE, NULL, NULL, 15, '2025-01-27 10:00:00', NOW()),
    (5008, 'Scheduler Algorithm Report', 'Developed optimization algorithm for room assignments. Reduces conflicts by 85%.', 'SUBMITTED', '2025-03-25 17:00:00', 16, 1003, 2006, 3006, 16, '2025-03-25', '2025-03-15', '2025-03-30', FALSE, NULL, NULL, 16, '2025-03-25 10:00:00', NOW()),
    
    -- Reports for Project 1004 (AI Posture Coach)
    (5009, 'Research Phase Report', 'Completed literature review on computer vision for posture analysis. Selected pose estimation model.', 'SUBMITTED', '2025-02-01 17:00:00', 17, 1004, 2007, 3007, 17, '2025-02-01', '2025-01-25', '2025-02-01', FALSE, NULL, NULL, 17, '2025-02-01 10:00:00', NOW()),
    (5010, 'MVP Development Report', 'Posture scoring model achieves 92% accuracy. Mobile app prototype ready for user testing.', 'SUBMITTED', '2025-03-28 17:00:00', 18, 1004, 2008, 3008, 18, '2025-03-28', '2025-03-20', '2025-04-01', FALSE, NULL, NULL, 18, '2025-03-28 10:00:00', NOW()),
    
    -- Reports for Project 1005 (Virtual Lab Companion)
    (5011, 'Lab Protocols Documentation', 'Documented 25 standard lab procedures. Created digital protocol templates.', 'SUBMITTED', '2025-02-08 17:00:00', 19, 1005, 2009, 3009, 19, '2025-02-08', '2025-02-01', '2025-02-08', FALSE, NULL, NULL, 19, '2025-02-08 10:00:00', NOW()),
    (5012, 'MR Interface Testing Report', 'Mixed reality interface tested with 10 students. Positive feedback received.', 'SUBMITTED', '2025-03-30 17:00:00', 20, 1005, 2010, 3010, 20, '2025-03-30', '2025-03-25', '2025-04-05', FALSE, NULL, NULL, 20, '2025-03-30 10:00:00', NOW()),
    
    -- Reports for Project 1006 (Sustainability Tracker) - COMPLETED
    (5013, 'Final Project Report', 'Completed all planned features. App launched successfully with 500+ active users.', 'LOCKED', '2025-01-25 17:00:00', 21, 1006, 2012, 3012, 21, '2025-01-25', '2025-01-20', '2025-01-30', TRUE, 3, '2025-01-26 09:00:00', 21, '2025-01-25 10:00:00', NOW()),
    (5014, 'Project Retrospective', 'Lessons learned and future improvements identified. Project met all success criteria.', 'LOCKED', '2025-01-28 17:00:00', 22, 1006, 2012, 3012, 22, '2025-01-28', '2025-01-25', '2025-01-30', TRUE, 8, '2025-01-29 09:00:00', 22, '2025-01-28 10:00:00', NOW()),
    
    -- Reports for Project 1007 (Motion Capture Analytics)
    (5015, 'Data Collection Report', 'Collected 200+ motion capture samples. Data preprocessing pipeline established.', 'SUBMITTED', '2024-10-15 17:00:00', 23, 1007, 2013, 3013, 23, '2024-10-15', '2024-10-01', '2024-10-15', FALSE, NULL, NULL, 23, '2024-10-15 10:00:00', NOW()),
    (5016, 'Analytics Dashboard Report', 'Dashboard prototype completed. Real-time visualization working correctly.', 'SUBMITTED', '2024-11-20 17:00:00', 24, 1007, 2014, 3014, 24, '2024-11-20', '2024-11-15', '2024-11-25', FALSE, NULL, NULL, 24, '2024-11-20 10:00:00', NOW()),
    
    -- Reports for Project 1008 (Learning Journey Hub)
    (5017, 'Competency Mapping Report', 'Mapped 50+ competencies to learning outcomes. Rubric templates created.', 'SUBMITTED', '2024-10-20 17:00:00', 25, 1008, 2015, 3015, 25, '2024-10-20', '2024-10-15', '2024-10-20', FALSE, NULL, NULL, 25, '2024-10-20 10:00:00', NOW()),
    (5018, 'Reflection Feature Report', 'Implemented reflection journal feature. AI summary generation integrated.', 'SUBMITTED', '2024-11-25 17:00:00', 26, 1008, 2016, 3016, 26, '2024-11-25', '2024-11-20', '2024-11-30', FALSE, NULL, NULL, 26, '2024-11-25 10:00:00', NOW()),
    
    -- Reports for Project 1009 (Pose Robotics Interface)
    (5019, 'ROS Integration Report', 'Successfully integrated with ROS middleware. Low latency communication achieved.', 'SUBMITTED', '2024-10-25 17:00:00', 27, 1009, 2017, 3017, 27, '2024-10-25', '2024-10-20', '2024-10-25', FALSE, NULL, NULL, 27, '2024-10-25 10:00:00', NOW()),
    (5020, 'Safety System Report', 'Implemented safety envelopes for robot control. All tests passed.', 'SUBMITTED', '2024-12-01 17:00:00', 28, 1009, 2018, 3018, 28, '2024-12-01', '2024-11-25', '2024-12-05', FALSE, NULL, NULL, 28, '2024-12-01 10:00:00', NOW()),
    
    -- Reports for Project 1010 (Research Paper CoPilot)
    (5021, 'Citation Database Report', 'Integrated with 5 major citation databases. Citation parser working accurately.', 'SUBMITTED', '2024-10-28 17:00:00', 29, 1010, 2019, 3019, 29, '2024-10-28', '2024-10-22', '2024-10-28', FALSE, NULL, NULL, 29, '2024-10-28 10:00:00', NOW()),
    (5022, 'AI Writing Assistant Report', 'LaTeX template generator completed. Grammar checking feature integrated.', 'SUBMITTED', '2024-12-10 17:00:00', 30, 1010, 2020, 3020, 30, '2024-12-10', '2024-12-05', '2024-12-16', FALSE, NULL, NULL, 30, '2024-12-10 10:00:00', NOW()),
    
    -- Reports for Project 1011 (Fitness Gamification Portal) - COMPLETED
    (5023, 'Gamification System Report', 'Implemented badge system and leaderboards. User engagement increased by 40%.', 'LOCKED', '2024-12-15 17:00:00', 31, 1011, 2022, 3022, 31, '2024-12-15', '2024-12-10', '2024-12-20', TRUE, 5, '2024-12-16 09:00:00', 31, '2024-12-15 10:00:00', NOW()),
    (5024, 'Final Evaluation Report', 'Project successfully completed. All objectives met. Ready for production.', 'LOCKED', '2024-12-18 17:00:00', 32, 1011, 2022, 3022, 32, '2024-12-18', '2024-12-15', '2024-12-20', TRUE, 5, '2024-12-19 09:00:00', 32, '2024-12-18 10:00:00', NOW()),
    
    -- Reports for Project 1012 (Accessibility Insight Dashboard)
    (5025, 'Survey Integration Report', 'Connected 3 survey platforms. Data aggregation pipeline working.', 'SUBMITTED', '2024-11-05 17:00:00', 33, 1012, 2023, 3023, 33, '2024-11-05', '2024-11-01', '2024-11-05', FALSE, NULL, NULL, 33, '2024-11-05 10:00:00', NOW()),
    (5026, 'Visualization Dashboard Report', 'Heatmaps and charts implemented. Real-time updates functioning correctly.', 'SUBMITTED', '2024-12-20 17:00:00', 34, 1012, 2024, 3024, 34, '2024-12-20', '2024-12-15', '2024-12-25', FALSE, NULL, NULL, 34, '2024-12-20 10:00:00', NOW()),
    
    -- Reports for Project 1013 (Smart Attendance Vision)
    (5027, 'Computer Vision Model Report', 'Trained gait recognition model. Accuracy: 94% on test dataset.', 'SUBMITTED', '2024-11-10 17:00:00', 35, 1013, 2025, 3025, 35, '2024-11-10', '2024-11-05', '2024-11-10', FALSE, NULL, NULL, 35, '2024-11-10 10:00:00', NOW()),
    (5028, 'Privacy Compliance Report', 'Implemented encryption for attendance logs. GDPR compliance verified.', 'SUBMITTED', '2024-12-25 17:00:00', 36, 1013, 2026, 3026, 36, '2024-12-25', '2024-12-20', '2024-12-30', FALSE, NULL, NULL, 36, '2024-12-25 10:00:00', NOW()),
    
    -- Reports for Project 1014 (Peer Mentorship Hub)
    (5029, 'Matching Algorithm Report', 'Developed compatibility scoring algorithm. Tested with 100 student pairs.', 'SUBMITTED', '2024-11-15 17:00:00', 37, 1014, 2027, 3027, 37, '2024-11-15', '2024-11-10', '2024-11-15', FALSE, NULL, NULL, 37, '2024-11-15 10:00:00', NOW()),
    (5030, 'Learning Playlist Report', 'Created 20 structured learning playlists. Integration with LMS completed.', 'SUBMITTED', '2025-01-02 17:00:00', 38, 1014, 2028, 3028, 38, '2025-01-02', '2024-12-28', '2025-01-04', FALSE, NULL, NULL, 38, '2025-01-02 10:00:00', NOW()),
    
    -- Reports for Project 1015 (Wellness Pulse Monitor)
    (5031, 'Sensor Integration Report', 'Integrated with 3 wearable device APIs. Data sync working reliably.', 'SUBMITTED', '2024-11-18 17:00:00', 39, 1015, 2029, 3029, 39, '2024-11-18', '2024-11-15', '2024-11-18', FALSE, NULL, NULL, 39, '2024-11-18 10:00:00', NOW()),
    (5032, 'Alert System Report', 'Implemented proactive health alerts. Counselor notification system active.', 'SUBMITTED', '2024-12-30 17:00:00', 40, 1015, 2030, 3030, 40, '2024-12-30', '2024-12-25', '2025-01-01', FALSE, NULL, NULL, 40, '2024-12-30 10:00:00', NOW()),
    
    -- Reports for Project 1016 (Collaborative Whiteboard AI)
    (5033, 'Real-time Collaboration Report', 'WebSocket implementation complete. Multi-user editing working smoothly.', 'SUBMITTED', '2024-11-20 17:00:00', 41, 1016, 2031, 3031, 41, '2024-11-20', '2024-11-18', '2024-11-20', FALSE, NULL, NULL, 41, '2024-11-20 10:00:00', NOW()),
    (5034, 'AI Transcription Report', 'Sketch-to-text conversion working. Action item detection accuracy: 87%.', 'LOCKED', '2024-12-20 17:00:00', 42, 1016, 2032, 3032, 42, '2024-12-20', '2024-12-15', '2024-12-25', TRUE, 2, '2024-12-21 09:00:00', 42, '2024-12-20 10:00:00', NOW()),
    
    -- Reports for Project 1017 (Capstone Portfolio Vault)
    (5035, 'Portfolio Template Report', 'Created 10 portfolio templates. Drag-and-drop interface implemented.', 'SUBMITTED', '2024-11-25 17:00:00', 43, 1017, 2033, 3033, 43, '2024-11-25', '2024-11-20', '2024-11-25', FALSE, NULL, NULL, 43, '2024-11-25 10:00:00', NOW()),
    (5036, 'Approval Workflow Report', 'Multi-level approval system working. Reviewer assignment automated.', 'SUBMITTED', '2025-01-10 17:00:00', 44, 1017, 2034, 3034, 44, '2025-01-10', '2025-01-05', '2025-01-15', FALSE, NULL, NULL, 44, '2025-01-10 10:00:00', NOW()),
    
    -- Reports for Project 1018 (Lab Inventory Assistant)
    (5037, 'Barcode Scanner Report', 'Mobile barcode scanning working. Inventory updates in real-time.', 'SUBMITTED', '2024-11-28 17:00:00', 45, 1018, 2035, 3035, 45, '2024-11-28', '2024-11-25', '2024-11-28', FALSE, NULL, NULL, 45, '2024-11-28 10:00:00', NOW()),
    (5038, 'Expiry Alert Report', 'Automated expiry alerts implemented. Purchase order integration pending.', 'SUBMITTED', '2025-01-15 17:00:00', 46, 1018, 2036, 3036, 46, '2025-01-15', '2025-01-10', '2025-01-20', FALSE, NULL, NULL, 46, '2025-01-15 10:00:00', NOW()),
    
    -- Reports for Project 1019 (AI Feedback Studio)
    (5039, 'Audio Analysis Report', 'Implemented pitch and timing analysis. Feedback generation in progress.', 'SUBMITTED', '2024-12-05 17:00:00', 47, 1019, 2037, 3037, 47, '2024-12-05', '2024-12-01', '2024-12-05', FALSE, NULL, NULL, 47, '2024-12-05 10:00:00', NOW()),
    (5040, 'Feedback UI Report', 'User-friendly feedback interface completed. Faculty review system integrated.', 'SUBMITTED', '2025-01-20 17:00:00', 48, 1019, 2038, 3038, 48, '2025-01-20', '2025-01-15', '2025-01-25', FALSE, NULL, NULL, 48, '2025-01-20 10:00:00', NOW()),
    
    -- Reports for Project 1020 (Internship Matching Engine)
    (5041, 'Skill Assessment Report', 'Skill telemetry system implemented. Employer profile matching working.', 'SUBMITTED', '2024-12-10 17:00:00', 49, 1020, 2039, 3039, 49, '2024-12-10', '2024-12-05', '2024-12-10', FALSE, NULL, NULL, 49, '2024-12-10 10:00:00', NOW()),
    (5042, 'Matching Algorithm Report', 'Compatibility scoring algorithm completed. Tested with 50 student-employer pairs.', 'SUBMITTED', '2025-01-25 17:00:00', 50, 1020, 2040, 3040, 50, '2025-01-25', '2025-01-20', '2025-01-30', FALSE, NULL, NULL, 50, '2025-01-25 10:00:00', NOW()),
    
    -- Additional milestone-level reports
    (5043, 'Quarterly Progress Summary', 'Q1 progress summary for PoseCare Telehealth Platform. All milestones on track.', 'SUBMITTED', '2025-03-31 17:00:00', 11, 1001, 2002, NULL, 11, '2025-03-31', '2025-03-25', '2025-04-05', FALSE, NULL, NULL, 11, '2025-03-31 10:00:00', NOW()),
    (5044, 'Technical Architecture Review', 'Architecture review completed. Scalability improvements identified.', 'SUBMITTED', '2025-02-28 17:00:00', 13, 1002, 2003, NULL, 13, '2025-02-28', '2025-02-20', '2025-03-01', FALSE, NULL, NULL, 13, '2025-02-28 10:00:00', NOW()),
    (5045, 'User Testing Results', 'User testing completed with 30 participants. Positive feedback received.', 'SUBMITTED', '2025-04-05 17:00:00', 15, 1003, 2006, NULL, 15, '2025-04-05', '2025-03-30', '2025-04-10', FALSE, NULL, NULL, 15, '2025-04-05 10:00:00', NOW()),
    (5046, 'Security Audit Report', 'Security audit completed. All vulnerabilities patched.', 'SUBMITTED', '2025-03-15 17:00:00', 17, 1004, 2008, NULL, 17, '2025-03-15', '2025-03-10', '2025-03-20', FALSE, NULL, NULL, 17, '2025-03-15 10:00:00', NOW()),
    (5047, 'Performance Optimization', 'Performance optimizations applied. Response time reduced by 60%.', 'SUBMITTED', '2024-11-30 17:00:00', 23, 1007, 2014, NULL, 23, '2024-11-30', '2024-11-25', '2024-12-05', FALSE, NULL, NULL, 23, '2024-11-30 10:00:00', NOW()),
    (5048, 'Integration Testing Report', 'All integration tests passed. System ready for staging deployment.', 'SUBMITTED', '2024-12-15 17:00:00', 27, 1009, 2018, NULL, 27, '2024-12-15', '2024-12-10', '2024-12-20', FALSE, NULL, NULL, 27, '2024-12-15 10:00:00', NOW()),
    (5049, 'Documentation Update', 'Updated API documentation. Developer guides published.', 'SUBMITTED', '2024-12-22 17:00:00', 29, 1010, 2020, NULL, 29, '2024-12-22', '2024-12-18', '2024-12-25', FALSE, NULL, NULL, 29, '2024-12-22 10:00:00', NOW()),
    (5050, 'Production Deployment Report', 'System deployed to production successfully. Monitoring in place.', 'LOCKED', '2024-12-20 17:00:00', 31, 1011, 2022, NULL, 31, '2024-12-20', '2024-12-18', '2024-12-22', TRUE, 5, '2024-12-21 09:00:00', 31, '2024-12-20 10:00:00', NOW()),
    
    -- Project-level reports (no milestone or task)
    (5051, 'Project Kickoff Summary', 'Kickoff meeting summary for PoseCare Telehealth Platform project.', 'SUBMITTED', '2025-01-10 17:00:00', 11, 1001, NULL, NULL, 11, '2025-01-10', '2025-01-08', '2025-01-15', FALSE, NULL, NULL, 11, '2025-01-10 10:00:00', NOW()),
    (5052, 'Stakeholder Feedback Summary', 'Compiled feedback from 10 stakeholders. Priorities adjusted accordingly.', 'SUBMITTED', '2025-02-15 17:00:00', 13, 1002, NULL, NULL, 13, '2025-02-15', '2025-02-10', '2025-02-20', FALSE, NULL, NULL, 13, '2025-02-15 10:00:00', NOW()),
    (5053, 'Risk Assessment Report', 'Identified 5 potential risks. Mitigation strategies documented.', 'SUBMITTED', '2025-01-25 17:00:00', 15, 1003, NULL, NULL, 15, '2025-01-25', '2025-01-20', '2025-01-30', FALSE, NULL, NULL, 15, '2025-01-25 10:00:00', NOW()),
    (5054, 'Budget Review Report', 'Budget review completed. All expenses within allocated limits.', 'SUBMITTED', '2025-03-01 17:00:00', 17, 1004, NULL, NULL, 17, '2025-03-01', '2025-02-25', '2025-03-05', FALSE, NULL, NULL, 17, '2025-03-01 10:00:00', NOW()),
    (5055, 'Team Performance Review', 'Team performance review for Virtual Lab Companion project.', 'SUBMITTED', '2025-04-01 17:00:00', 19, 1005, NULL, NULL, 19, '2025-04-01', '2025-03-28', '2025-04-05', FALSE, NULL, NULL, 19, '2025-04-01 10:00:00', NOW()),
    (5056, 'Final Project Presentation', 'Prepared final presentation for Sustainability Tracker project showcase.', 'LOCKED', '2025-01-28 17:00:00', 21, 1006, NULL, NULL, 21, '2025-01-28', '2025-01-25', '2025-01-30', TRUE, 8, '2025-01-29 09:00:00', 21, '2025-01-28 10:00:00', NOW()),
    (5057, 'Lessons Learned Document', 'Documented key lessons learned from Motion Capture Analytics project.', 'SUBMITTED', '2024-12-05 17:00:00', 23, 1007, NULL, NULL, 23, '2024-12-05', '2024-12-01', '2024-12-10', FALSE, NULL, NULL, 23, '2024-12-05 10:00:00', NOW()),
    (5058, 'Future Roadmap', 'Outlined future enhancements for Learning Journey Hub.', 'SUBMITTED', '2024-12-20 17:00:00', 25, 1008, NULL, NULL, 25, '2024-12-20', '2024-12-15', '2024-12-25', FALSE, NULL, NULL, 25, '2024-12-20 10:00:00', NOW()),
    (5059, 'Partnership Opportunities', 'Identified 3 potential industry partnerships for Pose Robotics Interface.', 'SUBMITTED', '2025-01-05 17:00:00', 27, 1009, NULL, NULL, 27, '2025-01-05', '2025-01-01', '2025-01-10', FALSE, NULL, NULL, 27, '2025-01-05 10:00:00', NOW()),
    (5060, 'Publication Proposal', 'Prepared research paper proposal for Research Paper CoPilot findings.', 'SUBMITTED', '2025-01-08 17:00:00', 29, 1010, NULL, NULL, 29, '2025-01-08', '2025-01-05', '2025-01-12', FALSE, NULL, NULL, 29, '2025-01-08 10:00:00', NOW());

-- Comments (120 entries)
-- Comments can be linked to projects, milestones, tasks, or reports
-- Some comments are replies (have parent_comment_id)
INSERT INTO comments (id, content, project_id, milestone_id, task_id, report_id, author_id, parent_comment_id, created_at, updated_at) VALUES
    -- Comments for Project 1001
    (6001, 'Great progress on the requirements gathering! Looking forward to seeing the wireframes.', 1001, NULL, NULL, NULL, 3, NULL, '2025-01-15 18:00:00', NOW()),
    (6002, 'Thanks! Wireframes will be ready by end of week.', 1001, NULL, NULL, NULL, 11, 6001, '2025-01-15 18:30:00', NOW()),
    (6003, 'The authentication module looks solid. Consider adding OAuth support for future integration.', 1001, NULL, NULL, NULL, 3, NULL, '2025-01-22 18:00:00', NOW()),
    (6004, 'Good suggestion! We''ll add it to the backlog.', 1001, NULL, NULL, NULL, 12, 6003, '2025-01-22 18:15:00', NOW()),
    (6005, 'Excellent work on the milestone review. All deliverables met expectations.', 1001, 2001, NULL, NULL, 3, NULL, '2025-02-20 17:00:00', NOW()),
    (6006, 'The video consultation feature is coming along nicely. Keep up the good work!', 1001, NULL, 3002, NULL, 3, NULL, '2025-03-10 18:00:00', NOW()),
    
    -- Comments for Project 1002
    (6007, 'The AR navigation concept is innovative! How are you handling indoor positioning?', 1002, NULL, NULL, NULL, 4, NULL, '2025-01-22 18:00:00', NOW()),
    (6008, 'We''re using a combination of beacons and Wi-Fi triangulation. I can share the details in the next report.', 1002, NULL, NULL, NULL, 13, 6007, '2025-01-22 18:30:00', NOW()),
    (6009, 'The prototype looks impressive! Consider adding voice navigation for accessibility.', 1002, NULL, NULL, NULL, 4, NULL, '2025-03-20 18:00:00', NOW()),
    (6010, 'Great idea! We''ll explore adding that feature.', 1002, NULL, NULL, NULL, 14, 6009, '2025-03-20 18:30:00', NOW()),
    
    -- Comments for Project 1003
    (6011, 'The scheduling algorithm is very efficient. Well done!', 1003, NULL, NULL, NULL, 5, NULL, '2025-01-27 18:00:00', NOW()),
    (6012, 'Thank you! We spent significant time optimizing it.', 1003, NULL, NULL, NULL, 15, 6011, '2025-01-27 18:30:00', NOW()),
    (6013, '85% conflict reduction is outstanding! Can you share the algorithm details?', 1003, NULL, NULL, NULL, 5, NULL, '2025-03-25 18:00:00', NOW()),
    (6014, 'I''ll include it in the technical documentation.', 1003, NULL, NULL, NULL, 16, 6013, '2025-03-25 18:45:00', NOW()),
    
    -- Comments for Project 1004
    (6015, 'The posture analysis model accuracy is excellent! What dataset did you use for training?', 1004, NULL, NULL, NULL, 6, NULL, '2025-02-01 18:00:00', NOW()),
    (6016, 'We used a combination of public datasets and our own collected data. About 10,000 samples total.', 1004, NULL, NULL, NULL, 17, 6015, '2025-02-01 18:30:00', NOW()),
    (6017, 'The mobile app prototype is user-friendly. Great UX design!', 1004, NULL, NULL, NULL, 6, NULL, '2025-03-28 18:00:00', NOW()),
    
    -- Comments for Project 1005
    (6018, 'The digital protocol templates are very comprehensive. Good work!', 1005, NULL, NULL, NULL, 7, NULL, '2025-02-08 18:00:00', NOW()),
    (6019, 'Thank you! We''ve covered 25 standard procedures so far.', 1005, NULL, NULL, NULL, 19, 6018, '2025-02-08 18:30:00', NOW()),
    (6020, 'The MR interface testing results are promising. Excellent progress!', 1005, NULL, NULL, NULL, 7, NULL, '2025-03-30 18:00:00', NOW()),
    
    -- Comments for Project 1006 (COMPLETED)
    (6021, 'Congratulations on completing the project! The app launch was very successful.', 1006, NULL, NULL, NULL, 8, NULL, '2025-01-25 18:00:00', NOW()),
    (6022, 'Thank you! It was a great learning experience.', 1006, NULL, NULL, NULL, 21, 6021, '2025-01-25 18:30:00', NOW()),
    (6023, 'The retrospective is insightful. Well documented lessons learned.', 1006, NULL, NULL, NULL, 8, NULL, '2025-01-28 18:00:00', NOW()),
    
    -- Comments on Reports
    (6024, 'This report is well-structured. Good job!', NULL, NULL, NULL, 5001, 3, NULL, '2025-01-15 19:00:00', NOW()),
    (6025, 'Thanks for the feedback!', NULL, NULL, NULL, 5001, 11, 6024, '2025-01-15 19:15:00', NOW()),
    (6026, 'The progress is impressive. Keep it up!', NULL, NULL, NULL, 5002, 3, NULL, '2025-01-22 19:00:00', NOW()),
    (6027, 'Excellent milestone review. All requirements met.', NULL, NULL, NULL, 5003, 3, NULL, '2025-02-20 18:00:00', NOW()),
    (6028, 'The implementation details are clear. Well documented.', NULL, NULL, NULL, 5004, 3, NULL, '2025-03-10 19:00:00', NOW()),
    (6029, 'Great work on the AR prototype!', NULL, NULL, NULL, 5006, 4, NULL, '2025-03-20 19:00:00', NOW()),
    (6030, 'The algorithm explanation is thorough. Excellent technical writing.', NULL, NULL, NULL, 5008, 5, NULL, '2025-03-25 19:00:00', NOW()),
    (6031, 'Congratulations on the project completion!', NULL, NULL, NULL, 5013, 8, NULL, '2025-01-25 19:00:00', NOW()),
    (6032, 'The retrospective provides valuable insights.', NULL, NULL, NULL, 5014, 8, NULL, '2025-01-28 19:00:00', NOW()),
    
    -- Comments for Project 1007
    (6033, 'The motion capture data collection is extensive. Good work!', 1007, NULL, NULL, NULL, 9, NULL, '2024-10-15 18:00:00', NOW()),
    (6034, 'Thanks! We collected over 200 samples.', 1007, NULL, NULL, NULL, 23, 6033, '2024-10-15 18:30:00', NOW()),
    (6035, 'The analytics dashboard looks professional. Great visualization!', 1007, NULL, NULL, NULL, 9, NULL, '2024-11-20 18:00:00', NOW()),
    
    -- Comments for Project 1008
    (6036, 'The competency mapping is comprehensive. Well done!', 1008, NULL, NULL, NULL, 10, NULL, '2024-10-20 18:00:00', NOW()),
    (6037, 'Thank you! We mapped 50+ competencies.', 1008, NULL, NULL, NULL, 25, 6036, '2024-10-20 18:30:00', NOW()),
    (6038, 'The AI summary feature is impressive. How accurate is it?', 1008, NULL, NULL, NULL, 10, NULL, '2024-11-25 18:00:00', NOW()),
    (6039, 'Initial testing shows about 85% accuracy. We''re working on improving it.', 1008, NULL, NULL, NULL, 26, 6038, '2024-11-25 18:45:00', NOW()),
    
    -- Comments for Project 1009
    (6040, 'The ROS integration is technically sound. Excellent work!', 1009, NULL, NULL, NULL, 3, NULL, '2024-10-25 18:00:00', NOW()),
    (6041, 'Thanks! The low latency was a key requirement.', 1009, NULL, NULL, NULL, 27, 6040, '2024-10-25 18:30:00', NOW()),
    (6042, 'Safety is paramount. Good to see it prioritized.', 1009, NULL, NULL, NULL, 3, NULL, '2024-12-01 18:00:00', NOW()),
    
    -- Comments for Project 1010
    (6043, 'The citation integration is seamless. Great work!', 1010, NULL, NULL, NULL, 4, NULL, '2024-10-28 18:00:00', NOW()),
    (6044, 'Thank you! We integrated 5 major databases.', 1010, NULL, NULL, NULL, 29, 6043, '2024-10-28 18:30:00', NOW()),
    (6045, 'The LaTeX generator will save a lot of time. Excellent feature!', 1010, NULL, NULL, NULL, 4, NULL, '2024-12-10 18:00:00', NOW()),
    
    -- Comments for Project 1011 (COMPLETED)
    (6046, 'The gamification features are engaging. Great job!', 1011, NULL, NULL, NULL, 5, NULL, '2024-12-15 18:00:00', NOW()),
    (6047, '40% engagement increase is fantastic!', 1011, NULL, NULL, NULL, 5, NULL, '2024-12-15 18:15:00', NOW()),
    (6048, 'Congratulations on the successful completion!', 1011, NULL, NULL, NULL, 5, NULL, '2024-12-18 18:00:00', NOW()),
    
    -- Comments for Project 1012
    (6049, 'The survey integration is working well. Good progress!', 1012, NULL, NULL, NULL, 6, NULL, '2024-11-05 18:00:00', NOW()),
    (6050, 'Thanks! We connected 3 survey platforms.', 1012, NULL, NULL, NULL, 33, 6049, '2024-11-05 18:30:00', NOW()),
    (6051, 'The heatmaps provide clear insights. Excellent visualization!', 1012, NULL, NULL, NULL, 6, NULL, '2024-12-20 18:00:00', NOW()),
    
    -- Comments for Project 1013
    (6052, '94% accuracy is impressive for gait recognition!', 1013, NULL, NULL, NULL, 7, NULL, '2024-11-10 18:00:00', NOW()),
    (6053, 'Thank you! We trained on a large dataset.', 1013, NULL, NULL, NULL, 35, 6052, '2024-11-10 18:30:00', NOW()),
    (6054, 'Privacy compliance is crucial. Good to see it addressed.', 1013, NULL, NULL, NULL, 7, NULL, '2024-12-25 18:00:00', NOW()),
    
    -- Comments for Project 1014
    (6055, 'The matching algorithm sounds promising. Well done!', 1014, NULL, NULL, NULL, 8, NULL, '2024-11-15 18:00:00', NOW()),
    (6056, 'Thanks! We tested it with 100 pairs.', 1014, NULL, NULL, NULL, 37, 6055, '2024-11-15 18:30:00', NOW()),
    (6057, 'The learning playlists are well-structured. Great resource!', 1014, NULL, NULL, NULL, 8, NULL, '2025-01-02 18:00:00', NOW()),
    
    -- Comments for Project 1015
    (6058, 'The wearable integration is seamless. Good work!', 1015, NULL, NULL, NULL, 9, NULL, '2024-11-18 18:00:00', NOW()),
    (6059, 'Thank you! We integrated with 3 device APIs.', 1015, NULL, NULL, NULL, 39, 6058, '2024-11-18 18:30:00', NOW()),
    (6060, 'The alert system will be very helpful for student wellness.', 1015, NULL, NULL, NULL, 9, NULL, '2024-12-30 18:00:00', NOW()),
    
    -- Comments for Project 1016
    (6061, 'The real-time collaboration is working smoothly. Excellent!', 1016, NULL, NULL, NULL, 10, NULL, '2024-11-20 18:00:00', NOW()),
    (6062, 'Thanks! WebSocket implementation was challenging but worth it.', 1016, NULL, NULL, NULL, 41, 6061, '2024-11-20 18:30:00', NOW()),
    (6063, '87% accuracy for action item detection is good. Can it be improved?', 1016, NULL, NULL, NULL, 10, NULL, '2024-12-20 18:00:00', NOW()),
    (6064, 'Yes, we''re working on improving it with more training data.', 1016, NULL, NULL, NULL, 42, 6063, '2024-12-20 18:45:00', NOW()),
    
    -- Comments for Project 1017
    (6065, 'The portfolio templates look professional. Great design!', 1017, NULL, NULL, NULL, 3, NULL, '2024-11-25 18:00:00', NOW()),
    (6066, 'Thank you! We created 10 templates.', 1017, NULL, NULL, NULL, 43, 6065, '2024-11-25 18:30:00', NOW()),
    (6067, 'The approval workflow is well-designed. Good progress!', 1017, NULL, NULL, NULL, 3, NULL, '2025-01-10 18:00:00', NOW()),
    
    -- Comments for Project 1018
    (6068, 'The barcode scanning is working perfectly. Well done!', 1018, NULL, NULL, NULL, 4, NULL, '2024-11-28 18:00:00', NOW()),
    (6069, 'Thanks! Real-time updates make inventory management much easier.', 1018, NULL, NULL, NULL, 45, 6068, '2024-11-28 18:30:00', NOW()),
    (6070, 'The expiry alerts will prevent waste. Great feature!', 1018, NULL, NULL, NULL, 4, NULL, '2025-01-15 18:00:00', NOW()),
    
    -- Comments for Project 1019
    (6071, 'The audio analysis is technically impressive. Good work!', 1019, NULL, NULL, NULL, 5, NULL, '2024-12-05 18:00:00', NOW()),
    (6072, 'Thank you! Pitch and timing analysis were the key features.', 1019, NULL, NULL, NULL, 47, 6071, '2024-12-05 18:30:00', NOW()),
    (6073, 'The feedback interface is user-friendly. Excellent UX!', 1019, NULL, NULL, NULL, 5, NULL, '2025-01-20 18:00:00', NOW()),
    
    -- Comments for Project 1020
    (6074, 'The skill assessment system is comprehensive. Well done!', 1020, NULL, NULL, NULL, 6, NULL, '2024-12-10 18:00:00', NOW()),
    (6075, 'Thanks! We wanted to make it thorough.', 1020, NULL, NULL, NULL, 49, 6074, '2024-12-10 18:30:00', NOW()),
    (6076, 'The matching algorithm results are promising. Good progress!', 1020, NULL, NULL, NULL, 6, NULL, '2025-01-25 18:00:00', NOW()),
    
    -- Comments on Milestones
    (6077, 'This milestone was completed ahead of schedule. Excellent work!', NULL, 2001, NULL, NULL, 3, NULL, '2025-02-20 17:30:00', NOW()),
    (6078, 'Thank you! The team worked hard on it.', NULL, 2001, NULL, NULL, 11, 6077, '2025-02-20 18:00:00', NOW()),
    (6079, 'The milestone deliverables are comprehensive. Well documented.', NULL, 2002, NULL, NULL, 3, NULL, '2025-03-25 18:00:00', NOW()),
    (6080, 'Good progress on this milestone. Keep it up!', NULL, 2003, NULL, NULL, 4, NULL, '2025-02-28 18:00:00', NOW()),
    (6081, 'All requirements met. Approved to proceed.', NULL, 2004, NULL, NULL, 4, NULL, '2025-03-20 18:00:00', NOW()),
    (6082, 'Excellent milestone completion!', NULL, 2012, NULL, NULL, 8, NULL, '2025-01-25 18:00:00', NOW()),
    
    -- Comments on Tasks
    (6083, 'This task is progressing well. Good job!', NULL, NULL, 3001, NULL, 3, NULL, '2025-01-20 18:00:00', NOW()),
    (6084, 'Thanks for the feedback!', NULL, NULL, 3001, NULL, 11, 6083, '2025-01-20 18:30:00', NOW()),
    (6085, 'The task is almost complete. Just need to finalize testing.', NULL, NULL, 3002, NULL, 12, NULL, '2025-03-20 18:00:00', NOW()),
    (6086, 'Great to hear! Looking forward to the final results.', NULL, NULL, 3002, NULL, 3, 6085, '2025-03-20 18:30:00', NOW()),
    (6087, 'Task completed successfully. Well done!', NULL, NULL, 3011, NULL, 8, NULL, '2024-09-26 18:00:00', NOW()),
    (6088, 'Thank you! It was a challenging task.', NULL, NULL, 3011, NULL, 21, 6087, '2024-09-26 18:30:00', NOW()),
    (6089, 'Good progress on this task. Keep going!', NULL, NULL, 3015, NULL, 10, NULL, '2024-10-05 18:00:00', NOW()),
    (6090, 'Will do! Thanks for the encouragement.', NULL, NULL, 3015, NULL, 25, 6089, '2024-10-05 18:30:00', NOW()),
    
    -- Additional comments on reports
    (6091, 'This report provides excellent insights. Well written!', NULL, NULL, NULL, 5015, 9, NULL, '2024-10-15 19:00:00', NOW()),
    (6092, 'The data collection methodology is sound.', NULL, NULL, NULL, 5015, 9, NULL, '2024-10-15 19:15:00', NOW()),
    (6093, 'Great progress report! Keep up the good work.', NULL, NULL, NULL, 5017, 10, NULL, '2024-10-20 19:00:00', NOW()),
    (6094, 'The competency mapping is thorough. Excellent work!', NULL, NULL, NULL, 5017, 10, NULL, '2024-10-20 19:15:00', NOW()),
    (6095, 'This report is very informative. Good documentation.', NULL, NULL, NULL, 5019, 3, NULL, '2024-10-25 19:00:00', NOW()),
    (6096, 'The ROS integration details are clear. Well explained.', NULL, NULL, NULL, 5019, 3, NULL, '2024-10-25 19:15:00', NOW()),
    (6097, 'Excellent report! The citation integration is impressive.', NULL, NULL, NULL, 5021, 4, NULL, '2024-10-28 19:00:00', NOW()),
    (6098, 'Good work on the documentation. Very thorough.', NULL, NULL, NULL, 5022, 4, NULL, '2024-12-10 19:00:00', NOW()),
    (6099, 'The gamification features are well-documented. Great job!', NULL, NULL, NULL, 5023, 5, NULL, '2024-12-15 19:00:00', NOW()),
    (6100, 'Congratulations on the successful completion!', NULL, NULL, NULL, 5024, 5, NULL, '2024-12-18 19:00:00', NOW()),
    (6101, 'The survey integration is working well. Good progress!', NULL, NULL, NULL, 5025, 6, NULL, '2024-11-05 19:00:00', NOW()),
    (6102, 'The visualization dashboard looks professional. Excellent work!', NULL, NULL, NULL, 5026, 6, NULL, '2024-12-20 19:00:00', NOW()),
    (6103, '94% accuracy is impressive! Great work on the model.', NULL, NULL, NULL, 5027, 7, NULL, '2024-11-10 19:00:00', NOW()),
    (6104, 'Privacy compliance is crucial. Good to see it addressed properly.', NULL, NULL, NULL, 5028, 7, NULL, '2024-12-25 19:00:00', NOW()),
    (6105, 'The matching algorithm is well-explained. Good technical writing.', NULL, NULL, NULL, 5029, 8, NULL, '2024-11-15 19:00:00', NOW()),
    (6106, 'The learning playlists are comprehensive. Great resource!', NULL, NULL, NULL, 5030, 8, NULL, '2025-01-02 19:00:00', NOW()),
    (6107, 'Excellent integration work! The wearable APIs are well-integrated.', NULL, NULL, NULL, 5031, 9, NULL, '2024-11-18 19:00:00', NOW()),
    (6108, 'The alert system will be very helpful. Good feature!', NULL, NULL, NULL, 5032, 9, NULL, '2024-12-30 19:00:00', NOW()),
    (6109, 'Real-time collaboration is working smoothly. Excellent implementation!', NULL, NULL, NULL, 5033, 10, NULL, '2024-11-20 19:00:00', NOW()),
    (6110, 'The AI transcription is impressive. Good progress!', NULL, NULL, NULL, 5034, 10, NULL, '2024-12-20 19:00:00', NOW()),
    (6111, 'The portfolio templates look professional. Great design work!', NULL, NULL, NULL, 5035, 3, NULL, '2024-11-25 19:00:00', NOW()),
    (6112, 'The approval workflow is well-designed. Good progress!', NULL, NULL, NULL, 5036, 3, NULL, '2025-01-10 19:00:00', NOW()),
    (6113, 'Barcode scanning is working perfectly. Well done!', NULL, NULL, NULL, 5037, 4, NULL, '2024-11-28 19:00:00', NOW()),
    (6114, 'The expiry alerts are a great feature. Will prevent waste.', NULL, NULL, NULL, 5038, 4, NULL, '2025-01-15 19:00:00', NOW()),
    (6115, 'The audio analysis is technically impressive. Good work!', NULL, NULL, NULL, 5039, 5, NULL, '2024-12-05 19:00:00', NOW()),
    (6116, 'The feedback interface is user-friendly. Excellent UX design!', NULL, NULL, NULL, 5040, 5, NULL, '2025-01-20 19:00:00', NOW()),
    (6117, 'The skill assessment system is comprehensive. Well done!', NULL, NULL, NULL, 5041, 6, NULL, '2024-12-10 19:00:00', NOW()),
    (6118, 'The matching algorithm results are promising. Good progress!', NULL, NULL, NULL, 5042, 6, NULL, '2025-01-25 19:00:00', NOW()),
    (6119, 'This quarterly summary is comprehensive. Well done!', NULL, NULL, NULL, 5043, 3, NULL, '2025-03-31 19:00:00', NOW()),
    (6120, 'The technical architecture review is thorough. Excellent documentation!', NULL, NULL, NULL, 5044, 4, NULL, '2025-02-28 19:00:00', NOW());