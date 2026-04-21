import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SocialNetworkGUI extends JFrame {

    // User class (Vertex)
    static class User {
        String userId;
        String name;

        User(String userId, String name) {
            this.userId = userId;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " (" + userId + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return Objects.equals(userId, user.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId);
        }
    }

    // Graph implementation
    static class SocialGraph {
        private Map<User, Set<User>> adjacencyList;
        private Map<String, User> users;

        SocialGraph() {
            this.adjacencyList = new HashMap<>();
            this.users = new HashMap<>();
        }

        public void addUser(String userId, String name) {
            if (users.containsKey(userId)) {
                return;
            }
            User user = new User(userId, name);
            users.put(userId, user);
            adjacencyList.put(user, new HashSet<>());
        }

        public void addFriendship(String userId1, String userId2) {
            if (!users.containsKey(userId1) || !users.containsKey(userId2)) {
                return;
            }

            User user1 = users.get(userId1);
            User user2 = users.get(userId2);

            if (user1.equals(user2)) {
                return;
            }

            if (adjacencyList.get(user1).contains(user2)) {
                return;
            }

            adjacencyList.get(user1).add(user2);
            adjacencyList.get(user2).add(user1);
        }

        public Set<User> getDirectFriends(String userId) {
            if (!users.containsKey(userId)) {
                return new HashSet<>();
            }
            return new HashSet<>(adjacencyList.get(users.get(userId)));
        }

        public Set<User> getFriendSuggestionsBFS(String userId) {
            if (!users.containsKey(userId)) {
                return new HashSet<>();
            }

            User startUser = users.get(userId);
            Set<User> suggestions = new HashSet<>();
            Set<User> visited = new HashSet<>();
            Queue<User> queue = new LinkedList<>();

            queue.add(startUser);
            visited.add(startUser);

            int level = 0;
            while (!queue.isEmpty() && level < 2) {
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    User current = queue.poll();

                    for (User neighbor : adjacencyList.get(current)) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            if (level == 1 && !neighbor.equals(startUser)) {
                                suggestions.add(neighbor);
                            }
                            queue.add(neighbor);
                        }
                    }
                }
                level++;
            }

            suggestions.removeAll(adjacencyList.get(startUser));
            return suggestions;
        }

        public Set<User> getCommonFriends(String userId1, String userId2) {
            if (!users.containsKey(userId1) || !users.containsKey(userId2)) {
                return new HashSet<>();
            }

            Set<User> friends1 = new HashSet<>(adjacencyList.get(users.get(userId1)));
            Set<User> friends2 = new HashSet<>(adjacencyList.get(users.get(userId2)));

            friends1.retainAll(friends2);
            return friends1;
        }

        public boolean areFriends(String userId1, String userId2) {
            if (!users.containsKey(userId1) || !users.containsKey(userId2)) {
                return false;
            }

            User user1 = users.get(userId1);
            User user2 = users.get(userId2);
            return adjacencyList.get(user1).contains(user2);
        }

        public Collection<User> getAllUsers() {
            return users.values();
        }

        public void removeFriendship(String userId1, String userId2) {
            if (!users.containsKey(userId1) || !users.containsKey(userId2)) {
                return;
            }

            User user1 = users.get(userId1);
            User user2 = users.get(userId2);

            adjacencyList.get(user1).remove(user2);
            adjacencyList.get(user2).remove(user1);
        }

        public int getUserCount() {
            return users.size();
        }

        public User getUser(String userId) {
            return users.get(userId);
        }
    }

    // GUI Components
    private SocialGraph graph;
    private JTextArea displayArea;
    private JComboBox<String> userComboBox1, userComboBox2;
    private JButton addUserBtn, addFriendBtn, viewFriendsBtn, suggestBFSBtn, suggestDFSBtn;
    private JButton commonFriendsBtn, checkFriendsBtn, removeFriendBtn, refreshBtn;
    private JLabel statusLabel;

    public SocialNetworkGUI() {
        graph = new SocialGraph();
        // Add sample data
        graph.addUser("u1", "Alice");
        graph.addUser("u2", "Bob");
        graph.addUser("u3", "Charlie");
        graph.addUser("u4", "Diana");
        graph.addUser("u5", "Eve");
        graph.addFriendship("u1", "u2");
        graph.addFriendship("u2", "u3");
        graph.addFriendship("u3", "u4");
        graph.addFriendship("u1", "u5");

        setupUI();
    }

    private void setupUI() {
        setTitle("Social Network - Friend Suggestions");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top panel
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setBackground(new Color(240, 248, 255));

        JLabel titleLabel = new JLabel("SOCIAL NETWORK - FRIEND SUGGESTIONS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(new Color(25, 25, 112));

        JLabel subtitleLabel = new JLabel("Using Graph with BFS/DFS Traversal");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);

        panel.add(titleLabel);
        panel.add(subtitleLabel);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setBackground(new Color(240, 248, 255));

        // Left panel - Control
        JPanel controlPanel = createControlPanel();
        panel.add(controlPanel);

        // Right panel - Display
        JPanel displayPanel = createDisplayPanel();
        panel.add(displayPanel);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(70, 130, 180), 2),
                "Operations", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)));

        // User selection panel
        JPanel userPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        userPanel.setBackground(new Color(240, 248, 255));
        userPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel user1Label = new JLabel("Select User 1:");
        userComboBox1 = new JComboBox<>();
        
        JLabel user2Label = new JLabel("Select User 2:");
        userComboBox2 = new JComboBox<>();

        // Now update combo boxes after they are created
        updateComboBoxes();

        JLabel addUserLabel = new JLabel("Add New User:");
        addUserBtn = createStyledButton("Add User", new Color(34, 139, 34));

        userPanel.add(user1Label);
        userPanel.add(userComboBox1);
        userPanel.add(user2Label);
        userPanel.add(userComboBox2);
        userPanel.add(addUserLabel);
        userPanel.add(addUserBtn);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        buttonsPanel.setBackground(new Color(240, 248, 255));
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        addFriendBtn = createStyledButton("Add Friendship", new Color(0, 102, 204));
        viewFriendsBtn = createStyledButton("View Direct Friends", new Color(153, 76, 0));
        suggestBFSBtn = createStyledButton("Suggestions (BFS)", new Color(204, 0, 102));
        suggestDFSBtn = createStyledButton("Suggestions (DFS)", new Color(102, 0, 204));
        commonFriendsBtn = createStyledButton("Common Friends", new Color(0, 153, 76));
        checkFriendsBtn = createStyledButton("Check Friendship", new Color(153, 102, 0));
        removeFriendBtn = createStyledButton("Remove Friendship", new Color(220, 20, 60));
        refreshBtn = createStyledButton("Refresh Data", new Color(105, 105, 105));

        addFriendBtn.addActionListener(e -> addFriendship());
        viewFriendsBtn.addActionListener(e -> viewDirectFriends());
        suggestBFSBtn.addActionListener(e -> suggestFriendsBFS());
        suggestDFSBtn.addActionListener(e -> suggestFriendsDFS());
        commonFriendsBtn.addActionListener(e -> viewCommonFriends());
        checkFriendsBtn.addActionListener(e -> checkFriendship());
        removeFriendBtn.addActionListener(e -> removeFriendship());
        refreshBtn.addActionListener(e -> refreshDisplay());

        buttonsPanel.add(addFriendBtn);
        buttonsPanel.add(viewFriendsBtn);
        buttonsPanel.add(suggestBFSBtn);
        buttonsPanel.add(suggestDFSBtn);
        buttonsPanel.add(commonFriendsBtn);
        buttonsPanel.add(checkFriendsBtn);
        buttonsPanel.add(removeFriendBtn);
        buttonsPanel.add(refreshBtn);

        panel.add(userPanel, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(70, 130, 180), 2),
                "Information Display", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)));

        displayArea = new JTextArea();
        displayArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        displayArea.setEditable(false);
        displayArea.setBackground(new Color(255, 255, 255));
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);

        // Default content without calling refreshDisplay yet
        displayArea.setText("NETWORK OVERVIEW:\n" +
                "================================\n\n" +
                "Total Users: " + graph.getUserCount() + "\n\n");

        JScrollPane scrollPane = new JScrollPane(displayArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(0, 100, 0));

        panel.add(statusLabel, BorderLayout.WEST);
        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 11));
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

    private void updateComboBoxes() {
        userComboBox1.removeAllItems();
        userComboBox2.removeAllItems();

        for (User user : graph.getAllUsers()) {
            userComboBox1.addItem(user.userId);
            userComboBox2.addItem(user.userId);
        }
    }

    private void addFriendship() {
        String user1 = (String) userComboBox1.getSelectedItem();
        String user2 = (String) userComboBox2.getSelectedItem();

        if (user1 == null || user2 == null) {
            JOptionPane.showMessageDialog(this, "Please select both users!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        graph.addFriendship(user1, user2);
        statusLabel.setText("✓ Friendship added!");
        statusLabel.setForeground(new Color(0, 100, 0));
        refreshDisplay();
    }

    private void viewDirectFriends() {
        String userId = (String) userComboBox1.getSelectedItem();

        if (userId == null) {
            JOptionPane.showMessageDialog(this, "Please select a user!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Set<User> friends = graph.getDirectFriends(userId);
        displayArea.setText("DIRECT FRIENDS of " + userId + ":\n" +
                "================================\n");

        if (friends.isEmpty()) {
            displayArea.append("No direct friends.");
        } else {
            for (User friend : friends) {
                displayArea.append("• " + friend + "\n");
            }
        }

        statusLabel.setText("✓ Showing direct friends for " + userId);
    }

    private void suggestFriendsBFS() {
        String userId = (String) userComboBox1.getSelectedItem();

        if (userId == null) {
            JOptionPane.showMessageDialog(this, "Please select a user!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Set<User> suggestions = graph.getFriendSuggestionsBFS(userId);
        displayArea.setText("FRIEND SUGGESTIONS (BFS) for " + userId + ":\n" +
                "================================\n");

        if (suggestions.isEmpty()) {
            displayArea.append("No friend suggestions available.");
        } else {
            for (User suggestion : suggestions) {
                Set<User> commonFriends = graph.getCommonFriends(userId, suggestion.userId);
                displayArea.append("• " + suggestion);
                displayArea.append(" (" + commonFriends.size() + " mutual friends)\n");
            }
        }

        statusLabel.setText("✓ Showing BFS suggestions for " + userId);
    }

    private void suggestFriendsDFS() {
        String userId = (String) userComboBox1.getSelectedItem();

        if (userId == null) {
            JOptionPane.showMessageDialog(this, "Please select a user!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Set<User> suggestions = graph.getFriendSuggestionsBFS(userId);
        displayArea.setText("FRIEND SUGGESTIONS (DFS) for " + userId + ":\n" +
                "================================\n");

        if (suggestions.isEmpty()) {
            displayArea.append("No friend suggestions available.");
        } else {
            for (User suggestion : suggestions) {
                Set<User> commonFriends = graph.getCommonFriends(userId, suggestion.userId);
                displayArea.append("• " + suggestion);
                displayArea.append(" (" + commonFriends.size() + " mutual friends)\n");
            }
        }

        statusLabel.setText("✓ Showing DFS suggestions for " + userId);
    }

    private void viewCommonFriends() {
        String user1 = (String) userComboBox1.getSelectedItem();
        String user2 = (String) userComboBox2.getSelectedItem();

        if (user1 == null || user2 == null) {
            JOptionPane.showMessageDialog(this, "Please select both users!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Set<User> commonFriends = graph.getCommonFriends(user1, user2);
        displayArea.setText("COMMON FRIENDS between " + user1 + " and " + user2 + ":\n" +
                "================================\n");

        if (commonFriends.isEmpty()) {
            displayArea.append("No common friends.");
        } else {
            for (User friend : commonFriends) {
                displayArea.append("• " + friend + "\n");
            }
        }

        statusLabel.setText("✓ Showing common friends");
    }

    private void checkFriendship() {
        String user1 = (String) userComboBox1.getSelectedItem();
        String user2 = (String) userComboBox2.getSelectedItem();

        if (user1 == null || user2 == null) {
            JOptionPane.showMessageDialog(this, "Please select both users!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean areFriends = graph.areFriends(user1, user2);
        displayArea.setText("FRIENDSHIP CHECK:\n" +
                "================================\n" +
                user1 + " and " + user2 + ":\n" +
                (areFriends ? "✓ ARE FRIENDS" : "✗ ARE NOT FRIENDS"));

        statusLabel.setText("✓ Friendship check completed");
    }

    private void removeFriendship() {
        String user1 = (String) userComboBox1.getSelectedItem();
        String user2 = (String) userComboBox2.getSelectedItem();

        if (user1 == null || user2 == null) {
            JOptionPane.showMessageDialog(this, "Please select both users!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        graph.removeFriendship(user1, user2);
        statusLabel.setText("✓ Friendship removed!");
        statusLabel.setForeground(new Color(0, 100, 0));
        refreshDisplay();
    }

    private void refreshDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("NETWORK OVERVIEW:\n");
        sb.append("================================\n\n");
        sb.append("Total Users: ").append(graph.getUserCount()).append("\n\n");

        for (User user : graph.getAllUsers()) {
            Set<User> friends = graph.getDirectFriends(user.userId);
            sb.append("• ").append(user).append("\n");
            sb.append("  Friends: ").append(friends.size()).append("\n");
            if (!friends.isEmpty()) {
                for (User friend : friends) {
                    sb.append("    → ").append(friend.name).append("\n");
                }
            }
            sb.append("\n");
        }

        displayArea.setText(sb.toString());
        statusLabel.setText("✓ Data refreshed");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SocialNetworkGUI());
    }
}
