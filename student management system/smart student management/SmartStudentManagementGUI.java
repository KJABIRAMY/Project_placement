import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SmartStudentManagementGUI extends JFrame {

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

        public void sortByMarks() {
            if (head == null || head.next == null) return;

            boolean swapped;
            do {
                swapped = false;
                Node current = head;
                while (current.next != null) {
                    if (current.data.marks < current.next.data.marks) {
                        Student temp = current.data;
                        current.data = current.next.data;
                        current.next.data = temp;
                        swapped = true;
                    }
                    current = current.next;
                }
            } while (swapped);
        }

        public java.util.List<Student> getAllStudents() {
            java.util.List<Student> list = new ArrayList<>();
            Node temp = head;
            while (temp != null) {
                list.add(temp.data);
                temp = temp.next;
            }
            return list;
        }

        public boolean exists(int id) {
            return search(id) != null;
        }

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

    // GUI Components
    private StudentLinkedList students;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JButton insertBtn, deleteBtn, searchBtn, sortBtn, refreshBtn;

    public SmartStudentManagementGUI() {
        students = new StudentLinkedList();
        FileManager.loadFromFile(students);
        setupUI();
    }

    private void setupUI() {
        setTitle("Smart Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top panel with title
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel with table
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setBackground(new Color(240, 248, 255));

        JLabel titleLabel = new JLabel("SMART STUDENT MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(new Color(25, 25, 112));

        JLabel countLabel = new JLabel("Total Students: " + students.size());
        countLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        countLabel.setHorizontalAlignment(JLabel.CENTER);

        panel.add(titleLabel);
        panel.add(countLabel);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(70, 130, 180), 2),
                "Student Records", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(70, 130, 180)));

        // Create table
        String[] columns = {"ID", "Name", "Marks"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(new Color(70, 130, 180));
        studentTable.getTableHeader().setForeground(Color.WHITE);

        updateTable();

        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(240, 248, 255));

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        buttonPanel.setBackground(new Color(240, 248, 255));

        insertBtn = createStyledButton("Insert", new Color(34, 139, 34));
        deleteBtn = createStyledButton("Delete", new Color(220, 20, 60));
        searchBtn = createStyledButton("Search", new Color(70, 130, 180));
        sortBtn = createStyledButton("Sort by Marks", new Color(184, 134, 11));
        refreshBtn = createStyledButton("Refresh", new Color(105, 105, 105));

        insertBtn.addActionListener(e -> insertStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        searchBtn.addActionListener(e -> searchStudent());
        sortBtn.addActionListener(e -> sortStudent());
        refreshBtn.addActionListener(e -> updateTable());

        buttonPanel.add(insertBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(sortBtn);
        buttonPanel.add(refreshBtn);

        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(240, 248, 255));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(0, 100, 0));
        statusPanel.add(statusLabel);

        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(statusPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void insertStudent() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel idLabel = new JLabel("Student ID:");
        JTextField idField = new JTextField();

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel marksLabel = new JLabel("Marks (0-100):");
        JTextField marksField = new JTextField();

        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(marksLabel);
        panel.add(marksField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Insert New Student",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                double marks = Double.parseDouble(marksField.getText().trim());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name cannot be empty!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (marks < 0 || marks > 100) {
                    JOptionPane.showMessageDialog(this, "Marks must be between 0 and 100!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (students.exists(id)) {
                    JOptionPane.showMessageDialog(this, "Student with ID " + id + " already exists!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                students.insert(new Student(id, name, marks));
                FileManager.saveToFile(students);
                updateTable();
                statusLabel.setText("✓ Student added successfully!");
                statusLabel.setForeground(new Color(0, 100, 0));

                JOptionPane.showMessageDialog(this, "Student added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid data!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteStudent() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Student ID to delete:");

        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);

            Student student = students.search(id);
            if (student == null) {
                JOptionPane.showMessageDialog(this, "Student with ID " + id + " not found!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete this student?\n\n" + student,
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                students.delete(id);
                FileManager.saveToFile(students);
                updateTable();
                statusLabel.setText("✓ Student deleted successfully!");
                statusLabel.setForeground(new Color(0, 100, 0));

                JOptionPane.showMessageDialog(this, "Student deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid ID!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStudent() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Student ID to search:");

        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            Student student = students.search(id);

            if (student != null) {
                JOptionPane.showMessageDialog(this,
                        student.toString(),
                        "Student Found", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Student with ID " + id + " not found!",
                        "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid ID!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sortStudent() {
        if (students.size() == 0) {
            JOptionPane.showMessageDialog(this, "No students to sort!",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        students.sortByMarks();
        FileManager.saveToFile(students);
        updateTable();
        statusLabel.setText("✓ Students sorted by marks (Descending)!");
        statusLabel.setForeground(new Color(0, 100, 0));

        JOptionPane.showMessageDialog(this, "Students sorted by marks (Descending)!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateTable() {
        tableModel.setRowCount(0);

        for (Student student : students.getAllStudents()) {
            tableModel.addRow(new Object[]{
                    student.id,
                    student.name,
                    student.marks
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartStudentManagementGUI());
    }
}
