package File;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Admin.AdminGui;

public class SendFile {
	
	public void sendFileRequest(File file,InetAddress serverAddress,int serverPort) {
		
	    try (DatagramSocket socket = new DatagramSocket()) {
	        serverAddress = InetAddress.getByName("localhost");

	        // Gửi yêu cầu gửi file
	        String request = "SEND_FILE_REQUEST";
	        byte[] requestData = request.getBytes();
	        DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, serverAddress, serverPort);
	        socket.send(requestPacket);

	        // Gửi kích thước tên file
	        String fileName = file.getName();
	        byte[] fileNameSizeData = String.valueOf(fileName.length()).getBytes();
	        DatagramPacket fileNameSizePacket = new DatagramPacket(fileNameSizeData, fileNameSizeData.length, serverAddress, serverPort);
	        socket.send(fileNameSizePacket);

	        // Gửi tên file
	        byte[] fileNameData = fileName.getBytes();
	        DatagramPacket fileNamePacket = new DatagramPacket(fileNameData, fileNameData.length, serverAddress, serverPort);
	        socket.send(fileNamePacket);

	        // Gửi kích thước file
	        long fileSize = file.length();
	        byte[] fileSizeData = String.valueOf(fileSize).getBytes();
	        DatagramPacket fileSizePacket = new DatagramPacket(fileSizeData, fileSizeData.length, serverAddress, serverPort);
	        socket.send(fileSizePacket);

	        // Gửi dữ liệu file
	        byte[] fileData = Files.readAllBytes(file.toPath());
	        DatagramPacket filePacket = new DatagramPacket(fileData, fileData.length, serverAddress, serverPort);
	        socket.send(filePacket);
	     

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
