import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

public class RemoteMessagePassingTest {
        
    public static void main(String[] args) {
        boolean isServer = "server".equals(args[0]);

        if (isServer) {
            startServer();
        } else{
            startClient();
        }
    }
    
    public static void startServer() {
	try{
	    ServerSocket servidor = new ServerSocket(8080);
	    System.out.println("servidor escuchando");
	    Socket socket;
	    RemoteMessagePassing<Message> pasoMensajes;
	    Message msj;
	    int id = 1; //id del mensaje
	    while(true){
		System.out.println("entra a while");
		socket = servidor.accept();
		System.out.println("socket de cliente");
		pasoMensajes = new RemoteMessagePassing<Message>(socket);
		System.out.println("se inicia paso de mensajes");
		msj = new Message(id++, "hola");
		pasoMensajes.send(msj);
		System.out.println("Mensaje "+msj.toString()+" enviado");
	    }
	} catch(IOException ioe){ioe.printStackTrace();}

    }
    
    public static void startClient() {
        try{
	    Socket socket = new Socket("localhost", 3000);
	    System.out.println("socket conectado con éxito");
	    RemoteMessagePassing <Message> pasoMensajes;
	    while (true){
		pasoMensajes = new RemoteMessagePassing(socket);
		Message msj = (Message)pasoMensajes.receive();
		System.out.println(msj.toString());
	    }
	} catch(IOException ioe){ioe.printStackTrace();}
    }
}
