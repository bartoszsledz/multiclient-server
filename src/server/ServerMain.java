package server;

import java.io.*;
import java.net.ServerSocket;

/**
 * @author Bartosz Śledź
 */
public class ServerMain {

    private static final int PORT = 9000;

    public static void main(String[] args) throws IOException {
        System.out.println("Start Server.");
        ServerSocket serverSocket = new ServerSocket(PORT);
        try {
            while (true) {
                new MultiThreadServer(serverSocket.accept()).start();
            }
        } finally {
            serverSocket.close();
        }
    }

}
