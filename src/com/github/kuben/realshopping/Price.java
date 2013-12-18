/*
 * RealShopping Bukkit plugin for Minecraft
 * Copyright 2013 Jakub Fojt, Roberto Benfatto
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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.MaterialData;

/**
 * This class represents a price for an item inside the store.
 * The price must be unique for custom items (the ones that have a different ItemMeta()).
 * This class also stores the hint (if present) of this item when printing prices with /rsprices.
 * @author stengun
 */
        
public final class Price {
    private Material type;
    private int metahash,amount;
    private MaterialData data;
    private String description,easyname,name;
    private boolean isgeneric = false;
    
    public Price(Material type){
        this(type,new MaterialData(type));
    }
    public Price(Material type, MaterialData data){
        this(new ItemStack(type));
        this.data = data;
    }
    public Price(Material type, MaterialData data, int metahash){
        this.type = type;
        this.data = data;
        this.metahash = metahash;
        this.description = null;
        this.name = null;
        this.easyname = type.toString();
        this.amount = 1;
    }
    /**
     * Correctly builds a Price from an itemstack.
     * This constructor is preferred when building a new Price.
     * @param itm Itemstack from where taking the values.
     */
    public Price(ItemStack itm) {
        this(itm.getType(),itm.getData(),0);
        //this.amount = itm.getAmount();
        final int prime = 31;
        if(itm.hasItemMeta()) {
            if(this.type == Material.WRITTEN_BOOK) { // prototype for books. I hash only 1 page to avoid overflow.
                BookMeta bm = (BookMeta) itm.getItemMeta();
                this.metahash = metahash + (bm.getPage(1).hashCode() * prime);
                this.metahash = metahash + (bm.getAuthor().hashCode()*prime);
                this.metahash = metahash + (bm.getTitle().hashCode()*prime);
                this.name = bm.getTitle();
            }
            else {
                this.name = itm.getItemMeta().hasDisplayName()?itm.getItemMeta().getDisplayName():null;
                this.metahash = itm.getItemMeta().hashCode();
            }
        }
        if(RealShopping.isTool(itm.getType())){ // Prototype for different Durability on items.
            this.metahash = (this.metahash + (itm.getDurability() * prime))*prime;
        }
    }
    /**
     * Constructs a price object from a string.
     * The string must be formatted as follows:
     * ID:data:amount:metahash[:description]
     * @param s A string representing a price object.
     * @deprecated
     */
    public Price(String s){
        String[] tmp = s.split(":");
//        this.type = Integer.parseInt(tmp[0]);
//        this.data = Byte.parseByte(tmp[1]);
        this.amount = Integer.parseInt(tmp[2]);
        this.metahash = Integer.parseInt(tmp[3]);
//        this.easyname = Material.getMaterial(type).toString();
        if(tmp.length >4){
            this.name = tmp[4];
        } this.name = null;
        
        if(tmp.length >5){
            this.description = tmp[5];
        } else this.description = null;
    }
    /**
     * This method creates a dummy itemstack of this object.
     * Use only in situations where you need an itemstack and a presence of ItemMeta is is not needeed.
     * @return Dummy itemstack object.
     */
    public ItemStack toItemStack(){
            ItemStack tempIS = new ItemStack(this.type);
            tempIS.setData(this.data);
            //tempIS.setAmount(amount);
            return tempIS;
    }

    public void setGeneric(boolean generic) {
        this.isgeneric = generic;
    }

    public boolean isIsgeneric() {
        return isgeneric;
    }
    
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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

    public Material getType() {
            return type;
    }

    public MaterialData getData() {
            return data;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean hasName() {
        return this.name != null;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Returns the human readable name of this item.
     * @return Easy name for this item.
    */
    public String getEasyname() {
        return easyname;
    }
  
    /**
     * Returns a standard formatted string.
     * This string will be of this format:
     * EASYNAME - COST [SALE STATUS]
     * Amount: AMOUNT
     * [Description: DESCRIPTION]
     * where MATNAME is the material name, NAME can be the hint string (if present) of that item.
     * @param cost This is the cost we must print for this item.
     * @param sale Triggers the activation of "ON SALE" green indicator near the item name.
     * @return Display formatted string for this object.
     */
    public String formattedString(double cost, Integer sale){
        return  ChatColor.BLUE + easyname + ((amount>1)?" * "+ ChatColor.GREEN + amount + ChatColor.RESET:"")+ 
                ChatColor.BLACK + " - " + ChatColor.RED + cost + LangPack.UNIT + 
                (sale!=null? " " + ChatColor.GREEN + LangPack.ONSALE + " " + sale +"%":"") + ChatColor.RESET +
                (name!=null?ChatColor.YELLOW + (this.type == Material.WRITTEN_BOOK?"\n┗━ Title: ": "\n┗━ Name: ") + ChatColor.GRAY + name: "") +
                (hasDescription()?ChatColor.YELLOW + "\n┗━ Description: "+ ChatColor.GRAY + description:"");
    }
    /**
     * Formats this object with a stat formatted string with amount of this Price object.
     * The format will be:
     * [Price tostring format]:amount
     * 
     * This amount must not be confused with Price amount that indicates 
     * how many items are sold with that cost.
     * @param amount amount of that item.
     * @return this formatted object.
     */
    public String toString(int amount) {
        return toString() + amount;
    }

    /**
     * The method which correctly returns a string containing type, data , amount sold, and eventually description for statistics.
     * @param amount The amount of items sold.
     * @return A string ready to be used in saveHelper for statistics.
     */
    public String export(int amount){
        return type + ":" + data + ":" + (hasName()?name + ":":"") + amount;
    }
    
    /**
     * Returns a string that represents this Price object and that can be parsed by Price(String ) constructor.
     * @return Constructor ready string for this object.
     */
    @Override
    public String toString() {
        return type + ":" + data + ":" + amount + ":" + metahash + (hasName()?name + ":":"") + (hasDescription()?":"+description:"");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + data.hashCode();
        result = prime * result + type.hashCode();
        result = prime * result + amount;
        result = prime * result + metahash;
        return result;
    }
    
    public boolean similarButHash(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        if (this == obj) return true;
        Price other = (Price) obj;
        return type == other.type;
    }
    /**
     * Checks if these two objects are similar (if are of the same type but data,amount and meta).
     * @param obj
     * @return true if similar.
     */
    public boolean similar(Object obj) {
        if(similarButHash(obj)) {
            Price other = (Price)obj;
            return metahash == other.metahash;
        }
        return false;
    }
    
    /**
     * Like similar(), with the addition of data in equality checks.
     * @param obj
     * @return true if similar.
     */
    public boolean similarData(Object obj) {
        if(similar(obj)) {
            return ((Price)obj).data == this.data;
        }
        return false;
    }
    
    /**
     * Like SimilarData, but this one checks if the other object's amount is less or equal than this one.
     * @param obj
     * @return true if compatible.
     */
    public boolean compatible(Object obj) {
        if(similarData(obj)) {
            
            return ((Price)obj).amount <= this.amount;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(similarData(obj)) {
            return amount == ((Price)obj).amount;
        }
        return false;
    }

    public int getMetaHash() {
        return metahash;
    }
}
