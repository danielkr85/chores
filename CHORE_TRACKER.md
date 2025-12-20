# Chore Tracker - Completion Timestamp Feature

## Overview

The Chore Tracker system now captures and tracks when each chore is completed with full timestamp support. This allows you to maintain a complete history of chore completions for analysis, reporting, and tracking habits over time.

## New Components

### Models

#### ChoreCompletion
- **Entity**: `com.robertson.chores.models.ChoreCompletion`
- **Purpose**: Represents a single completion event of a chore
- **Fields**:
  - `id`: Unique identifier for the completion record
  - `chore`: Reference to the associated Chore
  - `completedAt`: Timestamp of when the chore was completed (LocalDateTime)
  - `notes`: Optional notes about the completion (max 500 characters)

### Data Transfer Objects (DTOs)

#### ChoreCompletionDTO
- **Class**: `com.robertson.chores.dto.ChoreCompletionDTO`
- **Purpose**: Transfer completion data via REST API
- **Fields**:
  - `id`: Completion record ID
  - `choreId`: ID of the completed chore
  - `completedAt`: Timestamp of completion
  - `notes`: Optional completion notes

### Repositories

#### ChoreCompletionRepository
- **Class**: `com.robertson.chores.repositories.ChoreCompletionRepository`
- **Custom Queries**:
  - `findByChore_IdOrderByCompletedAtDesc()`: Get all completions for a chore, sorted by date descending
  - `findByChore_IdAndCompletedAtBetweenOrderByCompletedAtDesc()`: Get completions within a date range
  - `findByCategoryIdOrderByCompletedAtDesc()`: Get all completions for all chores in a category
  - `findByCategoryIdAndDateRange()`: Get category completions within a date range

### Services

#### ChoreCompletionService
- **Class**: `com.robertson.chores.services.ChoreCompletionService`
- **Key Methods**:
  - `markChoreComplete(choreId, notes)`: Mark a chore complete with current timestamp
  - `markChoreCompleteAt(choreId, completedAt, notes)`: Mark a chore complete with specific timestamp
  - `getChoreCompletions(choreId)`: Get all completions for a chore
  - `getChoreCompletionsByDateRange(choreId, startDate, endDate)`: Get completions in a date range
  - `getCategoryCompletions(categoryId)`: Get all completions for a category
  - `getCategoryCompletionsByDateRange(categoryId, startDate, endDate)`: Get category completions in a date range
  - `getLatestCompletion(choreId)`: Get the most recent completion for a chore
  - `updateCompletion(completionId, dto)`: Update a completion record
  - `deleteCompletion(completionId)`: Delete a completion record

### Controllers

#### ChoreCompletionController
- **Class**: `com.robertson.chores.controllers.ChoreCompletionController`
- **Base Path**: `/chores`

## REST API Endpoints

### Mark Chore as Complete

#### POST `/chores/{choreId}/complete`
Marks a chore as complete with the current server timestamp.

**Parameters:**
- `choreId` (path): ID of the chore to complete
- `notes` (query, optional): Optional notes about the completion

**Example:**
```
POST /chores/1/complete?notes=Finished%20the%20laundry
```

**Response (201 Created):**
```json
{
  "id": 1,
  "choreId": 1,
  "completedAt": "2025-12-20T14:30:00",
  "notes": "Finished the laundry"
}
```

#### POST `/chores/{choreId}/complete-at`
Marks a chore as complete with a specific timestamp (useful for logging past completions).

**Parameters:**
- `choreId` (path): ID of the chore to complete
- `completedAt` (query): ISO 8601 timestamp of completion (e.g., `2025-12-20T14:30:00`)
- `notes` (query, optional): Optional notes about the completion

**Example:**
```
POST /chores/1/complete-at?completedAt=2025-12-20T10:00:00&notes=Finished%20earlier
```

**Response (201 Created):**
```json
{
  "id": 2,
  "choreId": 1,
  "completedAt": "2025-12-20T10:00:00",
  "notes": "Finished earlier"
}
```

### Retrieve Completion History

#### GET `/chores/{choreId}/completions`
Retrieves all completions for a specific chore, ordered by most recent first.

