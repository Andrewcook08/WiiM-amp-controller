package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PortScanner {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java PortScanner <ip> [startPort] [endPort]");
            return;
        }
        String ip = args[0];
        int startPort = args.length > 1 ? Integer.parseInt(args[1]) : 1;
        int endPort = args.length > 2 ? Integer.parseInt(args[2]) : 1024;

        System.out.println("Scanning " + ip + " from port " + startPort + " to " + endPort + "...");
        for (int port = startPort; port <= endPort; port++) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ip, port), 200);
                System.out.println("Port " + port + " is OPEN");
            } catch (IOException ex) {
                // Port is closed or unreachable
            }
        }
        System.out.println("Scan complete.");
    }
}
