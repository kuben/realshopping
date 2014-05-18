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

import com.github.stengun.realshopping.events.OptionClickedEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Roberto Benfatto
 */
public abstract class InventoryManager implements Listener{
    protected final Inventory contents;
    protected final Map<Integer, ItemStack> options;
    public InventoryManager(Inventory inv) {
        this.contents = inv;
        this.options = new HashMap<>();
    }
    
    protected final void addOption(int index, String name, Material material) {
        this.addOption(index, name, material, null);
    }
    
    protected final void addOption(int index, String name, DyeColor color) {
        this.addOption(index, name, Material.WOOL, color);
    }
    
    protected final void updateOption(int index, String name, List<String> lore) {
        ItemStack itm = this.options.get(index);
        if(itm == null) return;
        ItemMeta meta = itm.getItemMeta();
        if(name != null) {
            meta.setDisplayName(name);
        }
        if(lore != null) {
            meta.setLore(lore);
        }
        itm.setItemMeta(meta);
        this.options.put(index, itm);
        this.contents.setItem(index, itm);
    }
    
    private void addOption(int index, String name, Material material, DyeColor color) {
        ItemStack menuitem;
        if(color == null) menuitem = new ItemStack(material, 1);
        else menuitem = new ItemStack(material, 1, color.getData());
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        meta.setDisplayName(name);
        menuitem.setItemMeta(meta);
        contents.setItem(index, menuitem);
        options.put(index, menuitem);
    }
    
    public boolean isAnOption(int index) {
        return options.containsKey(index);
    }
    public void setItem(int index, ItemStack item) {
        if(options.containsKey(index)) return;
        contents.setItem(index, item);
        this.update();
    }
    public Map<Integer, ? extends ItemStack> addItem(ItemStack...iss) {
        HashMap<Integer, ? extends ItemStack> retval = contents.addItem(iss);
        this.update();
        return retval;
    }
    public Map<Integer, ? extends ItemStack> removeItem(ItemStack...iss) {
        HashMap<Integer, ItemStack> retval = contents.removeItem(iss);
        this.update();
        return retval;
    }
    
    public ItemStack getItem(int index) {
        if(options.containsKey(index)) return null;
        return contents.getItem(index);
    }
    public ItemStack[] getContents() {
        ItemStack[] retval = new ItemStack[contents.getContents().length];
        for(int i=0; i<retval.length; i++) {
            if(options.containsKey(i)) retval[i] = null;
            else retval[i] = contents.getItem(i);
        }
        return retval;
    }
    
    protected List<ItemStack> getContentsList() {
        List<ItemStack> istk = new ArrayList<>();
        for(int i = 0; i < contents.getSize(); i++) {
            if(this.isAnOption(i) || contents.getItem(i) == null) continue;
            ItemStack itm = contents.getItem(i);
            istk.add(itm);
        }
        return istk;
    }
    
    public abstract void onOptionClicked(OptionClickedEvent event);
    protected abstract void update();
}