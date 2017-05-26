import java.io.Serializable;
import java.io.IOException;
import java.net.Socket;

public class ExtendedRendezvous<T extends Serializable> {
    
    private final RemoteMessagePassing<T> channel;
    
    public ExtendedRendezvous(Socket socket) {
        this.channel = new RemoteMessagePassing<T>(socket);
    }
    
    public T requestAndAwaitReply(T obj) {
        channel.send(obj);
	return channel.receive();
    }
    
    public T getRequest() {
        return channel.receive();
    }
    
    public void response(T obj) {
	channel.send(obj);
    }
    
    public void close() {
	try{
	    channel.close();
	} catch(IOException ioe){ioe.printStackTrace();}
    }

}
