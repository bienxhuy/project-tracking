# useWebSocketNotification.ts - Complete Explanation

## 📚 File Overview

**File**: `src/hooks/useWebSocketNotification.ts`  
**Purpose**: Custom React hook để manage WebSocket notifications trong React components  
**Pattern**: Custom Hook Pattern + Observer Pattern  
**Dependencies**: React (useState, useEffect, useCallback), websocket.service.ts

---

## 🎯 What This Hook Does

Hook này cung cấp một **simple interface** để React components có thể:
1. ✅ Connect/disconnect WebSocket tự động khi component mount/unmount
2. ✅ Receive real-time notifications
3. ✅ Track unread notification count
4. ✅ Monitor connection status
5. ✅ Prevent memory leaks với proper cleanup

---

## 📄 Complete Code

```typescript
import { useEffect, useState } from 'react';
import { webSocketService, WebSocketNotification } from '../services/websocket.service';

export const useWebSocketNotification = () => {
  const [notifications, setNotifications] = useState<WebSocketNotification[]>([]);
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const [isConnected, setIsConnected] = useState<boolean>(false);

  useEffect(() => {
    // Connect to WebSocket
    webSocketService.connect();

    // Register callbacks
    const unsubscribeNotification = webSocketService.onNotification((notification) => {
      setNotifications((prev) => [notification, ...prev]);
    });

    const unsubscribeCount = webSocketService.onNotificationCount((count) => {
      setUnreadCount(count);
    });

    const unsubscribeConnection = webSocketService.onConnectionChange((connected) => {
      setIsConnected(connected);
    });

    // Cleanup
    return () => {
      unsubscribeNotification();
      unsubscribeCount();
      unsubscribeConnection();
      webSocketService.disconnect();
    };
  }, []); // Empty dependency array

  return { notifications, unreadCount, isConnected };
};
```

---

## 📖 Line-by-Line Explanation

### 1. Imports

```typescript
import { useEffect, useState } from 'react';
```

**Giải thích:**
- `useState`: React hook để manage component state
- `useEffect`: React hook để handle side effects (connect WebSocket, cleanup)

**Tại sao cần:**
- State để store notifications, count, connection status
- Effect để connect/disconnect WebSocket khi component lifecycle changes

---

```typescript
import { webSocketService, WebSocketNotification } from '../services/websocket.service';
```

**Giải thích:**
- `webSocketService`: Singleton instance của WebSocket service (đã giải thích trong WEBSOCKET_SERVICE_EXPLAINED.md)
- `WebSocketNotification`: TypeScript interface định nghĩa notification structure

**Import path:**
- `../services/websocket.service` → Relative path từ hooks folder đến services folder

---

### 2. Hook Declaration

```typescript
export const useWebSocketNotification = () => {
```

**Giải thích:**
- `export`: Make hook available để import vào components
- `const`: Hook được define như một constant function
- `useWebSocketNotification`: Hook name theo convention phải start với "use"
- Arrow function `() => {}`: Modern JavaScript syntax

**Naming Convention:**
- ✅ Must start with "use" → React recognizes it as a hook
- ✅ Descriptive name → `useWebSocketNotification` clearly states purpose

**React Hook Rules:**
1. Only call at top level (không được gọi trong loops, conditions, nested functions)
2. Only call from React functions (components or custom hooks)

---

### 3. State Management

#### 3.1 Notifications State

```typescript
const [notifications, setNotifications] = useState<WebSocketNotification[]>([]);
```

**Breakdown:**
- `useState<WebSocketNotification[]>`: Generic type specifies array of notifications
- `([])`: Initial state là empty array
- `[notifications, setNotifications]`: Destructuring → current value và setter function

**TypeScript Generic:**
```typescript
WebSocketNotification[]
```
- Ensures type safety → Only notification objects can be added
- TypeScript sẽ error nếu push wrong type

**State Flow:**
```
Component Render #1: notifications = []
  ↓ WebSocket message received
setNotifications called
  ↓ React schedules re-render
Component Render #2: notifications = [notification1]
  ↓ Another message received
setNotifications called
  ↓ React schedules re-render
Component Render #3: notifications = [notification2, notification1]
```

