package Common;

import java.util.Date;
import java.util.UUID;

public class User {
    private String userName;
    private int userID;
    private UUID userToken;
    private boolean online;
    private Date lastActiveDate;

    public UUID getUserToken() {
        return userToken;
    }

    public void setUserToken(UUID userToken) {
        this.userToken = userToken;
    }



    public void setOnline(boolean online) {
        this.online = online;
    }

    public User(final String userName, final int userID, final boolean online) {
        this.userName = userName;
        this.userID = userID;
        this.online = online;
    }

    public String getUserName() {

        return userName;
    }

    public int getUserID() {
        return userID;
    }

    public boolean isOnline() {
        return online;
    }
}


