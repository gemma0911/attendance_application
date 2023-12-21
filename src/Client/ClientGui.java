package Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Admin.AdminGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientGui extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField username;
    private JTextField password;
    private DatagramSocket socket = null;
    private int serverPort = 9876;
    private InetAddress serverAddress;
    private JLabel lblNewLabel_1;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new ClientGui().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error creating the client", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public ClientGui() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 402, 282);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        username = new JTextField();
        username.setBounds(159, 23, 197, 40);
        contentPane.add(username);
        username.setColumns(10);

        password = new JTextField();
        password.setColumns(10);
        password.setBounds(159, 87, 197, 40);
        contentPane.add(password);

        JLabel lblNewLabel = new JLabel("Username");
        setLabelProperties(lblNewLabel, 33, 23);
        contentPane.add(lblNewLabel);

        JLabel lblPassword = new JLabel("Password");
        setLabelProperties(lblPassword, 33, 87);
        contentPane.add(lblPassword);

        JButton btnNewButton = createButton("Login", 89, 195, 197, 40, e -> loginButtonClicked());
        contentPane.add(btnNewButton);

        lblNewLabel_1 = new JLabel("");
        setLabelProperties(lblNewLabel_1, 33, 145, 323, 40);
        contentPane.add(lblNewLabel_1);
    }

    private void setLabelProperties(JLabel label, int x, int y) {
        label.setForeground(Color.RED);
        label.setFont(new Font("Tahoma", Font.PLAIN, 15));
        label.setBounds(x, y, 72, 40);
    }

    private void setLabelProperties(JLabel label, int x, int y, int width, int height) {
        setLabelProperties(label, x, y);
        label.setBounds(x, y, width, height);
    }

    private JButton createButton(String text, int x, int y, int width, int height, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(listener);
        return button;
    }

    private void loginButtonClicked() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    socket = new DatagramSocket();
                    serverAddress = InetAddress.getByName("localhost");

                    String request = "LOGIN " + username.getText() + "," + password.getText();
                    byte[] sendData = request.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                    socket.send(sendPacket);

                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);

                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    if (response.equals("OK")) {
                        System.out.println("Login successful!");
                        new MenuGui(username.getText()).setVisible(true);
                        dispose();
                    } else if (response.equals("INVALID")) {
                        showError("Invalid username or password!");
                    } else if (response.equals("ERROR")) {
                        showError("Bạn không thể đăng nhập ngay lúc này");
                    } else if (response.equals("ADMIN")) {
                    	 new AdminGui().setVisible(true);
                         dispose();
                    } 
                    
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (socket != null) {
                        socket.close();
                    }
                }
                return null;
            }
        };

        worker.execute();
    }

    
    private void showError(String message) {
        lblNewLabel_1.setText(message);
        lblNewLabel_1.setBackground(Color.RED);
        System.out.println(message);
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
