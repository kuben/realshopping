package com.github.kuben.realshopping;

import org.bukkit.material.MaterialData;

final class Statistic {
    final private MaterialData item;
    final private int amount;
    final private long timestamp;
    final private boolean bought;
    
    public Statistic(MaterialData mat, int soldamount, boolean bought) {
        this(mat.getItemTypeId(), mat.getData(), soldamount, bought);
    }
    
    public Statistic(int id, byte data, int soldamount, boolean bought){
        this.item = new MaterialData(id, data);
        this.amount = soldamount;
        this.timestamp = System.currentTimeMillis();
        this.bought = bought;
    }

    public MaterialData getMaterialData() {
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
    
@Override
    public String toString(){
        return (bought?"bought ":"sold ") + item.getItemType().toString() + " x" + amount;
    }
}