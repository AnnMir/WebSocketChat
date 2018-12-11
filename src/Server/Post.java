package Server;


import Common.Message;
import Common.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static Common.Commons.*;
import static Common.Commons.CLIENT_ERROR_BAD_REQUEST;
import static Common.Commons.SUCCESS_OK_CODE;

public class Post {
    private static final String KEY_LOGIN = "/login";
    private static final String KEY_LOGOUT = "/"+KEY_POST_LOGOUT;
    private static final String KEY_MESSAGES = "/message";
    private static final String KEY_CONTENT_TYPE = "application/json";

    private ConcurrentHashMap<String, User> usersList;
    private ConcurrentHashMap<Integer, Message> messageList;
    private RequestHandler requestHandler;
    private RequestData requestData;
    private String currentName;

    Post(ConcurrentHashMap<String, User> usersList, ConcurrentHashMap<Integer, Message> messageList,
         RequestHandler requestHandler, RequestData requestData) {
        this.usersList = usersList;
        this.messageList = messageList;
        this.requestHandler = requestHandler;
        this.requestData = requestData;
        currentName = requestHandler.getCurrentName();
    }

    public String work() throws RestException {
        URI uri;
        try {
            uri = new URI(requestData.getUri());
        } catch (URISyntaxException e) {
            throw new RestException("Bad URI", CLIENT_ERROR_METHOD_NOT_ALLOWED);
        }

        switch (uri.getPath()) {
            case KEY_LOGIN: {
                return postLogin(requestData);
            }
            case KEY_LOGOUT: {
                return postLogout(requestData);
            }
            case KEY_MESSAGES: {
                return postMessages(requestData);
            }
            default: {
                throw new RestException("Wrong name of method", CLIENT_ERROR_METHOD_NOT_ALLOWED);
            }
        }
    }

    private String postLogin(RequestData request) throws RestException {
        if (!request.getContentType().equals(KEY_CONTENT_TYPE)) {
            throw new RestException("ContentType is not application/json", CLIENT_ERROR_BAD_REQUEST);
        }
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObj = (JSONObject) parser.parse(request.getMessage());
            String username = (String) jsonObj.get("username");
            System.out.println(username);
            User userData;
            if (usersList.containsKey(username)) {
                userData = usersList.get(username);
                if (!userData.isOnline()) {
                    requestHandler.setCurrentName(username);
                    usersList.get(username).setOnline(true);
                    usersList.get(username).setUserToken(UUID.randomUUID());
                    messageList.put(messageList.size(), new Message(-1, "server",
                            "User: " + username + " reconnected."));
                } else {
                    throw new RestException("Token realm='Username is already in use'", CLIENT_ERROR_UNAUTHORIZED);
                }
            } else {
                userData = new User(username, usersList.size()+1, true);
                userData.setUserToken(UUID.randomUUID());
                messageList.put(messageList.size(), new Message(-1, "server",
                        "User: " + username + " connected."));
                requestHandler.setCurrentName(username);
                usersList.put(username, userData);
            }
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("id", userData.getUserID());
            jsonMessage.put("username", userData.getUserName());
            jsonMessage.put("online", userData.isOnline());
            jsonMessage.put("token", userData.getUserToken().toString());
            requestHandler.setLastActingDate(new Date());
            return HttpParser.makeResponse(SUCCESS_OK_CODE, request.getContentType(), jsonMessage.toJSONString());
        } catch (ParseException e) {
            throw new RestException("Parse exception while postLogin", CLIENT_ERROR_BAD_REQUEST);
        }
    }

    private String postLogout(RequestData request) throws RestException {
        if (!isUser(request.getToken())) {
            throw new RestException("Wrong token", CLIENT_ERROR_FORBIDDEN);
        }
        usersList.remove(currentName);
        requestHandler.setLogin(false);
        messageList.put(messageList.size(), new Message(-1, "server",
                "User: " + currentName + " disconnected."));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "bye");
        return HttpParser.makeResponse(SUCCESS_OK_CODE, request.getContentType(), jsonObject.toJSONString());
    }

    private String postMessages(RequestData request) throws RestException {
        if (!request.getContentType().equals(KEY_CONTENT_TYPE)) {
            throw new RestException("ContentType is not application/json", CLIENT_ERROR_BAD_REQUEST);
        }
        if (!isUser(request.getToken())) {
            throw new RestException("Wrong token", CLIENT_ERROR_FORBIDDEN);
        }
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObj = (JSONObject) parser.parse(request.getMessage());
            String message = (String) jsonObj.get("message");
            if(message.equals(""))
                throw new RestException("Empty message",CLIENT_ERROR_BAD_REQUEST);
            Message message1 = new Message(messageList.size(), currentName, message);
            messageList.put(messageList.size(), message1);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("id", message1.getId());
            jsonResponse.put("message", message);
            requestHandler.setLastActingDate(new Date());
            return HttpParser.makeResponse(SUCCESS_OK_CODE, request.getContentType(), jsonResponse.toString());
        } catch (ParseException e) {
            throw new RestException("Parse exception in postMessage", CLIENT_ERROR_BAD_REQUEST);
        }
    }

    private boolean isUser(String token) throws RestException {
        if(currentName == null){throw new RestException("Unauthorized user", CLIENT_ERROR_UNAUTHORIZED);}
        String rightToken = usersList.get(currentName).getUserToken().toString();
        return rightToken.equals(token);
    }
}
