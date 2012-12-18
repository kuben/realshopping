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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;

public class Config {

	public static boolean debug;
	
	public static boolean keepstolen;
	public static boolean enableSelling;
	public static boolean autoprotect;
	public static boolean allowFillChests;
	public static boolean enableAI;
	public static String punishment;
	public static String langpack;
	public static double pstorecreate;
	public static Location hellLoc;
	public static Location jailLoc;
	public static Location dropLoc;
	
	public static int deliveryZones;
	public static Zone[] zoneArray;
	public static byte autoUpdate;
	
	public static int notTimespan;//[millis] anything bellow 500 means disabled
	public static int statTimespan;//[secs]
	public static int cleanStatsOld;//Clean stats older than [secs]
	public static int updateFreq;//How often the statupdater should be run [secs]
	
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
	
	private static int MAX;
	
	public static Set<String> cartEnabledW;
	
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
    	File f = new File(RealShopping.mandir);
    	if(!f.exists()) f.mkdir();
		FileInputStream fstream;
		BufferedReader br;
		try {
			f = new File(RealShopping.mandir + "realshopping.properties");
			int notInConfig = MAX;
			if(f.exists()) {
				fstream = new FileInputStream(f);
				br = new BufferedReader(new InputStreamReader(fstream));
				String s;
				while ((s = br.readLine()) != null){// Read realshopping.properties
					notInConfig -= readConfigLine(s);
					System.out.println(notInConfig);
				}
				fstream.close();
				br.close();
			}
			
			if(!f.exists() || notInConfig > 0){
				if(!f.exists()) f.createNewFile();
				PrintWriter pW = new PrintWriter(new BufferedWriter(new FileWriter(RealShopping.mandir + "realshopping.properties", true)));
				
				if(notInConfig >= NOTTIMESPAN){
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
				} if(notInConfig >= ALLOWFILLCHESTS){
					pW.println("allow-filling-chests:"+allowFillChests);
					notInConfig -= ALLOWFILLCHESTS;
				} if(notInConfig >= AUTOUPDATE){
					pW.println("enable-automatic-updates:"+getAutoUpdateStr(autoUpdate));
					notInConfig -= AUTOUPDATE;
				} if(notInConfig >= AUTOPROTECT){
					pW.println("auto-protect-chests:"+autoprotect);
					notInConfig -= AUTOPROTECT;
				} if(notInConfig >= DELIVERYZONES){
					pW.println("delivery-cost-zones:"+deliveryZones);
					notInConfig -= DELIVERYZONES;
				} if(notInConfig >= LANGPACK){
					pW.println("language-pack:"+langpack);
					notInConfig -= LANGPACK;
				} if(notInConfig >= ENABLESELLING){
					pW.println("enable-selling-to-stores:"+enableSelling);
					notInConfig -= ENABLESELLING;
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
	   				pW.println();
					notInConfig -= CARTENABLEDW;
				} if(notInConfig >= PSTORECREATE){
					pW.println("player-stores-create-cost:"+pstorecreate);
					notInConfig -= PSTORECREATE;
				} if(notInConfig >= DROPLOC){
					pW.println("drop-items-at:"+dropLoc.getWorld().getName()+";"+dropLoc.getBlockX()+","+dropLoc.getBlockY()+","+dropLoc.getBlockZ());
					notInConfig -= DROPLOC;
				} if(notInConfig >= HELLLOC){
					pW.println("hell-location:"+hellLoc.getWorld().getName()+";"+hellLoc.getBlockX()+","+hellLoc.getBlockY()+","+hellLoc.getBlockZ());
					notInConfig -= HELLLOC;
				} if(notInConfig >= JAILLOC){
					pW.println("jail-location:"+jailLoc.getWorld().getName()+";"+jailLoc.getBlockX()+","+jailLoc.getBlockY()+","+jailLoc.getBlockZ());
					notInConfig -= JAILLOC;
				} if(notInConfig >= KEEPSTOLEN){
					pW.println("keep-stolen-items-after-punish:"+keepstolen);
					notInConfig -= KEEPSTOLEN;
				} if(notInConfig >= PUNISHMENT){
					pW.println("punishment:"+punishment);
					notInConfig -= PUNISHMENT;
				}
				pW.close();
				
				if(notInConfig >= 1){//Read the file all over again, and save every line as reading
					fstream = new FileInputStream(f);
					br = new BufferedReader(new InputStreamReader(fstream));
					File tempF = new File(RealShopping.mandir + "tempproperties");
					tempF.createNewFile();
					pW = new PrintWriter(tempF);
					String s;
					
					int i = 0;
					while ((s = br.readLine()) != null){
						if(i==0) pW.println("Properties file for RealShopping v0.40");
						if(s.length() > 33 && s.substring(0,33).equals("Properties file for RealShopping ")){}
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
	
	private static void initVals(){
		int curVal = 1;
		for(int i = 0;i < 17;i++){
			if(i == 0) KEEPSTOLEN = curVal;
			else if(i == 1) ENABLESELLING = curVal;
			else if(i == 2) AUTOPROTECT = curVal;
			else if(i == 3) ALLOWFILLCHESTS = curVal;
			else if(i == 4) ENABLEAI = curVal;
			else if(i == 5) PUNISHMENT = curVal;
			else if(i == 6) LANGPACK = curVal;
			else if(i == 7) PSTORECREATE = curVal;
			else if(i == 8) HELLLOC = curVal;
			else if(i == 9) JAILLOC = curVal;
			else if(i == 10) DROPLOC = curVal;
			else if(i == 11) DELIVERYZONES = curVal;
			else if(i == 12) AUTOUPDATE = curVal;
			else if(i == 13) NOTTIMESPAN = curVal;
			else if(i == 14) STATTIMESPAN = curVal;
			else if(i == 15) CLEANSTATSOLD = curVal;
			else if(i == 16) UPDATEFREQ = curVal;
			else if(i == 17) CARTENABLEDW = curVal;
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
			System.out.println(s);
			if(s.length() > HEADER.length() && s.substring(0, HEADER.length()).equals(HEADER)){//If header
				System.out.println("header");
				version = Float.parseFloat(s.substring(HEADER.length()));
				System.out.println(version);
				if(version == 0.40) return 1;
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
	
	static String getAutoUpdateStr(byte au){
		if(au == 5) return "true";//Download the new version as soon as there is one. Prints info in console
		else if(au == 4) return "ask";//Check for updates, alert all with rsupdate permission/OP. Update with rsupdate
		else if(au == 3) return "ask-console";//Check for updates, alert in console and be able to update via console with rsupdate
		else if(au == 2) return "check";//Check for updates and alert all with rsupdate permission
		else if(au == 1) return "check-console";//Check for updates, alert about new version in console
		else return "false";//No automatic updates at all
	}
	
	static String getTimeString(int t){
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