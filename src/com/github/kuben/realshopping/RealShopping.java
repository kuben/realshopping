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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.h31ix.updater.Updater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.github.kuben.realshopping.RSEconomy;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.commands.RSCommandExecutor;
import com.github.kuben.realshopping.exceptions.RealShoppingException;
import com.github.kuben.realshopping.listeners.RSPlayerListener;
import com.github.kuben.realshopping.prompts.PromptMaster;

//TODO better storing of database files

public class RealShopping extends JavaPlugin {//TODO stores case sensitive, players case preserving
	private Updater updater;
	private StatUpdater statUpdater;
	private Notificator notificatorThread;
	
	//Constants
	public static final String MANDIR = "plugins/RealShopping/";
	public static final String VERSION = "v0.50";
	public static final float VERFLOAT = 0.50f;
	
	//Vars
	private static Set<RSPlayerInventory> PInvSet;//Changed to set
	public static Map<String, Shop> shopMap;//TODO
	
	public static Map<String, PSetting> playerSettings;
	private static Map<EEPair, Shop> eePairs;
	private static Map<Price, Integer[]> defPrices;
	private static Map<Integer, Integer> maxDurMap;
	private static List<String> sortedAliases;
	private static Map<String, Integer[]> aliasesMap;
	private static Set<Integer> forbiddenInStore;
	private static Map<String, Location> playerEntrances;
	private static Map<String, Location> playerExits;
	private static Map<String, Location> jailedPlayers;
	private static Map<String, List<ShippedPackage>> shippedToCollect;
	private static Map<Location, Integer> forbiddenTpLocs;//TODO Maybe have a class for these, or enum
	private static Map<String, List<String>> notificator;
	
	private static boolean tpLocBlacklist;
	private static Location entrance;
	private static Location exit;

	private boolean smallReload = false;

	static Logger log;
	public static String working;
	public static String newUpdate;
	
	/*
	 * 
	 * Enable/Disable functions
	 * 
	 */
	
    public void onEnable(){
    	setUpdater(null);
    	statUpdater = null;
    	notificatorThread = null;
    	playerSettings = new HashMap<String, PSetting>();
    	eePairs = new HashMap<EEPair, Shop>();
      	defPrices = new HashMap<Price, Integer[]>();
    	PInvSet = new HashSet<RSPlayerInventory>();
    	maxDurMap = new HashMap<Integer, Integer>();
    	sortedAliases = null;//Is initialized in initAliases
    	aliasesMap = new HashMap<String, Integer[]>(801);
    	defPrices = new HashMap<Price, Integer[]>();
    	shopMap = new HashMap<String, Shop>();
    	playerEntrances = new HashMap<String, Location>();
    	playerExits = new HashMap<String, Location>();
    	jailedPlayers = new HashMap<String, Location>();
    	shippedToCollect = new HashMap<String, List<ShippedPackage>>();
    	forbiddenTpLocs = new HashMap<Location, Integer>();
    	notificator = new HashMap<String, List<String>>();
    	forbiddenInStore = new HashSet<Integer>();
    	tpLocBlacklist = false;
    	
    	Config.resetVars();
    	
        entrance = null;
        exit = null;
    	log = this.getLogger();
    	working = null;
    	
    	newUpdate = "";

    	if(!smallReload){
    		getServer().getPluginManager().registerEvents(new RSPlayerListener(), this);
    		RSCommandExecutor cmdExe = new RSCommandExecutor(this);
    		getCommand("rsenter").setExecutor(cmdExe);
    		getCommand("rsexit").setExecutor(cmdExe);
    		getCommand("rspay").setExecutor(cmdExe);
    		getCommand("rscost").setExecutor(cmdExe);
    		getCommand("rssell").setExecutor(cmdExe);
    		getCommand("rsprices").setExecutor(cmdExe);
    		getCommand("rsme").setExecutor(cmdExe);
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
    		getCommand("rsimport").setExecutor(cmdExe);
    		getCommand("realshopping").setExecutor(cmdExe);
    	}

   		working = "";
   		tpLocBlacklist = true;
        
   		PromptMaster.initialize(this);
   		RSEconomy.setupEconomy();
		Config.initialize();
		if(Config.getAutoUpdate() > 0){
			if(Config.getAutoUpdate() == 5){
				setUpdater(new Updater(this, "realshopping", this.getFile(), Updater.UpdateType.DEFAULT, true));
				if(getUpdater().getResult() == Updater.UpdateResult.SUCCESS){
					log.info(LangPack.REALSHOPPINGUPDATEDTO + getUpdater().getLatestVersionString() + LangPack.RESTARTTHESERVER_VERSION);
				}
			} else {
				setUpdater(new Updater(this, "realshopping", this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true));
				if(getUpdater().getResult() == Updater.UpdateResult.UPDATE_AVAILABLE){
	    			if(Config.getAutoUpdate() > 2)
    						newUpdate = getUpdater().getLatestVersionString() + LangPack.OFRE_UPDATECOMMAND;
	    			else
    						newUpdate = getUpdater().getLatestVersionString()
    						+ LangPack.OFRE_UPDATEINFO + ChatColor.LIGHT_PURPLE + "/rsupdate info";
	    			log.info(newUpdate);
				}
			}
		}
		
		File f;
		FileInputStream fstream;
		BufferedReader br;
		try {
			f = new File(MANDIR + "shops.db");
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
    		    		if(version >= 0.30) shopMap.get(tS[0]).setBuyFor(Integer.parseInt(tS[3]));
    		    		if(version >= 0.40){
    		    			byte notifyByte = 0;
    		    			if(tS[4].equals("notify")) notifyByte = 1;
    		    			else if(tS[4].equals("change")) notifyByte = 2;
    		    			shopMap.get(tS[0]).setNotifyChanges(notifyByte);
    		    			shopMap.get(tS[0]).setChangeTreshold(Integer.parseInt(tS[5]));
    		    			shopMap.get(tS[0]).setChangePercent(Integer.parseInt(tS[6]));
    		    			shopMap.get(tS[0]).setAllowNotifications(Boolean.parseBoolean(tS[7]));
    		    		}
    					for(int i = (version >= 0.40)?8:(version >= 0.30)?4:(version >= 0.20)?3:2;i < tS.length;i++){//The entrances + exits
    						String[] tSS = tS[i].split(",");
    			    		Location en = new Location(getServer().getWorld(tS[1]), Integer.parseInt(tSS[0]),Integer.parseInt(tSS[1]), Integer.parseInt(tSS[2]));
    			    		Location ex = new Location(getServer().getWorld(tS[1]), Integer.parseInt(tSS[3]),Integer.parseInt(tSS[4]), Integer.parseInt(tSS[5]));
    			    		try { shopMap.get(tS[0]).addEntranceExit(en, ex); } catch (RealShoppingException e) { }//Ignore
    					}
    					for(int i = 1;i < s.split(";").length;i++){//There are chests
    						Location l = new Location(getServer().getWorld(tS[1]), Integer.parseInt(s.split(";")[i].split("\\[")[0].split(",")[0])
    														, Integer.parseInt(s.split(";")[i].split("\\[")[0].split(",")[1])
    														, Integer.parseInt(s.split(";")[i].split("\\[")[0].split(",")[2]));
    						shopMap.get(tS[0]).addChest(l);
    						String idS = s.split(";")[i].split("\\[")[1].split("\\]")[0];
    						if(!idS.split(",")[0].trim().equals("")){
    							int[][] ids = new int[idS.split(",").length][3];
    							for(int j = 0;j < ids.length;j++){//The chests
    								if(idS.split(",")[j].contains(":")){
    									ids[j][0] = Integer.parseInt(idS.split(",")[j].split(":")[0].trim());
    									ids[j][1] = Integer.parseInt(idS.split(",")[j].split(":")[1].trim());
        								if(idS.split(",")[j].split(":").length > 2)
        									ids[j][2] = Integer.parseInt(idS.split(",")[j].split(":")[2].trim());
        								else ids[j][2] = 0;
    								} else {
    									ids[j][0] = Integer.parseInt(idS.split(",")[j].trim());
    									ids[j][1] = 0;
    									ids[j][2] = 0;
    								}
    							}
    							shopMap.get(tS[0]).addChestItem(l, ids);
    						}
    					}
    					int bIdx = s.indexOf("BANNED_");
    					if(bIdx > -1){//There are banned players
    						String[] banned = s.substring(bIdx + 7).split(",");
    						for(int i = 0;i < banned.length;i++){
    							shopMap.get(tS[0]).addBanned(banned[i]);
    						}
    					}
					}
				}
				fstream.close();
				br.close();
				if(version < VERFLOAT)//Needs updating
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
			f = new File(MANDIR + "prices.xml");
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
		//TODO load psettings and load default prices
		//TODO modify default prices
		
