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

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LangPack {
	
	public static String UNIT;
	public static String THISCOMMANDCANNOTBEUSEDFROMCONSOLE;
	public static String REALSHOPPINGRELOADED;
	public static String YOUENTERED;
	public static String YOURENOTATTHEENTRANCEOFASTORE;
	public static String THEREARENOSTORESSET;
	public static String YOULEFT;
	public static String YOURENOTATTHEEXITOFASTORE;
	public static String YOUHAVENTPAIDFORALLYOURARTICLES;
	public static String YOURENOTINSIDEASTORE;
	public static String YOUCANTAFFORDTOBUYTHINGSFOR;
	public static String YOUBOUGHTSTUFFFOR;
	public static String THEREARENOPRICESSETFORTHISSTORE;
	public static String YOURARTICLESCOST;
	public static String TRYINGTOCHEATYOURWAYOUT;
	public static String HAVEFUNINHELL;
	public static String ENTRANCEVARIABLESETTO;
	public static String EXITVARIABLESETTO;
	public static String WASCREATED;
	public static String WASREMOVED;
	public static String WASNTFOUND;
	public static String YOUARENOTTHEOWNEROFTHISSTORE;
	public static String THERESNOENTRANCESET;
	public static String THERSNOEXITSET;
	public static String YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE;
	public static String THEPAGENUMBERMUSTBE1ORHIGHER;
	public static String YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT;
	public static String ISNOTAVALIDPAGENUMBER;
	public static String YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS;
	public static String YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE;
	public static String PRICEFOR;
	public static String SETTO;
	public static String ISNOTAPROPER_FOLLOWEDBYTHEPRICE_;
	public static String REMOVEDPRICEFOR;
	public static String COULDNTFINDPRICEFOR;
	public static String DOESNTEXIST;
	public static String ISNOTAPROPER_;
	public static String YOUARENTPERMITTEDTOEMANAGETHISSTORE;
	public static String CHESTCREATED;
	public static String ACHESTALREADYEXISTSONTHISLOCATION;
	public static String CHESTREMOVED;
	public static String COULDNTFINDCHESTONTHISLOCATION;
	public static String ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS;
	public static String ADDED;
	public static String REMOVED;
	public static String ITEMS;
	public static String THISCHESTDOESNTEXIST;
	public static String ONEORMOREOFTHEITEMIDSWERENOTINTEGERS;
	public static String YOUHAVETOBEINASTORETOUSETHISCOMMAND;
	public static String MOREITEMSONPAGE;
	public static String THEREARENTTHATMANYPAGES;
	public static String TRIEDTOSTEALFROMTHESTORE;
	public static String WASTELEPORTEDTOHELL;
	public static String CREATINGASTORECOSTS;
	public static String YOUWEREJAILED;
	public static String YOUARENOLONGERINJAIL;
	public static String UNJAILED;
	public static String ISNOTJAILED;
	public static String ISNOTONLINE;
	public static String WASJAILED;
	
	public static void initialize(String lang){
		THISCOMMANDCANNOTBEUSEDFROMCONSOLE = "This command cannot be used from console";
		REALSHOPPINGRELOADED = "RealShopping reloaded.";
		YOUENTERED = "You entered ";
		YOULEFT = "You left ";
		YOURENOTATTHEENTRANCEOFASTORE = "You're not at the entrance of a store.";
		YOURENOTATTHEEXITOFASTORE = "You're not at the exit of a store.";
		THEREARENOSTORESSET = "There are no stores set.";
		YOUHAVENTPAIDFORALLYOURARTICLES = "You haven't paid for all your articles.";
		YOURENOTINSIDEASTORE = "You're not inside a store.";
		YOUCANTAFFORDTOBUYTHINGSFOR = "You can't afford to buy things for: ";
		YOUBOUGHTSTUFFFOR = "You bought stuff for: ";
		THEREARENOPRICESSETFORTHISSTORE = "There are no prices set for this store.";
		YOURARTICLESCOST = "Your articles cost: ";
		TRYINGTOCHEATYOURWAYOUT = "Trying to cheat your way out??";
		HAVEFUNINHELL = "Have fun in hell!";
		ENTRANCEVARIABLESETTO = "Entrance variable set to: ";
		EXITVARIABLESETTO = "Exit variable set to: ";
		WASCREATED = " was created.";
		WASREMOVED = " was removed.";
		WASNTFOUND = " wasn't found.";
		YOUARENOTTHEOWNEROFTHISSTORE = "You are not the owner of this store.";
		THERESNOENTRANCESET = "There's no entrance set.";
		THERSNOEXITSET = "There's no exit set.";
		YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE = "You have to use the 'store' argument when executing this command from console.";
		YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT = "You have to be in a store if not using the 'store' argument.";
		THEPAGENUMBERMUSTBE1ORHIGHER = "The page number must be 1 or higher.";
		ISNOTAVALIDPAGENUMBER = " is not a valid page number.";
		YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS = "You have to be in a store to use this command with no arguments.";
		YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE = "You have to use all three arguments when executing this command from console.";
		PRICEFOR = "Price for ";
		SETTO = " set to: ";
		ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ = " is not a proper argument, use a numeric ID of the item followed by a colon and the price. Example: '/rssetprices add 57:1000' to set the price for DIAMOND_BLOCK to 1000";
		ISNOTAPROPER_ = " is not a proper argument, use a numeric ID of the item instead. Example: '/rssetprices del 57' to remove the price for DIAMOND_BLOCK";
		REMOVEDPRICEFOR = "Removed price for: ";
		COULDNTFINDPRICEFOR = "Couldn't find price for: ";
		YOUARENTPERMITTEDTOEMANAGETHISSTORE = "You aren't permitted to manage this store.";
		DOESNTEXIST = " doesn't exist.";
		CHESTCREATED = "Chest created.";
		CHESTREMOVED = "Chest removed.";
		ACHESTALREADYEXISTSONTHISLOCATION = "A chest already exists on this location.";
		COULDNTFINDCHESTONTHISLOCATION = "Couldn't find chest on this location.";
		ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS = "Only admin stores can have self-refilling stores.";
		ADDED = "Added ";
		REMOVED = "Removed ";
		ITEMS = " items.";
		THISCHESTDOESNTEXIST = "This chest doensn't exist.";
		ONEORMOREOFTHEITEMIDSWERENOTINTEGERS = "One or more of the item IDs were not integers.";
		YOUHAVETOBEINASTORETOUSETHISCOMMAND = "You have to be in a store to use this command.";
		MOREITEMSONPAGE = "More items on page ";
		THEREARENTTHATMANYPAGES = "There aren't that many pages.";
		TRIEDTOSTEALFROMTHESTORE = " tried to steal from the store.";
		WASTELEPORTEDTOHELL = " was teleported to hell.";
		CREATINGASTORECOSTS = "Creating a store costs ";
		YOUWEREJAILED = "You were sent to jail. Now wait for someone to unjail you.";
		WASJAILED = " was jailed.";
		YOUARENOLONGERINJAIL = "You are no longer in jail.";
		UNJAILED = "Unjailed ";
		ISNOTJAILED = " is not in jail.";
		ISNOTONLINE = " is not online.";
		UNIT = "$";
		
		File f = new File(RealShopping.mandir + "langpacks/" + lang + ".xml");
		if(f.exists()) try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(f);

			Element docEle = dom.getDocumentElement();
			NodeList nl = docEle.getElementsByTagName("*");
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {
					Element el = (Element)nl.item(i);
					String name = el.getTagName();
					if(name.equals("UNIT")) UNIT = el.getAttribute("value");
					if(name.equals("THISCOMMANDCANNOTBEUSEDFROMCONSOLE")) THISCOMMANDCANNOTBEUSEDFROMCONSOLE = el.getAttribute("value");
					if(name.equals("REALSHOPPINGRELOADED")) REALSHOPPINGRELOADED = el.getAttribute("value");
					if(name.equals("YOUENTERED")) YOUENTERED = el.getAttribute("value");
					if(name.equals("YOULEFT")) YOULEFT = el.getAttribute("value");
					if(name.equals("YOURENOTATTHEENTRANCEOFASTORE")) YOURENOTATTHEENTRANCEOFASTORE = el.getAttribute("value");
					if(name.equals("YOURENOTATTHEEXITOFASTORE")) YOURENOTATTHEEXITOFASTORE = el.getAttribute("value");
					if(name.equals("THEREARENOSTORESSET")) THEREARENOSTORESSET = el.getAttribute("value");
					if(name.equals("YOUHAVENTPAIDFORALLYOURARTICLES")) YOUHAVENTPAIDFORALLYOURARTICLES = el.getAttribute("value");
					if(name.equals("YOURENOTINSIDEASTORE")) YOURENOTINSIDEASTORE = el.getAttribute("value");
					if(name.equals("YOUCANTAFFORDTOBUYTHINGSFOR")) YOUCANTAFFORDTOBUYTHINGSFOR = el.getAttribute("value");
					if(name.equals("YOUBOUGHTSTUFFFOR")) YOUBOUGHTSTUFFFOR = el.getAttribute("value");
					if(name.equals("THEREARENOPRICESSETFORTHISSTORE")) THEREARENOPRICESSETFORTHISSTORE = el.getAttribute("value");
					if(name.equals("YOURARTICLESCOST")) YOURARTICLESCOST = el.getAttribute("value");
					if(name.equals("TRYINGTOCHEATYOURWAYOUT")) TRYINGTOCHEATYOURWAYOUT = el.getAttribute("value");
					if(name.equals("HAVEFUNINHELL")) HAVEFUNINHELL = el.getAttribute("value");
					if(name.equals("ENTRANCEVARIABLESETTO")) ENTRANCEVARIABLESETTO = el.getAttribute("value");
					if(name.equals("EXITVARIABLESETTO")) EXITVARIABLESETTO = el.getAttribute("value");
					if(name.equals("WASCREATED")) WASCREATED = el.getAttribute("value");
					if(name.equals("WASREMOVED")) WASREMOVED = el.getAttribute("value");
					if(name.equals("WASNTFOUND")) WASNTFOUND = el.getAttribute("value");
					if(name.equals("YOUARENOTTHEOWNEROFTHISSTORE")) YOUARENOTTHEOWNEROFTHISSTORE = el.getAttribute("value");
					if(name.equals("THERESNOENTRANCESET")) THERESNOENTRANCESET = el.getAttribute("value");
					if(name.equals("THERSNOEXITSET")) THERSNOEXITSET = el.getAttribute("value");
					if(name.equals("YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE")) YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE = el.getAttribute("value");
					if(name.equals("YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT")) YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT = el.getAttribute("value");
					if(name.equals("THEPAGENUMBERMUSTBE1ORHIGHER")) THEPAGENUMBERMUSTBE1ORHIGHER = el.getAttribute("value");
					if(name.equals("ISNOTAVALIDPAGENUMBER")) ISNOTAVALIDPAGENUMBER = el.getAttribute("value");
					if(name.equals("YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS")) YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS = el.getAttribute("value");
					if(name.equals("YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE")) YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE = el.getAttribute("value");
					if(name.equals("PRICEFOR")) PRICEFOR = el.getAttribute("value");
					if(name.equals("SETTO")) SETTO = el.getAttribute("value");
					if(name.equals("ISNOTAPROPER_FOLLOWEDBYTHEPRICE_")) ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ = el.getAttribute("value");
					if(name.equals("ISNOTAPROPER_")) ISNOTAPROPER_ = el.getAttribute("value");
					if(name.equals("REMOVEDPRICEFOR")) REMOVEDPRICEFOR = el.getAttribute("value");
					if(name.equals("COULDNTFINDPRICEFOR")) COULDNTFINDPRICEFOR = el.getAttribute("value");
					if(name.equals("YOUARENTPERMITTEDTOEMANAGETHISSTORE")) YOUARENTPERMITTEDTOEMANAGETHISSTORE = el.getAttribute("value");
					if(name.equals("DOESNTEXIST")) DOESNTEXIST = el.getAttribute("value");
					if(name.equals("CHESTCREATED")) CHESTCREATED = el.getAttribute("value");
					if(name.equals("CHESTREMOVED")) CHESTREMOVED = el.getAttribute("value");
					if(name.equals("ACHESTALREADYEXISTSONTHISLOCATION")) ACHESTALREADYEXISTSONTHISLOCATION = el.getAttribute("value");
					if(name.equals("COULDNTFINDCHESTONTHISLOCATION")) COULDNTFINDCHESTONTHISLOCATION = el.getAttribute("value");
					if(name.equals("ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS")) ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS = el.getAttribute("value");
					if(name.equals("ADDED")) ADDED = el.getAttribute("value");
					if(name.equals("REMOVED")) REMOVED = el.getAttribute("value");
					if(name.equals("ITEMS")) ITEMS = el.getAttribute("value");
					if(name.equals("THISCHESTDOESNTEXIST")) THISCHESTDOESNTEXIST = el.getAttribute("value");
					if(name.equals("ONEORMOREOFTHEITEMIDSWERENOTINTEGERS")) ONEORMOREOFTHEITEMIDSWERENOTINTEGERS = el.getAttribute("value");
					if(name.equals("YOUHAVETOBEINASTORETOUSETHISCOMMAND")) YOUHAVETOBEINASTORETOUSETHISCOMMAND = el.getAttribute("value");
					if(name.equals("MOREITEMSONPAGE")) MOREITEMSONPAGE = el.getAttribute("value");
					if(name.equals("THEREARENTTHATMANYPAGES")) THEREARENTTHATMANYPAGES = el.getAttribute("value");
					if(name.equals("TRIEDTOSTEALFROMTHESTORE")) TRIEDTOSTEALFROMTHESTORE = el.getAttribute("value");
					if(name.equals("WASTELEPORTEDTOHELL")) WASTELEPORTEDTOHELL = el.getAttribute("value");
					if(name.equals("CREATINGASTORECOSTS")) CREATINGASTORECOSTS = el.getAttribute("value");
					if(name.equals("YOUWEREJAILED")) YOUWEREJAILED = el.getAttribute("value");
					if(name.equals("WASJAILED")) WASJAILED = el.getAttribute("value");
					if(name.equals("YOUARENOLONGERINJAIL")) YOUARENOLONGERINJAIL  = el.getAttribute("value");
					if(name.equals("UNJAILED")) UNJAILED = el.getAttribute("value");
					if(name.equals("ISNOTJAILED")) ISNOTJAILED = el.getAttribute("value");
					if(name.equals("ISNOTONLINE")) ISNOTONLINE = el.getAttribute("value");
				}
				RealShopping.log.info("Loaded " + lang + " language pack.");
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RealShopping.log.info("Failed while loading " + lang + " language pack.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RealShopping.log.info("Failed while loading " + lang + " language pack.");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RealShopping.log.info("Failed while loading " + lang + " language pack.");
		} catch (Exception e) {
			e.printStackTrace();
			RealShopping.log.info("Failed while loading " + lang + " language pack.");
		}
		else RealShopping.log.info("Loaded default language pack.");
	}
}
