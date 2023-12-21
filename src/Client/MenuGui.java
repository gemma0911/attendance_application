package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import File.SendFile;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MenuGui extends JFrame {

    private static final long serialVersionUID = 1L;
    private static JPanel contentPane;
    private static JTextField textField;
    private static DatagramSocket socket = null;
    private static int serverPort = 9876;
    private static InetAddress serverAddress;
    private JTextField textField_1;
    private JLabel lblHTnSinh;
    private String receivedData;
    private JTextField textField_2;
    private JButton btnNpBi;
	private SendFile file;
	
    private boolean isNopBaiEnabled = false; // Thêm biến mới

    private static void sendDiemDanhRequestToServer(String username, String time) {
        String request = "DIEMDANH " + username + "," + time;
        statClient(request);
    }

    public static void statClient(String data) {
        try {
            // Gửi và nhận dữ liệu từ server
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new DatagramSocket();
                        serverAddress = InetAddress.getByName("192.168.96.1");

                        String request = data;
                        byte[] sendData = request.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress,
                                serverPort);
                        socket.send(sendPacket);

                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        socket.receive(receivePacket);

                        String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        handleDiemDanhResponse(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (socket != null) {
                            socket.close();
                        }
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleDiemDanhResponse(String response) {
        switch (response) {
            case "OK":
                JOptionPane.showMessageDialog(null, "Đã điểm danh", response, JOptionPane.OK_OPTION);
                break;
            case "INVALID":
                JOptionPane.showMessageDialog(null, "Invalid username or password!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;
            case "FAILE":
                JOptionPane.showMessageDialog(null, "Bạn Đã điểm danh không thể điểm danh tiếp", response,
                        JOptionPane.OK_OPTION);
                break;
            case "ERROR":
                JOptionPane.showMessageDialog(null, "Bạn đang điểm danh giúp người khác đấy", response,
                        JOptionPane.OK_OPTION);
                break;
            default:
                // Xử lý các trường hợp khác nếu cần
                break;
        }
    }

    private void updateDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = dateFormat.format(new Date());
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        String dateTime1 = dateFormat1.format(new Date());
        textField.setText(dateTime);
        textField_2.setText(dateTime1);
    }

    public MenuGui(String username) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 402, 282);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        textField = new JTextField();
        textField.setBounds(159, 88, 197, 40);
        contentPane.add(textField);
        textField.setColumns(10);

        JLabel lblNewLabel = new JLabel("Ngày");
        lblNewLabel.setForeground(Color.RED);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 19));
        lblNewLabel.setBounds(21, 88, 128, 40);
        contentPane.add(lblNewLabel);

        JButton btnNewButton = new JButton("Điểm danh");
        btnNewButton.setBounds(199, 195, 157, 40);
        contentPane.add(btnNewButton);

        textField_1 = new JTextField();
        textField_1.setText(username);
        textField_1.setColumns(10);
        textField_1.setBounds(159, 31, 197, 40);
        contentPane.add(textField_1);

        lblHTnSinh = new JLabel("Username");
        lblHTnSinh.setForeground(Color.RED);
        lblHTnSinh.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 19));
        lblHTnSinh.setBounds(21, 31, 128, 40);
        contentPane.add(lblHTnSinh);

        JLabel lblGi = new JLabel("Giờ");
        lblGi.setForeground(Color.RED);
        lblGi.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 19));
        lblGi.setBounds(21, 138, 128, 40);
        contentPane.add(lblGi);

        textField_2 = new JTextField();
        textField_2.setColumns(10);
        textField_2.setBounds(159, 138, 197, 40);
        contentPane.add(textField_2);

        btnNpBi = new JButton("Nộp bài");
        btnNpBi.setBounds(32, 195, 157, 40);
        contentPane.add(btnNpBi);

        btnNpBi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	sendFile();
//                JOptionPane.showMessageDialog(null, "Bài đã được nộp thành công", "Thông báo",
//                        JOptionPane.INFORMATION_MESSAGE);
//                btnNpBi.setEnabled(false);
            }
        });

        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = textField_1.getText();
                String time = textField.getText();
                sendDiemDanhRequestToServer(textField_1.getText(), time);
                dispose();
            }
        });

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDateTime();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MenuGui frame = new MenuGui("TestUser");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void sendFile() {
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			System.err.println(selectedFile);
			file = new SendFile();
			try {
				file.sendFileRequest(selectedFile, serverAddress, serverPort);
				SwingUtilities.invokeLater(() -> {
					JOptionPane.showMessageDialog(this, "File sent successfully", "Success",
							JOptionPane.INFORMATION_MESSAGE);
				});
			} catch (Exception e) {
				e.printStackTrace();
				SwingUtilities.invokeLater(() -> {
					JOptionPane.showMessageDialog(this, "Error sending file", "Error", JOptionPane.ERROR_MESSAGE);
				});
			}
		}
	}
}
