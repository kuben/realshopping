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

	public static boolean keepstolen;
	public static boolean enableSelling;
	public static boolean autoprotect;
	public static String punishment;
	public static String langpack;
	public static double pstorecreate;
	public static Location hellLoc;
	public static Location jailLoc;
	public static Location dropLoc;
	
	public static int deliveryZones;
	public static Zone[] zoneArray;
	public static byte autoUpdate;
	
	public static Set<String> cartEnabledW;
	
	static Server server;
	
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

        keepstolen = false;
        langpack = "default";
        punishment = "none";
        pstorecreate = 0.0;
        enableSelling = true;
        autoprotect = true;
        deliveryZones = 0;
        autoUpdate = 0;
        zoneArray = new Zone[0];
    	File f = new File(RealShopping.mandir);
    	if(!f.exists()) f.mkdir();
		FileInputStream fstream;
		BufferedReader br;
		try {
			f = new File(RealShopping.mandir + "realshopping.properties");
			int notInConfig = 8191;
			if(f.exists()) {
				fstream = new FileInputStream(f);
				br = new BufferedReader(new InputStreamReader(fstream));
				String s;
				boolean v31plus = false;
				while ((s = br.readLine()) != null){// Read realshopping.properties
					if(s.equals("Properties file for RealShopping v0.33")){
						notInConfig -= 1;
						v31plus = true;
					} else if(s.equals("Properties file for RealShopping v0.32")){
						v31plus = true;
					} else if(s.equals("Properties file for RealShopping v0.31")){
						v31plus = true;
					} else if(s.split(":")[0].equals("punishment")){
						punishment = s.split(":")[1];
						notInConfig -= 2;
					} else if(s.split(":")[0].equals("keep-stolen-items-after-punish")){
						keepstolen = Boolean.parseBoolean(s.split(":")[1]);
						notInConfig -= 4;
					} else if(s.split(":")[0].equals("jail-location")){
						if(v31plus){
							if(server.getWorld(s.split(":")[1].split(";")[0]) != null){
								jailLoc.setWorld(server.getWorld(s.split(":")[1].split(";")[0]));
								jailLoc.setX(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[0]));
								jailLoc.setY(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[1]));
								jailLoc.setZ(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[2]));
								notInConfig -= 8;
							}
						} else {
							jailLoc.setWorld(server.getWorld("world"));
							jailLoc.setX(Integer.parseInt(s.split(":")[1].split(",")[0]));
							jailLoc.setY(Integer.parseInt(s.split(":")[1].split(",")[1]));
							jailLoc.setZ(Integer.parseInt(s.split(":")[1].split(",")[2]));
							notInConfig -= 8;
						}
					} else if(s.split(":")[0].equals("hell-location")){
						if(v31plus){
							if(server.getWorld(s.split(":")[1].split(";")[0]) != null){
								hellLoc.setWorld(server.getWorld(s.split(":")[1].split(";")[0]));
								hellLoc.setX(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[0]));
								hellLoc.setY(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[1]));
								hellLoc.setZ(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[2]));
								notInConfig -= 16;
							}
						} else {
							hellLoc.setWorld(server.getWorld("world"));
							hellLoc.setX(Integer.parseInt(s.split(":")[1].split(",")[0]));
							hellLoc.setY(Integer.parseInt(s.split(":")[1].split(",")[1]));
							hellLoc.setZ(Integer.parseInt(s.split(":")[1].split(",")[2]));
							notInConfig -= 16;
						}
					} else if(s.split(":")[0].equals("drop-items-at")){
						if(v31plus){
							if(server.getWorld(s.split(":")[1].split(";")[0]) != null){
								dropLoc.setWorld(server.getWorld(s.split(":")[1].split(";")[0]));
								dropLoc.setX(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[0]));
								dropLoc.setY(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[1]));
								dropLoc.setZ(Integer.parseInt(s.split(":")[1].split(";")[1].split(",")[2]));
								notInConfig -= 32;
							}
						} else {
							dropLoc.setWorld(server.getWorld("world"));
							dropLoc.setX(Integer.parseInt(s.split(":")[1].split(",")[0]));
							dropLoc.setY(Integer.parseInt(s.split(":")[1].split(",")[1]));
							dropLoc.setZ(Integer.parseInt(s.split(":")[1].split(",")[2]));
							notInConfig -= 32;
						}
					} else if(s.split(":")[0].equals("player-stores-create-cost")){
						pstorecreate = Double.parseDouble(s.split(":")[1]);
						notInConfig -= 64;
					} else if(s.split(":")[0].equals("enable-shopping-carts-in-worlds")){
						if(s.split(":").length > 1){
							for(String str:s.split(":")[1].split(",")){
								if(server.getWorld(str) != null) cartEnabledW.add(str);
							}
						}
						notInConfig -= 128;
					} else if(s.split(":")[0].equals("enable-selling-to-stores")){
						enableSelling = Boolean.parseBoolean(s.split(":")[1]);
						notInConfig -= 256;
					} else if(s.split(":")[0].equals("language-pack")){
						langpack = s.split(":")[1];
						notInConfig -= 512;
					} else if(s.split(":")[0].equals("delivery-cost-zones")){
						deliveryZones = Integer.parseInt(s.split(":")[1]);
						zoneArray = new Zone[deliveryZones];
						notInConfig -= 1024;
					} else if(s.split(":")[0].equals("auto-protect-chests")){
						autoprotect = Boolean.parseBoolean(s.split(":")[1]);
						notInConfig -= 2048;
					} else if(s.split(":")[0].equals("enable-automatic-updates")){
						String tempS = s.split(":")[1];
						if(tempS.equals("true")) autoUpdate = 5;
						else if(tempS.equals("ask")) autoUpdate = 4;
						else if(tempS.equals("ask-console")) autoUpdate = 3;
						else if(tempS.equals("check")) autoUpdate = 2;
						else if(tempS.equals("check-console")) autoUpdate = 1;
						else autoUpdate = 0;
						notInConfig -= 4096;
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
					}
				}
				fstream.close();
				br.close();
			}
			
			if(!f.exists() || notInConfig > 0){
				if(!f.exists()) f.createNewFile();
				PrintWriter pW = new PrintWriter(new BufferedWriter(new FileWriter(RealShopping.mandir + "realshopping.properties", true)));
				if(notInConfig >= 4096){
					pW.println("enable-automatic-updates:"+getAutoUpdateStr(autoUpdate));
					notInConfig -= 4096;
				} if(notInConfig >= 2048){
					pW.println("auto-protect-chests:"+autoprotect);
					notInConfig -= 2048;
				} if(notInConfig >= 1024){
					pW.println("delivery-cost-zones:"+deliveryZones);
					notInConfig -= 1024;
				} if(notInConfig >= 512){
					pW.println("language-pack:"+langpack);
					notInConfig -= 512;
				} if(notInConfig >= 256){
					pW.println("enable-selling-to-stores:"+enableSelling);
					notInConfig -= 256;
				} if(notInConfig >= 128){
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
					notInConfig -= 128;
				} if(notInConfig >= 64){
					pW.println("player-stores-create-cost:"+pstorecreate);
					notInConfig -= 64;
				} if(notInConfig >= 32){
					System.out.println(dropLoc);
					pW.println("drop-items-at:"+dropLoc.getWorld().getName()+";"+dropLoc.getBlockX()+","+dropLoc.getBlockY()+","+dropLoc.getBlockZ());
					notInConfig -= 32;
				} if(notInConfig >= 16){
					pW.println("hell-location:"+hellLoc.getWorld().getName()+";"+hellLoc.getBlockX()+","+hellLoc.getBlockY()+","+hellLoc.getBlockZ());
					notInConfig -= 16;
				} if(notInConfig >= 8){
					pW.println("jail-location:"+jailLoc.getWorld().getName()+";"+jailLoc.getBlockX()+","+jailLoc.getBlockY()+","+jailLoc.getBlockZ());
					notInConfig -= 8;
				} if(notInConfig >= 4){
					pW.println("keep-stolen-items-after-punish:"+keepstolen);
					notInConfig -= 4;
				} if(notInConfig >= 2){
					pW.println("punishment:"+punishment);
					notInConfig -= 2;
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
						if(i==0) pW.println("Properties file for RealShopping v0.33");
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
	
	static String getAutoUpdateStr(byte au){
		if(au == 5) return "true";//Download the new version as soon as there is one. Prints info in console
		else if(au == 4) return "ask";//Check for updates, alert all with rsupdate permission/OP. Update with rsupdate
		else if(au == 3) return "ask-console";//Check for updates, alert in console and be able to update via console with rsupdate
		else if(au == 2) return "check";//Check for updates and alert all with rsupdate permission
		else if(au == 1) return "check-console";//Check for updates, alert about new version in console
		else return "false";//No automatic updates at all
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