package com.github.kuben.realshopping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RSPlayerInventory {

	private Map<PItem, Integer> items;
	private String store;
	
	public RSPlayerInventory(Player p, String store){//Use when player is entering store
		this.store = store;
		items = invToPInv(p);//Item - amount/dur
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
				items.putAll(invToPInv(invs[i]));
		
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
				if(RealShopping.shopMap.get(store).prices.containsKey(key))//If item has price
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
					newInv.putAll(tempInv);
				}
			}

			Object[] keys = newInv.keySet().toArray();

			for(int i = 0;i < keys.length;i++){
				int type = (Integer) keys[i];
				if(tempShop.prices.containsKey(type)){//Something in inventory has a price
					int amount = newInv.get(type);
					float cost = tempShop.prices.get(type);
					if(tempShop.sale.containsKey(type)){//There is a sale on that item.
						int pcnt = 100 - tempShop.sale.get(type);
						cost *= pcnt;
						cost = Math.round(cost);
						cost /= 100;
					}
					if(items.containsKey(type)) {
						int oldAm = items.get(type);
						if(oldAm > amount){//More items before than now
							amount = 0;
						} else {//More items now
							amount -= oldAm;
						}
					}
					toPay += cost * (RealShopping.maxDurMap.containsKey(type)?Math.ceil((double)amount / (double)RealShopping.maxDurMap.get(type)):amount);//Convert items durability to item amount
				}
			}
		}
		
		return toPay;
    }
	
    public Map<PItem, Integer> getStolen(){
		//Get stolen items
		//Old inv = items
		
		Map<PItem, Integer> newInv = invToPInv();
		
		Map<PItem, Integer> stolen = new HashMap<PItem, Integer>();
		
		Object[] keys = newInv.keySet().toArray();
		for(int i = 0;i < keys.length;i++){
			if(RealShopping.shopMap.get(store).prices.containsKey(((PItem) keys[i]).type)){//Something in inventory has a price
				int amount = newInv.get(keys[i]);
				if(hasItem((PItem) keys[i])) {
					int oldAm = getAmount((PItem) keys[i]);
					if(oldAm > amount){//More items before than now
						amount = 0;
					} else {//More items now
						amount -= oldAm;
					}
				}
				if(stolen.containsKey(keys[i])) stolen.put((PItem) keys[i], amount + stolen.get(keys[i]));
				else stolen.put((PItem) keys[i], amount);
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
				if(item.split("[")[0].split(":").length > 2) data = Byte.parseByte(item.split("[")[0].split(":")[2]);
				if(item.split("[").length > 1){
					for(String ench:item.substring(item.indexOf("[")).split("[")){
						String[] en = ench.split("]")[0].split(":");
						enchts.put(Enchantment.getById(Integer.parseInt(en[0])), Integer.parseInt(en[1]));
					}
				}

				PItem temp = new PItem(type, data, enchts);
				int amount = Integer.parseInt(item.split("[")[0].split(":")[1]);
				if(items.containsKey(temp)) items.put(temp, items.get(temp) + amount);
				else items.put(temp, amount);
			}
		}			
	}
	
	private Map<PItem, Integer> invToPInv(Player p){//TODO merge..
		Map<PItem, Integer> tempMap = new HashMap<PItem, Integer>();
		for(Object iS:ArrayUtils.addAll(p.getInventory().getContents(), p.getInventory().getArmorContents())){
			if(iS != null){
				ItemStack IS = (ItemStack)iS;
				PItem temp = new PItem(IS);
				int amount;
				if(RealShopping.maxDurMap.containsKey(IS.getTypeId()))
					amount = RealShopping.maxDurMap.get(IS.getTypeId()) - IS.getDurability();
				else 
					amount = IS.getAmount();
				if(tempMap.containsKey(temp)) tempMap.put(temp, tempMap.get(temp) + amount);
				else tempMap.put(temp, amount);
			}
		}
		return tempMap;
	}
	
	private Map<PItem, Integer> invToPInv(){
		return invToPInv(getOwner());
	}
	
	private Map<PItem, Integer> invToPInv(Inventory inv){
		Map<PItem, Integer> tempMap = new HashMap<PItem, Integer>();
		for(Object iS:inv.getContents()){
			if(iS != null){
				ItemStack IS = (ItemStack)iS;
				PItem temp = new PItem(IS);
				int amount;
				if(RealShopping.maxDurMap.containsKey(IS.getTypeId()))
					amount = RealShopping.maxDurMap.get(IS.getTypeId()) - IS.getDurability();
				else 
					amount = IS.getAmount();
				if(tempMap.containsKey(temp)) tempMap.put(temp, tempMap.get(temp) + amount);
				else tempMap.put(temp, amount);
			}
		}
		return tempMap;
	}
	
	public String exportToString(){
		String s = "";
		Object[] keys = items.keySet().toArray();
		for(PItem pi:(PItem[]) keys){
			if(!s.equals("")) s += ",";
			s += pi.type + ":" + items.get(pi) + (pi.data > 0?":"+pi.data:"");
			Object[] ench = pi.enchantments.keySet().toArray();
			for(Enchantment en:(Enchantment[]) ench){
				s += "[" + en.getId() + ":" + pi.enchantments.get(en) + "]";
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
		return items.containsKey(new PItem(iS));
	}
	
	private Player getOwner(){//Will return null if THIS is removed from PInvMap
		Object[] keys = RealShopping.PInvMap.keySet().toArray();
		for(Object key:keys){
			if(RealShopping.PInvMap.get(key).equals(this)) return Bukkit.getPlayer(key.toString());
			else RealShopping.log.info(RealShopping.PInvMap.get(key)+"");
		}
		return null;
	}
}

class PItem{
	
	int type;
	byte data;
	Map<Enchantment, Integer> enchantments;
	
	public PItem(ItemStack is){
		enchantments = is.getEnchantments();
		type = is.getTypeId();
		data = is.getData().getData();
	}
	
	public PItem(int type, byte data, Map enchantments){
		this.enchantments = enchantments;
		this.type = type;
		this.data = data;
	}
	
	public ItemStack toItemStack(){
		ItemStack tempIS = new ItemStack(type, 0, (short) 0, data);
		tempIS.addEnchantments(enchantments);
		return tempIS;
	}
	
}