---

#### 3.2 Unread Count State

```typescript
const [unreadCount, setUnreadCount] = useState<number>(0);
```

**Breakdown:**
- `useState<number>`: Type-safe number state
- `(0)`: Initial count = 0
- Used để hiển thị badge/indicator trong UI

**Usage Example:**
```typescript
// In component:
{unreadCount > 0 && (
  <span className="badge">{unreadCount}</span>
)}
```

---

#### 3.3 Connection Status State

```typescript
const [isConnected, setIsConnected] = useState<boolean>(false);
```

**Breakdown:**
- `useState<boolean>`: Binary connected/disconnected state
- `(false)`: Initially disconnected (safe default)
- Used để show connection indicator hoặc retry button

**Usage Example:**
```typescript
// In component:
{!isConnected && (
  <div className="offline-banner">Reconnecting...</div>
)}
```

---

### 4. Side Effects (useEffect)

```typescript
useEffect(() => {
  // Effect logic here
}, []); // Empty dependency array
```

**React useEffect Hook:**
- Runs **after** component renders
- Used cho side effects: API calls, subscriptions, timers, etc.
- Returns cleanup function

**Dependency Array `[]`:**
- Empty array = run only **once** after initial mount
- Similar to `componentDidMount` in class components

**Lifecycle:**
```
Component Mount
  ↓
First Render
  ↓
useEffect runs (connect WebSocket)
  ↓
Component stays mounted, receives updates
  ↓
Component Unmount
  ↓
useEffect cleanup runs (disconnect WebSocket)
```

---

#### 4.1 WebSocket Connection

```typescript
webSocketService.connect();
```

**What Happens:**
1. Calls `connect()` method của singleton service
2. Service creates STOMP Client
3. Connects to `ws://localhost:9090/ws`
4. SockJS fallback nếu WebSocket fails
5. Subscribes to notification channels

**Connection Details:**
- URL: From `.env` file (`VITE_WS_URL`)
- Protocol: STOMP over WebSocket/SockJS
- Authentication: JWT token trong headers
- Heartbeat: 10s ping/pong

**See Also:**
- Chi tiết connection process → `WEBSOCKET_SERVICE_EXPLAINED.md`

---

#### 4.2 Register Notification Callback

```typescript
const unsubscribeNotification = webSocketService.onNotification((notification) => {
  setNotifications((prev) => [notification, ...prev]);
});
```

**Breakdown Line-by-Line:**

**Line 1:**
```typescript
const unsubscribeNotification = webSocketService.onNotification(
```
- `webSocketService.onNotification`: Register callback function (Observer pattern)
- Returns **unsubscribe function** để cleanup later
- Store trong `unsubscribeNotification` constant

**Line 2:**
```typescript
(notification) => {
```
- Arrow function callback
- Parameter `notification`: WebSocketNotification object received from server
- Called every time new notification arrives

**Line 3:**
```typescript
setNotifications((prev) => [notification, ...prev]);
```
- Update React state
- **Functional update form** → `(prev) => newValue`
- `prev`: Previous notifications array
- `[notification, ...prev]`: New array with new notification prepended
- Spread operator `...`: Copies all existing notifications

**Why Functional Update?**
```typescript
// ❌ BAD (may cause race conditions):
setNotifications([notification, ...notifications]);

// ✅ GOOD (always uses latest state):
setNotifications((prev) => [notification, ...prev]);
```

**State Update Example:**
```typescript
// Current state
prev = [
  { id: 1, title: "Old notification" }
]

// New notification arrives
notification = { id: 2, title: "New notification" }

// After update
[notification, ...prev] = [
  { id: 2, title: "New notification" },  // New (prepended)
  { id: 1, title: "Old notification" }   // Old (spread)
]
```

**Observer Pattern:**
```
webSocketService.onNotification() registers callback
  ↓ Stores in internal array
webSocketService receives STOMP message
  ↓ Parses JSON
webSocketService.notificationCallbacks.forEach()
  ↓ Calls all registered callbacks
This hook's callback executes
  ↓ Updates React state
Component re-renders with new notification ✅
```

---

#### 4.3 Register Count Callback

