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
import org.bukkit.Material;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

public class Shop {
	
	public Set<Statistic> stats = new HashSet<Statistic>();

	public Map<Integer, Float> prices = new HashMap<Integer, Float>();
	public List<ItemStack> stolenToClaim = new ArrayList<ItemStack>();
	
	public Map<Location,ArrayList<Integer[]>> chests = new HashMap<Location, ArrayList<Integer[]>>();
	public Map<String,ArrayList<ItemStack>> sellToStore = new HashMap<String, ArrayList<ItemStack>>();
	public Map<Integer, Integer> sale = new HashMap<Integer, Integer>();
	public List<Location> entrance = new ArrayList<Location>(), exit = new ArrayList<Location>();
	public Set<String> banned = new HashSet<String>();
	public Set<Location> protectedChests = new HashSet<Location>();
	public String name, world, owner;//Admin stores: owner = @admin
	public int buyFor = 0;
	
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
		return (bought?"bought":"sold") + item.toString() + " x" + amount;
	}
}