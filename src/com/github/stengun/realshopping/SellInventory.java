package com.github.stengun.realshopping;

import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A custom class for Sell to store inventories.
 * This class works as a "wrapper" of Inventory, providing item price visualization in its Lore lines.
 * This will facilitate the reading of the sell prices while selling items to a shop.
 * @author stengun
 */
public class SellInventory {

    private Inventory inventory;
    private ItemStack[] real_contents;
    private Shop shop;

    public SellInventory(Player p, Inventory inventory) {
        this.inventory = inventory;
        this.real_contents = new ItemStack[36];
        this.shop = RealShopping.getPInv(p).getShop();
    }
    
    /**
     * Gets the contents (without lores) of that inventory.
     * @return The actual contents of the inventory.
     */
    public ItemStack[] getContents() {
        return real_contents;
    }
    
    /**
     * Puts an item in this Inventory.
     * @param index Where to put the item.
     * @param item The item we want to insert in the inventory.
     */
    public void setItem(int index, ItemStack item) {
        if(index > real_contents.length || index < 0) return;
        real_contents[index] = item;
        updateContents();
    }
    
    /**
     * Adds Items to the inventory.
     * Thi method returns the items it can't add in the inventory.
     * @param iss Items to add
     * @return If the method can't add more items, the exceeded ones are returned in a map.
     */
    public Map<Integer, ? extends ItemStack> addItem(ItemStack...iss) {
        inventory.setContents(real_contents);
        Map<Integer, ? extends ItemStack> retval = inventory.addItem(iss);
        real_contents = inventory.getContents();
        updateContents();
        return retval;
    }
    
    /**
     * Removes items from this inventory.
     * If this method can't remove items, the not removed ones are returned.
     * @param iss Items to remove.
     * @return A map containing the items that were not removed from the inventory.
     */
    public Map<Integer, ? extends ItemStack> removeItem(ItemStack...iss) {
        inventory.setContents(real_contents);
        Map<Integer, ? extends ItemStack> retval = inventory.removeItem(iss);
        real_contents = inventory.getContents();
        updateContents();
        return retval;
    }
    
    /**
     * Removes an item from a given index.
     * If the index is not valid or there is no item in the given location, this method will
     * return false.
     * @param index Index where to remove the item.
     * @return false if no item was removed, true otherwise.
     */
    public boolean removeItem(int index) {
        if(index > real_contents.length || index < 0 || real_contents[index] == null) return false;
        real_contents[index] = null;
        updateContents();
        return true;
    }
    
    /**
     * Returns given item at given index.
     * Warning: if the item is not valid, this method will return null.
     * @param index Index where to take the item.
     * @return The given item, or null if index is not valid.
     */
    public ItemStack getItem(int index) {
        if(index > real_contents.length || index < 0) return null;
        return real_contents[index];
    }
    
    /**
     * Manually sets the content of this inventory.
     * @param contents Contents of the inventory.
     */
    public void setContents(ItemStack[] contents) {
        this.real_contents = contents;
        updateContents();
    }
    
    
// ---- PRIV
    
    /**
     * Updates the content of this inventory given the "real inventory" (the one
     * that's hidden behind this class).
     */
    private void updateContents() {
        this.inventory.clear();
        for(int i=0;i<real_contents.length;i++) {
            inventory.setItem(i,itemCalc(real_contents[i]));
        }
    }
    
    /**
     * Calculates the "visualize price" lore for given item.
     * If the item hasn't a price, it will be visualized as "Not accepted".
     * The returned item is a clone of the given one.
     * @param itm Item to convert.
     * @return The item converted and ready to be visualized.
     */
    private ItemStack itemCalc(ItemStack itm) {
        if(itm == null) return null;
        ItemStack retval = itm.clone();
        List<String> lore = new ArrayList<>();
        if(!retval.hasItemMeta()) {
            retval.setItemMeta(Bukkit.getItemFactory().getItemMeta(retval.getType()));
        }
        ItemMeta meta = retval.getItemMeta();
        
        if(!shop.hasPrices() || !shop.hasPrice(new Price(itm))){
            lore.add("Shop doesn't accept this.");
        } else {
            String singleprice,stackprice;
            singleprice = String.valueOf(Shop.sellPrice(shop, itm)/itm.getAmount());
            stackprice = String.valueOf(Shop.sellPrice(shop, itm));
            lore.add("Stack Price: "+stackprice);
            lore.add("Single Item Price: "+singleprice);
        }
        meta.setLore(lore);
        
        retval.setItemMeta(meta);
        return retval;
    }
}
