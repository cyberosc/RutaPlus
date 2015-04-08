package com.acktos.rutaplus.netcom;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;








import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.acktos.rutaplus.R;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import co.com.netcom.verificadorintegridadarchivo.impl.ComprobarIntegridadArchivoImpl;

import com.acktos.rutaplus.android.HttpRequest;
import com.acktos.rutaplus.netcom.CifradoConstantes;

/**
 * Clase que contiene la logica para encriptar y desencriptar
 * datos usando AES estandar sin usar iteraciones.
 * 
 * @author Javier Benavides
 *
 */
public class Payment {

	/** Cadena de texto a partir de la cual se genera
	 * la llave para encriptar o desencriptar */
	private String llave;

	/** Objeto encargado de encriptar y desencriptar 
	 * los datos. */
	private Cipher cipher;

	private Context context;

	/**
	 * Constructor con parametros.
	 * 
	 * @param llave
	 */
	public Payment(Context context,String llave) throws Exception{
		try {
			this.context=context;
			this.llave = llave;
		} catch (Exception e) {
			throw new Exception("No se pudo decifrar la llave. :" + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * co.com.netcom.seguridad.encripcion.IEncriptarAES#encriptar(java.lang.
	 * String)
	 */
	public String encrypt(String datoEncriptar, String token) throws Exception {
		String valueToEnc = null;
		String eValue = null;
		byte[] decodedString = null;
		Key key = generateKey();

		cipher = Cipher.getInstance(
				CifradoConstantes.CIPHER_TRANSFORMACION
				);
		cipher.init(
				Cipher.ENCRYPT_MODE, 
				key, 
				new IvParameterSpec(CifradoConstantes.VECTOR_INICIALIZACION_ESTANDAR)
				);

		decodedString = Base64.encodeBase64(datoEncriptar.getBytes(CifradoConstantes.CHARSET));
		eValue = new String(decodedString, CifradoConstantes.CHARSET);
		valueToEnc = token + eValue;
		byte[] encValue = cipher.doFinal(valueToEnc.getBytes(CifradoConstantes.CHARSET));
		eValue = new String(Base64.encodeBase64(encValue),CifradoConstantes.CHARSET);
		return eValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * co.com.netcom.seguridad.encripcion.IEncriptarAES#desencriptar(java.lang
	 * .String)
	 */
	public String decrypt(String datoDesencriptar, String token) throws Exception {
		String dValue = null;
		String valueToDecrypt = null;
		String tokenDato = null;
		Key key = null;

		if(token ==  null || token.length() == 0){
			throw new Exception("El token se encuentra nulo (null).");
		}

		key = generateKey();

		cipher = Cipher.getInstance(
				CifradoConstantes.CIPHER_TRANSFORMACION
				);
		cipher.init(
				Cipher.DECRYPT_MODE, 
				key, 
				new IvParameterSpec(CifradoConstantes.VECTOR_INICIALIZACION_ESTANDAR)
				);

		valueToDecrypt = datoDesencriptar;
		byte[] decordedValue = Base64.decodeBase64(valueToDecrypt.getBytes(CifradoConstantes.CHARSET));
		byte[] decValue = cipher.doFinal(decordedValue);
		tokenDato = new String(decValue, CifradoConstantes.CHARSET).substring(0, token.length());
		if(!token.equals(tokenDato)){
			throw new Exception("Token para desencriptar invalido.");
		}
		dValue = new String(decValue, CifradoConstantes.CHARSET).substring(token.length());
		valueToDecrypt = dValue;
		return new String(Base64.decodeBase64(dValue.getBytes(CifradoConstantes.CHARSET)), CifradoConstantes.CHARSET);
	}

	/**
	 * Metodo encargado de generar la llave para la
	 * encripcion a partir de la llave asociada
	 * al comercio.
	 * 
	 * @return
	 * @throws Exception
	 */
	private Key generateKey() throws Exception {
		Key key = new SecretKeySpec(llave.getBytes(CifradoConstantes.CHARSET), CifradoConstantes.ALGORITMO); 
		return key; 
	} 

	public String encodingSHA1(String cadenaDeEntrada) {

		String salida = null;

		//System.out.println("Caso de Prueba - testCasoSHA1DeArchivo");

		try {
			ComprobarIntegridadArchivoImpl comprobar = new ComprobarIntegridadArchivoImpl();
			salida = comprobar.calularSHA1(cadenaDeEntrada);
			//System.out.println("SHA-1 (String): " + salida + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}	

		return salida;

	}

	public String encodingResourceSHA1(String fileName){
		
		
		String outputSHA1=null;
		AssetManager assetMan = this.context.getAssets();
	    InputStream is;
		try {
			is = assetMan.open(fileName);
		

	    System.out.println("is.available(): " + is.available());
	    
	    StringBuilder fileData = new StringBuilder();
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    char[] buf = new char[1024];
	    
	          int numRead = 0;
	          while ((numRead = br.read(buf)) != -1) {
	              
	              String bufferString = String.valueOf(buf, 0, numRead);
	              fileData.append(bufferString);
	              buf = new char[1024];
	          }
	          
	          br.close();
	          System.out.println("data: " + fileData.toString());
	    
	   
	    outputSHA1 =encodingSHA1(fileData.toString());
	    System.out.println("SHA-1 (String): " + outputSHA1 + "\n");
	    
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputSHA1;
	}

	public JSONObject preRegister(JSONObject data){

		if(data!=null){

			String encodeData;
			try {
				encodeData = encrypt(data.toString(), CifradoConstantes.TOKEN);
				Log.i("encode data:",encodeData);
				
				String decodeData=decrypt(encodeData,CifradoConstantes.TOKEN);
				Log.i("decode data:",decodeData);
				
				String encodingSHA1File=encodingResourceSHA1(CifradoConstantes.AUTH_FILE);
				//encodingSHA1File="13adb46d0782a008386bcd165b03b147d4fb4fa5";
				
				/*String fileContent="token=PU}7=xTb-WX+{ZEy" + "\n" +
						"vector=B[6o@IuCoVD3vtK6" + "\n" +
						"usuario=iCc1HIyXXV8vvPSpxkCAaCmEEBPHjKFIsyCDbeAyywv1FtDTd9RGUgYnijHH9Ap7PWad73ViKDraWSSUMkDYWm7uLgzdcbu8RHzGPHc9ddNEzoY2qDsXzIcEyH0E+Z1dJzo1VCFYPi3VlxJzTUEd1S1zatoIZALyQNiRFJgGrouAHywLTp9qqeU5CVXx8w1+7aUQgz2GU8YJ7UrNQXzsdw==" + "\n" +
						"clave=iCc1HIyXXV8vvPSpxkCAaCmEEBPHjKFIsyCDbeAyywv1FtDTd9RGUgYnijHH9Ap7PWad73ViKDraWSSUMkDYWm7uLgzdcbu8RHzGPHc9ddMZqYpeuL9eEroMcs3Iu38JpVdUyT/RzNClqXgF0HUtOx8Zw/FK2DdxVzE/VknxkIMIT5MI1+grI3e1bB8mrXe8FWvgZm5fVcYqAawQc5Q1lw==";*/
				
				//fileContent="hola mundo";
				//String decodeString=encodingSHA1(fileContent);
				
				Log.i("encodeString",encodingSHA1File);
				
				ArrayList<NameValuePair> postParams=new ArrayList<NameValuePair>();
				postParams.add(new BasicNameValuePair(CifradoConstantes.KEY_COMMERCE_COD,CifradoConstantes.RUTA_PLUS_CODE));
				postParams.add(new BasicNameValuePair(CifradoConstantes.KEY_HASH,encodingSHA1File));
				postParams.add(new BasicNameValuePair(CifradoConstantes.KEY_DATA_ENCRYPT,encodeData));
				
				String response=callApi(CifradoConstantes.URL_PREREGISTER,postParams);
				Log.i("respose payOK","preregister:"+decrypt(response, CifradoConstantes.TOKEN));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}


		return null;
	}

	public String callApi(String serviceUrl,ArrayList<NameValuePair> postParams){

		String response=null;
		response=HttpRequest.httpPostRequest(serviceUrl, postParams);
		return response;
	}
}
