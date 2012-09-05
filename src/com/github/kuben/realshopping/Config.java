package com.github.kuben.realshopping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	public static String punishment;
	public static String langpack;
	public static double pstorecreate;
	public static Location hellLoc;
	public static Location jailLoc;
	public static Location dropLoc;
	
	public static Set<String> cartEnabledW;
	
	static Server server;
	
	public static void initialize(){
		server = Bukkit.getServer();
		if(server.getWorld("world") != null){
			hellLoc = new Location(server.getWorld("world"),0,0,0);
			jailLoc = new Location(server.getWorld("world"),0,0,0);
			dropLoc = new Location(server.getWorld("world"),0,0,0);
		}

        keepstolen = false;
        langpack = "default";
        punishment = "none";
        pstorecreate = 0.0;
        enableSelling = true;
    	File f = new File(RealShopping.mandir);
    	if(!f.exists()) f.mkdir();
		FileInputStream fstream;
		BufferedReader br;
		try {
			f = new File(RealShopping.mandir + "realshopping.properties");
			if(!f.exists()){
				f.createNewFile();
				PrintWriter pW = new PrintWriter(f);
				pW.println("Properties file for RealShopping v0.31");
				pW.println("language-pack:default");
				pW.println("punishment:none");
				pW.println("keep-stolen-items-after-punish:false");
				pW.println("jail-location:world;0,0,0");
				pW.println("hell-location:world;0,0,0");
				pW.println("drop-items-at:world;0,0,0");
				pW.println("player-stores-create-cost:0.0");
				pW.println("enable-shopping-carts-in-worlds:world");
				pW.println("enable-selling-to-stores:true");
				pW.close();
			} else {
				fstream = new FileInputStream(f);
				br = new BufferedReader(new InputStreamReader(fstream));
				String s;
				boolean v31plus = false;
				int notInConfig = 2047;
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
					} else if(s.equals("Properties file for RealShopping v0.31")){
						notInConfig -= 512;
						v31plus = true;
					}
				}
				fstream.close();
				br.close();
				if(notInConfig > 0){
    				PrintWriter pW = new PrintWriter(f);
    				pW.println("Properties file for RealShopping v0.31");
    				pW.println("language-pack:"+langpack);
    				pW.println("punishment:"+punishment);
    				pW.println("keep-stolen-items-after-punish:"+keepstolen);
    				pW.println("jail-location:"+jailLoc.getWorld().getName()+";"+jailLoc.getBlockX()+","+jailLoc.getBlockY()+","+jailLoc.getBlockZ());
    				pW.println("hell-location:"+hellLoc.getWorld().getName()+";"+hellLoc.getBlockX()+","+hellLoc.getBlockY()+","+hellLoc.getBlockZ());
    				pW.println("drop-items-at:"+dropLoc.getWorld().getName()+";"+dropLoc.getBlockX()+","+dropLoc.getBlockY()+","+dropLoc.getBlockZ());
    				pW.println("player-stores-create-cost:"+pstorecreate);
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
    				pW.println("enable-selling-to-stores:"+enableSelling);
    				pW.close();
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
	
}
