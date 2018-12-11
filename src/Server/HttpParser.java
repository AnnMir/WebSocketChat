package Server;


public class HttpParser {
    private static final String HttpVersion = "HTTP/1.1";
    private static final String HttpHeaderDelimiter = "\r\n\r\n";
    private static final String HttpLineDelimiter = "\r\n";
    private static final String HttpWordDelimiter = " ";

    static RequestData parseRequestHttp(String data) {
        String[] requestStrings = data.split(HttpHeaderDelimiter);
        String[] headerStrings = requestStrings[0].split(HttpLineDelimiter);
        String[] startLineWords = headerStrings[0].split(HttpWordDelimiter);
        RequestData request = new RequestData().method(startLineWords[0]).URI(startLineWords[1]);
        request.setContentType("");
        request.setToken("");
        for (String line : headerStrings) {
            if (line.contains("Content-Type")) {
                request.setContentType(line.split(HttpWordDelimiter)[1]);
                continue;
            }
            if (line.contains("Authorization")) {
                request.setToken(line.split(HttpWordDelimiter)[2]);
            }
        }
        if (requestStrings.length > 1) {
            int endPoint = requestStrings[1].indexOf("\u0000");
            request.setMessage(requestStrings[1].substring(0, endPoint));
        } else {
            request.setMessage("");
        }
        return request;
    }

    public static String makeResponse(int status, String contentType, String message) {
        String response = HttpVersion + HttpWordDelimiter + status + HttpWordDelimiter + HttpLineDelimiter;
        response = response + "Content-Type: " + contentType + HttpHeaderDelimiter + message;
        return response;
    }

}
