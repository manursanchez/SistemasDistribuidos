/* AUTOR: Manuel Rodr�guez S�nchez
 * CORREO ELECTR�NICO: mrodrigue212@alumno.uned.es*/
package common;

import java.lang.StringBuffer;
public class Herramienta {
	
	//M�todo para comprobar si lo introducido por teclado es un n�mero entero.
	public static boolean esNumero(String cadena){
		try{
			Integer.parseInt(cadena);
			return true;
		}catch (NumberFormatException nfe){
			return false;
	}
	}
	// M�todo para comprobar si una direccion de directorio termina en .jar
	public static boolean comprobarSufijoCadena(String []array) {
		StringBuffer cadena = new StringBuffer();
		
		//Convierto el array en una cadena
		for (int x=0;x<array.length;x++){
			   cadena.append(array[x]);
			}
		//compruebo que la cadena resultante tiene un .jar al final
		if (cadena.toString().endsWith(".jar")) {
			return true;
		}
		else {
			return false;
		}
	}
}
