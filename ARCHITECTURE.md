# Chore Tracker - Architecture & Component Overview

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                             │
│              (Web Browser, Mobile App, API Client)               │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                   HTTP Requests/Responses
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                    CONTROLLER LAYER                              │
│  ChoreCompletionController (REST API Endpoints)                  │
│                                                                   │
│  ├─ POST   /chores/{choreId}/complete                            │
│  ├─ POST   /chores/{choreId}/complete-at                         │
│  ├─ GET    /chores/{choreId}/completions                         │
│  ├─ GET    /chores/{choreId}/completions/range                   │
│  ├─ GET    /chores/{choreId}/completions/latest                  │
│  ├─ GET    /chores/completions/{completionId}                    │
│  ├─ PUT    /chores/completions/{completionId}                    │
│  ├─ DELETE /chores/completions/{completionId}                    │
│  ├─ GET    /chores/category/{categoryId}/completions             │
│  └─ GET    /chores/category/{categoryId}/completions/range       │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                      Spring Dependency Injection
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                    SERVICE LAYER                                 │
│    ChoreCompletionService (Business Logic)                       │
│                                                                   │
│  ├─ markChoreComplete()                                           │
│  ├─ markChoreCompleteAt()                                         │
│  ├─ getChoreCompletions()                                         │
│  ├─ getChoreCompletionsByDateRange()                              │
│  ├─ getCategoryCompletions()                                      │
│  ├─ getCategoryCompletionsByDateRange()                           │
│  ├─ getLatestCompletion()                                         │
│  ├─ updateCompletion()                                            │
│  ├─ deleteCompletion()                                            │
│  ├─ getCompletionById()                                           │
│  └─ toDTO() (Mapping)                                             │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                      Repository Injection
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                   REPOSITORY LAYER                               │
│    ChoreCompletionRepository (Data Access)                       │
│    ChoreRepository (Existing)                                    │
│                                                                   │
│  ├─ save()                                                        │
│  ├─ findById()                                                    │
│  ├─ delete()                                                      │
│  ├─ findByChore_IdOrderByCompletedAtDesc()                        │
│  ├─ findByChore_IdAndCompletedAtBetweenOrderByCompletedAtDesc()   │
│  ├─ findByCategoryIdOrderByCompletedAtDesc()                      │
│  └─ findByCategoryIdAndDateRange()                                │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                      JPA/Hibernate
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                   PERSISTENCE LAYER                              │
│          JPA/Hibernate ORM Framework                             │
│                                                                   │
│  ├─ Entity Mapping                                                │
│  ├─ SQL Query Generation                                          │
│  ├─ Transaction Management                                        │
│  └─ Connection Pooling                                            │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                        SQL Commands
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                   DATABASE LAYER                                 │
│          (H2, MySQL, PostgreSQL, etc.)                           │
│                                                                   │
│  ┌──────────────────────────────────────────────────┐             │
│  │ chore_completion Table                           │             │
│  ├──────────────────────────────────────────────────┤             │
│  │ PK: id (BIGINT)                                  │             │
│  │ FK: chore_id (BIGINT → chore.id)                 │             │
│  │ ──: completed_at (DATETIME)                      │             │
│  │ ──: notes (VARCHAR(500))                         │             │
│  │ IX: idx_chore_completion_timestamp               │             │
│  │     (chore_id, completed_at)                     │             │
│  └──────────────────────────────────────────────────┘             │
│                                                                   │
│  ┌──────────────────────────────────────────────────┐             │
│  │ chore Table (Existing, now with relationship)    │             │
│  ├──────────────────────────────────────────────────┤             │
│  │ PK: id (BIGINT)                                  │             │
│  │ ──: name (VARCHAR)                               │             │
│  │ FK: category_id (BIGINT → category.id)           │             │
│  │ ──: frequency (Embedded)                         │             │
│  │ 1→N: chore_completion (cascade delete)           │             │
│  └──────────────────────────────────────────────────┘             │
└──────────────────────────────────────────────────────────────────┘
```

---

## Data Model Diagram

```
┌─────────────────────┐          ┌──────────────────────────┐
│      Category       │          │        Chore             │
├─────────────────────┤          ├──────────────────────────┤
│ id (PK)             │◄─────────│ id (PK)                  │
│ name (UNIQUE)       │  1    N  │ name                     │
│                     │          │ category_id (FK)         │
└─────────────────────┘          │ frequency (Embedded)     │
                                 │                          │
                                 └──────────────┬───────────┘
                                                │
                                                │ 1
                                                │
                                    ┌───────────▼──────────────┐
                                    │  ChoreCompletion         │
                                    ├────────────────────────┬─┤
                                    │ id (PK)                │ │
                                    │ chore_id (FK)          │ │
                                    │ completed_at           │ │
                                    │ notes (optional)       │ │
                                    │                        │ │
                                    │ Index:                 │ │
                                    │  (chore_id,            │ │
                                    │   completed_at)        │ │
                                    └────────────────────────┴─┘
                                            N
                                            │
                                            └─ Cascade Delete
