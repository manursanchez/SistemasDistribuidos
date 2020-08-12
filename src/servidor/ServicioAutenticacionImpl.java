/* AUTOR: Manuel Rodríguez Sánchez
 * CORREO ELECTRÓNICO: mrodrigue212@alumno.uned.es*/
package servidor;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;
 
import servidor.ServicioAutenticacionInterface;
import servidor.ServicioDatosInterface;
import servidor.ServicioGestorInterface;
import repositorio.ServicioSrOperadorInterface;

//AQUÍ VAN ESTAR LOS MÉTODOS DE AUTENTICACIÓN QUE SERÁN REMOTOS Y QUE LA CLASE SERVIDOR LLAMARÁ.
 
public class ServicioAutenticacionImpl extends UnicastRemoteObject implements ServicioAutenticacionInterface {
 
    private static final long serialVersionUID = 1007197529122012L;
    
    // Número aleatorio que nos va a servir para obtener los números       
    // de sesión de los Clientes y Repositorios
    private static int sesion = new Random().nextInt();
    
    // Objetos para el acceso remoto al servicio Datos, Gestor y Servidor-Operador.
    private static ServicioDatosInterface datos;
    private static ServicioGestorInterface servicioGestor;
    private static ServicioSrOperadorInterface accesoMetodosSrOp;
    
    protected ServicioAutenticacionImpl() throws RemoteException {
        super();
        // TODO Auto-generated constructor stub
    }
 
    protected ServicioAutenticacionImpl(String nombreLog) throws RemoteException {
        super();
    }
 
    // Función que autentica un cliente y devuelve el número de sesión que le ha
    // sido asignado o 0 en caso de que no se haya podido llevar a cabo la
    // autenticación.
    //@Override
    public int autenticarCliente(String nombre) throws RemoteException, MalformedURLException, NotBoundException {
 
        int idSesionCliente = getSesion();
        try {
            datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
 
        if (datos.guardarAutenticacionCliente(idSesionCliente, nombre)) {
        		
        	//Si el cliente se ha conseguido loguear, debemos crear su carpeta en el repositorio y abrir el menú secundario
        	//Debemos obtener la dirección en el registro del Servicio Servidor-Operador. Le mandamos la sesión del cliente, para
        	//que nos proporcione la URL con el repositorio donde crear la carpeta.
        	
        		//Responsabilizamos al servicio Gestor de que cree la carpeta
        		
        		servicioGestor=(ServicioGestorInterface) Naming.lookup("rmi://localhost:2000/gestor");
        		String direccionServicioSrOp=servicioGestor.obtenerServicioServidorOperador(idSesionCliente);
			
        		//Hacemos un lookup de la clase remota que contiene los métodos 
        		accesoMetodosSrOp=(ServicioSrOperadorInterface) Naming.lookup(direccionServicioSrOp);
			
        		//Accedemos al método para crear carpeta
        		accesoMetodosSrOp.crearCarpeta(nombre);
            return idSesionCliente;
        } else {
            return 0;
        }
    }
    
  //DESAUTENTICAR EL CLIENTE
  //@Override
    public void desAutenticarCliente(int idSesionCliente) throws RemoteException, MalformedURLException, NotBoundException {
 
        try {
            datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        datos.borrarAutenticacionCliente(idSesionCliente);//Eliminar sesión del usuario autenticado
    }// FIN DESAUTENTICAR CLIENTE
 
    // Función que autentica un repositorio y devuelve el número de sesión que
    // le ha sido asignado o 0 en caso de que no se haya podido llevar a cabo la
    // autenticación.
    public int autenticarRepositorio(String nombre) throws RemoteException {
 
        //System.out.println(nombre + " esta intentando autenticarse.");
        int idSesionRepositorio = getSesion();
        //System.out.println("Número de sesion de Repositorio = "+idSesionRepositorio);
        
        try {
        	datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
               
        if (datos.guardarAutenticacionRepositorio(idSesionRepositorio,nombre)) {// Mando los datos para grabarlos a traves del servicio datos.
            return idSesionRepositorio;
        } else {
            return 0;
        }
    }
    
 //Método para calcular y devolver un número de sesión
    public static int getSesion() {
        if (sesion<0){
        	return (sesion=-1*sesion);//para que siempre sea positivo el número de sesión
        }else{
        	return ++sesion;
        }
    }
    
    public int dameRepositorio(int idSesionCliente) throws RemoteException
    {
    	return datos.buscaRepositorio(idSesionCliente);
    }
    
    public ArrayList<String> listaClientesDeRepositorio(int idSesionRepositorio) throws RemoteException{
    	ArrayList<String> listaClientesRepo=new ArrayList<String>();
    	listaClientesRepo=datos.recogeListaClientesRepo(idSesionRepositorio);    	
    return listaClientesRepo;
    }
    public boolean registraCliente(String nombre) throws RemoteException,MalformedURLException, NotBoundException
    {
    	
    	datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
    	if(datos.altaCliente(nombre))
    		return true;
    	else
    		return false;
    }
    public boolean registraRepositorio(String nombreRepositorio) throws RemoteException,MalformedURLException, NotBoundException
    {
    	datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
    	if (datos.altaRepositorio(nombreRepositorio))
    		return true;
    	else
    		return false;
    }
    public void desAutenticarRepositorio(int numeroSesionRepositorio) throws RemoteException,MalformedURLException, NotBoundException
    {
    	datos.borrarAutenticacionRepositorio(numeroSesionRepositorio);
    }
}
