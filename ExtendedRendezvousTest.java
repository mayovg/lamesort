import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
public class ExtendedRendezvousTest {
    
    public static void main(String[] args) {
        boolean isServer = "server".equals(args[0]);
        if (isServer) {
            startServer();
	    startClient();
        } else {
            startClient();
        }
    }
    
    public static void startServer() {
        try{
	    ServerSocket servidor = new ServerSocket(3000);
	    System.out.println("Servidor escuchando");
	    Socket socket;
	    ExtendedRendezvous<Message> extrend;
	    Message msj;
	    int msjId = 0; //id del mensaje
	    while (true){
	        socket = servidor.accept();
		System.out.println("socket conectado");
		extrend = new ExtendedRendezvous<Message>(socket);
		System.out.println("Rendezvous inicializado");
		msj = (Message) extrend.requestAndAwaitReply(new Message(msjId++,"servido "+msjId));
		System.out.println(msj.toString());
		//extrend.response(new Message(msjId++, "servido"));
	    }
 	} catch (Exception ex) {ex.printStackTrace();}
	finally{
	    socket.close();
	    extrend.close();
	}
    }
    
    public static void startClient() {
	try{
	    Socket socket;
	    ExtendedRendezvous<Message> extrend;
	    int msjId = 0;
	    while (true){
		socket = new Socket("localhost", 3000);
		extrend = new ExtendedRendezvous<Message>(socket);
		Message msj = (Message)extrend.requestAndAwaitReply(new Message(msjId, "hola"));
		System.out.println(msj.toString());
	    }
	} catch(Exception ex) {ex.printStackTrace();}
	finally{
	    socket.close();
	    extrend.close();
	}
    }
}
