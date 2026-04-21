import java.util.*;

public class SocialNetworkFriendSuggestions {

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

    // Graph implementation (Social Network)
    static class SocialGraph {
        private Map<User, Set<User>> adjacencyList;
        private Map<String, User> users;

        SocialGraph() {
            this.adjacencyList = new HashMap<>();
            this.users = new HashMap<>();
        }

        // Add user (vertex)
        public void addUser(String userId, String name) {
            if (users.containsKey(userId)) {
                System.out.println("User already exists!");
                return;
            }
            User user = new User(userId, name);
            users.put(userId, user);
            adjacencyList.put(user, new HashSet<>());
        }

        // Add friendship (edge) - bidirectional
        public void addFriendship(String userId1, String userId2) {
            if (!users.containsKey(userId1) || !users.containsKey(userId2)) {
                System.out.println("One or both users don't exist!");
                return;
            }

            User user1 = users.get(userId1);
            User user2 = users.get(userId2);

            if (user1.equals(user2)) {
                System.out.println("Cannot add friendship with same user!");
                return;
            }

            if (adjacencyList.get(user1).contains(user2)) {
                System.out.println("Already friends!");
                return;
            }

            adjacencyList.get(user1).add(user2);
            adjacencyList.get(user2).add(user1);
        }

        // Get direct friends
        public Set<User> getDirectFriends(String userId) {
            if (!users.containsKey(userId)) {
                return new HashSet<>();
            }
            return new HashSet<>(adjacencyList.get(users.get(userId)));
        }

        // Get friend suggestions using BFS (friends of friends)
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

            // BFS traversal (2 levels deep)
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

            // Remove direct friends from suggestions
            suggestions.removeAll(adjacencyList.get(startUser));
            return suggestions;
        }

        // Get friend suggestions using DFS (friends of friends)
        public Set<User> getFriendSuggestionsDFS(String userId) {
            if (!users.containsKey(userId)) {
                return new HashSet<>();
            }

            User startUser = users.get(userId);
            Set<User> suggestions = new HashSet<>();
            Set<User> visited = new HashSet<>();

            dfs(startUser, startUser, 0, visited, suggestions);

            // Remove direct friends from suggestions
            suggestions.removeAll(adjacencyList.get(startUser));
            return suggestions;
        }

        private void dfs(User current, User startUser, int depth, Set<User> visited, Set<User> suggestions) {
            if (depth > 2) return;

            visited.add(current);

            if (depth == 2 && !current.equals(startUser)) {
                suggestions.add(current);
            }

            for (User neighbor : adjacencyList.get(current)) {
                if (!visited.contains(neighbor) && depth < 2) {
                    dfs(neighbor, startUser, depth + 1, visited, suggestions);
                }
            }
        }

        // Get common friends
        public Set<User> getCommonFriends(String userId1, String userId2) {
            if (!users.containsKey(userId1) || !users.containsKey(userId2)) {
                return new HashSet<>();
            }

            Set<User> friends1 = new HashSet<>(adjacencyList.get(users.get(userId1)));
            Set<User> friends2 = new HashSet<>(adjacencyList.get(users.get(userId2)));

            friends1.retainAll(friends2);
            return friends1;
        }

        // Check if two users are friends
        public boolean areFriends(String userId1, String userId2) {
            if (!users.containsKey(userId1) || !users.containsKey(userId2)) {
                return false;
            }

            User user1 = users.get(userId1);
            User user2 = users.get(userId2);
            return adjacencyList.get(user1).contains(user2);
        }

        // Get all users
        public Collection<User> getAllUsers() {
            return users.values();
        }

        // Remove friendship
        public void removeFriendship(String userId1, String userId2) {
            if (!users.containsKey(userId1) || !users.containsKey(userId2)) {
                System.out.println("One or both users don't exist!");
                return;
            }

            User user1 = users.get(userId1);
            User user2 = users.get(userId2);

            adjacencyList.get(user1).remove(user2);
            adjacencyList.get(user2).remove(user1);
        }

