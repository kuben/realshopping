/*
 * RealShopping Bukkit plugin for Minecraft
 * Copyright 2013 Jakub Fojt, Copyright 2014 Roberto Benfatto
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
import com.github.kuben.realshopping.Shop;
import com.github.stengun.realshopping.events.OptionClickedEvent;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A custom class for Sell to store inventories.
 * This class works as a "wrapper" of Inventory, providing item price visualization in its Lore lines.
 This will facilitate the reading of the sell PrintPrices while selling items to a shop.
 * @author stengun
 */
public class SellInventory extends InventoryManager{

    private final Shop shop;
    private final Player p;
    public SellInventory(Player p, Inventory inventory) {
        super(inventory);
        this.shop = RealShopping.getPInv(p).getShop();
        this.p = p;
        this.addOption(8, LangPack.BTN_SELLINFO, Material.ITEM_FRAME);
        this.addOption(17, LangPack.BTN_CONFIRM, DyeColor.GREEN);
        this.addOption(26, LangPack.BTN_CANCEL, DyeColor.RED);
    }
    
    /**
     * Updates the content of this inventory given the "real inventory" (the one
     * that's hidden inside this class).
     */
    @Override
    protected void update() {
        double stackprice = 0.;
        for(int i = 0; i < contents.getSize(); i++) {
            if(this.isAnOption(i) || contents.getItem(i) == null) continue;
            ItemStack itm = contents.getItem(i);
            stackprice += Shop.sellPrice(shop, itm);
        }
        List<String> lore = new ArrayList<>();
        lore.add("Total sell price: "+stackprice);
        updateOption(8, null, lore);
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR)
    public void onOptionClicked(OptionClickedEvent event) {
        switch(event.getIndex()) {
            case 8:
                update();
                break;
            case 17:
                List<ItemStack> sellitms = getContentsList();
                List<ItemStack> unsold = Shop.sellToStore(p, sellitms);
                p.getInventory().addItem(unsold.toArray(new ItemStack[0]));
                RealShopping.getPInv(p).addItems(unsold);
                p.updateInventory();
            case 26:
                p.closeInventory();
            default:
                break;
        }
    }
}