```typescript
const unsubscribeCount = webSocketService.onNotificationCount((count) => {
  setUnreadCount(count);
});
```

**Purpose:**
- Update unread count badge
- Lightweight updates (just number, không cần full notification object)

**Flow:**
```
Backend: sendNotificationCount(userId, count)
  ↓
WebSocket: /user/queue/notification-count
  ↓
Service: onNotificationCount callback triggered
  ↓
Hook: setUnreadCount(count)
  ↓
Component: Badge updates ✅
```

**Example Scenario:**
```typescript
// User reads notification on another tab
// Backend sends count update: 5 → 4
count = 4

// React updates
setUnreadCount(4)

// Badge shows "4" instead of "5" ✅
```

---

#### 4.4 Register Connection Callback

```typescript
const unsubscribeConnection = webSocketService.onConnectionChange((connected) => {
  setIsConnected(connected);
});
```

**Purpose:**
- Track connection status
- Show offline banner hoặc reconnecting indicator
- Enable/disable features based on connection

**Connection States:**
```typescript
connected = true  → Online, receiving real-time updates
connected = false → Offline, may be reconnecting
```

**UI Example:**
```typescript
{isConnected ? (
  <span className="status online">●</span>
) : (
  <span className="status offline">●</span>
)}
```

**State Transitions:**
```
Initial: isConnected = false
  ↓ Connection successful
webSocketService triggers callback(true)
  ↓ React updates
isConnected = true ✅
  ↓ Network issue
webSocketService triggers callback(false)
  ↓ React updates
isConnected = false ⚠️
  ↓ Auto-reconnect successful
webSocketService triggers callback(true)
  ↓ React updates
isConnected = true ✅
```

---

#### 4.5 Cleanup Function

```typescript
return () => {
  unsubscribeNotification();
  unsubscribeCount();
  unsubscribeConnection();
  webSocketService.disconnect();
};
```

**Why Cleanup Needed:**
- **Prevent memory leaks**: Remove callback references
- **Close connections**: Disconnect WebSocket gracefully
- **React best practice**: Always cleanup side effects

**Cleanup Execution:**
- Runs when component **unmounts**
- Runs **before** re-running effect (if dependencies change)
- Since dependencies `[]` empty, runs only on unmount

**Step-by-Step Cleanup:**

**Step 1:**
```typescript
unsubscribeNotification();
```
- Calls unsubscribe function returned by `onNotification()`
- Removes callback from `webSocketService.notificationCallbacks` array
- Prevents callback from being called after component unmounts

**Internal Implementation:**
```typescript
// In webSocketService.onNotification():
onNotification(callback: (notification: WebSocketNotification) => void) {
  this.notificationCallbacks.push(callback);
  
  // Return unsubscribe function
  return () => {
    this.notificationCallbacks = this.notificationCallbacks.filter(
      cb => cb !== callback
    );
  };
}
```

**Step 2-3:**
```typescript
unsubscribeCount();
unsubscribeConnection();
```
- Same pattern for count and connection callbacks
- Remove all registered listeners

**Step 4:**
```typescript
webSocketService.disconnect();
```
- Close WebSocket connection
- Unsubscribe from STOMP topics
- Deactivate STOMP client
- Prevent unnecessary network activity

**Memory Leak Prevention:**
```
Without cleanup:
  Component unmounts
  → Callback still registered in service
  → Service calls callback
  → Tries to update unmounted component state
  → React warning + memory leak ❌

With cleanup:
  Component unmounts
  → Cleanup function runs
  → Callbacks unregistered
  → Service won't call removed callbacks
  → No memory leak ✅
```

---

#### 4.6 Empty Dependency Array

```typescript
}, []); // Empty dependency array
```

**What `[]` Means:**
- Effect runs **only once** after initial mount
- Cleanup runs **only once** before unmount
- Effect doesn't re-run on component updates

**Comparison:**

```typescript
// No dependencies (runs after every render)
useEffect(() => {
  console.log('Runs on every render');
});

// Empty dependencies (runs once on mount)
useEffect(() => {
  console.log('Runs only on mount');
}, []);

// With dependencies (runs when dependencies change)
useEffect(() => {
  console.log('Runs when userId changes');
}, [userId]);
```

