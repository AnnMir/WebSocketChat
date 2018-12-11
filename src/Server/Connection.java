package Server;

import Common.Message;
import Common.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import static Common.Commons.BUF_SIZE;
import static Common.Commons.TIMEOUT_PERIOD;

public class Connection extends Thread {

    private DataInputStream ConnectionInput;
    private DataOutputStream ConnectionOutput;
    private RequestHandler handler;

    Connection(final Socket socket, final ConcurrentHashMap<String, User> users,
               final ConcurrentHashMap<Integer, Message> messages) {
        try {
            socket.setSoTimeout(TIMEOUT_PERIOD);
            ConnectionInput = new DataInputStream(socket.getInputStream());
            ConnectionOutput = new DataOutputStream(socket.getOutputStream());
            handler = new RequestHandler(users, messages);
            start();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String httpRequest;
        int isEnd;
        while (true) {
            try {
                byte[] bytes = new byte[BUF_SIZE];
                isEnd = ConnectionInput.read(bytes);
                if (isEnd < 0) {
                    break;
                }
                httpRequest = new String(bytes, "UTF-8");
                String response;
                try {
                    response = handler.handle(httpRequest);
                    ConnectionOutput.write(response.getBytes());
                } catch (RestException e) {
                    ConnectionOutput.write(e.getMessage().getBytes());
                } finally {
                    ConnectionOutput.flush();
                }
                if (!handler.isLogin()) {
                    try {
                        ConnectionInput.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            } catch (IOException e) {
                try {
                    ConnectionInput.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (handler.isLogin()) {
                    handler.TimeoutLogout();
                }
                break;
            }
        }
    }
}
