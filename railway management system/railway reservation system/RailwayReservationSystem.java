import java.util.*;

public class RailwayReservationSystem {
    private static final int TOTAL_SEATS = 50;
    private static boolean[] seats;
    private static Map<Integer, Ticket> bookings;
    private static int ticketCounter;
    private static Scanner scanner;

    // Ticket class to store booking information
    static class Ticket {
        int ticketId;
        String passengerName;
        int seatNumber;
        double price;

        Ticket(int ticketId, String name, int seat, double price) {
            this.ticketId = ticketId;
            this.passengerName = name;
            this.seatNumber = seat;
            this.price = price;
        }

        @Override
        public String toString() {
            return "Ticket ID: " + ticketId + "\nPassenger: " + passengerName +
                    "\nSeat Number: " + seatNumber + "\nPrice: Rs." + price;
        }
    }

    public static void main(String[] args) {
        initializeSystem();
        displayWelcome();
        mainMenu();
    }

    // Initialize the system
    private static void initializeSystem() {
        seats = new boolean[TOTAL_SEATS];
        bookings = new HashMap<>();
        ticketCounter = 1000;
        scanner = new Scanner(System.in);
        Arrays.fill(seats, false); // false = available, true = booked
    }

    // Display welcome message
    private static void displayWelcome() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    WELCOME TO RAILWAY RESERVATION SYSTEM");
        System.out.println("=".repeat(50) + "\n");
    }

    // Main menu
    private static void mainMenu() {
        while (true) {
            System.out.println("\n" + "-".repeat(50));
            System.out.println("                    MAIN MENU");
            System.out.println("-".repeat(50));
            System.out.println("1. Book Ticket");
            System.out.println("2. Cancel Ticket");
            System.out.println("3. View Seats");
            System.out.println("4. View My Bookings");
            System.out.println("5. Exit");
            System.out.println("-".repeat(50));
            System.out.print("Enter your choice (1-5): ");

            int choice = getValidInput(1, 5);

            switch (choice) {
                case 1:
                    bookTicket();
                    break;
                case 2:
                    cancelTicket();
                    break;
                case 3:
                    viewSeats();
                    break;
                case 4:
                    viewBookings();
                    break;
                case 5:
                    exitSystem();
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    // Book ticket
    private static void bookTicket() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                  BOOK TICKET");
        System.out.println("=".repeat(50));

        // Get passenger name
        scanner.nextLine(); // Clear buffer
        System.out.print("Enter Passenger Name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Invalid name! Booking cancelled.");
            return;
        }

        // Check available seats
        int availableCount = countAvailableSeats();
        if (availableCount == 0) {
            System.out.println("\n❌ Sorry! No seats available.");
            return;
        }

        System.out.println("\nAvailable seats: " + availableCount + "/" + TOTAL_SEATS);
        viewSeatsStatus();

        System.out.print("\nEnter seat number (1-" + TOTAL_SEATS + "): ");
        int seatNumber = getValidInput(1, TOTAL_SEATS);

        if (seats[seatNumber - 1]) {
            System.out.println("\n❌ Seat " + seatNumber + " is already booked!");
            return;
        }

        // Book the seat
        seats[seatNumber - 1] = true;
        double price = 500.00; // Fixed price per seat
        int ticketId = ticketCounter++;

        Ticket ticket = new Ticket(ticketId, name, seatNumber, price);
        bookings.put(ticketId, ticket);

        System.out.println("\n✅ Ticket booked successfully!");
        System.out.println("-".repeat(50));
        System.out.println(ticket);
        System.out.println("-".repeat(50));
    }

    // Cancel ticket
    private static void cancelTicket() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                 CANCEL TICKET");
        System.out.println("=".repeat(50));

        if (bookings.isEmpty()) {
            System.out.println("No bookings found!");
            return;
        }

        System.out.print("Enter Ticket ID to cancel: ");
        int ticketId = getValidInteger();

        if (!bookings.containsKey(ticketId)) {
            System.out.println("❌ Ticket ID not found!");
            return;
        }

        Ticket ticket = bookings.get(ticketId);
        System.out.println("\nTicket Details:");
        System.out.println("-".repeat(50));
        System.out.println(ticket);
        System.out.println("-".repeat(50));

        System.out.print("Confirm cancellation? (yes/no): ");
        scanner.nextLine(); // Clear buffer
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            // Release the seat
            seats[ticket.seatNumber - 1] = false;
            bookings.remove(ticketId);

            double refund = ticket.price * 0.9; // 10% cancellation charge
            System.out.println("\n✅ Ticket cancelled successfully!");
            System.out.println("Refund amount: Rs." + refund);
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    // View seats
    private static void viewSeats() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                  VIEW SEATS");
        System.out.println("=".repeat(50));

        int available = countAvailableSeats();
        int booked = TOTAL_SEATS - available;

        System.out.println("\nSeat Status:");
        System.out.println("✓ Available: " + available + " seats");
        System.out.println("✗ Booked: " + booked + " seats");
        System.out.println("-".repeat(50));

        viewSeatsStatus();
    }

    // Display seats in a visual format
    private static void viewSeatsStatus() {
        System.out.println("\nSeat Layout:");
        System.out.println("(✓ = Available, ✗ = Booked)\n");

        for (int i = 0; i < TOTAL_SEATS; i++) {
            if (i % 10 == 0 && i != 0) {
                System.out.println();
            }
            System.out.print("[" + (seats[i] ? "✗" : "✓") + "]");
        }
        System.out.println("\n");
    }

    // View all bookings
    private static void viewBookings() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                 MY BOOKINGS");
        System.out.println("=".repeat(50));

        if (bookings.isEmpty()) {
            System.out.println("No bookings found!");
            return;
        }

        System.out.println("\nTotal Bookings: " + bookings.size());
        System.out.println("-".repeat(50));

        for (Ticket ticket : bookings.values()) {
            System.out.println(ticket);
            System.out.println("-".repeat(50));
        }
    }

    // Count available seats
    private static int countAvailableSeats() {
        int count = 0;
        for (boolean seat : seats) {
            if (!seat) count++;
        }
        return count;
    }

    // Get valid integer input
    private static int getValidInteger() {
        try {
            scanner.nextLine(); // Clear buffer
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Clear invalid input
            System.out.println("Invalid input! Please enter a number.");
            return getValidInteger();
        }
    }

    // Get valid input within range
    private static int getValidInput(int min, int max) {
        try {
            int input = scanner.nextInt();
            if (input < min || input > max) {
                System.out.println("Please enter a number between " + min + " and " + max);
                return getValidInput(min, max);
            }
            return input;
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Clear invalid input
            System.out.println("Invalid input! Please enter a number.");
            return getValidInput(min, max);
        }
    }

    // Exit system
    private static void exitSystem() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Thank you for using Railway Reservation System!");
        System.out.println("=".repeat(50) + "\n");
        scanner.close();
    }
}
