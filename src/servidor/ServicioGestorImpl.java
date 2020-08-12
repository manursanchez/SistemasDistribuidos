/* AUTOR: Manuel Rodríguez Sánchez
 * CORREO ELECTRÓNICO: mrodrigue212@alumno.uned.es*/
package servidor;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import servidor.ServicioDatosInterface;
import repositorio.ServicioSrOperadorInterface;
 
public class ServicioGestorImpl extends UnicastRemoteObject implements ServicioGestorInterface {
 
     private static final long serialVersionUID = 1L;
 
     //private static String nombreLog;
     //Haremos uso del servicio de DATOS del Servidor.
     private static ServicioDatosInterface datos;
     private static ServicioSrOperadorInterface servicioSrOp;
     
     protected ServicioGestorImpl() throws RemoteException {
          super();
          // TODO Auto-generated constructor stub
     }
 
     protected ServicioGestorImpl(String nombreLog) throws RemoteException{
          super();
     }
 
     //Método que obtiene la dirección del servicio ClienteOperador
     @Override
     public String obtenerServicioClienteOperador(int id) throws RemoteException, NotBoundException,MalformedURLException {
 
          datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
          int repositorio = datos.buscaRepositorio(id);
          String URL_repositorio = "rmi://localhost:2000/ServicioClOperador/"+ repositorio;
          return URL_repositorio;
     }
 
     //Método que obtiene la dirección del servicio ServidorOperador
     @Override
     public String obtenerServicioServidorOperador(int idSesionCliente) throws RemoteException, NotBoundException,MalformedURLException {
 
          datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
          int repositorio = datos.buscaRepositorio(idSesionCliente);
          
          String direccionRepositorioCliente = "rmi://localhost:2000/ServicioSrOperador/"+ repositorio;
          return direccionRepositorioCliente;
     }
     
     @Override
     public void bajarFicheroPaso1(int SesionCliente,String archivo, String nombreCliente)throws RemoteException, NotBoundException,MalformedURLException{
    	 
    	 /*El servidor averigua que repositorio aloja el fichero requerido por el cliente llamando 
    	  al servicio Servidor-Operador*/
    	 String direccionRepositorio=obtenerServicioServidorOperador(SesionCliente); //lo voy a necesitar para acceso de métodos remotos del servicio SR-OP
    	 
    	 datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
         int repositorio = datos.buscaRepositorio(SesionCliente); //Necesito el repositorio para el servicio Disco Cliente
          
    	 //Pasamos la URL del cliente al Servicio Sr Operador para desde ahí poder llamar al método de descarga de Disco-Cliente
    	 
    	 servicioSrOp=(ServicioSrOperadorInterface) Naming.lookup(direccionRepositorio);
    	   	 
    	 servicioSrOp.bajarFicheroPaso2(repositorio, archivo, nombreCliente);
     }
     public ArrayList<String> listarClientesSistema() throws RemoteException,NotBoundException,MalformedURLException{
    	ArrayList<String> listaClientes=new ArrayList<String>();
    	listaClientes=datos.listaClientesAutenticados();
    	return listaClientes;
     }
    
     public boolean buscarFicherosClientes(int idSesionRepositorio,String nombreCliente) throws RemoteException, MalformedURLException, NotBoundException{
    	
    	 ArrayList<String> ListaFicheros=new ArrayList<String>();
    	 
    	 if (datos.comprobarRepositorioCliente(idSesionRepositorio, nombreCliente)){
    		 
    		 ListaFicheros=datos.extraeListaFiheros(nombreCliente);
    		 servicioSrOp=(ServicioSrOperadorInterface) Naming.lookup("rmi://localhost:2000/ServicioSrOperador/"+idSesionRepositorio);
    		 servicioSrOp.mostrarListaFicheros(ListaFicheros);
    		 return true;
    	 }
    	 else{
    		 return false;
    	 }
     }
}
