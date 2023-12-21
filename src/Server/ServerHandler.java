package Server;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import DAO.Connect;

public class ServerHandler {

    public static void handleLoginRequest(String request, int clientPort, InetAddress clientAddress) {
        String[] loginInfo = request.substring(6).split(",");
        String username = loginInfo[0];
        String password = loginInfo[1];

        System.err.println(username);
        if (Connect.login(username, password) == 1) {
            ServerGui.appendToTextArea("username :" + username + "-IP" + clientAddress + ": đã đăng nhập");
            UDPServer.sendMessage("OK", clientAddress, clientPort);
            System.out.println(clientAddress + ":" + clientPort);
        } else if (Connect.login(username, password) == 2) {
        	UDPServer.sendMessage("ADMIN", clientAddress, clientPort);
        } else {
        	UDPServer.sendMessage("INVALID", clientAddress, clientPort);
        }
    }
	
	public static void handleDeleteRequest(String request, int clientPort, InetAddress clientAddress) {
		String[] data = request.substring(9).split(",");
		int id = Integer.parseInt(data[0]);
		if (Connect.deleteStatus(id)) {
			UDPServer.sendMessage("OK", clientAddress, clientPort);
			System.out.println(clientAddress + ":" + clientPort);
			 ServerGui.appendToTextArea("Đã xóa status id = " + id );
		} else {
			UDPServer.sendMessage("INVALID", clientAddress, clientPort);
		}
	}

	public static void handleDiemDanhRequest(String request, int clientPort, InetAddress clientAddress) {
		String[] loginInfo = request.substring(9).split(",");
		String username = loginInfo[0];
		String time = loginInfo[1];
		System.out.println(username);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateTime = dateFormat.format(new Date());
		if (Connect.checkDiemDanho(clientAddress.toString(), time) != true) {
			if (Connect.checkDiemDanh(username, clientAddress.toString(), time) != true) {
				if (Connect.addUser(username, time, clientAddress.toString(), clientPort)) {
					UDPServer.sendMessage("OK", clientAddress, clientPort);
					System.err.println(clientAddress);
					 ServerGui.appendToTextArea(username + "-"+clientAddress.toString()+": đã điểm danh." );
				}
			} else {
				UDPServer.sendMessage("FAILE", clientAddress, clientPort);
			}
		} else {
			UDPServer.sendMessage("ERROR", clientAddress, clientPort);
		}
	}
}