**Why Empty For This Hook:**
- WebSocket connection should persist throughout component lifecycle
- Don't want to reconnect on every render
- Single connection shared across re-renders

**Lifecycle Timeline:**
```
Component Mount
  ↓
Initial Render (notifications=[], unreadCount=0, isConnected=false)
  ↓
useEffect runs (dependencies [] → first mount only)
  ↓ webSocketService.connect()
  ↓ Register callbacks
  ↓
WebSocket connected ✅
  ↓
Messages arrive → callbacks trigger → state updates → re-renders
  ↓ (useEffect does NOT re-run on these re-renders)
  ↓
Component Unmount
  ↓
Cleanup function runs
  ↓ Unsubscribe callbacks
  ↓ Disconnect WebSocket
```

---

### 5. Return Statement

```typescript
return { notifications, unreadCount, isConnected };
```

**Object Return:**
- Returns object với named properties
- Components can destructure what they need
- Flexible API

**Usage in Component:**
```typescript
// Get all values
const { notifications, unreadCount, isConnected } = useWebSocketNotification();

// Or just what you need
const { notifications } = useWebSocketNotification();
const { unreadCount } = useWebSocketNotification();
```

**Return Type (TypeScript Inference):**
```typescript
{
  notifications: WebSocketNotification[];
  unreadCount: number;
  isConnected: boolean;
}
```

---

## 🔄 Complete Hook Flow

### Component Lifecycle

```
1. Component First Render
   ├─ Hook initialization
   │  ├─ useState creates state variables
   │  │  ├─ notifications = []
   │  │  ├─ unreadCount = 0
   │  │  └─ isConnected = false
   │  └─ Return initial values to component
   │
   ├─ Component renders with initial data
   │  └─ useEffect scheduled to run after render
   │
2. After First Render (useEffect runs)
   ├─ webSocketService.connect()
   │  ├─ Create STOMP Client
   │  ├─ Connect to ws://localhost:9090/ws
   │  └─ Subscribe to channels
   │
   ├─ Register callbacks
   │  ├─ onNotification → updates notifications state
   │  ├─ onNotificationCount → updates unreadCount state
   │  └─ onConnectionChange → updates isConnected state
   │
   └─ Store unsubscribe functions
   
3. WebSocket Connection Established
   ├─ Connection callback triggered
   │  └─ setIsConnected(true)
   │     └─ Component re-renders (isConnected=true)
   │
4. Notification Received
   ├─ STOMP message arrives
   ├─ Service parses JSON
   ├─ Notification callback triggered
   │  └─ setNotifications((prev) => [notification, ...prev])
   │     └─ Component re-renders (new notification in list)
   │
5. Count Update Received
   ├─ STOMP message arrives
   ├─ Service parses number
   └─ Count callback triggered
      └─ setUnreadCount(count)
         └─ Component re-renders (badge updated)

6. Component Unmount
   └─ useEffect cleanup runs
      ├─ unsubscribeNotification()
      ├─ unsubscribeCount()
      ├─ unsubscribeConnection()
      └─ webSocketService.disconnect()
         └─ Clean shutdown ✅
```

---

## 💡 Usage Examples

### Example 1: Notification List Component

```typescript
import React from 'react';
import { useWebSocketNotification } from '../hooks/useWebSocketNotification';

const NotificationList: React.FC = () => {
  const { notifications, unreadCount, isConnected } = useWebSocketNotification();

  return (
    <div className="notification-list">
      {/* Connection Status */}
      <div className="status-bar">
        {isConnected ? (
          <span className="online">● Connected</span>
        ) : (
          <span className="offline">● Reconnecting...</span>
        )}
      </div>

      {/* Unread Count Badge */}
      <div className="header">
        <h2>Notifications</h2>
        {unreadCount > 0 && (
          <span className="badge">{unreadCount}</span>
        )}
      </div>

      {/* Notification Items */}
      <div className="notification-items">
        {notifications.length === 0 ? (
          <p>No notifications</p>
        ) : (
          notifications.map((notification) => (
            <div key={notification.id} className="notification-item">
              <h4>{notification.title}</h4>
              <p>{notification.message}</p>
              <small>{new Date(notification.timestamp).toLocaleString()}</small>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default NotificationList;
```

