package Server;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class ServerGui extends JFrame {

    private static final long serialVersionUID = 1L;
    private static JPanel contentPane;
    private static DatagramSocket socket = null;
    private static UDPServer server;
    private static JLabel lblNewLabel_1;
    private static JButton btnNewButton;
    
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 402, 282);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setForeground(Color.RED);
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1.setBounds(33, 145, 323, 40);
        contentPane.add(lblNewLabel_1);

        btnNewButton = createButton("Start Server", 33, 27, 152, 56, e -> startServer());
        contentPane.add(btnNewButton);

        JButton btnStopServer = createButton("Stop Server", 195, 27, 120, 56, e -> stopServer());
        contentPane.add(btnStopServer);
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

    private void disableStartButton() {
        btnNewButton.setEnabled(false);
    }

    private void enableStartButton() {
        btnNewButton.setEnabled(true);
    }
}
