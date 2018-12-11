package Server;

import java.net.InetSocketAddress;

public class MainServer {
    public static void main(final String[] args) {
        Server s = new Server();
        s.start();
        int port = 8887;
        new WSServer(new InetSocketAddress(port),s.getMessageList()).start();
    }
}
