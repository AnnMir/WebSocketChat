package Client;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;

import static Common.Commons.*;
import static Common.Commons.KEY_GET_MESSAGES;

public class ClientReceiver implements Runnable {

    private DataInputStream bufferedReader;
    private String responseStartLine;
    private Hashtable<String, String> responseHeaders;
    private JSONObject responseJsonObject;
    private View view;
    private String lastMethod;
    private Client mainClient;

    void setLastMethod(String lastMethod) {
        this.lastMethod = lastMethod;
    }

    ClientReceiver(final Socket socket, final View chatView, final Client client) {
        try {
            mainClient = client;
            this.view = chatView;
            bufferedReader = new DataInputStream(socket.getInputStream());
            responseHeaders = new Hashtable<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            int isEnd;
            while (true) {
                byte[] bytes = new byte[BUF_SIZE];
                isEnd = bufferedReader.read(bytes);
                if (isEnd < 0) {
                    view.updateChat("Connection completed with code -1");
                    view.successLogout();
                    break;
                }
                String httpResponse = new String(bytes, "UTF-8");
                parseHttp(httpResponse);
            }
        } catch (final IOException e) {
            view.updateChat("Connection completed. Successful logout");
            view.successLogout();
            try {
                bufferedReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    private void parseHttp(final String incomingMessage) {
        responseStartLine = "";
        responseHeaders.clear();
        responseJsonObject = null;
        String[] messageStrings = incomingMessage.split("\r\n");
        int numberOfStrings = messageStrings.length;
        int endOfRequestHeaders = 0;
        for (String currentString : messageStrings) {
            if ("".equals(currentString)) {
                break;
            }
            endOfRequestHeaders++;
        }
        setResponseStartLine(messageStrings[0]);
        for (int i = 1; i < endOfRequestHeaders; i++) {
            appendHeaderParameter(messageStrings[i]);
        }
        if (numberOfStrings != endOfRequestHeaders) {
            int endPoint = messageStrings[numberOfStrings - 1].indexOf("\u0000");
            String jsonString = messageStrings[numberOfStrings - 1].substring(0, endPoint);
            decodeToJsonResponseBody(jsonString);
        }
        responseHandle();
    }

    private void responseHandle() {
        if (responseStartLine.contains("200")) {
            switch (lastMethod) {
                case KEY_POST_LOGIN: {
                    mainClient.setLogin(true);
                    view.successLogin(responseJsonObject.get("token").toString());
                    showResponseMessage("Login");
                    break;
                }
                case KEY_POST_LOGOUT: {
                    view.successLogout();
                    mainClient.closeSocket();
                    showResponseMessage("Logout");
                    break;
                }
                case KEY_POST_MESSAGE: {
                    showResponseMessage("My message");
                    break;
                }
                case KEY_GET_ONE_USER: {
                    showResponseMessage("Get one user");
                    break;
                }
                case KEY_GET_ALL_USERS: {
                    view.showUsers(responseJsonObject.toJSONString());
                    break;
                }
                case KEY_GET_MESSAGES: {
                    view.showMessages(responseJsonObject.toJSONString());
                }
            }
        } else {
            showResponseMessage("Response code is not 200");
        }
    }

    private void showResponseMessage(final String head) {
        view.updateChat("\n" + head + "\n");
        view.updateChat(responseStartLine);
        for (String key : responseHeaders.keySet()) {
            view.updateChat(key + ":" + responseHeaders.get(key));
        }
        if (responseJsonObject != null) {
            view.updateChat(responseJsonObject.toString());
        }
    }

    private void setResponseStartLine(final String requestLine) {
        if (requestLine == null || requestLine.length() == 0) {
            System.out.println("Wrong StartLine");
        }
        responseStartLine = requestLine;
    }

    private void appendHeaderParameter(final String header) {
        String[] splitHeader = header.split(":");
        if (splitHeader.length != 2) {
            System.out.println("Wrong header");
            return;
        }
        responseHeaders.put(splitHeader[0], splitHeader[1]);
    }

    private void decodeToJsonResponseBody(final String requestBody) {
        if (requestBody.equals("")) {
            return;
        }
        JSONParser parser = new JSONParser();
        try {
            responseJsonObject = (JSONObject) parser.parse(requestBody);
        } catch (final ParseException e) {
            responseJsonObject = null;
        }
    }
}

