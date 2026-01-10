-- Fix created_by_id for milestones and tasks
-- This script updates created_by_id from system user (1) to actual project members
-- for realistic authorship in sample data
--
-- IMPORTANT: Run this AFTER sample_users.sql and sample_tasks_reports_comments.sql

-- ==========================================
-- UPDATE MILESTONES: created_by_id = first student ID of each project
-- ==========================================
UPDATE milestones SET created_by_id = 11 WHERE project_id = 1001; -- Students: 11, 12, 13
UPDATE milestones SET created_by_id = 15 WHERE project_id = 1002; -- Students: 15, 17, 18
UPDATE milestones SET created_by_id = 22 WHERE project_id = 1003; -- Students: 22, 23, 24
UPDATE milestones SET created_by_id = 25 WHERE project_id = 1004; -- Students: 25, 26, 27
UPDATE milestones SET created_by_id = 28 WHERE project_id = 1005; -- Students: 28, 29, 30
UPDATE milestones SET created_by_id = 31 WHERE project_id = 1006; -- Students: 31, 32, 33
UPDATE milestones SET created_by_id = 34 WHERE project_id = 1007; -- Students: 34, 35, 36
UPDATE milestones SET created_by_id = 37 WHERE project_id = 1008; -- Students: 37, 38, 39
UPDATE milestones SET created_by_id = 40 WHERE project_id = 1009; -- Students: 40, 41, 42
UPDATE milestones SET created_by_id = 43 WHERE project_id = 1010; -- Students: 43, 44, 45
UPDATE milestones SET created_by_id = 46 WHERE project_id = 1011; -- Students: 46, 47, 48
UPDATE milestones SET created_by_id = 11 WHERE project_id = 1012; -- Students: 11, 14, 16
UPDATE milestones SET created_by_id = 12 WHERE project_id = 1013; -- Students: 12, 15, 19
UPDATE milestones SET created_by_id = 13 WHERE project_id = 1014; -- Students: 13, 17, 20
UPDATE milestones SET created_by_id = 18 WHERE project_id = 1015; -- Students: 18, 21, 22
UPDATE milestones SET created_by_id = 23 WHERE project_id = 1016; -- Students: 23, 24, 25
UPDATE milestones SET created_by_id = 26 WHERE project_id = 1017; -- Students: 26, 27, 28
UPDATE milestones SET created_by_id = 29 WHERE project_id = 1018; -- Students: 29, 30, 31
UPDATE milestones SET created_by_id = 32 WHERE project_id = 1019; -- Students: 32, 33, 34
UPDATE milestones SET created_by_id = 35 WHERE project_id = 1020; -- Students: 35, 36, 37
UPDATE milestones SET created_by_id = 38 WHERE project_id = 1021; -- Students: 38, 39, 40
UPDATE milestones SET created_by_id = 41 WHERE project_id = 1022; -- Students: 41, 42, 43
UPDATE milestones SET created_by_id = 44 WHERE project_id = 1023; -- Students: 44, 45, 46
UPDATE milestones SET created_by_id = 47 WHERE project_id = 1024; -- Students: 47, 48, 11
UPDATE milestones SET created_by_id = 12 WHERE project_id = 1025; -- Students: 12, 13, 15
UPDATE milestones SET created_by_id = 17 WHERE project_id = 1026; -- Students: 17, 18, 22
UPDATE milestones SET created_by_id = 23 WHERE project_id = 1027; -- Students: 23, 24, 25
UPDATE milestones SET created_by_id = 26 WHERE project_id = 1028; -- Students: 26, 27, 28
UPDATE milestones SET created_by_id = 29 WHERE project_id = 1029; -- Students: 29, 30, 31
UPDATE milestones SET created_by_id = 32 WHERE project_id = 1030; -- Students: 32, 33, 34
UPDATE milestones SET created_by_id = 35 WHERE project_id = 1031; -- Students: 35, 36, 37
UPDATE milestones SET created_by_id = 38 WHERE project_id = 1032; -- Students: 38, 39, 40
UPDATE milestones SET created_by_id = 41 WHERE project_id = 1033; -- Students: 41, 42, 43
UPDATE milestones SET created_by_id = 44 WHERE project_id = 1034; -- Students: 44, 45, 46
UPDATE milestones SET created_by_id = 47 WHERE project_id = 1035; -- Students: 47, 48, 11
UPDATE milestones SET created_by_id = 12 WHERE project_id = 1036; -- Students: 12, 13, 15
UPDATE milestones SET created_by_id = 17 WHERE project_id = 1037; -- Students: 17, 18, 22
UPDATE milestones SET created_by_id = 23 WHERE project_id = 1038; -- Students: 23, 24, 25
UPDATE milestones SET created_by_id = 26 WHERE project_id = 1039; -- Students: 26, 27, 28
UPDATE milestones SET created_by_id = 29 WHERE project_id = 1040; -- Students: 29, 30, 32

