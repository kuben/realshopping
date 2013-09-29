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

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;

public class Config {

	public static boolean debug;
	
	/*
	 * To add:
	 * allow reports (period)
	 * max notifications per user
	 * max notifications overall
	 * 
	 */
	
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
	
	private static final String HEADER = "Properties file for RealShopping v";
	
	//Used to initialize Settings order values
	private static int curVal = 2;
	
	static Server server;
	private static float version = 0f;
	
	/*
	 * Hour - 3600 seconds
	 * Day - 86,400 seconds
	 * Week - 604,800 seconds
	 * Month (30 days) - 2,592,000 seconds
	 */
	
	public static void initialize(){
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
		cartEnabledW = new HashSet<String>();
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
		
		if(curVal == 2){//Initialize Setting values from here. If curVal is something other than 2, they have already been initailized.
		    Setting.values();//This is the first time Setting is called, hence all the settings are initialized and they render the correct orderValues.
		}
		int notInConfig = curVal - 1;

		try {
			f = new File(RealShopping.MANDIR + "realshopping.properties");
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
				
				//Look for missing lines and append them to the file
				Setting[] vals = Setting.values();
				ArrayUtils.reverse(vals);//Check for highest values first
				for(Setting s:vals){
				    if(s.orderValue > notInConfig) continue;//Continue if line is in config
				    
				    pW.println(s.configString + ":" + s.returnVar());
				    notInConfig -= s.orderValue;
				}
				
				pW.close();
				
				//Read the file all over again, and save every line as reading
				if(notInConfig >= 1){
					fstream = new FileInputStream(f);
					br = new BufferedReader(new InputStreamReader(fstream));
					File tempF = new File(RealShopping.MANDIR + "tempproperties");
					tempF.createNewFile();
					pW = new PrintWriter(tempF);
					String s;
					
					int i = 0;
					while ((s = br.readLine()) != null){
					    if(i==0){
					        pW.println("Properties file for RealShopping " + RealShopping.VERSION);
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
					    if(tempF.renameTo(f)) notInConfig -= 1;
					    else RealShopping.loginfo("Couldn't save tempproperties as realshopping.properties (Error #202)");
					} else {
					    RealShopping.loginfo("Couldn't save tempproperties as realshopping.properties (Error #201)");
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			RealShopping.loginfo("Failed while reading realshopping.properties. Default properties loaded.");
		} catch(IOException e) {
			e.printStackTrace();
			RealShopping.loginfo("Failed while reading realshopping.properties. Default properties loaded.");
		}
	}
		
	static int readConfigLine(String line){
	    int beginning = 0;
	    boolean comment = false;
	    while(true){
	        if(line.length() <= beginning){
	            comment = true;//Empty line, break.
	            break;
	        }
	        if(line.charAt(beginning) != ' ' && line.charAt(beginning) != '\t'){//Continue searching if space or tab
	            if(line.charAt(beginning) == '#') comment = true;
	            break;
	        }
	        beginning++;
		}
		if(!comment){
			String s = line.substring(beginning, line.length()).split("#")[0].trim();
			if(s.length() > HEADER.length() && s.substring(0, HEADER.length()).equals(HEADER)){//If header
				version = Float.parseFloat(s.substring(HEADER.length()));
				if(version == RealShopping.VERFLOAT) return 1;
			} else {
				if(s.equals("debug")){
					debug = true;
				} else {
				    for(Setting sett:Setting.values()){
				        if(sett.configString.equals(s.split(":")[0])){
				            if(s.indexOf(":") + 1 > s.length()) sett.setVar(s.substring(s.indexOf(":") + 1));
				            return sett.orderValue;
				        }
				    }
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

	
	private enum Setting {
	    //Lines are displayed in config in reverse order
	    //The further down a line is, the higher its order value, and the sooner is it checked for.
	    CLEANSTATSOLD("clean-stats-older-than"),
	    STATTIMESPAN("statistics-timespan"),
	    UPDATEFREQ("stat-updater-frequency"),
	    ENABLEAI("enable-automatic-store-management"),
	    NOTTIMESPAN("notificatior-update-frequency"),
	    AUTOPROTECT("auto-protect-chests"),
	    ALLOWFILLCHESTS("allow-filling-chests"),
	    DELIVERYZONES("delivery-cost-zones"),
	    CARTENABLEDW("enable-shopping-carts-in-worlds"),
	    DISABLEENDERCHESTS("disable-ender-chests"),
	    DISABLEBUCKETS("disable-buckets"),
	    DISABLECRAFTING("disable-crafting"),
	    DISABLEDROP("disable-item-drop"),
	    ENABLEDOORS("enable-doors-as-entrances"),
	    ENABLESELLING("enable-selling-to-stores"),
	    PSTORECREATE("player-stores-create-cost"),
	    DROPLOC("drop-items-at"),
	    HELLLOC("hell-location"),
	    JAILLOC("jail-location"),
	    KEEPSTOLEN("keep-stolen-items-after-punish"),
	    PUNISHMENT("punishment"),
	    LANGPACK("language-pack"),
	    AUTOUPDATE("enable-automatic-updates");

	    private final String configString;
	    private final int orderValue;
	    
	    Setting(String configString){
	        this.configString = configString;
	        this.orderValue = curVal;
	        curVal *= 2;
	    }
	    void setVar(String setting){
	        switch(this){
	            case PUNISHMENT:
	                punishment = setting;
	                break;
	            case KEEPSTOLEN:
	                keepstolen = Boolean.parseBoolean(setting);
	                break;
	            case JAILLOC:
	                if(version >= 0.31){
	                    if(server.getWorld(setting.split(";")[0]) != null){
	                        jailLoc.setWorld(server.getWorld(setting.split(";")[0]));
	                        jailLoc.setX(Integer.parseInt(setting.split(";")[1].split(",")[0]));
	                        jailLoc.setY(Integer.parseInt(setting.split(";")[1].split(",")[1]));
	                        jailLoc.setZ(Integer.parseInt(setting.split(";")[1].split(",")[2]));
	                    }
	                } else {
	                    jailLoc.setWorld(server.getWorld("world"));
	                    jailLoc.setX(Integer.parseInt(setting.split(",")[0]));
	                    jailLoc.setY(Integer.parseInt(setting.split(",")[1]));
	                    jailLoc.setZ(Integer.parseInt(setting.split(",")[2]));
	                }
	                break;
	            case HELLLOC:
	                if(version >= 0.31){
	                    if(server.getWorld(setting.split(";")[0]) != null){
	                        hellLoc.setWorld(server.getWorld(setting.split(";")[0]));
	                        hellLoc.setX(Integer.parseInt(setting.split(";")[1].split(",")[0]));
	                        hellLoc.setY(Integer.parseInt(setting.split(";")[1].split(",")[1]));
	                        hellLoc.setZ(Integer.parseInt(setting.split(";")[1].split(",")[2]));
	                    }
	                } else {
	                    hellLoc.setWorld(server.getWorld("world"));
	                    hellLoc.setX(Integer.parseInt(setting.split(",")[0]));
	                    hellLoc.setY(Integer.parseInt(setting.split(",")[1]));
	                    hellLoc.setZ(Integer.parseInt(setting.split(",")[2]));
	                }
	                break;
	            case DROPLOC:
	                if(version >= 0.31){
	                    if(server.getWorld(setting.split(";")[0]) != null){
	                        dropLoc.setWorld(server.getWorld(setting.split(";")[0]));
	                        dropLoc.setX(Integer.parseInt(setting.split(";")[1].split(",")[0]));
	                        dropLoc.setY(Integer.parseInt(setting.split(";")[1].split(",")[1]));
	                        dropLoc.setZ(Integer.parseInt(setting.split(";")[1].split(",")[2]));
	                    }
	                } else {
	                    dropLoc.setWorld(server.getWorld("world"));
	                    dropLoc.setX(Integer.parseInt(setting.split(",")[0]));
	                    dropLoc.setY(Integer.parseInt(setting.split(",")[1]));
	                    dropLoc.setZ(Integer.parseInt(setting.split(",")[2]));
	                }
	                break;
	            case PSTORECREATE:
	                pstorecreate = Double.parseDouble(setting);
	                break;
	            case CARTENABLEDW:
	                for(String str:setting.split(",")){
	                    if(str.equals("@all")){
	                        cartEnabledW.clear();
	                        cartEnabledW.add("@all");
	                        break;
	                    } else if(server.getWorld(str) != null) cartEnabledW.add(str);
	                }
	                break;
	            case ENABLESELLING:
	                enableSelling = Boolean.parseBoolean(setting);
	                break;
	            case ENABLEDOORS:
	                enableDoors = Boolean.parseBoolean(setting);
	                break;
	            case DISABLEDROP:
	                disableDrop = Boolean.parseBoolean(setting);
	                break;
	            case DISABLECRAFTING:
	                disableCrafting = Boolean.parseBoolean(setting);
	                break;
	            case DISABLEBUCKETS:
	                disableBuckets = Boolean.parseBoolean(setting);
	                break;
	            case DISABLEENDERCHESTS:
	                disableEnderchests = Boolean.parseBoolean(setting);
	                break;
	            case LANGPACK:
	                langpack = setting;
	                break;
	            case DELIVERYZONES:
	                deliveryZones = Integer.parseInt(setting);
	                zoneArray = new Zone[deliveryZones];
	                break;
	            case AUTOPROTECT:
	                autoprotect = Boolean.parseBoolean(setting);
	                break;
	            case AUTOUPDATE:
	                String tempS = setting;
	                if(tempS.equals("true")) autoUpdate = 5;
	                else if(tempS.equals("ask")) autoUpdate = 4;
	                else if(tempS.equals("ask-console")) autoUpdate = 3;
	                else if(tempS.equals("check")) autoUpdate = 2;
	                else if(tempS.equals("check-console")) autoUpdate = 1;
	                else autoUpdate = 0;
/*	        } else if(s.length() > 8 && s.substring(0, 5).equals("zone ")){
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
	            break;*/
	            case ALLOWFILLCHESTS:
	                allowFillChests = Boolean.parseBoolean(setting);
	                break;
	            case ENABLEAI:
	                enableAI = Boolean.parseBoolean(setting);
	                break;
	            case UPDATEFREQ:
	                updateFreq = RSUtils.getTimeInt(setting);
	                break;
	            case STATTIMESPAN:
	                statTimespan = RSUtils.getTimeInt(setting);
	                break;
	            case CLEANSTATSOLD:
	                cleanStatsOld = RSUtils.getTimeInt(setting);
	                break;
	            case NOTTIMESPAN:
	                notTimespan = Integer.parseInt(setting);
	        }
	    }
	        
	    String returnVar(){
	        switch(this){
	            case AUTOUPDATE:
	                return getAutoUpdateStr(autoUpdate)+"  #Can be false, check-console, check, ask-console, ask, or true";
	            case LANGPACK:
	                return langpack+"  #located in langpacks/ , without the .xml ending";
	            case PUNISHMENT:
	                return punishment;
	            case KEEPSTOLEN:
	                return keepstolen + "";
	            case JAILLOC:
	                return jailLoc.getWorld().getName()+";"+jailLoc.getBlockX()+","+jailLoc.getBlockY()+","+jailLoc.getBlockZ();
	            case HELLLOC:
	                return hellLoc.getWorld().getName()+";"+hellLoc.getBlockX()+","+hellLoc.getBlockY()+","+hellLoc.getBlockZ();
	            case DROPLOC:
	                return dropLoc.getWorld().getName()+";"+dropLoc.getBlockX()+","+dropLoc.getBlockY()+","+dropLoc.getBlockZ();
	            case PSTORECREATE:
	                return pstorecreate + "";
	            case ENABLESELLING:
	                return enableSelling + "";
	            case ENABLEDOORS:
	                return enableDoors + "";
	            case DISABLEDROP:
	                return disableDrop+"  #Disable dropping items in stores.";
	            case DISABLECRAFTING:
	                return disableCrafting+"  #Disable crafting in stores.";
	            case DISABLEBUCKETS:
	                return disableBuckets+"  #Disable using buckets in stores.";
	            case DISABLEENDERCHESTS:
	                return disableEnderchests+"  #Disable using Ender Chests in stores.";
	            case CARTENABLEDW:
	                String s = "";
	                for(String str:cartEnabledW){
	                    if(!s.equals("")) s += ",";
	                    s += str;
	                }
	                return s + "  #Separate worlds by commas, or @all for all";
	            case DELIVERYZONES:
	                return deliveryZones+"  #More about this on the plugin page.";
	            case ALLOWFILLCHESTS:
	                return allowFillChests + "";
	            case AUTOPROTECT:
	                return autoprotect+"  #Protect auto-refilling chests automaticly from being opened outside a store. Just a little security feature.";
	            case NOTTIMESPAN:
	                return notTimespan + "";
	            case ENABLEAI:
	                return enableAI + "";
	            case UPDATEFREQ:
	                return RSUtils.getTimeString(updateFreq);
	            case STATTIMESPAN:
	                return RSUtils.getTimeString(statTimespan);
	            case CLEANSTATSOLD:
	                return RSUtils.getTimeString(cleanStatsOld);
	            default :
	                return "";
	        }
	    }

	}
}

class Zone {
	private int bounds = 0;
	private double cost = 0.0;
	private int percent = -1; //Ignore cost if percent != -1
	
	public Zone(int bounds, double cost){
		if(!setBounds(bounds)) RealShopping.loginfo("Could not create delivery zone. Wrong bounds value: " + bounds);
		if(!setCost(cost)) RealShopping.loginfo("Could not create delivery zone. Wrong cost value: " + cost);
	}
	
	public Zone(int bounds, int percent){
		if(!setBounds(bounds)) RealShopping.loginfo("Could not create delivery zone. Wrong bounds value: " + bounds);
		if(!setPercent(percent)) RealShopping.loginfo("Could not create delivery zone. Wrong percent value: " + percent);
	}
	
	public Zone(boolean multiworld, double cost){
		setMultiworld();
		if(!setCost(cost)) RealShopping.loginfo("Could not create delivery zone. Wrong cost value: " + cost);
	}
	
	public Zone(boolean multiworld, int percent){
		setMultiworld();
		if(!setPercent(percent)) RealShopping.loginfo("Could not create delivery zone. Wrong percent value: " + percent);
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