```

---

## Component Interaction Flow

### Scenario: Mark a Chore Complete

```
Client Application
       │
       │ HTTP POST /chores/5/complete?notes=Done
       ▼
ChoreCompletionController.markChoreComplete()
       │
       │ Validates request
       │ Calls service
       ▼
ChoreCompletionService.markChoreComplete()
       │
       ├─ Verifies chore exists
       │
       ├─ Creates ChoreCompletion object
       │   ├─ Sets chore reference
       │   ├─ Sets completedAt = now()
       │   └─ Sets notes
       │
       ├─ Persists via repository
       ▼
ChoreCompletionRepository.save()
       │
       │ Converts to JPA entity
       ▼
Hibernate/JPA
       │
       │ Generates INSERT SQL
       ▼
Database
       │
       └─ Inserts into chore_completion table
       │
       └─ Returns generated ID
       │
       ▼ Response flows back up
ChoreCompletionDTO
       │
       │ JSON (HTTP 201 Created)
       ▼
Client Application
```

---

## Request/Response Examples

### Create Completion
```
Request:
  POST /chores/5/complete?notes=Vacuumed%20living%20room
  
Response (201 Created):
{
  "id": 42,
  "choreId": 5,
  "completedAt": "2025-12-20T14:30:00",
  "notes": "Vacuumed living room"
}
```

### Get Completions History
```
Request:
  GET /chores/5/completions
  
Response (200 OK):
[
  {
    "id": 42,
    "choreId": 5,
    "completedAt": "2025-12-20T14:30:00",
    "notes": "Vacuumed living room"
  },
  {
    "id": 41,
    "choreId": 5,
    "completedAt": "2025-12-13T15:00:00",
    "notes": null
  },
  {
    "id": 40,
    "choreId": 5,
    "completedAt": "2025-12-06T14:00:00",
    "notes": "Quick cleanup"
  }
]
```

### Get Latest Completion
```
Request:
  GET /chores/5/completions/latest
  
Response (200 OK):
{
  "id": 42,
  "choreId": 5,
  "completedAt": "2025-12-20T14:30:00",
  "notes": "Vacuumed living room"
}
```

### Get Completions by Date Range
```
Request:
  GET /chores/5/completions/range
      ?startDate=2025-12-01T00:00:00
      &endDate=2025-12-31T23:59:59
  
Response (200 OK):
[
  {
    "id": 42,
    "choreId": 5,
    "completedAt": "2025-12-20T14:30:00",
    "notes": "Vacuumed living room"
  },
  {
    "id": 41,
    "choreId": 5,
    "completedAt": "2025-12-13T15:00:00",
    "notes": null
  },
  {
    "id": 40,
    "choreId": 5,
    "completedAt": "2025-12-06T14:00:00",
    "notes": "Quick cleanup"
  }
]
```

---

## Class Diagrams

### ChoreCompletion Entity

```
┌──────────────────────────────────────┐
│       ChoreCompletion               │
├──────────────────────────────────────┤
│ - id: Long                           │
│ - chore: Chore                       │
│ - completedAt: LocalDateTime         │
│ - notes: String                      │
├──────────────────────────────────────┤
│ + getId(): Long                      │
│ + getChore(): Chore                  │
│ + getCompletedAt(): LocalDateTime    │
│ + getNotes(): String                 │
│ + setId(Long)                        │
│ + setChore(Chore)                    │
│ + setCompletedAt(LocalDateTime)      │
│ + setNotes(String)                   │
└──────────────────────────────────────┘
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {@Index(...)})
```

### ChoreCompletionDTO

```
┌──────────────────────────────────────┐
│    ChoreCompletionDTO               │
├──────────────────────────────────────┤
│ - id: Long                           │
│ - choreId: Long                      │
│ - completedAt: LocalDateTime         │
│ - notes: String                      │
├──────────────────────────────────────┤
│ + getId(): Long                      │
│ + getChoreId(): Long                 │
│ + getCompletedAt(): LocalDateTime    │
│ + getNotes(): String                 │
│ + setId(Long)                        │
│ + setChoreId(Long)                   │
│ + setCompletedAt(LocalDateTime)      │
│ + setNotes(String)                   │
└──────────────────────────────────────┘
@Data
```

### ChoreCompletionService

```
┌─────────────────────────────────────────┐
│  ChoreCompletionService                │
├─────────────────────────────────────────┤
│ - choreCompletionRepository             │
│ - choreRepository                       │
├─────────────────────────────────────────┤
│ + markChoreComplete(Long, String)       │
│ + markChoreCompleteAt(Long, LocalDT, S) │
│ + getChoreCompletions(Long)             │
│ + getChoreCompletionsByDateRange(...)   │
│ + getCategoryCompletions(Long)          │
│ + getCategoryCompletionsByDateRange(...) │
│ + getLatestCompletion(Long)             │
│ + getCompletionById(Long)               │
│ + updateCompletion(Long, DTO)           │
│ + deleteCompletion(Long)                │
│ - toDTO(ChoreCompletion)                │
└─────────────────────────────────────────┘
@Service
@RequiredArgsConstructor
```

---

## Database Query Examples

### Find all completions for a chore
```sql
SELECT * FROM chore_completion
WHERE chore_id = 5
ORDER BY completed_at DESC
```

### Find completions in date range
```sql
SELECT * FROM chore_completion
WHERE chore_id = 5
  AND completed_at BETWEEN '2025-12-01' AND '2025-12-31'
