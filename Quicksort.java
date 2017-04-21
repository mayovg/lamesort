import java.io.Serializable;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Arrays;

public class Quicksort implements Serializable {

    private int [] elem; // Arreglo de elementos a ordernar
    private int izq, i, der, d; //indices
    private int rango;
    private int num_elem; // Número de elementos a ordenar
    
    public Quicksort(int [] us_elem){
	this.elem = us_elem.clone();
	izq = der = -1;
    }

    private Quicksort(int izq, int der){
	this.izq = izq;
	this.der = der;
    }

    protected class QuicksortServer implements Runnable{
	Quicksort qs;
	ServerSocket server;
	Socket socket;
	ExtendedRendezvous<Quicksort> er;

	public QuicksortServer(Quicksort qs){
	    this.qs = qs;
	    Thread qst = new Thread(this);
	    qst.start();
	}

	/*Hace todo el trabajo*/
	@Override
	public void run(){
	    try {
		server = new ServerSocket(8080);
		socket = server.accept();
		er = new ExtendedRendezvous<Quicksort>(socket);
		qs = er.getRequest();
		int piv = qs.elem[qs.izq];
		qs.i = qs.izq;
		qs.d = qs.der;
		if (qs.der - qs.izq <= 0) return;
		boolean done = false;
		while (!done){
		    if (qs.elem[(qs.i)+1] > piv){
			while(qs.d > (qs.i+1) && qs.elem[qs.d] > piv) (qs.d)--;
			if (qs.d > (qs.i)+1){
			    (qs.i)++;
			    swap(qs.d,qs.i);
			    done = (qs.i >= (qs.d)-1);
			} else done = true;
		    } else {
			(qs.i)++;
			done = qs.i >= qs.d;
		    }
		    swap(qs.elem[qs.izq], qs.elem[qs.i]);
		}
		er.response(qs);
	    } catch(Exception e) {e.printStackTrace();}
	}
    }

    protected class QuicksortClient implements Runnable{
	Quicksort qs;
	public QuicksortClient(int izq, int der){
	    this.qs = new Quicksort(izq,der);
	    Thread qst = new Thread(this);
	    qst.start();
	}

	@Override
	public void run(){
	    try{
		Socket socket = new Socket("localhost", 8080);
		ExtendedRendezvous<Quicksort> er = new ExtendedRendezvous<Quicksort>(socket);
		Quicksort aux = er.requestAndAwaitReply(this.qs);
		if((aux.der - ((aux.i)+1) > 0)){
		    Quicksort qsd = er.requestAndAwaitReply(new Quicksort((aux.i)+1, aux.der));	
		}
		if (((aux.i)-1)-aux.izq > 0){
		    Quicksort qsi = er.requestAndAwaitReply(new Quicksort(aux.izq, (aux.i)-1));
		}	    
	    } catch (Exception e){e.printStackTrace();}
	}
    }
    
    private static void swap(int x, int y){
	int tmp = x;
	x = y;
	y = tmp;
    }
    
    public static void main(String[] args){
	int num_elem = 10000; // número de elementos a ordenar
	int rango = (int) Math.pow(2,18); // rango de los elementos en el arreglo
	System.out.println("Uso: java Quicksort -nNumeroDeElementos -rRangoDeValores");
	for (int n = 0; n < args.length; n++){
	    try{
		if (args[n].startsWith("-n")){
		    num_elem = Integer.parseInt(args[n].substring(1));
		    if (num_elem <= 0){
			num_elem = 1000;
		    }
		} else if (args[n].startsWith("-r")){
		    rango = Integer.parseInt(args[n].substring(1));
		    if(rango <= 0) rango = 10000;
		}
	    } catch(Exception some_exception){some_exception.printStackTrace();}
	}
	
    }

}    
