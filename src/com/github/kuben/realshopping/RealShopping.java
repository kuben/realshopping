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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.h31ix.updater.Updater;

import org.bukkit.Bukkit;
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
import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class RealShopping extends JavaPlugin {
	Updater updater;
	StatUpdater statUpdater;
	Notificator notificatorThread;
	
	public static Map<String, RSPlayerInventory> PInvMap;
	
	static String mandir;
	public static Map<Integer, Integer> maxDurMap;
	public static Map<String, Shop> shopMap;
	public static Map<String, String> playerEntrances;
	public static Map<String, String> playerExits;
	public static Map<String, Location> jailedPlayers;
	public static Map<String, List<ShippedPackage>> shippedToCollect;
	public static Map<Location, Integer> forbiddenTpLocs;
	public static Map<String, List<String>> notificator;
	
	public static Set<Integer> forbiddenInStore;
	
	public static boolean tpLocBlacklist;

    String entrance;
    String exit;
	static Logger log;
	
	public static String working;
	
	boolean smallReload = false;
	
	public static String newUpdate;
	
	public static String unit;
	
	/*
	 * 
	 * Enable/Disable functions
	 * 
	 */
	
    public void onEnable(){
    	mandir = "plugins/RealShopping/";
    	updater = null;
    	statUpdater = null;
    	notificatorThread = null;
    	PInvMap = new HashMap<String, RSPlayerInventory>();
    	maxDurMap = new HashMap<Integer, Integer>();
    	shopMap = new HashMap<String, Shop>();
    	playerEntrances = new HashMap<String, String>();
    	playerExits = new HashMap<String, String>();
    	jailedPlayers = new HashMap<String, Location>();
    	shippedToCollect = new HashMap<String, List<ShippedPackage>>();
    	forbiddenTpLocs = new HashMap<Location, Integer>();
    	notificator = new HashMap<String, List<String>>();
    	
    	forbiddenInStore = new HashSet<Integer>();
    	Config.cartEnabledW = new HashSet<String>();
    	
    	tpLocBlacklist = false;
    	Config.debug = false;
        Config.keepstolen = false;
        Config.enableSelling = false;
        Config.autoprotect = false;
        Config.deliveryZones = -1;
        Config.autoUpdate = 0;
        Config.punishment = null;
        Config.langpack = null;
        Config.hellLoc = null;
        Config.jailLoc = null;
        Config.dropLoc = null;
        Config.pstorecreate = 0.0;
        Config.notTimespan = 0;
        Config.statTimespan = 0;
        Config.cleanStatsOld = 0;
        Config.updateFreq = 0;
        Config.allowFillChests = false;
        Config.enableAI = false;
        entrance = "";
        exit = "";
    	log = this.getLogger();
    	working = null;
    	
    	newUpdate = "";
    	
    	unit = "$";

    	if(!smallReload){
    		getServer().getPluginManager().registerEvents(new RSPlayerListener(), this);
    		RSCommandExecutor cmdExe = new RSCommandExecutor(this);
    		getCommand("rsenter").setExecutor(cmdExe);
    		getCommand("rsexit").setExecutor(cmdExe);
    		getCommand("rspay").setExecutor(cmdExe);
    		getCommand("rscost").setExecutor(cmdExe);
    		getCommand("rssell").setExecutor(cmdExe);
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
    		getCommand("rsprotect").setExecutor(cmdExe);
    		getCommand("rsupdate").setExecutor(cmdExe);
    		getCommand("realshopping").setExecutor(cmdExe);
    	}

   		working = "";
   		tpLocBlacklist = true;
        
   		RSEconomy.setupEconomy();
		Config.initialize();
		if(Config.autoUpdate > 0){
			if(Config.autoUpdate == 5){
				updater = new Updater(this, "realshopping", this.getFile(), Updater.UpdateType.DEFAULT, true);
				if(updater.getResult() == Updater.UpdateResult.SUCCESS){
					log.info("RealShopping updated to " + updater.getLatestVersionString() + ". Restart the server to load the new version.");
				}
			} else {
				updater = new Updater(this, "realshopping", this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
				if(updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE){
	    			if(Config.autoUpdate > 2)
    						newUpdate = updater.getLatestVersionString() + " of RealShopping is available for download. Update for new features and/or bugfixes with the rsupdate command.";
	    			else
    						newUpdate = updater.getLatestVersionString()
    						+ " of RealShopping is available for download. Update for new features and/or bugfixes. You can get information about the new version with 'rsupdate info'";
	    			log.info(newUpdate);
				}
			}
		}
		
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
				String header = "Shops database for RealShopping v";
				Float version = 0f;
				boolean notHeader = true;
				while ((s = br.readLine()) != null){// Read shops.db
					notHeader = true;
					if(version == 0 && s.length() > header.length() && s.substring(0, header.length()).equals(header)){
						version = Float.parseFloat(s.substring(header.length()));
						notHeader = false;
					}
					if(notHeader) {
    					String[] tS = s.split(";")[0].split(":");
    		    		shopMap.put(tS[0], new Shop(tS[0], tS[1], (version >= 0.20)?tS[2]:"@admin"));
    		    		shopMap.get(tS[0]).buyFor = (version >= 0.30)?Integer.parseInt(tS[3]):0;
    		    		if(version >= 0.40){
    		    			byte notifyByte = 0;
    		    			if(tS[4].equals("notify")) notifyByte = 1;
    		    			else if(tS[4].equals("change")) notifyByte = 2;
    		    			shopMap.get(tS[0]).notifyChanges = notifyByte;
    		    			shopMap.get(tS[0]).changeTreshold = Integer.parseInt(tS[5]);
    		    			shopMap.get(tS[0]).changePercent = Integer.parseInt(tS[6]);
    		    			shopMap.get(tS[0]).allowNotifications = Boolean.parseBoolean(tS[7]);
    		    		}
    					for(int i = (version >= 0.40)?8:(version >= 0.30)?4:(version >= 0.20)?3:2;i < tS.length;i++){//The entrances + exits
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
				if(version < 0.40)//Needs updating
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
		
		loadTemporaryFile(0);
		loadTemporaryFile(1);
		loadTemporaryFile(2);
		loadTemporaryFile(3);
		loadTemporaryFile(4);
		loadTemporaryFile(5);
		loadTemporaryFile(6);
		loadTemporaryFile(7);
		
		f = new File(mandir + "langpacks/");
		if(!f.exists()) f.mkdir();
    	LangPack.initialize(Config.langpack);
    	unit = LangPack.UNIT;
		initForbiddenInStore();
		initMaxDur();
		if(Config.notTimespan >= 500){
			notificatorThread = new Notificator();
			notificatorThread.start();
		}
		if(Config.enableAI){
			statUpdater = new StatUpdater();
			statUpdater.start();
		}
		log.info("RealShopping initialized");
    }
     
    public void onDisable(){
		try {
			
			saveTemporaryFile(0);//Inventories
			saveTemporaryFile(1);//Jailed
			saveTemporaryFile(2);//TpLocs
			saveTemporaryFile(3);//Protected chests
			saveTemporaryFile(4);//Shipped Packages
			saveTemporaryFile(5);//toClaim
			saveTemporaryFile(6);//Stats
			saveTemporaryFile(7);//Notifications
			RSEconomy.export();//If econ is null
			
			if(notificatorThread != null) notificatorThread.running = false;
			if(statUpdater != null) statUpdater.running = false;
			
			//Write prices to xml

			File f = new File(mandir+"prices.xml");//Reset file
			if(f.exists()) f.delete();
			f.createNewFile();
			
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
	        Document doc = docBuilder.newDocument();
	            

	        Element root = doc.createElement("prices");
	        doc.appendChild(root);
	        doc.appendChild(doc.createComment("If you want to manually edit this file, do it when your server is down. Your changes won't be saved otherwise!"));
	           
	        Map<Price, Float> tempMap;
	        Object[] keys = shopMap.keySet().toArray();
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
			pW.println("Shops database for RealShopping v0.40");
			for(int i = 0;i<keys.length;i++){
				Shop tempShop = shopMap.get(keys[i]);
				pW.print(keys[i] + ":" + tempShop.world + ":" + tempShop.owner + ":" + tempShop.buyFor + ":" + (tempShop.notifyChanges==2?"change":(tempShop.notifyChanges==1?"notify":"nothing"))
						+ ":" + tempShop.changeTreshold + ":" + tempShop.changePercent + ":" + tempShop.allowNotifications);
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

 /*   	I haven't managed to cancel these, they are just sending an useless message to the player
  * 	forbiddenInStore.add(326);//Bucket of water
    	forbiddenInStore.add(327);//Bucket of lava
    	
    	forbiddenInStore.add(373);//Potion
    	forbiddenInStore.add(384);//Bottle o' enchanting
    	forbiddenInStore.add(375);//Spider Eye
    	forbiddenInStore.add(385);//Fire Charge
    	forbiddenInStore.add(344);//Egg
    	forbiddenInStore.add(368);//Ender Pearl
    	forbiddenInStore.add(383);//Spawning egg*/
    }

    private void loadTemporaryFile(int what){
    	File f = null;
    	FileInputStream fstream = null;
    	BufferedReader br = null;
    	String header = "";
    	
		try {
			if(what == 0){
    			f = new File(mandir + "inventories.db");
    			header = "Inventories database for RealShopping v";
    		} else if(what == 1){
    			f = new File(mandir + "jailed.db");
    			header = "Jailed players database for RealShopping v";
    		} else if(what == 2){
    			f = new File(mandir + "allowedtplocs.db");
    			header = "Allowed teleport locations for RealShopping v";
    		} else if(what == 3){
    			f = new File(mandir + "protectedchests.db");
    			header = "Protected chests for RealShopping v";
    		} else if(what == 4){
    			f = new File(mandir + "shipped.db");
    			header = "Shipped Packages database for RealShopping v";
    		} else if(what == 5){
    			f = new File(mandir + "toclaim.db");
    			header = "Stolen items database for RealShopping v";
    		} else if(what == 6){
    			f = new File(mandir + "stats.db");
    			header = "Statistics database for RealShopping v";
    		} else if(what == 7){
    			f = new File(mandir + "notifications.db");
    			header = "Notifications database for RealShopping v";
    		}
			
			if(f.exists()){
				fstream = new FileInputStream(f);
				br = new BufferedReader(new InputStreamReader(fstream));
				String s;
				float version = 0f;
				while ((s = br.readLine()) != null){
					boolean notHeader = true;
					
					if(version == 0 && s.length() > header.length() && s.substring(0, header.length()).equals(header)){
						notHeader = false;
						String vStr = s.substring(header.length());
						if(what == 2){
							if(vStr.substring(5).equals("Blacklist")) tpLocBlacklist = true;
							vStr = vStr.substring(0,4);
						}
						version = Float.parseFloat(vStr);
					}
					
					if(notHeader)
					if(what == 0){
						PInvMap.put(s.split(";")[0].split("-")[0], new RSPlayerInventory(s.split(";")[1] ,s.split(";")[0].split("-")[1]));//Name - Pinv
		    		} else if(what == 1){
		    			jailedPlayers.put(s.split(";")[0], stringToLoc(s.split(";")[1], s.split(";")[2]));
		    		} else if(what == 2){
		    			forbiddenTpLocs.put(stringToLoc(s.split(";")[0], s.split(";")[1]), Integer.parseInt(s.split(";")[2]));
		    		} else if(what == 3){
						shopMap.get(s.split(";")[0]).protectedChests.add(new Location(getServer().getWorld(s.split(";")[1].split(",")[0])
								,Double.parseDouble(s.split(";")[1].split(",")[1])
								,Double.parseDouble(s.split(";")[1].split(",")[2])
								,Double.parseDouble(s.split(";")[1].split(",")[3])));
		    		} else if(what == 4){
						if(version >= 0.32){
							shippedToCollect.put(s.split("\\[")[0], new ArrayList<ShippedPackage>());
	    					for(int i = 1;i < s.split("\\[").length;i++){
	    						String[] parts = s.split("\\[")[i].split("\\]");
	    						ItemStack[] iS = new ItemStack[27];
	    						
	    						//Metainfo
	    						Float cost = Float.parseFloat(parts[0].split(":")[0]);
	    						Location loc = new Location(getServer().getWorld(parts[0].split(":")[1].split(";")[0])
	    								,Integer.parseInt(parts[0].split(":")[1].split(";")[1].split(",")[0])
	    								,Integer.parseInt(parts[0].split(":")[1].split(";")[1].split(",")[1])
	    								,Integer.parseInt(parts[0].split(":")[1].split(";")[1].split(",")[2]));
	    						long timeStamp = Long.parseLong(parts[0].split(":")[2]);
	    						
	    						//Contents
	    						for(int j = 0;j < parts[1].split(",").length;j++){
	    							ItemStack tempIS;
	    							if(parts[1].split(",")[j].equals("null")) tempIS = null;
	    							else {
	    								tempIS = new ItemStack(Integer.parseInt(parts[1].split(",")[j].split(":")[0]),
	    										Integer.parseInt(parts[1].split(",")[j].split(":")[1]),
        	    								Short.parseShort(parts[1].split(",")[j].split(":")[2]),
        	    								Byte.parseByte(parts[1].split(",")[j].split(":")[3]));
        	    						for(int k = 4;k < parts[1].split(",")[j].split(":").length;k++){
        	    							tempIS.addEnchantment(Enchantment.getById(Integer.parseInt(parts[1].split(",")[j].split(":")[k].split(";")[0])), Integer.parseInt(parts[1].split(",")[j].split(":")[k].split(";")[1]));
        	    						}
        	    						iS[j] = tempIS;
	    							}
	    						}
	    						shippedToCollect.get(s.split("\\[")[0]).add(new ShippedPackage(iS, cost, loc, timeStamp));
	    					}
						} else {
	    					shippedToCollect.put(s.split("\\[\\]")[0], new ArrayList<ShippedPackage>());
	    					for(int i = 1;i < s.split("\\[\\]").length;i++){
	    						ItemStack[] iS = new ItemStack[27];
	    						Float cost = 0.0f;
	    						Location loc = getServer().getWorlds().get(0).getSpawnLocation();
	    						long timeStamp = System.currentTimeMillis();
	    						for(int j = 0;j < iS.length && j < 27;j++){
	    							if(s.split("\\[\\]")[i].split(";")[j].equals("null")) iS[j] = null;
	    							else iS[j] = new ItemStack(Integer.parseInt(s.split("\\[\\]")[i].split(";")[j].split(",")[0]),
	        								Integer.parseInt(s.split("\\[\\]")[i].split(";")[j].split(",")[1]),
	        								Short.parseShort(s.split("\\[\\]")[i].split(";")[j].split(",")[2]),
	        								Byte.parseByte(s.split("\\[\\]")[i].split(";")[j].split(",")[3]));
	    						}
	    						shippedToCollect.get(s.split("\\[\\]")[0]).add(new ShippedPackage(iS, cost, loc, timeStamp));
	    					}
						}
		    		} else if(what == 5){
						Shop tempShop;
						if(version >= 0.32){
							tempShop = shopMap.get(s.split(",")[0]);
	    					for(int i = 2;i < s.split(",").length;i++){
	    						ItemStack tempIS = new ItemStack(Integer.parseInt(s.split(",")[2].split(":")[0]),
	    								Integer.parseInt(s.split(",")[2].split(":")[1]),
	    								Short.parseShort(s.split(",")[2].split(":")[2]),
	    								Byte.parseByte(s.split(",")[2].split(":")[3]));
	    						for(int j = 4;j < s.split(",")[2].split(":").length;j++){
	    							tempIS.addEnchantment(Enchantment.getById(Integer.parseInt(s.split(",")[2].split(":")[j].split(";")[0])), Integer.parseInt(s.split(",")[2].split(":")[j].split(";")[1]));
	    						}
	    						tempShop.stolenToClaim.add(tempIS);
	    					}
						} else {
							String[] stores = getOwnedStores(s.split(";")[0]);
							if(!stores[0].equals("")){
								tempShop = shopMap.get(stores[0]);
		    					for(int i = 1;i < s.split(";").length;i++){
		    						tempShop.stolenToClaim.add(new ItemStack(Integer.parseInt(s.split(";")[i].split(",")[0]),
		    								Integer.parseInt(s.split(";")[i].split(",")[1]),
		    								Short.parseShort(s.split(";")[i].split(",")[2]),
		    								Byte.parseByte(s.split(";")[i].split(",")[3])));
		    					}
							} else {
								log.info("Couldn't convert because player doesn't own any stores: " + s);
							}
						}
		    		} else if(what == 6){
		    			Shop tempShop = shopMap.get(s.split(";")[0]);
		    			for(int i = 1;i < s.split(";").length;i++){
		    				tempShop.stats.add(new Statistic(s.split(";")[i]));
		    			}
		    		} else if(what == 7){
		    			List<String> l = new ArrayList<String>();
		    			for(int i = 1;i < s.split("\"").length;i++) l.add(s.split("\"")[i]);
		    			if(!l.isEmpty()) notificator.put(s.split("\"")[0], l);
		    		}
				}
				fstream.close();
				br.close();
			}
			f.delete();
		} catch (Exception e){
			e.printStackTrace();
			log.info("Failed while reading " + f.getName());
		}
    }
    
    private boolean saveTemporaryFile(int what){
    	File f = null;
    	Object[] keys;
    	String header;

    	try {
    		if(what == 0){
    			keys = PInvMap.keySet().toArray();//Player Map
    			f = new File(mandir + "inventories.db");
    			header = "Inventories database for RealShopping v0.40";
    		} else if(what == 1){
    			keys = jailedPlayers.keySet().toArray();
    			f = new File(mandir + "jailed.db");
    			header = "Jailed players database for RealShopping v0.40";
    		} else if(what == 2){
    			keys = forbiddenTpLocs.keySet().toArray();
    			f = new File(mandir + "allowedtplocs.db");
    			header = "Allowed teleport locations for RealShopping v0.40 " + (tpLocBlacklist?"Blacklist":"Whitelist");
    		} else if(what == 3){
    			keys = shopMap.keySet().toArray();
    			f = new File(mandir + "protectedchests.db");
    			header = "Protected chests for RealShopping v0.40";
    		} else if(what == 4){
    			keys = shippedToCollect.keySet().toArray();//Map of players having shipped items
    			f = new File(mandir + "shipped.db");
    			header = "Shipped Packages database for RealShopping v0.40";
    		} else if(what == 5){
    			keys = shopMap.keySet().toArray();
    			f = new File(mandir + "toclaim.db");
    			header = "Stolen items database for RealShopping v0.40";
    		} else if(what == 6){
    			keys = shopMap.keySet().toArray();
    			f = new File(mandir + "stats.db");
    			header = "Statistics database for RealShopping v0.40";
    		} else if(what == 7){
    			keys = notificator.keySet().toArray();
    			f = new File(mandir + "notifications.db");
    			header = "Notifications database for RealShopping v0.40";
    		} else {
    			return false;
    		}

			if(!f.exists()) f.createNewFile();
			PrintWriter pW;
			pW = new PrintWriter(f);
			for(int i = 0;i < keys.length;i++){
				if(i == 0) pW.println(header);
				String line = "";
				switch(what){
					case 0:
						line = keys[i] + "-" + PInvMap.get(keys[i]).getStore() + ";" + PInvMap.get(keys[i]).exportToString();
						break;
					case 1:
						line = ((String)keys[i]) + ";" + jailedPlayers.get(keys[i]).getWorld().getName() + ";" + locAsString(jailedPlayers.get(keys[i]));
						break;
					case 2:
						line = ((Location)keys[i]).getWorld().getName() + ";" + locAsString((Location)keys[i]) + ";" + forbiddenTpLocs.get(keys[i]);
						break;
					case 3:
						String protStr = shopMap.get(keys[i]).exportProtectedToString();
						if(!protStr.equals("")) line = ((String) keys[i]) + ";" + protStr;
						break;
					case 4:
						Object[] keys2 = shippedToCollect.get(keys[i]).toArray();
						if(keys2.length > 0){
							line = keys[i].toString();
							for(int j = 0;j < keys2.length;j++){
								ShippedPackage tempSP = (ShippedPackage)keys2[j];
								line += "[" + tempSP.getCost() + ":" + tempSP.getLocationSent().getWorld().getName() + ";"
								+ tempSP.getLocationSent().getBlockX() + "," + tempSP.getLocationSent().getBlockY() + "," + tempSP.getLocationSent().getBlockZ() + ":" + tempSP.getDateSent();
								line += "]" + tempSP.exportContents();
							}
						}
						break;
					case 5:
						String exportedStr = shopMap.get(keys[i]).exportToClaim();
						if(!exportedStr.equals(""))	line = keys[i] + "," + exportedStr;
						break;
					case 6:
						String statStr = shopMap.get(keys[i]).exportStats();
						if(!statStr.equals(""))	line = keys[i] + statStr;
						break;
					case 7:
						String s = "";
						for(String ss:notificator.get(keys[i])){
							s += "\""+ss;
						}
						if(!s.equals("")) line = keys[i]+s;
						break;
					default:
						return false;
					}
				if(!line.equals("")) pW.println(line);
			}
			pW.close();
    	} catch(Exception e){
    		log.info("Failed while saving " + f.getName());
    		e.printStackTrace();
    	}
    	return false;
    }
    
	/*
	 * 
	 * Basic store functions
	 * 
	 */
    
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
				if(toPay==0f) return false;
				if(RSEconomy.getBalance(player.getName()) < toPay) {
					player.sendMessage(ChatColor.RED + LangPack.YOUCANTAFFORDTOBUYTHINGSFOR + toPay + unit);
					return true;
				} else {
					RSEconomy.withdraw(player.getName(), toPay);
					if(!shopMap.get(shopName).owner.equals("@admin")){
						RSEconomy.deposit(shopMap.get(shopName).owner, toPay);//If player owned store, pay player
						if(shopMap.get(shopName).allowNotifications) sendNotification(shopMap.get(shopName).owner, player.getName() + " bought stuff for " + toPay + LangPack.UNIT + " from your store " + shopName + ".");
					}
					Map<PItem, Integer> bought = PInvMap.get(player.getName()).getBought(invs);
					
					if(Config.enableAI){
						PItem[] keys = bought.keySet().toArray(new PItem[0]);
						for(PItem key:keys){
							shopMap.get(shopName).stats.add(new Statistic(key, bought.get(key), true));
						}
					}
					
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
	
    public static boolean prices(CommandSender sender, int page, String store, boolean cmd){//TODO move to executor
    	if(!shopMap.get(store).prices.isEmpty()){
    		Map<Price, Float> tempMap = shopMap.get(store).prices;
 			if(!tempMap.isEmpty()){
 				Price[] keys = tempMap.keySet().toArray(new Price[0]);
 				if(page*9 < keys.length){//If page exists
// 					boolean SL = false;
 					if(!shopMap.get(store).sale.isEmpty()){
 						sender.sendMessage(ChatColor.GREEN + LangPack.THEREISA + shopMap.get(store).sale.values().toArray()[0] + LangPack.PCNTOFFSALEAT + store);
// 						SL = true;
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
 		 					sender.sendMessage(ChatColor.BLUE + "" + keys[i] + " " + Material.getMaterial(keys[i].getType()) + ChatColor.BLACK + " - " + ChatColor.RED + cost + unit + onSlStr);
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
 		 					sender.sendMessage(ChatColor.BLUE + "" + keys[i] + " " + Material.getMaterial(keys[i].getType()) + ChatColor.BLACK + " - " + ChatColor.RED + cost + unit + onSlStr);
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
    
	/*
	 * 
	 * Selling to stores functions
	 * 
	 */
 
    public static boolean sellToStore(Player p, ItemStack[] iS){
    	Shop tempShop = shopMap.get(PInvMap.get(p.getName()).getStore());
    	if(Config.enableSelling && PInvMap.containsKey(p.getName()) && tempShop.buyFor > 0){
    		float payment = 0;
    		List<ItemStack> sold = new ArrayList<ItemStack>();
    		for(int i = 0;i < iS.length;i++){//Calculate cost
    			if(iS[i] != null){
        			int type = iS[i].getTypeId();
        			int data = iS[i].getData().getData();
        			if(tempShop.prices.containsKey(new Price(type)) || tempShop.prices.containsKey(new Price(type, data))){//Something in inventory has a price
        				int amount = ((maxDurMap.containsKey(type))?maxDurMap.get(type) - iS[i].getDurability():iS[i].getAmount());
    					float cost = -1;
    					if(tempShop.prices.containsKey(new Price(type))) cost = tempShop.prices.get(new Price(type));
    					if(tempShop.prices.containsKey(new Price(type, data))) cost = tempShop.prices.get(new Price(type, data));
    					if(tempShop.sale.containsKey(new Price(type)) || tempShop.sale.containsKey(new Price(type, data))){//There is a sale on that item.
    						int pcnt = -1;
    						if(tempShop.sale.containsKey(new Price(type))) pcnt = 100 - tempShop.sale.get(new Price(type));
    						if(tempShop.sale.containsKey(new Price(type, data)))  pcnt = 100 - tempShop.sale.get(new Price(type, data));
        					cost *= pcnt;
        					cost = Math.round(cost);
        					cost /= 100;
        				}
      					cost *= tempShop.buyFor;
    					cost = Math.round(cost);
    					cost /= 100;

    					sold.add(iS[i]);
        				payment += cost * (maxDurMap.containsKey(type)?Math.ceil((double)amount / (double)maxDurMap.get(type)):amount);//Convert items durability to item amount
        			}
    			}
    		}
    		boolean cont = false;
    		String own = tempShop.owner;
    		if(!own.equals("@admin")){
    			if(RSEconomy.getBalance(own) >= payment){
    				RSEconomy.deposit(p.getName(), payment);
    				RSEconomy.withdraw(own, payment);//If player owned store, withdraw from owner
    				tempShop.sellToStore.remove(p.getName());
    				p.sendMessage(ChatColor.GREEN + LangPack.SOLD + sold.size() + LangPack.ITEMSFOR + payment + unit);
					if(tempShop.allowNotifications) sendNotification(own, "Your store " + tempShop.name + " bought stuff for " + payment + LangPack.UNIT + " from " + p.getName());
					for(ItemStack key:sold){
						if(Config.enableAI) tempShop.stats.add(new Statistic(new PItem(key), key.getAmount(), false));
						PInvMap.get(p.getName()).removeItem(key, key.getAmount());
					}
    				cont = true;
    			}
    		} else {
				RSEconomy.deposit(p.getName(), payment);
				tempShop.sellToStore.remove(p.getName());
				p.sendMessage(ChatColor.GREEN + LangPack.SOLD + sold.size() + LangPack.ITEMSFOR + payment + unit);
				for(ItemStack key:sold){
					PInvMap.get(p.getName()).removeItem(key, key.getAmount());
				}
				cont = true;
   			}
   			if(cont){
       			if(!own.equals("@admin")){//Return items if player store.
       				for(int i = 0;i < sold.size();i++){
        				tempShop.stolenToClaim.add(sold.get(i));
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
    
	/*
	 * 
	 * Cart-related functions
	 * 
	 */
    
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
    	if(Config.allowFillChests && (Config.cartEnabledW.contains("@all") || Config.cartEnabledW.contains(l.getWorld().getName()))){
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
				if(x != null){
					int type = x.getTypeId();
					if(shopMap.get(PInvMap.get(p.getName()).getStore()).prices.containsKey(new Price(type))
							|| shopMap.get(PInvMap.get(p.getName()).getStore()).prices.containsKey(new Price(type, x.getData().getData()))){//Something in cart has a price
						if(PInvMap.get(p.getName()).hasItem(x)){//Player owns item
							int amount = (maxDurMap.containsKey(type)?maxDurMap.get(type) - x.getDurability():x.getAmount());
							PItem tempPI = new PItem(x);
							
							if(bought.containsKey(tempPI)) amount += bought.get(tempPI);
							if(amount > PInvMap.get(p.getName()).getAmount(tempPI))
								bought.put(tempPI, PInvMap.get(p.getName()).getAmount(tempPI));
							else
								bought.put(tempPI, amount);
						}
					}
				}
			}
			
			if(bought.isEmpty()){
				p.sendMessage(ChatColor.RED + LangPack.YOUCANTSHIPANEMPTYCART);
				return true;
			}
			Map<PItem, Integer> bought2 = new HashMap<PItem, Integer>(bought);
			
			//Remove bought items from carts inventory
			ItemStack[] newCartInv = new ItemStack[cartInv.length];
			ItemStack[] boughtIS = new ItemStack[cartInv.length];
			for(int i = 0;i < cartInv.length;i++){
				ItemStack x = cartInv[i];
				if(x != null){
					PItem tempPI = new PItem(x);
					if(bought.containsKey(tempPI)){//Has bought item
						int diff = bought.get(tempPI) - (maxDurMap.containsKey(x.getTypeId())?maxDurMap.get(x.getTypeId()) - x.getDurability():x.getAmount());
						if(diff > 0){//If + then even more bought left
							if(diff > 0) bought.put(tempPI, diff);
							boughtIS[i] = x.clone();
							x = null;
						} else if(diff == 0) {//If zero
							if(maxDurMap.containsKey(x.getTypeId())){
								if(x.getDurability()  - bought.get(tempPI) < maxDurMap.get(x.getTypeId())){
									x.setDurability((short)(x.getDurability() - bought.get(tempPI)));// - ?
									boughtIS[i] = new ItemStack(x);
									boughtIS[i].setDurability(bought.get(tempPI).shortValue());
								} else x = null;
							} else {
								boughtIS[i] = new ItemStack(x);
								boughtIS[i].setAmount(bought.get(tempPI));
								x = null;
							}
							bought.remove(tempPI);
						}
					}
				}
				newCartInv[i] = x;
			}
			sM.getInventory().setContents(newCartInv);
			
			if(!bought.isEmpty()){ log.info("Error #802"); System.out.println(bought);}
			
			//Calculate cost
			float toPay = 0;
			Object[] keys = bought2.keySet().toArray();

			for(int i = 0;i < keys.length;i++){
				PItem key = (PItem) keys[i];
				Shop tempShop = shopMap.get(PInvMap.get(p.getName()).getStore());
				int amount = bought2.get(key);
				float cost = -1;
				if(tempShop.prices.containsKey(new Price(key.type))) cost = tempShop.prices.get(new Price(key.type));
				if(tempShop.prices.containsKey(new Price(key.type, key.data))) cost = tempShop.prices.get(new Price(key.type, key.data));
				if(tempShop.sale.containsKey(new Price(key.type)) || tempShop.sale.containsKey(new Price(key.type, key.data))){//There is a sale on that item.
					int pcnt = -1;
					if(tempShop.sale.containsKey(new Price(key.type))) pcnt = 100 - tempShop.sale.get(new Price(key.type));
					if(tempShop.sale.containsKey(new Price(key.type, key.data))) pcnt = 100 - tempShop.sale.get(new Price(key.type, key.data));
					cost *= pcnt;
					cost = Math.round(cost);
					cost /= 100;
				}
				toPay += cost * (RealShopping.maxDurMap.containsKey(key.type)?Math.ceil((double)amount / (double)RealShopping.maxDurMap.get(key.type)):amount);//Convert items durability to item amount
			}
			
			//Ship
			if(!shippedToCollect.containsKey(p.getName()))
				shippedToCollect.put(p.getName(), new ArrayList<ShippedPackage>());
			shippedToCollect.get(p.getName()).add(new ShippedPackage(boughtIS, toPay, sM.getLocation()));
			
			p.sendMessage(ChatColor.GREEN + LangPack.PACKAGEWAITINGTOBEDELIVERED);
			
			//Update player inv
			for(int i = 0;i < keys.length;i++){
				PInvMap.get(p.getName()).removeItem((PItem)keys[i], bought2.get(keys[i]));
			}
		} else p.sendMessage(ChatColor.RED + LangPack.YOUHAVENTBOUGHTANYTHING);

		return true;
	}

	public static boolean collectShipped(Location l, Player p, int id) {
		if(l.getBlock().getState() instanceof Chest){
			if(shippedToCollect.containsKey(p.getName())){
				if(shippedToCollect.get(p.getName()).size() >= id){
					boolean cont = false;
					double cost = 0;
					if(Config.zoneArray.length > 0){
						int i = 0;
						if(p.getLocation().getWorld().equals(shippedToCollect.get(p.getName()).get(id - 1).getLocationSent().getWorld())){//Same world
							double dist = p.getLocation().distance(shippedToCollect.get(p.getName()).get(id - 1).getLocationSent());
							while(i < Config.zoneArray.length && dist > Config.zoneArray[i].getBounds() && dist != -1){
								i++;
							}
						} else {
							while(i < Config.zoneArray.length && Config.zoneArray[i].getBounds() > -1){
								i++;
							}
						}

						if(Config.zoneArray[i].getPercent() == -1)
							cost = Config.zoneArray[i].getCost();
						else {
							cost = shippedToCollect.get(p.getName()).get(id - 1).getCost() * Config.zoneArray[i].getPercent();
							cost = Math.round(cost);
							cost /= 100;
						}
						if(RSEconomy.getBalance(p.getName()) >= cost) cont = true;
					} else cont = true;
					
					if(cont){
						RSEconomy.withdraw(p.getName(), cost);
						p.sendMessage(ChatColor.GREEN + "" + cost + LangPack.UNIT + " withdrawn from your account.");
						ItemStack[] contents = ((Chest)l.getBlock().getState()).getBlockInventory().getContents();
						for(ItemStack tempIS:contents) if(tempIS != null) p.getWorld().dropItem(p.getLocation(), tempIS);
						
						((Chest)l.getBlock().getState()).getBlockInventory().setContents(shippedToCollect.get(p.getName()).get(id - 1).getContents());
						p.sendMessage(ChatColor.GREEN + "Filled chest with: ");
						p.sendMessage(formatItemStackToMess(shippedToCollect.get(p.getName()).get(id - 1).getContents()));
						shippedToCollect.get(p.getName()).remove(id - 1);
						return true;
					} else p.sendMessage(ChatColor.RED + "You can't afford to pay the delivery fee of " + cost);
				} else p.sendMessage(ChatColor.RED + LangPack.THERESNOPACKAGEWITHTHEID + id);
			} else p.sendMessage(ChatColor.RED + LangPack.YOUHAVENTGOTANYITEMSWAITINGTOBEDELIVERED);
		} else p.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUARESTANDINGONISNTACHEST);
		return false;
	}

	/*
	 * 
	 * Utils and formatting functions
	 * 
	 */
	
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

	public static String formatPlayerListToMess(String[] formStr){
		String str = "";
		int newLn = 0;
		for(String s:formStr){
			boolean online = (Bukkit.getServer().getPlayerExact(s)==null)?false:true;
			String tempStr = "[" + (online?ChatColor.GREEN:ChatColor.RESET) + s + ChatColor.RESET + "] ";
			if((str + tempStr).substring(newLn).length() > 88){//84+4
				if(str.split("\n").length < 7){
					str += "\n";
					newLn = str.length();
					str += tempStr;
				} else {
					str += "...";
				}
			} else str += tempStr;
		}
		return str;
	}

	public String locAsString(Location l){
		return l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
	}
	
	public Location stringToLoc(String world, String s){
		return new Location(getServer().getWorld(world), Double.parseDouble(s.split(",")[0].trim()), Double.parseDouble(s.split(",")[1].trim()), Double.parseDouble(s.split(",")[2].trim()));
	}
	
	/*
	 * 
	 * Punishment functions
	 * 
	 */
	
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
             	if(blockState instanceof Chest) {
             	    Chest chest = (Chest)blockState;
             	    ItemStack[] iS = new ItemStack[27];
             	    for (int i = 0;i<17;i++) {
             	    	iS[i] = pS[i+9];
             	    }
             	    chest.getBlockInventory().clear();
             	    chest.getBlockInventory().setContents(iS);
             	}
             	if(blockState2 instanceof Chest) {
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
					if(diff >= 0){//If + then even more stolen left
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
		
		String own = shopMap.get(PInvMap.get(p.getName()).getStore()).owner;
		if(!own.equals("@admin")){//Return items if player store.
			Object[] keyss = stolen2.keySet().toArray();
			for(int i = 0;i < keyss.length;i++){
				int type = ((PItem) keyss[i]).type;
				ItemStack tempIS = ((PItem) keyss[i]).toItemStack();
				if(maxDurMap.containsKey(type)){
					if(stolen2.get(keyss[i]) > maxDurMap.get(type))
						while(maxDurMap.get(type) < stolen2.get(keyss[i])){//If more than one stack/full tool
							shopMap.get(PInvMap.get(p.getName()).getStore()).stolenToClaim.add(tempIS.clone());
							stolen2.put((PItem) keyss[i], stolen2.get(keyss[i]) - maxDurMap.get(type));
						}
					tempIS.setDurability((short) (maxDurMap.get(type) - stolen2.get(keyss[i])));
					shopMap.get(PInvMap.get(p.getName()).getStore()).stolenToClaim.add(tempIS);
				} else {
					if(stolen2.get(keyss[i]) > Material.getMaterial(type).getMaxStackSize())
						while(Material.getMaterial(type).getMaxStackSize() < stolen2.get(keyss[i])){//If more than one stack/full tool
							ItemStack tempIStemp = tempIS.clone();
							tempIStemp.setAmount(Material.getMaterial(type).getMaxStackSize());
							shopMap.get(PInvMap.get(p.getName()).getStore()).stolenToClaim.add(tempIStemp);
							stolen2.put((PItem) keyss[i], stolen2.get(keyss[i]) - Material.getMaterial(type).getMaxStackSize());
						}
					tempIS.setAmount(stolen2.get(keyss[i]));
					shopMap.get(PInvMap.get(p.getName()).getStore()).stolenToClaim.add(tempIS);
				}
			}
		}
    }
    
	/*
	 * 
	 * Get functions
	 * 
	 */
    
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

	public String[] getPlayersInStore(String store){
		String pString = "";
		String[] keys = PInvMap.keySet().toArray(new String[0]);
		for(String player:keys){
			if(PInvMap.get(player).getStore().toLowerCase().equals(store)){
				if(!pString.equals("")) pString += ",";
				pString += player;
			}
		}
		return pString.split(",");
	}
	
	public static boolean isChestProtected(Location l){
		Shop[] keys = shopMap.values().toArray(new Shop[0]);
		for(Shop temp:keys){
			if(temp.protectedChests.contains(l)) return true;
		}
		return false;
	}
	
	public String[] getOwnedStores(String player){
		String sString = ",";
		String[] keys = shopMap.keySet().toArray(new String[0]);
		for(String store:keys){
			if(shopMap.get(store).owner.toLowerCase().equals(player)) sString += store;
		}
		return sString.substring(1).split(",");
	}
	
	public Location[] getNearestTpLocs(Location loc, int maxAmount){
		Location[] keys = forbiddenTpLocs.keySet().toArray(new Location[0]);
		Location[] nearest = null;
		Map<Location, Double> locDist = new HashMap<Location, Double>();
		for(Location l:keys)
			if(loc.getWorld().equals(l.getWorld()))
				locDist.put(l, loc.distance(l));
		
		if(!locDist.isEmpty()){
			ValueComparator bvc =  new ValueComparator(locDist);
			TreeMap<Location,Double> sorted_map = new TreeMap<Location,Double>(bvc);
			sorted_map.putAll(locDist);
			
			
			if(sorted_map.size() < maxAmount) nearest = new Location[sorted_map.size()];
			else nearest = new Location[maxAmount];
			
			keys = sorted_map.keySet().toArray(new Location[0]);
			for(int i = 0;i < nearest.length;i++){
				nearest[i] = keys[i];
			}
		}
		
		return nearest;
	}
    
	public static void sendNotification(String who, String what){
		if(Config.notTimespan >= 500){
			if(!notificator.containsKey(who)) notificator.put(who, new ArrayList<String>());
			notificator.get(who).add(what);
		}
	}
	
    File getPFile(){
    	return this.getFile();
    }
    
	/*
	 * 
	 * Reload
	 * 
	 */
    
     public void reload(){
		smallReload = true;
		onDisable();
		onEnable();
    }

}

class Notificator extends Thread {
	
	public boolean running;
	
	public Notificator(){
		running = true;
	}
	
	
	public void run(){
		try {
			while(running){
				for(String s:RealShopping.notificator.keySet()){
					if(Bukkit.getPlayerExact(s) != null){
						for(int i = 0;i < 10 && 0 < RealShopping.notificator.get(s).size();i ++){
							Bukkit.getPlayerExact(s).sendMessage(ChatColor.LIGHT_PURPLE + "[RealShopping] " + ChatColor.RESET + RealShopping.notificator.get(s).get(0));
							RealShopping.notificator.get(s).remove(0);
						}
					}
				}
				Thread.sleep(Math.max(Config.notTimespan, 500));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}

class PricesParser extends DefaultHandler {
	int index = -1;
	List<Map<Price, Float>> mapList = new ArrayList<Map<Price, Float>>();
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
			mapList.add(new HashMap<Price, Float>());
			index ++;
		} else if(name.equalsIgnoreCase("item")){
			mapList.get(index).put(new Price(attr.getValue("id")), Float.parseFloat(attr.getValue("cost")));
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

class ValueComparator implements Comparator<Location> {

    Map<Location, Double> base;
    public ValueComparator(Map<Location, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(Location a, Location b) {
        if (base.get(a) >= base.get(b)) {
            return 1;
        } else {
            return -1;
        } // returning 0 would merge keys
    }
}

class StatComparator implements Comparator<String> {

    Map<String, Integer> base;
    public StatComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}