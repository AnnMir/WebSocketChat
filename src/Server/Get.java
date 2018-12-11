package Server;


import Common.Message;
import Common.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static Common.Commons.*;
import static Common.Commons.CLIENT_ERROR_FORBIDDEN;

public class Get {
    private static final String MESSAGES_PATH = "/"+KEY_GET_MESSAGES;
    private static final String USERS_PATH = "/"+KEY_GET_ALL_USERS;

    private ConcurrentHashMap<String, User> usersList;
    private ConcurrentHashMap<Integer, Message> messageList;
    private RequestHandler requestHandler;
    private RequestData requestData;
    private String currentName;

    public Get(ConcurrentHashMap<String, User> usersList, ConcurrentHashMap<Integer, Message> messageList,
               RequestHandler requestHandler, RequestData requestData) {
        this.requestHandler = requestHandler;
        this.usersList = usersList;
        this.messageList = messageList;
        this.requestData = requestData;
        currentName = requestHandler.getCurrentName();
    }

    public String work() throws RestException {
        URI uri;
        try {
            uri = new URI(requestData.getUri());
        } catch (URISyntaxException e) {
            throw new RestException("Wrong URI", CLIENT_ERROR_METHOD_NOT_ALLOWED);
        }
        String path = uri.getPath();
        System.out.println(path);
        switch (path) {
            case USERS_PATH: {
                return getUsers(requestData);
            }
            case MESSAGES_PATH: {
                return getMessages(requestData);
            }
            default: {
                String[] dirs = path.split("/");
                System.out.println(dirs[0]);
                System.out.println(dirs[1]);
                System.out.println(dirs[2]);
                if (dirs[1].equals("users")) {
                    return getOneUser(requestData, dirs[2]);
                } else {
                    throw new RestException("Unknown method", CLIENT_ERROR_METHOD_NOT_ALLOWED);
                }
            }
        }
    }

    private String getUsers(RequestData request) throws RestException {
        if (!isUser(request.getToken())) {
            throw new RestException("Wrong token", CLIENT_ERROR_FORBIDDEN);
        }
        JSONArray jsonMessageList = new JSONArray();
        for (Map.Entry<String, User> entry : usersList.entrySet()) {
            User userData = entry.getValue();
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("id", userData.getUserID());
            jsonMessage.put("username", userData.getUserName());
            if (userData.isOnline()) {
                jsonMessage.put("online", true);
            } else {
                jsonMessage.put("online", null);
            }
            jsonMessageList.add(jsonMessage);
        }
        JSONObject result = new JSONObject();
        result.put("users", jsonMessageList);
        requestHandler.setLastActingDate(new Date());
        return HttpParser.makeResponse(SUCCESS_OK_CODE, request.getContentType(), result.toString());
    }

    private String getOneUser(RequestData request, String user) throws RestException {
        if (!isUser(request.getToken())) {
            throw new RestException("Wrong token", CLIENT_ERROR_FORBIDDEN);
        }
        User userData = null;
        try{
            int id = Integer.valueOf(user);
        }catch (NumberFormatException e){
            throw new RestException("It's not a number",CLIENT_ERROR_BAD_REQUEST);
        }
        if(Integer.parseInt(user)<1)
            throw new RestException("Wrong number",CLIENT_ERROR_BAD_REQUEST);
        for (Map.Entry<String, User> entry : usersList.entrySet()) {
            if (entry.getValue().getUserID() == Integer.parseInt(user)) {
                userData = entry.getValue();
            }
        }
        if (userData == null) {
            throw new RestException("User not found", CLIENT_ERROR_NOT_FOUND);
        }
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("id", userData.getUserID());
        jsonMessage.put("username", userData.getUserName());
        if (userData.isOnline()) {
            jsonMessage.put("online", true);
        } else {
            jsonMessage.put("online", null);
        }
        requestHandler.setLastActingDate(new Date());
        return HttpParser.makeResponse(SUCCESS_OK_CODE, request.getContentType(), jsonMessage.toString());
    }

    private String getMessages(RequestData request) throws RestException {
        if (!isUser(request.getToken())) {
            throw new RestException("Wrong token", CLIENT_ERROR_FORBIDDEN);
        }
        try {
            URI uri = new URI(request.getUri());
            String[] queries = uri.getQuery().split("&");
            int offset = Integer.parseInt(queries[0].split("=")[1]);
            int count = Integer.parseInt(queries[1].split("=")[1]);

            JSONArray jsonMessageList = new JSONArray();

            for (int i = offset; i < offset + count - 1; i++) {
                if (!messageList.containsKey(i)) {
                    break;
                }
                Message messageData = messageList.get(i);
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("id", i);
                jsonMessage.put("message", messageData.getMessage());
                jsonMessage.put("author", messageData.getAuthor());
                jsonMessageList.add(jsonMessage);
            }
            JSONObject result = new JSONObject();
            result.put("messages", jsonMessageList);
            return HttpParser.makeResponse(SUCCESS_OK_CODE, request.getContentType(), result.toString());

        } catch (URISyntaxException e) {
            throw new RestException("Bad URI", CLIENT_ERROR_BAD_REQUEST);
        }
    }

    private boolean isUser(String token) throws RestException {
        System.out.println(token);
        System.out.println(currentName);
        if (usersList.containsKey(currentName)) {
            String rightToken = usersList.get(currentName).getUserToken().toString();
            return rightToken.equals(token);
        } else {
            throw new RestException("Unknown user", CLIENT_ERROR_FORBIDDEN);
        }
    }
}


