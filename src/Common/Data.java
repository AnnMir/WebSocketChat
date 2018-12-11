package Common;

public class Data {
    private String nameOfMethod;
    private String message;
    public Data(String nameOfMethod, String message) {
        this.nameOfMethod = nameOfMethod;
        this.message = message;
    }

    public String getNameOfMethod() {
        return nameOfMethod;
    }

    public String getMessage() {
        return message;
    }
}