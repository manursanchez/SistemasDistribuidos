/* AUTOR: Manuel Rodríguez Sánchez
 * CORREO ELECTRÓNICO: mrodrigue212@alumno.uned.es*/
package cliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.util.ArrayList;


import servidor.ServicioAutenticacionInterface;
import servidor.ServicioGestorInterface;
import repositorio.ServicioClOperadorInterface;
import common.Fichero;
import common.Herramienta;

public class Cliente {
    private static String nombre;//nombre del cliente
    private static String archivo;
    private static String rutaDirectorio;
    private static String URL_DiscoCliente;
    private static ServicioAutenticacionInterface ServicioAutenticacion;
    private static ServicioGestorInterface servicioGestor;
    private static ServicioClOperadorInterface accesoMetodosClOp;
    
    private static BufferedReader lecturaConsola = new BufferedReader(new InputStreamReader(System.in));
    
    public static void main(String[] args) throws Exception {
    	 	
    	ServicioAutenticacion = (ServicioAutenticacionInterface) Naming.lookup("rmi://localhost:2000/autenticacion");
    	servicioGestor=(ServicioGestorInterface) Naming.lookup("rmi://localhost:2000/gestor");
        String recogeOpcion;
    	// PRIMER MENÚ
        int opcionMenu1=0;
		do{
			System.out.println("------------------------");
			System.out.println("MENÚ GENERAL DE CLIENTES");
			System.out.println("------------------------");
			System.out.println("1-Registrar nuevo cliente");
			System.out.println("2-Autenticarse en el sistema");
			System.out.println("3-Salir");
			System.out.print("Selecciona opción: ");
			recogeOpcion=lecturaConsola.readLine();
		    
		    if(!Herramienta.esNumero(recogeOpcion)){//Nos aseguramos que se introducen números
		    	System.out.println("Introduce un valor de 1 a 3");
		    	System.out.println();
		    }else{
		    	opcionMenu1=Integer.parseInt(recogeOpcion);//Convertimos la cadena a un entero
			
				switch(opcionMenu1){
				case 1: //Dar de alta o registrar un nuevo cliente
					System.out.println("Introduce nombre del cliente:");
					nombre=lecturaConsola.readLine(); //Cojo el nombre del cliente
					
					//Llamada remota para el alta del cliente
					if(ServicioAutenticacion.registraCliente(nombre)){//Aquí llamamos al servicioAutenticacionImpl
						System.out.println("Se ha registrado el cliente: "+nombre);
						System.out.println();
					}
					else{
						System.out.println("No se ha podido registrar el cliente, puede que ya exista: "+nombre+".");
						System.out.println();
					}
					break;
					
				case 2://Iniciar o autenticar la sesión de un cliente
					System.out.println("Introduce tu nombre para proceder a la autenticación:");
			        nombre = lecturaConsola.readLine();
			        
			        int idSesionCliente = ServicioAutenticacion.autenticarCliente(nombre);
			        
			        if (idSesionCliente==0){
			        	System.out.println("La operación de autenticación no ha sido posible. Causas: Repositorios no disponibles o cliente inexistente.");
			        }else{
			     		   	
						//Iniciamos el servicio Disco-Cliente
						
						// 1º Buscamos el número de repositorio correspondiente al cliente mediante el servicio Autenticacion
						int idRepositorioCliente=ServicioAutenticacion.dameRepositorio(idSesionCliente);
						System.out.println("Repositorio del cliente: "+ idRepositorioCliente);
						
						// 2º Declaro y creo el objeto remoto
						ServicioDiscoClienteImpl discoCliente= new ServicioDiscoClienteImpl();
						
						// 3º Iniciamos el servicio en el repositorio
						URL_DiscoCliente = "rmi://localhost:2000/DiscoCliente/"+idRepositorioCliente;
	           	 		Naming.rebind(URL_DiscoCliente , discoCliente);
	           	 		System.out.println("Levantado el servicio Disco-Cliente.");	
	           	 		
	           	 		//Obtenemos dirección del servicio gestor, que es el que nos permitirá acceder al servicio Cliente-Operador y sus métodos
	           	 		//remotos de subida y borrado de ficheros.
	           	 		String direccionServicioClOp=servicioGestor.obtenerServicioClienteOperador(idSesionCliente);
	           	 		
	           	 		//Haccemos un lookup de la clase remota que contiene los métodos para gestión de archivos
	           	 		accesoMetodosClOp=(ServicioClOperadorInterface) Naming.lookup(direccionServicioClOp);
						
					//INICIO DEL SEGUNDO MENÚ UNA VEZ AUTENTICADO EN EL SISTEMA
			        int opcionMenu2 = 0;
			        do {
						System.out.println("---------------");
			        	System.out.println("MENÚ DE CLIENTE");
						System.out.println("---------------");
			        	System.out.println("1.- Subir fichero.");//Servicio Cliente-Operador
			            System.out.println("2.- Bajar fichero.");//Servicio Servidor-Operador-->Disco-Cliente
			            System.out.println("3.- Borrar fichero.");//Servicio Cliente-Operador
			            System.out.println("4.- Compartir fichero(Opcional).");
			            System.out.println("5.- Listar ficheros.");
			            System.out.println("6.- Listar clientes del sistema.");
			            System.out.println("7.- Salir.");
			            System.out.print("Selecciona opción: ");
			            
			            recogeOpcion=lecturaConsola.readLine();
					    
					    if(!Herramienta.esNumero(recogeOpcion)){//Nos aseguramos que se introducen números
					    	System.out.println();
					    	System.out.println("Introduce un valor de 1 a 3");
					    	System.out.println();
					    }else{
					    	opcionMenu2=Integer.parseInt(recogeOpcion);//Convertimos la cadena a un entero
			   
				            switch (opcionMenu2) {
				            
				            case 1: //SUBIR FICHERO
				            	try{
				            	System.out.println("Introduce la ruta donde se encuentra el archivo:");
				            	rutaDirectorio=lecturaConsola.readLine();
				            	System.out.println("Introduce el fichero a subir: ");
				            	archivo=lecturaConsola.readLine();
				            	
								if (rutaDirectorio.isEmpty()){ //Si está vacía rutaDirectorio mandamos a un constructor
									Fichero fichero = new Fichero(archivo,nombre);
									//enviamos el fichero/archivo
									if (!accesoMetodosClOp.subirFichero(fichero))
										System.out.println("No existe el fichero.");
										System.out.println();
									}
								else{
									Fichero fichero= new Fichero(rutaDirectorio,archivo, nombre); //si no mandamos al otro constructor
									//enviamos el fichero/archivo
									if(!accesoMetodosClOp.subirFichero(fichero)) {
										System.out.println("No existe el fichero.");
										System.out.println();
									}
									else 
									{
										System.out.println("Fichero subido correctamente.");
										System.out.println();
									}
								}	
				            	}catch(NullPointerException npe){
				            		System.out.println("Error de creación de objeto. No existe el fichero.");
				            		System.out.println();
				            	}
				                break;
				                
				            case 2:// BAJAR FICHERO
				            		            	
				            	System.out.println("Introduce el nombre del fichero a descargar: ");
				            	archivo=lecturaConsola.readLine();
				            	
				            	/*Paso 1 - El cliente quiere bajar un fichero y se lo pide al servidor mediante el 
				            	servicio Gestor*/
				            	//Llamo a servicio Gestor del servidor
				            	try{//Controlamos que el fichero existe
				            		servicioGestor.bajarFicheroPaso1(idSesionCliente,archivo,nombre);
				            	}catch(NullPointerException npe){
				            		System.out.println("No existe el fichero.");
				            	}
				            	
				                break;
				            case 3:// BORRAR FICHERO
				            	
				            	System.out.println("Introduce el fichero a borrar: ");
				            	archivo=lecturaConsola.readLine();
				            	
								System.out.println("El fichero es: "+ archivo +", y el cliente es:"+nombre);
								if(accesoMetodosClOp.borrarFichero(archivo,nombre))
									System.out.println("Fichero borrado correctamente");
								else
									System.out.println();
									System.out.println("Fichero NO borrado. No encontrado o no existe.");
	
				                break;
				            case 4://COMPARTIR FICHERO
				            	System.out.println("Opción no implementada.");
				            	break;
				            	
				            case 5://LISTAR FICHEROS DE CLIENTE
				            	System.out.println("Listando ficheros de la carpeta del cliente "+nombre+":");
				            	ArrayList<String> archivosExtraidos=new ArrayList<String>();
				            	
				            	//Buscamos los ficheros del cliente
				            	archivosExtraidos=accesoMetodosClOp.listarFicherosCliente(nombre);
				            	
				            	//Mostramos el listado de archivos.
				            	for(int contador=0;contador<archivosExtraidos.size();contador++) {
				            			System.out.println(archivosExtraidos.get(contador));
				            	}
				            	break;
				            	
				            case 6://LISTAR CLIENTES DE REPOSITORIO
				            	ArrayList<String> listadoClientes=new ArrayList<String>();
				            	listadoClientes=servicioGestor.listarClientesSistema();
				            	if (!listadoClientes.isEmpty()) {
				                     System.out.println();
				                     System.out.println("Clientes del repositorio:");
				                     for (String nombre : listadoClientes) {
				                          System.out.println(nombre);
				                     }
				                     System.out.println();
				                } else {
				                     System.out.println("==================================");
				                     System.out.println("No hay clientes registrados.");
				                     System.out.println("==================================");
				                }
				            	break;
				            case 7:
				            	//llamar al servicio autenticación para que de orden al servicio datos de borrar el cliente autenticado
				            	ServicioAutenticacion.desAutenticarCliente(idSesionCliente);//Eliminar sesión del usuario autenticado		          
				            
				            	break;
				            default: 
				            	System.out.println("Opción no correcta");
				            	System.out.println();
				            }
					    }
			        } while (opcionMenu2 != 7);
			        //FIN DEL SEGUNDO MENU
			        
			        }//fin del else de control de id sesión
			        
				case 3:
					System.out.println("Saliendo");
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