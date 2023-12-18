package Server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import DAO.Connect;

public class UDPServer {

	private static Map<Integer, Thread> portThreads = new HashMap<>();
	private static Connect connection;

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

		Thread clientThread = portThreads.get(clientPort);

		if (request.startsWith("LOGIN")) {
			handleLoginRequest(request, clientPort, receivePacket.getAddress());
		} else if (request.startsWith("DIEMDANH")) {
			handleDiemDanhRequest(request, clientPort, receivePacket.getAddress());
		} else if (request.equals("ADMIN")) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(Connect.getUserStatus());
			objectOutputStream.flush();
			byte[] sendData = byteArrayOutputStream.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), clientPort);
            socket.send(sendPacket);
		}
	}

	private void handleLoginRequest(String request, int clientPort, InetAddress clientAddress) {
		String[] loginInfo = request.substring(6).split(",");
		String username = loginInfo[0];
		String password = loginInfo[1];

		connection = new Connect();

		if (connection.login(username, password)) {
			sendMessage("OK", clientAddress, clientPort);
			System.out.println(clientAddress + ":" + clientPort);
		} else {
			sendMessage("INVALID", clientAddress, clientPort);
		}
	}

	private void handleDiemDanhRequest(String request, int clientPort, InetAddress clientAddress) {
		String[] loginInfo = request.substring(9).split(",");
		String username = loginInfo[0];
		String time = loginInfo[1];
		System.out.println(username);
		connection = new Connect();

		if (connection.addUser(username, time, clientAddress.toString(), clientPort)) {
			sendMessage("OK", clientAddress, clientPort);
			System.err.println(clientAddress);
		} else {
			sendMessage("INVALID", clientAddress, clientPort);
		}
	}

	private static void sendMessage(String message, InetAddress clientAddress, int clientPort) {
		try (DatagramSocket socket = new DatagramSocket()) {
			byte[] sendData = message.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
