package Server;

public class RequestData {
    private String method;
    private String uri;
    private String token;
    private String contentType;
    private String message;

    RequestData method(String method) {
        this.method = method;
        return this;
    }

    RequestData URI(String URI) {
        this.uri = URI;
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    void setContentType(String contentType) {
        this.contentType = contentType;
    }

    void setToken(String token) {
        this.token = token;
    }

    String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getToken() {
        return token;
    }

    public String getContentType() {
        return contentType;
    }
}

