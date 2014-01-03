package com.github.kuben.realshopping;

final class Statistic {
    final private Price item;
    final private int amount;
    final private long timestamp;
    final private boolean bought;

    public Statistic(Price item, int soldamount, boolean bought){
        this.item = item;
        this.amount = soldamount;
        this.timestamp = System.currentTimeMillis();
        this.bought = bought;
    }
/**
 * Rebuilding statistic from a string.
 * The format of the string must be
 * TIMESTAMP:BOUGHT:ID:DATA[:DESCRIPTION]:AMOUNT
 * @param imp
 * @deprecated Use the YAML importer for that.
 */
    public Statistic(String imp){
        String[] s = imp.split(":");
        this.timestamp = Long.parseLong(s[0]);
        this.bought = Boolean.parseBoolean(s[1]);
        this.item = new Price(Integer.parseInt(s[2]), Byte.parseByte(s[3]), Integer.parseInt(s[4]));
        if(s.length > 6) item.setDescription(s[5]);
        this.amount = Integer.parseInt(s[s.length -1]);
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
    
@Override
    public String toString(){
            return (bought?"bought ":"sold ") + item.getEasyname() + " x" + amount;
    }
}