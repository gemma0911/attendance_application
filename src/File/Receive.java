package File;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receive {
    public static void handleFileRequest(DatagramSocket socket, DatagramPacket receivePacket) {
        try {
            // (Existing code remains the same)

            // Receive file name size
            byte[] nameSizeData = new byte[1024];
            DatagramPacket nameSizePacket = new DatagramPacket(nameSizeData, nameSizeData.length);
            socket.receive(nameSizePacket);
            String nameSizeStr = new String(nameSizePacket.getData(), 0, nameSizePacket.getLength());

            int nameSize = 0;
            if (!nameSizeStr.isEmpty()) {
                nameSize = Integer.parseInt(nameSizeStr);
            }

            // Receive file name
            byte[] nameData = new byte[nameSize];
            DatagramPacket namePacket = new DatagramPacket(nameData, nameData.length);
            socket.receive(namePacket);
            String fileName = new String(namePacket.getData(), 0, namePacket.getLength());

            // Receive file size (if needed)
            byte[] sizeData = new byte[1024];
            DatagramPacket sizePacket = new DatagramPacket(sizeData, sizeData.length);
            socket.receive(sizePacket);
            int fileSize = Integer.parseInt(new String(sizePacket.getData(), 0, sizePacket.getLength()));

            // Receive file data
            byte[] fileData = new byte[fileSize];
            DatagramPacket filePacket = new DatagramPacket(fileData, fileData.length);
            socket.receive(filePacket);

            // Determine file extension from file name
            String fileExtension = "";
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0) {
                fileExtension = fileName.substring(lastDotIndex + 1);
            }

            // Prompt the user for the save location using JFileChooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select a location to save the file");
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();

                // Append the file extension if it is missing
                if (!filePath.toLowerCase().endsWith("." + fileExtension.toLowerCase())) {
                    filePath += "." + fileExtension;
                }

                try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                    fileOutputStream.write(fileData);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                System.out.println("File saved to: " + filePath);
            } else {
                System.out.println("File save canceled by user.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