**How It Works:**
1. Component mounts → Hook connects WebSocket
2. Initial render shows empty list
3. Notifications arrive → State updates → Re-render
4. UI shows real-time notifications ✅

---

### Example 2: Notification Bell (Just Count)

```typescript
import React from 'react';
import { useWebSocketNotification } from '../hooks/useWebSocketNotification';

const NotificationBell: React.FC = () => {
  // Only need unreadCount, but hook still manages everything
  const { unreadCount } = useWebSocketNotification();

  return (
    <div className="notification-bell">
      <button className="bell-icon">
        🔔
        {unreadCount > 0 && (
          <span className="count-badge">{unreadCount}</span>
        )}
      </button>
    </div>
  );
};

export default NotificationBell;
```

**Optimization Note:**
- Even though we only use `unreadCount`, hook still manages all state
- This is okay because state updates are cheap
- If performance is critical, could create separate hooks

---

### Example 3: Multiple Components Using Same Hook

```typescript
// Component A
const Header: React.FC = () => {
  const { unreadCount } = useWebSocketNotification();
  return <span>Unread: {unreadCount}</span>;
};

// Component B
const Sidebar: React.FC = () => {
  const { notifications } = useWebSocketNotification();
  return <ul>{notifications.map(n => <li>{n.title}</li>)}</ul>;
};

// Component C
const StatusBar: React.FC = () => {
  const { isConnected } = useWebSocketNotification();
  return <div>{isConnected ? '●' : '○'}</div>;
};
```

**Important:**
- Each component calls hook separately
- Each hook instance connects to **same singleton service**
- Service manages one shared connection
- All components receive same updates ✅

**Service Singleton Pattern Ensures:**
```
Header.useWebSocketNotification()
  ↓
Sidebar.useWebSocketNotification()  → All connect to SAME webSocketService
  ↓
StatusBar.useWebSocketNotification()

One WebSocket connection shared by all ✅
```

---

## 🎯 Design Patterns

### 1. Custom Hook Pattern

**Definition:**
- Reusable logic extracted into function starting with "use"
- Follows React hooks rules
- Can use other hooks inside

**Benefits:**
- ✅ Separation of concerns (logic vs UI)
- ✅ Reusability across components
- ✅ Easier testing
- ✅ Cleaner component code

**Before (Without Hook):**
```typescript
const NotificationList: React.FC = () => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    webSocketService.connect();
    
    const unsub1 = webSocketService.onNotification((n) => {
      setNotifications((prev) => [n, ...prev]);
    });
    
    const unsub2 = webSocketService.onNotificationCount((c) => {
      setUnreadCount(c);
    });
    
    const unsub3 = webSocketService.onConnectionChange((c) => {
      setIsConnected(c);
    });

    return () => {
      unsub1();
      unsub2();
      unsub3();
      webSocketService.disconnect();
    };
  }, []);

  // Component rendering logic...
};
```

**After (With Hook):**
```typescript
const NotificationList: React.FC = () => {
  const { notifications, unreadCount, isConnected } = useWebSocketNotification();
  
  // Component rendering logic...
};
```

**Much Cleaner! ✅**

---

### 2. Observer Pattern

**Definition:**
- Subject (webSocketService) notifies observers (callbacks) when state changes
- One-to-many relationship

**Implementation:**
```
webSocketService (Subject)
  ├─ notificationCallbacks[] (Observers)
  ├─ countCallbacks[] (Observers)
  └─ connectionCallbacks[] (Observers)

When notification arrives:
  → webSocketService.notificationCallbacks.forEach(cb => cb(notification))
  → All registered callbacks execute
  → Each component updates its own state
```

**Benefits:**
- ✅ Loose coupling
- ✅ Multiple components can react to same event
- ✅ Easy to add/remove observers

---

### 3. Singleton Pattern (Service)

**Definition:**
- Only one instance of webSocketService exists
- Shared across all components

**Implementation:**
```typescript
// In websocket.service.ts
class WebSocketService {
  // class implementation
}

export const webSocketService = new WebSocketService(); // Singleton instance
```

