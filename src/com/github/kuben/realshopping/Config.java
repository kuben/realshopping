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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;

public class Config {

	public static boolean debug;
	
	private static boolean keepstolen;
	private static boolean enableSelling;
	private static boolean autoprotect;
	private static boolean allowFillChests;
	private static boolean enableAI;
	private static boolean enableDoors;
	private static boolean disableDrop;
	private static boolean disableCrafting;
	private static boolean disableBuckets;
	private static boolean disableEnderchests;
	private static String punishment;
	private static String langpack;
	private static double pstorecreate;
	private static Location hellLoc;
	private static Location jailLoc;
	private static Location dropLoc;
	
	private static int deliveryZones;
	private static Zone[] zoneArray;
	private static byte autoUpdate;
	
	private static int notTimespan;//[millis] anything bellow 500 means disabled
	private static int statTimespan;//[secs]
	private static int cleanStatsOld;//Clean stats older than [secs]
	private static int updateFreq;//How often the statupdater should be run [secs]
	
	private static Set<String> cartEnabledW;
	
	private static int KEEPSTOLEN;
	private static int ENABLESELLING;
	private static int AUTOPROTECT;
	private static int ALLOWFILLCHESTS;
	private static int ENABLEAI;
	private static int PUNISHMENT;
	private static int LANGPACK;
	private static int PSTORECREATE;
	private static int HELLLOC;
	private static int JAILLOC;
	private static int DROPLOC;
	private static int DELIVERYZONES;
	private static int AUTOUPDATE;
	private static int NOTTIMESPAN;
	private static int STATTIMESPAN;
	private static int CLEANSTATSOLD;
	private static int UPDATEFREQ;
	private static int CARTENABLEDW;
	private static int DISABLEDROP;
	private static int DISABLECRAFTING;
	private static int DISABLEBUCKETS;
	private static int DISABLEENDERCHESTS;
	private static int ENABLEDOORS;
	
	private static int MAX;
	private static final String HEADER = "Properties file for RealShopping v";
	
	static Server server;
	private static float version = 0f;
	
	/*
	 * Hour - 3600 seconds
	 * Day - 86,400 seconds
	 * Week - 604,800 seconds
	 * Month (30 days) - 2,592,000 seconds
	 */
	
