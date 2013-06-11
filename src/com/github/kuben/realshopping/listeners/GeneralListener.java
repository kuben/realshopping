package com.github.kuben.realshopping.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RSListenerException;
import com.github.kuben.realshopping.exceptions.RSListenerException.Type;

abstract class GeneralListener{
	private Shop shop;
	private Player player;
	
	public GeneralListener(Player player) throws RSListenerException{
		if(RealShopping.PInvMap.containsKey(player.getName())){
			shop = RealShopping.PInvMap.get(player.getName()).getShop();
			if((shop.getOwner().equals("@admin") && player.hasPermission("realshopping.rsset"))
					|| shop.getOwner().equals(player.getName())){
				this.player = player;
				//Now adds itself to set, or throws exception if failing
				if(!RSPlayerListener.addConversationListener(this)) throw new RSListenerException(player, Type.PLAYER_ALREADY_HAS_LISTENER);
			} else throw new RSListenerException(player, Type.NOT_ALLOWED_MANAGE);
		} else throw new RSListenerException(player, Type.NOT_IN_SHOP);
	}
	
	public GeneralListener(Player player, String store) throws RSListenerException{//Isn't and shouldn't be in store
		if(!RealShopping.PInvMap.containsKey(player.getName())){
			shop = RealShopping.shopMap.get(store);//Can be null at this point
			//Doesn't check for permissions
			this.player = player;
			//Now adds itself to set, or throws exception if failing
			if(!RSPlayerListener.addConversationListener(this)) throw new RSListenerException(player, Type.PLAYER_ALREADY_HAS_LISTENER);
		} else throw new RSListenerException(player, Type.IN_SHOP);
	}
	
	Shop getShop(){ return shop; }
	Player getPlayer(){ return player; }
	
	abstract void onEvent(Event event);
}

interface SignalReceiver {
	
	Object receiveSignal(Object sig) throws RSListenerException;
	
}

interface Appliable {
	
	int apply();
	
}