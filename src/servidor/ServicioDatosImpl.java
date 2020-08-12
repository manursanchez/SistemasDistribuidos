/* AUTOR: Manuel Rodr�guez S�nchez
 * CORREO ELECTR�NICO: mrodrigue212@alumno.uned.es*/
package servidor;
import java.io.File;
//import java.net.MalformedURLException;
//import java.rmi.Naming;
//import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;


//import repositorio.ServicioSrOperadorInterface;
//import servidor.ServicioGestorInterface;
 
public class ServicioDatosImpl extends UnicastRemoteObject implements ServicioDatosInterface {
 
     private static final long serialVersionUID = 291220122019L;
     // Conjunto de Maps para almacenar y relacionar los clientes, los repositorios y los n�meros de sesi�n
     private Map<Integer, String> sesionCliente = new HashMap<Integer, String>();//Para la autenticaci�n, sesiones o logueos de los clientes.
     private Map<Integer, String> registroCliente = new HashMap<Integer, String>(); //Registro para las altas de clientes.
     
     private Map<Integer, String> sesionRepositorio = new HashMap<Integer, String>();//Para las sesiones o logueos de los repositorios
     private Map<Integer, String> registroRepositorio = new HashMap<Integer, String>();//Para las altas de los repositorios
     
     private Map<Integer, Integer> clienteRepositorio = new HashMap<Integer, Integer>();//Para relacionar repositorios con clientes
 
    
     public ServicioDatosImpl() throws RemoteException {
          super();
     }
 
     public ServicioDatosImpl(String nombreLog) throws RemoteException {
          super();
     }
     
 //METODO QUE RELLENA LOS REGISTROS. PARA NO TENER QUE ESTAR HACIENDOLO CADA VEZ QUE HAGO PRUEBAS
     public boolean rellenarClientesRepositorios() throws RemoteException{
    	 registroCliente.put(1,"MANUEL");
    	 registroCliente.put(2,"ALBERTO");
    	 registroCliente.put(3,"SONIA");
    	 registroCliente.put(4,"ANGELA");
    	 
    	 registroRepositorio.put(1,"R1");
    	 registroRepositorio.put(2,"R2");
    	 registroRepositorio.put(3,"R3");
    	 
    	 return true;
     }
//FIN M�TODO RELLENAR REGISTROS. BORRAR CUANDO ENTREGUE LA PR�CTICA
     
     /*M�todo que a�ade un cliente al sistema recibiendo como par�metro un
     nombre y un c�digo de cliente. No se permiten nombres de clientes repetidos.*/
     @Override
     public boolean altaCliente(String nombre) throws RemoteException {
          // Comprobamos que el cliente no exista
          if (registroCliente.containsValue(nombre.toUpperCase())) {
        	  //System.out.println("�El cliente ya existe!");
        	  return false;
          } else {
              // A�adimos el cliente a la "base de datos"
              int codCliente=1+registroCliente.size();//Asignamos c�digo de cliente.
        	  registroCliente.put(codCliente, nombre.toUpperCase()); 
              return true;
           }
     }//fin m�todo altaCliente
 
     // M�todo que a�ade un Repositorio al sistema recibiendo como par�metros un
     // nombre y una id. No se permiten nombres de Repositorios repetidos.
     @Override
     public boolean altaRepositorio(String nombre) throws RemoteException {
 
         nombre=nombre.toUpperCase(); 
    	 if (registroRepositorio.containsValue(nombre)) {
               //System.out.println("�El repositorio ya existe!");
               return false;
          }
          else {
               int codRepositorio=1+registroRepositorio.size();
               registroRepositorio.put(codRepositorio, nombre.toUpperCase());
               //System.out.println("Dando de alta al repositorio " + nombre + " con c�digo de repositorio: " + codRepositorio);
               return true;
          }
     }
     
