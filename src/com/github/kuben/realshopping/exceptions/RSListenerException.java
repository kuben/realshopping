package com.github.kuben.realshopping.exceptions;

import org.bukkit.entity.Player;

import com.github.kuben.realshopping.RealShopping;

public class RSListenerException extends Exception {

	private static final long serialVersionUID = 6492116744455070187L;
	private Player player;
	protected Type type;
	
	public enum Type {
		IN_SHOP, NOT_IN_SHOP, NOT_ALLOWED_MANAGE, PLAYER_ALREADY_HAS_LISTENER
		, LISTENER_MISMATCH, SIGNAL_MISMATCH, RETURN_VALUE_MISMATCH;
	}
	
	public RSListenerException(Player player, Type type){
		super((type==Type.NOT_IN_SHOP) ? "Player " + player + " is not in a store.":
			(type==Type.NOT_ALLOWED_MANAGE) ? "Player " + player + " is not allowed to manage store " + RealShopping.getPInv(player).getShop().getName():
			(type==Type.PLAYER_ALREADY_HAS_LISTENER) ? "Player " + player + " is already in an conversation.":
			(type==Type.IN_SHOP) ? "Player " + player + " is in a store, which is not allowed.":
			//Used in RSPlayerListener
			(type==Type.LISTENER_MISMATCH) ? "Listener is not an instance of SignalReceiver. This is a bug with the plugin, please report it to the plugin creator.":
			//Used in listeners
			(type==Type.SIGNAL_MISMATCH) ? "Wrong type of signal sent. This is a bug with the plugin, please report it to the plugin creator.":
			//Used in prompts
			(type==Type.RETURN_VALUE_MISMATCH) ? "Wrong type of value was returned. This is a bug with the plugin, please report it to the plugin creator.":"");
		this.player = player;
		this.type = type;
	}
	
	public Type getType(){ return type; }
	public Player getPlayer(){ return player; }
	
}