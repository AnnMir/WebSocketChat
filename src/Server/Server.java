package Server;

import Common.Message;
import Common.User;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {

    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, User> usersSet;
    private ConcurrentHashMap<Integer, Message> messageList;

    Server() {
        try {
            serverSocket = new ServerSocket(8080);
            int serverPort = serverSocket.getLocalPort();
            System.out.println("Server started in : " + InetAddress.getLocalHost().getHostAddress()+ " " + serverPort);
            usersSet = new ConcurrentHashMap<>();
            messageList = new ConcurrentHashMap<>();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public ConcurrentHashMap<Integer, Message> getMessageList() {
        return messageList;
    }

    public void run() {
        try {

            while (true) {
                System.out.println("Server's waiting for new clients");
                Socket individualSocket = serverSocket.accept();
                System.out.println("Server gets new client");
                new Connection(individualSocket, usersSet, messageList);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}

