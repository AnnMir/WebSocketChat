package Server;

import Common.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class WSServer extends WebSocketServer {

    private ConcurrentHashMap<Integer, Message> messageList;

    WSServer(InetSocketAddress address, ConcurrentHashMap<Integer, Message> messageList) {
        super(address);
        this.messageList = messageList;
        System.out.println("WSServer created");
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        String str;
        if (!messageList.isEmpty())
            for (Message msg : messageList.values()) {
                if (msg.getId() == -1)
                    str = "\"author\":" + msg.getAuthor() + " \"message\":" + msg.getMessage();
                else
                    str = "\"author\":" + msg.getAuthor() + " \"id\": " + msg.getId() + " \"message\": " + msg.getMessage();
                webSocket.send(str);
            }
        for(WebSocket ws: getConnections())
            if(!ws.equals(webSocket)) {
            String msg = "\"author\":" + messageList.get(messageList.size() - 1).getAuthor() + " \"message\":" + messageList.get(messageList.size() - 1).getMessage();
            ws.send(msg);
            }
        System.out.println("onOpen");
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        String[] strs = s.split(" ");
        Integer i = Integer.valueOf(strs[0]);
        System.out.println(i);
        String msg;
        for (int j = i; j < messageList.size(); j++) {
            if (messageList.get(j).getId() == -1) {
                msg = "\"author\":" + messageList.get(j).getAuthor() + " \"message\":" + messageList.get(j).getMessage();
                //webSocket.send(msg);
                //if(j==messageList.size()-1)
                broadcast(msg, getConnections());
            } else {
                msg = "\"author\":" + messageList.get(j).getAuthor() + " \"id\":" + messageList.get(j).getId() + " \"message\": " + messageList.get(j).getMessage();
                //webSocket.send(msg);
                //if(j==messageList.size()-1)
                broadcast(msg, getConnections());
            }
        }
        System.out.println("onMessage");
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        //setConnectionLostTimeout(0);
        //setConnectionLostTimeout(100);
    }

}
