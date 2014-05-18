/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.stengun.realshopping.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Roberto Benfatto
 */
public final class OptionClickedEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final int index;
    private final ItemStack cursor;
    private final Player sender;
    public OptionClickedEvent(int index, ItemStack cursor, Player sender) {
        this.index = index;
        this.cursor = cursor;
        this.sender = sender;
    }

    public ItemStack getCursor() {
        return cursor;
    }

    public Player getSender() {
        return sender;
    }
 
    public int getIndex() {
        return index;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