        // Get user count
        public int getUserCount() {
            return users.size();
        }
    }

    // Main method
    private static SocialGraph graph;
    private static Scanner scanner;

    public static void main(String[] args) {
        graph = new SocialGraph();
        scanner = new Scanner(System.in);

        displayWelcome();
        mainMenu();
    }

    private static void displayWelcome() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("      SOCIAL NETWORK - FRIEND SUGGESTIONS SYSTEM");
        System.out.println("      Using Graph with BFS/DFS Traversal");
        System.out.println("=".repeat(60) + "\n");
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("                    MAIN MENU");
            System.out.println("-".repeat(60));
            System.out.println("1. Add User");
            System.out.println("2. Add Friendship");
            System.out.println("3. View Direct Friends");
            System.out.println("4. Get Friend Suggestions (BFS)");
            System.out.println("5. Get Friend Suggestions (DFS)");
            System.out.println("6. View Common Friends");
            System.out.println("7. Check if Friends");
            System.out.println("8. Remove Friendship");
            System.out.println("9. View All Users");
            System.out.println("10. Exit");
            System.out.println("-".repeat(60));
            System.out.print("Enter your choice (1-10): ");

            int choice = getValidInput(1, 10);

            switch (choice) {
                case 1:
                    addUser();
                    break;
                case 2:
                    addFriendship();
                    break;
                case 3:
                    viewDirectFriends();
                    break;
                case 4:
                    getFriendSuggestionsBFS();
                    break;
                case 5:
                    getFriendSuggestionsDFS();
                    break;
                case 6:
                    viewCommonFriends();
                    break;
                case 7:
                    checkFriends();
                    break;
                case 8:
                    removeFriendship();
                    break;
                case 9:
                    viewAllUsers();
                    break;
                case 10:
                    exitSystem();
                    return;
            }
        }
    }

    private static void addUser() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                      ADD USER");
        System.out.println("=".repeat(60));

        scanner.nextLine();
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();

        System.out.print("Enter User Name: ");
        String name = scanner.nextLine().trim();

        if (userId.isEmpty() || name.isEmpty()) {
            System.out.println("❌ ID and Name cannot be empty!");
            return;
        }

        graph.addUser(userId, name);
        System.out.println("✅ User added successfully!");
    }

    private static void addFriendship() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                   ADD FRIENDSHIP");
        System.out.println("=".repeat(60));

        scanner.nextLine();
        System.out.print("Enter User ID 1: ");
        String userId1 = scanner.nextLine().trim();

        System.out.print("Enter User ID 2: ");
        String userId2 = scanner.nextLine().trim();

        graph.addFriendship(userId1, userId2);
        System.out.println("✅ Friendship added successfully!");
    }

    private static void viewDirectFriends() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                  DIRECT FRIENDS");
        System.out.println("=".repeat(60));

        scanner.nextLine();
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();

        Set<User> friends = graph.getDirectFriends(userId);

        if (friends.isEmpty()) {
            System.out.println("No direct friends found.");
            return;
        }

        System.out.println("\n✓ Direct Friends (" + friends.size() + "):");
        System.out.println("-".repeat(60));
        for (User friend : friends) {
            System.out.println("  → " + friend);
        }
    }

    private static void getFriendSuggestionsBFS() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         FRIEND SUGGESTIONS (BFS TRAVERSAL)");
        System.out.println("=".repeat(60));

        scanner.nextLine();
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();

        Set<User> suggestions = graph.getFriendSuggestionsBFS(userId);

        if (suggestions.isEmpty()) {
            System.out.println("No friend suggestions available.");
            return;
        }

        System.out.println("\n✓ Friend Suggestions (" + suggestions.size() + "):");
        System.out.println("-".repeat(60));
        for (User suggestion : suggestions) {
            Set<User> commonFriends = graph.getCommonFriends(userId, suggestion.userId);
            System.out.println("  → " + suggestion + " (" + commonFriends.size() + " mutual friends)");
        }
    }

    private static void getFriendSuggestionsDFS() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         FRIEND SUGGESTIONS (DFS TRAVERSAL)");
        System.out.println("=".repeat(60));

        scanner.nextLine();
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();

        Set<User> suggestions = graph.getFriendSuggestionsDFS(userId);

        if (suggestions.isEmpty()) {
            System.out.println("No friend suggestions available.");
            return;
        }

        System.out.println("\n✓ Friend Suggestions (" + suggestions.size() + "):");
        System.out.println("-".repeat(60));
        for (User suggestion : suggestions) {
            Set<User> commonFriends = graph.getCommonFriends(userId, suggestion.userId);
            System.out.println("  → " + suggestion + " (" + commonFriends.size() + " mutual friends)");
        }
    }

    private static void viewCommonFriends() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                  COMMON FRIENDS");
        System.out.println("=".repeat(60));

        scanner.nextLine();
        System.out.print("Enter User ID 1: ");
        String userId1 = scanner.nextLine().trim();

        System.out.print("Enter User ID 2: ");
        String userId2 = scanner.nextLine().trim();

        Set<User> commonFriends = graph.getCommonFriends(userId1, userId2);

        if (commonFriends.isEmpty()) {
            System.out.println("No common friends found.");
            return;
        }

        System.out.println("\n✓ Common Friends (" + commonFriends.size() + "):");
        System.out.println("-".repeat(60));
        for (User friend : commonFriends) {
            System.out.println("  → " + friend);
        }
    }

    private static void checkFriends() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                   CHECK FRIENDSHIP");
        System.out.println("=".repeat(60));

        scanner.nextLine();
        System.out.print("Enter User ID 1: ");
        String userId1 = scanner.nextLine().trim();

        System.out.print("Enter User ID 2: ");
        String userId2 = scanner.nextLine().trim();

        boolean areFriends = graph.areFriends(userId1, userId2);
        System.out.println("\n" + (areFriends ? "✅ They are friends!" : "❌ They are not friends!"));
    }

    private static void removeFriendship() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                 REMOVE FRIENDSHIP");
        System.out.println("=".repeat(60));

        scanner.nextLine();
        System.out.print("Enter User ID 1: ");
        String userId1 = scanner.nextLine().trim();

        System.out.print("Enter User ID 2: ");
        String userId2 = scanner.nextLine().trim();

        graph.removeFriendship(userId1, userId2);
        System.out.println("✅ Friendship removed!");
    }

    private static void viewAllUsers() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    ALL USERS");
        System.out.println("=".repeat(60));

        Collection<User> allUsers = graph.getAllUsers();

        if (allUsers.isEmpty()) {
            System.out.println("No users in the system.");
            return;
        }

        System.out.println("\nTotal Users: " + allUsers.size());
        System.out.println("-".repeat(60));
        for (User user : allUsers) {
            Set<User> friends = graph.getDirectFriends(user.userId);
            System.out.println("  " + user + " - " + friends.size() + " friends");
        }
    }

    private static void exitSystem() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Thank you for using Social Network System!");
        System.out.println("=".repeat(60) + "\n");
        scanner.close();
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
