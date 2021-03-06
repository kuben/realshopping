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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;

public class RSEconomy {

    private static Economy econ;//Vault economy
	private static Map<String, Long> accounts = new HashMap<>();//Stores pennies
	//Simple and lightweight economy

	public static double getBalance(String p){
		if(econ != null) return econ.getBalance(p);
		if(accounts.containsKey(p)) return accounts.get(p).doubleValue()/100;
		else {
			accounts.put(p, 0l);
			return 0;
		}
	}
	
	public static boolean withdraw(String p, double amount){
		if(econ != null) return econ.withdrawPlayer(p, amount).transactionSuccess();
		if(accounts.containsKey(p))
			if(accounts.get(p) >= amount * 100){
				accounts.put(p, accounts.get(p) - (long)(amount * 100));
				return true;
			} else return false;
		else {
			accounts.put(p, 0l);
			return false;
		}
	}
	
	public static boolean deposit(String p, float amount){
		if(econ != null) return econ.depositPlayer(p, amount).transactionSuccess();
		if(accounts.containsKey(p)){
			accounts.put(p, accounts.get(p) + (long)(amount * 100));
			return true;
		} else {
			accounts.put(p, (long)amount * 100);
			return true;
		}
	}
	
	
    public static boolean setupEconomy() {
    	econ = null;
    	try{
            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                econ = economyProvider.getProvider();
            }

            
    	} catch (NoClassDefFoundError e) {
    		
    	}
    	
    	if(econ == null) {
    		RealShopping.loginfo("Vault/Economy plugin not found. Initializing internal economy.");
    		
    		File f;
    		FileInputStream fstream;
    		BufferedReader br;
    		try {
    			f = new File(RealShopping.MANDIR + "econ.db");
    			if(f.exists()){
    				fstream = new FileInputStream(f);
    				br = new BufferedReader(new InputStreamReader(fstream));
    				String s;
    				while ((s = br.readLine()) != null){
    					if(!s.equals("Internal Economy for RealShopping"))
    						accounts.put(s.split(":")[0], Long.parseLong(s.split(":")[1]));//Name - Pinv
    				}
    				fstream.close();
    				br.close();
    			}
    			f.delete();
    		} catch (Exception e){
				e.printStackTrace();
    			RealShopping.loginfo("Failed while reading econ.db");
    		}
    		return false;
    	} else return true;
    }
    
    public static void export(){
    	if(econ == null && !accounts.isEmpty()){
        	String[] keys = accounts.keySet().toArray(new String[0]);
        	File f = new File(RealShopping.MANDIR + "econ.db");
        	
			if(!f.exists())
				try {
					f.createNewFile();
					PrintWriter pW;
					pW = new PrintWriter(f);
					pW.println("Internal Economy for RealShopping");
		        	for(String s:keys){
		        		pW.println(s + ":" + accounts.get(s));
		        	}
		        	pW.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    	}
    }
}

class StatUpdater extends Thread {

    public static Map<String, Map<Integer, Integer>> provMap = new HashMap<String, Map<Integer, Integer>>();
    public boolean running;

    public StatUpdater(){
        running = true;
    }