	public static void initialize(){
		initVals();
		server = Bukkit.getServer();
		if(server.getWorld("world") != null){
			hellLoc = new Location(server.getWorld("world"),0,0,0);
			jailLoc = new Location(server.getWorld("world"),0,0,0);
			dropLoc = new Location(server.getWorld("world"),0,0,0);
		} else {
			hellLoc = new Location(server.getWorlds().get(0),0,0,0);
			jailLoc = new Location(server.getWorlds().get(0),0,0,0);
			dropLoc = new Location(server.getWorlds().get(0),0,0,0);
		}

		debug = false;
        keepstolen = false;
        langpack = "default";
        punishment = "none";
        pstorecreate = 0.0;
        enableSelling = true;
        enableDoors = false;
        disableDrop = true;
        disableCrafting = true;
        disableBuckets = true;
        disableEnderchests = true;
        autoprotect = true;
    	allowFillChests = true;
    	enableAI = false;
        deliveryZones = 0;
        autoUpdate = 0;
        zoneArray = new Zone[0];
        notTimespan = 10000;
        statTimespan = 604800;
        cleanStatsOld = 2592000;
        updateFreq = 3600;
    	File f = new File(RealShopping.MANDIR);
    	if(!f.exists()) f.mkdir();
		FileInputStream fstream;
		BufferedReader br;
		try {
			f = new File(RealShopping.MANDIR + "realshopping.properties");
			int notInConfig = MAX;
			if(f.exists()) {
				fstream = new FileInputStream(f);
				br = new BufferedReader(new InputStreamReader(fstream));
				String s;
				while ((s = br.readLine()) != null){// Read realshopping.properties
					notInConfig -= readConfigLine(s);
				}
				fstream.close();
				br.close();
			}
			
			if(!f.exists() || notInConfig > 0){
				if(!f.exists()) f.createNewFile();
				PrintWriter pW = new PrintWriter(new BufferedWriter(new FileWriter(RealShopping.MANDIR + "realshopping.properties", true)));
				
				if(notInConfig >= AUTOUPDATE){
						pW.println("enable-automatic-updates:"+getAutoUpdateStr(autoUpdate)+"  #Can be false, check-console, check, ask-console, ask, or true");
						notInConfig -= AUTOUPDATE;
				} if(notInConfig >= LANGPACK){
					pW.println("language-pack:"+langpack+"  #located in langpacks/ , without the .xml ending");
					notInConfig -= LANGPACK;
				} if(notInConfig >= PUNISHMENT){
					pW.println("punishment:"+punishment);
					notInConfig -= PUNISHMENT;
				} if(notInConfig >= KEEPSTOLEN){
					pW.println("keep-stolen-items-after-punish:"+keepstolen);
					notInConfig -= KEEPSTOLEN;
				} if(notInConfig >= JAILLOC){
					pW.println("jail-location:"+jailLoc.getWorld().getName()+";"+jailLoc.getBlockX()+","+jailLoc.getBlockY()+","+jailLoc.getBlockZ());
					notInConfig -= JAILLOC;
				} if(notInConfig >= HELLLOC){
					pW.println("hell-location:"+hellLoc.getWorld().getName()+";"+hellLoc.getBlockX()+","+hellLoc.getBlockY()+","+hellLoc.getBlockZ());
					notInConfig -= HELLLOC;
				} if(notInConfig >= DROPLOC){
					pW.println("drop-items-at:"+dropLoc.getWorld().getName()+";"+dropLoc.getBlockX()+","+dropLoc.getBlockY()+","+dropLoc.getBlockZ());
					notInConfig -= DROPLOC;
				} if(notInConfig >= PSTORECREATE){
					pW.println("player-stores-create-cost:"+pstorecreate);
					notInConfig -= PSTORECREATE;
				} if(notInConfig >= ENABLESELLING){
					pW.println("enable-selling-to-stores:"+enableSelling);
					notInConfig -= ENABLESELLING;
				} if(notInConfig >= ENABLEDOORS){
					pW.println("enable-doors-as-entrances:"+enableDoors);
					notInConfig -= ENABLEDOORS;
				} if(notInConfig >= DISABLEDROP){
					pW.println("disable-item-drop:"+disableDrop+"  #Disable dropping items in stores.");
					notInConfig -= DISABLEDROP;
				} if(notInConfig >= DISABLECRAFTING){
					pW.println("disable-crafting:"+disableCrafting+"  #Disable crafting in stores.");
					notInConfig -= DISABLECRAFTING;
				} if(notInConfig >= DISABLEBUCKETS){
					pW.println("disable-buckets:"+disableBuckets+"  #Disable using buckets in stores.");
					notInConfig -= DISABLEBUCKETS;
				} if(notInConfig >= DISABLEENDERCHESTS){
					pW.println("disable-ender-chests:"+disableEnderchests+"  #Disable using Ender Chests in stores.");
					notInConfig -= DISABLEENDERCHESTS;
				} if(notInConfig >= CARTENABLEDW){
	    			pW.print("enable-shopping-carts-in-worlds:");
	    			boolean i = true;
	    			for(String str:cartEnabledW){
	    				if(i){
	   						pW.print(str);
	   						i = false;
	   					}
	    				else pW.print("," + str);
	    			}
	   				pW.println("  #Separate worlds by commas, or @all for all");
					notInConfig -= CARTENABLEDW;
				} if(notInConfig >= DELIVERYZONES){
					pW.println("delivery-cost-zones:"+deliveryZones+"  #More about this on the plugin page.");
					notInConfig -= DELIVERYZONES;
				} if(notInConfig >= ALLOWFILLCHESTS){
					pW.println("allow-filling-chests:"+allowFillChests);
					notInConfig -= ALLOWFILLCHESTS;
				} if(notInConfig >= AUTOPROTECT){
					pW.println("auto-protect-chests:"+autoprotect+"  #Protect auto-refilling chests automaticly from being opened outside a store. Just a little security feature.");
					notInConfig -= AUTOPROTECT;
				} if(notInConfig >= NOTTIMESPAN){
					pW.println("notificatior-update-frequency:"+notTimespan);
					notInConfig -= NOTTIMESPAN;
				} if(notInConfig >= ENABLEAI){
					pW.println("enable-automatic-store-management:"+enableAI);
					notInConfig -= ENABLEAI;
				} if(notInConfig >= UPDATEFREQ){
					pW.println("stat-updater-frequency:"+getTimeString(updateFreq));
					notInConfig -= UPDATEFREQ;
				} if(notInConfig >= STATTIMESPAN){
					pW.println("statistics-timespan:"+getTimeString(statTimespan));
					notInConfig -= STATTIMESPAN;
				} if(notInConfig >= CLEANSTATSOLD){
					pW.println("clean-stats-older-than:"+getTimeString(cleanStatsOld));
					notInConfig -= CLEANSTATSOLD;
				}
				pW.close();
				
				if(notInConfig >= 1){//Read the file all over again, and save every line as reading
					fstream = new FileInputStream(f);
					br = new BufferedReader(new InputStreamReader(fstream));
					File tempF = new File(RealShopping.MANDIR + "tempproperties");
					tempF.createNewFile();
					pW = new PrintWriter(tempF);
					String s;
					
					int i = 0;
					while ((s = br.readLine()) != null){
						if(i==0){
							pW.println("Properties file for RealShopping v0.43");
							pW.println("## Do not edit above line!");
							pW.println("## The rest of a line after a hashtag is a comment and will be ignored.");
							pW.println("#");
						}
						if(s.length() >= 33 && s.substring(0,33).equals("Properties file for RealShopping ")){}
						else if(s.length() >= 26 && s.substring(0,26).trim().equals("## Do not edit above line!")){}
						else if(s.length() >= 71 && s.substring(0,71).trim().equals("## The rest of a line after a hashtag is a comment and will be ignored.")){}
						else if(s.length() >= 1 && s.substring(0,1).trim().equals("#")){}
						else
							pW.println(s);
						i++;
					}
					
					pW.close();
					fstream.close();
					br.close();
					if(f.delete()){
						if(tempF.renameTo(f))
							notInConfig -= 1;
						else
							RealShopping.log.info("Couldn't save tempproperties as realshopping.properties (Error #202)");
					} else {
						RealShopping.log.info("Couldn't save tempproperties as realshopping.properties (Error #201)");
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			RealShopping.log.info("Failed while reading realshopping.properties. Default properties loaded.");
		} catch(IOException e) {
			e.printStackTrace();
			RealShopping.log.info("Failed while reading realshopping.properties. Default properties loaded.");
		}
	}
	
	static void resetVars(){
    	cartEnabledW = new HashSet<String>();
    	debug = false;
        keepstolen = false;
        enableSelling = false;
        autoprotect = false;
        deliveryZones = -1;
        autoUpdate = 0;
        punishment = null;
        langpack = null;
        hellLoc = null;
        jailLoc = null;
        dropLoc = null;
        pstorecreate = 0.0;
        notTimespan = 0;
        statTimespan = 0;
        cleanStatsOld = 0;
        updateFreq = 0;
        allowFillChests = false;
        enableAI = false;
        disableDrop = false;
        disableCrafting = false;
        disableBuckets = false;
        disableEnderchests = false;
        enableDoors = false;
	}
	
	private static void initVals(){
		int curVal = 2;
		for(int i = 0;i < 23;i++){
			if(i == 0) CLEANSTATSOLD = curVal;
			else if(i == 1) STATTIMESPAN = curVal;
			else if(i == 2) UPDATEFREQ = curVal;
			else if(i == 3) ENABLEAI = curVal;
			else if(i == 4) NOTTIMESPAN = curVal;
			else if(i == 5) AUTOPROTECT = curVal;
			else if(i == 6) ALLOWFILLCHESTS = curVal;
			else if(i == 7) DELIVERYZONES = curVal;
			else if(i == 8) CARTENABLEDW = curVal;
			else if(i == 9) DISABLEENDERCHESTS = curVal;
			else if(i == 10) DISABLEBUCKETS = curVal;
			else if(i == 11) DISABLECRAFTING = curVal;
			else if(i == 12) DISABLEDROP = curVal;
			else if(i == 13) ENABLEDOORS = curVal;
			else if(i == 14) ENABLESELLING = curVal;
			else if(i == 15) PSTORECREATE = curVal;
			else if(i == 16) DROPLOC = curVal;
			else if(i == 17) HELLLOC = curVal;
			else if(i == 18) JAILLOC = curVal;
			else if(i == 19) KEEPSTOLEN = curVal;
			else if(i == 20) PUNISHMENT = curVal;
			else if(i == 21) LANGPACK = curVal;
			else if(i == 22) AUTOUPDATE = curVal;
			curVal *= 2;
		}
		MAX = curVal - 1;
	}
	
	static int readConfigLine(String line){
		int foo = 0;
		boolean comment = false;
		while(true){
			if(line.length() > foo){
				if(line.charAt(foo) != ' ' && line.charAt(foo) != '\t'){//Continue searching if space or tab
					if(line.charAt(foo) == '#') comment = true;
					break;
				}
			}
			foo++;
		}
		if(!comment){
			String s = line.substring(foo, line.length()).split("#")[0].trim();
			if(s.length() > HEADER.length() && s.substring(0, HEADER.length()).equals(HEADER)){//If header
				version = Float.parseFloat(s.substring(HEADER.length()));
				if(version == 0.43) return 1;
			} else {
				if(s.equals("debug")){
					debug = true;
				} else if(s.split(":")[0].equals("punishment")){
					punishment = s.split(":")[1];
					return PUNISHMENT;
				} else if(s.split(":")[0].equals("keep-stolen-items-after-punish")){
					keepstolen = Boolean.parseBoolean(s.split(":")[1]);
					return KEEPSTOLEN;
				} else if(s.split(":")[0].equals("jail-location")){
					if(version >= 0.31){
						if(server.getWorld(s.split(":")[1].split(";")[0]) != null){
							jailLoc.setWorld(server.getWorld(s.split(":")[1].split(";")[0]));
							jailLoc.setX(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[0]));
							jailLoc.setY(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[1]));
							jailLoc.setZ(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[2]));
							return JAILLOC;
						}
					} else {
						jailLoc.setWorld(server.getWorld("world"));
						jailLoc.setX(Integer.parseInt(s.split(":")[1].split(",")[0]));
						jailLoc.setY(Integer.parseInt(s.split(":")[1].split(",")[1]));
						jailLoc.setZ(Integer.parseInt(s.split(":")[1].split(",")[2]));
						return JAILLOC;
					}
				} else if(s.split(":")[0].equals("hell-location")){
					if(version >= 0.31){
						if(server.getWorld(s.split(":")[1].split(";")[0]) != null){
							hellLoc.setWorld(server.getWorld(s.split(":")[1].split(";")[0]));
							hellLoc.setX(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[0]));
							hellLoc.setY(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[1]));
							hellLoc.setZ(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[2]));
							return HELLLOC;
						}
					} else {
						hellLoc.setWorld(server.getWorld("world"));
						hellLoc.setX(Integer.parseInt(s.split(":")[1].split(",")[0]));
						hellLoc.setY(Integer.parseInt(s.split(":")[1].split(",")[1]));
						hellLoc.setZ(Integer.parseInt(s.split(":")[1].split(",")[2]));
						return HELLLOC;
					}
				} else if(s.split(":")[0].equals("drop-items-at")){
					if(version >= 0.31){
						if(server.getWorld(s.split(":")[1].split(";")[0]) != null){
							dropLoc.setWorld(server.getWorld(s.split(":")[1].split(";")[0]));
							dropLoc.setX(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[0]));
							dropLoc.setY(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[1]));
							dropLoc.setZ(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[2]));
							return DROPLOC;
						}
					} else {
						dropLoc.setWorld(server.getWorld("world"));
						dropLoc.setX(Integer.parseInt(s.split(":")[1].split(",")[0]));
						dropLoc.setY(Integer.parseInt(s.split(":")[1].split(",")[1]));
						dropLoc.setZ(Integer.parseInt(s.split(":")[1].split(",")[2]));
						return DROPLOC;
					}
				} else if(s.split(":")[0].equals("player-stores-create-cost")){
					pstorecreate = Double.parseDouble(s.split(":")[1]);
					return PSTORECREATE;
				} else if(s.split(":")[0].equals("enable-shopping-carts-in-worlds")){
					if(s.split(":").length > 1){
						for(String str:s.split(":")[1].split(",")){
							if(str.equals("@all")){
								cartEnabledW.clear();
								cartEnabledW.add("@all");
								break;
							} else if(server.getWorld(str) != null) cartEnabledW.add(str);
						}
					}
					return CARTENABLEDW;
				} else if(s.split(":")[0].equals("enable-selling-to-stores")){
					enableSelling = Boolean.parseBoolean(s.split(":")[1]);
					return ENABLESELLING;
				} else if(s.split(":")[0].equals("enable-doors-as-entrances")){
					enableDoors = Boolean.parseBoolean(s.split(":")[1]);
					return ENABLEDOORS;
				} else if(s.split(":")[0].equals("disable-item-drop")){
					disableDrop = Boolean.parseBoolean(s.split(":")[1]);
					return DISABLEDROP;
				} else if(s.split(":")[0].equals("disable-crafting")){
					disableCrafting = Boolean.parseBoolean(s.split(":")[1]);
					return DISABLECRAFTING;
				} else if(s.split(":")[0].equals("disable-buckets")){
					disableBuckets = Boolean.parseBoolean(s.split(":")[1]);
					return DISABLEBUCKETS;
				} else if(s.split(":")[0].equals("disable-ender-chests")){
					disableEnderchests = Boolean.parseBoolean(s.split(":")[1]);
					return DISABLEENDERCHESTS;
				} else if(s.split(":")[0].equals("language-pack")){
					langpack = s.split(":")[1];
					return LANGPACK;
				} else if(s.split(":")[0].equals("delivery-cost-zones")){
					deliveryZones = Integer.parseInt(s.split(":")[1]);
					zoneArray = new Zone[deliveryZones];
					return DELIVERYZONES;
				} else if(s.split(":")[0].equals("auto-protect-chests")){
					autoprotect = Boolean.parseBoolean(s.split(":")[1]);
					return AUTOPROTECT;
				} else if(s.split(":")[0].equals("enable-automatic-updates")){
					String tempS = s.split(":")[1];
					if(tempS.equals("true")) autoUpdate = 5;
					else if(tempS.equals("ask")) autoUpdate = 4;
					else if(tempS.equals("ask-console")) autoUpdate = 3;
					else if(tempS.equals("check")) autoUpdate = 2;
					else if(tempS.equals("check-console")) autoUpdate = 1;
					else autoUpdate = 0;
					return AUTOUPDATE;
				} else if(s.length() > 8 && s.substring(0, 5).equals("zone ")){
					String tempS = s.substring(5);
					int nr = Integer.parseInt(tempS.split(":")[0]);
					if(zoneArray.length >= nr)
						if(tempS.split(":")[1].equalsIgnoreCase("world")){
							if(tempS.endsWith("%")) zoneArray[nr - 1] = new Zone(true, Integer.parseInt(tempS.split(":")[2].split("%")[0]));
							else zoneArray[nr - 1] = new Zone(true, Double.parseDouble(tempS.split(":")[2]));
						} else {
							if(tempS.endsWith("%")) zoneArray[nr - 1] = new Zone(Integer.parseInt(tempS.split(":")[1]), Integer.parseInt(tempS.split(":")[2].split("%")[0]));
							else zoneArray[nr - 1] = new Zone(Integer.parseInt(tempS.split(":")[1]), Double.parseDouble(tempS.split(":")[2]));
						}
				} else if(s.split(":")[0].equals("allow-filling-chests")){
					allowFillChests = Boolean.parseBoolean(s.split(":")[1]);
					return ALLOWFILLCHESTS;
				} else if(s.split(":")[0].equals("enable-automatic-store-management")){
					enableAI = Boolean.parseBoolean(s.split(":")[1]);
					return ENABLEAI;
				} else if(s.split(":")[0].equals("stat-updater-frequency")){
					updateFreq = getTimeInt(s.split(":")[1]);
					return UPDATEFREQ;
				} else if(s.split(":")[0].equals("statistics-timespan")){
					statTimespan = getTimeInt(s.split(":")[1]);
					return STATTIMESPAN;
				} else if(s.split(":")[0].equals("clean-stats-older-than")){
					cleanStatsOld = getTimeInt(s.split(":")[1]);
					return CLEANSTATSOLD;
				} else if(s.split(":")[0].equals("notificatior-update-frequency")){
					notTimespan = Integer.parseInt(s.split(":")[1]);
					return NOTTIMESPAN;
				}
			}
		}
		return 0;
	}
	
	public static String getAutoUpdateStr(byte au){
		if(au == 5) return "true";//Download the new version as soon as there is one. Prints info in console
		else if(au == 4) return "ask";//Check for updates, alert all with rsupdate permission/OP. Update with rsupdate
		else if(au == 3) return "ask-console";//Check for updates, alert in console and be able to update via console with rsupdate
		else if(au == 2) return "check";//Check for updates and alert all with rsupdate permission
		else if(au == 1) return "check-console";//Check for updates, alert about new version in console
		else return "false";//No automatic updates at all
	}
	
	public static String getTimeString(int t){
		if(t == 3600) return "hour";
		else if(t == 86400) return "day";
		else if(t == 604800) return "week";
		else if(t == 2592000) return "month";
		else return t + "";
	}
	
	static int getTimeInt(String s){
		if(s.equals("hour")) return 3600;
		else if(s.equals("day")) return 86400;
		else if(s.equals("week")) return 604800;
		else if(s.equals("month")) return 2592000;
		else return Integer.parseInt(s);
	}

	public static boolean isKeepstolen() { return keepstolen; }
	public static boolean isEnableSelling() { return enableSelling; }
	public static boolean isAutoprotect() { return autoprotect; }
	public static boolean isAllowFillChests() { return allowFillChests; }
	public static boolean isEnableDoors() { return enableDoors; }
	public static boolean isEnableAI() { return enableAI; }
	public static boolean isDisableDrop() { return disableDrop; }
	public static boolean isDisableCrafting() { return disableCrafting; }
	public static boolean isDisableBuckets() { return disableBuckets; }	
	public static boolean isDisableEnderchests() { return disableEnderchests; }
	public static String getPunishment() { return punishment; }
	public static String getLangpack() { return langpack; }
	public static double getPstorecreate() { return pstorecreate; }
	public static Location getHellLoc() { return hellLoc; }
	public static Location getJailLoc() { return jailLoc; }
	public static Location getDropLoc() { return dropLoc; }
	public static int getDeliveryZones() { return deliveryZones; }
	public static Zone[] getZoneArray() { return zoneArray; }
	public static byte getAutoUpdate() { return autoUpdate; }
	public static int getNotTimespan() { return notTimespan; }	
	public static int getStatTimespan() { return statTimespan; }
	public static int getCleanStatsOld() { return cleanStatsOld; }
	public static int getUpdateFreq() { return updateFreq; }
	public static Set<String> getCartEnabledW() { return cartEnabledW; }
	public static boolean isCartEnabledW(String s) { if(cartEnabledW.contains("@all") || cartEnabledW.contains(s)) return true; return false; }

}

