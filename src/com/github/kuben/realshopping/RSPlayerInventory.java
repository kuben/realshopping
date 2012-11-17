/*
 * RealShopping Bukkit plugin for Minecraft
 * Copyright 2012 Jakub Fojt
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class RSPlayerInventory {

	private Map<PItem, Integer> items;
	private String store;
	
	public RSPlayerInventory(Player p, String store){//Use when player is entering store
		this.store = store;
		
		//Special Inv to PInv
		Object[] obj = ArrayUtils.addAll(p.getInventory().getContents(), p.getInventory().getArmorContents());
		ItemStack[] IS = new ItemStack[obj.length];
		
		for(int i = 0;i < obj.length;i++)
			IS[i] = (ItemStack) obj[i];
		items = invToPInv(IS);//Item - amount/dur
	}
	
	public RSPlayerInventory(String invStr, String store){//Use to recover a PlayerInventory from string
		items = new HashMap<PItem, Integer>();//Item - amount/dur
		this.store = store;

		createInv(invStr);
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
				items = Utils.joinMaps(items, invToPInv(invs[i]));
		
		return true;	
	}
	
    public boolean hasPaid(){
		Map<PItem, Integer> newInv = invToPInv();
		
		//Old inv = items
		
		Object[] keys = newInv.keySet().toArray();
		boolean hasPaid = true;
		if(!RealShopping.shopMap.get(store).prices.isEmpty())//If there are prices for store.
			for(int j = 0;j < keys.length;j++){
				PItem key = (PItem)keys[j];
				if(RealShopping.shopMap.get(store).prices.containsKey(new Price(key.type)) || RealShopping.shopMap.get(store).prices.containsKey(new Price(key.type, key.data)))//If item has price
					if(items.containsKey(key)){
						if(newInv.get(key) > items.get(key))
							hasPaid = false;
					} else hasPaid = false;
			}
		return hasPaid;
    }
    
    public float toPay(){
    	return toPay(null);
    }
    
    public float toPay(Inventory[] invs){
    	float toPay = 0;
    	Shop tempShop = RealShopping.shopMap.get(store);
		if(!tempShop.prices.isEmpty()){//If shop has prices
			Map<PItem, Integer> newInv = invToPInv();
			
			//Old inv = items
			
			if(invs != null){
				for(int i = 0;i < invs.length;i++){
					Map<PItem, Integer> tempInv = invToPInv(invs[i]);
					newInv = Utils.joinMaps(newInv, tempInv);
				}
			}

			Object[] keys = newInv.keySet().toArray();

			for(int i = 0;i < keys.length;i++){
				PItem key = (PItem) keys[i];
				if(tempShop.prices.containsKey(new Price(key.type)) || tempShop.prices.containsKey(new Price(key.type, key.data))) {//Something in inventory has a price
					int amount = newInv.get(keys[i]);
					float cost = -1;
					if(tempShop.prices.containsKey(new Price(key.type))) cost = tempShop.prices.get(new Price(key.type));
					if(tempShop.prices.containsKey(new Price(key.type, key.data))) cost = tempShop.prices.get(new Price(key.type, key.data));
					if(tempShop.sale.containsKey(new Price(key.type)) || tempShop.sale.containsKey(new Price(key.type, key.data))){//There is a sale on that item.
						int pcnt = -1;
						if(tempShop.sale.containsKey(new Price(key.type))) pcnt = 100 - tempShop.sale.get(new Price(key.type));
						if(tempShop.sale.containsKey(new Price(key.type, key.data)))  pcnt = 100 - tempShop.sale.get(new Price(key.type, key.data));
						cost *= pcnt;
						cost = Math.round(cost);
						cost /= 100;
					}
					if(items.containsKey(key)) {
						int oldAm = items.get(key);
						if(oldAm > amount){//More items before than now
							amount = 0;
						} else {//More items now
							amount -= oldAm;
						}
					}
					toPay += cost * (RealShopping.maxDurMap.containsKey(key.type)?Math.ceil((double)amount / (double)RealShopping.maxDurMap.get(key.type)):amount);//Convert items durability to item amount
				}
			}
		}
		
		return toPay;
    }
    
    public Map<PItem, Integer> getBought(Inventory[] invs){
    	Map<PItem, Integer> bought = new HashMap<PItem, Integer>();
    	Shop tempShop = RealShopping.shopMap.get(store);
		if(!tempShop.prices.isEmpty()){//If shop has prices
			Map<PItem, Integer> newInv = invToPInv();
			
			//Old inv = items
			
			if(invs != null){
				for(int i = 0;i < invs.length;i++){
					Map<PItem, Integer> tempInv = invToPInv(invs[i]);
					newInv = Utils.joinMaps(newInv, tempInv);
				}
			}

			Object[] keys = newInv.keySet().toArray();

			for(int i = 0;i < keys.length;i++){
				PItem key = (PItem) keys[i];
				if(tempShop.prices.containsKey(new Price(key.type)) || tempShop.prices.containsKey(new Price(key.type, key.data))) {//Something in inventory has a price
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
	
    public Map<PItem, Integer> getStolen(){
		//Get stolen items
		//Old inv = items
		
		Map<PItem, Integer> newInv = invToPInv();
		
		Map<PItem, Integer> stolen = new HashMap<PItem, Integer>();
		
		Object[] keys = newInv.keySet().toArray();
		for(int i = 0;i < keys.length;i++){
			PItem key = (PItem) keys[i];
			if(RealShopping.shopMap.get(store).prices.containsKey(new Price(key.type))
			|| RealShopping.shopMap.get(store).prices.containsKey(new Price(key.type, key.data))) {//Something in inventory has a price
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
				int type = Integer.parseInt(item.split(":")[0]);//Split [ unnecessary
				byte data = 0;
				Map<Enchantment, Integer> enchts = new HashMap<Enchantment, Integer>();
				if(item.split("\\[")[0].split(":").length > 2) data = Byte.parseByte(item.split("\\[")[0].split(":")[2]);
				if(item.split("\\[").length > 1){
					for(String ench:item.substring(item.indexOf("[")+1).split("\\]")){
						String[] en = ench.substring(ench.indexOf("[")<0?0:ench.indexOf("[")+1).split(":");
						enchts.put(Enchantment.getById(Integer.parseInt(en[0])), Integer.parseInt(en[1]));
					}
				}

				PItem temp = new PItem(type, data, enchts);
				int amount = Integer.parseInt(item.split("\\[")[0].split(":")[1]);
				if(items.containsKey(temp)) items.put(temp, items.get(temp) + amount);
				else items.put(temp, amount);
			}
		}			
	}
	
	private Map<PItem, Integer> invToPInv(ItemStack[] IS){
		Map<PItem, Integer> tempMap = new HashMap<PItem, Integer>();
		for(ItemStack iS:IS){
			if(iS != null){
				PItem temp = new PItem(iS);
				int amount;
				if(RealShopping.maxDurMap.containsKey(iS.getTypeId()))
					amount = RealShopping.maxDurMap.get(iS.getTypeId()) - iS.getDurability();
				else 
					amount = iS.getAmount();
				if(tempMap.containsKey(temp)) tempMap.put(temp, tempMap.get(temp) + amount);
				else tempMap.put(temp, amount);
			}
		}
		return tempMap;
	}
	
	private Map<PItem, Integer> invToPInv(){
		Object[] obj = ArrayUtils.addAll(getOwner().getInventory().getContents(), getOwner().getInventory().getArmorContents());
		ItemStack[] IS = new ItemStack[obj.length];
		
		for(int i = 0;i < obj.length;i++)
			IS[i] = (ItemStack) obj[i];
		return invToPInv(IS);
	}
	
	private Map<PItem, Integer> invToPInv(Inventory inv){
		return invToPInv(inv.getContents());
	}
	
	public String exportToString(){
		String s = "";
		Object[] keys = items.keySet().toArray();
		for(Object Opi:keys){
			PItem pi = (PItem)Opi;
			if(!s.equals("")) s += ",";
			s += pi.type + ":" + items.get(pi) + (pi.data > 0?":"+pi.data:"");
			Object[] ench = pi.enchantments.keySet().toArray();
			for(Object en:ench){
				s += "[" + ((Enchantment)en).getId() + ":" + pi.enchantments.get(en) + "]";
			}
		}
		return s;
	}
	
	public String getStore(){
		return store;
	}
	
	public boolean setStore(String store){
		this.store = store;
		return true;
	}
	
	public boolean hasItems(){
		return !items.isEmpty();
	}
	
	public boolean hasItem(ItemStack iS){
		return items.containsKey(new PItem(iS));
	}
	
	public boolean hasItem(PItem pi){
		return items.containsKey(pi);
	}
	
	public int getAmount(ItemStack iS){
		return items.get(new PItem(iS));
	}
	
	public int getAmount(PItem pi){
		return items.get(pi);
	}
	
	public int removeItem(ItemStack iS, int amount){
		return removeItem(new PItem(iS), amount);
	}
	
	public int removeItem(PItem pi, int amount){//Returns how many items couldn't be removed, or -1 if item didn't exist
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
		PItem tempP = new PItem(iS);
		if(items.containsKey(tempP)){
			items.put(tempP, items.get(tempP) + amount);
		} else items.put(tempP, amount);
		return true;
	}
	
	private Player getOwner(){//Will return null if THIS is removed from PInvMap
		Object[] keys = RealShopping.PInvMap.keySet().toArray();
		for(Object key:keys){
			if(RealShopping.PInvMap.get(key).equals(this)) return Bukkit.getPlayer(key.toString());
		}
		return null;
	}
	
	@Override
	public String toString(){
		return "PInventory Store: " + store + " Items: " + items;
	}
}

final class PItem{
	
	int type;
	byte data;
	Map<Enchantment, Integer> enchantments;
	
	public PItem(ItemStack is){
		enchantments = is.getEnchantments();
		type = is.getTypeId();
		data = is.getData().getData();
	}
	
	public PItem(int type, byte data, Map<Enchantment, Integer> enchantments){
		this.enchantments = enchantments;
		this.type = type;
		this.data = data;
	}
	
	public ItemStack toItemStack(){
		ItemStack tempIS = new ItemStack(type, 0, (short) 0, data);
		tempIS.addEnchantments(enchantments);
		return tempIS;
	}
	
	@Override
	public String toString(){
		return "PItem " + new MaterialData(type, data) + (enchantments.isEmpty()?"":" with " + enchantments.toString());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
		result = prime * result
				+ ((enchantments == null) ? 0 : enchantments.hashCode());
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PItem other = (PItem) obj;
		if (data != other.data)
			return false;
		if (enchantments == null) {
			if (other.enchantments != null)
				return false;
		} else if (!enchantments.equals(other.enchantments))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}

class ShippedPackage{
	
	private ItemStack[] contents;
	private float cost;
	private long dateSent;
	private Location locationSent;
	
	public ShippedPackage(ItemStack[] contents, float cost, Location locationSent){
		this.contents = contents;
		this.cost = cost;
		this.locationSent = locationSent;
		this.dateSent = System.currentTimeMillis();
	}
	
	public ShippedPackage(ItemStack[] contents, float cost, Location locationSent, long dateSent){
		this.contents = contents;
		this.cost = cost;
		this.locationSent = locationSent;
		this.dateSent = dateSent;
	}
	
	public ItemStack[] getContents(){
		return contents;
	}
	
	public long getDateSent(){
		return dateSent;
	}
	
	public float getCost(){
		return cost;
	}
	
	public Location getLocationSent(){
		return locationSent;
	}
	
	public String exportContents(){
		String s = "";
		for(ItemStack tempIS:contents){
			if(tempIS == null) s += ",null";
			else {
				s += "," + tempIS.getTypeId() + ":" + tempIS.getAmount() + ":" + tempIS.getDurability() + ":" + tempIS.getData().getData();
				Object[] ench = tempIS.getEnchantments().keySet().toArray();
				for(Object en:ench){
					s += ":" + ((Enchantment)en).getId() + ";" + tempIS.getEnchantments().get(en);
				}
			}
		}
		return (s.length() > 0)?s.substring(1):"";
	}
	
	@Override
	public String toString(){
		String s = "Shipped Package sent " + new Date(dateSent) + " from "
				+ locationSent.getBlockX() + "," + locationSent.getBlockY() + "," + locationSent.getBlockZ()
				+ " in world " + locationSent.getWorld() + " with ";
		for(ItemStack iS:contents){
			if(iS != null) s += iS + ", ";
		}
		return s;
	}
}

class Utils{
	public static Map<PItem, Integer> joinMaps(Map<PItem,Integer> uno, Map<PItem,Integer> dos){//Preserves old values
		PItem[] keys = dos.keySet().toArray(new PItem[0]);
		for(PItem o:keys){
			if(uno.containsKey(o)) uno.put(o, uno.get(o) + dos.get(o));
			else uno.put(o, dos.get(o));
		}
		return uno;
	}
	
    public static String formatNum(int value) {
        int hunRem = value % 100;
        int tenRem = value % 10;
        if (hunRem - tenRem == 10) {
                return value + "th";
        }
        switch (tenRem) {
        case 1:
                return value + "st";
        case 2:
                return value + "nd";
        case 3:
                return value + "rd";
        default:
                return value + "th";
        }
}

}