     //M�todo que va a a�adir un repositorio autenticado, se crea una sesi�n en el sistema. 
     //Usaremos el map sesionRepositorio.
     public boolean guardarAutenticacionRepositorio(int idSesionRepositorio,String nombre) throws RemoteException 
     {
    	 nombre=nombre.toUpperCase();
    	
    	 if (!registroRepositorio.containsValue(nombre)){//Si no est� registrado devolvemos false y terminamos
    		 //System.out.println("No esiste ningun usuario llamado "+nombre+" en el sistema.");
    		 return false;
    	 }else{
    		 if(sesionRepositorio.containsValue(nombre)){//si est� registrado comprobamos que no est� ya autenticado
    			 //System.out.println("El REPOSITORIO ya est� autenticado en el sistema");
    			 return false;
    		 }else
    		 {
    			 //Guardamos en el map el logueo
    			 sesionRepositorio.put(idSesionRepositorio, nombre); 
    			 //System.out.println("Repositorio "+nombre+" con c�digo de sesi�n "+idSesionRepositorio+" se ha autenticado correctamente en el sistema");
    			 return true;
    		 }
    	 }
     }
     
     //M�todo que desloguea el repositorio. Eliminaremos el repositorio indicado del map sesionRepositorio
     public boolean borrarAutenticacionRepositorio(int cod) throws RemoteException
     {
    	 sesionRepositorio.remove(cod); //eliminamos del hashmap las sesi�n logueada de repositorio
    	 return true;
     }
     
   //M�todo que va a a�adir un cliente autenticado
   //en el sistema. Usaremos el map sesionCliente.
     public boolean guardarAutenticacionCliente(int idSesionCliente,String nombre) throws RemoteException 
     {
    	 nombre=nombre.toUpperCase();
    	 if (!registroCliente.containsValue(nombre)){//Si no est� registrado devolvemos false y terminamos

    		 return false;
    	 }else{
    		 if(sesionCliente.containsValue(nombre)){//si est� registrado comprobamos que no est� ya autenticado
    			 //System.out.println("El usuario ya est� autenticado en el sistema");
    			 return false;
    		 }else
    		 {
    			// Recogemos la lista de repositorios disponibles
                 ArrayList<Integer> repositoriosLogueados = new ArrayList<Integer>(sesionRepositorio.keySet());
           	  
                  if (repositoriosLogueados.isEmpty()) {
                       //System.out.println("No hay repositorios logueados disponibles.");
                       return false;
                  } else {
                       // A�adimos la sesi�n del cliente al map
                	  sesionCliente.put(idSesionCliente, nombre); 
    			 
                	  //System.out.println("Usuario "+nombre+" autenticado correctamente en el sistema con c�digo: "+idSesionCliente);
                       
                	  // Asociamos al cliente con un repositorio al azar
                       
                       int n = (int) (Math.random() * repositoriosLogueados.size());
                       //System.out.println("La variable n contiene: "+n);
                       @SuppressWarnings("unused")
					int repositorioSeleccionado=repositoriosLogueados.get(n);
                       //System.out.println("El repositorio seleccionado es: "+repositorioSeleccionado);
                       
                       clienteRepositorio.put(idSesionCliente, repositoriosLogueados.get(n));
                       
                       return true;
                   }
    		 }
    	 }
     }
     
     //M�todo que desloguea el cliente
     public boolean borrarAutenticacionCliente(int cod) throws RemoteException
     {
    	 sesionCliente.remove(cod);//eliminamos del hashmap la sesion de logueo del cliente 
    	 return true;
     }
 
     // Metodo que devuelve una colecci�n con los nombres de los Clientes registrados
     @Override
     public Collection<String> listaClientes() throws RemoteException {
          Collection<String> clientes = (Collection<String>) registroCliente.values();
          return clientes;
     }
     //M�todo que devuelve los clientes que est�n autenticados en el sistema
     @Override
     public ArrayList<String> listaClientesAutenticados() throws RemoteException {
    	 ArrayList<String> clientesA = new ArrayList<String>(sesionCliente.values());
    	 return clientesA;
     }
     // Metodo que devuelve una colecci�n con los nombres de los Repositorios.
     @Override
     public Collection<String> listaRepositorios() throws RemoteException {
          Collection<String> repositorios = (Collection<String>) registroRepositorio.values();   
          return repositorios;
     }

