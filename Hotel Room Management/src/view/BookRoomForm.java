package view;

import controller.BookingController;
import model.Booking;
import javax.swing.*;

public class BookRoomForm {

    public BookRoomForm() {
        JFrame f = new JFrame("Book Room");
        f.setSize(300, 200);
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField roomId = new JTextField();
        roomId.setBounds(50, 50, 150, 30);
        f.add(roomId);

        JButton btn = new JButton("Book");
        btn.setBounds(50, 100, 150, 30);
        f.add(btn);

        btn.addActionListener(e -> {
            try {
                int rId = Integer.parseInt(roomId.getText());

                // Use the new constructor
                Booking b = new Booking(1, rId, "2026-01-01", "2026-01-02");

                int bookingId = new BookingController().createBooking(b);

                if (bookingId > 0) {
                    JOptionPane.showMessageDialog(f, "Booked successfully! Booking ID: " + bookingId);
                } else {
                    JOptionPane.showMessageDialog(f, "Booking failed!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Invalid Input!");
                ex.printStackTrace();
            }
        });

        f.setVisible(true);
    }
}