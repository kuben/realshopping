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
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RSPlayerInventory;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.stengun.realshopping.events.OptionClickedEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author stengun
 */
public class PayInventory extends InventoryManager{
    Collection<Inventory> carts;
    Player p;
    public PayInventory(Player p, Inventory inventory, Collection<Inventory> carts) {
        super(inventory);
        addOption(8, LangPack.BTN_PAYMENTINFO, Material.ITEM_FRAME);
        addOption(17, LangPack.BTN_CONFIRM, DyeColor.GREEN);
        addOption(26, LangPack.BTN_CANCEL, DyeColor.RED);
        this.carts = carts;
        this.p = p;
    }
    

    public boolean isEligible(ItemStack itm) {
        Coupon coup = Coupon.itemToCoupon(itm);
        if(coup != null) return true;
        Shop shop = RealShopping.getPInv(p).getShop();
        return shop.hasPrice(new Price(itm));
    }

    @Override
    public void update() {
        RSPlayerInventory pinv = RealShopping.getPInv(p);
        double totalcost = pinv.toPay(carts.toArray(new Inventory[0]), pinv.getShop())/100d;
        List<String> lore = new ArrayList<>();
        lore.add("Total cost of chosen items: " + totalcost);
        // TODO add discount coupon indicator in lore
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
                List<Coupon> couplist = new ArrayList<>();
                for(ItemStack it : unsold.toArray(new ItemStack[0])) {
                    Coupon coup = Coupon.itemToCoupon(it);
                    if(coup != null) {
                        unsold.remove(it);
                        couplist.add(coup);
                    }
                }
                p.getInventory().addItem(unsold.toArray(new ItemStack[0]));
                Shop.pay(p, carts.toArray(new Inventory[0]), couplist);
            case 26:
                p.closeInventory();
            default:
                break;
        }
        //event.getSender().sendMessage("Hai cliccato " + options.get(event.getIndex()).getItemMeta().getDisplayName());
        
    }
}
