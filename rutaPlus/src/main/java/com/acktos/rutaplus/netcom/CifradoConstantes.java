/**
 * 
 */
package com.acktos.rutaplus.netcom;

/**
 * Constantes usadas en la clase de encripcion de AES.
 * 
 * @author Javier Benavides
 *
 */
public class CifradoConstantes {

	/** Define el algoritmo de encripcion/desencripcion 
	 * a usar */
	public static final String ALGORITMO = "AES";
	
	/** Define la codificacion o charset a usar */
	public static final String CHARSET = "UTF-8";
	
	/** Datos usados para obtener la instancia del objeto
	 * que encripta y desencripta */
	public static final String CIPHER_TRANSFORMACION = "AES/CBC/PKCS5Padding";
	public static final String CIPHER_PROVEEDOR = "SunJCE";
	
	
	
	
	/********************************************************************
	 *  Datos entregado por Netcom.
	 *******************************************************************/
	
	/** Vector de inicializacion usado para la encripcion  y desencripcion estandar B[6o@IuCoVD3vtK6 */
	public static final byte[] VECTOR_INICIALIZACION_ESTANDAR = {0x42, 0x5b, 0x36, 0x6f, 0x40, 0x49, 0x75, 0x43, 0x6f, 0x56, 0x44, 0x33, 0x76, 0x74, 0x4b, 0x36};
	
	public static final String LLAVE = "vZy%}U;*K0mFA+5x";
	
	public static final String TOKEN = "PU}7=xTb-WX+{ZEy";
	
	public static final String RUTA_PLUS_CODE="1234567890";
	
	public static final String AUTH_FILE="archivoAutenticacion.key";
	
	/**
	 * Keys for userdata
	 */
	
	public static final String KEY_ID_NUMBER="numeroDocumento";
	public static final String KEY_ID_TYPE="tipoDocumento";
	public static final String KEY_FIRST_NAME="primerNombre";
	public static final String KEY_LAST_NAME="primerApellido";
	public static final String KEY_EMAIL="correo";
	public static final String KEY_PSWRD="clave";
	
	/**
	 * keys for transaction
	 */
	public static final String KEY_COMMERCE_COD="codigoComercio";
	public static final String KEY_HASH="hashAutenticacion";
	public static final String KEY_DATA_ENCRYPT="datosEncriptados";
	
	
	public static final String VALUE_ID_TYPE_DEFAULT="1";
	
	/**
	 * services
	 */
	
	public static final String URL_PREREGISTER="http://181.54.254.8:8080/payok/PayOkApi.do/preRegistro";
}