		f = new File(MANDIR + "langpacks/");
		if(!f.exists()) f.mkdir();
    	LangPack.initialize(Config.getLangpack());
		initForbiddenInStore();
		initMaxDur();
		initAliases();
		if(Config.getNotTimespan() >= 500){
			notificatorThread = new Notificator();
			notificatorThread.start();
		}
		if(Config.isEnableAI()){
			statUpdater = new StatUpdater();
			statUpdater.start();
		}
		log.info(LangPack.REALSHOPPINGINITIALIZED);
    }
     
    public void onDisable(){
		try {
			PromptMaster.abandonAllConversations();
			//TODO disable executor
			saveTemporaryFile(0);//Inventories
			saveTemporaryFile(1);//Jailed
			saveTemporaryFile(2);//TpLocs
			saveTemporaryFile(3);//Protected chests
			saveTemporaryFile(4);//Shipped Packages
			saveTemporaryFile(5);//toClaim
			saveTemporaryFile(6);//Stats
			saveTemporaryFile(7);//Notifications
			RSEconomy.export();//This will only happen if econ is null
			
			if(notificatorThread != null) notificatorThread.running = false;
			if(statUpdater != null) statUpdater.running = false;
			
			//Write prices to xml

			File f = new File(MANDIR+"prices.xml");//Reset file
			if(f.exists()) f.delete();
			
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
	        Document doc = docBuilder.newDocument();
	            

	        Element root = doc.createElement("prices");
	        doc.appendChild(root);
	        doc.appendChild(doc.createComment("If you want to manually edit this file, do it when your server is down. Your changes won't be saved otherwise!"));
	           
	        Map<Price, Integer[]> tempMap;
	        Object[] keys = shopMap.keySet().toArray();
	        for(int i = 0;i < keys.length;i++){	
	        	Element shop = doc.createElement("shop");
	        	shop.setAttribute("name", shopMap.get(keys[i]).getName());
	        	root.appendChild(shop);
	            	
	            tempMap = shopMap.get(keys[i]).getPricesMap();
	          	Object[] ids = tempMap.keySet().toArray();
	           	for(int j = 0;j < ids.length;j++){
	           		Element item = doc.createElement("item");
	                item.setAttribute("id", ids[j].toString());
	                Integer[] p = tempMap.get(ids[j]);
	                item.setAttribute("cost", (((float)p[0])/100) + "");//Save as decimal numbers
	                if(p.length == 3){
	                	item.setAttribute("min", (((float)p[1])/100) + "");
	                	item.setAttribute("max", (((float)p[2])/100) + "");
	                }
	                shop.appendChild(item);
	           	}
	        }
	        
	        DOMSource source = new DOMSource(doc);

	        PrintStream ps = new PrintStream(MANDIR+"prices.xml");
	        StreamResult result = new StreamResult(ps);
	        
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            transformer.transform(source, result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
    	log.info(LangPack.REALSHOPPINGDISABLED);
    }

    public static void updateEntrancesDb(){
    	//Update file
    	long tstamp = System.nanoTime();
		try {
			File f = new File(MANDIR + "shops.db");
			if(!f.exists()) f.createNewFile();
			PrintWriter pW = new PrintWriter(f);
			Object[] keys = shopMap.keySet().toArray();
			pW.println("Shops database for RealShopping " + VERSION);
			for(int i = 0;i<keys.length;i++){
				Shop tempShop = shopMap.get(keys[i]);
				pW.print(keys[i] + ":" + tempShop.getWorld() + ":" + tempShop.getOwner() + ":" + tempShop.getBuyFor()
						+ ":" + (tempShop.getNotifyChanges()==2?"change":(tempShop.getNotifyChanges()==1?"notify":"nothing"))
						+ ":" + tempShop.getChangeTreshold() + ":" + tempShop.getChangePercent() + ":" + tempShop.allowsNotifications());
				for(EEPair ee:eePairs.keySet()){
					if(eePairs.get(ee).equals(tempShop))
						pW.print(":" + RSUtils.locAsString(ee.getEntrance()) + "," + RSUtils.locAsString(ee.getExit()));
				}
				Map<Location, ArrayList<Integer[]>> tempChests = tempShop.getChests();
				Object[] chestLocs = tempChests.keySet().toArray();
				for(int j = 0;j < chestLocs.length;j++){
					String items = "";
					for(Integer[] ii:tempChests.get(chestLocs[j])){
						if(!items.equals("")) items += ",";
						items += ii[0];
						if(ii.length > 1 && ii[1] != 0) items += ":" + ii[1];
						if(ii.length > 2 && ii[2] != 0) items += ":" + ii[2];
					}
					pW.print(";" + RSUtils.locAsString((Location) chestLocs[j]) + "[ " + items + "]");
				}
				Object[] banned = tempShop.getBanned().toArray();
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
		if(Config.debug) log.info("Finished updating shops.db in " + (System.nanoTime() - tstamp) + "ns");
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
    	forbiddenInStore.add(391);//Carrot
    	forbiddenInStore.add(392);//Potato
    	forbiddenInStore.add(393);//Baked Potato
    	forbiddenInStore.add(394);//Poisonous Potato
    	forbiddenInStore.add(396);//Golden Carrot
    	forbiddenInStore.add(400);//Pumpkin Pie
    	
    	forbiddenInStore.add(261);//Bow
    	forbiddenInStore.add(332);//Snowball
    	forbiddenInStore.add(373);//Potion
    	forbiddenInStore.add(384);//Bottle o' enchanting
    	forbiddenInStore.add(375);//Spider Eye
    	forbiddenInStore.add(385);//Fire Charge
    	forbiddenInStore.add(344);//Egg
    	forbiddenInStore.add(368);//Ender Pearl
    	forbiddenInStore.add(381);//Eye of ender
    	forbiddenInStore.add(401);//Firework Rocket
    	//forbiddenInStore.add(386);//Book and Quill doesn't work
    	forbiddenInStore.add(383);//Spawning egg
    }

    private void initAliases(){
    	aliasesMap.put("stone", new Integer[]{1});
    	aliasesMap.put("grass", new Integer[]{2});
    	aliasesMap.put("dirt", new Integer[]{3});
    	aliasesMap.put("cobble", new Integer[]{4});
    	aliasesMap.put("cobblestone", new Integer[]{4});
    	aliasesMap.put("plank", new Integer[]{5});
    	aliasesMap.put("oakplank", new Integer[]{5,0});
    	aliasesMap.put("spruceplank", new Integer[]{5,1});
    	aliasesMap.put("brichplank", new Integer[]{5,2});
    	aliasesMap.put("jungleplank", new Integer[]{5,3});
    	aliasesMap.put("planks", new Integer[]{5});
    	aliasesMap.put("oakplanks", new Integer[]{5,0});
    	aliasesMap.put("spruceplanks", new Integer[]{5,1});
    	aliasesMap.put("brichplanks", new Integer[]{5,2});
    	aliasesMap.put("jungleplanks", new Integer[]{5,3});
    	aliasesMap.put("sapling", new Integer[]{6});
    	aliasesMap.put("oaksapling", new Integer[]{6,0});
    	aliasesMap.put("sprucesapling", new Integer[]{6,1});
    	aliasesMap.put("birchsapling", new Integer[]{6,2});
    	aliasesMap.put("junglesapling", new Integer[]{6,3});
    	aliasesMap.put("bedrock", new Integer[]{7});
    	aliasesMap.put("water", new Integer[]{8});
    	aliasesMap.put("stationarywater", new Integer[]{9});
    	aliasesMap.put("lava", new Integer[]{10});
    	aliasesMap.put("stationarylava", new Integer[]{11});
    	aliasesMap.put("sand", new Integer[]{12});
    	aliasesMap.put("gravel", new Integer[]{13});
    	aliasesMap.put("goldore", new Integer[]{14});
    	aliasesMap.put("ironore", new Integer[]{15});
    	aliasesMap.put("coalore", new Integer[]{16});
    	aliasesMap.put("wood", new Integer[]{17});
    	aliasesMap.put("oakwood", new Integer[]{17,0});
    	aliasesMap.put("sprucewood", new Integer[]{17,1});
    	aliasesMap.put("birchwood", new Integer[]{17,2});
    	aliasesMap.put("junglewood", new Integer[]{17,3});
    	aliasesMap.put("leaves", new Integer[]{18});
    	aliasesMap.put("oakleaves", new Integer[]{18,0});
    	aliasesMap.put("spruceleaves", new Integer[]{18,1});
    	aliasesMap.put("birchleaves", new Integer[]{18,2});
    	aliasesMap.put("jungleleaves", new Integer[]{18,3});
    	aliasesMap.put("sponge", new Integer[]{19});
    	aliasesMap.put("glass", new Integer[]{20});
    	aliasesMap.put("lapisore", new Integer[]{21});
    	aliasesMap.put("lapisblock", new Integer[]{22});
    	aliasesMap.put("lapislazuliore", new Integer[]{21});
    	aliasesMap.put("lapislazuliblock", new Integer[]{22});
    	aliasesMap.put("dispenser", new Integer[]{23});
    	aliasesMap.put("sandstone", new Integer[]{24});
    	aliasesMap.put("chiseledsandstone", new Integer[]{24,1});
    	aliasesMap.put("smoothsandstone", new Integer[]{24,2});
    	aliasesMap.put("noteblock", new Integer[]{25});
    	aliasesMap.put("bedblock", new Integer[]{26});
    	aliasesMap.put("poweredrail", new Integer[]{27});
    	aliasesMap.put("detectorrail", new Integer[]{28});
    	aliasesMap.put("stickypiston", new Integer[]{29});
    	aliasesMap.put("web", new Integer[]{30});
    	aliasesMap.put("deadshrub", new Integer[]{31});
    	aliasesMap.put("grass", new Integer[]{31,1});
    	aliasesMap.put("fern", new Integer[]{31,2});
    	aliasesMap.put("deadshrub", new Integer[]{32});
    	aliasesMap.put("piston", new Integer[]{33});
    	aliasesMap.put("pistonhead", new Integer[]{34});
    	aliasesMap.put("wool", new Integer[]{35});
    	aliasesMap.put("whitewool", new Integer[]{35,0});
    	aliasesMap.put("orangewool", new Integer[]{35,1});
    	aliasesMap.put("magentawool", new Integer[]{35,2});
    	aliasesMap.put("lightbluewool", new Integer[]{35,3});
    	aliasesMap.put("yellowwool", new Integer[]{35,4});
    	aliasesMap.put("lightgreenwool", new Integer[]{35,5});
    	aliasesMap.put("pinkwool", new Integer[]{35,6});
    	aliasesMap.put("graywool", new Integer[]{35,7});
    	aliasesMap.put("lightgraywool", new Integer[]{35,8});
    	aliasesMap.put("cyanwool", new Integer[]{35,9});
    	aliasesMap.put("purplewool", new Integer[]{35,10});
    	aliasesMap.put("bluewool", new Integer[]{35,11});
    	aliasesMap.put("brownwool", new Integer[]{35,12});
    	aliasesMap.put("darkgreenwool", new Integer[]{35,13});
    	aliasesMap.put("redwool", new Integer[]{35,14});
    	aliasesMap.put("blackwool", new Integer[]{35,15});
    	aliasesMap.put("dandelion", new Integer[]{37});
    	aliasesMap.put("rose", new Integer[]{38});
    	aliasesMap.put("brownmushroom", new Integer[]{39});
    	aliasesMap.put("redmushroom", new Integer[]{40});
    	aliasesMap.put("goldblock", new Integer[]{41});
    	aliasesMap.put("ironblock", new Integer[]{42});
    	aliasesMap.put("doubleslab", new Integer[]{43});
    	aliasesMap.put("doublestoneslab", new Integer[]{43,0});
    	aliasesMap.put("doublesandstoneslab", new Integer[]{43,1});
    	aliasesMap.put("doublewoodenslab", new Integer[]{43,2});
    	aliasesMap.put("doublecobblestoneslab", new Integer[]{43,3});
    	aliasesMap.put("doublebrickslab", new Integer[]{43,4});
    	aliasesMap.put("doublestonebrickslab", new Integer[]{43,5});
    	aliasesMap.put("doublenetherbrickslab", new Integer[]{43,6});
    	aliasesMap.put("doublequartzslab", new Integer[]{43,7});
    	aliasesMap.put("slab", new Integer[]{44});
    	aliasesMap.put("stoneslab", new Integer[]{44,0});
    	aliasesMap.put("sandstoneslab", new Integer[]{44,1});
    	aliasesMap.put("woodenslab", new Integer[]{44,2});
    	aliasesMap.put("cobblestoneslab", new Integer[]{44,3});
    	aliasesMap.put("brickslab", new Integer[]{44,4});
    	aliasesMap.put("stonebrickslab", new Integer[]{44,5});
    	aliasesMap.put("netherbrickslab", new Integer[]{44,6});
    	aliasesMap.put("quartzslab", new Integer[]{44,7});
    	aliasesMap.put("brickblock", new Integer[]{45});
    	aliasesMap.put("tnt", new Integer[]{46});
    	aliasesMap.put("bookshelf", new Integer[]{47});
    	aliasesMap.put("mossycobble", new Integer[]{48});
    	aliasesMap.put("mossycobblestone", new Integer[]{48});
    	aliasesMap.put("obsidian", new Integer[]{49});
    	aliasesMap.put("torch", new Integer[]{50});
    	aliasesMap.put("fire", new Integer[]{51});
    	aliasesMap.put("monsterspawner", new Integer[]{52});
    	aliasesMap.put("woodenstair", new Integer[]{53});
    	aliasesMap.put("woodstair", new Integer[]{53});
    	aliasesMap.put("woodenstairs", new Integer[]{53});
    	aliasesMap.put("woodstairs", new Integer[]{53});
    	aliasesMap.put("chest", new Integer[]{54});
    	aliasesMap.put("redstonewire", new Integer[]{55});
    	aliasesMap.put("diamondore", new Integer[]{56});
    	aliasesMap.put("diamondblock", new Integer[]{57});
    	aliasesMap.put("workbench", new Integer[]{58});
    	aliasesMap.put("wheatcrops", new Integer[]{59});
    	aliasesMap.put("farmland", new Integer[]{60});
    	aliasesMap.put("soil", new Integer[]{60});
    	aliasesMap.put("furnace", new Integer[]{61});
    	aliasesMap.put("burningfurnace", new Integer[]{62});
    	aliasesMap.put("signpost", new Integer[]{63});
    	aliasesMap.put("woodendoorblock", new Integer[]{64});
    	aliasesMap.put("ladder", new Integer[]{65});
    	aliasesMap.put("rail", new Integer[]{66});
    	aliasesMap.put("rails", new Integer[]{66});
    	aliasesMap.put("cobblestair", new Integer[]{67});
    	aliasesMap.put("cobblestonestair", new Integer[]{67});
    	aliasesMap.put("cobblestairs", new Integer[]{67});
    	aliasesMap.put("cobblestonestairs", new Integer[]{67});
    	aliasesMap.put("wallsign", new Integer[]{68});
    	aliasesMap.put("lever", new Integer[]{69});
    	aliasesMap.put("stonepressureplate", new Integer[]{70});
    	aliasesMap.put("irondoorblock", new Integer[]{71});
    	aliasesMap.put("woodpressureplate", new Integer[]{72});
    	aliasesMap.put("woodenpressureplate", new Integer[]{72});
    	aliasesMap.put("redstoneore", new Integer[]{73});
    	aliasesMap.put("glowingredstoneore", new Integer[]{74});
    	aliasesMap.put("redstonetorch", new Integer[]{75});
    	aliasesMap.put("redstonetorchon", new Integer[]{76});
    	aliasesMap.put("stonebutton", new Integer[]{77});
    	aliasesMap.put("snow", new Integer[]{78});
    	aliasesMap.put("ice", new Integer[]{79});
    	aliasesMap.put("snowblock", new Integer[]{80});
    	aliasesMap.put("cactus", new Integer[]{81});
    	aliasesMap.put("clayblock", new Integer[]{82});
    	aliasesMap.put("sugarcane", new Integer[]{83});
    	aliasesMap.put("jukebox", new Integer[]{84});
    	aliasesMap.put("fence", new Integer[]{85});
    	aliasesMap.put("pumpkin", new Integer[]{86});
    	aliasesMap.put("netherrack", new Integer[]{87});
    	aliasesMap.put("soulsand", new Integer[]{88});
    	aliasesMap.put("glowstone", new Integer[]{89});
    	aliasesMap.put("portalblock", new Integer[]{90});
    	aliasesMap.put("jack-o-lantern", new Integer[]{91});
    	aliasesMap.put("cakeblock", new Integer[]{92});
    	aliasesMap.put("redstonerepeaterblockoff", new Integer[]{93});
    	aliasesMap.put("redstonerepeaterblockon", new Integer[]{94});
    	aliasesMap.put("lockedchest", new Integer[]{95});
    	aliasesMap.put("trapdoor", new Integer[]{96});
    	aliasesMap.put("monsteregg", new Integer[]{97});
    	aliasesMap.put("silverfishblock", new Integer[]{97});
    	aliasesMap.put("silverfishstone", new Integer[]{97,0});
    	aliasesMap.put("silverfishcobble", new Integer[]{97,1});
    	aliasesMap.put("silverfishcobblestone", new Integer[]{97,1});
    	aliasesMap.put("silverfishstonebricks", new Integer[]{97,2});
    	aliasesMap.put("silverfishstonebrick", new Integer[]{97,2});
    	aliasesMap.put("stonebricks", new Integer[]{98});
    	aliasesMap.put("mossystonebricks", new Integer[]{98,1});
    	aliasesMap.put("crackedstonebricks", new Integer[]{98,2});
    	aliasesMap.put("chiseledstonebricks", new Integer[]{98,3});
    	aliasesMap.put("stonebrick", new Integer[]{98});
    	aliasesMap.put("mossystonebrick", new Integer[]{98,1});
    	aliasesMap.put("crackedstonebrick", new Integer[]{98,2});
    	aliasesMap.put("chiseledstonebrick", new Integer[]{98,3});
    	aliasesMap.put("redhugemushroom", new Integer[]{99});
    	aliasesMap.put("brownhugemushroom", new Integer[]{100});
    	aliasesMap.put("ironbars", new Integer[]{101});
    	aliasesMap.put("glasspane", new Integer[]{102});
    	aliasesMap.put("melonblock", new Integer[]{103});
    	aliasesMap.put("pumpkinstem", new Integer[]{104});
    	aliasesMap.put("melonstem", new Integer[]{105});
    	aliasesMap.put("vines", new Integer[]{106});
    	aliasesMap.put("fencegate", new Integer[]{107});
    	aliasesMap.put("brickstair", new Integer[]{108});
    	aliasesMap.put("stonebrickstair", new Integer[]{109});
    	aliasesMap.put("brickstairs", new Integer[]{108});
    	aliasesMap.put("stonebrickstairs", new Integer[]{109});
    	aliasesMap.put("mycelium", new Integer[]{110});
    	aliasesMap.put("lilypad", new Integer[]{111});
    	aliasesMap.put("netherbrick", new Integer[]{112});
    	aliasesMap.put("netherbrickfence", new Integer[]{113});
    	aliasesMap.put("netherbrickstair", new Integer[]{114});
    	aliasesMap.put("netherbrickstairs", new Integer[]{114});
    	aliasesMap.put("netherwartcrops", new Integer[]{115});
    	aliasesMap.put("enchantmenttable", new Integer[]{116});
    	aliasesMap.put("brewingstand", new Integer[]{117});
    	aliasesMap.put("cauldron", new Integer[]{118});
    	aliasesMap.put("endportal", new Integer[]{119});
    	aliasesMap.put("endportalframe", new Integer[]{120});
    	aliasesMap.put("endstone", new Integer[]{121});
    	aliasesMap.put("dragonegg", new Integer[]{122});
    	aliasesMap.put("redstonelamp", new Integer[]{123});
    	aliasesMap.put("redstonelampon", new Integer[]{124});
    	aliasesMap.put("doublewoodenslab", new Integer[]{125});
    	aliasesMap.put("doublewoodslab", new Integer[]{125});
    	aliasesMap.put("doubleoakwoodslab", new Integer[]{125,0});
    	aliasesMap.put("doublesprucewoodslab", new Integer[]{125,1});
    	aliasesMap.put("doublebirchwoodslab", new Integer[]{125,2});
    	aliasesMap.put("doublejunglewoodslab", new Integer[]{125,3});
    	aliasesMap.put("woodslab", new Integer[]{126});
    	aliasesMap.put("woodenslab", new Integer[]{126});
    	aliasesMap.put("oakwoodslab", new Integer[]{126,0});
    	aliasesMap.put("sprucewoodslab", new Integer[]{126,1});
    	aliasesMap.put("birchwoodslab", new Integer[]{126,2});
    	aliasesMap.put("junglewoodslab", new Integer[]{126,3});
    	aliasesMap.put("cocoaplant", new Integer[]{127});
    	aliasesMap.put("sandstonestairs", new Integer[]{128});
    	aliasesMap.put("emeraldore", new Integer[]{129});
    	aliasesMap.put("enderchest", new Integer[]{130});
    	aliasesMap.put("tripwirehook", new Integer[]{131});
    	aliasesMap.put("tripwire", new Integer[]{132});
    	aliasesMap.put("emeraldblock", new Integer[]{133});
    	aliasesMap.put("sprucewoodstair", new Integer[]{134});
    	aliasesMap.put("birchwoodstair", new Integer[]{135});
    	aliasesMap.put("junglewoodstair", new Integer[]{136});
    	aliasesMap.put("sprucewoodstairs", new Integer[]{134});
    	aliasesMap.put("birchwoodstairs", new Integer[]{135});
    	aliasesMap.put("junglewoodstairs", new Integer[]{136});
    	aliasesMap.put("commandblock", new Integer[]{137});
    	aliasesMap.put("beacon", new Integer[]{138});
    	aliasesMap.put("beaconblock", new Integer[]{138});
    	aliasesMap.put("cobblewall", new Integer[]{139});
    	aliasesMap.put("mossycobblewall", new Integer[]{139,1});
    	aliasesMap.put("cobblestonewall", new Integer[]{139});
    	aliasesMap.put("mossycobblestonewall", new Integer[]{139,1});
    	aliasesMap.put("flowerpot", new Integer[]{140});
    	aliasesMap.put("carrotcrops", new Integer[]{141});
    	aliasesMap.put("potatocrops", new Integer[]{142});
    	aliasesMap.put("woodbutton", new Integer[]{143});
    	aliasesMap.put("woodenbutton", new Integer[]{143});
    	aliasesMap.put("mobheadblock", new Integer[]{144});
    	aliasesMap.put("anvil", new Integer[]{145});
    	aliasesMap.put("trappedchest", new Integer[]{146});
    	aliasesMap.put("lightpressureplate", new Integer[]{147});
    	aliasesMap.put("heavypressureplate", new Integer[]{148});
    	aliasesMap.put("lightweightedpressureplate", new Integer[]{147});
    	aliasesMap.put("heavyweightedpressureplate", new Integer[]{148});
    	aliasesMap.put("redstonecomparatorblockoff", new Integer[]{149});
    	aliasesMap.put("redstonecomparatorblockon", new Integer[]{150});
    	aliasesMap.put("daylightsensor", new Integer[]{151});
    	aliasesMap.put("redstoneblock", new Integer[]{152});
    	aliasesMap.put("quartzore", new Integer[]{153});
    	aliasesMap.put("netherquartzore", new Integer[]{153});
    	aliasesMap.put("hopper", new Integer[]{154});
    	aliasesMap.put("quartzblock", new Integer[]{155});
    	aliasesMap.put("chiseledquartzblock", new Integer[]{155,1});
    	aliasesMap.put("pillarquartzblock", new Integer[]{155,2});
    	aliasesMap.put("quartzstair", new Integer[]{156});
    	aliasesMap.put("quartzstairs", new Integer[]{156});
    	aliasesMap.put("activatorrail", new Integer[]{157});
    	aliasesMap.put("dropper", new Integer[]{158});
    	aliasesMap.put("ironshovel", new Integer[]{256});
    	aliasesMap.put("ironpickaxe", new Integer[]{257});
    	aliasesMap.put("ironaxe", new Integer[]{258});
    	aliasesMap.put("flintandsteel", new Integer[]{259});
    	aliasesMap.put("apple", new Integer[]{260});
    	aliasesMap.put("bow", new Integer[]{261});
    	aliasesMap.put("arrow", new Integer[]{262});
    	aliasesMap.put("coal", new Integer[]{263});
    	aliasesMap.put("charcoal", new Integer[]{263,1});
    	aliasesMap.put("diamond", new Integer[]{264});
    	aliasesMap.put("ironingot", new Integer[]{265});
    	aliasesMap.put("goldingot", new Integer[]{266});
    	aliasesMap.put("ironsword", new Integer[]{267});
    	aliasesMap.put("woodsword", new Integer[]{268});
    	aliasesMap.put("woodshovel", new Integer[]{269});
    	aliasesMap.put("woodpickaxe", new Integer[]{270});
    	aliasesMap.put("woodaxe", new Integer[]{271});
    	aliasesMap.put("woodensword", new Integer[]{268});
    	aliasesMap.put("woodenshovel", new Integer[]{269});
    	aliasesMap.put("woodenpickaxe", new Integer[]{270});
    	aliasesMap.put("woodenaxe", new Integer[]{271});
    	aliasesMap.put("stonesword", new Integer[]{272});
    	aliasesMap.put("stoneshovel", new Integer[]{273});
    	aliasesMap.put("stonepickaxe", new Integer[]{274});
    	aliasesMap.put("stoneaxe", new Integer[]{275});
    	aliasesMap.put("diamondsword", new Integer[]{276});
    	aliasesMap.put("diamondshovel", new Integer[]{277});
    	aliasesMap.put("diamondpickaxe", new Integer[]{278});
    	aliasesMap.put("diamondaxe", new Integer[]{279});
    	aliasesMap.put("sticks", new Integer[]{280});
    	aliasesMap.put("stick", new Integer[]{280});
    	aliasesMap.put("bowl", new Integer[]{281});
    	aliasesMap.put("mushroomstew", new Integer[]{282});
    	aliasesMap.put("mushroomsoup", new Integer[]{282});
    	aliasesMap.put("goldsword", new Integer[]{283});
    	aliasesMap.put("goldshovel", new Integer[]{284});
    	aliasesMap.put("goldpickaxe", new Integer[]{285});
    	aliasesMap.put("goldaxe", new Integer[]{286});
    	aliasesMap.put("string", new Integer[]{287});
    	aliasesMap.put("feather", new Integer[]{288});
    	aliasesMap.put("gunpowder", new Integer[]{289});
    	aliasesMap.put("sulphur", new Integer[]{289});
    	aliasesMap.put("woodhoe", new Integer[]{290});
    	aliasesMap.put("woodenhoe", new Integer[]{290});
    	aliasesMap.put("stonehoe", new Integer[]{291});
    	aliasesMap.put("ironhoe", new Integer[]{292});
    	aliasesMap.put("diamondhoe", new Integer[]{293});
    	aliasesMap.put("goldhoe", new Integer[]{294});
    	aliasesMap.put("wheatseeds", new Integer[]{295});
    	aliasesMap.put("wheat", new Integer[]{296});
    	aliasesMap.put("bread", new Integer[]{297});
    	aliasesMap.put("leatherhelmet", new Integer[]{298});
    	aliasesMap.put("leatherchestplate", new Integer[]{299});
    	aliasesMap.put("leatherleggings", new Integer[]{300});
    	aliasesMap.put("leatherboots", new Integer[]{301});
    	aliasesMap.put("chainmailhelmet", new Integer[]{302});
    	aliasesMap.put("chainmailchestplate", new Integer[]{303});
    	aliasesMap.put("chainmailleggings", new Integer[]{304});
    	aliasesMap.put("chainmailboots", new Integer[]{305});
    	aliasesMap.put("ironhelmet", new Integer[]{306});
    	aliasesMap.put("ironchestplate", new Integer[]{307});
    	aliasesMap.put("ironleggings", new Integer[]{308});
    	aliasesMap.put("ironboots", new Integer[]{309});
    	aliasesMap.put("diamondhelmet", new Integer[]{310});
    	aliasesMap.put("diamondchestplate", new Integer[]{311});
    	aliasesMap.put("diamondleggings", new Integer[]{312});
    	aliasesMap.put("diamondboots", new Integer[]{313});
    	aliasesMap.put("goldhelmet", new Integer[]{314});
    	aliasesMap.put("goldchestplate", new Integer[]{315});
    	aliasesMap.put("goldleggings", new Integer[]{316});
    	aliasesMap.put("goldboots", new Integer[]{317});
    	aliasesMap.put("flint", new Integer[]{318});
    	aliasesMap.put("rawporkchop", new Integer[]{319});
    	aliasesMap.put("pork", new Integer[]{320});
    	aliasesMap.put("porkchop", new Integer[]{320});
    	aliasesMap.put("cookedpork", new Integer[]{320});
    	aliasesMap.put("cookedporkchop", new Integer[]{320});
    	aliasesMap.put("painting", new Integer[]{321});
    	aliasesMap.put("goldenapple", new Integer[]{322});
    	aliasesMap.put("enchantedgoldenapple", new Integer[]{322,1});
    	aliasesMap.put("sign", new Integer[]{323});
    	aliasesMap.put("wooddoor", new Integer[]{324});
    	aliasesMap.put("woodendoor", new Integer[]{324});
    	aliasesMap.put("bucket", new Integer[]{325});
    	aliasesMap.put("waterbucket", new Integer[]{326});
    	aliasesMap.put("lavabucket", new Integer[]{327});
    	aliasesMap.put("minecart", new Integer[]{328});
    	aliasesMap.put("saddle", new Integer[]{329});
    	aliasesMap.put("irondoor", new Integer[]{330});
    	aliasesMap.put("redstone", new Integer[]{331});
    	aliasesMap.put("snowball", new Integer[]{332});
    	aliasesMap.put("boat", new Integer[]{333});
    	aliasesMap.put("leather", new Integer[]{334});
    	aliasesMap.put("milkbucket", new Integer[]{335});
    	aliasesMap.put("brick", new Integer[]{336});
    	aliasesMap.put("claybrick", new Integer[]{336});
    	aliasesMap.put("clayballs", new Integer[]{337});
    	aliasesMap.put("clay", new Integer[]{337});
    	aliasesMap.put("sugarcane", new Integer[]{338});
    	aliasesMap.put("paper", new Integer[]{339});
    	aliasesMap.put("book", new Integer[]{340});
    	aliasesMap.put("slimeball", new Integer[]{341});
    	aliasesMap.put("chestminecraft", new Integer[]{342});
    	aliasesMap.put("minecraftwithchest", new Integer[]{342});
    	aliasesMap.put("storageminecart", new Integer[]{342});
    	aliasesMap.put("minecartwithfurnace", new Integer[]{343});
    	aliasesMap.put("furnaceminecart", new Integer[]{343});
    	aliasesMap.put("poweredminecart", new Integer[]{343});
    	aliasesMap.put("egg", new Integer[]{344});
    	aliasesMap.put("compass", new Integer[]{345});
    	aliasesMap.put("fishingpole", new Integer[]{346});
    	aliasesMap.put("fishingrod", new Integer[]{346});
    	aliasesMap.put("clock", new Integer[]{347});
    	aliasesMap.put("glowstone", new Integer[]{348});
    	aliasesMap.put("glowstonedust", new Integer[]{348});
    	aliasesMap.put("rawfish", new Integer[]{349});
    	aliasesMap.put("fish", new Integer[]{350});
    	aliasesMap.put("cookedfish", new Integer[]{350});
    	aliasesMap.put("dye", new Integer[]{351});
    	aliasesMap.put("inksack", new Integer[]{351,0});
    	aliasesMap.put("rosered", new Integer[]{351,1});
    	aliasesMap.put("cactusgreen", new Integer[]{351,2});
    	aliasesMap.put("cocobeans", new Integer[]{351,3});
    	aliasesMap.put("lapislazuli", new Integer[]{351,4});
    	aliasesMap.put("purpledye", new Integer[]{351,5});
    	aliasesMap.put("cyandye", new Integer[]{351,6});
    	aliasesMap.put("lightgraydye", new Integer[]{351,7});
    	aliasesMap.put("graydye", new Integer[]{351,8});
    	aliasesMap.put("pinkdye", new Integer[]{351,9});
    	aliasesMap.put("limedye", new Integer[]{351,10});
    	aliasesMap.put("dandelionyellow", new Integer[]{351,11});
    	aliasesMap.put("lightbluedye", new Integer[]{351,12});
    	aliasesMap.put("magentadye", new Integer[]{351,13});
    	aliasesMap.put("orangedye", new Integer[]{351,14});
    	aliasesMap.put("bonemeal", new Integer[]{351,15});
    	aliasesMap.put("bone", new Integer[]{352});
    	aliasesMap.put("sugar", new Integer[]{353});
    	aliasesMap.put("cake", new Integer[]{354});
    	aliasesMap.put("bed", new Integer[]{355});
    	aliasesMap.put("repeater", new Integer[]{356});
    	aliasesMap.put("redstonerepeater", new Integer[]{356});
    	aliasesMap.put("cookie", new Integer[]{357});
    	aliasesMap.put("map", new Integer[]{358});
    	aliasesMap.put("shears", new Integer[]{359});
    	aliasesMap.put("melon", new Integer[]{360});
    	aliasesMap.put("pumpkinseeds", new Integer[]{361});
    	aliasesMap.put("melonseeds", new Integer[]{362});
    	aliasesMap.put("rawbeef", new Integer[]{363});
    	aliasesMap.put("cookedbeef", new Integer[]{364});
    	aliasesMap.put("steak", new Integer[]{364});
    	aliasesMap.put("rawchicken", new Integer[]{365});
    	aliasesMap.put("chicken", new Integer[]{366});
    	aliasesMap.put("cookedchicken", new Integer[]{366});
    	aliasesMap.put("rottenflesh", new Integer[]{367});
    	aliasesMap.put("enderpearl", new Integer[]{368});
    	aliasesMap.put("blazerod", new Integer[]{369});
    	aliasesMap.put("ghasttear", new Integer[]{370});
    	aliasesMap.put("goldnugget", new Integer[]{371});
    	aliasesMap.put("netherwart", new Integer[]{372});
    	aliasesMap.put("potion", new Integer[]{373});
    	aliasesMap.put("waterbottle", new Integer[]{373,0});
    	aliasesMap.put("awkwardpotion", new Integer[]{373,16});
    	aliasesMap.put("thickpotion", new Integer[]{373,32});
    	aliasesMap.put("mundanepotion", new Integer[]{373,64});
    	aliasesMap.put("regenerationpotion", new Integer[]{373,8193});
    	aliasesMap.put("regenerationpotionlong", new Integer[]{373,8257});
    	aliasesMap.put("regenerationpotion2", new Integer[]{373,8225});
    	aliasesMap.put("regenerationpotion2long", new Integer[]{373,8289});
    	aliasesMap.put("swiftnesspotion", new Integer[]{373,8194});
    	aliasesMap.put("swiftnesspotionlong", new Integer[]{373,8258});
    	aliasesMap.put("swiftnesspotion2", new Integer[]{373,8226});
    	aliasesMap.put("swiftnesspotion2long", new Integer[]{373,8290});
    	aliasesMap.put("fireresistancepotion", new Integer[]{373,8195});
    	aliasesMap.put("fireresistancepotionlong", new Integer[]{373,8259});
    	aliasesMap.put("poisonpotion", new Integer[]{373,8196});
    	aliasesMap.put("poisonpotionlong", new Integer[]{373,8260});
    	aliasesMap.put("poisonpotion2", new Integer[]{373,8228});
    	aliasesMap.put("poisonpotion2long", new Integer[]{373,8292});
    	aliasesMap.put("healingpotion", new Integer[]{373,8197});
    	aliasesMap.put("healingpotion2", new Integer[]{373,8229});
    	aliasesMap.put("nightvisionpotion", new Integer[]{373,8198});
    	aliasesMap.put("nightvisionpotionlong", new Integer[]{373,8262});
    	aliasesMap.put("weaknesspotion", new Integer[]{373,8200});
    	aliasesMap.put("weaknesspotionlong", new Integer[]{373,8264});
    	aliasesMap.put("strengthpotion", new Integer[]{373,8201});
    	aliasesMap.put("strengthpotionlong", new Integer[]{373,8265});
    	aliasesMap.put("strengthpotion2", new Integer[]{373,8233});
    	aliasesMap.put("strengthpotion2long", new Integer[]{373,8297});
    	aliasesMap.put("slownesspotion", new Integer[]{373,8202});
    	aliasesMap.put("slownesspotionlong", new Integer[]{373,8266});
    	aliasesMap.put("harmingpotion", new Integer[]{373,8204});
    	aliasesMap.put("harmingpotion2", new Integer[]{373,8236});
    	aliasesMap.put("invisibilitypotion", new Integer[]{373,8206});
    	aliasesMap.put("invisibilitypotionlong", new Integer[]{373,8270});
    	aliasesMap.put("regenerationsplash", new Integer[]{373,16385});
    	aliasesMap.put("regenerationsplashlong", new Integer[]{373,16449});
    	aliasesMap.put("regenerationsplash2", new Integer[]{373,16417});
    	aliasesMap.put("regenerationsplash2long", new Integer[]{373,16481});
    	aliasesMap.put("swiftnesssplash", new Integer[]{373,16386});
    	aliasesMap.put("swiftnesssplashlong", new Integer[]{373,16450});
    	aliasesMap.put("swiftnesssplash2", new Integer[]{373,16418});
    	aliasesMap.put("swiftnesssplash2long", new Integer[]{373,16482});
    	aliasesMap.put("fireresistancesplash", new Integer[]{373,16387});
    	aliasesMap.put("fireresistancesplashlong", new Integer[]{373,16451});
    	aliasesMap.put("poisonsplash", new Integer[]{373,16388});
    	aliasesMap.put("poisonsplashlong", new Integer[]{373,16452});
    	aliasesMap.put("poisonsplash2", new Integer[]{373,16420});
    	aliasesMap.put("poisonsplash2long", new Integer[]{373,16484});
    	aliasesMap.put("healingsplash", new Integer[]{373,16389});
    	aliasesMap.put("healingsplash2", new Integer[]{373,16421});
    	aliasesMap.put("nightvisionsplash", new Integer[]{373,16390});
    	aliasesMap.put("nightvisionsplashlong", new Integer[]{373,16454});
    	aliasesMap.put("weaknesssplash", new Integer[]{373,16392});
    	aliasesMap.put("weaknesssplashlong", new Integer[]{373,16456});
    	aliasesMap.put("strengthsplash", new Integer[]{373,16393});
    	aliasesMap.put("strengthsplashlong", new Integer[]{373,16457});
    	aliasesMap.put("strengthsplash2", new Integer[]{373,16425});
    	aliasesMap.put("strengthsplash2long", new Integer[]{373,16489});
    	aliasesMap.put("slownesssplash", new Integer[]{373,16394});
    	aliasesMap.put("slownesssplashlong", new Integer[]{373,16458});
    	aliasesMap.put("harmingsplash", new Integer[]{373,16396});
    	aliasesMap.put("harmingsplash2", new Integer[]{373,16428});
    	aliasesMap.put("invisibilitysplash", new Integer[]{373,16398});
    	aliasesMap.put("invisibilitysplashlong", new Integer[]{373,16462});
    	aliasesMap.put("emptybottle", new Integer[]{374});
    	aliasesMap.put("glassbottle", new Integer[]{374});
    	aliasesMap.put("spidereye", new Integer[]{375});
    	aliasesMap.put("fermentedspidereye", new Integer[]{376});
    	aliasesMap.put("blazepowder", new Integer[]{377});
    	aliasesMap.put("magmacream", new Integer[]{378});
    	aliasesMap.put("brewingstand", new Integer[]{379});
    	aliasesMap.put("cauldron", new Integer[]{380});
    	aliasesMap.put("eyeofender", new Integer[]{381});
    	aliasesMap.put("glisteringmelon", new Integer[]{382});
    	aliasesMap.put("spawnegg", new Integer[]{383});
    	aliasesMap.put("spawnmob", new Integer[]{383});
    	aliasesMap.put("mobegg", new Integer[]{383});
    	aliasesMap.put("creeperegg", new Integer[]{383,50});
    	aliasesMap.put("skeletonegg", new Integer[]{383,51});
    	aliasesMap.put("spideregg", new Integer[]{383,52});
    	aliasesMap.put("zombieegg", new Integer[]{383,54});
    	aliasesMap.put("slimeegg", new Integer[]{383,55});
    	aliasesMap.put("ghastegg", new Integer[]{383,56});
    	aliasesMap.put("pigmanegg", new Integer[]{383,57});
    	aliasesMap.put("endermaneggegg", new Integer[]{383,58});
    	aliasesMap.put("cavespideregg", new Integer[]{383,59});
    	aliasesMap.put("silverfishegg", new Integer[]{383,60});
    	aliasesMap.put("blazeegg", new Integer[]{383,61});
    	aliasesMap.put("magmacubeegg", new Integer[]{383,62});
    	aliasesMap.put("bategg", new Integer[]{383,65});
    	aliasesMap.put("witchegg", new Integer[]{383,66});
    	aliasesMap.put("pigegg", new Integer[]{383,90});
    	aliasesMap.put("sheepegg", new Integer[]{383,91});
    	aliasesMap.put("cowegg", new Integer[]{383,92});
    	aliasesMap.put("chickenegg", new Integer[]{383,93});
    	aliasesMap.put("squidegg", new Integer[]{383,94});
    	aliasesMap.put("wolfegg", new Integer[]{383,95});
    	aliasesMap.put("mooshroomegg", new Integer[]{383,96});
    	aliasesMap.put("ocelotegg", new Integer[]{383,98});
    	aliasesMap.put("villageregg", new Integer[]{383,120});
    	aliasesMap.put("spawncreeper", new Integer[]{383,50});
    	aliasesMap.put("spawnskeleton", new Integer[]{383,51});
    	aliasesMap.put("spawnspider", new Integer[]{383,52});
    	aliasesMap.put("spawnzombie", new Integer[]{383,54});
    	aliasesMap.put("spawnslime", new Integer[]{383,55});
    	aliasesMap.put("spawnghast", new Integer[]{383,56});
    	aliasesMap.put("spawnpigman", new Integer[]{383,57});
    	aliasesMap.put("spawnenderman", new Integer[]{383,58});
    	aliasesMap.put("spawncavespider", new Integer[]{383,59});
    	aliasesMap.put("spawnsilverfish", new Integer[]{383,60});
    	aliasesMap.put("spawnblaze", new Integer[]{383,61});
    	aliasesMap.put("spawnmagmacube", new Integer[]{383,62});
    	aliasesMap.put("spawnbat", new Integer[]{383,65});
    	aliasesMap.put("spawnwitch", new Integer[]{383,66});
    	aliasesMap.put("spawnpig", new Integer[]{383,90});
    	aliasesMap.put("spawnsheep", new Integer[]{383,91});
    	aliasesMap.put("spawncow", new Integer[]{383,92});
    	aliasesMap.put("spawnchicken", new Integer[]{383,93});
    	aliasesMap.put("spawnsquid", new Integer[]{383,94});
    	aliasesMap.put("spawnwolf", new Integer[]{383,95});
    	aliasesMap.put("spawnmooshroom", new Integer[]{383,96});
    	aliasesMap.put("spawnocelot", new Integer[]{383,98});
    	aliasesMap.put("spawnvillager", new Integer[]{383,120});
    	aliasesMap.put("expflask", new Integer[]{384});
    	aliasesMap.put("experiencepotion", new Integer[]{384});
    	aliasesMap.put("enchantingbottle", new Integer[]{384});
    	aliasesMap.put("bottleoenchanting", new Integer[]{384});
    	aliasesMap.put("firecharge", new Integer[]{385});
    	aliasesMap.put("bookandquill", new Integer[]{386});
    	aliasesMap.put("writtenbook", new Integer[]{387});
    	aliasesMap.put("emerald", new Integer[]{388});
    	aliasesMap.put("itemframe", new Integer[]{389});
    	aliasesMap.put("flowerpot", new Integer[]{390});
    	aliasesMap.put("carrots", new Integer[]{391});
    	aliasesMap.put("carrot", new Integer[]{391});
    	aliasesMap.put("potato", new Integer[]{392});
    	aliasesMap.put("bakedpotato", new Integer[]{393});
    	aliasesMap.put("poisonouspotato", new Integer[]{394});
    	aliasesMap.put("map", new Integer[]{395});
    	aliasesMap.put("goldencarrot", new Integer[]{396});
    	aliasesMap.put("mobhead", new Integer[]{397});
    	aliasesMap.put("skeletonhead", new Integer[]{397,0});
    	aliasesMap.put("witherhead", new Integer[]{397,1});
    	aliasesMap.put("zombiehead", new Integer[]{397,2});
    	aliasesMap.put("stevehead", new Integer[]{397,3});
    	aliasesMap.put("humanhead", new Integer[]{397,3});
    	aliasesMap.put("creeperhead", new Integer[]{397,4});
    	aliasesMap.put("carrotonastick", new Integer[]{398});
    	aliasesMap.put("netherstar", new Integer[]{399});
    	aliasesMap.put("pumpkinpie", new Integer[]{400});
    	aliasesMap.put("fireworkrocket", new Integer[]{401});
    	aliasesMap.put("fireworkstar", new Integer[]{402});
    	aliasesMap.put("enchantedbook", new Integer[]{403});
    	aliasesMap.put("comparator", new Integer[]{404});
    	aliasesMap.put("redstonecomparator", new Integer[]{404});
    	aliasesMap.put("netherbrick", new Integer[]{405});
    	aliasesMap.put("netherquartz", new Integer[]{406});
    	aliasesMap.put("tntminecart", new Integer[]{407});
    	aliasesMap.put("hopperminecart", new Integer[]{408});
    	aliasesMap.put("minecartwithtnt", new Integer[]{407});
    	aliasesMap.put("minecartwithhopper", new Integer[]{408});
    	aliasesMap.put("13disc", new Integer[]{2256});
    	aliasesMap.put("catdisc", new Integer[]{2257});
    	aliasesMap.put("blocksdisc", new Integer[]{2258});
    	aliasesMap.put("chirpdisc", new Integer[]{2259});
    	aliasesMap.put("fardisc", new Integer[]{2260});
    	aliasesMap.put("malldisc", new Integer[]{2261});
    	aliasesMap.put("mellohidisc", new Integer[]{2262});
    	aliasesMap.put("staldisc", new Integer[]{2263});
    	aliasesMap.put("straddisc", new Integer[]{2264});
    	aliasesMap.put("warddisc", new Integer[]{2265});
    	aliasesMap.put("11disc", new Integer[]{2266});
    	aliasesMap.put("waitdisc", new Integer[]{2267});

    	
    	sortedAliases = new ArrayList<String>(aliasesMap.keySet());
		Collections.sort(sortedAliases, new StringLengthComparator());//Actually sort the keys
    }
    
    private void loadTemporaryFile(int what){
    	File f = null;
    	FileInputStream fstream = null;
    	BufferedReader br = null;
    	String header = "";
    	
		try {
			if(what == 0){
    			f = new File(MANDIR + "inventories.db");
    			header = "Inventories database for RealShopping v";
    		} else if(what == 1){
    			f = new File(MANDIR + "jailed.db");
    			header = "Jailed players database for RealShopping v";
    		} else if(what == 2){
    			f = new File(MANDIR + "allowedtplocs.db");
    			header = "Allowed teleport locations for RealShopping v";
    		} else if(what == 3){
    			f = new File(MANDIR + "protectedchests.db");
    			header = "Protected chests for RealShopping v";
    		} else if(what == 4){
    			f = new File(MANDIR + "shipped.db");
    			header = "Shipped Packages database for RealShopping v";
    		} else if(what == 5){
    			f = new File(MANDIR + "toclaim.db");
    			header = "Stolen items database for RealShopping v";
    		} else if(what == 6){
    			f = new File(MANDIR + "stats.db");
    			header = "Statistics database for RealShopping v";
    		} else if(what == 7){
    			f = new File(MANDIR + "notifications.db");
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
						PInvSet.add(new RSPlayerInventory(s));
		    		} else if(what == 1){
		    			jailedPlayers.put(s.split(";")[0], RSUtils.stringToLoc(s.split(";")[1], s.split(";")[2]));
		    		} else if(what == 2){
		    			forbiddenTpLocs.put(RSUtils.stringToLoc(s.split(";")[0], s.split(";")[1]), Integer.parseInt(s.split(";")[2]));
		    		} else if(what == 3){
						shopMap.get(s.split(";")[0]).addProtectedChest(new Location(getServer().getWorld(s.split(";")[1].split(",")[0])
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
	    						Integer cost = Integer.parseInt(parts[0].split(":")[0]);
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
	    								String s1[] = parts[1].split(",")[j].split(":");
	    								tempIS = new MaterialData(Integer.parseInt(s1[0]), Byte.parseByte(s1[3])).toItemStack(Integer.parseInt(s1[1]));
	    								tempIS.setDurability(Short.parseShort(s1[2]));
        	    						for(int k = 4;k < parts[1].split(",")[j].split(":").length;k++){
        	    							tempIS.addEnchantment(Enchantment.getById(Integer.parseInt(parts[1].split(",")[j].split(":")[k].split(";")[0])), Integer.parseInt(parts[1].split(",")[j].split(":")[k].split(";")[1]));
        	    						}
        	    						iS[j] = tempIS;
	    							}
	    						}
	    						shippedToCollect.get(s.split("\\[")[0]).add(new ShippedPackage(iS, cost, loc, timeStamp));
	    					}
						}
		    		} else if(what == 5){
						Shop tempShop;
						if(version >= 0.32){
							tempShop = shopMap.get(s.split(",")[0]);
	    					for(int i = 2;i < s.split(",").length;i++){
								String s1[] = s.split(",")[2].split(":");
								ItemStack tempIS = new MaterialData(Integer.parseInt(s1[0]), Byte.parseByte(s1[3])).toItemStack(Integer.parseInt(s1[1]));
								tempIS.setDurability(Short.parseShort(s1[2]));
	    						for(int j = 4;j < s.split(",")[2].split(":").length;j++){
	    							tempIS.addEnchantment(Enchantment.getById(Integer.parseInt(s.split(",")[2].split(":")[j].split(";")[0])), Integer.parseInt(s.split(",")[2].split(":")[j].split(";")[1]));
	    						}
	    						tempShop.addStolenToClaim(tempIS);
	    					}
						} else {
							String[] stores = RSUtils.getOwnedStores(s.split(";")[0]);
							if(!stores[0].equals("")){
								tempShop = shopMap.get(stores[0]);
		    					for(int i = 1;i < s.split(";").length;i++){
									String s1[] = s.split(";")[i].split(",");
									ItemStack tempIS = new MaterialData(Integer.parseInt(s1[0]), Byte.parseByte(s1[3])).toItemStack(Integer.parseInt(s1[1]));
									tempIS.setDurability(Short.parseShort(s1[2]));
		    						tempShop.addStolenToClaim(tempIS);
		    					}
							} else {
								log.info("Couldn't convert because player doesn't own any stores: " + s);
							}
						}
		    		} else if(what == 6){
		    			Shop tempShop = shopMap.get(s.split(";")[0]);
		    			for(int i = 1;i < s.split(";").length;i++){
		    				tempShop.addStat(new Statistic(s.split(";")[i]));
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
    			keys = PInvSet.toArray();
    			f = new File(MANDIR + "inventories.db");
    			header = "Inventories database for RealShopping " + VERSION;
    		} else if(what == 1){
    			keys = jailedPlayers.keySet().toArray();
    			f = new File(MANDIR + "jailed.db");
    			header = "Jailed players database for RealShopping " + VERSION;
    		} else if(what == 2){
    			keys = forbiddenTpLocs.keySet().toArray();
    			f = new File(MANDIR + "allowedtplocs.db");
    			header = "Allowed teleport locations for RealShopping " + VERSION + " " + (tpLocBlacklist?"Blacklist":"Whitelist");
    		} else if(what == 3){
    			keys = shopMap.keySet().toArray();
    			f = new File(MANDIR + "protectedchests.db");
    			header = "Protected chests for RealShopping " + VERSION;
    		} else if(what == 4){
    			keys = shippedToCollect.keySet().toArray();//Map of players having shipped items
    			f = new File(MANDIR + "shipped.db");
    			header = "Shipped Packages database for RealShopping " + VERSION;
    		} else if(what == 5){
    			keys = shopMap.keySet().toArray();
    			f = new File(MANDIR + "toclaim.db");
    			header = "Stolen items database for RealShopping " + VERSION;
    		} else if(what == 6){
    			keys = shopMap.keySet().toArray();
    			f = new File(MANDIR + "stats.db");
    			header = "Statistics database for RealShopping " + VERSION;
    		} else if(what == 7){
    			keys = notificator.keySet().toArray();
    			f = new File(MANDIR + "notifications.db");
    			header = "Notifications database for RealShopping " + VERSION;
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
						line = ((RSPlayerInventory)keys[i]).exportToString();
						break;
					case 1:
						line = ((String)keys[i]) + ";" + jailedPlayers.get(keys[i]).getWorld().getName() + ";" + RSUtils.locAsString(jailedPlayers.get(keys[i]));
						break;
					case 2:
						line = ((Location)keys[i]).getWorld().getName() + ";" + RSUtils.locAsString((Location)keys[i]) + ";" + forbiddenTpLocs.get(keys[i]);
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
								+ RSUtils.locAsString(tempSP.getLocationSent()) + ":" + tempSP.getDateSent();
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
    
    public static void log(String msg){ log.info(msg); }//TODO start using this?
    
	/*
	 * 
	 * Getters and Setters
	 * 
	 */

	public static String[] getPlayersInStore(String store){
		String pString = "";
		for(RSPlayerInventory pInv:PInvSet){
			if(pInv.getStore().equals(store)){
				if(!pString.equals("")) pString += ",";
				pString += pInv.getPlayer();
			}
		}
		return pString.split(",");
	}
    
	public static PSetting getPlayerSettings(Player p){
		if(!playerSettings.containsKey(p.getName())) playerSettings.put(p.getName(), new PSetting());
		return playerSettings.get(p.getName());
	}

	public static String getPSettingPlayer(PSetting ps){
		for(String p:playerSettings.keySet().toArray(new String[0])){
			if(playerSettings.get(p) == ps) return p;
		}
		return null;
	}
	
	public static RSPlayerInventory getPInv(Player player){
		for(RSPlayerInventory pInv:PInvSet)
			if(pInv.getPlayer().equals(player.getName())) return pInv;
		return null;
	}
	public static boolean addPInv(RSPlayerInventory pInv){ return PInvSet.add(pInv); }
	public static boolean hasPInv(Player player){
		for(RSPlayerInventory pInv:PInvSet)
			if(pInv.getPlayer().equals(player.getName())) return true;
		return false;
	}
	public static boolean removePInv(Player player){
		for(RSPlayerInventory pInv:PInvSet)
			if(pInv.getPlayer().equals(player.getName())){
				PInvSet.remove(pInv);
				return true;
			}
		return false;
	}
	public static boolean removePInv(String player){
		for(RSPlayerInventory pInv:PInvSet)
			if(pInv.getPlayer().equals(player)){
				PInvSet.remove(pInv);
				return true;
			}
		return false;
	}

	public Updater getUpdater() { return updater; }
	public void setUpdater(Updater updater) { this.updater = updater; }
    
    public static Set<String> getNotificatorKeys(){ return notificator.keySet(); }
    public static List<String> getNotifications(String player){ return notificator.get(player); }
    
    public static boolean isTool(int item){ return maxDurMap.containsKey(item); }
    public static int getMaxDur(int item){ return maxDurMap.get(item); }
    
    public static boolean isForbiddenInStore(int item){ return forbiddenInStore.contains(item); }
    
    public static List<String> getSortedAliases(){ return sortedAliases; }
    public static Map<String, Integer[]> getAliasesMap(){ return aliasesMap; }
    
    protected static boolean addEntranceExit(EEPair eePair, Shop shop){
    	if(eePairs.containsKey(eePair)) return false;
    	eePairs.put(eePair, shop);
    	return true;
    }
    protected static boolean removeEntranceExit(Shop shop, Location en, Location ex){
    	for(EEPair ee:eePairs.keySet()){
    		if(ee.hasEntrance(en) && ee.hasExit(ex) && eePairs.get(ee).equals(shop)){
    			eePairs.remove(ee);
    			return true; 
    		}
    	}
    	return false;
    }
    protected static int clearEntrancesExits(Shop shop){
    	Map<EEPair, Shop> tempPairs = new HashMap<EEPair, Shop>(eePairs);
    	int i = 0;
    	for(EEPair ee:tempPairs.keySet()){
    		if(tempPairs.get(ee).equals(shop)){
    			eePairs.remove(ee);
    			i++;
    		}
    	}
    	return i;
    }
    protected static boolean hasEntrance(Shop shop, Location en){
    	for(EEPair ee:eePairs.keySet()){
    		if(ee.hasEntrance(en) && eePairs.get(ee).equals(shop)) return true; 
    	}
    	return false;
    }
    protected static boolean hasExit(Shop shop, Location ex){
    	for(EEPair ee:eePairs.keySet()){
    		if(ee.hasExit(ex) && eePairs.get(ee).equals(shop)) return true; 
    	}
    	return false;
    }
    protected static Location getRandomEntrance(Shop shop){
    	for(EEPair ee:eePairs.keySet()){
    		if(eePairs.get(ee).equals(shop)) return ee.getEntrance();
    	}
    	return null;
    }
    protected static Location getEntrance(Shop shop, Location ex){
    	for(EEPair ee:eePairs.keySet()){
    		if(ee.hasExit(ex) && eePairs.get(ee).equals(shop)) return ee.getEntrance(); 
    	}
    	return null;
    }
    protected static Location getExit(Shop shop, Location en){
    	for(EEPair ee:eePairs.keySet()){
    		if(ee.hasEntrance(en) && eePairs.get(ee).equals(shop)) return ee.getExit(); 
    	}
    	return null;
    }
    protected static Shop isEntranceTo(Location loc){
    	for(EEPair ee:eePairs.keySet()){
    		if(ee.getEntrance().equals(loc)) return eePairs.get(ee);
    	}
    	return null;
    }
    
    public static boolean hasDefPrices(){ return !RealShopping.defPrices.isEmpty(); }
    public static void clearDefPrices(){ defPrices.clear(); }
	public static Integer getDefPricesSize() { return defPrices.size(); }
	public static Map<Price, Integer[]> getDefPrices() { return defPrices; }
	public static void addDefPrice(Price p, Integer[] i) { defPrices.put(p, i); }
    
    public static Location[] getTpLocsKeysArray(){ return forbiddenTpLocs.keySet().toArray(new Location[0]); }
    public static Integer getTpLoc(Location loc){ return forbiddenTpLocs.get(loc); }
    public static boolean hasTpLoc(Location loc){ return forbiddenTpLocs.containsKey(loc); }
    public static Integer addTpLoc(Location loc, Integer radius){ return forbiddenTpLocs.put(loc, radius); }
    public static Integer removeTpLoc(Location loc){ return forbiddenTpLocs.remove(loc); }
    public static boolean isTpLocBlacklist() { return tpLocBlacklist; }
	public static void setTpLocBlacklist() { tpLocBlacklist = true; }
	public static void setTpLocWhitelist() { tpLocBlacklist = false; }
    
    public static Location getJailed(String player){ return jailedPlayers.get(player); }
    public static boolean isJailed(String player){ return jailedPlayers.containsKey(player); }
    public static Location removeJailed(String player){ return jailedPlayers.remove(player); }
    
    public static boolean hasShippedToCollect(String player){ return shippedToCollect.containsKey(player); }
    public static int getShippedToCollectAmount(String player){ return shippedToCollect.get(player).size(); }
    public static ShippedPackage getShippedToCollect(String player, int ID){ return shippedToCollect.get(player).get(ID); }
    public static ShippedPackage removeShippedToCollect(String player, int ID){ return shippedToCollect.get(player).remove(ID); }
    public static void addShippedToCollect(String player, ShippedPackage pack){
		if(!hasShippedToCollect(player))
			shippedToCollect.put(player, new ArrayList<ShippedPackage>());
		shippedToCollect.get(player).add(pack);
    }
    
	public static void setEntrance(Player player) { entrance = player.getLocation().getBlock().getLocation(); }//TODO Rename?
	public static void setExit(Player player) { exit = player.getLocation().getBlock().getLocation(); }
    public static boolean hasEntrance(){ return !entrance.equals(""); }
    public static boolean hasExit(){ return !exit.equals(""); }
    public static Location getEntrance() { return entrance; }
	public static Location getExit() { return exit; }
    
    public static Location addPlayerEntrance(Player player){ return playerEntrances.put(player.getName(), player.getLocation().getBlock().getLocation()); }
    public static Location addPlayerExit(Player player){ return playerExits.put(player.getName(), player.getLocation().getBlock().getLocation()); }
    public static boolean hasPlayerEntrance(String player){ return playerEntrances.containsKey(player); }
    public static boolean hasPlayerExit(String player){ return playerExits.containsKey(player); }
    public static Location getPlayerEntrance(String player){ return playerEntrances.get(player); }
    public static Location getPlayerExit(String player){ return playerExits.get(player); }
  
    public static void jailPlayer(Player player){ jailedPlayers.put(player.getName(), shopMap.get(getPInv(player).getStore()).getFirstE()); }
    
	/*
	 * 
	 * Misc
	 * 
	 */
    
    public static void sendNotification(String who, String what){
		if(Config.getNotTimespan() >= 500){
			if(!notificator.containsKey(who)) notificator.put(who, new ArrayList<String>());
			notificator.get(who).add(what);
		}
	}
	
    public File getPFile(){
    	return this.getFile();
    }
    
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
				for(String s:RealShopping.getNotificatorKeys()){
					if(Bukkit.getPlayerExact(s) != null){
						List<String> nots = RealShopping.getNotifications(s);
						for(int i = 0;i < 10 && 0 < nots.size();i ++){
							Bukkit.getPlayerExact(s).sendMessage(ChatColor.LIGHT_PURPLE + "[RealShopping] " + ChatColor.RESET + nots.get(0));
							nots.remove(0);
						}
					}
				}
				Thread.sleep(Math.max(Config.getNotTimespan(), 500));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}

class PricesParser extends DefaultHandler {
	int index = -1;
	List<Map<Price, Integer[]>> mapList = new ArrayList<Map<Price, Integer[]>>();
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
			mapList.add(new HashMap<Price, Integer[]>());
			index ++;
		} else if(name.equalsIgnoreCase("item")){
			if(attr.getValue("cost") != null){
				Integer f[];
				if(attr.getValue("min") != null && attr.getValue("max") != null){
					f = new Integer[]{(int) (Float.parseFloat(attr.getValue("cost"))*100), 
									(int) (Float.parseFloat(attr.getValue("min"))*100), 
									(int) (Float.parseFloat(attr.getValue("max"))*100)};
				} else {
					f = new Integer[]{(int) (Float.parseFloat(attr.getValue("cost"))*100)};
				}
				mapList.get(index).put(new Price(attr.getValue("id")), f);
			}
		}
	}

	public void endElement(String a, String b, String name) throws SAXException {
		if (name.equalsIgnoreCase("prices")){
			for(int i = 0;i<shopList.size();i++){
				if(RealShopping.shopMap.containsKey(shopList.get(i))){
					RealShopping.shopMap.get(shopList.get(i)).setPrices(mapList.get(i));
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