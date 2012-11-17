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

public class Shop {
	
	public Set<Statistic> stats = new HashSet<Statistic>();

	public Map<Price, Float> prices = new HashMap<Price, Float>();//number after decimal is data value
	public List<ItemStack> stolenToClaim = new ArrayList<ItemStack>();
	
	public Map<Location,ArrayList<Integer[]>> chests = new HashMap<Location, ArrayList<Integer[]>>();
	public Map<String,ArrayList<ItemStack>> sellToStore = new HashMap<String, ArrayList<ItemStack>>();
	public Map<Price, Integer> sale = new HashMap<Price, Integer>();
	public List<Location> entrance = new ArrayList<Location>(), exit = new ArrayList<Location>();
	public Set<String> banned = new HashSet<String>();
	public Set<Location> protectedChests = new HashSet<Location>();
	public String name, world, owner;//Admin stores: owner = @admin
	public int buyFor = 0;
	public byte notifyChanges = 0;
	public int changeTreshold = 1;
	public int changePercent = 5;
	public boolean allowNotifications = true;
	
	public Shop(String name, String world, String owner){
		super();
		this.name = name;
		this.world = world;
		this.owner = owner;
	}
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
		return "Shop " + name + (owner.equals("@admin")?"":" owned by " + owner + " Prices: " + prices.toString());
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