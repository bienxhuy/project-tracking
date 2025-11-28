# Hibernate Envers - Audit Trail Documentation

## 📖 Documentation Index

### Quick Start
- **[ENVERS_QUICK_START.md](./ENVERS_QUICK_START.md)** - 5-minute quick start guide
  - How to test audit functionality immediately
  - Common API calls
  - Example queries

### Complete Guide
- **[ENVERS_AUDIT_GUIDE.md](./ENVERS_AUDIT_GUIDE.md)** - Complete implementation guide
  - How it works
  - Database schema
  - All API endpoints
  - Use cases
  - Performance considerations
  - Security

## 🎯 What Is Audited?

| Entity | What's Tracked | Use Case |
|--------|---------------|----------|
| **Project** | Status changes, grade changes, edits | "Who changed project status to IN_PROGRESS?" |
| **Task** | Completion, assignment changes | "When was this task completed?" |
| **Comment** | Feedback edits, deletions | "What was the original feedback?" |
| **Report** | Grade changes, content edits | "How many times did teacher change the grade?" |

## 🚀 Quick Examples

### Track Project Status Changes
```bash
curl http://localhost:9090/api/audit/projects/1/history \
  -H "Authorization: Bearer TOKEN"
```

### Track Grade Changes
```bash
curl http://localhost:9090/api/audit/reports/5/history \
  -H "Authorization: Bearer TOKEN"
```

### View Project State at Specific Date
```bash
curl "http://localhost:9090/api/audit/projects/1/at-date?date=2024-01-15T10:00:00" \
  -H "Authorization: Bearer TOKEN"
```

## 🗄️ Database Tables

Auto-created by Envers:
- `REVINFO` - Revision metadata (who, when, from where)
- `projects_AUD` - Project audit history
- `tasks_AUD` - Task audit history
- `comments_AUD` - Comment audit history
- `reports_AUD` - Report audit history

## 📊 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/audit/projects/{id}/history` | Get all project changes |
| GET | `/api/audit/projects/{id}/revisions/{rev}` | Get project at revision X |
| GET | `/api/audit/projects/{id}/at-date?date=...` | Get project at specific date |
| GET | `/api/audit/tasks/{id}/history` | Get task completion history |
| GET | `/api/audit/comments/{id}/history` | Get feedback change history |
| GET | `/api/audit/reports/{id}/history` | Get grade change history |
| GET | `/api/audit/users/{username}/changes` | Get all changes by user (admin) |
| GET | `/api/audit/changes?startDate=...&endDate=...` | Get changes in date range (admin) |

## 🔍 Use Cases

### 1. Compliance & Accreditation
Track all changes for audit reports required by educational accreditation bodies.

### 2. Accountability
Know exactly who made what changes and when. No more disputes about "who changed this?"

### 3. Transparency
Students and teachers can see the full history of project evaluations and feedback.

### 4. Data Recovery
View project state at any point in the past. Rollback if needed.

### 5. Analytics
Analyze patterns: How long do projects stay in each status? How often do teachers revise grades?

## ⚡ Performance Notes

- **Storage:** Each update creates one audit record
- **Queries:** Audit queries are slightly slower (JOIN with REVINFO table)
- **Write Performance:** ~10-20% overhead (acceptable for most cases)
- **Optimization:** Index on `(id, REV)`, archive old revisions

## 🔒 Security

- Regular users: Can view history of their own entities
- Instructors: Can view history of their students' work
- Admins: Full access to all audit trails

## 🧪 Testing

1. Start application
2. Make changes to projects/tasks
3. Call audit endpoints
4. Check database tables

See [ENVERS_QUICK_START.md](./ENVERS_QUICK_START.md) for detailed testing steps.

## 📝 Implementation Files

### Backend
- `entity/audit/CustomRevisionEntity.java` - Stores revision metadata
- `entity/audit/CustomRevisionListener.java` - Captures username & IP
- `service/AuditService.java` - Query audit history
- `controller/AuditController.java` - REST endpoints
- `dto/AuditRevisionDTO.java` - Response DTO

### Audited Entities
- `entity/Project.java` - @Audited
- `entity/Task.java` - @Audited
- `entity/Comment.java` - @Audited
- `entity/Report.java` - @Audited

## 🎓 Learn More

- [Hibernate Envers Documentation](https://hibernate.org/orm/envers/)
- [Spring Data Envers Reference](https://docs.spring.io/spring-data/envers/docs/current/reference/html/)

## 💡 Tips

1. **Don't audit everything** - Only audit entities that need history tracking
2. **Use @NotAudited** - Exclude collections to reduce storage
3. **Archive old data** - Set up periodic cleanup of old audit records
4. **Index properly** - Add indexes on `(id, REV)` for better query performance
5. **Secure endpoints** - Use proper authorization on audit APIs

---

**Envers = Time Machine for Your Database** ⏰

Track every change, know who did what, restore old states. All automatic! 🎉
