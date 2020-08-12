/* AUTOR: Manuel Rodríguez Sánchez
 * CORREO ELECTRÓNICO: mrodrigue212@alumno.uned.es*/
package servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;
import common.Herramienta;

public class Servidor {

	private static int nRegistro=2000;
	
	public static void main(String[] args) throws Exception{
		
		BufferedReader lecturaConsola=new BufferedReader(new InputStreamReader(System.in));
		arrancarRegistro(nRegistro); //Arranca el registro con rmiregistry
		
		//se levanta el servicio de datos
		ServicioDatosImpl datos=new ServicioDatosImpl();//Crea el objeto remoto.
		Naming.rebind("rmi://localhost:"+nRegistro+"/datos", datos);//Crea el registro con la URL y el objeto remoto creado.
		
		//método para ayudarme a rellenar clientes y repositorios en los registros map. 
		//Eliminarlo cuando vaya a entregar la práctica.
		if(!datos.rellenarClientesRepositorios())
			System.out.println("ERROR AL RELLENAR LOS REGISTROS HASHMAP");
		
		//se levanta el servicio de autenticación
		ServicioAutenticacionImpl autenticacion = new ServicioAutenticacionImpl();
		Naming.rebind("rmi://localhost:"+nRegistro+"/autenticacion", autenticacion);
		
		//se levanta el servicio de gestión
		ServicioGestorImpl gestor = new ServicioGestorImpl();
        Naming.rebind("rmi://localhost:" + nRegistro + "/gestor", gestor);
        
		System.out.println("Servicios levantados.");
		System.out.println();
		
		//Pintamos el menú
		String recogeOpcion;
		int opcion=0;
		do{
			System.out.println("---------------------------");
			System.out.println("MENÚ SERVIDOR");
			System.out.println("---------------------------");
			System.out.println("1-Listar clientes");
			System.out.println("2-Listar repositorios");
			System.out.println("3-Listar parejas Repositorio-Cliente");
			System.out.println("4-Salir");
		    System.out.print("Selecciona opción: ");
		    
		    recogeOpcion=lecturaConsola.readLine();
		    
		    if(!Herramienta.esNumero(recogeOpcion)){//Nos aseguramos que se introducen números
		    	System.out.println();
		    	System.out.println("Introduce un valor de 1 a 4");
		    	System.out.println();
		    }else{
		    	opcion=Integer.parseInt(recogeOpcion);//Convertimos la cadena a un entero
				switch(opcion){
				case 1:
					System.out.println();
					System.out.println("Mostrando clientes registrados: ");
					Collection<String> clientes = datos.listaClientes();
					
	                if (!clientes.isEmpty()) {
	                     System.out.print("[");
	                     for (String nombre : clientes) {
	                          System.out.print(" "+nombre+" ");
	                     }
	                     System.out.println("]");
	                     System.out.println();
	                } else {
	                     System.out.println("==================================");
	                     System.out.println("No hay clientes registrados.");
	                     System.out.println("==================================");
	                }
					break;
				case 2:
					System.out.println();
					System.out.println("Mostrando los repositorios registrados: ");
					Collection<String> repositorios = datos.listaRepositorios();
					
	                if (!repositorios.isEmpty()) {
	                	System.out.print("["); 
	                	
	                     for (String nombre : repositorios) {
	                          System.out.print(" "+nombre+" ");
	                     }
	                     System.out.println("]");
	                     System.out.println();
	                     
	                } else {
	                     System.out.println("==================================");
	                     System.out.println("No hay repositorios registrados.");
	                     System.out.println("==================================");
	                }
					break;
					
				case 3://muestro las parejas Repositorios-clientes
					datos.listarRepositoriosClientes();
					break;
				case 4:
					//Finalizamos los servicios y salimos
					Naming.unbind("rmi://localhost:" + nRegistro + "/autenticacion");
					Naming.unbind("rmi://localhost:" + nRegistro + "/datos");
					Naming.unbind("rmi://localhost:" + nRegistro + "/gestor");
					System.out.println("Servicios finalizados. Saliendo del sistema.");
					System.out.println();
					System.exit(4);
					break;
				default: 
					System.out.println("Opción no correcta");
					break;
				}
		    }
			} while (opcion!=4);
	}// FIN DEL MAIN
	
	//método para arrancar el registro, si no existe, lo crea
	private static void arrancarRegistro(int numPuertoRMI) throws RemoteException
	{
		try{
			Registry registro=LocateRegistry.getRegistry(numPuertoRMI);
			registro.list();
		}
		catch(RemoteException e)
		{
			System.out.println("El registro no se puede localizar en el puerto: "+numPuertoRMI);
			System.out.println();
			@SuppressWarnings("unused")
			Registry registro=LocateRegistry.createRegistry(numPuertoRMI);
			System.out.println("Registro RMI creado en el puerto: "+numPuertoRMI);
			System.out.println();
		}//Fin catch
	}//fin arrancarRegistro
	
}// FIN DE LA CLASE
