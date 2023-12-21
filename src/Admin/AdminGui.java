package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import DAO.Status;
import File.SendFile;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class AdminGui extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel tableModel;
	private DatagramSocket socket;
	private int serverPort = 9876;
	private InetAddress serverAddress;
	private SendFile file;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new AdminGui().setVisible(true));
	}

	public AdminGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JButton btnSendFile = createButton("Send File", e -> sendFile());
		contentPane.add(btnSendFile, BorderLayout.SOUTH);

		initializeTable();
		initializeButtons();

		// Load data when the application is initialized
		displayUsersData();
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


	private void initializeTable() {
		tableModel = new DefaultTableModel(new Vector<>(0),
				new Vector<>(Arrays.asList("ID", "Username", "Time", "IP", "Host", "Delete")));
		table = new JTable(tableModel);
		addActionButtonToTable(table);
		contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	private void initializeButtons() {
		JButton btnDisplayUsersData = createButton("Display Users Data", e -> displayUsersData());
		contentPane.add(btnDisplayUsersData, BorderLayout.NORTH);
	}

	private JButton createButton(String text, ActionListener listener) {
		JButton button = new JButton(text);
		button.addActionListener(listener);
		return button;
	}

	private void displayUsersData() {
		SwingWorker<List<Status>, Void> worker = new SwingWorker<>() {
			@Override
			protected List<Status> doInBackground() {
				try (DatagramSocket socket = new DatagramSocket()) {
					serverAddress = InetAddress.getByName("localhost");

					String request = "ADMIN";
					byte[] sendData = request.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress,
							serverPort);
					socket.send(sendPacket);

					byte[] receiveData = new byte[1024];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					socket.receive(receivePacket);

					// Deserialize the received bytes into a List<Status>
					try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receivePacket.getData());
							ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

						return (List<Status>) objectInputStream.readObject();
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return new ArrayList<>();
			}

			@Override
			protected void done() {
				try {
					List<Status> data = get();
					if (!data.isEmpty()) {
						populateTable(data);
					} else {
						showError("No data received from the server");
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		};

		worker.execute();
	}

	private void populateTable(List<Status> statusList) {
		SwingUtilities.invokeLater(() -> {

			int rowCount = tableModel.getRowCount();
			for (int i = rowCount - 1; i >= 0; i--) {
				tableModel.removeRow(i);
			}

			for (Status status : statusList) {
				Vector<Object> rowData = new Vector<>(Arrays.asList(status.getId(), status.getUsername(),
						status.getTime(), status.getIp(), status.getHost(), "Delete"));
				tableModel.addRow(rowData);
			}
		});
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	// ...

	private void addActionButtonToTable(JTable table) {
		TableColumn column = table.getColumnModel().getColumn(table.getColumnCount() - 1);
		column.setCellRenderer((table1, value, isSelected, hasFocus, row, column1) -> {
			JButton button = new JButton("Delete");
			button.setOpaque(true);
			return button;
		});
		column.setCellEditor(new ButtonEditor(new JCheckBox()));
	}

	private class ButtonEditor extends DefaultCellEditor {
		private JButton button;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(e -> {
				int selectedRow = table.getSelectedRow();
				if (selectedRow != -1) {
					int modelRow = table.convertRowIndexToModel(selectedRow);
					Object value = tableModel.getValueAt(modelRow, 0);
					if (value instanceof Integer) {
						int statusId = (Integer) value;
						System.out.println("Delete status with ID: " + statusId);
						SwingWorker<Void, Void> deleteWorker = new SwingWorker<>() {
							@Override
							protected Void doInBackground() {
								try (DatagramSocket socket = new DatagramSocket()) {
									serverAddress = InetAddress.getByName("localhost");
									String request = "DELETEID " + statusId;
									System.err.println(request);
									byte[] sendData = request.getBytes();
									DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
											serverAddress, serverPort);
									socket.send(sendPacket);
								} catch (IOException e) {
									e.printStackTrace();
								}
								return null;
							}

							@Override
							protected void done() {
								displayUsersData();
								fireEditingStopped();
							}
						};

						deleteWorker.execute();
					}
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			button.setText("Delete");
			return button;
		}

		@Override
		public Object getCellEditorValue() {
			return "Delete";
		}

		@Override
		public boolean stopCellEditing() {
			return super.stopCellEditing();
		}
	}

}
