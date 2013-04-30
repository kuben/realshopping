package com.github.kuben.realshopping.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RealShoppingException;

public class ChestListener extends GeneralListener {
	
	private Set<Location> selected = new HashSet<Location>();
	
	public ChestListener(Player player) throws RealShoppingException{
		super(player);
	}
	
	void onInteract(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){//Add to selection
			Location l = event.getClickedBlock().getLocation();
			if(!selected.contains(l)){
				selected.add(l);
				event.getPlayer().sendBlockChange(l, Material.GOLD_BLOCK, (byte)0);
				event.getPlayer().sendMessage(ChatColor.GREEN + "Block added to selection.");
			} else {
				event.getPlayer().sendBlockChange(l, Material.GOLD_BLOCK, (byte)0);
				event.getPlayer().sendMessage(ChatColor.RED + "Block already selected.");
			}
		} else {//Remove from selection
			Location l = event.getClickedBlock().getLocation();
			if(selected.contains(l)){
				selected.remove(l);
				event.getPlayer().sendBlockChange(l, l.getBlock().getTypeId(), (byte)0);
				event.getPlayer().sendMessage(ChatColor.GREEN + "Block removed from selection.");
			} else {
				event.getPlayer().sendBlockChange(l, l.getBlock().getTypeId(), (byte)0);
				event.getPlayer().sendMessage(ChatColor.RED + "Block wasn't selected.");
			}
		}
	}
	
	int apply(){
		for(Location l:selected){
			getPlayer().sendBlockChange(l, l.getBlock().getTypeId(), (byte)0);
			getShop().addChest(l);
		}
		RSPlayerListener.killConversationListener(this);
		return selected.size();
	}
}