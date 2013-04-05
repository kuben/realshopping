package com.github.kuben.realshopping;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ShippedPackage{
	
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
		String s = LangPack.SHIPPEDPACKAGESENT + new Date(dateSent) + LangPack.FROM
				+ locationSent.getBlockX() + "," + locationSent.getBlockY() + "," + locationSent.getBlockZ()
				+ LangPack.INWORLD + locationSent.getWorld() + LangPack.WITH;
		for(ItemStack iS:contents){
			if(iS != null) s += iS + ", ";
		}
		return s;
	}
}