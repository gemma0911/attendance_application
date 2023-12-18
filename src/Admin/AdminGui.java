package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import DAO.Status;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class AdminGui extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;
    private DatagramSocket socket;
    private int serverPort = 9876;
    private InetAddress serverAddress;
    private static List<Status> data;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new AdminGui().setVisible(true));
    }

    private void loadData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (DatagramSocket socket = new DatagramSocket()) {
                    serverAddress = InetAddress.getByName("localhost");

                    String request = "ADMIN";
                    byte[] sendData = request.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                    socket.send(sendPacket);

                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);

                    // Deserialize the received bytes into an ArrayList
                    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receivePacket.getData());
                         ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

                        List<Status> receivedData = (ArrayList<Status>) objectInputStream.readObject();
                        data = new ArrayList<>(receivedData);

                        // Do something with the received data (update GUI, process, etc.)
                        System.out.println("Received data from server: " + receivedData);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        worker.execute();
    }

    public AdminGui() {
        loadData();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Username");
        tableModel.addColumn("Time");
        tableModel.addColumn("IP");
        tableModel.addColumn("Host");
        tableModel.addColumn("Delete");

        table = new JTable(tableModel);
        addActionButtonToTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JButton btnDisplayUsersData = createButton("Display Users Data", 150, 10, 180, 30, e -> displayUsersData());
        displayUsersData();
        contentPane.add(btnDisplayUsersData, BorderLayout.NORTH);
    }

    private JButton createButton(String text, int x, int y, int width, int height, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(listener);
        return button;
    }

    private void displayUsersData() {
        loadData();
        if (data != null) {
            populateTable(data);
        } else {
            showError("No data received from the server");
        }
    }

    private void populateTable(List<Status> statusList) {
        SwingUtilities.invokeLater(() -> {
            // Clear existing data
            tableModel.setRowCount(0);

            // Populate the table with the retrieved data
            for (Status status : statusList) {
                Object[] rowData = {status.getId(),status.getUsername(), status.getTime(), status.getIp(), status.getHost(), "Delete"};
                tableModel.addRow(rowData);
            }
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void addActionButtonToTable(JTable table) {
        TableColumn column = table.getColumnModel().getColumn(table.getColumnCount() - 1);
        column.setCellRenderer(new ButtonRenderer());
        column.setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Action");
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private static final long serialVersionUID = 1L;
        private JButton button;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                Object value = tableModel.getValueAt(table.getSelectedRow(), 0);
                if (value instanceof Integer) {
                    int statusId = (Integer) value;
                    System.out.println("Delete status with ID: " + statusId);
                    tableModel.removeRow(table.getSelectedRow());
                    SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() {
                            try (DatagramSocket socket = new DatagramSocket()) {
                                serverAddress = InetAddress.getByName("localhost");
                                String request = "DELETEID " + String.valueOf(statusId);
                                System.err.println(request);
                                byte[] sendData = request.getBytes();
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                                socket.send(sendPacket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };

                    worker.execute();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText("Delete");
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Action";
        }

        @Override
        public boolean stopCellEditing() {
            fireEditingStopped();
            return super.stopCellEditing();
        }
    }
}
