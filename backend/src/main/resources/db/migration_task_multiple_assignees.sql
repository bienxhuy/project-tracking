-- Migration script to change Task from single assignee to multiple assignees
-- This script should be executed manually or via your migration tool

-- Step 1: Create the junction table for task assignees
CREATE TABLE IF NOT EXISTS task_assignees (
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (task_id, user_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Step 2: Migrate existing data from assigned_to_id to task_assignees
INSERT INTO task_assignees (task_id, user_id)
SELECT id, assigned_to_id
FROM tasks
WHERE assigned_to_id IS NOT NULL;

-- Step 3: Drop the old assigned_to_id column (optional - keep for backward compatibility)
-- Uncomment the line below if you want to remove the old column completely
-- ALTER TABLE tasks DROP COLUMN assigned_to_id;

-- Note: If you keep the assigned_to_id column for backward compatibility,
-- make sure to update it when task_assignees changes, or just ignore it in the application
