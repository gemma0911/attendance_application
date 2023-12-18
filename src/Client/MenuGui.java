package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.awt.Color;

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
    
	private static void sendLoginInfoToServer(String username, String time) {
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
						if (response.equals("OK")) {
							System.out.println("INSERT DATA THANH CONG");
							JOptionPane.showMessageDialog(null, "Đã điểm danh",
									response, JOptionPane.OK_OPTION);							
						} else if (response.equals("INVALID")) {
							System.out.println("Invalid username or password!");
							JOptionPane.showMessageDialog(null, "Invalid username or password!", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
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
    
	private void updateDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = dateFormat.format(new Date());
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        String dateTime1 = dateFormat1.format(new Date());
        textField.setText(dateTime);
        textField_2.setText(dateTime1);
    }

	public MenuGui (String username) {
		System.out.println(username);
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
		btnNewButton.setBounds(159, 195, 197, 40);
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
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = textField_1.getText();
				String time = textField.getText();
				sendLoginInfoToServer(textField_1.getText(), time);
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
}
