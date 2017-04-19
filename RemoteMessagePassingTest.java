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
	    ServerSocket servidor = new ServerSocket(3000);
	    System.out.println("servidor escuchando");
	    Socket socket;
	    RemoteMessagePassing<Message> pasoMensajes;
	    Message enviado,recibido;
	    int id = 1; //id del mensaje
	    while(true){
		System.out.println("entra a while");
		socket = servidor.accept();
		System.out.println("socket de cliente");
		pasoMensajes = new RemoteMessagePassing<Message>(socket);
		System.out.println("se inicia paso de mensajes");
		recibido = (Message) pasoMensajes.receive();
		System.out.println("Mensaje "+recibido.toString()+" recibido en el servidor");
		enviado = new Message(id++, "hola"+id);
		pasoMensajes.send(enviado);
		System.out.println("Mensaje "+enviado.toString()+" enviado al cliente");
	    }
	} catch(IOException ioe){ioe.printStackTrace();}

    }
    
    public static void startClient() {
        try{
	    Socket socket;
	    RemoteMessagePassing <Message> pasoMensajes;
      Message enviado, recibido;
      int id = 1;
	    while (true){
    		socket = new Socket("localhost", 3000);
	    	System.out.println("socket conectado con éxito");
		pasoMensajes = new RemoteMessagePassing<Message>(socket);
		enviado = new Message(id++, "ni hao"+id);
		pasoMensajes.send(enviado);
		System.out.println("Mensaje "+enviado.toString()+" enviado al servidor");
		recibido = (Message)pasoMensajes.receive();
		System.out.println("Mensaje "+recibido.toString()+" recibido en el cliente");
	    }
	} catch(IOException ioe){ioe.printStackTrace();}
    }
}
