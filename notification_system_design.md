Stage 1
Notification System API Design

The notification system should allow users to receive, view, read, and manage notifications. Since users are already logged into the application, no login or registration APIs are required.

Main Actions Supported
Get all notifications for a user
Get unread notifications
Mark a notification as read
Mark all notifications as read
Delete a notification
Receive real-time notifications

API Endpoints
Get All Notifications

Request

GET /api/notifications

Response

{
"notifications": [
{
"id": "123",
"type": "Placement",
"message": "ABC Company is hiring",
"isRead": false,
"createdAt": "2025-08-01T10:00:00"
}
]
}
Get Unread Notifications

Request

GET /api/notifications/unread

Response

{
"count": 5,
"notifications": []
}
Mark Notification as Read

Request

PATCH /api/notifications/{id}/read

Response

{
"message": "Notification marked as read"
}
Mark All Notifications as Read

Request

PATCH /api/notifications/read-all

Response

{
"message": "All notifications marked as read"
}
Delete Notification

Request

DELETE /api/notifications/{id}

Response

{
"message": "Notification deleted successfully"
}
Real-Time Notifications

For real-time updates, I would use WebSocket connections.

When a new notification is created, the server pushes it directly to connected users instead of waiting for the user to refresh the page.

Flow:

User opens application.
WebSocket connection is established.
New notification is generated.
Server pushes notification instantly.
User sees notification immediately.

This reduces unnecessary API calls and provides a better user experience.

# Stage 2

## Database Design

For storing notifications, I would choose PostgreSQL.

Reasons:

* Reliable and widely used
* Good support for indexing
* Handles large amounts of data efficiently
* Easy to maintain and scale

---

## Notifications Table

| Column           | Type      |
| ---------------- | --------- |
| id               | UUID      |
| studentId        | BIGINT    |
| notificationType | VARCHAR   |
| message          | TEXT      |
| isRead           | BOOLEAN   |
| createdAt        | TIMESTAMP |

---

## Sample Schema

```sql
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    studentId BIGINT NOT NULL,
    notificationType VARCHAR(30),
    message TEXT,
    isRead BOOLEAN DEFAULT FALSE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Possible Problems at Scale

As the number of students and notifications increases:

* Queries become slower
* Storage requirements increase
* Sorting and filtering take more time

---

## Improvements

1. Add indexes on frequently searched columns.
2. Archive old notifications.
3. Use pagination instead of loading everything at once.
4. Use caching for frequently requested data.

---

## Example Queries

Get all notifications:

```sql
SELECT *
FROM notifications
WHERE studentId = 1042;
```

Get unread notifications:

```sql
SELECT *
FROM notifications
WHERE studentId = 1042
AND isRead = false;
```

Mark as read:

```sql
UPDATE notifications
SET isRead = true
WHERE id = 'notification-id';
```
# Stage 3

## Query Review

Given Query:

```sql
SELECT *
FROM notifications
WHERE studentID = 1042
AND isRead = false
ORDER BY createdAt DESC;
```

The query is correct because it returns unread notifications for a student.

However, it becomes slow when the database contains millions of notifications because the database may need to scan many rows before finding the required records.

---

## Recommended Index

```sql
CREATE INDEX idx_notifications_student_read_created
ON notifications(studentID, isRead, createdAt DESC);
```

This allows the database to quickly find unread notifications and return them in the correct order.

---

## Should We Add Indexes on Every Column?

No.

Adding indexes on every column increases storage usage and slows down INSERT and UPDATE operations.

Indexes should only be created on columns that are frequently used for searching, filtering, or sorting.

---

## Placement Notifications in Last 7 Days

```sql
SELECT DISTINCT studentID
FROM notifications
WHERE notificationType = 'Placement'
AND createdAt >= NOW() - INTERVAL '7 days';
```

---

# Stage 4

## Performance Improvements

Currently notifications are fetched every time a page loads.

As the number of students grows, this creates unnecessary database traffic.

---

## Suggested Improvements

### Pagination

Load notifications in smaller batches.

Benefits:

* Faster response
* Lower database load

---

### Caching

Store frequently requested notification data in Redis.

Benefits:

* Faster access
* Reduced database queries

---

### Real-Time Updates

Use WebSockets so new notifications are pushed to users instantly.

Benefits:

* Better user experience
* Fewer repeated API requests

---

## Trade-Offs

| Solution   | Advantage         | Disadvantage         |
| ---------- | ----------------- | -------------------- |
| Pagination | Fast loading      | Multiple requests    |
| Cache      | Very fast         | Cache maintenance    |
| WebSocket  | Real-time updates | Extra infrastructure |
|            |                   |                      |

# Stage 5

## Problems in Current Implementation

Current code processes one student at a time.

For 50,000 students:

* Execution will be very slow
* One failure can interrupt processing
* Difficult to retry failed notifications

---

## Better Approach

Use a message queue.

Process:

1. Save notification in database.
2. Push notification job to queue.
3. Worker sends email.
4. Worker sends in-app notification.
5. Failed jobs are retried automatically.

---

## Revised Pseudocode

```text
Create notification

Save notification in database

Push job to queue

Worker:
    Send email
    Send in-app notification

If failure:
    Retry automatically
```

Benefits:

* Faster processing
* Better reliability
* Easier error handling
* Can handle large volumes of users

```
```
## Stage 6

Priority is calculated using both notification type and recency.

Weights used:

- Placement = 300
- Result = 200
- Event = 100

A recency bonus is added so newer notifications appear before older notifications of the same type.

The current implementation sorts all notifications and selects the first 10.

For large-scale systems, a min-heap (PriorityQueue) of size 10 can be maintained. This allows processing incoming notifications in O(n log 10) time while always keeping only the top 10 highest-priority notifications in memory.
