# Chore Tracker - Quick Start Guide

## Quick API Reference

### Mark a Chore Complete (NOW)
```bash
POST /chores/{choreId}/complete
```
Instantly marks a chore as complete with current timestamp.

**Quick Example:**
```bash
curl -X POST "http://localhost:8080/chores/5/complete"
```

### Mark a Chore Complete (Specific Time)
```bash
POST /chores/{choreId}/complete-at?completedAt={ISO_DATETIME}&notes={optional_notes}
```
Record a chore completion at a specific past or future time.

**Quick Example:**
```bash
curl -X POST "http://localhost:8080/chores/5/complete-at?completedAt=2025-12-20T14:30:00&notes=Did%20it%20yesterday"
```

### View Completion History
```bash
GET /chores/{choreId}/completions
```
See all times a chore was completed.

**Quick Example:**
```bash
curl "http://localhost:8080/chores/5/completions"
```

### View Last Completion
```bash
GET /chores/{choreId}/completions/latest
```
Find out the last time a chore was completed.

**Quick Example:**
```bash
curl "http://localhost:8080/chores/5/completions/latest"
```

### View Completions This Week
```bash
GET /chores/{choreId}/completions/range?startDate=START&endDate=END
```

**Quick Example:**
```bash
# For this week (Dec 14-20, 2025):
curl "http://localhost:8080/chores/5/completions/range?startDate=2025-12-14T00:00:00&endDate=2025-12-20T23:59:59"
```

### View All Category Completions
```bash
GET /chores/category/{categoryId}/completions
```
See all completions for all chores in a category.

**Quick Example:**
```bash
curl "http://localhost:8080/chores/category/2/completions"
```

---

## Common Scenarios

### Scenario 1: Just finished a chore
```bash
# Quick completion with notes
curl -X POST "http://localhost:8080/chores/1/complete?notes=Finished%20vacuuming"
```

### Scenario 2: You completed a chore but forgot to log it
```bash
# Log completion from an hour ago
curl -X POST "http://localhost:8080/chores/1/complete-at?completedAt=2025-12-20T13:30:00&notes=Did%20it%20earlier"
```

### Scenario 3: Check if you've done a chore this month
```bash
curl "http://localhost:8080/chores/1/completions/range?startDate=2025-12-01T00:00:00&endDate=2025-12-31T23:59:59"
```

### Scenario 4: How long until the chore is due again?
```bash
# First, get the chore info to see frequency
curl "http://localhost:8080/chores/1"

# Then check the last completion
curl "http://localhost:8080/chores/1/completions/latest"

# Calculate next due date based on frequency + last completion
```

### Scenario 5: Generate a completion report for the family
```bash
# Get all completions for bathroom chores this week
curl "http://localhost:8080/chores/category/3/completions/range?startDate=2025-12-14T00:00:00&endDate=2025-12-20T23:59:59"
```

### Scenario 6: Oops, logged the wrong time
```bash
# Update the completion record
curl -X PUT "http://localhost:8080/chores/completions/5" \
  -H "Content-Type: application/json" \
  -d '{"completedAt":"2025-12-20T15:00:00"}'
```

### Scenario 7: Delete an accidental completion
```bash
curl -X DELETE "http://localhost:8080/chores/completions/5"
```

---

## Datetime Format

Always use ISO 8601 format for timestamps:
- **Format**: `YYYY-MM-DDTHH:mm:ss`
- **Example**: `2025-12-20T14:30:00`
- **Timezone**: No timezone (stored as local time)

### Date Examples
- Today at 2:30 PM: `2025-12-20T14:30:00`
- Tomorrow at midnight: `2025-12-21T00:00:00`
- Last Monday at 10 AM: `2025-12-15T10:00:00`
- Start of month: `2025-12-01T00:00:00`
- End of month: `2025-12-31T23:59:59`

---

## Response Examples

### Success (201 Created)
```json
{
  "id": 42,
  "choreId": 5,
  "completedAt": "2025-12-20T14:30:00",
  "notes": "Finished vacuuming the living room"
}
```

### List Response (200 OK)
```json
[
  {
    "id": 42,
    "choreId": 5,
    "completedAt": "2025-12-20T14:30:00",
    "notes": "Finished vacuuming"
  },
  {
    "id": 41,
    "choreId": 5,
    "completedAt": "2025-12-13T15:00:00",
    "notes": null
  }
]
```

### Error (404 Not Found)
```json
{
  "error": "Chore not found with id: 999"
}
```

---

## Tips & Tricks

✨ **Tip 1**: Always include notes explaining why it took longer than expected - helpful for adjusting chore frequency!

✨ **Tip 2**: Use the latest endpoint to check if a chore is overdue:
```bash
curl "http://localhost:8080/chores/1/completions/latest"
# Compare the completedAt with today's date
```

✨ **Tip 3**: Query a date range for reporting:
```bash
# Weekly report
curl "http://localhost:8080/chores/1/completions/range?startDate=2025-12-14T00:00:00&endDate=2025-12-20T23:59:59"

# Monthly report
curl "http://localhost:8080/chores/category/1/completions/range?startDate=2025-12-01T00:00:00&endDate=2025-12-31T23:59:59"
```

✨ **Tip 4**: The category endpoint helps you see all activity in one area (kitchen, bathroom, etc.)

✨ **Tip 5**: Optional notes are great for:
- "Spilled something, cleaned extra"
- "Quick cleanup, 5 minutes"
- "Did thorough deep clean"
- "Someone else did this"
- "Postponed, will do tomorrow"

---

## Integration Ideas

1. **Mobile App**: Create an app that calls these endpoints
2. **Alexa/Google Home**: Add voice support ("Alexa, I just finished vacuuming")
3. **Daily Digest**: Send email summaries of completions
4. **Smart Reminders**: Check latest completion and remind if overdue
5. **Family Dashboard**: Display completion timeline for shared chores
6. **Notifications**: Alert family members when chores are done
7. **Analytics**: Track completion consistency and patterns over months

