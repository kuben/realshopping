package com.github.kuben.realshopping.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RealShoppingException;
import com.github.kuben.realshopping.exceptions.RealShoppingException.Type;

abstract class GeneralListener{
	private Shop shop;
	private Player player;
	
	public GeneralListener(Player player) throws RealShoppingException{
		if(RealShopping.PInvMap.containsKey(player.getName())){
			shop = RealShopping.PInvMap.get(player.getName()).getShop();
			if((shop.getOwner().equals("@admin") && player.hasPermission("realshopping.rsset"))
					|| shop.getOwner().equals(player.getName())){
				this.player = player;
				//Now adds itself to set, or throws exception if failing
				if(!RSPlayerListener.addConversationListener(this)) throw new RealShoppingException(player, Type.PLAYER_ALREADY_HAS_LISTENER);
			} else throw new RealShoppingException(player, Type.NOT_ALLOWED_MANAGE);
		} else throw new RealShoppingException(player, Type.NOT_IN_SHOP);
	}
	
	Shop getShop(){ return shop; }
	Player getPlayer(){ return player; }
	
	abstract void onInteract(PlayerInteractEvent event);
}