package org.example;

public class Main {
    public static void main(String[] args) {
        // Example usage: replace with your WiiM Amp's IP and port
        String ampIp = "192.168.1.100";
        int port = 80;
        WiiMAmpController controller = new WiiMAmpController(ampIp, port);

        boolean playResult = controller.play();
        System.out.println("Play command sent: " + playResult);

        boolean pauseResult = controller.pause();
        System.out.println("Pause command sent: " + pauseResult);
    }
}