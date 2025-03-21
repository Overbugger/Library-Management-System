# DreamDevs Library Management Dashboard

A Java-based Library Management System with a modern JavaFX dashboard interface. This application provides a comprehensive solution to manage books, members, and borrowing/return transactions through a user-friendly dashboard with a responsive UI.

## Features

- **Dashboard Overview:**
    - View key statistics such as total books, total members, borrowed books, and returned books in a visually appealing card layout.

- **Books Management:**
    - View, add, update, borrow, and delete books.
    - Paginated table for books with an action menu for each row.
    - Export books data to CSV using a file chooser.

- **Members Management:**
    - View, add, update, and delete members.
    - Paginated table for members.
    - Export members data to CSV using a file chooser.

- **Borrow/Return Management:**
    - View all borrowing records in a paginated table.
    - Borrow a book and return a book using dedicated forms.

- **Dashboard Navigation:**
    - A sidebar with active/hover indicators.
    - Loading indicators to signal background actions.

## Technologies

- **Java 23**
- **JavaFX 21**
- **Maven** for dependency management
- **PostgreSQL** (configured for Neon DB, with JDBC)

[//]: # (- **JUnit 5** for unit testing)

[//]: # (![Dashboard Screenshot]&#40;./screenshots/dashboard.png&#41;)

## Setup & Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/Library-Management-System.git
   cd Library-Management-System
