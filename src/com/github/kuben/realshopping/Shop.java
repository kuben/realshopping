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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import com.github.kuben.realshopping.*;

public class Shop {//TODO add load/save interface
	
	public Shop(String name, String world, String owner){
		super();
		this.name = name;
		this.world = world;
		this.owner = owner;
	}

	/*
	 * 
	 * Vars
	 * 
	 */
	private String name, world, owner;//Admin stores: owner = @admin
	private int buyFor = 0;
	private byte notifyChanges = 0;
	private int changeTreshold = 1;
	private int changePercent = 5;
	private boolean allowNotifications = true;
	
	public String getName(){ return name; }
	public String getWorld(){ return world; }
	public String getOwner(){ return owner; }

	public int getBuyFor(){ return buyFor; }
	public void setBuyFor(int buyFor){ this.buyFor = buyFor; }
	public byte getNotifyChanges(){ return notifyChanges; }
	public void setNotifyChanges(byte notifyChanges){ this.notifyChanges = notifyChanges; }
	public int getChangeTreshold(){ return changeTreshold; }
	public void setChangeTreshold(int changeTreshold){ this.changeTreshold = changeTreshold; }
	public int getChangePercent(){ return changePercent; }
	public void setChangePercent(int changePercent){ this.changePercent = changePercent; }
	public boolean allowsNotifications(){ return allowNotifications; }
	public void setAllowNotifications(boolean allowNotifications){ this.allowNotifications = allowNotifications; }
	
	/*
	 * 
	 * Entrance/Exit
	 * 
	 */
	
	private List<Location> entrance = new ArrayList<Location>(), exit = new ArrayList<Location>();
	