**Why Singleton:**
- Only one WebSocket connection needed
- Multiple components share same connection
- Prevents connection duplication

**Hook Usage:**
```typescript
// Component A
const { notifications } = useWebSocketNotification();
// → Uses webSocketService singleton

// Component B
const { unreadCount } = useWebSocketNotification();
// → Uses SAME webSocketService singleton

// Result: One connection, multiple consumers ✅
```

---

## ⚡ Performance Considerations

### 1. State Updates

**Functional Updates:**
```typescript
// ✅ GOOD (always uses latest state)
setNotifications((prev) => [notification, ...prev]);

// ❌ BAD (may use stale state in concurrent updates)
setNotifications([notification, ...notifications]);
```

**Why:**
- React may batch multiple state updates
- Functional form ensures each update uses latest state
- Prevents race conditions

---

### 2. Array Prepending

**Current Implementation:**
```typescript
[notification, ...prev]
```

**Performance:**
- ✅ O(n) time complexity (spreads all elements)
- ✅ Creates new array (immutability required by React)
- ✅ Newest notifications at top (UX preference)

**Alternative (Appending):**
```typescript
[...prev, notification]
```
- Same O(n) performance
- Oldest at top (usually not desired for notifications)

**If Performance Critical:**
```typescript
// Limit array size
setNotifications((prev) => [notification, ...prev].slice(0, 50));
```
- Keep only recent 50 notifications
- Prevents unbounded growth

---

### 3. Dependency Array

**Empty Array:**
```typescript
useEffect(() => {
  // Setup logic
}, []); // Runs once
```

**Benefits:**
- ✅ No unnecessary re-runs
- ✅ Single WebSocket connection
- ✅ Optimal performance

**Warning:**
```typescript
// ❌ Don't do this (exhaustive deps warning)
useEffect(() => {
  webSocketService.connect();
}, [webSocketService]); // Object never changes, but React may warn
```

---

### 4. Multiple Hook Instances

**Scenario:**
```typescript
// 3 components use hook
<Header />    // useWebSocketNotification()
<Sidebar />   // useWebSocketNotification()
<Footer />    // useWebSocketNotification()
```

**Result:**
- 3 hook instances created
- Each has own state (notifications, unreadCount, isConnected)
- **But only 1 WebSocket connection** (singleton service)

**Performance Impact:**
- State: 3x memory (small objects, negligible)
- Connection: 1x network (efficient)
- Re-renders: Only affected components re-render

---

## 🧪 Testing

### Unit Testing the Hook

```typescript
import { renderHook, act } from '@testing-library/react-hooks';
import { useWebSocketNotification } from './useWebSocketNotification';
import { webSocketService } from '../services/websocket.service';

// Mock the service
jest.mock('../services/websocket.service');

describe('useWebSocketNotification', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('should connect on mount', () => {
    renderHook(() => useWebSocketNotification());
    
    expect(webSocketService.connect).toHaveBeenCalledTimes(1);
  });

  test('should register callbacks', () => {
    renderHook(() => useWebSocketNotification());
    
    expect(webSocketService.onNotification).toHaveBeenCalled();
    expect(webSocketService.onNotificationCount).toHaveBeenCalled();
    expect(webSocketService.onConnectionChange).toHaveBeenCalled();
  });

  test('should update notifications state', () => {
    const { result } = renderHook(() => useWebSocketNotification());
    
    const mockNotification = {
      id: 1,
      title: 'Test',
      message: 'Test message',
      type: 'INFO',
      timestamp: new Date().toISOString()
    };

    // Simulate notification callback
    act(() => {
      const callback = webSocketService.onNotification.mock.calls[0][0];
      callback(mockNotification);
    });

    expect(result.current.notifications).toContain(mockNotification);
  });

  test('should cleanup on unmount', () => {
    const unsubscribeMock = jest.fn();
    webSocketService.onNotification.mockReturnValue(unsubscribeMock);
    
    const { unmount } = renderHook(() => useWebSocketNotification());
    
    unmount();
    
    expect(unsubscribeMock).toHaveBeenCalled();
    expect(webSocketService.disconnect).toHaveBeenCalled();
  });
});
```

