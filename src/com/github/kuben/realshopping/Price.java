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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
/**
 * This class represents a price for an item inside the store.
 * The price must be unique for custom items (the ones that have a different ItemMeta()).
 * This class also stores the hint (if present) of this item when printing prices with /rsprices.
 * @author stengun
 */
        
public final class Price {
	private int type;
	private byte data;
        private int metahash;
        private String description;
        
	public Price(int type){
            this(type,Byte.parseByte("0"));
	}
	public Price(int type, byte data){
            this(new MaterialData(type, data).toItemStack());
	}
        public Price(int type, byte data, int metahash){
            this.type = type;
            this.data = data;
            this.metahash = metahash;
            this.description = null;
        }
        /**
         * Correctly builds a Price from an itemstack.
         * This constructor is preferred when building a new Price.
         * @param itm Itemstack from where taking the values.
         */
        public Price(ItemStack itm) {
            this(itm.getTypeId(),itm.getData().getData(),0);
            if(itm.hasItemMeta()) this.metahash = itm.getItemMeta().hashCode();
            if(RealShopping.isTool(itm.getTypeId())){ // Prototype for different Durability on items.
                final int prime = 31;
                this.metahash = (this.metahash + (itm.getDurability() * prime))*prime;
            }
        }
        /**
         * Constructs a price object from a string.
         * The string must be formatted as follows:
         * ID:data:metahash[:description]
         * @param s A string representing a price object.
         */
	public Price(String s){
            String[] tmp = s.split(":");
            this.type = Integer.parseInt(tmp[0]);
            this.data = Byte.parseByte(tmp[1]);
            this.metahash = Integer.parseInt(tmp[2]);
            if(tmp.length >3){
                this.description = tmp[3];
            }
	}
        /**
         * This method creates a dummy itemstack of this object.
         * Use only in situations where you need an itemstack and a presence of ItemMeta is irrelevant.
         * @return Dummy itemstack object.
         */
        public ItemStack toItemStack(){
		ItemStack tempIS = new MaterialData(type, data).toItemStack();
		return tempIS;
	}
        
        public String getDescription(){
            return description;
        }
        
        public void setDescription(String s){
            this.description = s;
        }
        
        public boolean hasDescription(){
            return !(description == null || description.equals("") || description.isEmpty());
        }
        
        public int getType() {
		return type;
	}

	public byte getData() {
		return data;
	}
@Deprecated
	public Price stripOffData(){
		return new Price(type);
	}
	
        /**
         * Returns a standard formatted string like
         * ID:DATA MATNAME [- NAME]
         * where MATNAME is the material name, NAME can be the hint string (if present) of that item.
         * @return Display formatted string for this object.
         */
        public String formattedString(){
            return type+(data > 0?":"+data:"")+" "+Material.getMaterial(type).toString() + (hasDescription() ? " - "+description:"");
        }
        /**
         * Formats this object with a stat formatted string with amount.
         * The format will be:
         * ID:DATA:AMOUNT[:DESCRIPTION]
         * @param amount amount of that item.
         * @return this formatted object.
         */
        public String toString(int amount) {
            return toString() + amount;
        }
        
        /**
         * Returns a string that represents this Price object and that can be parsed by Price(String ) constructor.
         * @return Constructor ready string for this object.
         */
	@Override
	public String toString() {
            return type+":"+data+":"+metahash+(hasDescription()?":"+description:"");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
                result = prime * result + metahash;
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
                if (obj == null || getClass() != obj.getClass()) return false;
                if (this == obj) return true;
                Price other = (Price) obj;
                if (data != other.data || type != other.type) return false;
                if(metahash != other.metahash) return false;
                return true;
	}

    public int getMetaHash() {
        return metahash;
    }
}