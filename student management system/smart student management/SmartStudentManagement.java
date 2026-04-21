import java.io.*;
import java.util.*;

public class SmartStudentManagement {

    // Student class
    static class Student {
        int id;
        String name;
        double marks;

        Student(int id, String name, double marks) {
            this.id = id;
            this.name = name;
            this.marks = marks;
        }

        @Override
        public String toString() {
            return "ID: " + id + " | Name: " + name + " | Marks: " + marks;
        }
    }

    // Node class for LinkedList
    static class Node {
        Student data;
        Node next;

        Node(Student data) {
            this.data = data;
            this.next = null;
        }
    }

    // LinkedList implementation
    static class StudentLinkedList {
        private Node head;

        StudentLinkedList() {
            this.head = null;
        }

        // Insert student at the end
        public void insert(Student student) {
            Node newNode = new Node(student);
            if (head == null) {
                head = newNode;
            } else {
                Node temp = head;
                while (temp.next != null) {
                    temp = temp.next;
                }
                temp.next = newNode;
            }
        }

        // Delete student by ID
        public boolean delete(int id) {
            if (head == null) return false;

            if (head.data.id == id) {
                head = head.next;
                return true;
            }

            Node temp = head;
            while (temp.next != null) {
                if (temp.next.data.id == id) {
                    temp.next = temp.next.next;
                    return true;
                }
                temp = temp.next;
            }
            return false;
        }

        // Search student by ID
        public Student search(int id) {
            Node temp = head;
            while (temp != null) {
                if (temp.data.id == id) {
                    return temp.data;
                }
                temp = temp.next;
            }
            return null;
        }

        // Sort by marks (descending)
        public void sortByMarks() {
            if (head == null || head.next == null) return;

            boolean swapped;
            do {
                swapped = false;
                Node current = head;
                while (current.next != null) {
                    if (current.data.marks < current.next.data.marks) {
                        // Swap data
                        Student temp = current.data;
                        current.data = current.next.data;
                        current.next.data = temp;
                        swapped = true;
                    }
                    current = current.next;
                }
            } while (swapped);
        }

        // Get all students as list
        public java.util.List<Student> getAllStudents() {
            java.util.List<Student> list = new ArrayList<>();
            Node temp = head;
            while (temp != null) {
                list.add(temp.data);
                temp = temp.next;
            }
            return list;
        }

        // Check if student exists by ID
        public boolean exists(int id) {
            return search(id) != null;
        }

        // Get size
        public int size() {
            int count = 0;
            Node temp = head;
            while (temp != null) {
                count++;
                temp = temp.next;
            }
            return count;
        }
    }

    // File handling
    static class FileManager {
        private static final String FILE_NAME = "students.txt";

        public static void saveToFile(StudentLinkedList list) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
                for (Student student : list.getAllStudents()) {
                    writer.println(student.id + "," + student.name + "," + student.marks);
                }
            } catch (IOException e) {
                System.out.println("Error saving to file: " + e.getMessage());
            }
        }

        public static void loadFromFile(StudentLinkedList list) {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        double marks = Double.parseDouble(parts[2]);
                        list.insert(new Student(id, name, marks));
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading from file: " + e.getMessage());
            }
        }
    }

    // Main method - Console version
    private static StudentLinkedList students;
    private static Scanner scanner;

    public static void main(String[] args) {
        students = new StudentLinkedList();
        scanner = new Scanner(System.in);

        // Load existing data
        FileManager.loadFromFile(students);

        displayWelcome();
        mainMenu();
    }

    private static void displayWelcome() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    SMART STUDENT MANAGEMENT SYSTEM");
        System.out.println("=".repeat(50) + "\n");
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n" + "-".repeat(50));
            System.out.println("                    MAIN MENU");
            System.out.println("-".repeat(50));
            System.out.println("1. Insert Student");
            System.out.println("2. Delete Student");
            System.out.println("3. Search Student by ID");
            System.out.println("4. Sort Students by Marks");
            System.out.println("5. View All Students");
            System.out.println("6. Save and Exit");
            System.out.println("-".repeat(50));
            System.out.print("Enter your choice (1-6): ");

            int choice = getValidInput(1, 6);

            switch (choice) {
                case 1:
                    insertStudent();
                    break;
                case 2:
                    deleteStudent();
                    break;
                case 3:
                    searchStudent();
                    break;
                case 4:
                    sortStudents();
                    break;
                case 5:
                    viewAllStudents();
                    break;
                case 6:
                    saveAndExit();
                    return;
            }
        }
    }

    private static void insertStudent() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                  INSERT STUDENT");
        System.out.println("=".repeat(50));

        System.out.print("Enter Student ID: ");
        int id = getValidInteger();

        if (students.exists(id)) {
            System.out.println("❌ Student with ID " + id + " already exists!");
            return;
        }

        scanner.nextLine(); // Clear buffer
        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty!");
            return;
        }

        System.out.print("Enter Marks (0-100): ");
        double marks = getValidDouble();

        if (marks < 0 || marks > 100) {
            System.out.println("❌ Marks must be between 0 and 100!");
            return;
        }

        students.insert(new Student(id, name, marks));
        System.out.println("✅ Student added successfully!");
    }

    private static void deleteStudent() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                 DELETE STUDENT");
        System.out.println("=".repeat(50));

        System.out.print("Enter Student ID to delete: ");
        int id = getValidInteger();

        if (students.delete(id)) {
            System.out.println("✅ Student deleted successfully!");
        } else {
            System.out.println("❌ Student with ID " + id + " not found!");
        }
    }

    private static void searchStudent() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                SEARCH STUDENT");
        System.out.println("=".repeat(50));

        System.out.print("Enter Student ID to search: ");
        int id = getValidInteger();

        Student student = students.search(id);
        if (student != null) {
            System.out.println("\n✅ Student found:");
            System.out.println("-".repeat(50));
            System.out.println(student);
            System.out.println("-".repeat(50));
        } else {
            System.out.println("❌ Student with ID " + id + " not found!");
        }
    }

    private static void sortStudents() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              SORT BY MARKS");
        System.out.println("=".repeat(50));

        if (students.size() == 0) {
            System.out.println("❌ No students to sort!");
            return;
        }

        students.sortByMarks();
        System.out.println("✅ Students sorted by marks (Descending):\n");

        for (Student s : students.getAllStudents()) {
            System.out.println(s);
        }
    }

    private static void viewAllStudents() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("               ALL STUDENTS");
        System.out.println("=".repeat(50));

        if (students.size() == 0) {
            System.out.println("No students in the system.");
            return;
        }

        for (Student s : students.getAllStudents()) {
            System.out.println(s);
        }
        System.out.println("\nTotal Students: " + students.size());
    }

    private static void saveAndExit() {
        FileManager.saveToFile(students);
        System.out.println("\n✅ Data saved successfully!");
        System.out.println("Thank you for using Smart Student Management System!");
        scanner.close();
    }

    private static int getValidInteger() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine();
            System.out.println("Invalid input! Please enter a number.");
            return getValidInteger();
        }
    }

    private static double getValidDouble() {
        try {
            return scanner.nextDouble();
        } catch (InputMismatchException e) {
            scanner.nextLine();
            System.out.println("Invalid input! Please enter a valid number.");
            return getValidDouble();
        }
    }

    private static int getValidInput(int min, int max) {
        try {
            int input = scanner.nextInt();
            if (input < min || input > max) {
                System.out.println("Please enter a number between " + min + " and " + max);
                return getValidInput(min, max);
            }
            return input;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            System.out.println("Invalid input! Please enter a number.");
            return getValidInput(min, max);
        }
    }
}
