/**
 * Computación Concurrente.
 * Implementación de Quicksort en paralelo usando paso de mensajes.
 * La implementación de paso de mensajes usada es ExtendedRendezvous.
 */

import java.io.Serializable;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Arrays;

public class Quicksort implements Serializable {

    private int [] elem; // Arreglo de elementos a ordernar
    private int izq, i, der, d; //indices
    private int rango;
    
    /**
     * Constructor de Quicksort a partir de un arreglo de enteros
     * @param us_elem - arreglo sin ordenar de enteros
     */
    public Quicksort(int [] us_elem){
	this.elem = us_elem.clone();
	izq = der = -1;
    }

    /**
     * Constructor de Quicksort a partir de dos enteros (indices)
     * @param izq, der - indices sobre los que se inicia el algoritmo
     */
    private Quicksort(int izq, int der){
	this.izq = izq;
	this.der = der;
    }

    /**
     * Clase que funciona como servidor para el paso de mensajes de Quicksort
     * La mayor parte del trabajo se hace aquí.
     */
    protected static class QuicksortServer implements Runnable{
	Quicksort qs;
	ServerSocket server;
	Socket socket;
	ExtendedRendezvous<Quicksort> er;

	public QuicksortServer(int izq, int der){
	    this.qs = new Quicksort(izq,der);
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

    protected static class QuicksortClient implements Runnable{
	Quicksort qs;
	Socket socket;
	ExtendedRendezvous<Quicksort> er;
	public QuicksortClient(Quicksort qs){
	    this.qs = qs;
	    Thread qst = new Thread(this);
	    qst.start();
	}

	@Override
	public void run(){
	    try{
		socket = new Socket("localhost", 8080);
		er = new ExtendedRendezvous<Quicksort>(socket);
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
    /*método auxiliar para intercambiar el valor de dos variables*/
    private static void swap(int x, int y){
	int tmp = x;
	x = y;
	y = tmp;
    }

    /*método auxiliar para imprimir un arreglo (debugging)*/
    private static void printArray(int [] a){
	System.out.print("[");
	for (int x = 0; x < a.length; x++){
	    if (x+1 == a.length) System.out.print(a[x]);
	    else System.out.print(a[x]+",");
	}
	System.out.print("]\n");
    }
    
    public static void main(String[] args){
	int num_elem = 100; // número de elementos a ordenar
	int rango = (int) Math.pow(2,8); // rango de los elementos en el arreglo
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
	int [] us_elem = new int [num_elem]; //arreglo donde se guardaran los numeros sin ordenar
	/*genera e imprime los números*/
	for (int k = 0; k < us_elem.length; k++)
	    us_elem[k] = (int)(Math.random()*rango+1);
	printArray(us_elem);
	QuicksortServer qss = new QuicksortServer(us_elem[0], us_elem[us_elem.length-1]);
	QuicksortClient qsc = new QuicksortClient(new Quicksort(us_elem[0], us_elem[us_elem.length-1])); //wrapped af
	//printArray(qsc.er.requestAndAwaitReply(qsc.qs).elem); // cuántos puntos hay ahí???
    }

}    
