/* AUTOR: Manuel Rodríguez Sánchez
 * CORREO ELECTRÓNICO: mrodrigue212@alumno.uned.es*/
package repositorio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.*;
import java.util.ArrayList;
import servidor.ServicioAutenticacionInterface;
import cliente.ServicioDiscoClienteInterface;
import common.Herramienta;
 
public class Repositorio {
 
    private static String nombre;
    public static ArrayList<String> lista;
    private static ServicioAutenticacionInterface autenticacion;
    private static ServicioDiscoClienteInterface SrDiscoCliente;
    
    private static BufferedReader lecturaConsola = new BufferedReader(new InputStreamReader(System.in));
    
    public static void main(String[] args) throws Exception {
    	
    	
    	autenticacion = (ServicioAutenticacionInterface) Naming.lookup("rmi://localhost:2000/autenticacion");
    	
    	String recogeOpcion;
        
    	// PRIMER MENÚ
        int opcionMenu1 = 0;
		do{
			System.out.println("---------------------------");
			System.out.println("MENÚ GENERAL DE REPOSITORIO");
			System.out.println("---------------------------");
			System.out.println("1-Registrar nuevo repositorio");
			System.out.println("2-Autenticarse en el sistema");
			System.out.println("3-Salir");
			System.out.print("Selecciona opcion:");
		    recogeOpcion=lecturaConsola.readLine();
		    System.out.println();
		    if(!Herramienta.esNumero(recogeOpcion)){//Nos aseguramos que se introducen números
		    	System.out.println("Introduce un valor de 1 a 3");
		    	System.out.println();
		    }else{
		    	opcionMenu1=Integer.parseInt(recogeOpcion);//Convertimos la cadena a un entero
		    
				switch(opcionMenu1){
				case 1:
					System.out.println();
					System.out.print("Introduce nombre del repositorio:");
					nombre=lecturaConsola.readLine();
					System.out.println();
					//Llamada remota para el alta del repositorio
					if(autenticacion.registraRepositorio(nombre)){
						System.out.println("Se ha registrado el repositorio: "+nombre);
						System.out.println();
					}
					else{
						System.out.println("No se ha podido registrar el repositorio: "+nombre);
						System.out.println();
					}
					break;
					
				case 2://Autenticar repositorio
					
					System.out.println("Introduce el nombre del REPOSITORIO para proceder a la autenticación de éste:");
			        nombre = lecturaConsola.readLine();
			        System.out.println();
			        int idSesionRepositorio = autenticacion.autenticarRepositorio(nombre);
			       
			        
			        if (idSesionRepositorio==0){
			        	System.out.println("Fallo de autenticación de repositorio. Causas: nombre repositorio inexistente. Repositorio ya autenticado.");
			        	System.out.println();
			        }else{
			        	
			 
			        // LEVANTAMOS EL SERVICIO CLIENTE-OPERADOR(Cliente comunica bidireccionalmente con Repositorio)
			        ServicioClOperadorImpl clienteOperador = new ServicioClOperadorImpl();
			        String URL_nombreClienteOperador = "rmi://localhost:2000/ServicioClOperador/"+ idSesionRepositorio;
			        Naming.rebind(URL_nombreClienteOperador, clienteOperador);
			        System.out.println();
			        System.out.println("Servicio Cliente Operador levantado.");
			        
					
					// LEVANTAMOS EL SERVICIO SERVIDOR-OPERADOR (Servidor comunica unidireccionalmente con Repositorio)
			        ServicioSrOperadorImpl servidorOperador = new ServicioSrOperadorImpl();
			        String URL_nombreServidorOperador = "rmi://localhost:2000/ServicioSrOperador/"+ idSesionRepositorio;
			        Naming.rebind(URL_nombreServidorOperador, servidorOperador);
			        System.out.println();
			        System.out.println("Servicio Servidor Operador levantado.");
			      
					
					//INICIO DEL SEGUNDO MENU UNA VEZ AUTENTICADO EN EL SISTEMA
			        int opcionMenu2 = 0;
			        do {
			        	System.out.println("-------------------");
			        	System.out.println("MENÚ DE REPOSITORIO");
						System.out.println("-------------------");
			        	System.out.println("1.- Listar Clientes.");
			            System.out.println("2.- Listar Ficheros del Cliente.");
			            System.out.println("3.- Salir.");
			            System.out.print("Selecciona opción: ");
					    
					    recogeOpcion=lecturaConsola.readLine();
					    System.out.println();
					    
					    if(!Herramienta.esNumero(recogeOpcion)){//Nos aseguramos que se introducen números
					    	System.out.println("Introduce un valor de 1 a 3");
					    	System.out.println();
					    }else{
					    	opcionMenu2=Integer.parseInt(recogeOpcion);//Convertimos la cadena a un entero
				            switch (opcionMenu2) {
				            case 1:// LISTAR LOS CLIENTES DEL REPOSITORIO AUTENTICADO
				            	lista=new ArrayList<String>();
				            	lista=autenticacion.listaClientesDeRepositorio(idSesionRepositorio);
				            	System.out.println("Clientes de este repositorio:");
				            	for(int x=0;x<lista.size();x++) {
				            		  System.out.println(lista.get(x));
				            		}
				                break;
				            case 2:// LISTAR FICHEROS DEL CLIENTE
				            	System.out.println("Introduce el nombre del cliente: ");
				            	nombre=lecturaConsola.readLine();
				            	try{
				            		SrDiscoCliente=(ServicioDiscoClienteInterface) Naming.lookup("rmi://localhost:2000/DiscoCliente/"+idSesionRepositorio);
				            		if(!SrDiscoCliente.listarFicherosCliente(idSesionRepositorio, nombre.toUpperCase()))
				            		System.out.println("No existe el cliente en este repositorio");
				            	}catch (NotBoundException nbe){
				            		System.out.println("No hay ningún cliente autenticado");
				            	}
				                break;
				            case 3:
				            	autenticacion.desAutenticarRepositorio(idSesionRepositorio);//Elimina la sesión de repositorio autenticado
				                break;
				            default:
				            	System.out.println("Opción no correcta");
								System.out.println();
				                break;
				            }
					    }
			        } while (opcionMenu2 != 3);
			        //FIN DEL SEGUNDO MENU
			        
			        }//fin del else de control de id sesion
			        
				case 3:
					System.out.println("Saliendo...");
					if (opcionMenu1==3)
						System.exit(3);
					break;
				default: 
					System.out.println("Opción no correcta");
					System.out.println();
				break;
				}
		    }
		} while (opcionMenu1!=3);
		//FIN DE PRIMER MENU
        
    }//FIN DEL MAIN
} //FIN DE LA CLASE

