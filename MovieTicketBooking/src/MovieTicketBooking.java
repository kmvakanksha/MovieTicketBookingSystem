import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;

public class MovieTicketBooking extends JFrame {
    private final int rows = 5;
    private final int cols = 8;
    private final JButton[][] seats = new JButton[rows][cols];
    private final HashSet<String> bookedSeats = new HashSet<>();
    private final JLabel totalLabel;
    private int ticketPrice = 150; // price per seat
    private int selectedCount = 0;
    private boolean houseFull = false; // new flag

    public MovieTicketBooking() {
        setTitle("Movie Ticket Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(650, 500);
        setLocationRelativeTo(null);

        // Seat panel
        JPanel seatPanel = new JPanel(new GridLayout(rows, cols, 5, 5));
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String seatId = (char) ('A' + i) + String.valueOf(j + 1);
                seats[i][j] = new JButton(seatId);
                seats[i][j].setBackground(Color.GREEN);
                seats[i][j].setOpaque(true);
                seats[i][j].setBorderPainted(false);

                seats[i][j].addActionListener(new SeatAction(seatId, seats[i][j]));
                seatPanel.add(seats[i][j]);
            }
        }

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel();
        totalLabel = new JLabel("BillValue (INR):");
        JButton confirmBtn = new JButton("Confirm Booking");
        JButton resetBtn = new JButton("Reset Selection");

        confirmBtn.addActionListener(e -> confirmBooking());
        resetBtn.addActionListener(e -> resetSelection());

        bottomPanel.add(totalLabel);
        bottomPanel.add(confirmBtn);
        bottomPanel.add(resetBtn);

        add(new JLabel("Select Your Seats", SwingConstants.CENTER), BorderLayout.NORTH);
        add(seatPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Handles seat clicks
    private class SeatAction implements ActionListener {
        private final String seatId;
        private final JButton button;
        private boolean selected = false;

        SeatAction(String seatId, JButton button) {
            this.seatId = seatId;
            this.button = button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (houseFull) {
                JOptionPane.showMessageDialog(null,
                        "No tickets available. Housefull!",
                        "Housefull",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // If seat is booked, allow cancellation
            if (bookedSeats.contains(seatId)) {
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        "Cancel booking for seat " + seatId + "?",
                        "Cancel Booking",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    button.setBackground(Color.GREEN);
                    bookedSeats.remove(seatId);
                    JOptionPane.showMessageDialog(null, "Seat " + seatId + " booking canceled.");
                    houseFull = false; // allow bookings again
                }
                return;
            }

            // Toggle selection
            if (!selected) {
                button.setBackground(Color.YELLOW);
                selectedCount++;
                selected = true;
            } else {
                button.setBackground(Color.GREEN);
                selectedCount--;
                selected = false;
            }
            updateTotal();
        }
    }

    // Updates price label
    private void updateTotal() {
        totalLabel.setText("Total: ₹" + (selectedCount * ticketPrice));
    }

    // Confirm selected seats as booked
    private void confirmBooking() {
        if (houseFull) {
            JOptionPane.showMessageDialog(this,
                    "No tickets available. Housefull!",
                    "Housefull",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean bookedAny = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton btn = seats[i][j];
                if (btn.getBackground() == Color.YELLOW) {
                    btn.setBackground(Color.RED);
                    bookedSeats.add(btn.getText());
                    bookedAny = true;
                }
            }
        }
        if (bookedAny) {
            JOptionPane.showMessageDialog(this, "Booking Confirmed! Enjoy your movie.....");
        } else {
            JOptionPane.showMessageDialog(this, "No seats selected to confirm.");
        }
        selectedCount = 0;
        updateTotal();

        // Check if now the theatre is full
        if (bookedSeats.size() == rows * cols) {
            houseFull = true;
            JOptionPane.showMessageDialog(this,
                    "Theatre is now Housefull!",
                    "Housefull",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Reset only selected (yellow) seats
    private void resetSelection() {
        if (houseFull) {
            JOptionPane.showMessageDialog(this,
                    "Reset not allowed. Housefull!",
                    "Housefull",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedCount == 0) {
            JOptionPane.showMessageDialog(this, "No seats selected to reset.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to clear your current selection?",
                "Reset Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    JButton btn = seats[i][j];
                    if (btn.getBackground() == Color.YELLOW) {
                        btn.setBackground(Color.GREEN);
                    }
                }
            }
            selectedCount = 0;
            updateTotal();
            JOptionPane.showMessageDialog(this, "Selection cleared!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MovieTicketBooking::new);
    }
}
