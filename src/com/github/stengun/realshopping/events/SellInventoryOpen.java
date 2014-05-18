/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.stengun.realshopping.events;

import com.github.kuben.realshopping.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Roberto Benfatto
 */
public class SellInventoryOpen extends Event{
    private final static HandlerList handlers = new HandlerList();
    private final Shop shop;
    private final Player player;
    
    public SellInventoryOpen(Player p, Shop shop) {
        this.shop = shop;
        this.player = p;
    }
    
    public Player getSender() {
        return player;
    }

    public Shop getShop() {
        return shop;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
}
