/* AUTOR: Manuel Rodr�guez S�nchez
 * CORREO ELECTR�NICO: mrodrigue212@alumno.uned.es*/
package repositorio;
import common.Fichero;
import common.Herramienta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServicioClOperadorImpl extends UnicastRemoteObject implements ServicioClOperadorInterface{
	
	private static final long serialVersionUID = 1L;

	protected ServicioClOperadorImpl() throws RemoteException {
        super();
    }
 
//METODO PARA SUBIR FICHERO	
	@Override
	public boolean subirFichero(Fichero fichero) throws RemoteException {
		OutputStream os;
		File repositorio = new File(fichero.obtenerPropietario());
		if (!repositorio.exists()) 
			repositorio.mkdir();
		
		String nombreFichero = fichero.obtenerPropietario() + "/" + fichero.obtenerNombre();
		
		try {
			os = new FileOutputStream(nombreFichero); 
			if (fichero.escribirEn(os)==false)
			{
				os.close();
				return false;
			}
			os.close();
			//System.out.println("Fichero "+ fichero.obtenerNombre()  + " ( "+ fichero.obtenerPropietario() +" ) recibido");
		} catch (FileNotFoundException fnfe) {
			return false;
		} catch (IOException ioe) {
			return false;	
		}catch (NullPointerException npe){ //Por si no encuentra el archivo
			return false;
		}
		return true;
	}	
	
//FIN DE METODO PARA SUBIR FICHERO	
	
 //M�todo para borrar fichero del repositorio
    @Override
    public boolean borrarFichero(String archivo, String carpeta) throws RemoteException {
        int tamanoRutaTemporal=0;
        URL rutaca = ServicioClOperadorImpl.class.getProtectionDomain().getCodeSource().getLocation(); 
       
        String []rutaTemporal = rutaca.toString().split("/");
        StringBuilder rutaFinal = new StringBuilder();
        //Controlamos como finaliza la ruta del directorio
        if (Herramienta.comprobarSufijoCadena(rutaTemporal))
        	tamanoRutaTemporal=rutaTemporal.length-1;
        else
        	tamanoRutaTemporal=rutaTemporal.length;
        
        for(int i = 1; i < tamanoRutaTemporal; i++){       
            rutaFinal.append(rutaTemporal[i]);
            rutaFinal.append("/");
        }
        
        //System.out.println("Ruta con string builder: " + rutaFinal.toString());
        File ruta = new File(rutaFinal.toString() + carpeta);
         
        if (ruta.exists()) {
            String nombreFichero = ruta + "/" + archivo;
            
            File fichero = new File(nombreFichero);
            if(fichero.delete()){
                //System.out.println("Fichero " + archivo + " borrado correctamente.");
                return true;
            }
            else{
                //System.out.println("No se ha encontrado el fichero.");
                return false;
            }
        } else {
        	//System.out.println("No tienes ningun fichero almacenado.");
        	return false;
        }
    }
    
    @Override
    public ArrayList<String> listarFicherosCliente(String nombreCarpeta) throws RemoteException {
    	 int tamanoRutaTemporal=0;
    	ArrayList<String> listaArchivos =new ArrayList<String>();
    	URL rutaca = ServicioClOperadorImpl.class.getProtectionDomain().getCodeSource().getLocation(); 
            
    	String []rutaTemporal = rutaca.toString().split("/");
         StringBuilder rutaFinal = new StringBuilder(); 
         
         if (Herramienta.comprobarSufijoCadena(rutaTemporal))
         	tamanoRutaTemporal=rutaTemporal.length-1;
         else
         	tamanoRutaTemporal=rutaTemporal.length;
         
         for(int i = 1; i < tamanoRutaTemporal; i++){       
             rutaFinal.append(rutaTemporal[i]);
             rutaFinal.append("/");
         }
         
         //System.out.println("Ruta con string builder: " + rutaFinal.toString());
         File ruta = new File(rutaFinal.toString() + nombreCarpeta);
         
         //Agregamos al arrayList los ficheros.
         File[] ficheros = ruta.listFiles();
         for (int x=0;x<ficheros.length;x++){
        	listaArchivos.add(ficheros[x].getName());
         }	
        // Devolvemos al cliente un arraylist con los archivos	 
         return listaArchivos;
    }
    
}
