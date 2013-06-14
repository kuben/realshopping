package com.github.kuben.realshopping.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.exceptions.RSListenerException;

public class ChestFreeManageListener extends GeneralListener implements Appliable {
	
	private Map<Location, Inventory> toUpdate = new HashMap<Location, Inventory>();

	public ChestFreeManageListener(Player player) throws RSListenerException{
		super(player);
	}
	
	private void onInteract(PlayerInteractEvent event){
		if(event.getAction() == Action.LEFT_CLICK_BLOCK){//Cancel changes
			event.setCancelled(true);
			Location l = event.getClickedBlock().getLocation();
			if(toUpdate.containsKey(l)){
				toUpdate.remove(l);
				event.getPlayer().sendRawMessage(ChatColor.GREEN + "Changes undone.");
			}
		}
	}
	
	void onCloseChest(InventoryCloseEvent event){
		Inventory i = event.getInventory();
		if(i.getType() == InventoryType.CHEST && i.getHolder() instanceof Chest){
			Location l = ((Chest)i.getHolder()).getLocation();
			if(getShop().isChest(l)){
				toUpdate.put(l, i);
			}
		}
	}

	public int apply(){
		for(Location l:toUpdate.keySet()){
			if(getShop().isChest(l)) getShop().setChestContents(l, toUpdate.get(l));
		}
		RSPlayerListener.killConversationListener(this);
		RealShopping.updateEntrancesDb();
		return toUpdate.size();
	}
	
	void onEvent(Event event){
		if(event instanceof PlayerInteractEvent) onInteract((PlayerInteractEvent) event);
		else if(event instanceof InventoryCloseEvent) onCloseChest((InventoryCloseEvent) event);
	}
}