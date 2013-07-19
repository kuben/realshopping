/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuben.realshopping;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;

final class Statistic {
    final private Price item;
    final private int amount;
    final private long timestamp;
    final private boolean bought;

    public Statistic(Price item, int amount, boolean bought){
            this.item = item;
            this.amount = amount;
            this.timestamp = System.currentTimeMillis();
            this.bought = bought;
    }

    public Statistic(String imp){
            this.timestamp = Long.parseLong(imp.split("\\[")[0].split(":")[0]);
            this.bought = Boolean.parseBoolean(imp.split("\\[")[0].split(":")[1]);
            Byte data = 0;
            Map<Enchantment, Integer> enchs = new HashMap<>();
            if(imp.split("\\[")[0].split(":").length > 4) data = Byte.parseByte(imp.split("\\[")[0].split(":")[4]);
            for(int i = 1;i < imp.split("\\[").length;i++){
                    enchs.put(Enchantment.getById(Integer.parseInt(imp.split("\\[")[i].split("\\]")[0].split(":")[0])), Integer.parseInt(imp.split("\\[")[i].split("\\]")[0].split(":")[1]));
            }
            this.item = new Price(Integer.parseInt(imp.split(":")[2]), data);
            this.amount = Integer.parseInt(imp.split("\\[")[0].split(":")[3]);
    }

    public Price getItem() {
            return item;
    }

    public int getAmount() {
            return amount;
    }

    public long getTime() {
            return timestamp;
    }

    public boolean isBought() {
            return bought;
    }

    public String toString(){
            return (bought?"bought ":"sold ") + item.toString() + " x" + amount;
    }
}