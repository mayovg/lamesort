import java.io.Serializable;

public class Message implements Serializable{
    private final long id;
    private final String msg;

    public Message(long id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", msg=" + msg + '}';
    }
}
