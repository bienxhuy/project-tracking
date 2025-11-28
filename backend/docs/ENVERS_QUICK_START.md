# Envers Quick Start Guide

## 🚀 Quick Start - 5 Minutes

### Step 1: Application Started ✅
Audit tables (`REVINFO`, `projects_AUD`, `tasks_AUD`, etc.) are automatically created when the application starts.

### Step 2: Make Some Changes

```bash
# Login and get token
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@example.com","password":"password123"}'

# Update a project (this will be audited!)
curl -X PUT http://localhost:9090/api/projects/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_PROGRESS"}'
```

### Step 3: View Audit History

```bash
# Get project history
curl http://localhost:9090/api/audit/projects/1/history \
  -H "Authorization: Bearer YOUR_TOKEN"

# Response:
[
  {
    "revisionNumber": 1,
    "timestamp": "2024-01-01T10:00:00",
    "username": "student@example.com",
    "ipAddress": "127.0.0.1",
    "revisionType": "INSERT",
    "entityData": {
      "id": 1,
      "title": "My Project",
      "status": "PENDING"
    }
  },
  {
    "revisionNumber": 2,
    "timestamp": "2024-01-02T14:30:00",
    "username": "student@example.com",
    "ipAddress": "127.0.0.1",
    "revisionType": "UPDATE",
    "entityData": {
      "id": 1,
      "title": "My Project",
      "status": "IN_PROGRESS"  // Changed!
    }
  }
]
```

## 📊 Common Queries

### Who changed this project's status?
```bash
curl http://localhost:9090/api/audit/projects/1/history \
  -H "Authorization: Bearer TOKEN"
```

### What was the original feedback before teacher edited it?
```bash
curl http://localhost:9090/api/audit/comments/10/history \
  -H "Authorization: Bearer TOKEN"
```

### Track all grade changes
```bash
curl http://localhost:9090/api/audit/reports/5/history \
  -H "Authorization: Bearer TOKEN"
```

### When was this task completed?
```bash
curl http://localhost:9090/api/audit/tasks/15/history \
  -H "Authorization: Bearer TOKEN"
```

### View project as it was on Jan 5, 2024
```bash
curl "http://localhost:9090/api/audit/projects/1/at-date?date=2024-01-05T10:00:00" \
  -H "Authorization: Bearer TOKEN"
```

## 🗄️ Check Database

```sql
-- View all revisions
SELECT * FROM REVINFO ORDER BY REV DESC LIMIT 10;

-- View project audit history
SELECT 
  p.id,
  p.REV,
  p.REVTYPE,
  p.title,
  p.status,
  r.username,
  r.REVTSTMP
FROM projects_AUD p
JOIN REVINFO r ON p.REV = r.REV
WHERE p.id = 1
ORDER BY p.REV;
```

## 📝 API Endpoints Summary

| Endpoint | Description |
|----------|-------------|
| `GET /api/audit/projects/{id}/history` | Full project history |
| `GET /api/audit/projects/{id}/revisions/{rev}` | Project at specific revision |
| `GET /api/audit/projects/{id}/at-date?date=...` | Project at specific date |
| `GET /api/audit/tasks/{id}/history` | Task completion history |
| `GET /api/audit/comments/{id}/history` | Feedback change history |
| `GET /api/audit/reports/{id}/history` | Grade change history |
| `GET /api/audit/users/{username}/changes` | All changes by user (admin) |
| `GET /api/audit/changes?startDate=...&endDate=...` | Changes in date range (admin) |

## 🎯 Use Case Examples

### Example 1: Track Project Status Flow

```typescript
// Frontend code
const response = await fetch(`/api/audit/projects/${projectId}/history`);
const history = await response.json();

// Show status progression
const statusFlow = history
  .filter(rev => rev.revisionType !== 'DELETE')
  .map(rev => ({
    status: rev.entityData.status,
    changedBy: rev.username,
    changedAt: rev.timestamp
  }));

console.log(statusFlow);
// Output:
// [
//   { status: 'PENDING', changedBy: 'john@example.com', changedAt: '2024-01-01T10:00:00' },
//   { status: 'IN_PROGRESS', changedBy: 'john@example.com', changedAt: '2024-01-02T14:30:00' },
//   { status: 'COMPLETED', changedBy: 'john@example.com', changedAt: '2024-01-05T16:00:00' }
// ]
```

### Example 2: Grade Change Notification

```typescript
const history = await fetch(`/api/audit/reports/${reportId}/history`);
const revisions = await history.json();

// Find grade changes
const gradeChanges = [];
for (let i = 1; i < revisions.length; i++) {
  const prev = revisions[i-1].entityData.grade;
  const curr = revisions[i].entityData.grade;
  
  if (prev !== curr) {
    gradeChanges.push({
      from: prev,
      to: curr,
      changedBy: revisions[i].username,
      changedAt: revisions[i].timestamp
    });
  }
}

console.log(gradeChanges);
// Output:
// [
//   { from: null, to: 8.5, changedBy: 'teacher1@example.com', changedAt: '2024-01-06T09:00:00' },
//   { from: 8.5, to: 9.0, changedBy: 'teacher1@example.com', changedAt: '2024-01-07T10:00:00' }
// ]
```

### Example 3: Feedback Edit History

```typescript
const history = await fetch(`/api/audit/comments/${commentId}/history`);
const revisions = await history.json();

// Show edit timeline
const editTimeline = revisions.map(rev => ({
  content: rev.entityData.content,
  editedBy: rev.username,
  editedAt: rev.timestamp,
  action: rev.revisionType
}));

console.log(editTimeline);
// Output:
// [
//   { 
//     content: 'Good work, but needs improvement',
//     editedBy: 'teacher1@example.com',
//     editedAt: '2024-01-06T09:00:00',
//     action: 'INSERT'
//   },
//   { 
//     content: 'Excellent! Well done',
//     editedBy: 'teacher1@example.com',
//     editedAt: '2024-01-07T10:00:00',
//     action: 'UPDATE'
//   }
// ]
```

## 🧪 Testing Checklist

- [ ] Create a project → Check `REVINFO` table has 1 new row
- [ ] Update project status → Check `projects_AUD` has new row with REVTYPE=1
- [ ] Call `/api/audit/projects/1/history` → See both INSERT and UPDATE revisions
- [ ] Check username field contains your email
- [ ] Check timestamp is correct
- [ ] Update grade → Verify audit trail captured the change
- [ ] Edit feedback → Verify old feedback is preserved in audit

## 🎉 You're Done!

Envers is now tracking all changes automatically. Every time you:
- Create a project → Audited
- Update project status → Audited
- Complete a task → Audited
- Edit feedback → Audited
- Change a grade → Audited

All changes are stored with:
- ✅ Username (who)
- ✅ Timestamp (when)
- ✅ IP address (from where)
- ✅ Full entity state (what)

No extra code needed - it just works! 🚀
