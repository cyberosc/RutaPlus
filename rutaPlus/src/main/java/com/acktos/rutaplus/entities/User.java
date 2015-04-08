package com.acktos.rutaplus.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
	
	private String id=null;
	private String name=null;
	private String email=null;
	private String cc=null;
	private String pswrd=null;
	private String gId=null;
	private String state=null;
	private String phone=null;
	private String mobileId=null;
	private String enterprise=null;
	
	public static final String KEY_USER="cliente";
	public static final String KEY_EMAIL="email";
	public static final String KEY_ID="id";
	public static final String KEY_NAME="name";
	public static final String KEY_CC="cc";
	public static final String KEY_PSWRD="pswrd";
	public static final String KEY_ENTERPRISE="empresa";
	
	
	public String getMobileId() {
		return mobileId;
	}

	public void setMobileId(String mobileId) {
		this.mobileId = mobileId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getPswrd() {
		return pswrd;
	}

	public void setPswrd(String pswrd) {
		this.pswrd = pswrd;
	}

	public String getgId() {
		return gId;
	}

	public void setgId(String gId) {
		this.gId = gId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}

	@Override
	public String toString(){
		return toJson();
	}
	
	public String toJson(){
		return "{"
				+ "\"id\":\""+getId()
				+"\",\"name\":\""+getName()
				+"\",\"email\":\""+getEmail()
				+"\",\"phone\":\""+getPhone()
				+"\",\"pswrd\":\""+getPswrd()
				+"\"," +"\"state\":\""+getState()
				+"\",\"gId\":\""+getgId()
				+"\",\"mobile_id\":\""+getMobileId()
				+"\",\"enterprise\":\""+getEnterprise()
				+"\"}";
	}
	
	public void ToObject(String jsonString) throws JSONException{

		JSONArray jsonArray=new JSONArray(jsonString);
		JSONObject jsonObject=jsonArray.getJSONObject(0);
		jsonToObject(jsonObject);	
	}
	
	public void jsonToObject(JSONObject jsonObject) throws JSONException{
		
		this.setId(jsonObject.getString("id"));
		this.setName(jsonObject.getString("name"));
		this.setEmail(jsonObject.getString("email"));
		this.setPswrd(jsonObject.getString("pswrd"));
		this.setState(jsonObject.getString("state"));
		this.setgId(jsonObject.getString("gId"));
		this.setPhone(jsonObject.getString("phone"));
		this.setMobileId(jsonObject.getString("mobile_id"));
		this.setEnterprise(jsonObject.getString("enterprise"));
		
	}
}
