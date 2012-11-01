/*
 * RealShopping Bukkit plugin for Minecraft
 * Copyright 2012 Jakub Fojt
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package com.github.kuben.realshopping;

import java.awt.Event;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sun.xml.internal.stream.Entity;

public class RSPlayerListener implements Listener {
	
	public static Map<Integer, TreeMap<String, Integer>> statsMap = new HashMap<Integer, TreeMap<String, Integer>>();//TODO change integer to PItem?, move to separate class with updateStats
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onTeleport(PlayerTeleportEvent event){
		Player player = event.getPlayer();
		if(RealShopping.PInvMap.containsKey(player.getName()) && event.getCause() != TeleportCause.UNKNOWN){
			if(!RealShopping.PInvMap.get(player.getName()).hasPaid()){
				event.setCancelled(true);
				RealShopping.punish(player);
			} else {
				if(RealShopping.allowTpOutOfStore(event.getTo())){
					String shopName = RealShopping.PInvMap.get(player.getName()).getStore();
					if(RealShopping.shopMap.get(shopName).sellToStore.containsKey(player.getName()))
						RealShopping.shopMap.get(shopName).sellToStore.remove(player.getName());
					RealShopping.PInvMap.remove(player.getName());
					player.sendMessage(ChatColor.RED + LangPack.YOULEFT + shopName);
				} else {
					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + LangPack.YOUARENTALLOWEDTOTELEPORTTHERE);
				}
			}
		}
	}
	@EventHandler (priority = EventPriority.HIGH)
	public void onInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Block b = event.getClickedBlock();
		if(event.getItem() != null && RealShopping.forbiddenInStore.contains(event.getItem().getTypeId()))
			if(RealShopping.PInvMap.containsKey(player.getName()))
				if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
					player.sendMessage(ChatColor.RED + LangPack.YOUCANTUSETHATITEMINSTORE);
					event.setUseItemInHand(Result.DENY);
				}
		if(RealShopping.jailedPlayers.containsKey(player.getName())) event.setCancelled(true);
		else {
			if(event.hasBlock())
				if(b.getType() == Material.GLASS || b.getType() == Material.THIN_GLASS) {
					if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
						if(RealShopping.PInvMap.containsKey(player.getName())){
							if(player.hasPermission("realshopping.rsexit")) event.setCancelled(RealShopping.exit(player, false));
						} else {
							if(player.hasPermission("realshopping.rsenter")) event.setCancelled(RealShopping.enter(player, false));
						}
					}
				/*} else if(b.getType() == Material.SAND) {
					if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
						updateStats();
					}*/
				} else if(b.getType() == Material.OBSIDIAN) {
					if(RealShopping.PInvMap.containsKey(player.getName())){
						if(player.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType() == Material.STEP){
							if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
								if(player.hasPermission("realshopping.rspay")){
									StorageMinecart[] SM = RealShopping.checkForCarts(event.getClickedBlock().getLocation());
									Inventory[] carts = new Inventory[SM.length];
									for(int i = 0;i < SM.length;i++)
										carts[i] = SM[i].getInventory();
									event.setCancelled(RealShopping.pay(player, carts));
								}
							} else if(event.getAction() == Action.LEFT_CLICK_BLOCK) if(player.hasPermission("realshopping.rscost")){
								if(RealShopping.PInvMap.containsKey(player.getName())){
									event.setCancelled(true);
									StorageMinecart[] SM = RealShopping.checkForCarts(event.getClickedBlock().getLocation());
									Inventory[] carts = new Inventory[SM.length];
									for(int i = 0;i < SM.length;i++)
										carts[i] = SM[i].getInventory();
									player.sendMessage(LangPack.YOURARTICLESCOST + RealShopping.PInvMap.get(player.getName()).toPay(carts) + RealShopping.unit);
								} else {
									player.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
								}
							}
						} else if(player.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType() == Material.RED_MUSHROOM){
							if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
								if(player.hasPermission("realshopping.rspay")){
									Object[] mcArr = player.getWorld().getEntitiesByClass(StorageMinecart.class).toArray();
									StorageMinecart sM = null;
									for(Object o:mcArr)
										if(((StorageMinecart)o).getLocation().getBlock().getLocation().equals(b.getLocation().subtract(0,1,0)))
											sM = (StorageMinecart)o;
									if(sM != null)
										event.setCancelled(RealShopping.shipCartContents(sM, player));
								}
							}
						}/* else if(player.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType() == Material.BROWN_MUSHROOM){
							if(Config.enableSelling){
								if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
									if(event.hasItem()){
										event.setCancelled(RealShopping.addItemToSell(player, event.getItem()));
									} else {
										event.setCancelled(RealShopping.confirmToSell(player));
									}
								} else if(event.getAction() == Action.LEFT_CLICK_BLOCK){
										event.setCancelled(RealShopping.cancelToSell(player));
								}
							} else player.sendMessage(ChatColor.RED + LangPack.SELLINGTOSTORESISNOTENABLEDONTHISSERVER);
						}*/
					}
				}
		}
	}
	
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryOpenEvent(InventoryOpenEvent event){
        if (event.getInventory().getHolder() instanceof Chest){
        	if(!RealShopping.PInvMap.containsKey(event.getPlayer().getName())){//If player is not in store
        		if(RealShopping.isChestProtected(((Chest) event.getInventory().getHolder()).getLocation())){
        			event.setCancelled(true);
        			((Chest) event.getInventory().getHolder()).getInventory().getViewers().remove(event.getPlayer());
        			((CommandSender) event.getPlayer()).sendMessage(ChatColor.RED + "[RealShopping] " + LangPack.THISCHESTISPROTECTED);
        		}
        	}
        } else if (event.getInventory().getHolder() instanceof DoubleChest){
        	if(!RealShopping.PInvMap.containsKey(event.getPlayer().getName())){//If player is not in store
        		if(RealShopping.isChestProtected(((Chest)((DoubleChest) event.getInventory().getHolder()).getLeftSide()).getLocation())
        		| RealShopping.isChestProtected(((Chest)((DoubleChest) event.getInventory().getHolder()).getRightSide()).getLocation())){
        			event.setCancelled(true);
        			((DoubleChest) event.getInventory().getHolder()).getInventory().getViewers().remove(event.getPlayer());
        			((CommandSender) event.getPlayer()).sendMessage(ChatColor.RED + "[RealShopping] " + LangPack.THISCHESTISPROTECTED);
        		}
        	}
        }
    }
	
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryCloseEvent(InventoryCloseEvent event){
        if (event.getInventory().getTitle().equals("Sell to store")){
        	if(RealShopping.PInvMap.containsKey(event.getPlayer().getName())){//If player is in store
        		RealShopping.sellToStore((Player) event.getPlayer(), event.getInventory().getContents());
        	}
        }
    }
	
    
	@EventHandler (priority = EventPriority.MONITOR)
	public void onLogin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(Config.autoUpdate == 2 || Config.autoUpdate == 4)
			if(!RealShopping.newUpdate.equals(""))
				if(player.isOp() || player.hasPermission("realshopping.rsupdate")){
					player.sendMessage(ChatColor.LIGHT_PURPLE + RealShopping.newUpdate);
				}
		}

	public static void updateStats(){
		Map<Integer, TreeMap<String, Integer>> oldStats = statsMap;
		
		statsMap.clear();
		String[] keys = RealShopping.shopMap.keySet().toArray(new String[0]);
		for(String s:keys){
			Statistic[] sKeys = RealShopping.shopMap.get(s).stats.toArray(new Statistic[0]);
			for(Statistic stat:sKeys){
				if(System.currentTimeMillis() - 3600000 < stat.getTime() && stat.isBought()){//Only past hour and only bought
					if(!statsMap.containsKey(stat.getItem().type)) statsMap.put(stat.getItem().type, new TreeMap<String, Integer>());
					if(!statsMap.get(stat.getItem().type).containsKey(s)) statsMap.get(stat.getItem().type).put(s, 0);
					statsMap.get(stat.getItem().type).put(s, statsMap.get(stat.getItem().type).get(s) + stat.getAmount());
				}
			}
		}
		Object[] keys2 = statsMap.keySet().toArray();
		for(int i = 0;i < keys2.length;i++){//Sort
			StatComparator bvc = new StatComparator(statsMap.get(keys2[i]));
			TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
			sorted_map.putAll(statsMap.get(keys2[i]));
			statsMap.put((Integer)keys2[i], sorted_map);
		}
		
	System.out.println(statsMap);
	}
	
}
