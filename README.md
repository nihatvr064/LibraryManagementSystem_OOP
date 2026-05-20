# Library Management System

A console-based Library Management System built in Java for the OOP final project. The system handles books, magazines, and theses alongside three member tiers (Basic, Silver, Gold), all persisted to plain text files between sessions.

---

## Project Structure

```
LibraryManagementSystem/
├── data/
│   ├── items.txt       ← persisted catalog
│   ├── members.txt     ← persisted members
│   └── borrows.txt     ← persisted borrow records
└── src/main/java/az/library/
    ├── main/
    │   └── Main.java               ← entry point, console UI
    ├── model/
    │   ├── LibraryItem.java        ← abstract base class
    │   ├── Borrowable.java         ← interface
    │   ├── Book.java
    │   ├── Magazine.java
    │   ├── Thesis.java
    │   ├── Member.java             ← abstract base class
    │   └── member/
    │       ├── BasicMember.java    ← limit: 2
    │       ├── SilverMember.java   ← limit: 4
    │       └── GoldMember.java     ← limit: 6
    ├── service/
    │   └── Library.java            ← central manager
    ├── persistence/
    │   └── FileManager.java        ← file I/O
    ├── util/
    │   ├── SearchResult.java       ← generic wrapper
    │   ├── ItemTablePrinter.java   ← formatted table output
    │   └── IdGenerator.java        ← auto-incrementing IDs
    └── exception/
        ├── ItemNotAvailableException.java
        └── BorrowLimitExceededException.java
```

---

## Features

- Add books, magazines, and theses with auto-generated IDs (B001, M001, T001, ...)
- Register members in three tiers: Basic, Silver, Gold
- Borrow and return items with automatic availability tracking
- Fine calculation on return based on overdue days — each item type has its own rate
- Search by title or by author using a generic `SearchResult<T>` wrapper
- Per-member borrowing report with a formatted table
- List all currently available items
- Data persistence — everything is saved to `data/` on every mutating operation and loaded on startup
- ANSI colour output and animated loading indicators in the terminal
- No crashes on invalid or empty input

---

## Member Tiers

| Tier | Borrow Limit |
|------|-------------|
| Basic | 2 items |
| Silver | 4 items |
| Gold | 6 items |

---

## Item Types and Fine Rates

| Type | Max Loan Period | Fine Per Overdue Day |
|------|----------------|----------------------|
| Book | 14 days | 0.50 AZN |
| Magazine | 7 days | 0.25 AZN |
| Thesis | 21 days | 1.00 AZN |

Fine is calculated as: `overdueDays × finePerDay`. The `calculateFine(int overdueDays)` method is a `default` method in the `Borrowable` interface, calling `getFinePerOverdueDay()` which each item type overrides.

---

## Running the Application

**Requirements:** Java 17+, no external libraries

```bash
# Clone
git clone https://github.com/nihatvr064/LibraryManagementSystem_OOP.git
cd LibraryManagementSystem_OOP

# Compile
find src -name "*.java" | xargs javac -d out

# Run (from project root so data/ paths resolve correctly)
java -cp out az.library.main.Main
```

---

## Console Menu

```
╔══════════════════════════════════════════════════════════╗
║              LIBRARY MANAGEMENT SYSTEM                   ║
║              Basic • Silver • Gold Edition               ║
║              OOP Java Final Project                      ║
╚══════════════════════════════════════════════════════════╝

┌──────────────────────────────────────────────────────────┐
│  1. Add new item                                         │
│  2. Register new member                                  │
│  3. Borrow item                                          │
│  4. Return item                                          │
│  5. Search items                                         │
│  6. View member report                                   │
│  7. View all available items                             │
│  8. Exit                                                 │
└──────────────────────────────────────────────────────────┘
```

---

## OOP Concepts Applied

| Concept | Implementation |
|---------|---------------|
| Abstract class | `LibraryItem` — shared fields and `displayInfo()`; `Member` — shared borrow list and validation |
| Interface | `Borrowable` — `borrow()`, `returnItem()`, `getFinePerOverdueDay()`, default `calculateFine()` |
| Inheritance | `Book`, `Magazine`, `Thesis` extend `LibraryItem`; `BasicMember`, `SilverMember`, `GoldMember` extend `Member` |
| Polymorphism | `getItemType()`, `getMaxLoanDays()`, `getFinePerOverdueDay()`, `getMaxBorrowLimit()` all resolved at runtime |
| Encapsulation | All fields private; setters reject null/empty values with `IllegalArgumentException` |
| Generics | `SearchResult<T extends LibraryItem>` — type-safe result wrapper |
| Collections | `HashMap<String, LibraryItem>` for catalog; `HashMap<String, Member>` for members; `ArrayList` for borrowed items |
| Custom exceptions | `ItemNotAvailableException`, `BorrowLimitExceededException` — both `RuntimeException`, thrown and caught at the right layer |
| File persistence | `FileManager` reads/writes three semicolon-delimited files; `IdGenerator.sync*()` restores counters after load |

---

## ID Generation

IDs are auto-generated and follow a simple prefix + zero-padded number format:

- Books → `B001`, `B002`, ...
- Magazines → `M001`, `M002`, ...
- Theses → `T001`, `T002`, ...
- Members → `U001`, `U002`, ...

On startup, `IdGenerator.syncItemId()` and `syncMemberId()` scan the loaded data and advance the counters so new entries never collide with existing ones.

---

## Author

Nihat — OOP Final Project, Spring 2026
