package com.github.kuben.realshopping.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RSListenerException;
import com.github.kuben.realshopping.exceptions.RealShoppingException;

public class SetupStoreListener extends GeneralListener implements Appliable {
	
	private List<Location> entrances = new ArrayList<Location>();
	private List<Location> exits = new ArrayList<Location>();
	private final Type type;
	private final String store;
	private final boolean admin;
	
	public enum Type {//Append and create the same
		APPEND, DELETE
	}

	public SetupStoreListener(Player player, Type type, String store, boolean admin) throws RSListenerException {
		super(player, store);
		this.type = type;
		this.store = store;
		this.admin = admin;
	}
	
	private void message(){
		if(entrances.size() > exits.size()){
			if(type == Type.APPEND) getPlayer().sendRawMessage("Please select exit " + ChatColor.DARK_GREEN + "#" + (exits.size()+1));
			else getPlayer().sendRawMessage("Please select the exit linked to entrance  " + ChatColor.DARK_GREEN + "#" + (exits.size()+1));
		} else
			getPlayer().sendRawMessage("Please select entrance " + ChatColor.DARK_GREEN + "#" + (entrances.size()+1));
	}
	
	private void onInteract(PlayerInteractEvent event){
		Location temp = null;
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){//Use block on top of clicked block
			event.setCancelled(true);
			temp = event.getClickedBlock().getLocation().clone().add(0, 1, 0);
		} else if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR){//Use coordinates
			event.setCancelled(true);
			temp = getPlayer().getLocation().getBlock().getLocation();
		}
		if(temp != null){
			if(entrances.size() > exits.size()){//Add exit
				if(type == Type.APPEND){
					exits.add(temp);
					getPlayer().sendRawMessage(ChatColor.GREEN + LangPack.ADDED + ChatColor.DARK_PURPLE + RSUtils.locAsString(temp) + ChatColor.RESET + " to exits list.");	
				} else {
					if(getShop().getCorrExit(entrances.get(entrances.size()-1)).equals(temp)){
						exits.add(temp);
						getPlayer().sendRawMessage(ChatColor.GREEN + LangPack.ADDED + ChatColor.DARK_PURPLE + RSUtils.locAsString(temp) + ChatColor.RESET + " to exits list.");	
					} else getPlayer().sendRawMessage(ChatColor.DARK_PURPLE + RSUtils.locAsString(temp) + ChatColor.RED + " is not an exit to the last entrance.");
				}
			} else {//Add entrance
				if(type == Type.APPEND){
					entrances.add(temp);
					getPlayer().sendRawMessage(ChatColor.GREEN + LangPack.ADDED + ChatColor.DARK_PURPLE + RSUtils.locAsString(temp) + ChatColor.RESET + " to entrances list.");
				} else {
					if(getShop().hasEntrance(temp)){
						entrances.add(temp);
						getPlayer().sendRawMessage(ChatColor.GREEN + LangPack.ADDED + ChatColor.DARK_PURPLE + RSUtils.locAsString(temp) + ChatColor.RESET + " to entrances list.");
					} else getPlayer().sendRawMessage(ChatColor.DARK_PURPLE + RSUtils.locAsString(temp) + ChatColor.RED + " is not an entrance to " + getShop().getName());
				}
			}
			message();
		}
	}
	
	public int apply(){
		int r = 0;
		if(exits.size() > 0){
			if(type == Type.APPEND){
				Shop tempShop = getShop();
				if(tempShop == null){//Time to create a store
					tempShop = new Shop(store, getPlayer().getWorld().getName(), admin?"@admin":getPlayer().getName());
					RealShopping.shopMap.put(store, tempShop);
				}
				for(int i = 0;i < exits.size();i++){
					try {
						tempShop.addEntranceExit(entrances.get(i), exits.get(i));
						r++;
					} catch (RealShoppingException e) { }//Ignore
				}	
			} else {
				for(int i = 0;i < exits.size();i++){
					getShop().removeEntranceExit(entrances.get(i), exits.get(i));
					r++;
				}
			}
			RealShopping.updateEntrancesDb();
			RSPlayerListener.killConversationListener(this);
		}
		return r;
	}

	void onEvent(Event event){
		if(event instanceof PlayerInteractEvent) onInteract((PlayerInteractEvent) event);
	}
}