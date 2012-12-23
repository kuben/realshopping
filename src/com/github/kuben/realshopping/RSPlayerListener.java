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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;

public class RSPlayerListener implements Listener {
	
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
				} else if(b.getType() == Material.OBSIDIAN) {
					if(RealShopping.PInvMap.containsKey(player.getName())){
						if(player.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType() == Material.STEP){
							if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
								if(player.hasPermission("realshopping.rspay")){
									Inventory[] carts = null;
									if(Config.allowFillChests && (Config.cartEnabledW.contains("@all") || Config.cartEnabledW.contains(player.getWorld().getName()))){
										StorageMinecart[] SM = RealShopping.checkForCarts(event.getClickedBlock().getLocation());
										carts = new Inventory[SM.length];
										for(int i = 0;i < SM.length;i++)
											carts[i] = SM[i].getInventory();
									}
									event.setCancelled(RealShopping.pay(player, carts));
								}
							} else if(event.getAction() == Action.LEFT_CLICK_BLOCK) if(player.hasPermission("realshopping.rscost")){
								if(RealShopping.PInvMap.containsKey(player.getName())){
									event.setCancelled(true);
									Inventory[] carts = null;
									if(Config.allowFillChests && Config.cartEnabledW.contains(player.getWorld().toString())){
										StorageMinecart[] SM = RealShopping.checkForCarts(event.getClickedBlock().getLocation());
										carts = new Inventory[SM.length];
										for(int i = 0;i < SM.length;i++)
											carts[i] = SM[i].getInventory();
									}
									player.sendMessage(LangPack.YOURARTICLESCOST + RealShopping.PInvMap.get(player.getName()).toPay(carts) + RealShopping.unit);
								} else {
									player.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
								}
							}
						} else if(player.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType() == Material.RED_MUSHROOM){
							if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
								if(player.hasPermission("realshopping.rspay")){
									if(Config.allowFillChests){
										if(Config.cartEnabledW.contains(player.getWorld().toString())){
											Object[] mcArr = player.getWorld().getEntitiesByClass(StorageMinecart.class).toArray();
											StorageMinecart sM = null;
											for(Object o:mcArr)
												if(((StorageMinecart)o).getLocation().getBlock().getLocation().equals(b.getLocation().subtract(0,1,0)))
													sM = (StorageMinecart)o;
											if(sM != null)
												event.setCancelled(RealShopping.shipCartContents(sM, player));
										} else player.sendMessage(ChatColor.RED + "Shopping carts are not enabled in this world.");
									} else player.sendMessage(ChatColor.RED + "Shipping is not enabled on this server.");
								}
							}
						} else if(player.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType() == Material.BROWN_MUSHROOM){
							if(Config.enableSelling){
								if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
				    				Inventory tempInv = Bukkit.createInventory(null, 36, "Sell to store");
									player.openInventory(tempInv);
								}
							} else player.sendMessage(ChatColor.RED + LangPack.SELLINGTOSTORESISNOTENABLEDONTHISSERVER);
						}
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
}