    public void run(){
        try {
            while(running && Config.isEnableAI()){
                updateStats();
                Thread.sleep(Math.max(Config.getUpdateFreq() * 1000, 60000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void updateStats(){
        long tStamp = System.nanoTime();
        Map<Integer, TreeMap<String, Integer>> statsMap = new HashMap<Integer, TreeMap<String, Integer>>();
        for(Shop shop:RealShopping.getShops()){
            for(Statistic stat:shop.getStats()){
                if((System.currentTimeMillis()/1000) - Config.getStatTimespan() < stat.getTime()/1000 && stat.isBought()){//Only past <timespan> and only bought
                    if(!statsMap.containsKey(stat.getItem().getType())) statsMap.put(stat.getItem().getType(), new TreeMap<String, Integer>());
                    if(!statsMap.get(stat.getItem().getType()).containsKey(shop.getName())) statsMap.get(stat.getItem().getType()).put(shop.getName(), 0);
                    statsMap.get(stat.getItem().getType()).put(shop.getName(), statsMap.get(stat.getItem().getType()).get(shop.getName()) + stat.getAmount());
                } else if((System.currentTimeMillis()/1000) - Math.max(Config.getCleanStatsOld(), Math.max(Config.getStatTimespan(), Config.getUpdateFreq())) > stat.getTime()/1000){
                    shop.removeStat(stat);	
                }
            }
        }
        Integer[] keys2 = statsMap.keySet().toArray(new Integer[0]);
        for(int i = 0;i < keys2.length;i++){//Sort
            StatComparator bvc = new StatComparator(statsMap.get(keys2[i]));
            TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
            sorted_map.putAll(statsMap.get(keys2[i]));
            statsMap.put(keys2[i], sorted_map);
        }

        Map<String, Map<Integer, Integer>> oldProvMap = new HashMap<String, Map<Integer, Integer>>(provMap);
        provMap.clear();
        for(Shop shop:RealShopping.getShops()){
            keys2 = statsMap.keySet().toArray(new Integer[0]);
            for(Integer o:keys2){
                String[] keys3 = statsMap.get(o).keySet().toArray(new String[0]);//Names of the stores
                int i = 0;
                boolean exists = false;
                for(;i < keys3.length;i++){
                    if(keys3[i].equals(shop.getName())){
                        exists = true;
                        break;
                    }
                }
                if(exists){
                    if(!provMap.containsKey(shop.getName())) provMap.put(shop.getName(), new HashMap<Integer, Integer>());
                    provMap.get(shop.getName()).put(o, i + 1);
                }
            }
        }
        if(oldProvMap != null){
            for(String s:oldProvMap.keySet().toArray(new String[0])){
                Shop tempShop = RealShopping.getShop(s);
                if(tempShop != null && tempShop.getOwner().equals("@admin")){
                    String owner = tempShop.getOwner();
                    if(provMap.containsKey(s)){
                        PSetting sett = RealShopping.getPlayerSettings(owner); 
                        if(sett.getAINotifications(tempShop)){//If AI Notifications on for store
                            for(int i:oldProvMap.get(s).keySet().toArray(new Integer[0])){
                                if(provMap.get(s).containsKey(i)){
                                    int diff = oldProvMap.get(s).get(i) - provMap.get(s).get(i);
                                    String sinceStr;
                                    if(Config.getStatTimespan() == 3600) sinceStr =  LangPack.THELASTHOUR;
                                    else if(Config.getStatTimespan() == 86400) sinceStr =  LangPack.YESTERDAY;
                                    else if(Config.getStatTimespan() == 604800) sinceStr =  LangPack.LASTWEEK;
                                    else if(Config.getStatTimespan() == 2592000) sinceStr =  LangPack.LASTMONTH;
                                    else sinceStr = new Date(Config.getStatTimespan()).toString();
                                    if(diff >= sett.AINotsMinStep(tempShop)) RealShopping.sendNotification(tempShop.getOwner(), LangPack.YOURSTORE + s + LangPack.ISNOWTHE
                                            + RSUtils.formatNum(provMap.get(s).get(i)) + " (" + ChatColor.GREEN + "+" + diff + ChatColor.RESET + LangPack.SINCE+ sinceStr + ")" + LangPack.PROVIDEROF + Material.getMaterial(i));
                                    else if(diff <= sett.AINotsMinStep(tempShop)*-1) RealShopping.sendNotification(tempShop.getOwner(), LangPack.YOURSTORE + s + LangPack.ISNOWTHE
                                            + RSUtils.formatNum(provMap.get(s).get(i)) + " (" + ChatColor.RED + diff + ChatColor.RESET + LangPack.SINCE + sinceStr + ")" + LangPack.PROVIDEROF + Material.getMaterial(i));
                                    if(sett.getChangePricesOnAI(tempShop) && diff != 0){//If change PrintPrices on AI
                                        if(tempShop.hasPrice(new Price(i))){
                                            int newPrice = (int) (tempShop.getCostPerUnit(new Price(i)));
                                            newPrice *= (diff >= sett.AINotsMinStep(tempShop))?1f + (sett.AINotsMinStep(tempShop) / 100f):1f - (sett.AINotsMinStep(tempShop) / 100f);
                                            if(tempShop.hasMinMax(new Price(i))){
                                                if(newPrice > tempShop.getMax(new Price(i))) newPrice = tempShop.getMax(new Price(i));
                                                else if(newPrice < tempShop.getMin(new Price(i))) newPrice = tempShop.getMin(new Price(i));
                                            }
                                            if(newPrice != tempShop.getCostPerUnit(new Price(i))){
                                                tempShop.setCost(new Price(i), newPrice);
                                                if(diff >= sett.AINotsMinStep(tempShop)) RealShopping.sendNotification(tempShop.getOwner(), LangPack.RAISEDTHEPRICEFOR+ Material.getMaterial(i) + LangPack.BY
                                                        + sett.changeOnAIPercentage(tempShop) + "%" +LangPack.TO + newPrice);
                                                else if(diff <= sett.AINotsMinStep(tempShop)*-1) RealShopping.sendNotification(tempShop.getOwner(), LangPack.LOWEREDTHEPRICEFOR + Material.getMaterial(i) + LangPack.BY
                                                        + sett.changeOnAIPercentage(tempShop) + "%" + LangPack.TO + newPrice);	
                                            }
                                        }
                                    }
                                } else {
                                    RealShopping.sendNotification(tempShop.getOwner(), LangPack.YOURSTORE + s + LangPack.WENTFROMBEINGTHE
                                            + RSUtils.formatNum(oldProvMap.get(s).get(i)) + LangPack.PROVIDEROF + Material.getMaterial(i) + LangPack.TONOTSELLINGANY);
                                }
                            }
                        }
                    } else {
                        //nothing sold
                    }
                }
            }
        }

        if(Config.debug) RealShopping.loginfo(System.nanoTime() - tStamp + "ns");
    }

}