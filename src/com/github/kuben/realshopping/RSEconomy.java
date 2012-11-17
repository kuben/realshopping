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
	private static Map<String, Long> accounts = new HashMap<String, Long>();//Stores pennies
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
    		RealShopping.log.info("Vault/Economy plugin not found. Initializing internal economy.");
    		
    		File f;
    		FileInputStream fstream;
    		BufferedReader br;
    		try {
    			f = new File(RealShopping.mandir + "econ.db");
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
    			RealShopping.log.info("Failed while reading econ.db");
    		}
    		return false;
    	} else return true;
    }
    
    public static void export(){
    	if(econ == null && !accounts.isEmpty()){
        	String[] keys = accounts.keySet().toArray(new String[0]);
        	File f = new File(RealShopping.mandir + "econ.db");
        	
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
			while(running){
				updateStats();
				Thread.sleep(Math.max(Config.updateFreq * 1000, 60000));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateStats(){
		long tStamp = System.nanoTime();
		Map<Integer, TreeMap<String, Integer>> statsMap = new HashMap<Integer, TreeMap<String, Integer>>();
		String[] keys = RealShopping.shopMap.keySet().toArray(new String[0]);
		for(String s:keys){
			Statistic[] sKeys = RealShopping.shopMap.get(s).stats.toArray(new Statistic[0]);
			for(Statistic stat:sKeys){
				if((System.currentTimeMillis()/1000) - Config.statTimespan * 1000< stat.getTime()/1000 && stat.isBought()){//Only past <timespan> and only bought
					if(!statsMap.containsKey(stat.getItem().type)) statsMap.put(stat.getItem().type, new TreeMap<String, Integer>());
					if(!statsMap.get(stat.getItem().type).containsKey(s)) statsMap.get(stat.getItem().type).put(s, 0);
					statsMap.get(stat.getItem().type).put(s, statsMap.get(stat.getItem().type).get(s) + stat.getAmount());
				} else
				if((System.currentTimeMillis()/1000) - Config.cleanStatsOld > stat.getTime()/1000){
					RealShopping.shopMap.get(s).stats.remove(stat);	
				}
			}
		}
		Object[] keys2 = statsMap.keySet().toArray();
		for(int i = 0;i < keys2.length;i++){//Sort
			StatComparator bvc = new StatComparator(statsMap.get(keys2[i]));
			TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
			sorted_map.putAll(statsMap.get(keys2[i]));
			statsMap.put((Integer)keys2[i], sorted_map);
		}
		
		System.out.println(statsMap);
	
		Map<String, Map<Integer, Integer>> oldProvMap = new HashMap<String, Map<Integer, Integer>>(provMap);
		provMap.clear();
		for(String s:keys){
			keys2 = statsMap.keySet().toArray();
			for(Object o:keys2){
				Object[] keys3 = statsMap.get((Integer)o).keySet().toArray();
				int i = 0;
				boolean exists = false;
				for(;i < keys3.length;i++){
					if(keys3[i].equals(s)){
						exists = true;
						break;
					}
				}
				if(exists){
					if(!provMap.containsKey(s)) provMap.put(s, new HashMap<Integer, Integer>());
					provMap.get(s).put((Integer)o, i + 1);
				}
			}
		}
		if(oldProvMap != null){
			System.out.println(oldProvMap);
			System.out.println(provMap);
			
			keys = oldProvMap.keySet().toArray(new String[0]);
			for(String s:keys){
				if(RealShopping.shopMap.containsKey(s) && !RealShopping.shopMap.get(s).owner.equals("@admin")){
					Shop tempShop = RealShopping.shopMap.get(s);
					if(provMap.containsKey(s)){
						if(tempShop.notifyChanges > 0){
							Integer[] iKeys = oldProvMap.get(s).keySet().toArray(new Integer[0]);
							for(int i:iKeys){
								if(provMap.get(s).containsKey(i)){
									int diff = oldProvMap.get(s).get(i) - provMap.get(s).get(i);
									String sinceStr;
									if(Config.statTimespan == 3600) sinceStr =  "the last hour";
									else if(Config.statTimespan == 86400) sinceStr =  " yesterday";
									else if(Config.statTimespan == 604800) sinceStr =  "last week";
									else if(Config.statTimespan == 2592000) sinceStr =  "last month";
									else sinceStr = new Date(Config.statTimespan).toString();
									if(diff >= tempShop.changeTreshold) RealShopping.sendNotification(tempShop.owner, "Your store " + s + " is now the "
									+ Utils.formatNum(provMap.get(s).get(i)) + " (" + ChatColor.GREEN + "+" + diff + ChatColor.RESET + " since " + sinceStr +") provider of " + Material.getMaterial(i));
									else if(diff <= tempShop.changeTreshold*-1) RealShopping.sendNotification(tempShop.owner, "Your store " + s + " is now the "
									+ Utils.formatNum(provMap.get(s).get(i)) + " (" + ChatColor.RED + diff + ChatColor.RESET + " since " + sinceStr +") provider of " + Material.getMaterial(i));
									if(tempShop.notifyChanges == 2 && diff != 0){
										if(tempShop.prices.containsKey(new Price(i))){
											int tempPrice = (int) (tempShop.prices.get(new Price(i)) * 100);
											tempPrice *= (diff >= tempShop.changeTreshold)?1f + (tempShop.changePercent / 100f):1f - (tempShop.changePercent / 100f);
											float newPrice = tempPrice / 100f;
											tempShop.prices.put(new Price(i), newPrice);
											if(diff >= tempShop.changeTreshold) RealShopping.sendNotification(tempShop.owner, "Raised the price for " + Material.getMaterial(i) + " by "
												+ tempShop.changePercent + "% to "+ newPrice);
											else if(diff <= tempShop.changeTreshold*-1) RealShopping.sendNotification(tempShop.owner, "Lowered the price for " + Material.getMaterial(i) + " by "
												+ tempShop.changePercent + "% to "+ newPrice);
										}
									}
								} else {
									RealShopping.sendNotification(tempShop.owner, "Your store " + s + " went from being the  " + oldProvMap.get(s).get(i)
											+ " th provider of " + Material.getMaterial(i) + " to not selling any.");
								}
							}
						}
					} else {
						//nothing sold
					}
				}
			}
		}
		
		System.out.println(System.nanoTime() - tStamp + "ns");
	}
	
}