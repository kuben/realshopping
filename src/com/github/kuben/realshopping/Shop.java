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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

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
	
	//[0] is ID, [1] is data, [2] is amount(0 if full stack)
	private Map<Location,ArrayList<Integer[]>> chests = new HashMap<Location, ArrayList<Integer[]>>();
	public Map<Location, ArrayList<Integer[]>> getChests(){ return chests; }
//	public ArrayList<Integer[]> getChest(Location l){ return chests.get(l); }No function is using this

	public boolean addChest(Location l){
		if(!chests.containsKey(l)){
			chests.put(l, new ArrayList<Integer[]>());
			if(Config.isAutoprotect()) protectedChests.add(l);
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
	
	//Stores pennies from 0.44 on
	private Map<Price, Integer[]> prices = new HashMap<Price, Integer[]>();//Price array [0] is price, [1] is min and [2] is maxprice
	
	public boolean hasPrices(){ return !prices.isEmpty(); }
	public boolean hasPrice(Price p) { return prices.containsKey(p); }
	public Integer getPrice(Price p) { Integer[] r = prices.get(p); return (r==null?null:r[0]); }
	public Map<Price, Integer> getPrices() {
		Map<Price, Integer> temp = new HashMap<Price, Integer>();
		for(Price p:prices.keySet().toArray(new Price[0]))
			temp.put(p, prices.get(p)[0]);
		return temp;
	}
	public Map<Price, Integer[]> getPricesMap(){ return prices; }
	public Integer setPrice(Price p, Integer i) { Integer[] r = prices.put(p, new Integer[]{i}); return (r==null?null:r[0]); }
	public boolean removePrice(Price p) { return prices.remove(p) != null; }
	
	public Integer getMin(Price p) { if(prices.containsKey(p) && prices.get(p).length == 3) return prices.get(p)[1]; return null; }
	public Integer getMax(Price p) { if(prices.containsKey(p) && prices.get(p).length == 3) return prices.get(p)[2]; return null; }
	public boolean hasMinMax(Price p) { return (prices.containsKey(p) && prices.get(p).length == 3); }
	public boolean setMinMax(Price p, Integer min, Integer max){
		if(prices.containsKey(p)){
			prices.put(p, new Integer[]{getPrice(p), min, max});
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
		prices = new HashMap<Price, Integer[]>(RealShopping.shopMap.get(store).prices);
		return true;
	}
	public void setPrices(Map<Price, Integer[]> prices) { this.prices = prices; }
	
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
	public ItemStack claimStolenToClaim() {
		if(!stolenToClaim.isEmpty()){
			ItemStack tempIs = stolenToClaim.get(0);
			stolenToClaim.remove(tempIs);
			return tempIs;
		}
		return null;
	}
	
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
	
	private Map<Price, Integer[]> getLowestPrices(){
		Map<Price, Integer[]> tempMap = new HashMap<Price, Integer[]>();
		String[] keys = RealShopping.shopMap.keySet().toArray(new String[0]);
		for(String s:keys){
			if(!s.equals(name)){
				Price[] keys2 = RealShopping.shopMap.get(s).getPrices().keySet().toArray(new Price[0]);
				for(Price p:keys2){
					if(tempMap.containsKey(p)){
						if(tempMap.get(p)[0] > RealShopping.shopMap.get(s).getPrice(p)) tempMap.put(p, new Integer[]{RealShopping.shopMap.get(s).getPrice(p)});
					} else
						tempMap.put(p, new Integer[]{RealShopping.shopMap.get(s).getPrice(p)});
				}
			}
		}
		return tempMap;
	}

	/*
	 * 
	 * Static Methods
	 * 
	 */
	
	public static boolean pay(Player player, Inventory[] invs){
		if(RealShopping.PInvMap.containsKey(player.getName())){
			String shopName = RealShopping.PInvMap.get(player.getName()).getStore();
			if(RealShopping.shopMap.get(shopName).hasPrices()) {
				int toPay = RealShopping.PInvMap.get(player.getName()).toPay(invs);
				if(toPay==0) return false;
				if(RSEconomy.getBalance(player.getName()) < toPay/100f) {
					player.sendMessage(ChatColor.RED + LangPack.YOUCANTAFFORDTOBUYTHINGSFOR + toPay/100f + RealShopping.unit);
					return true;
				} else {
					RSEconomy.withdraw(player.getName(), toPay/100f);
					if(!RealShopping.shopMap.get(shopName).getOwner().equals("@admin")){
						RSEconomy.deposit(RealShopping.shopMap.get(shopName).getOwner(), toPay/100f);//If player owned store, pay player
						if(RealShopping.shopMap.get(shopName).allowsNotifications()) RealShopping.sendNotification(RealShopping.shopMap.get(shopName).getOwner(), player.getName()
								+ LangPack.BOUGHTSTUFFFOR + toPay/100f + LangPack.UNIT + LangPack.FROMYOURSTORE + shopName + ".");
					}
					Map<PItem, Integer> bought = RealShopping.PInvMap.get(player.getName()).getBought(invs);
					
					if(Config.isEnableAI()){
						PItem[] keys = bought.keySet().toArray(new PItem[0]);
						for(PItem key:keys){
							RealShopping.shopMap.get(shopName).addStat(new Statistic(key, bought.get(key), true));
						}
					}
					
					if(invs != null) RealShopping.PInvMap.get(player.getName()).update(invs);
					else RealShopping.PInvMap.get(player.getName()).update();
					player.sendMessage(ChatColor.GREEN + LangPack.YOUBOUGHTSTUFFFOR + toPay/100f + RealShopping.unit);
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + LangPack.THEREARENOPRICESSETFORTHISSTORE);
				return true;
			}
		} else {
			player.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
		}
		return false;
	}
	public static boolean exit(Player player, boolean cmd){
		if(RealShopping.PInvMap.containsKey(player.getName())){
			if(RealShopping.shopMap.size() > 0){
				if(RealShopping.PInvMap.get(player.getName()).hasPaid() || player.getGameMode() == GameMode.CREATIVE){
					String shopName = RealShopping.PInvMap.get(player.getName()).getStore();
					Location l = player.getLocation().getBlock().getLocation().clone();
					if(RealShopping.shopMap.get(shopName).hasExit(l)){
						l = RealShopping.shopMap.get(shopName).getCorrEntrance(l);
						RealShopping.PInvMap.remove(player.getName());
						player.teleport(l.add(0.5, 0, 0.5));
						player.sendMessage(ChatColor.GREEN + LangPack.YOULEFT + shopName);
						return true;
					} else {
						if(cmd)	player.sendMessage(ChatColor.RED + LangPack.YOURENOTATTHEEXITOFASTORE);
						return false;
					}
	
				} else {
					player.sendMessage(ChatColor.RED + LangPack.YOUHAVENTPAIDFORALLYOURARTICLES);
					return false;
				}
			} else {
				player.sendMessage(ChatColor.RED + LangPack.THEREARENOSTORESSET);
				return false;
			}
		} else {
			player.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
			return false;
		}
	}
	public static boolean enter(Player player, boolean cmd){
		if(RealShopping.shopMap.size() > 0){
			int i = 0;
			Object[] keys = RealShopping.shopMap.keySet().toArray();
			Location l = player.getLocation().getBlock().getLocation().clone();
			Location ex = null;
			for(;i<keys.length;i++){
				if(RealShopping.shopMap.get(keys[i]).hasEntrance(l)){
					ex = RealShopping.shopMap.get(keys[i]).getCorrExit(l);
					break;
				}
			}
			if(ex != null){//Enter shop
				if(!RealShopping.shopMap.get(keys[i]).isBanned(player.getName().toLowerCase())) {
					player.teleport(ex.add(0.5, 0, 0.5));
					
					RealShopping.PInvMap.put(player.getName(), new RSPlayerInventory(player, (String) keys[i]));
					player.sendMessage(ChatColor.GREEN + LangPack.YOUENTERED + RealShopping.shopMap.get(keys[i]).getName());
					
					//Refill chests
					Object[] chestArr = RealShopping.shopMap.get(keys[i]).getChests().keySet().toArray();
					for(int ii = 0;ii < chestArr.length;ii++){
						Block tempChest = player.getWorld().getBlockAt((Location) chestArr[ii]);
						if(tempChest.getType() != Material.CHEST) tempChest.setType(Material.CHEST);
		             	BlockState blockState = tempChest.getState();
		             	if(blockState instanceof Chest)
		             	{
		             	    Chest chest = (Chest)blockState;
		             	    chest.getBlockInventory().clear();
		             	    ItemStack[] itemStack = new ItemStack[27];
		             	    int k = 0;
		             	    for(Integer[] jj:RealShopping.shopMap.get(keys[i]).getChests().get((Location) chestArr[ii])){
		             	    	itemStack[k] = new MaterialData(jj[0],jj[1].byteValue())
		             	    		.toItemStack((jj[2]==0)?Material.getMaterial(jj[0]).getMaxStackSize():jj[0]);
		             	    	k++;
		             	    }
		             	    chest.getBlockInventory().setContents(itemStack);
		             	}
					}
					return true;
				} else {
					player.sendMessage(ChatColor.RED + LangPack.YOUAREBANNEDFROM + keys[i]);
					return false;
				}
			} else {
				if(cmd){
					player.sendMessage(ChatColor.RED + LangPack.YOURENOTATTHEENTRANCEOFASTORE);
				}
				return false;
			}
		} else {
			if(cmd){
				player.sendMessage(ChatColor.RED + LangPack.THEREARENOSTORESSET);
			}
			return false;
		}
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