package com.github.kuben.realshopping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class RSEconomy {

    private static Economy econ;//Vault economy
	private static Map<String, Long> accounts = new HashMap<String, Long>();//Stores pennies
	//Simple and lightweight economy

	public static double getBalance(String p){
		if(econ != null) return econ.getBalance(p);
		if(accounts.containsKey(p)) return accounts.get(p).doubleValue()/100;
		else return -1;
	}
	
	public static boolean withdraw(String p, double amount){
		if(econ != null) return econ.withdrawPlayer(p, amount).transactionSuccess();
		if(accounts.containsKey(p) && accounts.get(p) >= amount * 100){
			accounts.put(p, accounts.get(p) - (long)(amount * 100));
			return true;
		} else return false;
	}
	
	public static boolean deposit(String p, float amount){
		if(econ != null) return econ.depositPlayer(p, amount).transactionSuccess();
		if(accounts.containsKey(p)){
			accounts.put(p, accounts.get(p) + (long)(amount * 100));
			return true;
		} else return false;
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    }
}

class EconAI {
	
}