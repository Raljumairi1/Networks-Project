import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        DatagramSocket clientSocket = null;

        try {
            clientSocket = new DatagramSocket();  // Create client socket
            InetAddress serverAddress = InetAddress.getByName("localhost");  // Server address

            System.out.println("- Mail Client starting on host: " + InetAddress.getLocalHost().getHostName());
            System.out.println("- Type name of Mail server: " + serverAddress);

            while (true) {
                // User input
                Scanner cin = new Scanner(System.in);
                System.out.println("---------------------\nCreating New Email..");

                // Check for "quit" in each field
                System.out.print("To: ");
                String to = cin.nextLine();
                if (to.equalsIgnoreCase("quit")) {
                    sendDisconnectMessage(clientSocket, serverAddress);
                    break;
                }

                System.out.print("From: ");
                String from = cin.nextLine();
                if (from.equalsIgnoreCase("quit")) {
                    sendDisconnectMessage(clientSocket, serverAddress);
                    break;
                }

                System.out.print("Subject: ");
                String subject = cin.nextLine();
                if (subject.equalsIgnoreCase("quit")) {
                    sendDisconnectMessage(clientSocket, serverAddress);
                    break;
                }

                System.out.print("Body: ");
                String body = cin.nextLine();
                if (body.equalsIgnoreCase("quit")) {
                    sendDisconnectMessage(clientSocket, serverAddress);
                    break;
                }

                // Send the email
                String timestamp = java.time.LocalDateTime.now().toString();
                String email = String.format("From: %s\nTo: %s\nSubject: %s\nBody: %s\nTimestamp: %s\n", to, from, subject, body, timestamp);

                byte[] sendData = email.getBytes();  // Convert email to bytes
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 12345);
                clientSocket.send(sendPacket);  // Send email packet

                // Receive the server response
                byte[] receiveData = new byte[1024];  // Buffer for server response
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);  // Receive response

                // Print server's response
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("- Server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close client socket if open
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            System.out.println("Client disconnected.");
        }
    }

    // Method to send a disconnect message
    private static void sendDisconnectMessage(DatagramSocket clientSocket, InetAddress serverAddress) {
        try {
            String disconnectMessage = "Client disconnecting";
            byte[] sendData = disconnectMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 12345);
            clientSocket.send(sendPacket);  // Send disconnect message
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