-- ==========================================
-- UPDATE TASKS: created_by_id = first student ID of each project
-- ==========================================
UPDATE tasks SET created_by_id = 11 WHERE project_id = 1001; -- Students: 11, 12, 13
UPDATE tasks SET created_by_id = 15 WHERE project_id = 1002; -- Students: 15, 17, 18
UPDATE tasks SET created_by_id = 22 WHERE project_id = 1003; -- Students: 22, 23, 24
UPDATE tasks SET created_by_id = 25 WHERE project_id = 1004; -- Students: 25, 26, 27
UPDATE tasks SET created_by_id = 28 WHERE project_id = 1005; -- Students: 28, 29, 30
UPDATE tasks SET created_by_id = 31 WHERE project_id = 1006; -- Students: 31, 32, 33
UPDATE tasks SET created_by_id = 34 WHERE project_id = 1007; -- Students: 34, 35, 36
UPDATE tasks SET created_by_id = 37 WHERE project_id = 1008; -- Students: 37, 38, 39
UPDATE tasks SET created_by_id = 40 WHERE project_id = 1009; -- Students: 40, 41, 42
UPDATE tasks SET created_by_id = 43 WHERE project_id = 1010; -- Students: 43, 44, 45
UPDATE tasks SET created_by_id = 46 WHERE project_id = 1011; -- Students: 46, 47, 48
UPDATE tasks SET created_by_id = 11 WHERE project_id = 1012; -- Students: 11, 14, 16
UPDATE tasks SET created_by_id = 12 WHERE project_id = 1013; -- Students: 12, 15, 19
UPDATE tasks SET created_by_id = 13 WHERE project_id = 1014; -- Students: 13, 17, 20
UPDATE tasks SET created_by_id = 18 WHERE project_id = 1015; -- Students: 18, 21, 22
UPDATE tasks SET created_by_id = 23 WHERE project_id = 1016; -- Students: 23, 24, 25
UPDATE tasks SET created_by_id = 26 WHERE project_id = 1017; -- Students: 26, 27, 28
UPDATE tasks SET created_by_id = 29 WHERE project_id = 1018; -- Students: 29, 30, 31
UPDATE tasks SET created_by_id = 32 WHERE project_id = 1019; -- Students: 32, 33, 34
UPDATE tasks SET created_by_id = 35 WHERE project_id = 1020; -- Students: 35, 36, 37
UPDATE tasks SET created_by_id = 38 WHERE project_id = 1021; -- Students: 38, 39, 40
UPDATE tasks SET created_by_id = 41 WHERE project_id = 1022; -- Students: 41, 42, 43
UPDATE tasks SET created_by_id = 44 WHERE project_id = 1023; -- Students: 44, 45, 46
UPDATE tasks SET created_by_id = 47 WHERE project_id = 1024; -- Students: 47, 48, 11
UPDATE tasks SET created_by_id = 12 WHERE project_id = 1025; -- Students: 12, 13, 15
UPDATE tasks SET created_by_id = 17 WHERE project_id = 1026; -- Students: 17, 18, 22
UPDATE tasks SET created_by_id = 23 WHERE project_id = 1027; -- Students: 23, 24, 25
UPDATE tasks SET created_by_id = 26 WHERE project_id = 1028; -- Students: 26, 27, 28
UPDATE tasks SET created_by_id = 29 WHERE project_id = 1029; -- Students: 29, 30, 31
UPDATE tasks SET created_by_id = 32 WHERE project_id = 1030; -- Students: 32, 33, 34
UPDATE tasks SET created_by_id = 35 WHERE project_id = 1031; -- Students: 35, 36, 37
UPDATE tasks SET created_by_id = 38 WHERE project_id = 1032; -- Students: 38, 39, 40
UPDATE tasks SET created_by_id = 41 WHERE project_id = 1033; -- Students: 41, 42, 43
UPDATE tasks SET created_by_id = 44 WHERE project_id = 1034; -- Students: 44, 45, 46
UPDATE tasks SET created_by_id = 47 WHERE project_id = 1035; -- Students: 47, 48, 11
UPDATE tasks SET created_by_id = 12 WHERE project_id = 1036; -- Students: 12, 13, 15
UPDATE tasks SET created_by_id = 17 WHERE project_id = 1037; -- Students: 17, 18, 22
UPDATE tasks SET created_by_id = 23 WHERE project_id = 1038; -- Students: 23, 24, 25
UPDATE tasks SET created_by_id = 26 WHERE project_id = 1039; -- Students: 26, 27, 28
UPDATE tasks SET created_by_id = 29 WHERE project_id = 1040; -- Students: 29, 30, 32
