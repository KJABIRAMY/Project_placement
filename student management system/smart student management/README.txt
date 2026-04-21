SMART STUDENT MANAGEMENT SYSTEM
================================

A comprehensive student management system built with Java using:
- Linked List data structure
- File handling for persistence
- Graphical User Interface (GUI) with Swing
- Console interface option

FEATURES:
---------
1. INSERT STUDENT
   - Add new students with ID, Name, and Marks
   - Validation for duplicate IDs
   - Marks range: 0-100

2. DELETE STUDENT
   - Remove student by ID
   - Confirmation dialog before deletion
   - Updates file automatically

3. SEARCH STUDENT
   - Find student by ID
   - Displays complete student information
   - O(n) search complexity

4. SORT BY MARKS
   - Sort all students by marks in descending order
   - Bubble sort algorithm
   - Auto-saves sorted data

5. VIEW ALL STUDENTS
   - Display all students in table format (GUI)
   - Shows ID, Name, and Marks
   - Real-time total count

6. FILE HANDLING
   - Auto-save to students.txt
   - Load existing data on startup
   - CSV format for easy data management

DATA STRUCTURE:
---------------
- Linked List implementation for dynamic storage
- Custom Node class for list traversal
- No array size limitations

USAGE:
------

GUI VERSION (Recommended):
1. Compile: javac SmartStudentManagementGUI.java
2. Run: java SmartStudentManagementGUI
3. Use the intuitive interface with buttons and table display

CONSOLE VERSION:
1. Compile: javac SmartStudentManagement.java
2. Run: java SmartStudentManagement
3. Follow the menu-driven interface

FILE FORMAT:
-----------
students.txt contains student records in format:
ID,Name,Marks
101,John,85.5
102,Sarah,92.0
103,Mike,78.5

FEATURES DETAILS:
-----------------
✓ Linked List for dynamic memory
✓ File persistence (auto-save)
✓ Input validation
✓ User-friendly GUI
✓ Real-time data updates
✓ Search functionality
✓ Sort with bubble sort
✓ Error handling
