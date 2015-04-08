package com.acktos.rutaplus.controllers;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

import android.util.Log;
import co.com.payok.lib.encapsulacion.dto.ListadoMedioPago;
import co.com.payok.lib.encapsulacion.dto.PreRegistroDTO;
import co.com.payok.lib.encapsulacion.dto.RespuestaConsultaTarjetasDTO;
import co.com.payok.lib.encapsulacion.dto.RespuestaInicioSesionDTO;
import co.com.payok.lib.encapsulacion.dto.RespuestaPreRegistroDTO;
import co.com.payok.lib.encapsulacion.dto.UsuarioDTO;
import co.com.payok.lib.impl.PayOkClienteApi;

import com.acktos.rutaplus.entities.CreditCard;


public class PaymentController {

	private Context context;
	private String cc;
	private String user;
	private String pswrd;
	private UserController userController;
	private PayOkClienteApi payOkClienteApi;

	private static final String llaveEntregada = "vZy%}U;*K0mFA+5x";
	private static final String codigoComercio = "0014265367";
	private static final String url = "http://181.54.254.8:8080";

	public static final String CODE_USER_NOT_EXISTS = "20";
	public static final String CODE_ERROR = "99";
	public static final String CODE_SUCCESS = "00";

	public static final String RESULT_TRUE="RESULT_TRUE";
	public static final String RESULT_FALSE="RESULT_FALSE";
	public static final String RESULT_SERVER_ERROR="SERVER_ERROR";

	private static final int TYPE_CC=1;

	//ANDROID UTILS
	SharedPreferences mPrefs;// Handle to SharedPreferences for this APP
	SharedPreferences.Editor mEditor;// Handle to a SharedPreferences editor

	//SHARED PREFERENCES CONSTANTS
	public static final String SHARED_PREFERENCES ="com.acktos.rutaplus.SHARED_PREFERENCES";
	public static final String SHARED_CARD_REFERENCE ="com.acktos.rutaplus.CARD_REFERENCE";
	public static final String SHARED_CARD_TYPE ="com.acktos.rutaplus.CARD_TYPE";
	public static final String SHARED_CARD_NAME ="com.acktos.rutaplus.CARD_NAME";
	public static final String SHARED_CARD_TERMINATION ="com.acktos.rutaplus.CARD_TERMINATION";


	public PaymentController(Context context){

		this.context=context;
		userController=new UserController(this.context);
		user=userController.getUserEmail();
		cc=userController.getCC();
		pswrd=userController.getPswrd();

		mPrefs = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);// Open Shared Preferences
		mEditor = mPrefs.edit();// Get an editor

