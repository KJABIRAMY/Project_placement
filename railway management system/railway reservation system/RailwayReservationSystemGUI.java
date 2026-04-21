import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class RailwayReservationSystemGUI extends JFrame {
    private static final int TOTAL_SEATS = 50;
    private static boolean[] seats;
    private static Map<Integer, Ticket> bookings;
    private static int ticketCounter;

    // Ticket class
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
            return "Ticket ID: " + ticketId + " | Passenger: " + passengerName +
                    " | Seat: " + seatNumber + " | Price: Rs." + price;
        }
    }

    // GUI Components
    private JPanel mainPanel;
    private JLabel statusLabel;
    private JTextArea seatDisplayArea;
    private JTextArea bookingInfoArea;
    private JButton bookButton, cancelButton, viewSeatsButton, viewBookingsButton;
    private JLabel availableSeatsLabel;

    public RailwayReservationSystemGUI() {
        initializeSystem();
        setupUI();
    }

    // Initialize the system
    private void initializeSystem() {
        seats = new boolean[TOTAL_SEATS];
        bookings = new HashMap<>();
        ticketCounter = 1000;
        Arrays.fill(seats, false);
    }

    // Setup GUI
    private void setupUI() {
        setTitle("Railway Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Panel - Title
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel - Seat Display and Info
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // South Panel - Buttons
        JPanel southPanel = createSouthPanel();
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // Create top panel with title and status
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setBackground(new Color(240, 248, 255));

        JLabel titleLabel = new JLabel("RAILWAY RESERVATION SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(new Color(25, 25, 112));

        availableSeatsLabel = new JLabel();
        availableSeatsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        availableSeatsLabel.setHorizontalAlignment(JLabel.CENTER);
        updateAvailableSeatsLabel();

        panel.add(titleLabel);
        panel.add(availableSeatsLabel);
        return panel;
    }

    // Create center panel with seat display
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setBackground(new Color(240, 248, 255));

        // Left - Seat Display
        JPanel seatPanel = new JPanel(new BorderLayout(0, 10));
        seatPanel.setBackground(new Color(240, 248, 255));
        seatPanel.setBorder(new TitledBorder(new LineBorder(new Color(70, 130, 180), 2),
                "Seat Layout", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(70, 130, 180)));

        seatDisplayArea = new JTextArea(15, 30);
        seatDisplayArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        seatDisplayArea.setEditable(false);
        seatDisplayArea.setBackground(new Color(255, 255, 255));
        seatDisplayArea.setForeground(new Color(0, 0, 0));
        updateSeatDisplay();

        JScrollPane seatScroll = new JScrollPane(seatDisplayArea);
        seatPanel.add(seatScroll, BorderLayout.CENTER);

        // Right - Booking Info
        JPanel infoPanel = new JPanel(new BorderLayout(0, 10));
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(new TitledBorder(new LineBorder(new Color(70, 130, 180), 2),
                "Booking Information", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(70, 130, 180)));

        bookingInfoArea = new JTextArea(15, 30);
        bookingInfoArea.setFont(new Font("Arial", Font.PLAIN, 11));
        bookingInfoArea.setEditable(false);
        bookingInfoArea.setBackground(new Color(255, 255, 255));
        bookingInfoArea.setLineWrap(true);
        bookingInfoArea.setWrapStyleWord(true);

        JScrollPane infoScroll = new JScrollPane(bookingInfoArea);
        infoPanel.add(infoScroll, BorderLayout.CENTER);

        panel.add(seatPanel);
        panel.add(infoPanel);
        return panel;
    }

    // Create south panel with buttons
    private JPanel createSouthPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(240, 248, 255));

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        buttonPanel.setBackground(new Color(240, 248, 255));

        bookButton = createStyledButton("Book Ticket", new Color(34, 139, 34));
        cancelButton = createStyledButton("Cancel Ticket", new Color(220, 20, 60));
        viewSeatsButton = createStyledButton("View Seats", new Color(70, 130, 180));
        viewBookingsButton = createStyledButton("View Bookings", new Color(184, 134, 11));

        bookButton.addActionListener(e -> bookTicket());
        cancelButton.addActionListener(e -> cancelTicket());
        viewSeatsButton.addActionListener(e -> viewSeats());
        viewBookingsButton.addActionListener(e -> viewBookings());

        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(viewSeatsButton);
        buttonPanel.add(viewBookingsButton);

        // Status Panel
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

    // Create styled button
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(10, bgColor));
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

    // Book Ticket
    private void bookTicket() {
        // Get passenger name first
        String name = JOptionPane.showInputDialog(this, "Enter Passenger Name:");
        
        if (name == null) return;
        
        if (name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid passenger name!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show available seats and let user select
        displayAvailableSeatsAndBook(name);
    }

    // Display available seats with clickable buttons
    private void displayAvailableSeatsAndBook(String passengerName) {
        // Get available seats list
        java.util.List<Integer> availableSeats = new ArrayList<>();
        for (int i = 0; i < TOTAL_SEATS; i++) {
            if (!seats[i]) {
                availableSeats.add(i + 1);
            }
        }

        if (availableSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No seats available!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create custom panel with seat selection
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(240, 248, 255));

        // Info label
        JLabel infoLabel = new JLabel("<html>Available Seats: " + availableSeats.size() + 
                "<br>Click a button below to select a seat</html>");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(infoLabel, BorderLayout.NORTH);

        // Seat buttons panel
        JPanel seatsPanel = new JPanel(new GridLayout(0, 5, 8, 8));
        seatsPanel.setBackground(new Color(240, 248, 255));

        final int[] selectedSeat = {-1};

        for (int seatNum : availableSeats) {
            JButton seatBtn = new JButton(String.valueOf(seatNum));
            seatBtn.setBackground(new Color(34, 139, 34));
            seatBtn.setForeground(Color.WHITE);
            seatBtn.setFont(new Font("Arial", Font.BOLD, 14));
            seatBtn.setFocusPainted(false);
            seatBtn.setOpaque(true);
            seatBtn.setBorderPainted(false);
            seatBtn.setPreferredSize(new Dimension(50, 50));
            
            seatBtn.addActionListener(e -> {
                selectedSeat[0] = seatNum;
            });

            seatsPanel.add(seatBtn);
        }

        JScrollPane scrollPane = new JScrollPane(seatsPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Select Your Seat", JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && selectedSeat[0] != -1) {
            // Book the seat
            int seatNumber = selectedSeat[0];
            seats[seatNumber - 1] = true;
            double price = 500.00;
            int ticketId = ticketCounter++;
            Ticket ticket = new Ticket(ticketId, passengerName, seatNumber, price);
            bookings.put(ticketId, ticket);

            updateDisplay();
            statusLabel.setText("✓ Ticket booked successfully! (ID: " + ticketId + ")");
            statusLabel.setForeground(new Color(0, 100, 0));

            JOptionPane.showMessageDialog(this,
                    "Booking Confirmed!\n\nTicket ID: " + ticketId +
                            "\nPassenger: " + passengerName + "\nSeat: " + seatNumber +
                            "\nPrice: Rs. 500",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else if (selectedSeat[0] == -1) {
            JOptionPane.showMessageDialog(this, "Please select a seat!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Cancel Ticket
    private void cancelTicket() {
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bookings available to cancel!",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String ticketIdStr = JOptionPane.showInputDialog(this, "Enter Ticket ID to cancel:");

        if (ticketIdStr == null) return;

        try {
            int ticketId = Integer.parseInt(ticketIdStr);

            if (!bookings.containsKey(ticketId)) {
                JOptionPane.showMessageDialog(this, "Ticket ID not found!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Ticket ticket = bookings.get(ticketId);
            String message = "Ticket Details:\n\n" + ticket +
                    "\n\nConfirm cancellation?\n(10% cancellation charge will be deducted)";

            int confirm = JOptionPane.showConfirmDialog(this, message, "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                seats[ticket.seatNumber - 1] = false;
                bookings.remove(ticketId);
                double refund = ticket.price * 0.9;

                updateDisplay();
                statusLabel.setText("✓ Ticket cancelled. Refund: Rs." + refund);
                statusLabel.setForeground(new Color(0, 100, 0));

                JOptionPane.showMessageDialog(this,
                        "Cancellation Successful!\n\nRefund Amount: Rs." + refund,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid ticket ID!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // View Seats
    private void viewSeats() {
        updateSeatDisplay();
        statusLabel.setText("Seat status updated");
        statusLabel.setForeground(new Color(0, 100, 0));
    }

    // View Bookings
    private void viewBookings() {
        StringBuilder sb = new StringBuilder();

        if (bookings.isEmpty()) {
            sb.append("No bookings available.\n");
        } else {
            sb.append("Total Bookings: ").append(bookings.size()).append("\n");
            sb.append("=".repeat(60)).append("\n\n");

            for (Ticket ticket : bookings.values()) {
                sb.append(ticket).append("\n\n");
            }
        }

        bookingInfoArea.setText(sb.toString());
        statusLabel.setText("Displaying " + bookings.size() + " booking(s)");
        statusLabel.setForeground(new Color(0, 100, 0));
    }

    // Update seat display
    private void updateSeatDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("Legend: [✓] Available  [✗] Booked\n\n");

        for (int i = 0; i < TOTAL_SEATS; i++) {
            if (i % 5 == 0) {
                sb.append(String.format("Row %2d: ", (i / 5) + 1));
            }
            sb.append(seats[i] ? "[✗]" : "[✓]");

            if ((i + 1) % 5 == 0) {
                sb.append("\n");
            } else {
                sb.append(" ");
            }
        }

        seatDisplayArea.setText(sb.toString());
    }

    // Update available seats label
    private void updateAvailableSeatsLabel() {
        int available = countAvailableSeats();
        int booked = TOTAL_SEATS - available;
        availableSeatsLabel.setText(String.format("Available: %d | Booked: %d | Total: %d",
                available, booked, TOTAL_SEATS));
    }

    // Update all displays
    private void updateDisplay() {
        updateSeatDisplay();
        updateAvailableSeatsLabel();
        viewBookings();
    }

    // Count available seats
    private int countAvailableSeats() {
        int count = 0;
        for (boolean seat : seats) {
            if (!seat) count++;
        }
        return count;
    }

    // Rounded Border for buttons
    static class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RailwayReservationSystemGUI());
    }
}
