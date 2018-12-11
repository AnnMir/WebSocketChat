package Client;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;

import static Common.Commons.*;
import static Common.Commons.KEY_GET_MESSAGES;
import static Common.Commons.KEY_POST_MESSAGE;

public class Client {

    private String address;
    private Socket socket;
    private int serverPort;
    View view;
    private int lastMessageId;
    private ClientSender clientSender = null;
    private ClientReceiver clientReceiver = null;
    private Timer timer;
    private boolean isLogin = false;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Wrong arguments");
            return;
        }

        new Client(args[0], Integer.parseInt(args[1]));
    }

    private Client(final String addr, final int port){
        address = addr;
        serverPort = port;
        view = new View(this);
    }

    public void postLogin(final String userName) {
        try {
            makeConnection();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot connect to server. Check ip address");
            closeSocket();
            return;
        }
        clientSender.work(KEY_POST_LOGIN, userName);
        clientReceiver.setLastMethod(KEY_POST_LOGIN);
    }

    public void postLogout() {
        try {
            makeConnection();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot connect to server. Check ip address");
            closeSocket();
            return;
        }
        clientSender.work(KEY_POST_LOGOUT, null);
        clientReceiver.setLastMethod(KEY_POST_LOGOUT);
    }

    public void getOneUser(final int id) {
        try {
            makeConnection();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot connect to server. Check ip address");
            closeSocket();
            return;
        }
        clientSender.work(KEY_GET_ONE_USER, String.valueOf(id));
        clientReceiver.setLastMethod(KEY_GET_ONE_USER);
    }

    public void getAllUsers() {
        try {
            makeConnection();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot connect to server. Check ip address");
            closeSocket();
            return;
        }
        clientSender.work(KEY_GET_ALL_USERS, null);
        clientReceiver.setLastMethod(KEY_GET_ALL_USERS);
    }

    public void postMessage(final String message) {
        try {
            makeConnection();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot connect to server. Check ip address");
            closeSocket();
            return;
        }
        clientSender.work(KEY_POST_MESSAGE, message);
        clientReceiver.setLastMethod(KEY_POST_MESSAGE);
    }

    void getMessages(final String numberOfMessage) {
        clientSender.work(KEY_GET_MESSAGES, numberOfMessage);
        clientReceiver.setLastMethod(KEY_GET_MESSAGES);
    }

    private void makeConnection() throws IOException {
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket(address, serverPort);
                clientSender = new ClientSender(socket);
                clientReceiver = new ClientReceiver(socket, view, this);
                System.out.println("Client created. Address: " + socket.getLocalAddress().getHostAddress()
                        + ":" + socket.getLocalPort());
                new Thread(clientSender).start();
                new Thread(clientReceiver).start();
                //timer = new Timer();
                //ChatTask updateChatTask = new ChatTask(this);
                //timer.schedule(updateChatTask, 5000, 2000);
            }
        } catch (IOException e) {
            throw new IOException();
        }
    }

    public void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (timer != null) {
                timer.cancel();
            }
            setLogin(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Setters & Getters
    boolean isLogin() {
        return isLogin;
    }

    void setLogin(boolean login) {
        isLogin = login;
    }

    public int getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(int lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public void setClientToken(String clientToken) {
        clientSender.setToken(clientToken);
    }
}