		//initialize PAYOK API
		payOkClienteApi = PayOkClienteApi.getInstance();
		try {
			payOkClienteApi.iniciarApi(url, this.context, llaveEntregada, codigoComercio);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String isRegistered(){

		String responseLogin=login();

		Log.i("isRegistered() responseLogin:",responseLogin);

		if(responseLogin.equals(CODE_USER_NOT_EXISTS)){
			return RESULT_FALSE;
		}else if(responseLogin.equals(RESULT_TRUE)){
			return RESULT_TRUE;
		}else{
			return RESULT_SERVER_ERROR;
		}

	}

	public String register(){

		RespuestaPreRegistroDTO respuestaPreregistro = new RespuestaPreRegistroDTO();
		PreRegistroDTO preRegistroDTO = new PreRegistroDTO();


		String firstName=userController.getName().split(" ")[0];
		String lastName=userController.getName().split(" ")[1];

		try {

			preRegistroDTO.setTipoDocumento(TYPE_CC);
			preRegistroDTO.setNumeroDocumento(cc);
			preRegistroDTO.setPrimerNombre(firstName);
			preRegistroDTO.setPrimerApellido(lastName);
			preRegistroDTO.setCorreo(user);
			preRegistroDTO.setClave(pswrd);

			Log.i("user email: ", user);

			respuestaPreregistro = PayOkClienteApi.getInstance().preRegistrar(preRegistroDTO);


			Log.i("response register code:", respuestaPreregistro.getCodigoRespuesta() );
			Log.i("res register message:", respuestaPreregistro.getMensajeUsuario());

			if(respuestaPreregistro.getCodigoRespuesta().equals(CODE_SUCCESS)){
				return RESULT_TRUE;
			}else{
				return RESULT_FALSE;
			}


		} catch (Exception e) {
			e.printStackTrace();
			return RESULT_SERVER_ERROR;
		}

	}

	public String login(){

		RespuestaInicioSesionDTO respuesta = new RespuestaInicioSesionDTO();

		try {
			UsuarioDTO usuarioDTO = new UsuarioDTO();


			if(user.length() != 0 && pswrd.length() != 0) {
				usuarioDTO.setUsuario(user);
				usuarioDTO.setClave(pswrd);

				respuesta = PayOkClienteApi.getInstance().iniciarSesion(usuarioDTO);

				Log.i("response payment login: " ,"response code login:"+ respuesta.getCodigoRespuesta() );
				Log.i("response payment login: " ,"response message login:"+ respuesta.getMensajeUsuario() );

				if(respuesta.getCodigoRespuesta().equals(CODE_SUCCESS)){
					return RESULT_TRUE;
				}else if (respuesta.getCodigoRespuesta().equals(CODE_USER_NOT_EXISTS)){
					return CODE_USER_NOT_EXISTS;
				}else{
					return RESULT_FALSE;
				}
			}else{
				return RESULT_SERVER_ERROR;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return RESULT_SERVER_ERROR;
		}

	}
	
	/**
	 * store credit card the object in shared preferences through all activities
	 * @param cardReference
	 */
	public void setPaymentCard(CreditCard card){

		mEditor.putString(SHARED_CARD_REFERENCE, card.reference);
		mEditor.putString(SHARED_CARD_TYPE, card.type);
		mEditor.putString(SHARED_CARD_NAME, card.name);
		mEditor.putString(SHARED_CARD_TERMINATION, card.termination);
		mEditor.commit();
	}
	
	/**
	 * get credit card object from shared preferences
	 * @return
	 */
	public CreditCard getPaymentCard(){

		
		String cardReference=null;
		String cardType=null;
		String cardName=null;
		String cardTermination=null;

		if (mPrefs.contains(SHARED_CARD_REFERENCE)) {
			cardReference= mPrefs.getString(SHARED_CARD_REFERENCE, "");
		}
		if (mPrefs.contains(SHARED_CARD_TYPE)) {
			cardType= mPrefs.getString(SHARED_CARD_TYPE, "");
		}
		if (mPrefs.contains(SHARED_CARD_NAME)) {
			cardName= mPrefs.getString(SHARED_CARD_NAME, "");
		}
		if (mPrefs.contains(SHARED_CARD_TERMINATION)) {
			cardTermination= mPrefs.getString(SHARED_CARD_TERMINATION, "");
		}

		CreditCard card=new CreditCard(cardName,cardReference,cardTermination,cardType);

		return card;
	}
	
	public boolean isPaymentSelected(){
		
		if (mPrefs.contains(SHARED_CARD_REFERENCE) && mPrefs.contains(SHARED_CARD_TYPE)) {
			return true;
		}else{
			return false;
		}
	}

	public ArrayList<CreditCard> getCardList(){

		RespuestaConsultaTarjetasDTO respuestaConsultarTarjetas;
		ArrayList<CreditCard> cards=null;
		UsuarioDTO usuarioDTO = new UsuarioDTO();
		usuarioDTO.setUsuario(user);
		usuarioDTO.setClave(pswrd);

		try {
			respuestaConsultarTarjetas = PayOkClienteApi.getInstance().consultarMediosPagos(usuarioDTO);

			if(respuestaConsultarTarjetas != null && 
					respuestaConsultarTarjetas.getListadoMedioPago() != null)  {

				cards=new ArrayList<CreditCard>();

				for(ListadoMedioPago item: respuestaConsultarTarjetas.getListadoMedioPago()) {
					
					
					String[] terminationParts;
					String termination;
					String name;
					String cardType=Long.toString(item.getTipoMedioPagoId());
					
					if(cardType.equals(CreditCard.CARD_TYPE_MASTERCARD)){
						name=CreditCard.CARD_NAME_MASTERCARD;
					}else if(cardType.equals(CreditCard.CARD_TYPE_VISA)){
						name=CreditCard.CARD_NAME_VISA;
					}else if(cardType.equals(CreditCard.CARD_TYPE_DINERS)){
						name=CreditCard.CARD_NAME_DINERS;
					}else{
						name=CreditCard.CARD_NAME_AMEX;
					}
					
					termination=item.getDescripcion();
					terminationParts=termination.split("-");
					termination=terminationParts[0];
					
					CreditCard creditCard=new CreditCard(name,Long.toString(item.getMedioPagoId()),termination,cardType);
					
					Log.i(this.getClass().getSimpleName()+"getCardList","name:"+name);
					Log.i(this.getClass().getSimpleName()+"getCardList","termination:"+termination);
					Log.i(this.getClass().getSimpleName()+"getCardList","type:"+item.getTipoMedioPagoId());

					cards.add(creditCard);
				}

			} 

		} catch (Exception e) {
			e.printStackTrace();
		}

		return cards;
	}

}