     // Metodo que devuelve la id del repositorio que le corresponde a un cliente
     // cuya id se pasa como par�metro.
     @Override
     public int buscaRepositorio(int idSesionCliente) throws RemoteException {
 
          int idSesionRepositorio = 0;
          try {
               idSesionRepositorio = clienteRepositorio.get(idSesionCliente);//Coge la idSesionCliente extraemos de la relaci�n CLIENTE-REPOSITORIO 
               														//del idSesionRepositorio que usaremos para operar en el repositorio del cliente.
               														 
          } catch (Exception e) {
               //System.out.println("ServicioDatosImpl.buscaRepositorio: " + e.getMessage());
               System.out.println(clienteRepositorio.get(idSesionCliente));
          }
          return idSesionRepositorio;
     }
 
     // M�todo que imprime por pantalla la lista de repositorios junto con los
     // clientes asociados a cada uno de ellos.
     
     @Override
     public void listarRepositoriosClientes() throws RemoteException {
    	 ArrayList<Integer> repositorios = new ArrayList<Integer>(sesionRepositorio.keySet());
    	 ArrayList<Integer> clientes = new ArrayList<Integer>(sesionCliente.keySet());
    	 
               if (repositorios.isEmpty()) {
            	   System.out.println(); 
            	   System.out.println("No existen asociaciones Repositorios-Clientes.");
            	   System.out.println();
               } else if (clientes.isEmpty()) {
            	   System.out.println(); 
            	   System.out.println("No existen asociaciones Repositorios-Clientes.");
            	   System.out.println();
               } else {
                    for (int n : repositorios) {
                         System.out.println();
                         System.out.println();
                    	 System.out.println("Repositorio: " + sesionRepositorio.get(n));
                         System.out.print("Clientes asociados:");
                         for (int c : clientes) {
                              if (clienteRepositorio.get(c) == n) {
                                   System.out.print(" "+sesionCliente.get(c));
                         }
                    }
               System.out.println();
               System.out.println();
               }
          }
     }
     
     /*M�todo que lista los clientes asociados a un repositorio*/
     public ArrayList<String> recogeListaClientesRepo(int idSesionRepositorio) throws RemoteException{
    	 ArrayList<Integer> clientes = new ArrayList<Integer>(sesionCliente.keySet());
    	 ArrayList<String> listaClientesRepo=new ArrayList<String>(); 	 
             for (int c : clientes) {
                  if (clienteRepositorio.get(c) == idSesionRepositorio) {
                	  
                	  listaClientesRepo.add(sesionCliente.get(c));
                  }
             }
     return listaClientesRepo;
     }
     
     //m�todo para comprobar si el cliente pertenece al repositorio cuyo contenido de carpeta vamos a mostrar
     public boolean comprobarRepositorioCliente(int idSesionRepositorio, String nombreCliente) throws RemoteException{
    	 ArrayList<String> clientesDeRepositorio=new ArrayList<String>();
    	 clientesDeRepositorio=recogeListaClientesRepo(idSesionRepositorio);
    	 
    	 if (clientesDeRepositorio.contains(nombreCliente)){
    	 	return true;
    	 }
    	 else{
    		 return false;
    	}
     }
    	 
    //M�todo para meter los ficheros del cliente en un array	 
     public ArrayList<String> extraeListaFiheros(String nombreCliente) throws RemoteException{
    	 
    	 ArrayList<String> contenidoCarpeta=new ArrayList<String>(); 
    	File carpeta = new File(nombreCliente);
     	File[] ficheros = carpeta.listFiles();
     	for (int x=0;x<ficheros.length;x++){
     		contenidoCarpeta.add(ficheros[x].getName());
     	}
    	 return contenidoCarpeta;
     }
}


