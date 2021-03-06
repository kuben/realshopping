/*
 * RealShopping Bukkit plugin for Minecraft
 * Copyright 2013 Jakub Fojt
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
package com.github.kuben.realshopping.listeners;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RSPlayerInventory;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RSListenerException;
import com.github.kuben.realshopping.exceptions.RSListenerException.Type;
import com.github.kuben.realshopping.prompts.PromptMaster;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RSPlayerListener implements Listener {

    private static Set<GeneralListener> listenerSet = new HashSet<>();
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onConnect(PlayerJoinEvent event) {
        String player = event.getPlayer().getName();
        if(RealShopping.hasPInv(player)) {
            Shop.addPager(player);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDisconnect(PlayerQuitEvent event) {
        String player = event.getPlayer().getName();
        if(RealShopping.hasPInv(player)) {
            Shop.removePager(player);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (PromptMaster.isConversing(player) || hasConversationListener(player)) {
            event.setCancelled(true);
            player.sendRawMessage(ChatColor.DARK_PURPLE + "[RealShopping] "
                    + ChatColor.RED + LangPack.YOU_CANT_TELEPORT_WHILE_IN_A_CONVERSATION);
        } else if (RealShopping.hasPInv(player) && event.getCause() != TeleportCause.UNKNOWN) {
            if (!RealShopping.getPInv(player).hasPaid()) {
                event.setCancelled(true);
                RSUtils.punish(player);
            } else {
                if (RSUtils.allowTpOutOfStore(event.getTo())) {
                    String shopName = RealShopping.getPInv(player).getShop().getName();
                    RealShopping.removePInv(player);
                    player.sendMessage(ChatColor.RED + LangPack.YOULEFT + shopName);
                } else {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + LangPack.YOUARENTALLOWEDTOTELEPORTTHERE);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDropItem(PlayerDropItemEvent event) {
        if (Config.isDisableDrop()) {
            if (RealShopping.hasPInv(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + LangPack.YOUCANNOTDROPITEMS_);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketEvent(PlayerBucketEmptyEvent event) {
        if (Config.isDisableBuckets()) {
            if (RealShopping.hasPInv(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + LangPack.YOUCANNOTEMPTYBUCKETS_);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPreCraft(CraftItemEvent event) {
        if (Config.isDisableCrafting()) {
            if (RealShopping.hasPInv((Player) event.getWhoClicked())) {
                event.setCancelled(true);
                Bukkit.getPlayerExact(event.getWhoClicked().getName()).sendMessage(ChatColor.RED + LangPack.YOUCANNOTCRAFTITEMS_);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (RealShopping.hasPInv(player)) {
            RSPlayerInventory pinv = RealShopping.getPInv(player);
            Shop shop = pinv.getShop();
            if (!pinv.hasPaid()) {
                List<ItemStack> newdrops = new LinkedList<>();
                for (ItemStack itm : event.getDrops()) {
                    if (itm == null) {
                        continue;
                    }
                    Price p = new Price(itm);
                    int amount = itm.getAmount();
                    if (shop.hasPrice(p)) {
                        Map<Price, Integer> contents = pinv.getItems();
                        if (contents.containsKey(p)) {
                            int haveamount = contents.get(p);
                            if (haveamount < amount) {

                                itm.setAmount(haveamount);
                                newdrops.add(itm);
                                ItemStack stole = new ItemStack(itm);
                                stole.setAmount(amount - haveamount);
                                shop.addToClaim(stole);
                                player.getInventory().remove(stole);
                            }
                        } else {
                            shop.addToClaim(itm);
                            pinv.removeItem(itm, itm.getAmount());
                            player.getInventory().remove(itm);
                        }
                    } else {
                        newdrops.add(itm);
                    }
                }
                event.getDrops().clear();
                event.getDrops().addAll(newdrops);
            }
            player.teleport(shop.getFirstE());
            Shop.exit(player, false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (launchConversationListener(player, event)) return;//Redirects event if player is in conversation, otherwise as usual
        if (RealShopping.isJailed(player.getName())) {
            event.setCancelled(true);
            return;
        }
        if (RealShopping.hasPInv(player) && event.getItem() != null && RealShopping.isForbiddenInStore(event.getItem().getType())) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                player.sendMessage(ChatColor.RED + LangPack.YOUCANTUSETHATITEMINSTORE);
                event.setUseItemInHand(Result.DENY);
                if (event.getItem().getTypeId() == 401 || event.getItem().getTypeId() == 385 || event.getItem().getTypeId() == 383) {//Ugly solution, doesn't work for book and quill
                    event.setCancelled(true);
                }
            }
        }
        
        if (event.hasBlock()) {
            Block b = event.getClickedBlock();
            // We are clicking on a possible entrance? (a glass block or a door/iron door if doors are enabled)
            if (   event.getAction() == Action.RIGHT_CLICK_BLOCK
                && ( b.getType() == Material.GLASS 
                     ||  b.getType() == Material.THIN_GLASS 
                     ||( Config.isEnableDoors() 
                         && ( b.getType() == Material.WOODEN_DOOR || b.getType() == Material.IRON_DOOR_BLOCK ))))
            {
                event.setCancelled(true);
                if (RealShopping.hasPInv(player)) {
                    if (player.hasPermission("realshopping.rsexit")) {
                        Shop.exit(player, false);
                        return;
                    }
                }
                if (player.hasPermission("realshopping.rsenter")) {
                    Shop.enter(player, false);
                    return;
                }
            }
            // We are clicking on an obsidian block while inside a store?
            // Take action depending on the block above the obsidian one.
            if(b.getType() == Material.OBSIDIAN && RealShopping.hasPInv(player)) {
                switch(player.getWorld().getBlockAt(b.getLocation().add(0, 1, 0)).getType()) {
                    case STEP: // Cash block
                        Inventory[] carts = null;
                        if (Config.isAllowFillChests() && Config.isCartEnabledW(player.getWorld().getName())) {
                            StorageMinecart[] SM = RSUtils.checkForCarts(event.getClickedBlock().getLocation());
                            carts = new Inventory[SM.length];
                            for (int i = 0; i < SM.length; i++) {
                                carts[i] = SM[i].getInventory();
                            }
                        }
                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.hasPermission("realshopping.rspay")) {
                            event.setCancelled(Shop.pay(player, carts));
                            return;
                        } 
                        if (event.getAction() == Action.LEFT_CLICK_BLOCK && player.hasPermission("realshopping.rscost")) {
                            event.setCancelled(true);
                            player.sendMessage(LangPack.YOURARTICLESCOST + RealShopping.getPInv(player).toPay(carts) / 100f + LangPack.UNIT);
                        }
                        return;
                    case RED_MUSHROOM: // Shipment block
                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.hasPermission("realshopping.rspay")) {
                            if (Config.isAllowFillChests()) {
                                if (Config.isCartEnabledW(player.getWorld().getName())) {
                                    for (StorageMinecart o : player.getWorld().getEntitiesByClass(StorageMinecart.class)) {
                                        if ((o.getLocation().getBlock().getLocation().equals(b.getLocation().subtract(0, 1, 0)))) {
                                            event.setCancelled(RSUtils.shipCartContents(o, player));
                                            break;
                                        }
                                    }
                                } else player.sendMessage(ChatColor.RED + LangPack.SHOPPINGCARTSARENOTENABLED_);
                                return;
                            }
                            player.sendMessage(ChatColor.RED + LangPack.SHIPPINGISNOTENABLED_);
                            return;
                        }
                        return;
                    case BROWN_MUSHROOM: // Sell to store block
                        if (Config.isEnableSelling()) {
                            if (RealShopping.getPInv(player).getShop().getBuyFor() > 0) {
                                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                    player.sendMessage("I will pay " + Shop.sellPrice(RealShopping.getPInv(player).getShop(), player.getItemInHand()) + "for that item.");
                                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                    Inventory tempInv = Bukkit.createInventory(null, 36, LangPack.SELLTOSTORE);
                                    player.openInventory(tempInv);
                                }
                            } else player.sendMessage(ChatColor.RED + LangPack.NOTBUYINGFROMPLAYERS);
                            return;
                        }
                        player.sendMessage(ChatColor.RED + LangPack.SELLINGTOSTORESISNOTENABLEDONTHISSERVER);
                    default:
                        return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked() instanceof ItemFrame) {
            if (RealShopping.hasPInv(player) && player.hasPermission("realshopping.rsprices") && ((ItemFrame) event.getRightClicked()).getItem().getType() == Material.PAPER) {
                event.setCancelled(true);
                Shop.getPager(player.getName()).push();
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        if (Config.isDisableEnderchests()) {
            if (RealShopping.hasPInv((Player) event.getPlayer()) && event.getInventory().getType() == InventoryType.ENDER_CHEST) {
                event.setCancelled(true);
                ((CommandSender) event.getPlayer()).sendMessage(ChatColor.RED + LangPack.YOUCANNOTOPENENDERCHESTS_);
                return;
            }
        }
        if (!RealShopping.hasPInv((Player) event.getPlayer())) {
            boolean canceled = false;
            if (event.getInventory().getHolder() instanceof Chest) {
                //If player is not in store
                if (RSUtils.isChestProtected(((Chest) event.getInventory().getHolder()).getLocation())) {
                    canceled = true;
                }
            } else if (event.getInventory().getHolder() instanceof DoubleChest) {
                if (RSUtils.isChestProtected(((Chest) ((DoubleChest) event.getInventory().getHolder()).getLeftSide()).getLocation())
                        | RSUtils.isChestProtected(((Chest) ((DoubleChest) event.getInventory().getHolder()).getRightSide()).getLocation())) {
                    canceled = true;
                }
            }
            if(canceled){
                event.setCancelled(canceled);
                event.getInventory().getHolder().getInventory().getViewers().remove(event.getPlayer());
                ((CommandSender) event.getPlayer()).sendMessage(ChatColor.RED + "[RealShopping] " + LangPack.THISCHESTISPROTECTED);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Config.getAutoUpdate() == 2 || Config.getAutoUpdate() == 4) {
            if (!RealShopping.newUpdate.equals("")) {
                if (player.isOp() || player.hasPermission("realshopping.rsupdate")) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + RealShopping.newUpdate);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRedstoneBlockEvent(BlockRedstoneEvent event) {
        Block b = event.getBlock();
        if (Config.isEnableDoors() && (b.getType() == Material.WOODEN_DOOR || b.getType() == Material.IRON_DOOR_BLOCK)) {
            if (!canOpenDoor(b.getLocation())) {
                event.setNewCurrent(0);
            }
        }
    }

    private boolean canOpenDoor(Location loc) {
        Location l = loc.getBlock().getLocation();
        Location l2 = loc.getBlock().getLocation().clone().subtract(0, 1, 0);
        for(Shop shop : RealShopping.getShops()) {
            if(shop.hasEntrance(l) || shop.hasExit(l) || shop.hasEntrance(l2) || shop.hasExit(l2)){
                return false;
            }
        }
        return true;//Safe to open door
    }

    public static boolean hasConversationListener(Player p) {//For every method which wants a player to exit a store
        for (GeneralListener l : listenerSet) {
            if (l.getPlayer() == p) {
                return true;
            }
        }
        return false;
    }

    public static boolean killConversationListener(Player p) {
        for (GeneralListener l : listenerSet) {
            if (l.getPlayer() == p) {
                listenerSet.remove(l);
                return true;
            }
        }
        return false;
    }

    static boolean killConversationListener(GeneralListener l) {//aka kill yourself
        if (listenerSet.contains(l)) {
            listenerSet.remove(l);
            return true;
        }
        return false;
    }

    public static int finishConversationListener(Player p) {
        for (GeneralListener l : listenerSet) {
            if (l.getPlayer() == p) {
                if (l instanceof Appliable) {
                    return ((Appliable) l).apply();
                } else {
                    return -2;
                }
            }
        }
        return -1;
    }

    public static Object sendSignalToConversationListener(Player p, Object sig) throws RSListenerException {
        for (GeneralListener l : listenerSet) {
            if (l.getPlayer() == p) {
                if (l instanceof SignalReceiver) {
                    return ((SignalReceiver) l).receiveSignal(sig);
                } else {
                    throw new RSListenerException(p, Type.LISTENER_MISMATCH);
                }
            }
        }
        return false;
    }

    static boolean addConversationListener(GeneralListener l) {
        if (!hasConversationListener(l.getPlayer())) {
            listenerSet.add(l);
            return true;
        }
        return false;
    }

    private boolean launchConversationListener(Player p, Event event) {
        for (GeneralListener l : listenerSet) {
            if (l.getPlayer() == p) {
                l.onEvent(event);
                return true;
            }
        }
        return false;//Did not find 
    }
}
