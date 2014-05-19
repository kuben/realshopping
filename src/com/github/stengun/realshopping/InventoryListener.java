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

package com.github.stengun.realshopping;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;
import com.github.stengun.realshopping.events.OptionClickedEvent;
import com.github.stengun.realshopping.events.PayInventoryOpen;
import com.github.stengun.realshopping.events.SellInventoryOpen;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

/**
 * This class will listen for all Inventory events for Store specific invs.
 * @author stengun
 */
public class InventoryListener implements Listener{
    private final Map<String,SellInventory> Sellinv_map;
    private final Map<String,PayInventory> Payinv_map;
    
    public InventoryListener() {
        super();
        this.Sellinv_map = new HashMap<>();
        this.Payinv_map = new HashMap<>();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPayInventoryOpen(PayInventoryOpen event) {
        Player p = event.getSender();
        Inventory inv = Bukkit.createInventory(null, 27, "PayInventory");
        PayInventory payinv = new PayInventory(p, inv, event.getCarts());
        Bukkit.getServer().getPluginManager().registerEvents(payinv, Bukkit.getServer().getPluginManager().getPlugin("RealShopping"));
        Payinv_map.put(p.getName(), payinv);
        payinv.update();
        p.openInventory(inv);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSellInventoryOpen(SellInventoryOpen event) {
        Player p = event.getSender();
        Inventory inv = Bukkit.createInventory(null, 27, LangPack.SELLTOSTORE);
        SellInventory sellinv = new SellInventory(p, inv);
        Bukkit.getServer().getPluginManager().registerEvents(sellinv, Bukkit.getServer().getPluginManager().getPlugin("RealShopping"));
        Sellinv_map.put(p.getName(), sellinv);
        sellinv.update();
        p.openInventory(inv);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if(event.getInventory().getTitle().equals(LangPack.SELLTOSTORE)) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (event.getInventory().getTitle().equals("PayInventory")) {
            PayInventory payinv = Payinv_map.get(p.getName());
            if(payinv.isAnOption(event.getRawSlot())) {
                cancelAndFireOptionEvent(event);
            } else if(event.getRawSlot() > 26 || event.getCursor().getAmount() == 0 || payinv.isEligible(event.getCursor())) {
                payinv.update();
            } else {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
            return;            
        }
        if (event.getInventory().getTitle().equals(LangPack.SELLTOSTORE)) {
            if(event.isShiftClick()) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }
            SellInventory sellinv = Sellinv_map.get(event.getWhoClicked().getName());
            if(sellinv.isAnOption(event.getRawSlot())) {
                cancelAndFireOptionEvent(event);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if(event.getInventory().getTitle().equals("PayInventory") && RealShopping.hasPInv(player)) {
            HandlerList.unregisterAll(Payinv_map.get(player.getName()));
            Payinv_map.remove(player.getName());
            return;
        }
        if(event.getInventory().getTitle().equals(LangPack.SELLTOSTORE) && RealShopping.hasPInv(player)) {
            HandlerList.unregisterAll(Sellinv_map.get(player.getName()));
            Sellinv_map.remove(player.getName());
            return;
        }
    }
    
    // ------------ PRIVS
    private <T extends InventoryClickEvent & Cancellable> void cancelAndFireOptionEvent(T event) {
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
        if(event.getCursor() == null || event.getCursor().getType() == Material.AIR){
            OptionClickedEvent optclick = new OptionClickedEvent(event.getRawSlot(), event.getCursor(), (Player) event.getWhoClicked());
            Bukkit.getServer().getPluginManager().callEvent(optclick);
        }
    }
}
