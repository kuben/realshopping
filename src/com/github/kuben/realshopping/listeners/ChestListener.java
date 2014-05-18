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
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.exceptions.RSListenerException;
import com.github.stengun.realshopping.SerializationManager;

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
			Material mat = (type == Type.ADD)?Material.GOLD_BLOCK:Material.IRON_BLOCK;
			if(!selected.contains(l)){
				if(type == Type.ADD || getShop().isChest(l)){
					selected.add(l);
					getPlayer().sendRawMessage(ChatColor.GREEN + LangPack.BLOCK_ADDED_TO_SELECTION);
					blockChange(l, mat);
				} else if(type == Type.REMOVE && !getShop().isChest(l)){
					getPlayer().sendRawMessage(ChatColor.RED + LangPack.THEBLOCKYOUSELECTEDISNTACHEST);
				}
			} else {
				getPlayer().sendRawMessage(ChatColor.RED + LangPack.BLOCK_ALREADY_SELECTED);
				blockChange(l, mat);
			}
		} else if(event.getAction() == Action.LEFT_CLICK_BLOCK){//Remove from selection
			event.setCancelled(true);
			Location l = event.getClickedBlock().getLocation();
			if(selected.contains(l)){
				selected.remove(l);
				getPlayer().sendBlockChange(l, l.getBlock().getTypeId(), (byte)0);
				getPlayer().sendRawMessage(ChatColor.GREEN + LangPack.BLOCK_REMOVED_FROM_SELECTION);
			} else {
				getPlayer().sendBlockChange(l, l.getBlock().getTypeId(), (byte)0);
				getPlayer().sendRawMessage(ChatColor.RED + LangPack.BLOCK_WASNT_SELECTED);
			}
		}
	}
	
	public int apply(){
		for(Location l:selected){
			getPlayer().sendBlockChange(l, l.getBlock().getTypeId(), l.getBlock().getData());
			if(type == Type.ADD) getShop().addChest(l);
			else getShop().delChest(l);
		}
		RSPlayerListener.killConversationListener(this);
		SerializationManager.saveShops();
		return selected.size();
	}

	void onEvent(Event event){
		if(event instanceof PlayerInteractEvent) onInteract((PlayerInteractEvent) event);
	}
}