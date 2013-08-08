package com.github.kuben.realshopping.exceptions;

public class RealShoppingException extends Exception {

	private static final long serialVersionUID = 1516574848772470424L;
	protected Type type;
	private String info = "";
	
	public enum Type {
		EEPAIR_ALREADY_EXISTS, SHOP_DOESNT_EXIST, CANNOT_FAVORTIE_OWN_SHOP
		, FEATURE_NOT_AVAIABLE_FOR_ADMIN_STORES, NOT_VALID_ARGUMENT, SYNTAX_ERROR
	}
	
	public RealShoppingException(Type type){//No translation
		super((type==Type.EEPAIR_ALREADY_EXISTS)?"This entrance/exit pair already exists."
				:(type==Type.SHOP_DOESNT_EXIST)?"The named store doesn't exist."
				:(type==Type.CANNOT_FAVORTIE_OWN_SHOP)?"A player can't favorite their own store."
				:(type==Type.FEATURE_NOT_AVAIABLE_FOR_ADMIN_STORES)?"The feature is not avaiable for admin stores."
				:(type==Type.NOT_VALID_ARGUMENT)?"That is not a valid argument."
				:(type==Type.SYNTAX_ERROR)?"Syntax error.":"");
		this.type = type;
	}
	
	public RealShoppingException(Type type, String info){
		this(type);
		this.info = info;
	}

	public Type getType(){ return type; }
	
	public String getAdditionalInfo(){ return info; }
}