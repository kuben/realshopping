/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.stengun.realshopping.events;

import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Roberto Benfatto
 */
public class PayInventoryOpen extends Event{
    private final static HandlerList handlers = new HandlerList();
    private final Player sender;
    private final Collection<Inventory> carts;
    
    public PayInventoryOpen(Player player, Collection<Inventory> carts) {
        this.sender = player;
        this.carts = carts;
    }

    public Player getSender() {
        return sender;
    }

    public Collection<Inventory> getCarts() {
        return carts;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
}
