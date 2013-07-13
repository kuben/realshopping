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
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.github.kuben.realshopping.listeners.RSPlayerListener;
import com.github.kuben.realshopping.prompts.PromptMaster;
import com.github.kuben.realshopping.exceptions.RealShoppingException;
import com.github.kuben.realshopping.exceptions.RealShoppingException.Type;

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
	
	/*
	 * 
	 * Getters and Setters
	 * 
	 */
	
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

	public void addEntranceExit(Location en, Location ex) throws RealShoppingException{ new EEPair(en, ex, this); }
	public boolean removeEntranceExit(Location en, Location ex){ return RealShopping.removeEntranceExit(this, en, ex); }//TODO add to rsset and rssetstores
	public int clearEntrancesExits(){ return RealShopping.clearEntrancesExits(this); }
	public boolean hasEntrance(Location en){ return RealShopping.hasEntrance(this, en); }
	public boolean hasExit(Location ex){ return RealShopping.hasExit(this, ex); }
	public Location getFirstE(){ return RealShopping.getRandomEntrance(this); }
	public Location getCorrEntrance(Location ex) { return RealShopping.getEntrance(this, ex); }
	public Location getCorrExit(Location en) { return RealShopping.getExit(this, en); }
	
	/*
	 * 
	 * Chest functions
	 * [0] is ID, [1] is data, [2] is amount(0 if full stack)
	 */

	private Map<Location,ArrayList<Integer[]>> chests = new HashMap<Location, ArrayList<Integer[]>>();
	public Map<Location, ArrayList<Integer[]>> getChests(){ return chests; }
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
	public boolean isChest(Location l){ return chests.containsKey(l); }
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
	public boolean setChestContents(Location l, Inventory i){
		if(chests.containsKey(l)){
			if(i != null){
				chests.get(l).clear();
				for(ItemStack iS:i.getContents()){
					if(iS != null){
						int am = iS.getAmount();
						if(am == iS.getType().getMaxStackSize()) am = 0;
						chests.get(l).add(new Integer[]{iS.getTypeId(), (int)iS.getData().getData(), am});
					} else chests.get(l).add(new Integer[]{0, 0, 0});
				}	
			}
		}
		return false;
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
	public int clearChestItems(Location l){
		int j = -1;
		if(chests.containsKey(l)){
			j = chests.get(l).size();
			chests.get(l).clear();
		}
		return j;
	}

	/*
	 * 
	 * Prices
	 * Map stores pennies from 0.44 on
	 */
	
	private Map<Price, Integer[]> prices = new HashMap<Price, Integer[]>();//Price array [0] is price, [1] is min and [2] is maxprice
	
	public boolean hasPrices(){ return !prices.isEmpty(); }
	public boolean hasPrice(Price p) { return prices.containsKey(p); }
        public Integer getPrice(Price p) { Integer[] r = prices.get(p); return (r==null?0:r[0]); }
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
	public Integer getSale(Price p) { 
            if(hasSale(p)) return sale.get(p); 
            return 0;
        }
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
			stat.getItem().toString(stat.getAmount());
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
	
	public static boolean sellToStore(Player p, ItemStack[] iS){
		Shop tempShop = RealShopping.shopMap.get(RealShopping.getPInv(p).getStore());
		if(Config.isEnableSelling() && RealShopping.hasPInv(p) && tempShop.getBuyFor() > 0){
			int payment = 0;
			List<ItemStack> sold = new ArrayList<>();
			for(ItemStack ist:iS){//Calculate cost and check if player owns items
				if(ist != null){
                                    Price itm = new Price(ist);
                                    if(tempShop.hasPrice(itm)){//Something in inventory has a price
                                            int amount = ((RealShopping.isTool(ist.getTypeId()))?RealShopping.getMaxDur(ist.getTypeId()) - ist.getDurability():ist.getAmount());

                                            int soldAm = amount;
                                            for(ItemStack tempSld:sold)
                                                    if(tempSld.getTypeId() == itm.getType()) soldAm += ((RealShopping.isTool(itm.getType()))?RealShopping.getMaxDur(itm.getType()) - ist.getDurability():ist.getAmount());

                                            if(RealShopping.getPInv(p).getAmount(ist) >= soldAm){
                                                    int cost = 0;
                                                    if(tempShop.hasPrice(itm)) cost = tempShop.getPrice(itm);
                                                    //There is a sale on that item.
                                                    int pcnt = 0;
                                                    if(tempShop.hasSale(itm)){
                                                        pcnt = 100 - tempShop.getSale(itm);
                                                        cost *= pcnt/100f;
                                                    }
                                                    cost *= tempShop.getBuyFor()/100f;

                                                    sold.add(ist);
                                                    payment += cost * (RealShopping.isTool(itm.getType())?(double)amount / (double)RealShopping.getMaxDur(itm.getType()):amount);//Convert items durability to item amount
                                            }
                                    }
				}
			}
			boolean cont = false;
			String own = tempShop.getOwner();
			if(!own.equals("@admin")){
				if(RSEconomy.getBalance(own) >= payment/100f){
					RSEconomy.deposit(p.getName(), payment/100f);
					RSEconomy.withdraw(own, payment/100f);//If player owned store, withdraw from owner
                                        if(!sold.isEmpty()) p.sendMessage(ChatColor.GREEN + LangPack.SOLD + sold.size() + LangPack.ITEMSFOR + payment/100f + LangPack.UNIT);
					if(tempShop.allowsNotifications() && !sold.isEmpty()) RealShopping.sendNotification(own, LangPack.YOURSTORE + tempShop.getName() + LangPack.BOUGHTSTUFFFOR + payment/100f + LangPack.UNIT + LangPack.FROM + p.getName());
					for(ItemStack key:sold){
						if(Config.isEnableAI()) tempShop.addStat(new Statistic(new Price(key), key.getAmount(), false));
						RealShopping.getPInv(p).removeItem(key, key.getAmount());
					}
					cont = true;
				} else p.sendMessage(ChatColor.RED + LangPack.OWNER + own + LangPack.CANTAFFORDTOBUYITEMSFROMYOUFOR + payment/100f + LangPack.UNIT);
			} else {
				RSEconomy.deposit(p.getName(), payment/100f);
				p.sendMessage(ChatColor.GREEN + LangPack.SOLD + sold.size() + LangPack.ITEMSFOR + payment/100f + LangPack.UNIT);
				for(ItemStack key:sold){
					RealShopping.getPInv(p).removeItem(key, key.getAmount());
				}
				cont = true;
			}
			if(cont){
	   			if(!own.equals("@admin")){//Return items if player store.
	   				for(int i = 0;i < sold.size();i++){
	    				tempShop.addStolenToClaim(sold.get(i));
	    			}
	    		}
				ItemStack[] newInv = p.getInventory().getContents();
				boolean skip = false;//To save CPU
	    		for(int i = 0;i < iS.length;i++){
	    			if(sold.contains(iS[i])){//Item is sold, do not return to player
	    				sold.remove(iS[i]);
	    			} else {
	    				if(!skip) for(int j = 0;j < newInv.length;j++){
	    					if(newInv[j] == null){
	    						newInv[j] = iS[i];
	    						iS[i] = null;
	    						break;
	    					}
	    				}
	    				if(iS[i] != null){//Item hasn't been returned
	    					skip = true;
	    					p.getWorld().dropItem(p.getLocation(), iS[i]);
	    				}
	    			}
	    		}
	    		p.getInventory().setContents(newInv);
	    		return true;
			}
		}
		return false;
	}
	
	public static boolean prices(CommandSender sender, int page, String store){//In 0.50+ pages start from 1
	    	Shop tempShop = RealShopping.shopMap.get(store);
	    	if(tempShop.hasPrices()){
	    		Map<Price, Integer> tempMap = tempShop.getPrices();
	 			if(!tempMap.isEmpty()){
	 				Price[] keys = tempMap.keySet().toArray(new Price[0]);
	 				if((page-1)*9 < keys.length){//If page exists
	// 					boolean SL = false;
	 					if(tempShop.hasSales()){
	 						sender.sendMessage(ChatColor.GREEN + LangPack.THEREISA + tempShop.getFirstSale() + LangPack.PCNTOFFSALEAT + store);
	// 						SL = true;
	 					}
	 					if(page*9 < keys.length){//Not last
	 		 				for(int i = 9*(page-1);i < 9*page;i++){
	 		 					int cost = tempMap.get(keys[i]);
	 		 					String onSlStr = "";
	 		 					if(tempShop.hasSale(keys[i].stripOffData()) || tempShop.hasSale(keys[i])){//There is a sale on that item.
	 								int pcnt = -1;
	 								if(tempShop.hasSale(keys[i].stripOffData())) pcnt = 100 - tempShop.getSale(keys[i].stripOffData());
	 								if(tempShop.hasSale(keys[i]))  pcnt = 100 - tempShop.getSale(keys[i]);
	 								cost *= pcnt/100f;
	 		 						onSlStr = ChatColor.GREEN + LangPack.ONSALE;
	 		 					}
	 		 					sender.sendMessage(ChatColor.BLUE + "" + keys[i].formattedString() + ChatColor.BLACK + " - " + ChatColor.RED + cost/100f + LangPack.UNIT + onSlStr);
	 		 				}
	 		 				sender.sendMessage(ChatColor.RED + LangPack.MOREITEMSONPAGE + (page + 1));
	 					} else {//Last page
	 		 				for(int i = 9*(page-1);i < keys.length;i++){
	 		 					int cost = tempMap.get(keys[i]);
	 		 					String onSlStr = "";
	 		 					if(tempShop.hasSale(keys[i].stripOffData()) || tempShop.hasSale(keys[i])){//There is a sale on that item.
	 								int pcnt = -1;
	 								if(tempShop.hasSale(keys[i].stripOffData())) pcnt = 100 - tempShop.getSale(keys[i].stripOffData());
	 								if(tempShop.hasSale(keys[i]))  pcnt = 100 - tempShop.getSale(keys[i]);
	 								cost *= pcnt/100f;
	 		 						onSlStr = ChatColor.GREEN + LangPack.ONSALE;
	 		 					}
	 		 					sender.sendMessage(ChatColor.BLUE + "" + keys[i].formattedString() + ChatColor.BLACK + " - " + ChatColor.RED + cost/100f + LangPack.UNIT + onSlStr);
	 		 				}
	 					}
	 				} else {
	 					sender.sendMessage(ChatColor.RED + LangPack.THEREARENTTHATMANYPAGES);
	 				}
	 			} else {
	 				sender.sendMessage(ChatColor.RED + LangPack.THEREARENOPRICESSETFORTHISSTORE);
	 				return true;
	 			}
	    	} else {
					sender.sendMessage(ChatColor.RED + LangPack.THEREARENOPRICESSETFORTHISSTORE);
					return true;
	    	}
	 		return true;
	     }
	
	public static boolean pay(Player player, Inventory[] invs){
		if(RealShopping.hasPInv(player)){
			String shopName = RealShopping.getPInv(player).getStore();
			if(RealShopping.shopMap.get(shopName).hasPrices()) {
				int toPay = RealShopping.getPInv(player).toPay(invs);
				if(toPay==0) return false;
				if(RSEconomy.getBalance(player.getName()) < toPay/100f) {
					player.sendMessage(ChatColor.RED + LangPack.YOUCANTAFFORDTOBUYTHINGSFOR + toPay/100f + LangPack.UNIT);
					return true;
				} else {
					RSEconomy.withdraw(player.getName(), toPay/100f);
					if(!RealShopping.shopMap.get(shopName).getOwner().equals("@admin")){
						RSEconomy.deposit(RealShopping.shopMap.get(shopName).getOwner(), toPay/100f);//If player owned store, pay player
						if(RealShopping.shopMap.get(shopName).allowsNotifications()) RealShopping.sendNotification(RealShopping.shopMap.get(shopName).getOwner(), player.getName()
								+ LangPack.BOUGHTSTUFFFOR + toPay/100f + LangPack.UNIT + LangPack.FROMYOURSTORE + shopName + ".");
					}
					Map<Price, Integer> bought = RealShopping.getPInv(player).getBought(invs);
					
					if(Config.isEnableAI()){
						Price[] keys = bought.keySet().toArray(new Price[0]);
						for(Price key:keys){
							RealShopping.shopMap.get(shopName).addStat(new Statistic(key, bought.get(key), true));
						}
					}
					
					if(invs != null) RealShopping.getPInv(player).update(invs);
					else RealShopping.getPInv(player).update();
					player.sendMessage(ChatColor.GREEN + LangPack.YOUBOUGHTSTUFFFOR + toPay/100f + LangPack.UNIT);
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
		if(RealShopping.hasPInv(player)){
			if(RealShopping.shopMap.size() > 0){
				if(!PromptMaster.isConversing(player) && !RSPlayerListener.hasConversationListener(player)){
					if(RealShopping.getPInv(player).hasPaid() || player.getGameMode() == GameMode.CREATIVE){
						String shopName = RealShopping.getPInv(player).getStore();
						Location l = player.getLocation().getBlock().getLocation().clone();
						if(RealShopping.shopMap.get(shopName).hasExit(l)){
							l = RealShopping.shopMap.get(shopName).getCorrEntrance(l);
							RealShopping.removePInv(player);
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
					player.sendRawMessage(ChatColor.RED + "You can't do this while in a conversation. ");
					player.sendRawMessage("All conversations can be aborted with " + ChatColor.DARK_PURPLE + "quit");//LANG
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
			Location l = player.getLocation().getBlock().getLocation().clone();	
			Shop tempShop = RealShopping.isEntranceTo(l);
			if(!PromptMaster.isConversing(player) && !RSPlayerListener.hasConversationListener(player)){
				if(tempShop != null){//Enter shop
					Location ex = tempShop.getCorrExit(l);
					if(!tempShop.isBanned(player.getName().toLowerCase())){
						player.teleport(ex.add(0.5, 0, 0.5));

						RealShopping.addPInv(new RSPlayerInventory(player, tempShop.getName()));
						player.sendMessage(ChatColor.GREEN + LangPack.YOUENTERED + tempShop.getName());
						
						//Refill chests
						Location[] chestArr = tempShop.getChests().keySet().toArray(new Location[0]);
						for(int i = 0;i < chestArr.length;i++){
							Block tempChest = player.getWorld().getBlockAt(chestArr[i]);
							if(tempChest.getType() != Material.CHEST) tempChest.setType(Material.CHEST);
			             	BlockState blockState = tempChest.getState();
			             	if(blockState instanceof Chest){
			             	    Chest chest = (Chest)blockState;
			             	    chest.getBlockInventory().clear();
			             	    ItemStack[] itemStack = new ItemStack[27];
			             	    int k = 0;
			             	    for(Integer[] j:tempShop.getChests().get(chestArr[i])){
			             	    	itemStack[k] = new MaterialData(j[0],j[1].byteValue())
			             	    		.toItemStack((j[2]==0)?Material.getMaterial(j[0]).getMaxStackSize():j[2]);
			             	    	k++;
			             	    }
			             	    chest.getBlockInventory().setContents(itemStack);
			             	}
						}
						return true;
					} else {
						player.sendMessage(ChatColor.RED + LangPack.YOUAREBANNEDFROM + tempShop.getName());
						return false;
					}
				} else {
					if(cmd) player.sendMessage(ChatColor.RED + LangPack.YOURENOTATTHEENTRANCEOFASTORE);
					return false;
				}
			} else {
				player.sendRawMessage(ChatColor.RED + "You can't do this while in a conversation. ");
				player.sendRawMessage("All conversations can be aborted with " + ChatColor.DARK_PURPLE + "quit");//LANG
				return false;
			}
		} else {
			if(cmd) player.sendMessage(ChatColor.RED + LangPack.THEREARENOSTORESSET);
			return false;
		}
	}
}

final class EEPair {//An entrance and exit
	
	final private Location entrance, exit;

	public EEPair(Location entrance, Location exit, Shop shop) throws RealShoppingException {
		if(entrance == null || exit == null || shop == null) throw new NullPointerException();
		this.entrance = entrance;
		this.exit = exit;
		if(!RealShopping.addEntranceExit(this, shop)) throw new RealShoppingException(Type.EEPAIR_ALREADY_EXISTS);
	}
	
	public boolean hasEntrance(Location en){ return entrance.equals(en); }
	public boolean hasExit(Location ex){ return exit.equals(ex); }
	//Clone because 0.5 will be added
	public Location getEntrance(){ return entrance.clone(); }
	public Location getExit(){ return exit.clone(); }
	
	@Override
	public String toString(){
		return "Entrance: " + entrance + ", exit: " + exit;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((entrance == null) ? 0 : entrance.hashCode());
		result = prime * result + ((exit == null) ? 0 : exit.hashCode());
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
		EEPair other = (EEPair) obj;
		if (entrance == null) {
			if (other.entrance != null)
				return false;
		} else if (!entrance.equals(other.entrance))
			return false;
		if (exit == null) {
			if (other.exit != null)
				return false;
		} else if (!exit.equals(other.exit))
			return false;
		return true;
	}
}

final class Statistic {
	
	final private Price item;
	final private int amount;
	final private long timestamp;
	final private boolean bought;
	
	public Statistic(Price item, int amount, boolean bought){
		this.item = item;
		this.amount = amount;
		this.timestamp = System.currentTimeMillis();
		this.bought = bought;
	}
	
	public Statistic(String imp){
		this.timestamp = Long.parseLong(imp.split("\\[")[0].split(":")[0]);
		this.bought = Boolean.parseBoolean(imp.split("\\[")[0].split(":")[1]);
		Byte data = 0;
		Map<Enchantment, Integer> enchs = new HashMap<>();
		if(imp.split("\\[")[0].split(":").length > 4) data = Byte.parseByte(imp.split("\\[")[0].split(":")[4]);
		for(int i = 1;i < imp.split("\\[").length;i++){
			enchs.put(Enchantment.getById(Integer.parseInt(imp.split("\\[")[i].split("\\]")[0].split(":")[0])), Integer.parseInt(imp.split("\\[")[i].split("\\]")[0].split(":")[1]));
		}
		this.item = new Price(Integer.parseInt(imp.split(":")[2]), data);
		this.amount = Integer.parseInt(imp.split("\\[")[0].split(":")[3]);
	}

	public Price getItem() {
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