	public boolean hasEntrance(Location en){ return entrance.contains(en); }
	public boolean hasExit(Location ex){ return exit.contains(ex); }
	public Location getFirstE(){ return entrance.get(0); }
	public List<Location> getEntrance(){ return entrance; }
	public List<Location> getExit() { return exit; }
	public Location getCorrEntrance(Location ex) { if(exit.contains(ex)){ return entrance.get(exit.indexOf(ex)).clone(); } return null; }
	public Location getCorrExit(Location en) { if(entrance.contains(en)){ return exit.get(entrance.indexOf(en)).clone(); } return null; }
	public int eLen(){ return entrance.size(); }
	public boolean addE(Location en, Location ex){
		if(entrance.contains(en) && exit.contains(ex)){//duplicates
			return false;
		} else {
			entrance.add(en);
			exit.add(ex);
			return true;
		}
	}
	public boolean delE(Location en, Location ex){
		Object[] o = entrance.toArray();
		for(int i = 0;i < o.length;i++){//Find the entrance - exit pair
			if(entrance.get(i).equals(en) && exit.get(i).equals(ex)){
				entrance.remove(i);
				exit.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 
	 * Chest functions
	 * 
	 */
	
	private Map<Location,ArrayList<Integer[]>> chests = new HashMap<Location, ArrayList<Integer[]>>();
	//TODO
	public Map<Location, ArrayList<Integer[]>> getChests() {
		return chests;
	}
	
	public boolean addChest(Location l){
		if(!chests.containsKey(l)){
			chests.put(l, new ArrayList<Integer[]>());
			if(Config.autoprotect) protectedChests.add(l);
			return true;
		}
		else return false;
	}
	public boolean delChest(Location l){
		if(chests.containsKey(l)) chests.remove(l);
		else return false;
		protectedChests.remove(l);
		return true;
	}
	public int addChestItem(Location l, int[][] id){
		int  j = -1;
		if(chests.containsKey(l)){
			j++;
			for(int[] i:id){
				if(chests.get(l).size() < 27){
					if(Material.getMaterial(i[0]) != null){
						chests.get(l).add(ArrayUtils.toObject(i));
						j++;
					}
				}
			}
		}
		return j;
	}
	public int delChestItem(Location l, int[][] id){
		int j = -1;
		if(chests.containsKey(l)){
			j++;
			for(int[] i:id) {
				boolean match = false;
				int k = 0;
				for(;k < chests.get(l).size();k++){
					if(chests.get(l).get(k)[0] == i[0] && chests.get(l).get(k)[1] == i[1]){
						match = true;
						break;
					}
				}
				if(match){
					chests.get(l).remove(k);
					j++;
				}
			}
		}
		return j;
	}

	/*
	 * 
	 * Prices
	 * 
	 */
	private Map<Price, Float[]> prices = new HashMap<Price, Float[]>();//Price array [0] is price, [1] is min and [2] is maxprice
	
	public boolean hasPrices(){ return !prices.isEmpty(); }
	public boolean hasPrice(Price p) { return prices.containsKey(p); }
	public Float getPrice(Price p) { Float[] r = prices.get(p); return (r==null?null:r[0]); }
	public Map<Price, Float> getPrices() {
		Map<Price, Float> temp = new HashMap<Price, Float>();
		for(Price p:prices.keySet().toArray(new Price[0]))
			temp.put(p, prices.get(p)[0]);
		return temp;
	}
	public Map<Price, Float[]> getPricesMap(){ return prices; }
	public Float setPrice(Price p, Float f) { Float[] r = prices.put(p, new Float[]{f}); return (r==null?null:r[0]); }
	public boolean removePrice(Price p) { return prices.remove(p) != null; }
	
	public Float getMin(Price p) { if(prices.containsKey(p) && prices.get(p).length == 3) return prices.get(p)[1]; return null; }
	public Float getMax(Price p) { if(prices.containsKey(p) && prices.get(p).length == 3) return prices.get(p)[2]; return null; }
	public boolean hasMinMax(Price p) { return (prices.containsKey(p) && prices.get(p).length == 3); }
	public boolean setMinMax(Price p, Float min, Float max){
		if(prices.containsKey(p)){
			prices.put(p, new Float[]{getPrice(p), min, max});
			return true;
		}
		return false;
	}
	public void clearMinMax(Price p) { setPrice(p, getPrice(p)); }
	public void clearPrices() { prices.clear(); }
	public boolean clonePrices(String store) {
		if(store == null){
			prices = getLowestPrices();
			return true;
		}
		if(!RealShopping.shopMap.containsKey(store)) return false;
		prices = new HashMap<Price, Float[]>(RealShopping.shopMap.get(store).prices);
		return true;
	}
	public void setPrices(Map<Price, Float[]> prices) { this.prices = prices; }
	
	/*
	 * 
	 * Sales
	 * 
	 */
	
	private Map<Price, Integer> sale = new HashMap<Price, Integer>();
	public boolean hasSales(){ return !sale.isEmpty(); }
	public boolean hasSale(Price p){ return sale.containsKey(p); }
	public void clearSales() { sale.clear(); }
	public Integer getFirstSale(){ return (Integer) sale.values().toArray()[0]; }
	public Integer getSale(Price p) { return sale.get(p); }
	public void addSale(Price p, int pcnt) { sale.put(p, pcnt); }
	public void setSale(Map<Price, Integer> sale) { this.sale = sale; }
	
	/*
	 * 
	 * Statistics
	 * 
	 */
	private Set<Statistic> stats = new HashSet<Statistic>();
	
	public Set<Statistic> getStats() {
		return stats;
	}
	public void addStat(Statistic stat){ if(stat.getAmount() > 0) stats.add(stat); }
	public void removeStat(Statistic stat){ stats.remove(stat); }
	
	/*
	 * 
	 * Stolen, banned, and protected
	 * 
	 */
	private List<ItemStack> stolenToClaim = new ArrayList<ItemStack>();
	private Set<String> banned = new HashSet<String>();
	private Set<Location> protectedChests = new HashSet<Location>();
	
	public List<ItemStack> getStolenToClaim() { return stolenToClaim; }
	public boolean hasStolenToClaim() { return !stolenToClaim.isEmpty(); }
	public void clearStolenToClaim() { stolenToClaim.clear(); }
	public void addStolenToClaim(ItemStack stolenItem) { stolenToClaim.add(stolenItem); }
	public void removeStolenToClaim(ItemStack stolenItem) { stolenToClaim.remove(stolenItem); }
	
	public Set<String> getBanned() { return banned; }
	public boolean isBanned(String p) { return banned.contains(p); }
	public void addBanned(String p) { banned.add(p); }
	public void removeBanned(String p) { banned.remove(p); }
	
	public boolean isProtectedChest(Location chest){ return protectedChests.contains(chest);}
	public boolean addProtectedChest(Location chest){ return protectedChests.add(chest); }
	public boolean removeProtectedChest(Location chest){ return protectedChests.remove(chest); }
	
	/*
	 * 
	 * Misc
	 * 
	 */

	public String exportProtectedToString(){
		if(!protectedChests.isEmpty()){
			String tempS = "";
			for(Location tempL:protectedChests){
				if(!chests.containsKey(tempL)) tempS += ";" + tempL.getWorld().getName() + "," + (int)tempL.getX() + "," + (int)tempL.getY() + "," + (int)tempL.getZ();
			}
			return (tempS.length() > 0)?tempS.substring(1):"";
		} else return "";
	}
	
	public String exportToClaim(){
		String s = "";
		for(ItemStack tempIS:stolenToClaim){
			if(tempIS != null) {
				s += "," + tempIS.getTypeId() + ":" + tempIS.getAmount() + ":" + tempIS.getDurability() + ":" + tempIS.getData().getData();
				Object[] ench = tempIS.getEnchantments().keySet().toArray();
				for(Object en:ench){
					s += ":" + ((Enchantment)en).getId() + ";" + tempIS.getEnchantments().get(en);
				}
			}
		}
		return (s.length() > 0)?s.substring(1):"";
	}
	
	public String exportStats(){
		String s = "";
		for(Statistic stat:stats){
			s += ";"+stat.getTime()+":"+stat.isBought()+":";
			s += stat.getItem().type + ":" + stat.getAmount() + (stat.getItem().data > 0?":"+stat.getItem().data:"");
			Object[] ench = stat.getItem().enchantments.keySet().toArray();
			for(Object en:ench){
				s += "[" + ((Enchantment)en).getId() + ":" + stat.getItem().enchantments.get(en) + "]";
			}
		}
		return s;
	}
	
	@Override
	public String toString(){
		return "Shop " + name + (owner.equals("@admin")?"":" owned by " + owner) + " Prices: " + prices.toString();
	}

	@SuppressWarnings("static-access")
	private Map<Price, Float[]> getLowestPrices(){
		Map<Price, Float[]> tempMap = new HashMap<Price, Float[]>();
		String[] keys = RealShopping.shopMap.keySet().toArray(new String[0]);
		for(String s:keys){
			if(!s.equals(name)){
				Price[] keys2 = RealShopping.shopMap.get(s).getPrices().keySet().toArray(new Price[0]);
				for(Price p:keys2){
					if(tempMap.containsKey(p)){
						if(tempMap.get(p)[0] > RealShopping.shopMap.get(s).getPrice(p)) tempMap.put(p, new Float[]{RealShopping.shopMap.get(s).getPrice(p)});
					} else
						tempMap.put(p, new Float[]{RealShopping.shopMap.get(s).getPrice(p)});
				}
			}
		}
		return tempMap;
	}
}

final class Statistic {
	
	private PItem item;
	private int amount;
	private long timestamp;
	private boolean bought;
	
	public Statistic(PItem item, int amount, boolean bought){
		this.item = item;
		this.amount = amount;
		this.timestamp = System.currentTimeMillis();
		this.bought = bought;
	}
	
	public Statistic(String imp){
		this.timestamp = Long.parseLong(imp.split("\\[")[0].split(":")[0]);
		this.bought = Boolean.parseBoolean(imp.split("\\[")[0].split(":")[1]);
		Byte data = 0;
		Map<Enchantment, Integer> enchs = new HashMap<Enchantment, Integer>();
		if(imp.split("\\[")[0].split(":").length > 4) data = Byte.parseByte(imp.split("\\[")[0].split(":")[4]);
		for(int i = 1;i < imp.split("\\[").length;i++){
			enchs.put(Enchantment.getById(Integer.parseInt(imp.split("\\[")[i].split("\\]")[0].split(":")[0])), Integer.parseInt(imp.split("\\[")[i].split("\\]")[0].split(":")[1]));
		}
		this.item = new PItem(Integer.parseInt(imp.split(":")[2]), data, enchs);
		this.amount = Integer.parseInt(imp.split("\\[")[0].split(":")[3]);
	}

	public PItem getItem() {
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

final class Price {
	private int type;
	private int data;
	
	public Price(int type){
		this.type = type;
		this.data = -1;
	}
	
	public Price(int type, int data){
		this.type = type;
		this.data = data;
	}
	
	public Price(String s){
		this.type = Integer.parseInt(s.split(":")[0]);
		this.data = s.split(":").length==1?-1:Integer.parseInt(s.split(":")[1]);
	}

	public int getType() {
		return type;
	}

	public int getData() {
		return data;
	}
	
	@Override
	public String toString() {
		return type + (data > -1?":"+data:"");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
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
		Price other = (Price) obj;
		if (data != other.data)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}