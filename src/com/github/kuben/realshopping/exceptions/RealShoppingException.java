package com.github.kuben.realshopping.exceptions;

public class RealShoppingException extends Exception {

	private static final long serialVersionUID = 1516574848772470424L;
	protected Type type;
	
	public enum Type {
		EEPAIR_ALREADY_EXISTS
	}
	
	public RealShoppingException(Type type){
		super((type==Type.EEPAIR_ALREADY_EXISTS)?"This entrance/exit pair already exists.":"Nigga");
		this.type = type;
	}

	public Type getType(){ return type; }
}