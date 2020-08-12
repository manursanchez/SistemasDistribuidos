/* AUTOR: Manuel Rodríguez Sánchez
 * CORREO ELECTRÓNICO: mrodrigue212@alumno.uned.es*/
package repositorio;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

//import java.util.ArrayList;
//import java.util.List;
import common.Fichero;
import common.Herramienta;
import cliente.ServicioDiscoClienteInterface;

public class ServicioSrOperadorImpl extends UnicastRemoteObject implements ServicioSrOperadorInterface{
	private static final long serialVersionUID = 1L;
    private static ServicioDiscoClienteInterface discoCliente;
	
    protected ServicioSrOperadorImpl() throws RemoteException {
        super();
        // TODO Auto-generated constructor stub
    }
 
    // Método que crea una carpeta con el nombre del cliente en el caso que no exista
    public void crearCarpeta(String nombre) throws RemoteException{
    	File nombreCarpeta=new File(nombre);
    	nombreCarpeta.mkdir();
    }
    
    @Override
    public void bajarFicheroPaso2(int repositorioCliente, String archivo, String nombreCliente)throws RemoteException{
    	int tamanoRutaTemporal=0;
    	
    	//Se extrae la dirección de la carpeta del cliente, con el ejecutable incluido.
    	
    	URL rutaca = ServicioSrOperadorImpl.class.getProtectionDomain().getCodeSource().getLocation();
        
    	//Se desmembra la dirección y se mete en un array
    	String []rutaTemporal = rutaca.toString().split("/");
    	
    	//Se vuelve a construir la dirección pero mas exacta y limpia.
    	StringBuilder rutaFinal = new StringBuilder();
    	
    	//Aquí controlamos que se ejecute con .jar o sin .jar
    	if (Herramienta.comprobarSufijoCadena(rutaTemporal))
        	tamanoRutaTemporal=rutaTemporal.length-1;
        else
        	tamanoRutaTemporal=rutaTemporal.length;
        
        for(int i = 1; i < tamanoRutaTemporal; i++){       
            rutaFinal.append(rutaTemporal[i]);
            rutaFinal.append("/");
        }
    	
        
        //Se le añade el cliente para mandarla al objeto fichero.
        String ruta = rutaFinal.toString() + nombreCliente;
        //System.out.println("El directorio es: "+ruta);
        Fichero fichero = new Fichero(ruta,archivo,nombreCliente);
        
        try {
            discoCliente = (ServicioDiscoClienteInterface) Naming.lookup("rmi://localhost:2000/DiscoCliente/"+repositorioCliente);
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        discoCliente.bajarFicheroPaso3(fichero);
    }

    public void mostrarListaFicheros(ArrayList<String> ListaFicheros) throws RemoteException{
    	System.out.println();
    	System.out.println("Mostrando ficheros: ");
    	for (int contador=0;contador< ListaFicheros.size();contador++) {
          	  System.out.println(ListaFicheros.get(contador));
            }
    	System.out.println();
    	System.out.println("Fin de listado.");
    	System.out.println("--------------------------");
       }
    
}
