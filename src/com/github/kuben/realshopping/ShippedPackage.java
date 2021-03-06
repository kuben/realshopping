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

import java.io.Serializable;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ShippedPackage implements Serializable{
	
	private transient ItemStack[] contents;
	private int cost;
	private long dateSent;
	private transient Location locationSent;
	
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
        
        public void setContents(ItemStack[] itms){
            contents = itms;
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
	@Deprecated
	public String exportContents(){
            String s = "";
            for(ItemStack tempIS:contents){
                    if(tempIS == null) s += ",null";
                    else {
                        s += "," + tempIS.getType() +":"+ tempIS.getData().getData() + ":"+ tempIS.getAmount() + ":" + tempIS.getDurability();
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