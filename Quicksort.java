/**
 * Computación Concurrente.
 * Implementación de Quicksort en paralelo usando paso de mensajes.
 * La implementación de paso de mensajes usada es ExtendedRendezvous
 */

import java.io.Serializable;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Arrays;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Quicksort implements Serializable{
    
    int [] elem; // Arreglo de elementos a ordernar
    int izq, der; //indices    

    /**
     * Constructor de Quicksort a partir de un arreglo de enteros
     * @param us_elem - arreglo sin ordenar de enteros
     */
    public Quicksort(int [] us_elem){
	this.elem = us_elem;
	izq = 0;
	der = elem.length - 1;
    }

    /**
     * Constructor de Quicksort a partir de dos enteros (indices)
     * @param izq, der - indices sobre los que se inicia el algoritmo
     */
    public Quicksort(int [] ar,int izq, int der){
	this.izq = izq;
	this.der = der;
	this.elem = Arrays.copyOfRange(ar,izq, der);
    }

    /**
     * Representación en cadena del arreglo ordenado
     */
    @Override
    public String toString(){
	return printArray(this.elem);
    }

    /**
     * Clase para el thread cliente del paso de mensajes.
     * Genera un arreglo aleatorio de la longitud que recibe como parametro
     * y envia este arreglo al servidor para que este lo ordene
     */
    static class QuicksortClient implements Runnable{
	int [] array;
	Random ran;
	ExtendedRendezvous<Quicksort> rms;
	
	/**
	 * Constructor del cliente
	 * @param arr_size - tamaño del arreglo que se ordenara
	 */
	QuicksortClient(int arr_size){
	    array = new int[arr_size];
	    ran = new Random();
	    new Thread(this).start();
	}

	@Override
	public void run(){
	    // Genera los elementos del arreglo pseudoaleatoriamente
	    for (int i=0; i<array.length; i++)
		array[i] = ran.nextInt(array.length*20);
	    Quicksort qs = new Quicksort(array);
	    System.out.println("Arreglo sin ordenar: \n" +qs.toString());
	    try {
		Socket socket = new Socket("localhost",3000);
		System.out.println("Se conectó un socket al servidor");
		 rms = new ExtendedRendezvous<Quicksort>(socket);
		System.out.println("Se inició la Rendezvous con el socket");
		rms.response(qs); // envia el objeto Quicksort al servidor 
		//System.out.println("el cliente solicita al servidor que se ordene su arreglo");
		qs = rms.getRequest(); // espera a que el servidor conteste con el arreglo ordenado
		System.out.println("Arreglo ordenado: "+qs.toString());
		System.exit(1);
	    } catch(Exception ex){ex.printStackTrace();}
	}
    }

    /**
     * Clase para el servidor del paso de mensajes.
     * Recibe del cliente un mensaje (objeto Quicksort) con un arreglo para ordenarlo.
     */
    static class QuicksortServer implements Runnable{
	ExtendedRendezvous rmp;
	ServerSocket server;
	Socket socket;
	Quicksort qs;
	
	/**
	 * Constructor para el servidor del paso de mensajes de Quicksort
	 */
	public QuicksortServer(){
	     try {
		 server = new ServerSocket(3000);
	     } catch(Exception ex){ex.printStackTrace();}
	     new Thread(this).start();
	}

	/*algoritmo*/
	static void sort(int [] ar, int ini, int fin){
	    if (fin - ini < 1){
		return;
	    }
	    int i = ini + 1;
	    int j = fin;
	    while (i < j){
		if (ar[i] > ar[ini] && ar[j] <= ar[ini]){
		    swap(ar,i++, j--);
		}
		else if(ar[i] <= ar[ini]){
		    i++;
		}
		else{
		    j--;
		}
	    }
	    if (ar[i] > ar[ini])
		i--;
	    swap(ar, i, ini);
	    sort(ar, ini,i-1);
	    sort(ar, i+1, fin);
	}
	
	/*método auxiliar para intercambiar el valor de dos variables*/
	private static void swap(int[] a, int x, int y){
	    if(x == y)
		return;
	    int tmp = a[x];
	    a[x] = a[y];
	    a[y] = tmp;
	}
	
	@Override
	public void run(){
	    try {
		System.out.println("Se inicio el servidor");
		/* protocolo de envio de mensajes */
		socket = server.accept();
		System.out.println("Hay una nueva conexión");
		rmp = new ExtendedRendezvous<Quicksort>(socket);
		qs  = (Quicksort) rmp.getRequest();
		if (qs != null){
		    System.out.println("se recibió solicitud");
		}
		else{
		    System.out.println("no se recibió solicitud");
		    System.exit(-1);
		}
		System.out.println("Ordenando el arreglo recibido");
		int[] ar = qs.elem;
		sort(ar, qs.izq, qs.der);      
		Quicksort ord = new Quicksort(ar);
		rmp.response(ord);
	    
	    } catch(Exception e) {e.printStackTrace();}
	    finally{
		try{
		    socket.close();
		    rmp.close();
		} catch(IOException ioe){ioe.printStackTrace();}
	    }
	}	
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
	int num_elem = 1000; // número de elementos a ordenar
	int rango = (int) Math.pow(2,8); // rango de los elementos en el arreglo
	if(args.length < 1 )
	    System.out.println("Uso: java Quicksort <número de elementos>");
	try{
	    num_elem = Integer.parseInt(args[0]);
	    if (num_elem < 1)
		num_elem = 1000;
	} catch(Exception ex){ex.printStackTrace();}
	QuicksortServer server = new QuicksortServer();
	QuicksortClient client = new QuicksortClient(num_elem);	    
    }  
}
