package Common;

public final class Commons {
    public final static int SUCCESS_OK_CODE = 200;
    public final static int CLIENT_ERROR_BAD_REQUEST = 400;
    public final static int CLIENT_ERROR_UNAUTHORIZED = 401;
    public final static int CLIENT_ERROR_FORBIDDEN = 403;
    public final static int CLIENT_ERROR_NOT_FOUND = 404;
    public final static int CLIENT_ERROR_METHOD_NOT_ALLOWED = 405;
    public final static int SERVER_ERROR = 500;

    public final static String KEY_POST_LOGIN = "login";
    public final static String KEY_POST_LOGOUT = "logout";
    public final static String KEY_POST_MESSAGE = "message";

    public final static String KEY_GET_ONE_USER = "users/";
    public final static String KEY_GET_ALL_USERS = "users";
    public final static String KEY_GET_MESSAGES = "messages";

    public static final int BUF_SIZE = 16 * 1024;
    public static final int TIMEOUT_PERIOD = 150000;
}
