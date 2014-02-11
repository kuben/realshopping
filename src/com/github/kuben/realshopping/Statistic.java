package com.github.kuben.realshopping;

import org.bukkit.Material;

public final class Statistic {
    final private Material item;
    final private int amount;
    final private long timestamp;
    final private boolean bought;
    final private String name;
    
    public Statistic(Material mat, String name, int soldamount, boolean bought) {
        this.item = mat;
        this.amount = soldamount;
        this.bought = bought;
        this.timestamp = System.currentTimeMillis();
        this.name = name;
    }

    public Material getItem() {
        return item;
    }
    
    public String getName() {
        return name;
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
    
    @Override
    public String toString(){
        return (bought?"bought ":"sold ") + (name.isEmpty()?item.toString(): name) + " x" + amount;
    }
}