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
     * Representación en cadena del arreglo ordenado
     */
    @Override
    public String toString(){
	return printArray(this.elem);
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
		server = new ServerSocket(3000);
		while(true){
		    socket = server.accept();
		    er = new ExtendedRendezvous<Quicksort>(socket);
		    qs = er.getRequest();
		    if (qs != null){
			System.out.println("se recibió solicitud");
			System.out.println(qs.toString());
		    }
		    else{
			System.out.println("no se recibió solicitud");
			break;
		    }
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
		    socket.close();
		}
	    } catch(Exception e) {e.printStackTrace();}
	    finally{
		er.close();
	    }
	}
    }

    protected static class QuicksortClient implements Runnable{
	Quicksort quick;
	Socket socket;
	ExtendedRendezvous<Quicksort> er;
	public QuicksortClient(Quicksort qs){
	    this.quick = qs;
	    Thread qst = new Thread(this);
	    qst.start();
	}

	@Override
	public void run(){
	    try{
		socket = new Socket("localhost", 3000);
		er = new ExtendedRendezvous<Quicksort>(socket);
		while(true){
		    er.response(quick);	   
		    Quicksort aux = er.getRequest();
		    if(aux != null){
			System.out.println("se envió solicitud"); 
			System.out.println("se recibió como respuesta: "+ aux.toString());
		    }else {
			System.out.println("no se recibió respuesta");
		    }
		    if((aux.der - ((aux.i)+1) > 0)){
			Quicksort qsd = er.requestAndAwaitReply(new Quicksort((aux.i)+1, aux.der));	
		    }
		    if (((aux.i)-1)-aux.izq > 0){
			Quicksort qsi = er.requestAndAwaitReply(new Quicksort(aux.izq, (aux.i)-1));
		    }
		}
		//socket.close();
	    } catch (Exception e){e.printStackTrace();}
	    finally {
		er.close();
	    }
	}
    }
    /*método auxiliar para intercambiar el valor de dos variables*/
    private static void swap(int x, int y){
	int tmp = x;
	x = y;
	y = tmp;
    }

    /*método auxiliar para imprimir un arreglo (debugging)*/
    private static String printArray(int [] a){
	String stb = "[";
	for (int x = 0; x < a.length; x++){
	    if (x+1 == a.length) stb+=a[x];
	    else stb+=a[x]+",";
	}
	return stb+="]\n";
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
	System.out.print("Elementos sin ordenar:\n");
	System.out.println(printArray(us_elem));
	QuicksortClient qsc = new QuicksortClient(new Quicksort(us_elem[0], us_elem[us_elem.length-1])); //wrapped af
	QuicksortServer qss = new QuicksortServer(us_elem[0], us_elem[us_elem.length-1]);
	//printArray(qsc.er.requestAndAwaitReply(qsc.qs).elem); // cuántos puntos hay ahí???
    }

}    
