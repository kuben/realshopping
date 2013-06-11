package com.github.kuben.realshopping.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.exceptions.RSListenerException;

public class ChestListener extends GeneralListener implements Appliable {
	
	private Set<Location> selected = new HashSet<Location>();
	private Type type;
	
	public enum Type {
		ADD, REMOVE
	}

	public ChestListener(Player player, Type type) throws RSListenerException{
		super(player);
		this.type = type;
	}
	
	private void onInteract(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){//Add to selection
			event.setCancelled(true);
			Location l = event.getClickedBlock().getLocation();
			if((type == Type.ADD || getShop().isChest(l)) && !selected.contains(l)){
				selected.add(l);
				getPlayer().sendBlockChange(l, Material.GOLD_BLOCK, (byte)0);
				getPlayer().sendRawMessage(ChatColor.GREEN + "Block added to selection.");
			} else {//TODO Add elseif block isn't chest
				getPlayer().sendBlockChange(l, Material.GOLD_BLOCK, (byte)0);
				getPlayer().sendRawMessage(ChatColor.RED + "Block already selected.");
			}
		} else if(event.getAction() == Action.LEFT_CLICK_BLOCK){//Remove from selection
			event.setCancelled(true);
			Location l = event.getClickedBlock().getLocation();
			if(selected.contains(l)){
				selected.remove(l);
				getPlayer().sendBlockChange(l, l.getBlock().getTypeId(), (byte)0);
				getPlayer().sendRawMessage(ChatColor.GREEN + "Block removed from selection.");
			} else {
				getPlayer().sendBlockChange(l, l.getBlock().getTypeId(), (byte)0);
				getPlayer().sendRawMessage(ChatColor.RED + "Block wasn't selected.");
			}
		}
	}
	
	public int apply(){
		for(Location l:selected){
			getPlayer().sendBlockChange(l, l.getBlock().getTypeId(), (byte)0);
			if(type == Type.ADD) getShop().addChest(l);
			else getShop().delChest(l);
		}
		RSPlayerListener.killConversationListener(this);
		RealShopping.updateEntrancesDb();
		return selected.size();
	}

	void onEvent(Event event){
		if(event instanceof PlayerInteractEvent) onInteract((PlayerInteractEvent) event);
	}
}