class Zone {
	private int bounds = 0;
	private double cost = 0.0;
	private int percent = -1; //Ignore cost if percent != -1
	
	public Zone(int bounds, double cost){
		if(!setBounds(bounds)) RealShopping.log.info("Could not create delivery zone. Wrong bounds value: " + bounds);
		if(!setCost(cost)) RealShopping.log.info("Could not create delivery zone. Wrong cost value: " + cost);
	}
	
	public Zone(int bounds, int percent){
		if(!setBounds(bounds)) RealShopping.log.info("Could not create delivery zone. Wrong bounds value: " + bounds);
		if(!setPercent(percent)) RealShopping.log.info("Could not create delivery zone. Wrong percent value: " + percent);
	}
	
	public Zone(boolean multiworld, double cost){
		setMultiworld();
		if(!setCost(cost)) RealShopping.log.info("Could not create delivery zone. Wrong cost value: " + cost);
	}
	
	public Zone(boolean multiworld, int percent){
		setMultiworld();
		if(!setPercent(percent)) RealShopping.log.info("Could not create delivery zone. Wrong percent value: " + percent);
	}

	public int getBounds() {
		return bounds;
	}

	public boolean setBounds(int bounds) {
		if(bounds > 0){
			this.bounds = bounds;
			return true;
		} else return false;
	}

	public void setMultiworld() {
		bounds = -1;
	}

	public double getCost() {
		return cost;
	}

	public boolean setCost(double cost) {
		if(cost >= 0){
			this.cost = (Math.round(cost * 100));
			this.cost /= 100;
			return true;
		} else return false;
	}

	public int getPercent() {
		return percent;
	}

	public boolean setPercent(int percent) {
		if(percent >= 0){
			this.percent = percent;
			return true;
		} else return false;
	}
	public void resetPercent(){
		percent = -1;
	}
	
	@Override
	public String toString(){
		if(bounds > -1){
			if(percent > -1)
				return "Zone ending at " + bounds + " with " + percent + "% delivery cost.";
			else
				return "Zone ending at " + bounds + " with " + cost + LangPack.UNIT + " delivery cost.";
		} else {
			if(percent > -1)
				return "Multiworld zone with " + percent + "% delivery cost.";
			else
				return "Multiworld zone with " + cost + LangPack.UNIT + " delivery cost.";
		}
	}
}