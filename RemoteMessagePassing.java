import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class RemoteMessagePassing<T extends Serializable> {
    private Socket socket;
    private ObjectOutputStream salida; // datos de salida para el socket
    private ObjectInputStream entrada; // datos de entrada para el socket 
    private Object enviando; // monitor de envio
    private Object recibiendo; // monitor de recepción
    
    public RemoteMessagePassing(Socket socket) {
	if (socket == null){
	    throw new NullPointerException();
	} else{
	    this.socket = socket;
	    try {
		salida = new ObjectOutputStream(socket.getOutputStream());
		entrada = new ObjectInputStream(socket.getInputStream());
	    } catch (IOException ioe){ioe.printStackTrace();}
	}
	enviando = new Object();
	recibiendo = new Object();
    }
    
    public void send(T obj) {
	synchronized (enviando){
	    try{
		salida.writeObject(obj);
		salida.flush();
	    } catch(IOException ioe){ioe.printStackTrace();}
	}
    }
    
    public T receive() {
	T valor = null;
	synchronized (recibiendo){
	    try{	    
		valor = objectToGeneric(entrada.readObject());
	    } catch (Exception e){e.printStackTrace();}
	}
	return valor;
    }
    
    @SuppressWarnings("unchecked")
    private T objectToGeneric(Object obj){
	return (T) obj;
    }

    public void close() throws IOException {
	if (entrada != null)
	    entrada.close();
	if (salida != null)
	    salida.close();
	if (socket != null)
	    socket.close();
    }
}

