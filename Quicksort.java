public class Quicksort implements Runnable {

    private int [] elem; // Arreglo de elementos a ordernar
    private int izq, der; //indices para los hilos

    public Quicksort(int numElem){
	this.elem = new int[numElem];
	izq = der = -1;
    }

    private Quicksort(int izq, int der){
	super("Quicksort desde :" +izq+ " hasta "+der);
	this.izq = izq;
	this.der = der;
    }

    private static void swap(int x, int y){
	int tmp = x;
	x = y;
	y = tmp;
    }
    
    private static Thread QuicksortThread(int izq, int der){
	Quicksort qs = new Quicksort(izq,der);
	Thread qst = new Thread(qs);
	qst.start();
	return qst;
    }

    @Override
    public void run(){
	int piv = elem[izq]; //pivote
	int i = izq,
	    d = der;
	Thread qsi = null, // hilo de recursión por izquierda
	    qsd = null; // hillo de recursión por derecha
	if (der - izq <= 0) return;
	boolean done = false;
	while (!done){
	    if (elem[i+1] > piv) {
		while (d > i+1 && elem[d] > piv) d--;
		if (d > i+1){
		    i++;
		    swap(d,i);
		    done = i >= d-1;
		} else done = true;
	    } else {
		i++;
		done = i >= d;
	    }
	    swap(elem[izq], elem[i]);
    }
    
    
    public static void main(String... args){

    }
    
}
