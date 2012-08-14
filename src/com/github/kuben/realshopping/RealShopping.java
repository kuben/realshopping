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
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.ItemSpade;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Rails;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.github.kuben.realshopping.*;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class RealShopping extends JavaPlugin {
	
	static String mandir;
	public static Map<String, String> playerMap;
	public static Map<String, Map<Integer, Float>> prices;//Shop name - (id - price, id - price...)
	public static Map<Integer, Integer> maxDurMap;
	public static Map<String, Shop> shopMap;
	public static Map<String, String> playerEntrances;
	public static Map<String, String> playerExits;
	public static Map<String, Location> jailedPlayers;
	public static Map<String, List<ItemStack>> stolenToClaim;
	public static Map<String, List<ItemStack[]>> shippedToCollect;
	
    public static Economy econ;
    static boolean keepstolen;
    static String punishment;
    static String langpack;
    static double pstorecreate;
    static int[] hellLoc;
    static int[] jailLoc;
    static int[] dropLoc;
    String entrance;
    String exit;
	static Logger log;	
	
	boolean smallReload = false;
	
	public static String newUpdate;
	
	public static String unit;
	
    public void onEnable(){
    	mandir = "plugins/RealShopping/";
    	playerMap = new HashMap<String, String>();
    	prices = new HashMap<String, Map<Integer, Float>>();//Shop name - (id - price, id - price...)
    	maxDurMap = new HashMap<Integer, Integer>();
    	shopMap = new HashMap<String, Shop>();
    	playerEntrances = new HashMap<String, String>();
    	playerExits = new HashMap<String, String>();
    	jailedPlayers = new HashMap<String, Location>();
    	stolenToClaim = new HashMap<String, List<ItemStack>>();
    	shippedToCollect = new HashMap<String, List<ItemStack[]>>();
        econ = null;
        keepstolen = false;
        punishment = null;
        langpack = null;
        hellLoc = null;
        jailLoc = null;
        dropLoc = null;
        pstorecreate = 0.0;
        entrance = "";
        exit = "";
    	log = this.getLogger();
    	
    	newUpdate = "";
    	
    	unit = "$";
    	
    	try {
    		double newest = updateCheck(0.30);
    		if(newest > 0.30){
    			newUpdate = "v" + newest + " of RealShopping is available for download. Update for new features and/or bugfixes.";
    			log.info(newUpdate);
    		}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

    	if(setupEconomy()){
    		if(!smallReload){
    			getServer().getPluginManager().registerEvents(new RSPlayerListener(), this);
    			RSCommandExecutor cmdExe = new RSCommandExecutor(this);
    			getCommand("rsenter").setExecutor(cmdExe);
    			getCommand("rsexit").setExecutor(cmdExe);
    			getCommand("rspay").setExecutor(cmdExe);
    			getCommand("rscost").setExecutor(cmdExe);
    			getCommand("rsprices").setExecutor(cmdExe);
    			getCommand("rsstores").setExecutor(cmdExe);
    			getCommand("rsset").setExecutor(cmdExe);
    			getCommand("rssetstores").setExecutor(cmdExe);
    			getCommand("rssetprices").setExecutor(cmdExe);
    			getCommand("rssetchests").setExecutor(cmdExe);
    			getCommand("rsshipped").setExecutor(cmdExe);
    			getCommand("rsunjail").setExecutor(cmdExe);
    			getCommand("rsreload").setExecutor(cmdExe);
    		}
        	hellLoc = new int[3];
    		hellLoc[0] = 0;
    		hellLoc[1] = 0;
    		hellLoc[2] = 0;
        	jailLoc = new int[3];
    		jailLoc[0] = 0;
    		jailLoc[1] = 0;
    		jailLoc[2] = 0;
    		dropLoc = new int[3];
    		dropLoc[0] = 0;
    		dropLoc[1] = 0;
    		dropLoc[2] = 0;
            keepstolen = false;
            langpack = "default";
            punishment = "hell";
            pstorecreate = 0.0;
        	File f = new File(mandir);
        	if(!f.exists()) f.mkdir();
    		FileInputStream fstream;
    		BufferedReader br;
    		try {
    			f = new File(mandir + "realshopping.properties");
    			if(!f.exists()){
    				f.createNewFile();
    				PrintWriter pW = new PrintWriter(f);
    				pW.println("language-pack:default");
    				pW.println("punishment:none");
    				pW.println("keep-stolen-items-after-punish:false");
    				pW.println("jail-location:0,0,0");
    				pW.println("hell-location:0,0,0");
    				pW.println("drop-items-at:0,0,0");
    				pW.println("player-stores-create-cost:0.0");
    				pW.close();
    			} else {
    				fstream = new FileInputStream(f);
    				br = new BufferedReader(new InputStreamReader(fstream));
    				String s;
    				int notInConfig = 127;
    				while ((s = br.readLine()) != null){// Read realshopping.properties
    					if(s.split(":")[0].equals("language-pack")){
    						langpack = s.split(":")[1];
    						notInConfig -= 1;
    					} else if(s.split(":")[0].equals("punishment")){
    						punishment = s.split(":")[1];
    						notInConfig -= 2;
    					} else if(s.split(":")[0].equals("keep-stolen-items-after-punish")){
    						keepstolen = Boolean.parseBoolean(s.split(":")[1]);
    						notInConfig -= 4;
    					} else if(s.split(":")[0].equals("jail-location")){
    						jailLoc[0] = Integer.parseInt(s.split(":")[1].split(",")[0]);
    						jailLoc[1] = Integer.parseInt(s.split(":")[1].split(",")[1]);
    						jailLoc[2] = Integer.parseInt(s.split(":")[1].split(",")[2]);
    						notInConfig -= 8;
    					} else if(s.split(":")[0].equals("hell-location")){
    						hellLoc[0] = Integer.parseInt(s.split(":")[1].split(",")[0]);
    						hellLoc[1] = Integer.parseInt(s.split(":")[1].split(",")[1]);
    						hellLoc[2] = Integer.parseInt(s.split(":")[1].split(",")[2]);
    						notInConfig -= 16;
    					} else if(s.split(":")[0].equals("drop-items-at")){
    						dropLoc[0] = Integer.parseInt(s.split(":")[1].split(",")[0]);
    						dropLoc[1] = Integer.parseInt(s.split(":")[1].split(",")[1]);
    						dropLoc[2] = Integer.parseInt(s.split(":")[1].split(",")[2]);
    						notInConfig -= 32;
    					} else if(s.split(":")[0].equals("player-stores-create-cost")){
    						pstorecreate = Double.parseDouble(s.split(":")[1]);
    						notInConfig -= 64;
    					} else if(s.split(":")[0].equals("enable-hell")){
    						notInConfig += 128;
    					}
    				}
    				fstream.close();
    				br.close();
    				if(notInConfig > 0){
        				PrintWriter pW = new PrintWriter(f);
        				pW.println("language-pack:"+langpack);
        				pW.println("punishment:"+punishment);
        				pW.println("keep-stolen-items-after-punish:"+keepstolen);
        				pW.println("jail-location:"+jailLoc[0]+","+jailLoc[1]+","+jailLoc[2]);
        				pW.println("hell-location:"+hellLoc[0]+","+hellLoc[1]+","+hellLoc[2]);
        				pW.println("drop-items-at:"+dropLoc[0]+","+dropLoc[1]+","+dropLoc[2]);
        				pW.println("player-stores-create-cost:"+pstorecreate);
        				pW.close();
    				}
    			}
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    			log.info("Failed while reading realshopping.properties. Default properties loaded.");
			} catch(IOException e) {
				e.printStackTrace();
    			log.info("Failed while reading realshopping.properties. Default properties loaded.");
			}
    		try {
    			f = new File(mandir + "shops.db");
    			if(!f.exists()){
    				f.createNewFile();
    			} else {
    				fstream = new FileInputStream(f);
    				br = new BufferedReader(new InputStreamReader(fstream));
    				String s;
    				String ss = "";
    				boolean v2plus = false;
    				boolean v3plus = false;
    				while ((s = br.readLine()) != null){// Read shops.db
    					if(s.equals("Shops database for RealShopping v0.20") || s.equals("Shops database for RealShopping v0.21")){
    						v2plus = true;
    					} else if(s.equals("Shops database for RealShopping v0.30")) { 
    						v2plus = true;
    						v3plus = true;
    					} else {
        					String[] tS = s.split(";")[0].split(":");
        		    		shopMap.put(tS[0], new Shop(tS[0], tS[1], v2plus?tS[2]:"@admin"));
        		    		shopMap.get(tS[0]).buyFor = (v3plus)?Integer.parseInt(tS[3]):0;
        					for(int i = v3plus?4:v2plus?3:2;i < tS.length;i++){//The entrances + exits
        						String[] tSS = tS[i].split(",");
        			    		Location en = new Location(getServer().getWorld(tS[1]), Integer.parseInt(tSS[0]),Integer.parseInt(tSS[1]), Integer.parseInt(tSS[2]));
        			    		Location ex = new Location(getServer().getWorld(tS[1]), Integer.parseInt(tSS[3]),Integer.parseInt(tSS[4]), Integer.parseInt(tSS[5]));
        			    		shopMap.get(tS[0]).addE(en, ex);
        					}
        					for(int i = 1;i < s.split(";").length;i++){//There are chests
        						Location l = new Location(getServer().getWorld(tS[1]), Integer.parseInt(s.split(";")[i].split("\\[")[0].split(",")[0])
        														, Integer.parseInt(s.split(";")[i].split("\\[")[0].split(",")[1])
        														, Integer.parseInt(s.split(";")[i].split("\\[")[0].split(",")[2]));
        						shopMap.get(tS[0]).addChest(l);
        						String idS = s.split(";")[i].split("\\[")[1].split("\\]")[0];
        						if(!idS.split(",")[0].trim().equals("")){
        							int[][] ids = new int[idS.split(",").length][2];
        							for(int j = 0;j < ids.length;j++){//The chests
        								if(idS.split(",")[j].contains(":")){
        									ids[j][0] = Integer.parseInt(idS.split(",")[j].split(":")[0].trim());
        									ids[j][1] = Integer.parseInt(idS.split(",")[j].split(":")[1].trim());
        								} else {
        									ids[j][0] = Integer.parseInt(idS.split(",")[j].trim());
        									ids[j][1] = 0;
        								}
        							}
        							shopMap.get(tS[0]).addChestItem(l, ids);
        						}
        					}
        					int bIdx = s.indexOf("BANNED_");
        					if(bIdx > -1){//There are banned players
        						String[] banned = s.substring(bIdx + 7).split(",");
        						for(int i = 0;i < banned.length;i++){
        							shopMap.get(tS[0]).banned.add(banned[i]);
        						}
        					}
    					}
    				}
    				fstream.close();
    				br.close();
    				if(!v3plus)//Needs updating
    					updateEntrancesDb();
    			}
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    			log.info("Failed while reading shops.db");
			} catch(IOException e) {
				e.printStackTrace();
    			log.info("Failed while reading shops.db");
			}
    		try {
    			f = new File(mandir + "prices.xml");
    			if(!f.exists()){
    				f.createNewFile();
    			} else {
    				new PricesParser().parseDocument(f);
    			}
    		} catch (Exception e){
				e.printStackTrace();
    			log.info("Failed while reading prices.xml");
    		}
    		
    		try {
    			f = new File(mandir + "inventories.db");
    			if(f.exists()){
    				fstream = new FileInputStream(f);
    				br = new BufferedReader(new InputStreamReader(fstream));
    				String s;
    				while ((s = br.readLine()) != null){
    					shopMap.get(s.split(";")[0].split("-")[1]).players.put(s.split(";")[0].split("-")[0],(s.split(";").length == 2)?s.split(";")[1]:"");//name - inventory
    					playerMap.put(s.split(";")[0].split("-")[0], s.split(";")[0].split("-")[1]);// name - shop
    				}
    				fstream.close();
    				br.close();
    			}
    			f.delete();
    		} catch (Exception e){
				e.printStackTrace();
    			log.info("Failed while reading inventories.db");
    		}
    		
    		try {
    			f = new File(mandir + "jailed.db");
    			if(f.exists()){
    				fstream = new FileInputStream(f);
    				br = new BufferedReader(new InputStreamReader(fstream));
    				String s;
    				while ((s = br.readLine()) != null){
    					if(s.equals("Jailed players database for RealShopping v0.21") || s.equals("Jailed players database for RealShopping v0.30")){

    					} else {
    						jailedPlayers.put(s.split(";")[0], stringToLoc(s.split(";")[1], s.split(";")[2]));
    					}
    				}
    				fstream.close();
    				br.close();
    			}
    			f.delete();
    		} catch (Exception e){
				e.printStackTrace();
    			log.info("Failed while reading jailed.db");
    		}
    		
    		try {
    			f = new File(mandir + "toclaim.db");
    			if(f.exists()){
    				fstream = new FileInputStream(f);
    				br = new BufferedReader(new InputStreamReader(fstream));
    				String s;
    				while ((s = br.readLine()) != null){
    					stolenToClaim.put(s.split(";")[0], new ArrayList<ItemStack>());
    					for(int i = 1;i < s.split(";").length;i++){
    						stolenToClaim.get(s.split(";")[0]).add(new ItemStack(Integer.parseInt(s.split(";")[i].split(",")[0]),
    								Integer.parseInt(s.split(";")[i].split(",")[1]),
    								Short.parseShort(s.split(";")[i].split(",")[2]),
    								Byte.parseByte(s.split(";")[i].split(",")[3])));
    					}
    				}
    				fstream.close();
    				br.close();
    			}
    			f.delete();
    		} catch (Exception e) {
    			e.printStackTrace();
    			log.info("Failed while reading toclaim.db");
    		}
    		
    		try {
    			f = new File(mandir + "shipped.db");
    			if(f.exists()){
    				fstream = new FileInputStream(f);
    				br = new BufferedReader(new InputStreamReader(fstream));
    				String s;
    				while ((s = br.readLine()) != null){
    					shippedToCollect.put(s.split("\\[\\]")[0], new ArrayList<ItemStack[]>());
    					for(int i = 1;i < s.split("\\[\\]").length;i++){
    						ItemStack[] iS = new ItemStack[27];
    						for(int j = 0;j < iS.length && j < 27;j++){
    							if(s.split("\\[\\]")[i].split(";")[j].equals("null")) iS[j] = null;
    							else iS[j] = new ItemStack(Integer.parseInt(s.split("\\[\\]")[i].split(";")[j].split(",")[0]),
        								Integer.parseInt(s.split("\\[\\]")[i].split(";")[j].split(",")[1]),
        								Short.parseShort(s.split("\\[\\]")[i].split(";")[j].split(",")[2]),
        								Byte.parseByte(s.split("\\[\\]")[i].split(";")[j].split(",")[3]));
    						}
    						shippedToCollect.get(s.split("\\[\\]")[0]).add(iS);
    					}
    				}
    				fstream.close();
    				br.close();
    			}
    			f.delete();
    		} catch (Exception e) {
    			e.printStackTrace();
    			log.info("Failed while reading shipped.db");
    		}
    		
    		f = new File(mandir + "langpacks/");
    		if(!f.exists()) f.mkdir();
        	LangPack.initialize(langpack);
        	unit = LangPack.UNIT;
    		initMaxDur();
    		log.info("RealShopping initialized");
    	} else {
    		log.info("Couldn't initialize RealShopping.");
    	}
        
    }
     
    public void onDisable(){
		try {
			Object[] keys = shopMap.keySet().toArray();
			
			File f = new File(mandir+"inventories.db");
			if(!f.exists()) f.createNewFile();
			PrintWriter pW;
			pW = new PrintWriter(f);
			for(int i = 0;i < keys.length;i++){
				Map inv = shopMap.get(keys[i]).players;
				if(!inv.isEmpty()){
					//Write inventories to file
					Object[] players = inv.keySet().toArray();
					for(int j = 0;j < players.length;j++){
						String s = players[j].toString() + "-" + shopMap.get(keys[i]).name + ";";
						s += inv.get(players[j]);
						pW.println(s);
					}
					
				}
			}
			pW.close();
			
			keys = stolenToClaim.keySet().toArray();
			f = new File(mandir+"toclaim.db");
			if(!f.exists()) f.createNewFile();
			pW = new PrintWriter(f);
			for(int i = 0;i < keys.length;i++){
				List toClaim = stolenToClaim.get(keys[i]);
				if(!toClaim.isEmpty()){
					Object[] iS = toClaim.toArray();
					String s = keys[i].toString();
					for(int j = 0;j < iS.length;j++){
						s += ";" + ((ItemStack) iS[j]).getTypeId() + "," +  ((ItemStack) iS[j]).getAmount() + "," +  ((ItemStack) iS[j]).getDurability() + "," +  ((ItemStack) iS[j]).getData().getData();
					}
					pW.println(s);
				}
			}
			pW.close();
			
			keys = shippedToCollect.keySet().toArray();
			f = new File(mandir+"shipped.db");
			if(!f.exists()) f.createNewFile();
			pW = new PrintWriter(f);
			for(int i = 0;i < keys.length;i++){
				List toCollect = shippedToCollect.get(keys[i]);
				if(!toCollect.isEmpty()){
					Object[] IS = toCollect.toArray();
					String s = keys[i].toString();
					for(int j = 0;j < IS.length;j++){
						ItemStack[] iS = (ItemStack[])IS[j];
						s += "[]";
						for(int k = 0;k < iS.length;k++){
							if(k > 0) s += ";";
							if(iS[k] == null) s += "null";
							else s += iS[k].getTypeId() + "," +  iS[k].getAmount() + "," +  iS[k].getDurability() + "," +  iS[k].getData().getData();
						}
					}
					pW.println(s);
				}
			}
			pW.close();
			
			f = new File(mandir+"jailed.db");
			if(!f.exists()) f.createNewFile();
			pW = new PrintWriter(f);
			keys = jailedPlayers.keySet().toArray();
			for(int i = 0;i < keys.length;i++){
				if(i == 0) pW.println("Jailed players database for RealShopping v0.30");
				pW.println((String)keys[i] + ";" + jailedPlayers.get(keys[i]).getWorld().getName() + ";" + locAsString(jailedPlayers.get(keys[i])));
			}
			pW.close();
			
				//Write prices to xml

				keys = prices.keySet().toArray();
				f = new File(mandir+"prices.xml");//Reset file
				if(f.exists()) f.delete();
				f.createNewFile();
				
				DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
	            Document doc = docBuilder.newDocument();
	            

	            Element root = doc.createElement("prices");
	            doc.appendChild(root);
	            doc.appendChild(doc.createComment("If you want to manually edit this file, do it when your server is down. Your changes won't be saved otherwise!"));
	            
	            Map tempMap;
	            keys = prices.keySet().toArray();
	            for(int i = 0;i < keys.length;i++){	
	            	Element shop = doc.createElement("shop");
	            	shop.setAttribute("name", keys[i].toString());
	            	root.appendChild(shop);
	            	
	            	tempMap = prices.get(keys[i]);
	            	Object[] ids = tempMap.keySet().toArray();
	            	for(int j = 0;j < ids.length;j++){
	            		Element item = doc.createElement("item");
	                    item.setAttribute("id", ids[j].toString());
	                    item.setAttribute("cost", tempMap.get(ids[j]) + "");
	                    shop.appendChild(item);
	            	}
	            }
	            
				OutputFormat format = new OutputFormat(doc);
				format.setIndenting(true);
				XMLSerializer serializer = new XMLSerializer(new FileOutputStream(f), format);
				serializer.serialize(doc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
    	log.info("RealShopping disabled");
    }

    void updateEntrancesDb(){
    	//Update file
		try {
			File f = new File(mandir + "shops.db");
			if(!f.exists()) f.createNewFile();
			PrintWriter pW = new PrintWriter(f);
			Object[] keys = shopMap.keySet().toArray();
			pW.println("Shops database for RealShopping v0.30");
			for(int i = 0;i<keys.length;i++){
				Shop tempShop = shopMap.get(keys[i]);
				pW.print(keys[i] + ":" + tempShop.world + ":" + tempShop.owner + ":" + tempShop.buyFor);
				for(int j = 0;j < tempShop.entrance.size();j++){
					pW.print(":" + locAsString(tempShop.entrance.get(j)) + "," + locAsString(tempShop.exit.get(j)));
				}
				Object[] chestLocs = tempShop.chests.keySet().toArray();
				for(int j = 0;j < chestLocs.length;j++){
					String items = "";
					for(Integer[] ii:tempShop.chests.get(chestLocs[j])){
						if(!items.equals("")) items += ",";
						items += ii[0] + ":" + ii[1];
					}
					pW.print(";" + locAsString((Location) chestLocs[j]) + "[ " + items + "]");
				}
				Object[] banned = tempShop.banned.toArray();
				if(banned.length > 0){
					pW.print("BANNED_");
					for(int j = 0;j < banned.length;j++){
						if(j > 0) pW.print(",");
						pW.print(banned[j]);
					}
					
				}
				pW.println();
			}
			pW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static boolean enter(Player player, boolean cmd){
		if(shopMap.size() > 0){
			boolean containsKey = false;
			int i = 0, j = 0;
			Object[] keys = shopMap.keySet().toArray();
			Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
			for(;i<keys.length;i++){
				if(shopMap.get(keys[i]).entrance.contains(l)){
					j = shopMap.get(keys[i]).entrance.indexOf(l);
					containsKey = true;
					break;
				}
			}
			if(containsKey){//Enter shop
				if(!shopMap.get(keys[i]).banned.contains(player.getName().toLowerCase())) {
					l = shopMap.get(keys[i]).exit.get(j).clone();
					player.teleport(l.add(0.5, 0, 0.5));
					shopMap.get(keys[i]).players.put(player.getName(), createInv(player));
					playerMap.put(player.getName(), shopMap.get(keys[i]).name);
					player.sendMessage(ChatColor.RED + LangPack.YOUENTERED + shopMap.get(keys[i]).name);
					
					//Refill chests
					Object[] chestArr = shopMap.get(keys[i]).chests.keySet().toArray();
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
		             	    for(Integer[] jj:shopMap.get(keys[i]).chests.get((Location) chestArr[ii])){
		             	    	itemStack[k] = new ItemStack(jj[0], Material.getMaterial(jj[0]).getMaxStackSize(), (short)0, jj[1].byteValue());
		             	    	k++;
		             	    }
		             	    chest.getBlockInventory().setContents(itemStack);
		             	}
					}
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You are banned from " + keys[i]);
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
    
    public static boolean exit(Player player, boolean cmd){
		if(playerMap.containsKey(player.getName())){
			if(shopMap.size() > 0){
				if(hasPaid(player) || player.getGameMode() == GameMode.CREATIVE){
					String shopName = playerMap.get(player.getName());
					Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
					if(shopMap.get(shopName).exit.contains(l)){
						l = shopMap.get(shopName).entrance.get(shopMap.get(shopName).exit.indexOf(l)).clone();
						if(shopMap.get(playerMap.get(player.getName())).sellToStore.containsKey(player.getName()))
							shopMap.get(playerMap.get(player.getName())).sellToStore.remove(player.getName());
						shopMap.get(shopName).players.remove(player.getName());
						playerMap.remove(player.getName());
						player.teleport(l.add(0.5, 0, 0.5));
						player.sendMessage(ChatColor.RED + LangPack.YOULEFT + shopName);
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
    
    public static boolean pay(Player player, Inventory[] invs){
		if(playerMap.containsKey(player.getName())){
			if(prices.containsKey(playerMap.get(player.getName())))
			if(!prices.get(playerMap.get(player.getName())).isEmpty()){
				float toPay = cost(player, invs);
				if(econ.getBalance(player.getName()) < toPay) {
					player.sendMessage(ChatColor.RED + LangPack.YOUCANTAFFORDTOBUYTHINGSFOR + toPay + unit);
					return true;
				} else {
					econ.withdrawPlayer(player.getName(), toPay);
					if(!shopMap.get(playerMap.get(player.getName())).owner.equals("@admin")) econ.depositPlayer(shopMap.get(playerMap.get(player.getName())).owner, toPay);//If player owned store, pay player
					String tempInv = createInv(player);
					String tempCInv;
					if(invs != null)
						for(int i = 0;i < invs.length;i++){
							tempCInv = createInv(invs[i]);
							tempInv += tempCInv.equals("")?"":","+tempCInv;
						}
					shopMap.get(playerMap.get(player.getName())).players.put(player.getName(),tempInv);
					player.sendMessage(ChatColor.RED + LangPack.YOUBOUGHTSTUFFFOR + toPay + unit);
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + LangPack.THEREARENOPRICESSETFORTHISSTORE);
				return true;
			}
		} else {
			player.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
			return false;
		}
		return false;
	}
    
    public static boolean hasPaid(Player p){
		String[] inv = createInv(p).split(",");
		Map<Integer, Integer> newInv = createInvOneOccur(inv);
		
		inv = shopMap.get(playerMap.get(p.getName())).players.get(p.getName()).split(",");
		Map<Integer, Integer> oldInv = createInvOneOccur(inv);
		
		Object[] keys = newInv.keySet().toArray();
		boolean hasPaid = true;
		if(prices.containsKey(playerMap.get(p.getName())))//If there are prices for store.
			for(int j = 0;j < keys.length;j++){
				int key = Integer.parseInt(keys[j].toString());
				if(prices.get(playerMap.get(p.getName())).containsKey(key))//If item has price
					if(oldInv.containsKey(key)){
						if(newInv.get(key) > oldInv.get(key))
							hasPaid = false;
					} else hasPaid = false;
			}
		return hasPaid;
    }
    
    public static float cost(Player p, Inventory[] invs){
		String[] inv = shopMap.get(playerMap.get(p.getName())).players.get(p.getName()).split(",");
		Map<Integer, Integer> oldInv = createInvOneOccur(inv);
		
		inv = createInv(p).split(",");
		String tempInv;
		if(invs != null)
			for(int i = 0;i < invs.length;i++){
				tempInv = createInv(invs[i]);
				if(!tempInv.equals("")) inv = (String[]) ArrayUtils.addAll(inv, tempInv.split(","));
			}
		Map<Integer, Integer> newInv = createInvOneOccur(inv);
		
		float toPay = 0;
		Object[] keys = newInv.keySet().toArray();
		for(int i = 0;i < keys.length;i++){
			int type = (Integer) keys[i];
			if(prices.containsKey(playerMap.get(p.getName())))
			if(prices.get(playerMap.get(p.getName())).containsKey(type)){//Something in inventory has a price
				int amount = newInv.get(type);
				float cost = prices.get(playerMap.get(p.getName())).get(type);
				if(shopMap.get(playerMap.get(p.getName())).sale.containsKey(type)){//There is a sale on that item.
					int pcnt = 100 - shopMap.get(playerMap.get(p.getName())).sale.get(type);
					cost *= pcnt;
					cost = Math.round(cost);
					cost /= 100;
				}
				if(oldInv.containsKey(type)) {
					int oldAm = oldInv.get(type);
					if(oldAm > amount){//More items before than now
						amount = 0;
					} else {//More items now
						amount -= oldAm;
					}
				}
				toPay += cost * (maxDurMap.containsKey(type)?Math.ceil((double)amount / (double)maxDurMap.get(type)):amount);
			}
		}

		return toPay;
    }
    
    public static boolean addItemToSell(Player p, ItemStack iS){
    	if(playerMap.containsKey(p.getName())){
    		if(shopMap.get(playerMap.get(p.getName())).buyFor > 0 && prices.containsKey(playerMap.get(p.getName())) && prices.get(playerMap.get(p.getName())).size() > 0){
    			if(prices.get(playerMap.get(p.getName())).containsKey(iS.getTypeId())){
    				if(shopMap.get(playerMap.get(p.getName())).players.containsKey(p.getName()) && !shopMap.get(playerMap.get(p.getName())).players.get(p.getName()).equals("")){
    					if(!shopMap.get(playerMap.get(p.getName())).players.get(p.getName()).equals("")){
    						Map<Integer, Integer> pItems = createInvOneOccur(shopMap.get(playerMap.get(p.getName())).players.get(p.getName()).split(","));
    						if(pItems.containsKey(iS.getTypeId())){
    							int ownedAm = pItems.get(iS.getTypeId());
    							int sellAm = (maxDurMap.containsKey(iS.getTypeId()))?maxDurMap.get(iS.getTypeId()) - iS.getDurability():iS.getAmount();
    							if(sellAm < ownedAm){
    								if(!shopMap.get(playerMap.get(p.getName())).sellToStore.containsKey(p.getName()))
    									shopMap.get(playerMap.get(p.getName())).sellToStore.put(p.getName(), new ArrayList<ItemStack>());
    								shopMap.get(playerMap.get(p.getName())).sellToStore.get(p.getName()).add(iS);
    								pItems.put(iS.getTypeId(), ownedAm - sellAm);
    							} else if(sellAm == ownedAm){
    								if(!shopMap.get(playerMap.get(p.getName())).sellToStore.containsKey(p.getName()))
    									shopMap.get(playerMap.get(p.getName())).sellToStore.put(p.getName(), new ArrayList<ItemStack>());
    								shopMap.get(playerMap.get(p.getName())).sellToStore.get(p.getName()).add(iS);
    								pItems.remove(iS.getTypeId());//Simply remove because no more items left
    							} else {
    								p.sendMessage("You don't own the item(s) you want to sell.");
    								return false;
    							}
    						//	Update player inv var
    							String pInv = "";
    							Object[] keys = pItems.keySet().toArray();
    							for(int i = 0;i < keys.length;i++){
    								pInv += "," + keys[i] + ":" + pItems.get(keys[i]);
    							}
    							if(!pInv.equals("")) pInv = pInv.substring(1);		
    							shopMap.get(playerMap.get(p.getName())).players.put(p.getName(), pInv);
    							p.sendMessage(RealShopping.formatItemStackToMess(new ItemStack[]{iS}) + " added to to-sell list. Confirm sale by right-clicking the same block with no items. Cancel by left-clicking.");
    							return true;
    						} else p.sendMessage("You don't own the item(s) you want to sell.");
    					} else p.sendMessage("You don't own the item(s) you want to sell.");
    				} else p.sendMessage("You don't own the item(s) you want to sell.");
    			} else p.sendMessage("This store doesn't buy " + iS.getType());
    		} else p.sendMessage("This store doesn't buy any items.");
    	} else p.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
    	return false;
    }
    
    public static boolean cancelToSell(Player p){
    	if(shopMap.get(playerMap.get(p.getName())).sellToStore.containsKey(p.getName())){
			//Update player inv var
    		List<ItemStack> pList = shopMap.get(playerMap.get(p.getName())).sellToStore.get(p.getName());
			String pInv = shopMap.get(playerMap.get(p.getName())).players.get(p.getName());
			for(int i = 0;i < pList.size();i++){
				pInv += "," + pList.get(i).getTypeId() + ":" + ((maxDurMap.containsKey(pList.get(i).getTypeId()))?maxDurMap.get(pList.get(i).getTypeId()) - pList.get(i).getDurability():pList.get(i).getAmount());
			}
			if(!shopMap.get(playerMap.get(p.getName())).players.get(p.getName()).equals("")) pInv = pInv.substring(1);		
			shopMap.get(playerMap.get(p.getName())).players.put(p.getName(), pInv);
			shopMap.get(playerMap.get(p.getName())).sellToStore.remove(p.getName());
			p.sendMessage("Cancelled selling items.");
			return true;
    	}
    	return false;
    }
    
    public static boolean confirmToSell(Player p){
    	if(shopMap.get(playerMap.get(p.getName())).buyFor > 0){
    		if(shopMap.get(playerMap.get(p.getName())).sellToStore.containsKey(p.getName())){
    			List<ItemStack> sList = shopMap.get(playerMap.get(p.getName())).sellToStore.get(p.getName());
    			Object[] pInv = ArrayUtils.addAll(p.getInventory().getContents(), p.getInventory().getArmorContents());
    			for(int i = 0;i < sList.size();i++){
    				boolean cont = false;
    				for(int j = 0;j < pInv.length;j++){
    					if(pInv[j] != null){
    						if(((ItemStack)pInv[j]).equals(sList.get(i))){
    							pInv[j] = null;
    							cont = true;
    							break;
    						}
    					} else cont = true;
    				}
    				if(!cont){
    					shopMap.get(playerMap.get(p.getName())).sellToStore.remove(p.getName());
    					p.sendMessage("You don't have all the items you were going to sell. Cancelled sale.");
    					return false;
    				}
    			}
    			float payment = 0;
    			for(int i = 0;i < sList.size();i++){
    				int type = sList.get(i).getTypeId();
    				if(prices.containsKey(playerMap.get(p.getName())))
    					if(prices.get(playerMap.get(p.getName())).containsKey(type)){//Something in inventory has a price
    						int amount = ((maxDurMap.containsKey(type))?maxDurMap.get(type) - sList.get(i).getDurability():sList.get(i).getAmount());
    						float cost = prices.get(playerMap.get(p.getName())).get(type);
    						if(shopMap.get(playerMap.get(p.getName())).sale.containsKey(type)){//There is a sale on that item.
    							int pcnt = 100 - shopMap.get(playerMap.get(p.getName())).sale.get(type);
    							cost *= pcnt;
    							cost = Math.round(cost);
    							cost /= 100;
    						}
    						cost *= shopMap.get(playerMap.get(p.getName())).buyFor;
							cost = Math.round(cost);
							cost /= 100;
							
    						payment += cost * amount;
    					}
    			}
    			boolean cont = false;
    			if(!shopMap.get(playerMap.get(p.getName())).owner.equals("@admin")){
    				if(econ.getBalance(shopMap.get(playerMap.get(p.getName())).owner) >= payment){
    					econ.depositPlayer(p.getName(), payment);
    					econ.withdrawPlayer(shopMap.get(playerMap.get(p.getName())).owner, payment);//If player owned store, pay player
    					p.sendMessage("Sold " + sList.size() + " items for " + payment + unit);
    					cont = true;
    				}
    			} else {
					econ.depositPlayer(p.getName(), payment);
					p.sendMessage("Sold " + sList.size() + " items for " + payment + unit);
					cont = true;
    			}
    			if(cont){
    				ItemStack[] newInv = (ItemStack[])(p.getInventory().getContents());
    				ItemStack[] newArm = (ItemStack[])(p.getInventory().getArmorContents());
        			for(int i = 0;i < sList.size();i++){
        				for(int j = 0;j < newInv.length;j++){
        					if(newInv[j] != null){
        						if(sList.get(i).equals(newInv[j])){
        							newInv[j] = null;
        							break;
        						}
        					}
        				}
        				for(int j = 0;j < newArm.length;j++){
        					if(newArm[j] != null){
        						if(sList.get(i).equals(newArm[j])){
        							newArm[j] = null;
        							break;
        						}
        					}
        				}
        			}
        			p.getInventory().setContents(newInv);
        			p.getInventory().setArmorContents(newArm);
        			
        			String own = shopMap.get(playerMap.get(p.getName())).owner;
        			if(!own.equals("@admin")){//Return items if player store.
        				if(!stolenToClaim.containsKey(own)) stolenToClaim.put(own, new ArrayList<ItemStack>());
        				for(int i = 0;i < sList.size();i++){
        					ItemStack tempIS = sList.get(i);
        					stolenToClaim.get(own).add(tempIS);
        				}
        			}
        			return true;
    			}
    		}
    	}
    	return false;
    }
    
    static StorageMinecart[] checkForCarts(Location l){
    	Location[] aL = new Location[]{	l.clone().subtract(0, 1, 0)		// [6][7][8]
    									, l.clone().subtract(-1, 1, 0)	// [5][0][1]
										, l.clone().subtract(-1, 1, -1)	// [4][3][2]
										, l.clone().subtract(0, 1, -1)
										, l.clone().subtract(1, 1, -1)
										, l.clone().subtract(1, 1, 0)
										, l.clone().subtract(1, 1, 1)
										, l.clone().subtract(0, 1, 1)
										, l.clone().subtract(-1, 1, 1)};
    	Block[] b = new Block[]{	aL[0].getBlock()
									, aL[1].getBlock()
    								, aL[2].getBlock()
    								, aL[3].getBlock()
    								, aL[4].getBlock()
    								, aL[5].getBlock()
    								, aL[6].getBlock()
    								, aL[7].getBlock()
    								, aL[8].getBlock()};
    	Object[] mcArr = l.getWorld().getEntitiesByClass(StorageMinecart.class).toArray();
    	String rails = "";
    	for(int i = 0;i < b.length;i++){//Get rails in area
    		if(b[i].getType() == Material.RAILS || b[i].getType() == Material.POWERED_RAIL || b[i].getType() == Material.DETECTOR_RAIL){
    			rails += "," + i;
    		}
    	}
    	StorageMinecart firstCart = null;//First cart found on first block
    	if(!rails.equals("")){
    		for(int j = 1;j < rails.split(",").length;j++){//Repeat for every rail in area until a minecart is found
    			int areaInt = Integer.parseInt(rails.split(",")[j]);
        		int k = 0;
        		boolean hasCart = false;
        		for(;k < mcArr.length;k++){//Repeat until a minecart is found on the block, or no minecarts are found
        			if(((StorageMinecart)mcArr[k]).getLocation().getBlock().getLocation().equals(aL[areaInt])){//If minecart is on the rail
        				hasCart = true;
        				break;
        			}
        		}
    			if(hasCart){
    				firstCart = (StorageMinecart)mcArr[k];
    				break;
    			}
    		}
    	}
    	if(firstCart == null) return new StorageMinecart[0];
    	else return new StorageMinecart[]{firstCart};
    }
    
	public static boolean shipCartContents(StorageMinecart sM, Player p) {
		//Get bought items in cart
		String[] inv = shopMap.get(playerMap.get(p.getName())).players.get(p.getName()).split(",");//All items belonging to player
		Map<Integer, Integer> oldInv = createInvOneOccur(inv);
		
		inv = createInv(sM.getInventory()).split(",");//Items in shopping cart
		Map<Integer, Integer> newInv = createInvOneOccur(inv);
		
		Map<Integer, Integer> bought = new HashMap<Integer, Integer>();
		Map<Integer, Integer> bought2 = new HashMap<Integer, Integer>();
		
		if(!oldInv.isEmpty()){
			Object[] keys = newInv.keySet().toArray();
			for(int i = 0;i < keys.length;i++){
				int type = (Integer) keys[i];
				if(prices.containsKey(playerMap.get(p.getName())))
				if(prices.get(playerMap.get(p.getName())).containsKey(type)){//Something in inventory has a price
					int amount = newInv.get(type);

					if(bought.containsKey(type)){
						bought.put(type, amount + bought.get(type));
						bought2.put(type, amount + bought.get(type));
					} else {
						bought.put(type, amount);
						bought2.put(type, amount);
					}
					if(oldInv.containsKey(type)){
						if(bought.get(type) > oldInv.get(type)){
							bought.put(type, oldInv.get(type));
							bought2.put(type, oldInv.get(type));
						}
					} else {
						log.info("Error #803");
						bought.remove(type);
						bought2.remove(type);
					}
				}
			}
			
			if(bought.isEmpty()){
				p.sendMessage("You can't ship an empty cart.");
				return true;
			}
			
			//Remove stolen items from carts inventory
			ItemStack[] cartInv = sM.getInventory().getContents();
			ItemStack[] newCartInv = new ItemStack[cartInv.length];
			ItemStack[] boughtIS = new ItemStack[cartInv.length];
			for(int i = 0;i < cartInv.length;i++){
				ItemStack x = cartInv[i];
				if(x != null)
					if(bought.containsKey(x.getTypeId())){//Has bought item
						int diff = bought.get(x.getTypeId()) - (maxDurMap.containsKey(x.getTypeId())?maxDurMap.get(x.getTypeId()) - x.getDurability():x.getAmount());
						if(diff >= 0){//If + then even more stolen left
							bought.put(x.getTypeId(), diff);
							boughtIS[i] = x.clone();
							x = null;
						} else {//If negative then no more stolen thing in inventory
							if(maxDurMap.containsKey(x.getTypeId())){
								x.setDurability((short)(x.getDurability() + bought.get(x.getTypeId())));
								boughtIS[i] = new ItemStack(x.getTypeId());
								boughtIS[i].setDurability(bought.get(x.getTypeId()).shortValue());
							} else {
								x.setAmount(x.getAmount() - bought.get(x.getTypeId()));
								boughtIS[i] = new ItemStack(x.getTypeId(), bought.get(x.getTypeId()));
							}
							bought.remove(x.getTypeId());
						}
					}
				newCartInv[i] = x;
			}
			sM.getInventory().setContents(newCartInv);
			
//			if(!bought.isEmpty()){ log.info("Error #802"); System.out.println(bought);}
			
			//Ship
			if(!shippedToCollect.containsKey(p.getName()))
				shippedToCollect.put(p.getName(), new ArrayList<ItemStack[]>());
			shippedToCollect.get(p.getName()).add(boughtIS);
			
			p.sendMessage(ChatColor.GREEN + "Package waiting to be delivered. Use /rsshipped to pick up the package.");
			
			//Update player inv var
			keys = oldInv.keySet().toArray();
			for(int i = 0;i < keys.length;i++){
				int type = (Integer) keys[i];
				if(bought2.containsKey(type)){
					if(bought2.get(type) < oldInv.get(type)){
						oldInv.put(type, oldInv.get(type) - bought2.get(type));
					} else {
						oldInv.remove(type);
					}
				}
			}
			String pInv = "";
			keys = oldInv.keySet().toArray();
			for(int i = 0;i < keys.length;i++){
				pInv += "," + keys[i] + ":" + oldInv.get(keys[i]);
			}
			if(!pInv.equals("")) pInv = pInv.substring(1);		
			shopMap.get(playerMap.get(p.getName())).players.put(p.getName(), pInv);
		} else p.sendMessage("You haven't bought anything.");

		return true;
	}

	public static boolean collectShipped(Location l, Player p, int id) {
		if(l.getBlock().getState() instanceof Chest){
			if(shippedToCollect.containsKey(p.getName())){
				if(shippedToCollect.get(p.getName()).size() >= id){
					((Chest)l.getBlock().getState()).getBlockInventory().setContents(shippedToCollect.get(p.getName()).get(id - 1));
					shippedToCollect.get(p.getName()).remove(id - 1);
					return true;
				} else p.sendMessage("There's no package with the id " + id);
			} else p.sendMessage("You haven't got any items waiting to be delivered.");
		} else p.sendMessage("The block you are standing on isn't a chest.");
		return false;
	}

	public static String formatItemStackToMess(ItemStack[] IS){
		String str = "";
		int newLn = 0;
		for(ItemStack iS:IS){
			if(iS != null){
				String tempStr = "[" + ChatColor.RED + iS.getType() + (maxDurMap.containsKey(iS.getTypeId())
								?ChatColor.RESET + " with " + ChatColor.GREEN + (maxDurMap.get(iS.getTypeId()) - iS.getDurability())
								+ ChatColor.RESET +  "/" + ChatColor.GREEN + maxDurMap.get(iS.getTypeId()) + ChatColor.RESET + " uses left" + "] "
								:ChatColor.RESET + " * " + ChatColor.GREEN + iS.getAmount() + ChatColor.RESET + "] " + ChatColor.BLACK + ChatColor.RESET);
				if((str + tempStr).substring(newLn).length() > 96){//84+12
					str += "\n";
					newLn = str.length();
					str += tempStr;
				} else str += tempStr;
			}
		}
		return str;
	}
    
    public static boolean prices(CommandSender sender, int page, String store, boolean cmd){
    	if(prices.containsKey(store)){
    		Map<Integer, Float> tempMap = prices.get(store);
 			if(!tempMap.isEmpty()){
 				Object[] keys = tempMap.keySet().toArray();
 				if(page*9 < keys.length){//If page exists
 					boolean SL = false;
 					if(!shopMap.get(store).sale.isEmpty()){
 						sender.sendMessage(ChatColor.GREEN + "There is a " + shopMap.get(store).sale.values().toArray()[0] + "% off sale at " + store);
 						SL = true;
 					}
 					if((page+1)*9 < keys.length){//Not last
 		 				for(int i = 9*page;i < 9*(page+1);i++){
 		 					float cost = tempMap.get(keys[i]);
 		 					String onSlStr = "";
 		 					if(shopMap.get(store).sale.containsKey(keys[i])){//There is a sale on that item.
 		 						int pcnt = 100 - shopMap.get(store).sale.get(keys[i]);
 		 						cost *= pcnt;
 		 						cost = Math.round(cost);
 		 						cost /= 100;
 		 						onSlStr = ChatColor.GREEN + " ON SALE!";
 		 					}
 		 					sender.sendMessage(ChatColor.BLUE + "" + keys[i] + " " + Material.getMaterial(Integer.parseInt(keys[i] + "")) + ChatColor.BLACK + " - " + ChatColor.RED + cost + unit + onSlStr);
 		 				}
 		 				sender.sendMessage(ChatColor.RED + LangPack.MOREITEMSONPAGE + (page + 2));
 					} else {//Last page
 		 				for(int i = 9*page;i < keys.length;i++){
 		 					float cost = tempMap.get(keys[i]);
 		 					String onSlStr = "";
 		 					if(shopMap.get(store).sale.containsKey(keys[i])){//There is a sale on that item.
 		 						int pcnt = 100 - shopMap.get(store).sale.get(keys[i]);
 		 						cost *= pcnt;
 		 						cost = Math.round(cost);
 		 						cost /= 100;
 		 						onSlStr = ChatColor.GREEN + " ON SALE!";
 		 					}
 		 					sender.sendMessage(ChatColor.BLUE + "" + keys[i] + " " + Material.getMaterial(Integer.parseInt(keys[i] + "")) + ChatColor.BLACK + " - " + ChatColor.RED + cost + unit + onSlStr);
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
    
    public static void punish(Player p){
    	if(punishment.equalsIgnoreCase("hell")){
    		if(!keepstolen){
    			returnStolen(p);
    		}
    		shopMap.get(playerMap.get(p.getName())).players.remove(p.getName());
			if(shopMap.get(playerMap.get(p.getName())).sellToStore.containsKey(p.getName()))
				shopMap.get(playerMap.get(p.getName())).sellToStore.remove(p.getName());
			playerMap.remove(p.getName());
        	p.sendMessage(ChatColor.RED + LangPack.TRYINGTOCHEATYOURWAYOUT);
        	log.info(p.getName() + LangPack.TRIEDTOSTEALFROMTHESTORE);

        	Location dropL = new Location(p.getWorld(),dropLoc[0], dropLoc[1], dropLoc[2]);
        	Location dropL2 = new Location(p.getWorld(),dropLoc[0] + 1, dropLoc[1], dropLoc[2]);
         	Location loc = new Location(p.getWorld(),hellLoc[0] + 0.5, hellLoc[1], hellLoc[2] + 0.5);
         	if(p.teleport(loc)){
        		log.info(p.getName() + LangPack.WASTELEPORTEDTOHELL);
        		p.sendMessage(ChatColor.RED + LangPack.HAVEFUNINHELL);
             	Block block = p.getWorld().getBlockAt(dropL);
             	if(block.getType() != Material.CHEST) block.setType(Material.CHEST);
             	BlockState blockState = block.getState();
             	
             	block = p.getWorld().getBlockAt(dropL2);
             	if(block.getType() != Material.CHEST) block.setType(Material.CHEST);

             	BlockState blockState2 = block.getState();
             	ItemStack[] pS = p.getInventory().getContents();
             	if(blockState instanceof Chest)
             	{
             	    Chest chest = (Chest)blockState;
             	    ItemStack[] iS = new ItemStack[27];
             	    for (int i = 0;i<17;i++) {
             	    	iS[i] = pS[i+9];
             	    }
             	    chest.getBlockInventory().clear();
             	    chest.getBlockInventory().setContents(iS);
             	}
             	if(blockState2 instanceof Chest)
             	{
             	   Chest chest = (Chest)blockState;
             	   ItemStack[] iS = new ItemStack[9];
             	   for (int i = 0;i<9;i++) {
             		   iS[i] = pS[i];
             	   }
             	   chest.getBlockInventory().clear();
             	   chest.getBlockInventory().setContents(iS);
             	   chest.getBlockInventory().addItem(p.getInventory().getArmorContents());
             	}
             	p.getInventory().clear();
             	p.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
        	} else {
        		p.getInventory().clear();
        		p.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
        	} 	
    	} else if(punishment.equalsIgnoreCase("jail")){
    		if(!keepstolen){
    			returnStolen(p);
    		}
			jailedPlayers.put(p.getName(), shopMap.get(playerMap.get(p.getName())).entrance.get(0));
    		shopMap.get(playerMap.get(p.getName())).players.remove(p.getName());
			if(shopMap.get(playerMap.get(p.getName())).sellToStore.containsKey(p.getName()))
				shopMap.get(playerMap.get(p.getName())).sellToStore.remove(p.getName());
    		playerMap.remove(p.getName());
        	p.sendMessage(ChatColor.RED + LangPack.TRYINGTOCHEATYOURWAYOUT);
        	log.info(p.getName() + LangPack.TRIEDTOSTEALFROMTHESTORE);

         	Location loc = new Location(p.getWorld(),jailLoc[0] + 0.5, jailLoc[1], jailLoc[2] + 0.5);
         	if(p.teleport(loc)) {
         		p.sendMessage(LangPack.YOUWEREJAILED);
         		log.info(p.getName() + LangPack.WASJAILED);
         	}
    	} else if(punishment.equalsIgnoreCase("none")){
    		if(!keepstolen){
    			returnStolen(p);
    		}
    	}
    }
    
    static void returnStolen(Player p){
		//Get stolen items
		String[] inv = shopMap.get(playerMap.get(p.getName())).players.get(p.getName()).split(",");//Items player had in inventory when entering OR inv + items in carts
		Map<Integer, Integer> oldInv = createInvOneOccur(inv);
		
		inv = createInv(p).split(",");//Items in players inventory
		Map<Integer, Integer> newInv = createInvOneOccur(inv);
		
		Map<Integer, Integer> stolen = new HashMap<Integer, Integer>();
		Map<Integer, Integer> stolen2 =  new HashMap<Integer, Integer>();
		
		Object[] keys = newInv.keySet().toArray();
		for(int i = 0;i < keys.length;i++){
			int type = (Integer) keys[i];
			if(prices.containsKey(playerMap.get(p.getName())))
			if(prices.get(playerMap.get(p.getName())).containsKey(type)){//Something in inventory has a price
				int amount = newInv.get(type);
				if(oldInv.containsKey(type)) {
					int oldAm = oldInv.get(type);
					if(oldAm > amount){//More items before than now
						amount = 0;
					} else {//More items now
						amount -= oldAm;
					}
				}
				if(stolen.containsKey(type)){
					stolen.put(type, amount + stolen.get(type));
					stolen2.put(type, amount + stolen.get(type));
				}
				else{
					stolen.put(type, amount);
					stolen2.put(type, amount);
				}
			}
		}

		//Remove stolen items from players inventory
		ItemStack[][] playerInv = new ItemStack[][]{p.getInventory().getContents(), p.getInventory().getArmorContents()};
		ItemStack[][] newPlayerInv = new ItemStack[][]{new ItemStack[playerInv[0].length], new ItemStack[playerInv[1].length]};
		for(int j = 0;j < 2;j ++)
		for(int i = 0;i < playerInv[j].length;i++){
			ItemStack x = playerInv[j][i];
			if(x != null)
				if(stolen.containsKey(x.getTypeId())){//Has stolen item
					int diff = stolen.get(x.getTypeId()) - (maxDurMap.containsKey(x.getTypeId())?maxDurMap.get(x.getTypeId()) - x.getDurability():x.getAmount());
					if(diff > 0){//If + then even more stolen left
						stolen.put(x.getTypeId(), diff);
						x = null;
					} else {//If negative then no more stolen thing in inventory
						if(maxDurMap.containsKey(x.getTypeId())){
							x.setDurability((short)(x.getDurability() + stolen.get(x.getTypeId())));
						} else {
							x.setAmount(x.getAmount() - stolen.get(x.getTypeId()));
						}
						stolen.remove(x.getTypeId());
					}
				}
			newPlayerInv[j][i] = x;
		}
		p.getInventory().setContents(newPlayerInv[0]);
		p.getInventory().setArmorContents(newPlayerInv[1]);
		
		String own = shopMap.get(playerMap.get(p.getName())).owner;
		if(!own.equals("@admin")){//Return items if player store.
			if(!stolenToClaim.containsKey(own)) stolenToClaim.put(own, new ArrayList<ItemStack>());
			Object[] keyss = stolen2.keySet().toArray();
			for(int i = 0;i < keyss.length;i++){
				int type = (Integer) keyss[i];
				ItemStack tempIS = new ItemStack(type);
				if(maxDurMap.containsKey(type)){
					if(stolen2.get(type) > maxDurMap.get(type))
						while(maxDurMap.get(type) < stolen2.get(type)){//If more than one stack/full tool
							stolenToClaim.get(own).add(new ItemStack(type));
							stolen2.put(type, stolen2.get(type) - maxDurMap.get(type));
						}
					tempIS.setDurability((short) (maxDurMap.get(type) - stolen2.get(type)));
					stolenToClaim.get(own).add(tempIS);
				} else {
					if(stolen2.get(type) > Material.getMaterial(type).getMaxStackSize())
						while(Material.getMaterial(type).getMaxStackSize() < stolen2.get(type)){//If more than one stack/full tool
							stolenToClaim.get(own).add(new ItemStack(type, Material.getMaterial(type).getMaxStackSize()));
							stolen2.put(type, stolen2.get(type) - Material.getMaterial(type).getMaxStackSize());
						}
					tempIS.setAmount(stolen.get(type));
					stolenToClaim.get(own).add(tempIS);
				}
			}
		}
    }
    
    static String createInv(Player player){
		ItemStack[] items = (ItemStack[]) ArrayUtils.addAll(player.getInventory().getContents(), player.getInventory().getArmorContents());
		String str = "";
		for(int j = 0;j < items.length;j++){
			if(items[j] != null){
				int type = items[j].getTypeId();
				if(maxDurMap.containsKey(type)) str += ","+ type + ":" + (maxDurMap.get(type) - items[j].getDurability());//If tool, id:uses left
				else if(items[j].getTypeId() != 0) str += ","+ items[j].getTypeId() + ":"+ items[j].getAmount();
			}
		}
		if(!str.equals("")) str = str.substring(1);
		return str;
    }

    static String createInv(Inventory inv){
		return createInv(inv.getContents());
    }
    
    static String createInv(ItemStack[] items){
		String str = "";
		for(int j = 0;j < items.length;j++){
			if(items[j] != null){
				int type = items[j].getTypeId();
				if(maxDurMap.containsKey(type)) str += ","+ type + ":" + (maxDurMap.get(type) - items[j].getDurability());//If tool, id:uses left
				else if(items[j].getTypeId() != 0) str += ","+ items[j].getTypeId() + ":"+ items[j].getAmount();
			}
		}
		if(!str.equals("")) str = str.substring(1);
		return str;
    }
    
    static Map<Integer, Integer> createInvOneOccur(String[] inv){
    	//Creates a HashMap with each item in the players inventory occurring only once. The value is the amount of all items together.
		Map<Integer, Integer> invOO = new HashMap<Integer, Integer>();
		if(!inv[0].equals(""))//If inv not empty
			for(int j = 0;j < inv.length;j++){
				int type = Integer.parseInt(inv[j].split(":")[0]);
				int amount;
				amount = Integer.parseInt(inv[j].split(":")[1]);
				if(invOO.containsKey(type)) invOO.put(type, amount + invOO.get(type));
				else invOO.put(type, amount);
			}
		return invOO;
    }
    
    private void initMaxDur(){
    	maxDurMap.put(256,251);//Iron tools
   		maxDurMap.put(257,251);
   		maxDurMap.put(258,251);
   		maxDurMap.put(267,251);
    	maxDurMap.put(292,251);
    	maxDurMap.put(268,60);//Wooden tools
    	maxDurMap.put(269,60);
    	maxDurMap.put(270,60);
    	maxDurMap.put(271,60);
    	maxDurMap.put(290,60);
    	maxDurMap.put(272,132);//Stone tools
    	maxDurMap.put(273,132);
    	maxDurMap.put(274,132);
    	maxDurMap.put(275,132);
    	maxDurMap.put(291,132);
    	maxDurMap.put(276,1562);//Diamond tools
    	maxDurMap.put(277,1562);
    	maxDurMap.put(278,1562);
    	maxDurMap.put(279,1562);
    	maxDurMap.put(293,1562);
    	maxDurMap.put(283,33);//Gold tools
    	maxDurMap.put(284,33);
    	maxDurMap.put(285,33);
    	maxDurMap.put(286,33);
    	maxDurMap.put(294,33);
    	maxDurMap.put(298,56);//Leather
    	maxDurMap.put(299,82);
    	maxDurMap.put(300,76);
    	maxDurMap.put(301,66);
    	maxDurMap.put(302,78);//Chainmail
    	maxDurMap.put(303,114);
    	maxDurMap.put(304,106);
    	maxDurMap.put(305,92);
    	maxDurMap.put(306,166);//Iron
    	maxDurMap.put(307,242);
    	maxDurMap.put(308,226);
    	maxDurMap.put(309,296);
    	maxDurMap.put(310,364);//Diamond
    	maxDurMap.put(311,529);
    	maxDurMap.put(312,496);
    	maxDurMap.put(313,430);
    	maxDurMap.put(314,78);//Gold
    	maxDurMap.put(315,114);
    	maxDurMap.put(316,106);
    	maxDurMap.put(317,92);
    	maxDurMap.put(346,65);//Fishing rod
    	maxDurMap.put(359,239);//Shears
    	maxDurMap.put(259,65);//Flint and steel
    	maxDurMap.put(261,385);//Bow
    }
    
	public String locAsString(Location l){
		return l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
	}
	
	public Location stringToLoc(String world, String s){
		return new Location(getServer().getWorld(world), Double.parseDouble(s.split(",")[0].trim()), Double.parseDouble(s.split(",")[1].trim()), Double.parseDouble(s.split(",")[2].trim()));
	}
	
    private boolean setupEconomy() {
    	try{
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                econ = economyProvider.getProvider();
            }

            
    	} catch (NoClassDefFoundError e) {
    		log.info("You need vault for this plugin to work.");
    		return false;
    	}
    	return (econ != null);
    }
    
    public void reload(){
		smallReload = true;
		onDisable();
		onEnable();
    }
    
    public double updateCheck(double currentVersion) throws Exception {
        try {
            URL url = new URL("http://dev.bukkit.org/server-mods/realshopping/files.rss");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("item");
            Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) {
                Element firstElement = (Element)firstNode;
                NodeList firstElementTagName = firstElement.getElementsByTagName("title");
                Element firstNameElement = (Element) firstElementTagName.item(0);
                NodeList firstNodes = firstNameElement.getChildNodes();
                return Double.valueOf(firstNodes.item(0).getNodeValue().replace("v", "").trim());
            }
        }
        catch (Exception localException) {
        }
        return currentVersion;
    }
}

class PricesParser extends DefaultHandler {
	int index = -1;
	List<Map<Integer, Float>> mapList = new ArrayList<Map<Integer, Float>>();
	List<String> shopList = new ArrayList<String>();
	
	 void parseDocument(File f) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(f, this);
		} catch(SAXException se) {
			se.printStackTrace();
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	//Event Handlers
	public void startElement(String a, String b, String name, Attributes attr) throws SAXException {
		if(name.equalsIgnoreCase("shop")){
			shopList.add(attr.getValue("name"));
			mapList.add(new HashMap());
			index ++;
		} else if(name.equalsIgnoreCase("item")){
			mapList.get(index).put(Integer.parseInt(attr.getValue("id")), Float.parseFloat(attr.getValue("cost")));
		}
	}

	public void endElement(String a, String b, String name) throws SAXException {
		if (name.equalsIgnoreCase("prices")){
			for(int i = 0;i<shopList.size();i++){
				RealShopping.prices.put(shopList.get(i), mapList.get(i));
			}
		}
	}
}