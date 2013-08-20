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
package com.github.kuben.realshopping;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RSPlayerInventory {

    private Map<Price, Integer> items, bought;
    private String store;//TODO switch to Shop
    private String player;//String because should involve offline players too

    public RSPlayerInventory(Player player, String store) {//Use when player is entering store
        this.store = store;
        this.player = player.getName();
        //Special Inv to PInv
        ItemStack[] IS = (ItemStack[]) ArrayUtils.addAll(player.getInventory().getContents(), player.getInventory().getArmorContents());
        items = invToPInv(IS);//Item - amount/dur
        bought = new HashMap<>();
    }

    public RSPlayerInventory(String player, String store, Map<Price, Integer> bought, Map<Price, Integer> items) {
        this.player = player;
        this.store = store;
        this.bought = bought;
        this.items = items;
    }

    @Deprecated
    public RSPlayerInventory(String importStr) {//Use to recover a PlayerInventory from string
        items = new HashMap<>();//Item - amount/dur
        bought = new HashMap<>();
        this.store = importStr.split(";")[0].split("-")[1];
        this.player = importStr.split(";")[0].split("-")[0];
        createInv(importStr.split(";")[1]);
    }

    public boolean update() {
        items.clear();
        items = invToPInv();
        return true;
    }

    public boolean update(Inventory[] invs) {
        items.clear();
        items = invToPInv();

        if (invs != null) {
            for (Inventory i : invs) {
                items = RSUtils.joinMaps(items, invToPInv(i));
            }
        }
        return true;
    }

    public boolean hasPaid() {
        Map<Price, Integer> newInv = invToPInv();

        //Old inv = items + bought.
        Map<Price, Integer> contents = getItems();
        Shop tempshop = RealShopping.shopMap.get(store);
        if (!tempshop.hasPrices()) {
            return true;
        }

        for (Price key : newInv.keySet()) {
            if (tempshop.hasPrice(key)) {
                if (contents.containsKey(key) && contents.get(key) >= newInv.get(key)) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    public int toPay() {
        return toPay(null);
    }

    public int toPay(Inventory[] invs) {
        return toPay(invs, store);
    }

    public int toPay(Inventory[] invs, String store) {
        int toPay = 0;
        Shop tempShop = RealShopping.shopMap.get(store);
        if (tempShop.hasPrices()) {//If shop has prices
            Map<Price, Integer> newInv = invToPInv();

            //Old inv = items

            if (invs != null) {
                for (int i = 0; i < invs.length; i++) {
                    Map<Price, Integer> tempInv = invToPInv(invs[i]);
                    newInv = RSUtils.joinMaps(newInv, tempInv);
                }
            }
            Map<Price, Integer> contents = getItems();

            for (Price key : newInv.keySet()) {
                if (tempShop.hasPrice(key)) {//Something in inventory has a price
                    int amount = newInv.get(key);
                    int cost = tempShop.getPrice(key);
                    int pcnt = 100 - tempShop.getSale(key);
                    cost *= pcnt / 100f;
                    if (contents.containsKey(key)) {
                        int oldAm = contents.get(key);
                        if (oldAm > amount) {//More items before than now
                            amount = 0;
                        } else {//More items now
                            amount -= oldAm;
                        }
                    }
                    toPay += cost * (RealShopping.isTool(key.getType()) ? (double) amount / (double) RealShopping.getMaxDur(key.getType()) : amount);//Convert items durability to item amount
                }
            }
        }

        return toPay;
    }

    public Map<Price, Integer> getItems() {
        return RSUtils.joinMaps(items, bought);
    }

    public void addBought(Price p, Integer amount) {
        int iam = 0;
        if (bought.containsKey(p)) {
            iam = bought.get(p);
        }
        bought.put(p, amount + iam);
    }

    public void delBought(Price p) {
        bought.remove(p);
    }

    public void resetBought() {
        bought = new HashMap<>();
    }

    /**
     * Gives Bought items since the player entered the shop.
     *
     * @return A map with Item - Amount
     */
    public Map<Price, Integer> getBought() {
        return bought;
    }

    /**
     * Gets bought items that waits to be paid.
     *
     * @param invs
     * @return hashmap containing all items that waits to be bought.
     */
    public Map<Price, Integer> getBoughtWait(Inventory[] invs) {
        Shop tempShop = RealShopping.shopMap.get(store);
        Map<Price, Integer> surplus = new HashMap<>();
        if (tempShop.hasPrices()) {//If shop has prices
            Map<Price, Integer> newInv = invToPInv();
            Map<Price, Integer> oldInv = RSUtils.joinMaps(bought, items);

            //Old inv = items

            if (invs != null) {
                for (int i = 0; i < invs.length; i++) {
                    Map<Price, Integer> tempInv = invToPInv(invs[i]);
                    newInv = RSUtils.joinMaps(newInv, tempInv);
                }
            }
            for (Price key : newInv.keySet()) {
                if (tempShop.hasPrice(key)) {
                    int amount = newInv.get(key);
                    if (oldInv.containsKey(key)) {//Something in inventory has a price
                        int oldAm = oldInv.get(key);
                        if (oldAm < amount) {//More items than before
                            amount -= oldAm;
                        }
                    }
                    if (surplus.containsKey(key)) {
                        surplus.put(key, amount + surplus.get(key));
                    } else {
                        surplus.put(key, amount);
                    }
                }
            }
        }

        return surplus;
    }

    public Map<Price, Integer> getStolen() {
        //Get stolen items
        //Old inv = items

        Map<Price, Integer> newInv = invToPInv();
        Map<Price, Integer> stolen = new HashMap<>();

        for (Price key : newInv.keySet()) {
            if (RealShopping.shopMap.get(store).hasPrice(key)) {//Something in inventory has a price
                int amount = newInv.get(key);
                if (hasItem(key)) {
                    int oldAm = getAmount(key);
                    if (oldAm > amount) {//More items before than now
                        amount = 0;
                    } else {//More items now
                        amount -= oldAm;
                    }
                }
                if (stolen.containsKey(key)) {
                    stolen.put(key, amount + stolen.get(key));
                } else {
                    stolen.put(key, amount);
                }
            }
        }
        return stolen;
    }

    void createInv(String invStr) {
        if (!invStr.equals("")) {
            for (String item : invStr.split(",")) {
                Price temp = new Price(item.split(":")[0]);
                int amount = Integer.parseInt(item.split(":")[1]);
                if (items.containsKey(temp)) {
                    items.put(temp, items.get(temp) + amount);
                } else {
                    items.put(temp, amount);
                }
            }
        }
    }

    private Map<Price, Integer> invToPInv(ItemStack[] IS) {
        Map<Price, Integer> tempMap = new HashMap<>();
        for (ItemStack iS : IS) {
            if (iS != null) {
                Price temp = new Price(iS);
                int amount;
                if (RealShopping.isTool(iS.getTypeId())) {
                    amount = 1;
                } else {
                    amount = iS.getAmount();
                }
                if (tempMap.containsKey(temp)) {
                    tempMap.put(temp, tempMap.get(temp) + amount);
                } else {
                    tempMap.put(temp, amount);
                }
            }
        }
        return tempMap;
    }

    private Map<Price, Integer> invToPInv() {
        //Guess it is safe to use getPlayerExact, because this will only be called with an online player.
        //Will throw NullPointer otherwise
        Object[] IS = ArrayUtils.addAll(Bukkit.getPlayerExact(player).getInventory().getContents(), Bukkit.getPlayerExact(player).getInventory().getArmorContents());
//            ItemStack[] IS = new ItemStack[obj.length];
//
//            for(int i = 0;i < obj.length;i++)
//                    IS[i] = (ItemStack) obj[i];
        return invToPInv((ItemStack[]) IS);
    }

    private Map<Price, Integer> invToPInv(Inventory inv) {
        return invToPInv(inv.getContents());
    }

    public String exportToString() {
        String s = "";
        Object[] keys = items.keySet().toArray();
        for (Object oob : keys) {
            Price pi = (Price) oob;
            if (!s.equals("")) {
                s += ",";
            }
            s += pi.toString(items.get(pi).intValue());
        }
        return player + "-" + store + ";" + s;
    }

    public String getStore() {
        return store;
    }

    public Shop getShop() {
        return RealShopping.shopMap.get(store);
    }

    public String getPlayer() {
        return player;
    }

    public boolean setStore(String store) {
        this.store = store;
        return true;
    }

    public boolean hasItems() {
        return !items.isEmpty();
    }

    public boolean hasItem(ItemStack iS) {
        return items.containsKey(new Price(iS));
    }

    public boolean hasItem(Price pi) {
        return items.containsKey(pi);
    }

    public int getAmount(ItemStack iS) {
        return items.get(new Price(iS));
    }

    public int getAmount(Price pi) {
        return items.get(pi);
    }

    public int removeItem(ItemStack iS, int amount) {
        return removeItem(new Price(iS), amount);
    }

    public int removeItem(Price pi, int amount) {//Returns how many items couldn't be removed, or -1 if item didn't exist
        int retval = -1;

        if (items.containsKey(pi)) {
            if (items.get(pi) > amount) {
                items.put(pi, items.get(pi) - amount);
                retval = 0;
            } else if (items.get(pi) == amount) {
                items.remove(pi);
                retval = 0;
            } else {
                int diff = amount - items.get(pi);
                items.remove(pi);
                retval = diff;
            }
        }
        if (bought.containsKey(pi)) {
            if (bought.get(pi) > amount) {
                bought.put(pi, bought.get(pi) - amount);
                retval = 0;
            } else if (bought.get(pi) == amount) {
                bought.remove(pi);
                retval = 0;
            } else {
                int diff = amount - bought.get(pi);
                bought.remove(pi);
                retval = diff;
            }
        }
        return retval;
    }

    public boolean addItem(ItemStack iS, int amount) {
        Price tempP = new Price(iS);
        if (items.containsKey(tempP)) {
            items.put(tempP, items.get(tempP) + amount);
        } else {
            items.put(tempP, amount);
        }
        return true;
    }

    @Override
    public String toString() {
        return "PInventory Store: " + store + " Items: " + items;
    }
}