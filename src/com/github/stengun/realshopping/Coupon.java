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
import com.github.kuben.realshopping.Price;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Discount coupon object.
 * This object can give discounts to players when trading.
 * @author Roberto Benfatto
 */
public class Coupon {
    private final String store;
    private DiscountType type_discount; // TODO enum
    private DiscountAmountType type_amount; // Percent, fixed
    private Double discount_amount;
    private Integer num_items;
    private Price item;
    
    public Coupon(String store) {
        this.store = store;
    }
    
    public ItemStack buildItemCoupon() {
        List<String> lore = new ArrayList<>();
        lore.add(LangPack.STORE + ": " + store);
        lore.add(LangPack.TYPE + ": " + type_discount);
        String symbol = LangPack.UNIT;
        if(type_amount == DiscountAmountType.PERCENT && discount_amount > 100.) {
            discount_amount = 100.;
            symbol = "%";
        }
        lore.add(LangPack.DISCOUNTAMOUNT + ": " + discount_amount + symbol);
        //TODO add next
        ItemStack itm =  new ItemStack(Material.PAPER);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.PAPER);
        meta.setLore(lore);
        meta.setDisplayName(LangPack.COUPONNAME);
        itm.setItemMeta(meta);
        return itm;
    }
    
    public String getStore() {
        return store;
    }

    public DiscountType getType_discount() {
        return type_discount;
    }

    public void setType_discount(DiscountType type_discount) {
        this.type_discount = type_discount;
    }

    public DiscountAmountType getType_amount() {
        return type_amount;
    }

    public void setType_amount(DiscountAmountType type_amount) {
        this.type_amount = type_amount;
    }

    public Double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(Double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public Integer getNum_items() {
        return num_items;
    }

    public void setNum_items(Integer num_items) {
        this.num_items = num_items;
    }

    public Price getItem() {
        return item;
    }

    public void setItem(Price item) {
        this.item = item;
    }
    // ------- static
    
    public static Coupon itemToCoupon(ItemStack itm) {
        ItemMeta meta = itm.getItemMeta();
        if(meta.hasDisplayName() && meta.getDisplayName().equals(LangPack.COUPONNAME) && meta.hasLore()) {
            return loreToCoupon(meta.getLore());
        }
        return null;
    }
    
    private static Coupon loreToCoupon(List<String> lore) {
        return null;
    }
}
