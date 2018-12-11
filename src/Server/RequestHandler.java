package Server;


import Common.Message;
import Common.User;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import static Common.Commons.CLIENT_ERROR_METHOD_NOT_ALLOWED;
import static Common.Commons.TIMEOUT_PERIOD;

public class RequestHandler {

    private String currentName;
    private boolean login;
    private ConcurrentHashMap<String, User> usersList;
    private ConcurrentHashMap<Integer, Message> messageList;
    private Date lastActingDate;
    private Timer timer;

    RequestHandler(final ConcurrentHashMap<String, User> users, final ConcurrentHashMap<Integer, Message> messages) {
        usersList = users;
        messageList = messages;
        login = true;
        lastActingDate = new Date();
        timer = new Timer();
        timer.schedule(new TimeOutTask(this), 0, TIMEOUT_PERIOD);
    }

    String handle(String httpRequest) throws RestException {
        RequestData request = HttpParser.parseRequestHttp(httpRequest);

        switch (request.getMethod()) {
            case "POST": {
                Post post = new Post(usersList, messageList, this, request);
                return post.work();
            }
            case "GET": {
                Get get = new Get(usersList, messageList, this, request);
                return get.work();
            }
            default: {
                throw new RestException("", CLIENT_ERROR_METHOD_NOT_ALLOWED);
            }
        }
    }

    boolean isLogin() {
        return login;
    }

    void TimeoutLogout() {
        System.out.println(currentName + " offline");
        if (null == currentName) {
            return;
        }
        usersList.get(currentName).setOnline(false);
        login = false;
        messageList.put(messageList.size(), new Message(-1, "server",
                "User: " + currentName + " disconnected with timeout."));
        timer.cancel();
    }

    public String getCurrentName() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public Date getLastActingDate() {
        return lastActingDate;
    }

    public void setLastActingDate(Date lastActingDate) {
        this.lastActingDate = lastActingDate;
    }
}
