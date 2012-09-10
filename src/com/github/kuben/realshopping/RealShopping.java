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
	
	public static Map<String, RSPlayerInventory> PInvMap = new HashMap<String, RSPlayerInventory>();
	
	static String mandir;
	public static Map<Integer, Integer> maxDurMap;
	public static Map<String, Shop> shopMap;
	public static Map<String, String> playerEntrances;
	public static Map<String, String> playerExits;
	public static Map<String, Location> jailedPlayers;
	public static Map<String, List<ItemStack>> stolenToClaim;
	public static Map<String, List<ItemStack[]>> shippedToCollect;
	public static Map<Location, Integer> forbiddenTpLocs;
	
	public static Set<Integer> forbiddenInStore;
	
	public static boolean tpLocBlacklist;
	
    public static Economy econ;

    String entrance;
    String exit;
	static Logger log;	
	
	boolean smallReload = false;
	
	public static String newUpdate;
	
	public static String unit;
	
    public void onEnable(){
    	mandir = "plugins/RealShopping/";
    	PInvMap = new HashMap<String, RSPlayerInventory>();
    	maxDurMap = new HashMap<Integer, Integer>();
    	shopMap = new HashMap<String, Shop>();
    	playerEntrances = new HashMap<String, String>();
    	playerExits = new HashMap<String, String>();
    	jailedPlayers = new HashMap<String, Location>();
    	stolenToClaim = new HashMap<String, List<ItemStack>>();
    	shippedToCollect = new HashMap<String, List<ItemStack[]>>();
    	forbiddenTpLocs = new HashMap<Location, Integer>();
    	
    	forbiddenInStore = new HashSet<Integer>();
    	Config.cartEnabledW = new HashSet<String>();
    	
    	tpLocBlacklist = false;
        econ = null;
        Config.keepstolen = false;
        Config.enableSelling = false;
        Config.punishment = null;
        Config.langpack = null;
        Config.hellLoc = null;
        Config.jailLoc = null;
        Config.dropLoc = null;
        Config.pstorecreate = 0.0;
        entrance = "";
        exit = "";
    	log = this.getLogger();
    	
    	newUpdate = "";
    	
    	unit = "$";
    	
    	try {
    		double newest = updateCheck(0.31);
    		if(newest > 0.31){
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
    			getCommand("rstplocs").setExecutor(cmdExe);
    			getCommand("rsunjail").setExecutor(cmdExe);
    			getCommand("rsreload").setExecutor(cmdExe);
    		}
    		
            tpLocBlacklist = true;
            
    		Config.initialize();
    		
    		File f;
    		FileInputStream fstream;
    		BufferedReader br;
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
    					} else if(s.equals("Shops database for RealShopping v0.30") || s.equals("Shops database for RealShopping v0.31")) { 
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
    					if(!s.equals("Inventories database for RealShopping v0.32"))
    						PInvMap.put(s.split(";")[0].split("-")[0], new RSPlayerInventory(s.split(";")[1] ,s.split(";")[0].split("-")[1]));//Name - Pinv
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
    					if(s.equals("Jailed players database for RealShopping v0.21") || s.equals("Jailed players database for RealShopping v0.30") || s.equals("Jailed players database for RealShopping v0.31")){

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
    			f = new File(mandir + "allowedtplocs.db");
    			if(f.exists()){
    				fstream = new FileInputStream(f);
    				br = new BufferedReader(new InputStreamReader(fstream));
    				String s;
    				while ((s = br.readLine()) != null){
    					if(s.length() > 50 && s.substring(0, 49).equals("Allowed teleport locations for RealShopping v0.31")){
    						if(s.substring(51).equals("Blacklist")) tpLocBlacklist = true;
    						else tpLocBlacklist = false;
    					} else {
    						forbiddenTpLocs.put(stringToLoc(s.split(";")[0], s.split(";")[1]), Integer.parseInt(s.split(";")[2]));
    					}
    				}
    				fstream.close();
    				br.close();
    			}
    			f.delete();
    		} catch (Exception e){
				e.printStackTrace();
    			log.info("Failed while reading allowedtplocs.db");
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
        	LangPack.initialize(Config.langpack);
        	unit = LangPack.UNIT;
    		initForbiddenInStore();
    		initMaxDur();
    		log.info("RealShopping initialized");
    	} else {
    		log.info("Couldn't initialize RealShopping.");
    	}
        
    }
     
    public void onDisable(){
		try {
			Object[] keys = PInvMap.keySet().toArray();//Player Map
			
			File f = new File(mandir + "inventories.db");
			if(!f.exists()) f.createNewFile();
			PrintWriter pW;
			pW = new PrintWriter(f);
			for(int i = 0;i < keys.length;i++){
				if(i == 0) pW.println("Inventories database for RealShopping v0.32");
				String invStr = PInvMap.get(keys[i]).exportToString();
				pW.println(keys[i] + "-" + PInvMap.get(keys[i]).getStore() + ";" + invStr);
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
				if(i == 0) pW.println("Jailed players database for RealShopping v0.31");
				pW.println((String)keys[i] + ";" + jailedPlayers.get(keys[i]).getWorld().getName() + ";" + locAsString(jailedPlayers.get(keys[i])));
			}
			pW.close();
			
			f = new File(mandir+"allowedtplocs.db");
			if(!f.exists()) f.createNewFile();
			pW = new PrintWriter(f);
			keys = forbiddenTpLocs.keySet().toArray();
			for(int i = 0;i < keys.length;i++){
				if(i == 0) pW.println("Allowed teleport locations for RealShopping v0.31 " + (tpLocBlacklist?"Blacklist":"Whitelist"));
				pW.println(((Location)keys[i]).getWorld().getName() + ";" + locAsString((Location)keys[i]) + ";" + forbiddenTpLocs.get(keys[i]));
			}
			pW.close();
			
				//Write prices to xml

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
	            keys = shopMap.keySet().toArray();
	            for(int i = 0;i < keys.length;i++){	
	            	Element shop = doc.createElement("shop");
	            	shop.setAttribute("name", shopMap.get(keys[i]).name);
	            	root.appendChild(shop);
	            	
	            	tempMap = shopMap.get(keys[i]).prices;
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
			pW.println("Shops database for RealShopping v0.31");
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
					
					PInvMap.put(player.getName(), new RSPlayerInventory(player, (String) keys[i]));
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
    
    public static boolean exit(Player player, boolean cmd){
		if(PInvMap.containsKey(player.getName())){
			if(shopMap.size() > 0){
				if(PInvMap.get(player.getName()).hasPaid() || player.getGameMode() == GameMode.CREATIVE){
					String shopName = PInvMap.get(player.getName()).getStore();
					Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
					if(shopMap.get(shopName).exit.contains(l)){
						l = shopMap.get(shopName).entrance.get(shopMap.get(shopName).exit.indexOf(l)).clone();
						if(shopMap.get(shopName).sellToStore.containsKey(player.getName()))
							shopMap.get(shopName).sellToStore.remove(player.getName());
						PInvMap.remove(player.getName());
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
		if(PInvMap.containsKey(player.getName())){
			String shopName = PInvMap.get(player.getName()).getStore();
			if(!shopMap.get(shopName).prices.isEmpty()) {
				float toPay = PInvMap.get(player.getName()).toPay(invs);
				if(econ.getBalance(player.getName()) < toPay) {
					player.sendMessage(ChatColor.RED + LangPack.YOUCANTAFFORDTOBUYTHINGSFOR + toPay + unit);
					return true;
				} else {
					econ.withdrawPlayer(player.getName(), toPay);
					if(!shopMap.get(shopName).owner.equals("@admin")) econ.depositPlayer(shopMap.get(shopName).owner, toPay);//If player owned store, pay player
					if(invs != null) PInvMap.get(player.getName()).update(invs);
					else PInvMap.get(player.getName()).update();
					player.sendMessage(ChatColor.RED + LangPack.YOUBOUGHTSTUFFFOR + toPay + unit);
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
    
    public static boolean addItemToSell(Player p, ItemStack iS){
    	if(Config.enableSelling && PInvMap.containsKey(p.getName())){
    		Shop tempShop = shopMap.get(PInvMap.get(p.getName()).getStore());
    		if(tempShop.buyFor > 0 && !tempShop.prices.isEmpty()){
    			if(tempShop.prices.containsKey(iS.getTypeId())){
    				RSPlayerInventory tempPInv = PInvMap.get(p.getName());
    	    		System.out.println(tempPInv.toString());
    				if(tempPInv.hasItems()){
   						if(tempPInv.hasItem(iS)){
   							int ownedAm = tempPInv.getAmount(iS);
   							int sellAm = (maxDurMap.containsKey(iS.getTypeId()))?maxDurMap.get(iS.getTypeId()) - iS.getDurability():iS.getAmount();
   							if(sellAm > ownedAm) {
   								p.sendMessage(ChatColor.RED + LangPack.YOUDONTOWNTHEITEMSYOUWANTTOSELL);
   								return false;
   							}
   							if(!tempShop.sellToStore.containsKey(p.getName()))
   								tempShop.sellToStore.put(p.getName(), new ArrayList<ItemStack>());
   							tempShop.sellToStore.get(p.getName()).add(iS);
   							tempPInv.removeItem(iS, sellAm);//Update player inv

   							p.sendMessage(ChatColor.GREEN + RealShopping.formatItemStackToMess(new ItemStack[]{iS}) + LangPack.ADDEDTOSELLLIST);
   							return true;
   						} else p.sendMessage(ChatColor.RED + LangPack.YOUDONTOWNTHEITEMSYOUWANTTOSELL);
    				} else p.sendMessage(ChatColor.RED + LangPack.YOUDONTOWNTHEITEMSYOUWANTTOSELL);
    			} else p.sendMessage(ChatColor.RED + LangPack.THISSTOREDOESNTBUY + iS.getType());
    		} else p.sendMessage(ChatColor.RED + LangPack.THISSTOREDOESNTBUYANYITEMS);
    	} else p.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
    	return false;
    }
    
    public static boolean cancelToSell(Player p){
    	Shop tempShop = shopMap.get(PInvMap.get(p.getName()).getStore());
    	if(Config.enableSelling && PInvMap.containsKey(p.getName()) && tempShop.sellToStore.containsKey(p.getName())){
    		List<ItemStack> pList = tempShop.sellToStore.get(p.getName());
			for(int i = 0;i < pList.size();i++){
				System.out.println(pList.get(i));
				int amount = (maxDurMap.containsKey(pList.get(i).getTypeId()))?maxDurMap.get(pList.get(i).getTypeId()) - pList.get(i).getDurability():pList.get(i).getAmount();
				PInvMap.get(p.getName()).addItem(pList.get(i), amount);//Update player inv
			}
			
			tempShop.sellToStore.remove(p.getName());//Cancel selling
			p.sendMessage(ChatColor.GREEN + LangPack.CANCELLEDSELLINGITEMS);
			return true;
    	}
    	return false;
    }
    
    public static boolean confirmToSell(Player p){
    	Shop tempShop = shopMap.get(PInvMap.get(p.getName()).getStore());
    	if(Config.enableSelling && PInvMap.containsKey(p.getName()) && tempShop.buyFor > 0){
    		if(tempShop.sellToStore.containsKey(p.getName())){
    			List<ItemStack> sList = tempShop.sellToStore.get(p.getName());
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
    					tempShop.sellToStore.remove(p.getName());
    					p.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEALLTHEITEMSYOUWANTEDTOSELL);
    					return false;
    				}
    			}
    			float payment = 0;
    			for(int i = 0;i < sList.size();i++){
    				int type = sList.get(i).getTypeId();
    					if(tempShop.prices.containsKey(type)){//Something in inventory has a price
    						int amount = ((maxDurMap.containsKey(type))?maxDurMap.get(type) - sList.get(i).getDurability():sList.get(i).getAmount());
    						float cost = tempShop.prices.get(type);
    						if(tempShop.sale.containsKey(type)){//There is a sale on that item.
    							int pcnt = 100 - tempShop.sale.get(type);
    							cost *= pcnt;
    							cost = Math.round(cost);
    							cost /= 100;
    						}
    						cost *= tempShop.buyFor;
							cost = Math.round(cost);
							cost /= 100;
							
    						payment += cost * amount;
    					}
    			}
    			boolean cont = false;
    			String own = tempShop.owner;
    			if(!own.equals("@admin")){
    				if(econ.getBalance(own) >= payment){
    					econ.depositPlayer(p.getName(), payment);
    					econ.withdrawPlayer(own, payment);//If player owned store, pay player
    					p.sendMessage(ChatColor.GREEN + LangPack.SOLD + sList.size() + LangPack.ITEMSFOR + payment + unit);
    					cont = true;
    				}
    			} else {
					econ.depositPlayer(p.getName(), payment);
					tempShop.sellToStore.remove(p.getName());
					p.sendMessage(ChatColor.GREEN + LangPack.SOLD + sList.size() + LangPack.ITEMSFOR + payment + unit);
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
    	StorageMinecart firstCart = null;//First cart found on first block
    	if(Config.cartEnabledW.contains(l.getWorld().getName())){
        	Object[] mcArr = l.getWorld().getEntitiesByClass(StorageMinecart.class).toArray();
        	String rails = "";
        	for(int i = 0;i < b.length;i++){//Get rails in area
        		if(b[i].getType() == Material.RAILS || b[i].getType() == Material.POWERED_RAIL || b[i].getType() == Material.DETECTOR_RAIL){
        			rails += "," + i;
        		}
        	}
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
    	}
    	if(firstCart == null) return new StorageMinecart[0];
    	else return new StorageMinecart[]{firstCart};//TODO add more carts
    }
    
	public static boolean shipCartContents(StorageMinecart sM, Player p) {
		//Get bought items in cart
		
		//Old inv = items
		
		ItemStack[] cartInv = sM.getInventory().getContents();//Items in shopping cart
		
		Map<PItem, Integer> bought = new HashMap<PItem, Integer>();
		
		if(PInvMap.get(p.getName()).hasItems()){
			for(int i = 0;i < cartInv.length;i++){
				ItemStack x = cartInv[i];
				int type = x.getTypeId();
				System.out.println(shopMap);
				if(shopMap.get(p.getName()).prices.containsKey(type)){//Something in inventory has a price
					int amount = (maxDurMap.containsKey(type)?maxDurMap.get(type) - x.getDurability():x.getAmount());

					if(bought.containsKey(type)){
						bought.put(new PItem(x), amount + bought.get(new PItem(x)));
					} else {
						bought.put(new PItem(x), amount);
					}
					if(PInvMap.get(p.getName()).hasItem(x)){
						if(bought.get(new PItem(x)) > PInvMap.get(p.getName()).getAmount(x)){
							bought.put(new PItem(x), PInvMap.get(p.getName()).getAmount(x));
						}
					} else {
						log.info("Error #803");
						bought.remove(x);
					}
				}
			}
			
			if(bought.isEmpty()){
				p.sendMessage(ChatColor.RED + LangPack.YOUCANTSHIPANEMPTYCART);
				return true;
			}
			
			Map<PItem, Integer> bought2 = new HashMap<PItem, Integer>(bought);
			
			//Remove stolen items from carts inventory
			ItemStack[] newCartInv = new ItemStack[cartInv.length];
			ItemStack[] boughtIS = new ItemStack[cartInv.length];
			for(int i = 0;i < cartInv.length;i++){
				ItemStack x = cartInv[i];
				if(x != null)
					if(bought.containsKey(new PItem(x))){//Has bought item
						int diff = bought.get(new PItem(x)) - (maxDurMap.containsKey(x.getTypeId())?maxDurMap.get(x.getTypeId()) - x.getDurability():x.getAmount());
						if(diff >= 0){//If + then even more stolen left
							bought.put(new PItem(x), diff);
							boughtIS[i] = x.clone();
							x = null;
						} else {//If negative then no more stolen thing in inventory
							if(maxDurMap.containsKey(x.getTypeId())){
								x.setDurability((short)(x.getDurability() + bought.get(new PItem(x))));
								boughtIS[i] = new ItemStack(x);
								boughtIS[i].setDurability(bought.get(new PItem(x)).shortValue());
							} else {
								x.setAmount(x.getAmount() - bought.get(new PItem(x)));
								boughtIS[i] = new ItemStack(x);
								boughtIS[i].setAmount(bought.get(new PItem(x)));
							}
							bought.remove(new PItem(x));
						}
					}
				newCartInv[i] = x;
			}
			sM.getInventory().setContents(newCartInv);
			
			if(!bought.isEmpty()){ log.info("Error #802"); System.out.println(bought);}
			
			//Ship
			if(!shippedToCollect.containsKey(p.getName()))
				shippedToCollect.put(p.getName(), new ArrayList<ItemStack[]>());
			shippedToCollect.get(p.getName()).add(boughtIS);
			
			p.sendMessage(ChatColor.GREEN + LangPack.PACKAGEWAITINGTOBEDELIVERED);
			
			//Update player inv
			Object[] keys = bought2.keySet().toArray();
			for(int i = 0;i < keys.length;i++){
				log.info(PInvMap.get(p.getName()).removeItem((PItem)keys[i], bought2.get(keys[i]))+"");//TODO remove check
			}
		} else p.sendMessage(ChatColor.RED + LangPack.YOUHAVENTBOUGHTANYTHING);

		return true;
	}

	public static boolean collectShipped(Location l, Player p, int id) {
		if(l.getBlock().getState() instanceof Chest){
			if(shippedToCollect.containsKey(p.getName())){
				if(shippedToCollect.get(p.getName()).size() >= id){
					((Chest)l.getBlock().getState()).getBlockInventory().setContents(shippedToCollect.get(p.getName()).get(id - 1));
					shippedToCollect.get(p.getName()).remove(id - 1);
					return true;
				} else p.sendMessage(ChatColor.RED + LangPack.THERESNOPACKAGEWITHTHEID + id);
			} else p.sendMessage(ChatColor.RED + LangPack.YOUHAVENTGOTANYITEMSWAITINGTOBEDELIVERED);
		} else p.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUARESTANDINGONISNTACHEST);
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
    	if(!shopMap.get(store).prices.isEmpty()){
    		Map<Integer, Float> tempMap = shopMap.get(store).prices;
 			if(!tempMap.isEmpty()){
 				Object[] keys = tempMap.keySet().toArray();
 				if(page*9 < keys.length){//If page exists
 					boolean SL = false;
 					if(!shopMap.get(store).sale.isEmpty()){
 						sender.sendMessage(ChatColor.GREEN + LangPack.THEREISA + shopMap.get(store).sale.values().toArray()[0] + LangPack.PCNTOFFSALEAT + store);
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
 		 						onSlStr = ChatColor.GREEN + LangPack.ONSALE;
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
 		 						onSlStr = ChatColor.GREEN + LangPack.ONSALE;
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
    	if(Config.punishment.equalsIgnoreCase("hell")){
    		if(!Config.keepstolen){
    			returnStolen(p);
    		}
			if(shopMap.get(PInvMap.get(p.getName()).getStore()).sellToStore.containsKey(p.getName()))
				shopMap.get(PInvMap.get(p.getName()).getStore()).sellToStore.remove(p.getName());
			PInvMap.remove(p.getName());
        	p.sendMessage(ChatColor.RED + LangPack.TRYINGTOCHEATYOURWAYOUT);
        	log.info(p.getName() + LangPack.TRIEDTOSTEALFROMTHESTORE);

        	Location dropLoc2 = Config.dropLoc.clone().add(1, 0, 0);
         	if(p.teleport(Config.hellLoc.clone().add(0.5, 0, 0.5))){
        		log.info(p.getName() + LangPack.WASTELEPORTEDTOHELL);
        		p.sendMessage(ChatColor.RED + LangPack.HAVEFUNINHELL);
             	Block block = p.getWorld().getBlockAt(Config.dropLoc);
             	if(block.getType() != Material.CHEST) block.setType(Material.CHEST);
             	BlockState blockState = block.getState();
             	
             	block = p.getWorld().getBlockAt(dropLoc2);
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
    	} else if(Config.punishment.equalsIgnoreCase("jail")){
    		if(!Config.keepstolen){
    			returnStolen(p);
    		}
			jailedPlayers.put(p.getName(), shopMap.get(PInvMap.get(p.getName()).getStore()).entrance.get(0));
			if(shopMap.get(PInvMap.get(p.getName()).getStore()).sellToStore.containsKey(p.getName()))
				shopMap.get(PInvMap.get(p.getName()).getStore()).sellToStore.remove(p.getName());
    		PInvMap.remove(p.getName());
        	p.sendMessage(ChatColor.RED + LangPack.TRYINGTOCHEATYOURWAYOUT);
        	log.info(p.getName() + LangPack.TRIEDTOSTEALFROMTHESTORE);

         	if(p.teleport(Config.jailLoc.clone().add(0.5, 0, 0.5))) {
         		p.sendMessage(LangPack.YOUWEREJAILED);
         		log.info(p.getName() + LangPack.WASJAILED);
         	}
    	} else if(Config.punishment.equalsIgnoreCase("none")){
    		if(!Config.keepstolen){
    			returnStolen(p);
    		}
    	}
    }
    
    static void returnStolen(Player p){
    	Map<PItem, Integer> stolen = PInvMap.get(p.getName()).getStolen();
    	Map<PItem, Integer> stolen2 = new HashMap<PItem, Integer>(stolen);

		//Remove stolen items from players inventory
		ItemStack[][] playerInv = new ItemStack[][]{p.getInventory().getContents(), p.getInventory().getArmorContents()};
		ItemStack[][] newPlayerInv = new ItemStack[][]{new ItemStack[playerInv[0].length], new ItemStack[playerInv[1].length]};
		for(int j = 0;j < 2;j ++)
		for(int i = 0;i < playerInv[j].length;i++){
			ItemStack x = playerInv[j][i];
			if(x != null){
				PItem tempPI = new PItem(x);
				if(stolen.containsKey(tempPI)){//Has stolen item
					int diff = stolen.get(tempPI) - (maxDurMap.containsKey(x.getTypeId())?maxDurMap.get(x.getTypeId()) - x.getDurability():x.getAmount());
					if(diff > 0){//If + then even more stolen left
						stolen.put(tempPI, diff);
						x = null;
					} else {//If negative then no more stolen thing in inventory
						if(maxDurMap.containsKey(x.getTypeId())){
							x.setDurability((short)(x.getDurability() + stolen.get(tempPI)));
						} else {
							x.setAmount(x.getAmount() - stolen.get(tempPI));
						}
						stolen.remove(tempPI);
					}
				}
			}
			newPlayerInv[j][i] = x;
		}
		p.getInventory().setContents(newPlayerInv[0]);
		p.getInventory().setArmorContents(newPlayerInv[1]);
		
		System.out.println(stolen2);
		
		String own = shopMap.get(PInvMap.get(p.getName()).getStore()).owner;
		if(!own.equals("@admin")){//Return items if player store.
			if(!stolenToClaim.containsKey(own)) stolenToClaim.put(own, new ArrayList<ItemStack>());
			Object[] keyss = stolen2.keySet().toArray();
			for(int i = 0;i < keyss.length;i++){
				int type = ((PItem) keyss[i]).type;
				ItemStack tempIS = ((PItem) keyss[i]).toItemStack();
				if(maxDurMap.containsKey(type)){
					if(stolen2.get(keyss[i]) > maxDurMap.get(type))
						while(maxDurMap.get(type) < stolen2.get(keyss[i])){//If more than one stack/full tool
							stolenToClaim.get(own).add(tempIS.clone());
							stolen2.put((PItem) keyss[i], stolen2.get(keyss[i]) - maxDurMap.get(type));
						}
					tempIS.setDurability((short) (maxDurMap.get(type) - stolen2.get(keyss[i])));
					stolenToClaim.get(own).add(tempIS);
				} else {
					if(stolen2.get(keyss[i]) > Material.getMaterial(type).getMaxStackSize())
						while(Material.getMaterial(type).getMaxStackSize() < stolen2.get(keyss[i])){//If more than one stack/full tool
							ItemStack tempIStemp = tempIS.clone();
							tempIStemp.setAmount(Material.getMaterial(type).getMaxStackSize());
							stolenToClaim.get(own).add(tempIStemp);
							stolen2.put((PItem) keyss[i], stolen2.get(keyss[i]) - Material.getMaterial(type).getMaxStackSize());
						}
					tempIS.setAmount(stolen.get(keyss[i]));
					stolenToClaim.get(own).add(tempIS);
				}
			}
		}
    }
    
    public static boolean allowTpOutOfStore(Location l){
    	Object[] keys = forbiddenTpLocs.keySet().toArray();
    	boolean allow = true;
    	if(tpLocBlacklist){
    		for(Object loc:keys){
    			if(l.getWorld() == ((Location)loc).getWorld());
    			int xDif = l.getBlockX() - ((Location)loc).getBlockX();
    			int yDif = l.getBlockY() - ((Location)loc).getBlockY();
    			int zDif = l.getBlockZ() - ((Location)loc).getBlockZ();
    			double temp = Math.sqrt(Math.pow(Math.max(xDif, xDif * -1), 2) + Math.pow(Math.max(yDif, yDif * -1), 2) + Math.pow(Math.max(zDif, zDif * -1), 2));
    			if(temp <= forbiddenTpLocs.get(loc)){
    					allow = false;
    					break;
    			}		
    		}
    	}

    	return allow;
    }
    
/*    static String createInv(Player player){
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
		if(!inv[0].trim().equals(""))//If inv not empty
			for(int j = 0;j < inv.length;j++){
				int type = Integer.parseInt(inv[j].split(":")[0]);
				int amount;
				amount = Integer.parseInt(inv[j].split(":")[1]);
				if(invOO.containsKey(type)) invOO.put(type, amount + invOO.get(type));
				else invOO.put(type, amount);
			}
		return invOO;
    }
    
    static String createStrOneOccur(String[] inv){
    	//Creates a string with each item in the players inventory occurring only once. The value is the amount of all items together.
		Map<Integer, Integer> invOO = createInvOneOccur(inv);
		String str = "";
		Object[] keys = invOO.keySet().toArray();
		for(Object o:keys){
			str += "," + o + ":" + invOO.get(o);
		}
		if(!str.equals("")) str = str.substring(1);
		return str;
    }*/
    
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

    private void initForbiddenInStore(){
    	forbiddenInStore.add(297);//Bread
    	forbiddenInStore.add(354);//Cake
    	forbiddenInStore.add(366);//Cooked Chicken
    	forbiddenInStore.add(349);//Cooked Fish
    	forbiddenInStore.add(320);//Cooked Porkchop
    	forbiddenInStore.add(357);//Cookie
    	forbiddenInStore.add(322);//Golden Apple
    	forbiddenInStore.add(360);//Melon Slice
    	forbiddenInStore.add(282);//Mushroom Stew
    	forbiddenInStore.add(363);//Raw Beef
    	forbiddenInStore.add(365);//Raw Chicken
    	forbiddenInStore.add(349);//Raw Fish
    	forbiddenInStore.add(319);//Raw Porkchop
    	forbiddenInStore.add(260);//Red Apple
    	forbiddenInStore.add(367);//Rotten Flesh
    	forbiddenInStore.add(364);//Steak

    	forbiddenInStore.add(326);//Bucket of water
    	forbiddenInStore.add(327);//Bucket of lava
    	
    	forbiddenInStore.add(373);//Potion
    	forbiddenInStore.add(384);//Bottle o' enchanting
    	forbiddenInStore.add(375);//Spider Eye
    	forbiddenInStore.add(385);//Fire Charge
    	forbiddenInStore.add(344);//Egg
    	forbiddenInStore.add(368);//Ender Pearl
    	forbiddenInStore.add(383);//Spawning egg
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
				if(RealShopping.shopMap.containsKey(shopList.get(i))){
					RealShopping.shopMap.get(shopList.get(i)).prices = mapList.get(i);
				} else {
					RealShopping.log.info("Couldn't store prices for non existing store " + shopList.get(i));
				}
			}
		}
	}
}