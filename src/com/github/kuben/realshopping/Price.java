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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

        
public final class Price {
	private int type;
	private byte data;
        private int metahash;
        private String display_name;
	
//        public Price(ItemMeta meta, int type) {
//                this(meta,type,Byte.parseByte("-1"));
//        }
//        
//        public Price(ItemMeta meta, int type, byte data) {
//                this(type,data);
//                this.metahash = meta.hashCode();
//
//        }
        
	public Price(int type){
            this(type,Byte.parseByte("0"));
	}
	
	public Price(int type, byte data){
            this(new MaterialData(type, data).toItemStack());
	}
        public Price(int type, byte data, String displayname, int metahash){
            this.type = type;
            this.data = data;
            this.display_name = displayname;
            this.metahash = metahash;
        }
        
        public Price(ItemStack itm) {
            this(itm.getTypeId(),itm.getData().getData(),null,0);
            if(itm.hasItemMeta()){
                ItemMeta meta = itm.getItemMeta();
                if(this.type == Material.WRITTEN_BOOK.getId()) this.display_name = ((BookMeta)meta).getTitle();
                this.metahash = meta.hashCode();
                if(meta.hasDisplayName()) this.display_name = meta.getDisplayName();
            }
        }
        @Deprecated
	public Price(String s){
		this.type = Integer.parseInt(s.split(":")[0]);
		this.data = s.split(":").length==1?-1:Byte.parseByte(s.split(":")[1]);
	}
        
        public ItemStack toItemStack(){
		ItemStack tempIS = new MaterialData(type, data).toItemStack();
		return tempIS;
	}
        
        public String getDisplayName(){
            return display_name;
        }
        
        public boolean hasDisplayName(){
            return display_name != null;
        }
        
        public int getType() {
		return type;
	}

	public byte getData() {
		return data;
	}

	public Price stripOffData(){
		return new Price(type);
	}
	
        /*
         * Returns a item standard formatted string like
         * ID:DATA NAME
         * where NAME can be the display name (if present) of item 
         * or else the material type.
         */
        public String formattedString(){
            String s = type+(data > 0?":"+data:"")+" "+Material.getMaterial(type).toString();
            s += (display_name != null ? " - "+display_name:"");
            return s;
        }
        public String toString(int amount) {
            String s = "";
                s += getType() + ":" + amount + (getData() > 0?":"+getData():"");
                if(hasDisplayName()){
                    s+="[Name:"+this.display_name+"]";
                }
                return s;
        }
        
	@Override
	public String toString() {
                return type+":"+data;
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