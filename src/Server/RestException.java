package Server;

import static Common.Commons.CLIENT_ERROR_UNAUTHORIZED;

public class RestException extends Exception {
    private static final String HttpVersion = "HTTP/1.1";
    private static final String HttpHeaderDelimiter = "\r\n\r\n";
    private static final String HttpLineDelimiter = "\r\n";
    private static final String HttpWordDelimiter = " ";

    private String message;
    private int code;
    private String headerName;

    RestException(String message1, int code) {
        this.message = message1;
        this.code = code;
    }

    @Override
    public String getMessage() {
        if(code==CLIENT_ERROR_UNAUTHORIZED) {
            headerName="WWW-Authenticate: ";
        } else {
            headerName="Content-Type: ";
        }
        String response = HttpVersion + HttpWordDelimiter + code + HttpWordDelimiter + HttpLineDelimiter;
        response = response + headerName + message + HttpHeaderDelimiter;
        return response;
    }
}