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
import java.util.List;
import java.util.Map;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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

public class RealShopping extends JavaPlugin {
	
	static String mandir;
	public static Map<String, String> playerMap;
	public static Map<String, Map<Integer, Float>> prices;//Shop name - (id - price, id - price...)
	public static Map<Integer, Integer> maxDurMap;
	public static Map<String, Shop> shopMap;
	public static Map<String, String> playerEntrances;
	public static Map<String, String> playerExits;
	public static Map<String, Location> jailedPlayers;
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
    	
    	unit = "$";
    	
    	try {
    		double newest = updateCheck(0.20);
    		if(newest > 0.20) log.info("v" + newest + " of RealShopping is available for download. Update for new features and/or bugfixes.");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	if(setupEconomy()){
    		if(!smallReload) getServer().getPluginManager().registerEvents(new RSPlayerListener(), this);
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
    				pW.println("punishment:hell");
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
    				boolean v20 = false;
    				while ((s = br.readLine()) != null){// Read shops.db
    					if(s.equals("Shops database for RealShopping v0.20")){
    						v20 = true;
    					} else {
        					String[] tS = s.split(";")[0].split(":");
        		    		shopMap.put(tS[0], new Shop(tS[0], tS[1], v20?tS[2]:"@admin"));
        					for(int i = v20?3:2;i < tS.length;i++){//The entrances + exits
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
        						String idS = s.split(";")[i].split("\\[")[1];
        						idS = idS.substring(0, idS.length() - 1);
        						if(!idS.split(",")[0].equals("")){
        							int[] ids = new int[idS.split(",").length];
        							for(int j = 0;j < ids.length;j++){//The chests
        								ids[j] = Integer.parseInt(idS.split(",")[j].trim());
        							}
        							shopMap.get(tS[0]).addChestItem(l, ids);
        						}
        					}
    					}
    				}
    				fstream.close();
    				br.close();
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
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
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
     
    public void onDisable(){//Write 'inventories' to text file
		try {
			Object[] keys = shopMap.keySet().toArray();
			for(int i = 0;i < keys.length;i++){
				Map inv = shopMap.get(keys[i]).players;
				if(!inv.isEmpty()){
					//Write inventories to file
					Object[] players = inv.keySet().toArray();
					File f = new File(mandir+"inventories.db");
					if(!f.exists()) f.createNewFile();
					PrintWriter pW;
					pW = new PrintWriter(f);
					for(int j = 0;j < players.length;j++){
						String s = players[j].toString() + "-" + shopMap.get(keys[i]).name + ";";
						s += inv.get(players[j]);
						pW.println(s);
					}
					pW.close();
				}
			}

				//Write prices to xml

				keys = prices.keySet().toArray();
				File f = new File(mandir+"prices.xml");//Reset file
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	log.info("RealShopping disabled");
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	
    	Player player = null;
    	if (sender instanceof Player) {
    		player = (Player) sender;
    	}
    	if(cmd.getName().equalsIgnoreCase("rsreload")){
    		smallReload = true;
    		onDisable();
    		onEnable();
    		sender.sendMessage(ChatColor.GREEN + LangPack.REALSHOPPINGRELOADED);
    		return true;
    	} else if(cmd.getName().equalsIgnoreCase("rsenter")){
    		if(player != null){
    			return enter(player, true);
    		}
    		else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rspay")){
    		if(player != null){
    			return pay(player);
    		}
    		else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rscost")){
    		if(player != null){
    			player.sendMessage(ChatColor.RED + LangPack.YOURARTICLESCOST + cost(player));
    			return true;
    		}
    		else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rsexit")){
    		if(player != null){
    			return exit(player, true);
    		}
    		else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if (cmd.getName().equalsIgnoreCase("rsprices")){
			if(args.length == 0){
				if(player != null){
					if(playerMap.get(player.getName()) != null) {
						return prices(sender, 0, playerMap.get(player.getName()), true);
					} else {
						sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
					}
				} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
			} else if(args.length == 1){
				if(args[0].matches("[0-9]+")){
					if(player != null){
						if(playerMap.containsKey(player.getName())){
							int i = Integer.parseInt(args[0]);
							if(i > 0) return prices(sender, i - 1, playerMap.get(player.getName()), true);
							else sender.sendMessage(ChatColor.RED + LangPack.THEPAGENUMBERMUSTBE1ORHIGHER);
						} else {
							sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT);
							return true;
						}
					} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
				} else {
					return prices(sender, 0, args[0], true);
				}
			} else if(args.length == 2){
				if(args[1].matches("[0-9]+")){
					return prices(sender, Integer.parseInt(args[1]), args[0], true);
				} else {
					sender.sendMessage(ChatColor.RED + "" + args[1] + LangPack.ISNOTAVALIDPAGENUMBER);
				}
			}
    	} else if(cmd.getName().equalsIgnoreCase("rssetprices")){
    		String shop = "";
    		int ii = 1;
    		if(args.length == 2){
    			if(player != null){
    				if(playerMap.containsKey(player.getName())){
    					shop = playerMap.get(player.getName());
    				} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS);
    			} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
    		} else if(args.length == 3){
    			shop = args[1];
    			ii = 2;
    		}
    		if(!shop.equals("")){
    			if(shopMap.get(shop).owner.equals(player.getName()) || player.hasPermission("realshopping.rsset")){//If player is owner OR has admin perms
    				if(args[0].equalsIgnoreCase("add")){
    					try {
    						int i = Integer.parseInt(args[ii].split(":")[0]);
    						float k = Float.parseFloat(args[ii].split(":")[1]);
    						DecimalFormat twoDForm = new DecimalFormat("#.##");
    						float j = Float.parseFloat(twoDForm.format(k));
    						if(!prices.containsKey(shop)) prices.put(shop, new HashMap());
    						prices.get(shop).put(i, j);
    						sender.sendMessage(ChatColor.RED + LangPack.PRICEFOR + Material.getMaterial(i) + LangPack.SETTO + j + unit);
    						return true;
    					} catch (NumberFormatException e) {
    						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + unit);
    					} catch (ArrayIndexOutOfBoundsException e){
    						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + unit);
    					}
    				} else if(args[0].equalsIgnoreCase("del")){
    					try {
    						int i = Integer.parseInt(args[ii]);
    						if(prices.containsKey(shop)){
    							if(prices.get(shop).containsKey(i)){
    								prices.get(shop).remove(i);
    								sender.sendMessage(ChatColor.RED + LangPack.REMOVEDPRICEFOR + Material.getMaterial(i));
    								return true;
    							} else {
    								sender.sendMessage(ChatColor.RED + LangPack.COULDNTFINDPRICEFOR + i + " - " + Material.getMaterial(i));
    							}
    						} else {
    							sender.sendMessage(shop + LangPack.DOESNTEXIST);
    						}
    					} catch (NumberFormatException e) {
    						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_);
    					}
    				}
    			} else {
    				sender.sendMessage(ChatColor.RED + LangPack.YOUARENTPERMITTEDTOEMANAGETHISSTORE);
    			}
    		}
    	} else if(cmd.getName().equalsIgnoreCase("rssetchests")){
    		if(player != null){
    			if(playerMap.containsKey(player.getName())){
    				Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
    				Shop tempShop = shopMap.get(playerMap.get(player.getName()));
    				if(tempShop.owner.equals("@admin")){
            			if (args.length == 1 && args[0].equalsIgnoreCase("create")){
            				if(tempShop.addChest(l)){
            					player.sendMessage(ChatColor.RED + LangPack.CHESTCREATED);
            					updateEntrancesDb();
            				}
            				else player.sendMessage(ChatColor.RED + LangPack.ACHESTALREADYEXISTSONTHISLOCATION);
            				return true;
            			} else if (args.length == 1 && args[0].equalsIgnoreCase("del")){
            				if(tempShop.delChest(l)){
            					player.sendMessage(ChatColor.RED + LangPack.CHESTREMOVED);
            					updateEntrancesDb();
            				}
            				else player.sendMessage(ChatColor.RED + LangPack.COULDNTFINDCHESTONTHISLOCATION);
            				return true;
            			} else if (args.length == 2 && args[0].equalsIgnoreCase("additems")){
            				try {
            					int[] ids = new int[args[1].split(",").length];
            					for(int i = 0;i < args[1].split(",").length && i < 27;i++){
            						ids[i] = Integer.parseInt(args[1].split(",")[i]);
            					}
            					int j = tempShop.addChestItem(l, ids);
            					if(j > -1){
            						sender.sendMessage(ChatColor.RED + LangPack.ADDED + j + LangPack.ITEMS);
            						updateEntrancesDb();
            						return true;
            					}
            					else {
            						sender.sendMessage(ChatColor.RED + LangPack.THISCHESTDOESNTEXIST);
            						return false;
            					}
            				} catch (NumberFormatException e){
            					sender.sendMessage(ChatColor.RED + LangPack.ONEORMOREOFTHEITEMIDSWERENOTINTEGERS + args[1]);
            					return false;
            				}
            			} else if (args.length == 2 && args[0].equalsIgnoreCase("delitems")){
            				try {
            					int[] ids = new int[args[1].split(",").length];
            					for(int i = 0;i < args[1].split(",").length;i++){
            						ids[i] = Integer.parseInt(args[1].split(",")[i]);
            					}
            					int j = tempShop.delChestItem(l, ids);
            					if(j > -1){
            						sender.sendMessage(ChatColor.RED + LangPack.REMOVED + j + LangPack.ITEMS);
            						updateEntrancesDb();
            						return true;
            					} else {
            						sender.sendMessage(ChatColor.RED + LangPack.THISCHESTDOESNTEXIST);
            						return false;
            					}
            				} catch (NumberFormatException e){
            					sender.sendMessage(ChatColor.RED + LangPack.ONEORMOREOFTHEITEMIDSWERENOTINTEGERS + args[1]);
            					return false;
            				}
            			}
    				} else {
    					sender.sendMessage(ChatColor.RED + LangPack.ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS);
    				}
    			} else  sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTORETOUSETHISCOMMAND);
    		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rssetstores")){//Set player owned stores
    		if(player != null){
    			if (args.length == 1 && args[0].equalsIgnoreCase("entrance")){
    				String s = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
    				playerEntrances.put(player.getName(),s);
    				player.sendMessage(ChatColor.RED + LangPack.ENTRANCEVARIABLESETTO + s);
    				return true;
    			} else if (args.length == 1 && args[0].equalsIgnoreCase("exit")){
    				String s = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
    				playerExits.put(player.getName(), s);
    				player.sendMessage(ChatColor.RED + LangPack.EXITVARIABLESETTO + s);
    				return true;
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("createstore")){
    				if(playerEntrances.containsKey(player.getName())){
        				if(playerExits.containsKey(player.getName())){
        			    	if(!shopMap.containsKey(args[1])){//Create
            					if(econ.getBalance(player.getName()) < pstorecreate) {
            						player.sendMessage(ChatColor.RED + LangPack.CREATINGASTORECOSTS + pstorecreate + unit);
            						return true;
            					} else {
            						econ.withdrawPlayer(player.getName(), pstorecreate);
            						shopMap.put(args[1], new Shop(args[1], player.getWorld().getName(), player.getName()));
            					}
        			    	}
        			    	if(shopMap.get(args[1]).owner.equals(player.getName())){
        			    		//Add entrance
        			    		String[] entr = playerEntrances.get(player.getName()).split(",");
        			    		String[] ext = playerExits.get(player.getName()).split(",");
        			    		Location en = new Location(getServer().getWorld(shopMap.get(args[1]).world), Integer.parseInt(entr[0]),Integer.parseInt(entr[1]), Integer.parseInt(entr[2]));
        			    		Location ex = new Location(getServer().getWorld(shopMap.get(args[1]).world), Integer.parseInt(ext[0]),Integer.parseInt(ext[1]), Integer.parseInt(ext[2]));
        			    		shopMap.get(args[1]).addE(en, ex);
        			    		updateEntrancesDb();
        			    		player.sendMessage(ChatColor.RED + args[1] + LangPack.WASCREATED);
        			    	} else {
        			    		player.sendMessage(ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE);
        			    	}
        					return true;
        				} else {
        					player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
        					return false;
        				}
    				} else {
    					player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
    					return false;
    				}
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("delstore")){
    				if(shopMap.containsKey(args[1])){
    					if(shopMap.get(args[1]).owner.equals(player.getName())){
    						shopMap.remove(args[1]);
    						player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
    						updateEntrancesDb();
    					} else {
    						player.sendMessage(ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE);
    					}
    					return true;
    				} else {
    					player.sendMessage(ChatColor.RED + args[1] + LangPack.WASNTFOUND);
    					return false;
    				}
    			}
    		} else {
    			 sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    		}
    	} else if(cmd.getName().equalsIgnoreCase("rsset")){
    		if(player != null){
    			if (args.length == 1 && args[0].equalsIgnoreCase("entrance")){
    				entrance = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
    				player.sendMessage(ChatColor.RED + LangPack.ENTRANCEVARIABLESETTO + entrance);
    				return true;
    			} else if (args.length == 1 && args[0].equalsIgnoreCase("exit")){
    				exit = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
    				player.sendMessage(ChatColor.RED + LangPack.EXITVARIABLESETTO + exit);
    				return true;
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("createstore")){
    				if(!entrance.equals("")){
        				if(!exit.equals("")){
        			    	if(!shopMap.containsKey(args[1])){//Create
        			    		shopMap.put(args[1], new Shop(args[1], player.getWorld().getName(), "@admin"));
        			    	}
        			    	//Add entrance
        			    	Location en = new Location(getServer().getWorld(shopMap.get(args[1]).world), Integer.parseInt(entrance.split(",")[0]),Integer.parseInt(entrance.split(",")[1]), Integer.parseInt(entrance.split(",")[2]));
        			    	Location ex = new Location(getServer().getWorld(shopMap.get(args[1]).world), Integer.parseInt(exit.split(",")[0]),Integer.parseInt(exit.split(",")[1]), Integer.parseInt(exit.split(",")[2]));
        			    	shopMap.get(args[1]).addE(en, ex);
        					updateEntrancesDb();
        					player.sendMessage(ChatColor.RED + args[1] + LangPack.WASCREATED);
        					return true;
        				} else {
        					player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
        					return false;
        				}
    				} else {
    					player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
    					return false;
    				}
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("delstore")){
    				if(shopMap.containsKey(args[1])){
    					shopMap.remove(args[1]);
    					player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
    					updateEntrancesDb();
    					return true;
    				} else {
    					player.sendMessage(ChatColor.RED + args[1] + LangPack.WASNTFOUND);
    					return false;
    				}
    			}
    		} else {
    			 sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    		}
    	} else if(cmd.getName().equalsIgnoreCase("rsunjail")){
    		if (args.length == 1){
    			if(jailedPlayers.containsKey(args[0])){
    				Player[] pla = getServer().getOnlinePlayers();
    				Player jailee = null;
    				for(Player p:pla){
    					if(p.getName().equals(args[0])){
    						jailee = p;
    						break;
    					}
    				}
    				if(jailee != null){
        				jailee.teleport(jailedPlayers.get(args[0])); 
        				jailedPlayers.remove(args[0]);
        				jailee.sendMessage(LangPack.YOUARENOLONGERINJAIL);
        				sender.sendMessage(LangPack.UNJAILED + args[0]);
        				return true;
    				} else {
    					sender.sendMessage(args[0] + LangPack.ISNOTONLINE);
    				}
    			} else {
    				sender.sendMessage(args[0] + LangPack.ISNOTJAILED);
    				return false;
    			}
    		} else {
    			return false;
    		}
    	}
    	return false;
    }

    void updateEntrancesDb(){
    	//Update file
		try {
			File f = new File(mandir + "shops.db");
			if(!f.exists()) f.createNewFile();
			PrintWriter pW = new PrintWriter(f);
			Object[] keys = shopMap.keySet().toArray();
			pW.println("Shops database for RealShopping v0.20");
			for(int i = 0;i<keys.length;i++){
				Shop tempShop = shopMap.get(keys[i]);
				pW.print(keys[i] + ":" + tempShop.world + ":" + tempShop.owner);
				for(int j = 0;j < tempShop.entrance.size();j++){
					pW.print(":" + locAsString(tempShop.entrance.get(j)) + "," + locAsString(tempShop.exit.get(j)));
				}
				Object[] chestLocs = tempShop.chests.keySet().toArray();
				for(int j = 0;j < chestLocs.length;j++){
					pW.print(";" + locAsString((Location) chestLocs[j]) + Arrays.toString(tempShop.chests.get(chestLocs[j]).toArray()));
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
				l = shopMap.get(keys[i]).exit.get(j).clone();
				player.teleport(l.add(0.5, 0, 0.5));
				shopMap.get(keys[i]).players.put(player.getName(), createInv(player));
				shopMap.get(keys[i]).origInvs.put(player.getName(), new ItemStack[][]{player.getInventory().getContents(),player.getInventory().getArmorContents()});
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
	             	    for(int jj:shopMap.get(keys[i]).chests.get((Location) chestArr[ii])){
	             	    	itemStack[k] = new ItemStack(jj, Material.getMaterial(jj).getMaxStackSize());
	             	    	k++;
	             	    }
	             	    chest.getBlockInventory().setContents(itemStack);
	             	}
				}
				return true;
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
				if(hasPaid(player)){
					String shopName = playerMap.get(player.getName());
					Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
					if(shopMap.get(shopName).exit.contains(l)){
						l = shopMap.get(shopName).entrance.get(shopMap.get(shopName).exit.indexOf(l)).clone();
						player.teleport(l.add(0.5, 0, 0.5));
						shopMap.get(shopName).players.remove(player.getName());
						shopMap.get(shopName).origInvs.remove(player.getName());
						playerMap.remove(player.getName());
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
    
    public static boolean pay(Player player){
		if(playerMap.containsKey(player.getName())){
			if(prices.containsKey(playerMap.get(player.getName())))
			if(!prices.get(playerMap.get(player.getName())).isEmpty()){
				float toPay = cost(player);
				if(econ.getBalance(player.getName()) < toPay) {
					player.sendMessage(ChatColor.RED + LangPack.YOUCANTAFFORDTOBUYTHINGSFOR + toPay + unit);
					return true;
				} else {
					econ.withdrawPlayer(player.getName(), toPay);
					if(!shopMap.get(playerMap.get(player.getName())).owner.equals("@admin")) econ.depositPlayer(shopMap.get(playerMap.get(player.getName())).owner, toPay);//If player owned store, pay player
					shopMap.get(playerMap.get(player.getName())).players.put(player.getName(),createInv(player));
					shopMap.get(playerMap.get(player.getName())).origInvs.put(player.getName(),new ItemStack[][]{player.getInventory().getContents(),player.getInventory().getArmorContents()});
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
    
    public static float cost(Player p){
		String[] inv = shopMap.get(playerMap.get(p.getName())).players.get(p.getName()).split(",");
		Map<Integer, Integer> oldInv = createInvOneOccur(inv);
		
		inv = createInv(p).split(",");
		Map<Integer, Integer> newInv = createInvOneOccur(inv);
		
		float toPay = 0;
		Object[] keys = newInv.keySet().toArray();
		for(int i = 0;i < keys.length;i++){
			int type = (Integer) keys[i];
			if(prices.containsKey(playerMap.get(p.getName())))
			if(prices.get(playerMap.get(p.getName())).containsKey(type)){//Something in inventory has a price
				int amount = newInv.get(type);
				float cost = prices.get(playerMap.get(p.getName())).get(type);
				if(oldInv.containsKey(type)) {
					int oldAm = oldInv.get(type);
					if(oldAm > amount){//More items before than now
						amount = 0;
					} else {//More items now
						amount -= oldAm;
					}
				}
				if(maxDurMap.containsKey(keys[i])) toPay += cost * Math.ceil(amount / (double)maxDurMap.get(type));//If tool
				else toPay += cost * amount;
			}
		}

		return toPay;
    }
    
    public static boolean prices(CommandSender sender, int page, String store, boolean cmd){
    	if(prices.containsKey(store)){
    		Map tempMap = prices.get(store);
 			if(!tempMap.isEmpty()){
 				Object[] keys = tempMap.keySet().toArray();
 				if(page*9 < keys.length){//If page exists
 					if((page+1)*9 < keys.length){//Not last
 		 				for(int i = 9*page;i < 9*(page+1);i++){
 		 					sender.sendMessage(ChatColor.BLUE + "" + keys[i] + " " + Material.getMaterial(Integer.parseInt(keys[i] + "")) + ChatColor.BLACK + " - " + ChatColor.RED + tempMap.get(keys[i]) + unit);
 		 				}
 		 				sender.sendMessage(ChatColor.RED + LangPack.MOREITEMSONPAGE + (page + 2));
 					} else {//Last page
 		 				for(int i = 9*page;i < keys.length;i++){
 		 					sender.sendMessage(ChatColor.BLUE + "" + keys[i] + " " + Material.getMaterial(Integer.parseInt(keys[i] + "")) + ChatColor.BLACK + " - " + ChatColor.RED + tempMap.get(keys[i]) + unit);
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
    			p.getInventory().setContents(shopMap.get(playerMap.get(p.getName())).origInvs.get(p.getName())[0]);
    			p.getInventory().setArmorContents(shopMap.get(playerMap.get(p.getName())).origInvs.get(p.getName())[1]);
    		}
    		shopMap.get(playerMap.get(p.getName())).players.remove(p.getName());
    		shopMap.get(playerMap.get(p.getName())).origInvs.remove(p.getName());
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
    			p.getInventory().setContents(shopMap.get(playerMap.get(p.getName())).origInvs.get(p.getName())[0]);
    			p.getInventory().setArmorContents(shopMap.get(playerMap.get(p.getName())).origInvs.get(p.getName())[1]);
    		}
			jailedPlayers.put(p.getName(), shopMap.get(playerMap.get(p.getName())).entrance.get(0));
    		shopMap.get(playerMap.get(p.getName())).players.remove(p.getName());
    		shopMap.get(playerMap.get(p.getName())).origInvs.remove(p.getName());
    		playerMap.remove(p.getName());
        	p.sendMessage(ChatColor.RED + LangPack.TRYINGTOCHEATYOURWAYOUT);
        	log.info(p.getName() + LangPack.TRIEDTOSTEALFROMTHESTORE);

         	Location loc = new Location(p.getWorld(),jailLoc[0] + 0.5, jailLoc[1], jailLoc[2] + 0.5);
         	if(p.teleport(loc)) {
         		p.sendMessage(LangPack.YOUWEREJAILED);
         		log.info(p.getName() + LangPack.WASJAILED);
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
    
    static Map<Integer, Integer> createInvOneOccur(String[] inv){
    	//Creates a HashMap with each item in the players inventory occurring only once. The value is the amount of all items together.
		Map<Integer, Integer> invOO = new HashMap<Integer, Integer>();
		if(!inv[0].equals(""))//If inv not empty
			for(int j = 0;j < inv.length;j++){
				int type = Integer.parseInt(inv[j].split(":")[0]);
				int amount = Integer.parseInt(inv[j].split(":")[1]);
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