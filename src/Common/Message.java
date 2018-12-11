package Common;

public class Message {
    private int id;
    private String author;
    private String message;

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public Message(int id, String author, String message) {

        this.id = id;
        this.author = author;
        this.message = message;
    }
}