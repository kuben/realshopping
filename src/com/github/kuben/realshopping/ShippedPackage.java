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

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ShippedPackage{
	
	private ItemStack[] contents;
	private int cost;
	private long dateSent;
	private Location locationSent;
	
	public ShippedPackage(ItemStack[] contents, int cost, Location locationSent){
		this.contents = contents;
		this.cost = cost;
		this.locationSent = locationSent;
		this.dateSent = System.currentTimeMillis();
	}
	
	public ShippedPackage(ItemStack[] contents, int cost, Location locationSent, long dateSent){
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
	
	public int getCost(){
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
		String s = LangPack.SHIPPEDPACKAGESENT + new Date(dateSent) + LangPack.FROM
				+ RSUtils.locAsString(locationSent)
				+ LangPack.INWORLD + locationSent.getWorld() + LangPack.WITH;
		for(ItemStack iS:contents){
			if(iS != null) s += iS + ", ";
		}
		return s;
	}
}