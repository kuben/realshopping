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

    private Map<Price, Integer> items;
    private String store;//TODO switch to Shop
    private String player;//String because should involve offline players too

    public RSPlayerInventory(Player player, String store){//Use when player is entering store
            this.store = store;
            this.player = player.getName();
            //Special Inv to PInv
            Object[] obj = ArrayUtils.addAll(player.getInventory().getContents(), player.getInventory().getArmorContents());
            ItemStack[] IS = new ItemStack[obj.length];

            for(int i = 0;i < obj.length;i++)
                    IS[i] = (ItemStack) obj[i];
            items = invToPInv(IS);//Item - amount/dur
    }

    public RSPlayerInventory(String importStr){//Use to recover a PlayerInventory from string
            items = new HashMap<>();//Item - amount/dur
            this.store = importStr.split(";")[0].split("-")[1];
            this.player = importStr.split(";")[0].split("-")[0];
            createInv(importStr.split(";")[1]);
    }

    public boolean update(){
            items.clear();
            items = invToPInv();
            return true;	
    }

    public boolean update(Inventory[] invs){
		items.clear();
		items = invToPInv();
		
		if(invs != null)
			for(int i = 0;i < invs.length;i++)
				items = RSUtils.joinMaps(items, invToPInv(invs[i]));
		
		return true;	
	}
	
    public boolean hasPaid(){
		Map<Price, Integer> newInv = invToPInv();

		//Old inv = items
		Shop tempshop = RealShopping.shopMap.get(store);
		Object[] keys = newInv.keySet().toArray();
		boolean hasPaid = true;
		if(tempshop.hasPrices())//If there are prices for store.
			for(Object k:keys){
                                Price key = (Price)k;
				if(tempshop.hasPrice(key)) {//If item has price
					hasPaid = items.containsKey(key);
                                        if(hasPaid && newInv.get(key) > items.get(key))
                                            hasPaid = false;
                                }
			}
		return hasPaid;
    }
    
    public int toPay(){
    	return toPay(null);
    }
    
    public int toPay(Inventory[] invs) {
        return toPay(invs,store);
    }
    
    public int toPay(Inventory[] invs, String store){
    	int toPay = 0;
    	Shop tempShop = RealShopping.shopMap.get(store);
        if(tempShop.hasPrices()){//If shop has prices
            Map<Price, Integer> newInv = invToPInv();

            //Old inv = items

            if(invs != null){
                for(int i = 0;i < invs.length;i++){
                    Map<Price, Integer> tempInv = invToPInv(invs[i]);
                    newInv = RSUtils.joinMaps(newInv, tempInv);
                }
            }

            Object[] keys = newInv.keySet().toArray();

            for(Object k:keys){
                Price key = (Price)k;
                if(tempShop.hasPrice(key)) {//Something in inventory has a price
                    int amount = newInv.get(key);
                    int cost = tempShop.getPrice(key);
                    int pcnt = 100 - tempShop.getSale(key);
                    cost *= pcnt/100f;
                    if(items.containsKey(key)) {
                        int oldAm = items.get(key);
                        if(oldAm > amount){//More items before than now
                                amount = 0;
                        } else {//More items now
                                amount -= oldAm;
                        }
                    }
                    toPay += cost * (RealShopping.isTool(key.getType())?(double)amount / (double)RealShopping.getMaxDur(key.getType()):amount);//Convert items durability to item amount
                }
            }
        }

        return toPay;
    }
    
    public Map<Price, Integer> getBought(Inventory[] invs){
    	Map<Price, Integer> bought = new HashMap<>();
    	Shop tempShop = RealShopping.shopMap.get(store);
		if(tempShop.hasPrices()){//If shop has prices
			Map<Price, Integer> newInv = invToPInv();
			
			//Old inv = items
			
			if(invs != null){
				for(int i = 0;i < invs.length;i++){
					Map<Price, Integer> tempInv = invToPInv(invs[i]);
					newInv = RSUtils.joinMaps(newInv, tempInv);
				}
			}

			Object[] keys = newInv.keySet().toArray();

			for(Object k:keys){
                                Price key = (Price)k;
				if(tempShop.hasPrice(key)) {//Something in inventory has a price
					int amount = newInv.get(key);
					if(items.containsKey(key)) {
						int oldAm = items.get(key);
						if(oldAm > amount){//More items before than now
							amount = 0;
						} else {//More items now
							amount -= oldAm;
						}
					}
					if(bought.containsKey(key)) bought.put(key, amount + bought.get(key));
					else bought.put(key, amount);
				}
			}
		}
		
		return bought;
    }
	
    public Map<Price, Integer> getStolen(){
        //Get stolen items
        //Old inv = items

        Map<Price, Integer> newInv = invToPInv();

        Map<Price, Integer> stolen = new HashMap<>();

        Price[] keys = (Price[])newInv.keySet().toArray();
        for(Price key:keys){
            if(RealShopping.shopMap.get(store).hasPrice(key)) {//Something in inventory has a price
                int amount = newInv.get(key);
                if(hasItem(key)) {
                    int oldAm = getAmount(key);
                    if(oldAm > amount){//More items before than now
                        amount = 0;
                    } else {//More items now
                        amount -= oldAm;
                    }
                }
                if(stolen.containsKey(key)) stolen.put(key, amount + stolen.get(key));
                else stolen.put(key, amount);
            }
        }
        return stolen;
    }
    
    void createInv(String invStr){
        if(!invStr.equals("")){
            for(String item:invStr.split(",")){
                Price temp = new Price(item.split(":")[0]);
                int amount = Integer.parseInt(item.split(":")[1]);
                if(items.containsKey(temp)) items.put(temp, items.get(temp) + amount);
                else items.put(temp, amount);
            }
        }			
    }

    private Map<Price, Integer> invToPInv(ItemStack[] IS){
            Map<Price, Integer> tempMap = new HashMap<>();
            for(ItemStack iS:IS){
                    if(iS != null){
                            Price temp = new Price(iS);
                            int amount;
                            if(RealShopping.isTool(iS.getTypeId()))
                                    amount = RealShopping.getMaxDur(iS.getTypeId()) - iS.getDurability();
                            else 
                                    amount = iS.getAmount();
                            if(tempMap.containsKey(temp)) tempMap.put(temp, tempMap.get(temp) + amount);
                            else tempMap.put(temp, amount);
                    }
            }
            return tempMap;
    }

    private Map<Price, Integer> invToPInv(){
            //Guess it is safe to use getPlayerExact, because this will only be called with an online player.
            //Will throw NullPointer otherwise
            Object[] obj = ArrayUtils.addAll(Bukkit.getPlayerExact(player).getInventory().getContents()
                            , Bukkit.getPlayerExact(player).getInventory().getArmorContents());
            ItemStack[] IS = new ItemStack[obj.length];

            for(int i = 0;i < obj.length;i++)
                    IS[i] = (ItemStack) obj[i];
            return invToPInv(IS);
    }

    private Map<Price, Integer> invToPInv(Inventory inv){
            return invToPInv(inv.getContents());
    }

    public String exportToString(){
            String s = "";
            Object[] keys = items.keySet().toArray();
            for(Object oob:keys){
                    Price pi = (Price)oob;
                    if(!s.equals("")) s += ",";
                    s += pi.toString(items.get(pi).intValue());
            }
            return player + "-" + store + ";" + s;
    }

    public String getStore(){
            return store;
    }

    public Shop getShop(){
            return RealShopping.shopMap.get(store);
    }

    public String getPlayer(){ return player; }

    public boolean setStore(String store){
            this.store = store;
            return true;
    }

    public boolean hasItems(){
            return !items.isEmpty();
    }

    public boolean hasItem(ItemStack iS){
            return items.containsKey(new Price(iS));
    }

    public boolean hasItem(Price pi){
            return items.containsKey(pi);
    }

    public int getAmount(ItemStack iS){
            return items.get(new Price(iS));
    }

    public int getAmount(Price pi){
            return items.get(pi);
    }

    public int removeItem(ItemStack iS, int amount){
            return removeItem(new Price(iS), amount);
    }

    public int removeItem(Price pi, int amount){//Returns how many items couldn't be removed, or -1 if item didn't exist
            if(items.containsKey(pi))
                    if(items.get(pi) > amount){
                            items.put(pi, items.get(pi) - amount);
                            return 0;
                    } else if(items.get(pi) == amount){
                            items.remove(pi);
                            return 0;
                    } else {
                            int diff = amount - items.get(pi);
                            items.remove(pi);
                            return diff;
                    }
            return -1;
    }

    public boolean addItem(ItemStack iS, int amount){
		Price tempP = new Price(iS);
		if(items.containsKey(tempP)){
			items.put(tempP, items.get(tempP) + amount);
		} else items.put(tempP, amount);
		return true;
	}
    
    @Override
    public String toString(){
            return "PInventory Store: " + store + " Items: " + items;
    }
}