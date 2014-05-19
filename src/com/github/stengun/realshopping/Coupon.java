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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
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
    private DiscountType type_discount;
    private DiscountAmountType type_amount;
    private Double discount_amount;
    private Integer num_items;
    private final Long itemhash;
    
    public Coupon(String store, DiscountType discount_type, DiscountAmountType amount_type, Double discount_amount, Integer quantity, Long itemhash) {
        this.store = store;
        this.discount_amount = discount_amount;
        this.type_amount = amount_type;
        this.type_discount = discount_type;
        this.num_items = quantity;
        this.itemhash = itemhash;
    }
    
    public ItemStack buildItemCoupon() {
        List<String> lore = new ArrayList<>();
        lore.add(LangPack.STORE + ": " + store);
        lore.add(LangPack.TYPE + ": " + type_discount.easyName());
        String symbol = LangPack.UNIT;
        if(type_amount == DiscountAmountType.PERCENT) {
            if(discount_amount > 100.) discount_amount = 100.;
            if(discount_amount < 0.) discount_amount = 0.;
            symbol = "%";
        }
        lore.add(LangPack.DISCOUNTAMOUNT + ": " + discount_amount + symbol);
        lore.add("Fingerprint:");
        lore.add("§k;" + type_discount + (num_items != null && num_items > 0 ? ";" + num_items:"") + (itemhash != null ? ";" + itemhash:"") + ";§r");
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

    public Long getItemhash() {
        return itemhash;
    }
    // ------- static
    
    public static Double calculateTotalDiscount(List<Coupon> coupons, Double fullprice) {
        PriorityQueue<Double> percentlist = new PriorityQueue(27, Collections.reverseOrder());
        double discount_percent = 0.;
        double discount_fixed = 0.;
        if(coupons != null && coupons.size() > 0) {
            for(Coupon coup: coupons) {
                if(coup.getType_discount() != DiscountType.ALLITEMS) continue;
                switch(coup.getType_amount()) {
                    case FIXED:
                        discount_fixed += coup.getDiscount_amount();
                        break;
                    case PERCENT:
                        percentlist.offer(coup.getDiscount_amount());
                        break;
                }
            }
            if(!percentlist.isEmpty()){
                discount_percent = percentlist.poll();
                Iterator<Double> perciter = percentlist.iterator();
                while(perciter.hasNext()) {
                    discount_percent += (100. - discount_percent)*perciter.next()/100d;
                }
                if(discount_percent > 100d) discount_percent = 100d;
            }
        }
        discount_fixed = (fullprice > discount_fixed ? discount_fixed : fullprice);
        return (discount_fixed + (fullprice*discount_percent/100d));
    }
    
    public static Coupon itemToCoupon(ItemStack itm) {
        ItemMeta meta = itm.getItemMeta();
        if(meta.hasDisplayName() && meta.getDisplayName().equals(LangPack.COUPONNAME) && meta.hasLore()) {
            return loreToCoupon(meta.getLore());
        }
        return null;
    }
    
    private static Coupon loreToCoupon(List<String> lore) {
        Coupon retval;
        String store = lore.get(0).split(": ")[1];
        if(RealShopping.getShop(store) == null) return null;
        String[] fingerprint = lore.get(lore.size()-1).split(";");
        String parsd = lore.get(2).split(": ")[1];
        DiscountAmountType amounttype = (parsd.contains("%") ? DiscountAmountType.PERCENT: DiscountAmountType.FIXED);
        parsd = parsd.replace("%", "");
        parsd = parsd.replace(LangPack.UNIT, "");
        Double discamount = Double.parseDouble(parsd);
        Integer numitems = null;
        DiscountType disctype = null;
        Long itemhash = null;
        switch(fingerprint.length) {
            case 5:
                itemhash = Long.parseLong(fingerprint[3]);
            case 4:
                numitems = Integer.parseInt(fingerprint[2]);
            default:
                if(fingerprint.length > 2) disctype = DiscountType.valueOf(fingerprint[1]);
                break;
        }
        retval = new Coupon(store, disctype, amounttype, discamount, numitems, itemhash);
        return retval;
    }
}
