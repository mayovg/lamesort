import java.io.Serializable;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Arrays;

public class Quicksort implements Serializable {

    private int [] elem; // Arreglo de elementos a ordernar
    private int izq, der; //indices para los hilos
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

    private static void swap(int x, int y){
	int tmp = x;
	x = y;
	y = tmp;
    }
    
    public static void main(String[] args){
	
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

	@Override
	public void run(){
	    try {
		server = new ServerSocket(8080);
		socket = server.accept();
		er = new ExtendedRendezvous<Quicksort>(socket);
		qs = er.getRequest();
		int piv = qs.elem[qs.izq];
		int i = qs.izq, d = qs.der;
		if (qs.der - qs.izq <= 0) return;
		boolean done = false;
		while (!done){
		    if (qs.elem[i+1] > piv){
			while(d > i+1 && qs.elem[d] > piv) d--;
			if (d > i+1){
			    i++;
			    swap(d,i);
			    done = (i >= d-1);
			} else done = true;
		    } else {
			i++;
			done = i >= d;
		    }
		    swap(qs.elem[izq], qs.elem[i]);
		}
	    } catch(Exception e) {e.printStackTrace();}
	}
    }

    protected class QuicksortClient implements Runnable{
	
	public QuicksortClient(int izq, int der){
	    Quicksort qs = new Quicksort(izq,der);
	    Thread qst = new Thread(this);
	    qst.start();
	    //return qst;
	}

	@Override
	public void run(){
	    try{
	    Socket socket = new Socket("localhost", 8080);
	    ExtendedRendezvous<Quicksort> er = new ExtendedRendezvous<Quicksort>(socket);
	    } catch (Exception e){e.printStackTrace();}
	}
    }
	//@Override
	public void run(){	
	    int piv = elem[izq]; //pivote
	    int i = izq,
		d = der;
	    Thread qsi = null, // hilo de recursión por izquierda
		qsd = null; // hillo de recursión por derecha
	    if (der - izq <= 0) return; // nothing to do here
	    boolean done = false;
	    while (!done){
		if (elem[i+1] > piv) {
		    while (d > i+1 && elem[d] > piv) d--;
		    if (d > i+1){
			i++;
			swap(d,i);
			done = (i >= d-1);
		    } else done = true;
		} else {
		    i++;
		    done = i >= d;
		}
		swap(elem[izq], elem[i]);
	    }
	    //if(der-(i+1) > 0) qsd; //recursión por derecha
	    //if((i-1)-izq > 0) qsi; //recursión por izquierda

	    /*Comunicación*/
	}
}    
