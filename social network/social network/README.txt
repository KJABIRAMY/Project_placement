SOCIAL NETWORK - FRIEND SUGGESTIONS SYSTEM
===========================================

A Graph-based friend suggestions system using BFS and DFS traversal algorithms.

FEATURES:
---------
1. ADD USER
   - Create new users (vertices) in the social network
   - Each user has a unique ID and name

2. ADD FRIENDSHIP
   - Create bidirectional edges between users
   - Friendship links are automatically maintained both ways

3. VIEW DIRECT FRIENDS
   - See all direct friends (1st degree connections)
   - Shows immediate connections only

4. FRIEND SUGGESTIONS (BFS)
   - Uses Breadth-First Search to find friends of friends
   - Implements level-by-level traversal
   - Shows potential friends (2nd degree connections)
   - Displays mutual friend count

5. FRIEND SUGGESTIONS (DFS)
   - Uses Depth-First Search for traversal
   - Alternative approach to finding friend suggestions
   - Same results but different traversal order

6. COMMON FRIENDS
   - Find mutual connections between two users
   - Shows shared friend list

7. CHECK FRIENDSHIP
   - Verify if two users are direct friends
   - Quick lookup using adjacency list

8. REMOVE FRIENDSHIP
   - Delete friendship between two users
   - Maintains graph integrity

DATA STRUCTURE:
---------------
- Graph Implementation using Adjacency List
- Vertices: Users (with ID and Name)
- Edges: Bidirectional friendships
- Time Complexity:
  * Add User: O(1)
  * Add Friendship: O(1)
  * Search Friend: O(V + E) using BFS/DFS
  * Common Friends: O(min(F1, F2)) where F1, F2 are friend counts

ALGORITHMS:
-----------
BFS (Breadth-First Search):
- Explores level by level
- Finds shortest path to nodes
- Good for finding 2nd degree connections
- Uses Queue for traversal

DFS (Depth-First Search):
- Explores depth first
- Uses recursion or stack
- Alternative traversal method
- Same results, different order

USAGE:
------

GUI VERSION (Recommended):
1. Compile: javac SocialNetworkGUI.java
2. Run: java SocialNetworkGUI
3. Pre-loaded with sample users: Alice, Bob, Charlie, Diana, Eve
4. Use dropdowns to select users
5. Click buttons to perform operations

CONSOLE VERSION:
1. Compile: javac SocialNetworkFriendSuggestions.java
2. Run: java SocialNetworkFriendSuggestions
3. Follow the menu-driven interface
4. Enter user IDs when prompted

SAMPLE DATA:
-----------
Users:
- u1: Alice
- u2: Bob
- u3: Charlie
- u4: Diana
- u5: Eve

Friendships:
- Alice ↔ Bob
- Bob ↔ Charlie
- Charlie ↔ Diana
- Alice ↔ Eve

COMPLEXITY ANALYSIS:
-------------------
Operation          | Time Complexity | Space Complexity
---------------------------------------------
Add User          | O(1)            | O(1)
Add Friendship    | O(1)            | O(1)
BFS Traversal     | O(V + E)        | O(V)
DFS Traversal     | O(V + E)        | O(V)
Search User       | O(1)            | O(1)
Common Friends    | O(min(F1,F2))   | O(min(F1,F2))

Where:
- V = Number of vertices (users)
- E = Number of edges (friendships)
- F = Number of friends for a user
