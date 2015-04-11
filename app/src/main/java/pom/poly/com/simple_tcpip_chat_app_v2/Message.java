package pom.poly.com.simple_tcpip_chat_app_v2;

/**
 * Created by User on 12/4/2015.
 */
public class Message {
    String message;
    boolean left;

    public Message(String message, boolean left) {
        this.message = message;
        this.left = left;
    }

    @Override
    public String toString() {
        return message;
    }
}
