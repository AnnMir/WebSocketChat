package Client;

import Common.Data;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import static Common.Commons.*;

public class ClientSender implements Runnable {

    private BufferedWriter bufferedWriter;
    private String method;
    private LinkedBlockingQueue<Data> toSendQueue;

    void setToken(String token) {
        this.token = token;
    }

    private String token;

    ClientSender(final Socket socket) {
        try {
            method = "default";
            toSendQueue = new LinkedBlockingQueue<>();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void work(final String methodName, final String message) {
        try {
            toSendQueue.put(new Data(methodName, message));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            Data data;
            try {
                data = toSendQueue.take();
                executeMethod(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void executeMethod(Data data) {

        method = data.getNameOfMethod();
        String nameOrMessage = data.getMessage();
        switch (method) {
            case KEY_POST_LOGIN: {
                sendPostLogin(nameOrMessage);
                method = "default";
                break;
            }
            case KEY_POST_LOGOUT: {
                sendPostLogout();
                method = "default";
                break;
            }
            case KEY_POST_MESSAGE: {
                sendPostMessage(nameOrMessage);
                method = "default";
                break;
            }
            case KEY_GET_ONE_USER: {
                sendGetOneUser(nameOrMessage);
                method = "default";
                break;
            }
            case KEY_GET_ALL_USERS: {
                sendGetAllUsers();
                method = "default";
                break;
            }
            case KEY_GET_MESSAGES: {
                sendGetAllMessages(nameOrMessage);
                method = "default";
                break;
            }
            default: {
                break;
            }
        }
    }

    private void sendPostMessage(final String msg) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", msg);

            String message = "POST /" + KEY_POST_MESSAGE + " HTTP/1.1" + "\r\n"
                    + "Authorization: Token " + token + "\r\n"
                    + "Content-Type: application/json " + "\r\n"
                    + "\r\n"
                    + jsonObject.toJSONString() + "\r\n";
            System.out.println("post message: " + message);
            bufferedWriter.write(message);
            bufferedWriter.flush();
            //while (!wsClient.webSocketClient.isOpen()) {

            //}
            //wsClient.webSocketClient.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPostLogin(final String name) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", name);

            String message = "POST /" + KEY_POST_LOGIN + " HTTP/1.1" + "\r\n"
                    + "Content-Type: application/json" + "\r\n"
                    + "\r\n"
                    + jsonObject.toJSONString() + "\r\n";
            bufferedWriter.write(message);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPostLogout() {
        try {
            String message = "POST /" + KEY_POST_LOGOUT + " HTTP/1.1" + "\r\n"
                    + "Authorization: Token " + token + "\r\n"
                    + "\r\n";
            System.out.println(message);
            bufferedWriter.write(message);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendGetOneUser(final String nameOrMessage) {
        try {
            String message = "GET /" + KEY_GET_ONE_USER + nameOrMessage + " HTTP/1.1\r\n"
                    + "Authorization: Token " + token + "\r\n"
                    + "\r\n";
            bufferedWriter.write(message);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendGetAllUsers() {
        try {
            String message = "GET /" + KEY_GET_ALL_USERS + " HTTP/1.1" + "\r\n"
                    + "Authorization: Token " + token + "\r\n"
                    + "\r\n";
            bufferedWriter.write(message);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendGetAllMessages(String numberOfMessage) {
        try {
            String message = "GET /" + KEY_GET_MESSAGES + "?offset=" + numberOfMessage + "&count=10" + " HTTP/1.1\r\n"
                    + "Authorization: Token " + token + "\r\n"
                    + "\r\n";
            bufferedWriter.write(message);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