---

### Integration Testing

```typescript
import { render, screen, waitFor } from '@testing-library/react';
import NotificationList from './NotificationList';
import { webSocketService } from '../services/websocket.service';

test('should display notifications in real-time', async () => {
  render(<NotificationList />);

  // Simulate notification received
  const mockNotification = {
    id: 1,
    title: 'New Task',
    message: 'You have been assigned a task',
    type: 'INFO',
    timestamp: new Date().toISOString()
  };

  // Trigger callback
  webSocketService.onNotification.mock.calls[0][0](mockNotification);

  // Check UI updates
  await waitFor(() => {
    expect(screen.getByText('New Task')).toBeInTheDocument();
  });
});
```

---

## 🐛 Common Issues

### Issue 1: Memory Leak Warning

**Error:**
```
Warning: Can't perform a React state update on an unmounted component.
```

**Cause:**
- WebSocket message received after component unmounts
- Callback tries to update state

**Solution:**
- Always call cleanup functions ✅
- Hook already handles this properly

**Verification:**
```typescript
return () => {
  unsubscribeNotification(); // ✅ Removes callback
  unsubscribeCount();
  unsubscribeConnection();
  webSocketService.disconnect();
};
```

---

### Issue 2: Stale State in Callbacks

**Problem:**
```typescript
// ❌ BAD
const [count, setCount] = useState(0);

useEffect(() => {
  webSocketService.onNotification(() => {
    setCount(count + 1); // Uses stale 'count' value
  });
}, []);
```

**Solution:**
```typescript
// ✅ GOOD
setNotifications((prev) => [notification, ...prev]); // Uses latest state
```

---

### Issue 3: Multiple Connections

**Problem:**
- Hook called in many components
- Fear of multiple WebSocket connections

**Solution:**
- webSocketService is singleton ✅
- Only one connection created
- All components share it

---

### Issue 4: Effect Re-running

**Problem:**
```typescript
useEffect(() => {
  webSocketService.connect();
}, [someValue]); // Re-connects when someValue changes
```

**Solution:**
```typescript
useEffect(() => {
  webSocketService.connect();
}, []); // Empty array = run once ✅
```

---

## 🎓 Key Takeaways

### React Concepts Used

1. **Custom Hooks**
   - Reusable stateful logic
   - Follows naming convention (use*)
   - Can use other hooks

2. **useState**
   - Manage component state
   - Triggers re-renders on updates
   - Functional updates for latest state

3. **useEffect**
   - Handle side effects
   - Cleanup prevents memory leaks
   - Dependency array controls re-runs

4. **Functional Updates**
   - `setState((prev) => newValue)`
   - Ensures latest state used
   - Prevents race conditions

### TypeScript Benefits

1. **Type Safety**
   ```typescript
   useState<WebSocketNotification[]>([])
   ```
   - Compiler checks type correctness
   - IntelliSense autocomplete
   - Catches errors early

2. **Interface Definitions**
   ```typescript
   WebSocketNotification
   ```
   - Clear data structure
   - Documentation
   - Refactoring safety

### Design Patterns

1. **Custom Hook Pattern**: Reusable logic extraction
2. **Observer Pattern**: Service notifies hook via callbacks
3. **Singleton Pattern**: One service instance shared

---

## 📚 Related Documentation

- **WebSocket Service**: `WEBSOCKET_SERVICE_EXPLAINED.md`
- **Backend Config**: `backend/docs/websocket/WEBSOCKET_CONFIG_EXPLAINED.md`
- **Backend Service**: `backend/docs/websocket/WEBSOCKET_SERVICE_EXPLAINED.md`
- **Complete Guide**: `docs/websocket/INDEX.md`

---

## 🚀 Next Steps

1. **Read Service Documentation**: Understand underlying WebSocket service
2. **Try Examples**: Implement in your components
3. **Customize**: Extend hook for your needs (e.g., filtering, pagination)
4. **Test**: Write unit tests for your usage

---

**Happy Coding! 🎉**

This hook provides a clean, type-safe interface to WebSocket notifications in React. Use it wherever you need real-time updates!
