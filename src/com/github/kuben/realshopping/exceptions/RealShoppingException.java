package com.github.kuben.realshopping.exceptions;

import org.bukkit.entity.Player;

import com.github.kuben.realshopping.RealShopping;

public class RealShoppingException extends Exception {

	private static final long serialVersionUID = 6492116744455070187L;
	private Player player;
	private Type type;
	
	public enum Type {
		NOT_IN_SHOP, NOT_ALLOWED_MANAGE, PLAYER_ALREADY_HAS_LISTENER;
	}
	
	public RealShoppingException(Player player, Type type){
		super((type==Type.NOT_IN_SHOP) ? "Player " + player + " is not in a store.":
			(type==Type.NOT_ALLOWED_MANAGE) ? "Player " + player + " is not allowed to manage store " + RealShopping.PInvMap.get(player.getName()).getShop().getName():
			(type==Type.PLAYER_ALREADY_HAS_LISTENER) ? "Player " + player + " is already in an conversation.":"");
		this.player = player;
		this.type = type;
	}
	
	public Type getType(){ return type; }
	public Player getPlayer(){ return player; }
	
}