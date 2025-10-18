# Cleaner and Viewer Synchronization Problem (Java Monitor Implementation)

This repository demonstrates two classic **critical section synchronization** problems implemented in Java, modeled as a **"Cleaner and Viewer" hallway scenario**.  
The goal is to simulate multiple threads accessing a shared resource (a hallway between two buildings) under different synchronization constraints — using only Java’s **monitor mechanism (`synchronized`, `wait`, `notify`, `notifyAll`)**, without semaphores.

---

## 🧩 Scenario 1: Simple Cleaner–Viewer Problem

### 🔹 Description
There are two types of people:
- **Viewers**: can share the hallway with other viewers.
- **Cleaners**: need **exclusive** access — no other cleaners or viewers can be inside.

### 🔹 Rules
- If a **viewer** wants to enter:
  - There must be **no cleaner** inside.
- If a **cleaner** wants to enter:
  - The hallway must be **completely empty**.
- To avoid **cleaner starvation**, once any cleaner is waiting, new viewers must wait until all cleaners have finished.

### 🔹 Implementation Highlights
- Uses `synchronized` methods for mutual exclusion.
- Uses `wait()` and `notifyAll()` for coordination.
- Viewers can enter concurrently, but cleaners have priority when waiting.

### 🔹 Output Example
Viewer-1 ENTERED hallway
Viewer-2 ENTERED hallway
Cleaner-1 wants to ENTER hallway
Viewer-1 EXITED hallway
Viewer-2 EXITED hallway
Cleaner-1 ENTERED to clean
Cleaner-1 EXITED hallway

---

## 🧠 Scenario 2: Advanced “Always-Active Viewers with Dirt Detection”

### 🔹 Description
In this extended version:
- **Viewers** are continuously in the hallway.
- With a **20% probability**, a viewer detects *dirt*.
- When dirt is seen:
  - All viewers **evacuate** and wait.
  - A **cleaner** is **summoned**.
  - After cleaning, viewers **re-enter** and continue as before.
- The system gracefully **shuts down** when all viewers are done.

### 🔹 Key Monitor Logic
- `dirtReported` — signals that the hallway is dirty.
- `cleanerIn` — indicates that a cleaner has exclusive access.
- `viewersIn` and `waitingViewers` — track current and waiting viewers.
- `notifyAll()` ensures that all waiting threads recheck conditions after each state change.

### 🔹 Graceful Shutdown
A counter (`liveViewers`) tracks active viewers.  
When all viewers complete their runs, `shuttingDown` is set to true, signaling the cleaner thread to exit cleanly.

### 🔹 Output Example
✅ Viewer-3 ENTERED hallway
⚠️ Viewer-3 saw DIRT! Summoning cleaner.
🚪 Viewer-3 EXITED to waiting queue
🧹 Cleaner ENTERED to clean
✨ Cleaner FINISHED cleaning
🔙 Viewer-3 RE-ENTERED after cleaning
👋 Viewer-3 DONE for demo
🛑 Cleaner exiting.
