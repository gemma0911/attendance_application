package Server;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

import DAO.Connect;

public class UDPServer {

	private static Map<Integer, Thread> portThreads = new HashMap<>();
	private static Connect connection;
	private ServerGui serverGui;

	public void setServerGui(ServerGui serverGui) {
		this.serverGui = serverGui;
	}

	private volatile boolean isRunning = true;

	public static void main(String[] args) {
		// Your main method code, if needed
	}

	public void closeServer(DatagramSocket socket) {
		isRunning = false;
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}

	public void startServer(DatagramSocket socket) {
		try {
			socket = new DatagramSocket(9876);
			System.out.println("Server is running...");

			while (isRunning) {
				processRequest(socket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeServer(socket);
		}
	}

	private void processRequest(DatagramSocket socket) throws IOException {
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(receivePacket);

		String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
		int clientPort = receivePacket.getPort();

		if (request.startsWith("LOGIN")) {
			ServerHandler.handleLoginRequest(request, clientPort, receivePacket.getAddress());

		} else if (request.startsWith("DIEMDANH")) {
			ServerHandler.handleDiemDanhRequest(request, clientPort, receivePacket.getAddress());

		} else if (request.equals("ADMIN")) {

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

			objectOutputStream.writeObject(Connect.getUserStatus());
			objectOutputStream.flush();

			byte[] sendData = byteArrayOutputStream.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(),
					clientPort);
			socket.send(sendPacket);

		} else if (request.startsWith("DELETEID")) {
			ServerHandler.handleDeleteRequest(request, clientPort, receivePacket.getAddress());
			
		} else if (request.startsWith("SEND_FILE_REQUEST")) {
			handleFileRequest(socket, receivePacket);
		}
	}

	private void handleFileRequest(DatagramSocket socket, DatagramPacket receivePacket) {
		try {

			byte[] nameSizeData = new byte[1024];
			DatagramPacket nameSizePacket = new DatagramPacket(nameSizeData, nameSizeData.length);
			socket.receive(nameSizePacket);
			String nameSizeStr = new String(nameSizePacket.getData(), 0, nameSizePacket.getLength());

			int nameSize = 0;
			if (!nameSizeStr.isEmpty()) {
				nameSize = Integer.parseInt(nameSizeStr);
			}

			byte[] nameData = new byte[nameSize];
			DatagramPacket namePacket = new DatagramPacket(nameData, nameData.length);
			socket.receive(namePacket);
			String fileName = new String(namePacket.getData(), 0, namePacket.getLength());

			byte[] sizeData = new byte[1024];
			DatagramPacket sizePacket = new DatagramPacket(sizeData, sizeData.length);
			socket.receive(sizePacket);
			int fileSize = Integer.parseInt(new String(sizePacket.getData(), 0, sizePacket.getLength()));

			byte[] fileData = new byte[fileSize];
			DatagramPacket filePacket = new DatagramPacket(fileData, fileData.length);
			socket.receive(filePacket);

			String filePath = "C:\\Users\\thang\\Desktop\\hehe\\" + File.separator + fileName;

			try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
				fileOutputStream.write(fileData);
			} catch (IOException e) {
				e.printStackTrace();
				sendMessage("Error receiving file: " + e.getMessage(), receivePacket.getAddress(),
						receivePacket.getPort());
				return;
			}

			sendMessage("File received successfully", receivePacket.getAddress(), receivePacket.getPort());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendMessage(String message, InetAddress clientAddress, int clientPort) {
		try (DatagramSocket socket = new DatagramSocket()) {
			byte[] sendData = message.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
