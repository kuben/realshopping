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

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.exceptions.RSListenerException;
import com.github.kuben.realshopping.exceptions.RSListenerException.Type;

public class ChestManageListener extends GeneralListener implements SignalReceiver {
	
	private Set<Location> selected = new HashSet<>();
	
	public enum SIGNAL{
		ADD_ITEMS, DEL_ITEMS, CLEAR_ITEMS, SEL_ALL, SEL_CLEAR;
	}

	public ChestManageListener(Player player) throws RSListenerException{
		super(player);
	}
	
	private void onInteract(PlayerInteractEvent event){
		if(event.getAction() == Action.LEFT_CLICK_BLOCK){//Add to selection
			event.setCancelled(true);
			Location l = event.getClickedBlock().getLocation();
			if(getShop().isChest(l)){
				if (!selected.contains(l)){
					selected.add(l);
					blockChange(l, Material.GOLD_BLOCK.getId());
					getPlayer().sendRawMessage(ChatColor.GREEN + LangPack.BLOCK_ADDED_TO_SELECTION);
				} else {
					blockChange(l, Material.GOLD_BLOCK.getId());
					getPlayer().sendRawMessage(ChatColor.RED + LangPack.BLOCK_ALREADY_SELECTED);
				}
			} else getPlayer().sendRawMessage(ChatColor.RED + LangPack.THEBLOCKYOUSELECTEDISNTACHEST);
		}
	}
	
	public Object receiveSignal(Object sig) throws RSListenerException {
		SIGNAL SIG = null;
		if(sig instanceof SIGNAL) SIG = (SIGNAL) sig;
		else if(sig instanceof Object[]){
			Object[] o = (Object[]) sig;
			if(o.length > 1 && o[0] instanceof SIGNAL) SIG = (SIGNAL) o[0];
		}
		if(SIG == null) throw new RSListenerException(getPlayer(), Type.SIGNAL_MISMATCH);
		
		switch(SIG){
			case ADD_ITEMS:
			case DEL_ITEMS:
				 if(sig instanceof Object[]){
					Object[] o = (Object[]) sig;
					if(o.length > 1 && o[1] instanceof Object[][]){
						if(SIG == SIGNAL.ADD_ITEMS) return additems((Object[][]) o[1]);
						else return delitems((Object[][]) o[1]);
					}
				 }
				 throw new RSListenerException(getPlayer(), Type.SIGNAL_MISMATCH);
			case CLEAR_ITEMS:
				return clearall();
			case SEL_ALL:
				return selall();
			case SEL_CLEAR:
				return selclear();
			default:
				 throw new RSListenerException(getPlayer(), Type.SIGNAL_MISMATCH);
		}
	}
	
	int selall(){
		int i = 0;
		for(Location l:getShop().getChests().keySet()){
			selected.add(l);
			getPlayer().sendBlockChange(l, Material.GOLD_BLOCK, (byte)0);
			i++;
		}
		return i;
	}
	
	int selclear(){
		int i = 0;
		for(Location l:selected){
			getPlayer().sendBlockChange(l, l.getBlock().getTypeId(), l.getBlock().getData());
			i++;
		}
		selected.clear();
		return i;
	}

	int clearall(){
		for(Location l:selected){
			getShop().clearChestItems(l);
		}
		RealShopping.updateEntrancesDb();
		return selected.size();
	}
	
	Object[] additems(Object[][] ids){
		int i = 0;
		for(Location l:selected){
			getShop().addChestItem(l, ids);
			i++;
		}
		RealShopping.updateEntrancesDb();
		return new Object[]{ids.length, i};
	}
	
	Object[] delitems(Object[][] ids){
		int i = 0;
		for(Location l:selected){
			getShop().delChestItem(l, ids);
			i++;
		}
		RealShopping.updateEntrancesDb();
		return new Object[]{ids.length, i};
	}

	void onEvent(Event event){
		if(event instanceof PlayerInteractEvent) onInteract((PlayerInteractEvent) event);
	}
}