package Server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerGui extends JFrame {

    private static final long serialVersionUID = 1L;
    private static JPanel contentPane;
    private static DatagramSocket socket = null;
    private static UDPServer server;
    private static JButton btnNewButton;
    private static JTextArea textArea;
    
    public static void main(String[] args) throws SocketException {
        EventQueue.invokeLater(() -> {
            try {
                ServerGui frame = new ServerGui();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ServerGui() throws SocketException {
        server = new UDPServer();
        server.setServerGui(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 484, 454);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        btnNewButton = createButton("Start Server", 33, 27, 152, 56, e -> startServer());
        contentPane.add(btnNewButton);

        JButton btnStopServer = createButton("Stop Server", 195, 27, 120, 56, e -> stopServer());
        contentPane.add(btnStopServer);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(33, 117, 416, 290);
        contentPane.add(scrollPane);
    }

    private JButton createButton(String text, int x, int y, int width, int height, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Sitka Text", Font.BOLD, 15));
        button.setBounds(x, y, width, height);
        button.addActionListener(listener);
        return button;
    }

    private void startServer() {
        disableStartButton();
        Thread serverThread = new Thread(() -> {
            server.startServer(socket);
            enableStartButton();
        });
        serverThread.start();
    }

    private void stopServer() {
        server.closeServer(socket);
        enableStartButton();
    }

    public static void appendToTextArea(String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }

    private void disableStartButton() {
        btnNewButton.setEnabled(false);
    }

    private void enableStartButton() {
        btnNewButton.setEnabled(true);
    }
}