ORDER BY completed_at DESC
```

### Find latest completion
```sql
SELECT * FROM chore_completion
WHERE chore_id = 5
ORDER BY completed_at DESC
LIMIT 1
```

### Find all category completions
```sql
SELECT cc.* FROM chore_completion cc
JOIN chore c ON cc.chore_id = c.id
WHERE c.category_id = 2
ORDER BY cc.completed_at DESC
```

### Index usage
```sql
-- The index idx_chore_completion_timestamp
-- optimizes these queries:
SELECT * FROM chore_completion
WHERE chore_id = 5 AND completed_at BETWEEN ? AND ?
ORDER BY completed_at DESC
```

---

## File Organization

```
src/main/java/com/robertson/chores/
├── models/
│   ├── ChoreCompletion.java          (NEW)
│   ├── Chore.java                    (UPDATED)
│   ├── Category.java                 (EXISTING)
│   ├── Frequency.java                (EXISTING)
│   ├── FrequencyType.java            (EXISTING)
│   └── ChoreDay.java                 (EXISTING)
│
├── dto/
│   ├── ChoreCompletionDTO.java       (NEW)
│   ├── ChoreDTO.java                 (EXISTING)
│   ├── CategoryDTO.java              (EXISTING)
│   └── FrequencyDTO.java             (EXISTING)
│
├── repositories/
│   ├── ChoreCompletionRepository.java (NEW)
│   ├── ChoreRepository.java          (EXISTING)
│   └── CategoryRepository.java       (EXISTING)
│
├── services/
│   ├── ChoreCompletionService.java   (NEW)
│   └── ChoreService.java             (EXISTING)
│
└── controllers/
    ├── ChoreCompletionController.java (NEW)
    ├── ChoreController.java          (EXISTING)
    └── CategoryController.java       (EXISTING)
```

---

## Technology Stack

```
┌─────────────────────────────────────┐
│      Spring Boot 3.5.7              │
│  ├─ Spring Web (REST API)           │
│  ├─ Spring Data JPA                 │
│  └─ Spring Boot Starter Validation  │
├─────────────────────────────────────┤
│    Jakarta Persistence (JPA)        │
│  └─ Hibernate ORM                   │
├─────────────────────────────────────┤
│         Lombok                      │
│  └─ Reduces Boilerplate             │
├─────────────────────────────────────┤
│    Maven 3.6+                       │
│  └─ Build Management                │
├─────────────────────────────────────┤
│    H2 Database (dev)                │
│    MySQL/PostgreSQL (production)    │
└─────────────────────────────────────┘
```

---

## Performance Considerations

### Database Indexes
```
CREATE INDEX idx_chore_completion_timestamp
ON chore_completion(chore_id, completed_at)
```

**Why this index?**
- Speeds up: `WHERE chore_id = ? AND completed_at BETWEEN ? AND ?`
- Enables efficient sorting: `ORDER BY completed_at DESC`
- Composite index (chore_id, completed_at) covers both conditions

### Query Optimization
- Lazy loading prevents N+1 query problems
- Specific queries fetch only needed data
- Date range queries use indexed columns

### Connection Management
- Hibernate connection pooling
- Transaction management per request
- Automatic resource cleanup

---

## Error Handling

```
┌─────────────────────────────────────┐
│     Exception Handling Flow         │
├─────────────────────────────────────┤
│                                     │
│ ChoreCompletionService              │
│  └─ Throws ResourceNotFoundException │
│     when chore not found            │
│                                     │
│ GlobalExceptionHandler (existing)   │
│  └─ Catches and maps to HTTP 404    │
│                                     │
│ HTTP Response (404 Not Found)       │
│  └─ JSON error message              │
│                                     │
└─────────────────────────────────────┘
```

---

## Summary

The Chore Tracker completion system uses a **layered architecture** with clear separation of concerns:

1. **Controller Layer**: Handles HTTP requests and responses
2. **Service Layer**: Contains business logic and validation
3. **Repository Layer**: Manages data access
4. **Database Layer**: Persists data with optimized indexes

This architecture is **scalable**, **testable**, and **maintainable**.