**Example:**
```
GET /chores/1/completions
```

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "choreId": 1,
    "completedAt": "2025-12-20T14:30:00",
    "notes": "Finished the laundry"
  },
  {
    "id": 1,
    "choreId": 1,
    "completedAt": "2025-12-20T10:00:00",
    "notes": "Finished earlier"
  }
]
```

#### GET `/chores/{choreId}/completions/range`
Retrieves completions for a chore within a specific date range.

**Parameters:**
- `choreId` (path): ID of the chore
- `startDate` (query): ISO 8601 start date (e.g., `2025-12-01T00:00:00`)
- `endDate` (query): ISO 8601 end date (e.g., `2025-12-31T23:59:59`)

**Example:**
```
GET /chores/1/completions/range?startDate=2025-12-01T00:00:00&endDate=2025-12-31T23:59:59
```

#### GET `/chores/{choreId}/completions/latest`
Retrieves the most recent completion for a chore.

**Example:**
```
GET /chores/1/completions/latest
```

**Response (200 OK):**
```json
{
  "id": 2,
  "choreId": 1,
  "completedAt": "2025-12-20T14:30:00",
  "notes": "Finished the laundry"
}
```

#### GET `/chores/completions/{completionId}`
Retrieves a specific completion record by ID.

**Example:**
```
GET /chores/completions/1
```

#### GET `/chores/category/{categoryId}/completions`
Retrieves all completions for all chores in a category, ordered by most recent first.

**Example:**
```
GET /chores/category/1/completions
```

#### GET `/chores/category/{categoryId}/completions/range`
Retrieves completions for a category within a specific date range.

**Parameters:**
- `categoryId` (path): ID of the category
- `startDate` (query): ISO 8601 start date
- `endDate` (query): ISO 8601 end date

**Example:**
```
GET /chores/category/1/completions/range?startDate=2025-12-01T00:00:00&endDate=2025-12-31T23:59:59
```

### Update Completion

#### PUT `/chores/completions/{completionId}`
Updates a completion record (timestamp and/or notes).

**Example:**
```
PUT /chores/completions/1
```

**Request Body:**
```json
{
  "completedAt": "2025-12-20T15:00:00",
  "notes": "Updated notes"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "choreId": 1,
  "completedAt": "2025-12-20T15:00:00",
  "notes": "Updated notes"
}
```

### Delete Completion

#### DELETE `/chores/completions/{completionId}`
Deletes a completion record.

**Example:**
```
DELETE /chores/completions/1
```

**Response (204 No Content)**

## Usage Examples

### Complete a chore right now
```bash
curl -X POST "http://localhost:8080/chores/1/complete?notes=Completed%20quickly"
```

### Complete a chore with a specific time
```bash
curl -X POST "http://localhost:8080/chores/1/complete-at?completedAt=2025-12-20T10:30:00"
```

### Get all completions for a chore
```bash
curl http://localhost:8080/chores/1/completions
```

### Get completions from this week
```bash
curl "http://localhost:8080/chores/1/completions/range?startDate=2025-12-14T00:00:00&endDate=2025-12-20T23:59:59"
```

### Get the last time a chore was completed
```bash
curl http://localhost:8080/chores/1/completions/latest
```

### Get all completions in a category
```bash
curl http://localhost:8080/chores/category/2/completions
```

## Database Schema

The system uses JPA/Hibernate to manage the database. A new table `chore_completion` is automatically created with the following structure:

```sql
CREATE TABLE chore_completion (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  chore_id BIGINT NOT NULL,
  completed_at DATETIME NOT NULL,
  notes VARCHAR(500),
  FOREIGN KEY (chore_id) REFERENCES chore(id) ON DELETE CASCADE,
  INDEX idx_chore_completion_timestamp (chore_id, completed_at)
);
```

## Features

✅ **Timestamp Tracking**: Automatically captures when each chore is completed
✅ **History**: Complete audit trail of all chore completions
✅ **Date Range Queries**: Filter completions by date range
✅ **Category-Level Reporting**: View all completions for a category
✅ **Optional Notes**: Add context to each completion (why it took longer, any issues, etc.)
✅ **Update Capability**: Correct timestamps or notes if needed
✅ **Delete Support**: Remove incorrect completion records
✅ **Lazy Loading**: Optimized queries to prevent N+1 problems
✅ **RESTful API**: Full REST support for all operations

## Benefits

1. **Habit Tracking**: Monitor how frequently you complete chores
2. **Performance Analysis**: Identify patterns in when you complete tasks
3. **Progress Reports**: Generate reports on your chore completion over time
4. **Accountability**: Keep a record of task completion for shared responsibilities
5. **Data-Driven Insights**: Understand your cleaning patterns and adjust frequency accordingly

## Notes

- All timestamps are stored as `LocalDateTime` (no timezone included)
- The `completedAt` field is always required when creating a completion record
- The `notes` field is optional and can be null
- Cascade delete is enabled, so deleting a chore automatically deletes all its completion records
- Indexes are created on `chore_id` and `completed_at` for optimal query performance

