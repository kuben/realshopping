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

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.ChatColor;
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
	public static String YOUAREBANNEDFROM;
	public static String YOUDONTOWNTHEITEMSYOUWANTTOSELL;
	public static String ADDEDTOSELLLIST;
	public static String THISSTOREDOESNTBUY;
	public static String THISSTOREDOESNTBUYANYITEMS;
	public static String CANCELLEDSELLINGITEMS;
	public static String YOUDONTHAVEALLTHEITEMSYOUWANTEDTOSELL;
	public static String SOLD;
	public static String ITEMSFOR;
	public static String YOUCANTSHIPANEMPTYCART;
	public static String PACKAGEWAITINGTOBEDELIVERED;
	public static String YOUHAVENTBOUGHTANYTHING;
	public static String THERESNOPACKAGEWITHTHEID;
	public static String YOUHAVENTGOTANYITEMSWAITINGTOBEDELIVERED;
	public static String THEBLOCKYOUARESTANDINGONISNTACHEST;
	public static String THEREISA;
	public static String PCNTOFFSALEAT;
	public static String ONSALE;
	public static String STORE;
	public static String OWNEDBY;
	public static String BUYSFOR;
	public static String PCNTOFORIGINAL;
	public static String NOTBUYINGFROMPLAYERS;
	public static String YOUCANTUSEAVALUEBELLOW0;
	public static String YOUCANTUSEAVALUEOF0ORLESS;
	public static String YOUCANTUSEAVALUEOVER100;
	public static String YOUCANTUSEAVALUEOF100ORMORE;
	public static String NOITEMSARESOLDINTHESTORE;
	public static String SALEENDED;
	public static String HASA;
	public static String PCNTOFFSALERIGHTNOW;
	public static String PCNTOFF;
	public static String ISALREADYBANNEDFROMYOURSTORE;
	public static String ISNOLONGERBANNEDFROMYOURSTORE;
	public static String WASNTBANNEDFROMYOURSTORE;
	public static String BANNED;
	public static String FROMSTORE;
	public static String YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE;
	public static String NOTHINGTOCOLLECT;
	public static String FILLEDCHESTWITH;
	public static String DROPPED;
	public static String YOUHAVEPACKAGESWITHIDS_;
	public static String YOUDONTHAVEANYPACKAGESTOPICKUP;
	public static String TOPICKUP;
	public static String YOUHAVETOSPECIFYTHEID_;
	public static String THECONTENTSOFTHEPACKAGEARE;
	public static String WHITELISTMODEALREADYSET;
	public static String BLACKLISTMODEALREADYSET;
	public static String SETWHITELISTMODE;
	public static String SETBLACKLISTMODE;
	public static String REMOVEDONEOFTHE;
	public static String TELEPORTLOCATIONS;
	public static String THEREISNO;
	public static String TELEPORTLOCATIONWITHITSCENTERHERE;
	public static String OLDRADIUSVALUE;
	public static String REPLACEDWITH;
	public static String TELEPORTLOCATIONWITHARADIUSOF;
	public static String ISNOTANINTEGER;
	public static String ISNOTAVALIDARGUMENT;
	public static String YOUARENTALLOWEDTOTELEPORTTHERE;
	public static String SELLINGTOSTORESISNOTENABLEDONTHISSERVER;
	public static String ISNOTINYOURSTORE;
	public static String WASKICKEDFROMYOURSTORE;
	public static String PLAYER;
	public static String ISNTONLINEKICK;
	public static String PLAYERSINSTORE;
	public static String STORENOTEMPTY;
	public static String PACKAGESENT;
	public static String FROM;
	public static String INWORLD;
	public static String YOUCANTUSETHATITEMINSTORE;
	public static String THISCHESTISPROTECTED;
	
	/* New strings from this point on 
	
	"Sell to store"
	"Enabled notifications for "
	"Disabled notifications for "
	"Usage:"
	"Notifications are "
	"on"
	"off"
	"(Skip the %-sign)"
	"You won't get notified when your store "
	" becomes more or less popular."
	"You will get notified when your store "
	" becomes at least "
	" places more or less popular."
	" where TRESHOLD is how many places your store needs to lose or gain for you to be notified."
	"And the prices will be lowered or increased by "
	" where TRESHOLD is how many places your store needs to lose or gain for you to be for the changes to happen, "
	"and PERCENT is how many percent the prices will be lowered or increased."
	"Automatic store management is not enabled on this server."
	"Set minimal and maximal prices for "
	"Old prices replaced with prices from "
	"Old prices replaced with the lowest price of every item in every store."
	"Cleared all prices for "
	"Store "?
	" has a minimal price of "
	" and a maximal price of "
	" for "?
	" doesn't have a minimal and maximal price for "
	"Cleared minimal and maximal prices for "
	" didn't have a minimal and maximal price for "
	"Set minimal and maximal prices for "
	" is not a proper argument."
	"You can't name a store that."
	"Highlighted 5 locations for 5 seconds."
	"No locations to highlight."
	"This chest is already protected."
	"Made chest protected."
	"Unprotected chest."
	"This chest isn't protected."
	"Reading description..."
	"This is the newest version."
	"Successful update!"
	"Update failed."
	"You aren't permitted to use this command."
	"You can't collect your items to a chest in a store you do not own."
	"You can't collect your items to a chest on this server."
	"the last hour"
	" yesterday"
	"last week"
	"last month"
	"Your store "?
	" is now the "
	" since "
	") provider of "
	"Raised the price for "
	"Lowered the price for "
	" by "
	" to "
	" went from being the  "
	" th provider of "
	" to not selling any."
	"Shipped Package sent "
	" from "
	" in world "
	" with "
	"You cannot drop items while in a store."
	"You cannot empty buckets while in a store."
	"You cannot craft items while in a store."
	"Shopping carts are not enabled in this world."
	"Shipping is not enabled on this server."
	"You cannot open Ender Chests while in a store."
	". Restart the server to load the new version."
	" of RealShopping is available for download. Update for new features and/or bugfixes with the rsupdate command."
	" of RealShopping is available for download. Update for new features and/or bugfixes. You can get information about the new version with 'rsupdate info'"
	"RealShopping initialized"
	"RealShopping disabled"
	" bought stuff for "
	" from your store "(from + your store?)
	"Owner "
	" can't afford to buy items from you for "
	" withdrawn from your account."
	"Filled chest with: "
	"You can't afford to pay the delivery fee of "
	"You can't collect your items to a chest in a store you do not own."
	" uses left"
	" owned by "
	" Prices: "
	"Use rsstores with only the name of the store as argument to get some information about the store. For help, type any of these arguments: "
	remove dots..
	". Sets if and for how much of the sell price your store will buy items from players. 0 is default and means selling to your store is disabled."
	". Collects items that have been stolen from (and then returned to) or sold to your store. If using the -c flag the items will spawn in a chast which you are standing on. You can limit the number of items returned by writing an number."
	". Banishes a player from your store forever. "
	". Unbanishes a previously banned player. "
	". Kicks a player out of your store. You can use the -o flag to kick an offline player but do ONLY use it if you're about to remove the store, as the player won't be teleported out."
    ". Starts a sale on the all or given items. Also cancels the last sale. Write items in this format: "
    " and separate multiple items with commas. The percent argument can be any integer between 1 and 99"
    ". Ends all sales."
    ". Sets if notifications are enabled or disabled for this store. Use without arguments to check current status."
    ". Sets if this store should notify you about changes in how well this store sells compared to others."
    							

	*/
	
	public static void initialize(String lang){
		THISCOMMANDCANNOTBEUSEDFROMCONSOLE = "This command cannot be used from console";
		REALSHOPPINGRELOADED = "RealShopping reloaded.";
		YOUCANTUSETHATITEMINSTORE = "You can't use that item in store.";
		THISCHESTISPROTECTED = "This chest is protected. You have to be inside a store to open it.";
		YOUENTERED = "You entered ";
		YOULEFT = "You left ";
		YOUAREBANNEDFROM = "You are banned from ";
		YOURENOTATTHEENTRANCEOFASTORE = "You're not at the entrance of a store.";
		YOURENOTATTHEEXITOFASTORE = "You're not at the exit of a store.";
		THEREARENOSTORESSET = "There are no stores set.";
		YOUHAVENTPAIDFORALLYOURARTICLES = "You haven't paid for all your articles.";
		YOURENOTINSIDEASTORE = "You're not inside a store.";
		YOUCANTAFFORDTOBUYTHINGSFOR = "You can't afford to buy things for: ";
		YOUBOUGHTSTUFFFOR = "You bought stuff for: ";
		THEREARENOPRICESSETFORTHISSTORE = "There are no prices set for this store.";
		YOURARTICLESCOST = "Your articles cost: ";
		YOUDONTOWNTHEITEMSYOUWANTTOSELL = "You don't own the item(s) you want to sell.";
		ADDEDTOSELLLIST = " added to to-sell list. Confirm sale by right-clicking the same block with no items. Cancel by left-clicking.";
		THISSTOREDOESNTBUY = "This store doesn't buy ";
		THISSTOREDOESNTBUYANYITEMS = "This store doesn't buy any items.";
		CANCELLEDSELLINGITEMS = "Cancelled selling items.";
		YOUDONTHAVEALLTHEITEMSYOUWANTEDTOSELL = "You don't have all the items you were going to sell. Cancelled sale.";
		SOLD = "Sold ";
		ITEMSFOR = " items for ";
		YOUCANTSHIPANEMPTYCART = "You can't ship an empty cart.";
		PACKAGEWAITINGTOBEDELIVERED = "Package waiting to be delivered. Use /rsshipped to pick up the package. You may need to pay a recieving fee.";
		YOUHAVENTBOUGHTANYTHING = "You haven't bought anything.";
		THERESNOPACKAGEWITHTHEID = "There's no package with the id ";
		YOUHAVENTGOTANYITEMSWAITINGTOBEDELIVERED = "You haven't got any items waiting to be delivered.";
		THEBLOCKYOUARESTANDINGONISNTACHEST = "The block you are standing on isn't a chest.";
		TRYINGTOCHEATYOURWAYOUT = "Trying to cheat your way out??";
		HAVEFUNINHELL = "Have fun in hell!";
		ENTRANCEVARIABLESETTO = "Entrance variable set to: ";
		EXITVARIABLESETTO = "Exit variable set to: ";
		WASCREATED = " was created.";
		WASREMOVED = " was removed.";
		WASNTFOUND = " wasn't found.";
		STORENOTEMPTY = "The store is not empty. You can kick players with /rsstores STORE kick PLAYER";
		YOUARENOTTHEOWNEROFTHISSTORE = "You are not the owner of this store.";
		THERESNOENTRANCESET = "There's no entrance set.";
		THERSNOEXITSET = "There's no exit set.";
		YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE = "You have to use the 'store' argument when executing this command from console.";
		YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT = "You have to be in a store if not using the 'store' argument.";
		THEPAGENUMBERMUSTBE1ORHIGHER = "The page number must be 1 or higher.";
		ISNOTAVALIDPAGENUMBER = " is not a valid page number.";
		YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS = "You have to be in a store to use this command with no arguments.";
		YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE = "You have to use all three arguments when executing this command from console.";
		THEREISA = "There is a ";
		PCNTOFFSALEAT = "% off sale at ";
		ONSALE = " ON SALE!";
		PRICEFOR = "Price for ";
		SETTO = " set to: ";
		ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ = " is not a proper argument, use a numeric ID of the item followed by a colon and the price. Example: '/rssetprices add 57:1000' to set the price for DIAMOND_BLOCK to 1000";
		ISNOTAPROPER_ = " is not a proper argument, use a numeric ID of the item instead. Example: '/rssetprices del 57' to remove the price for DIAMOND_BLOCK";
		REMOVEDPRICEFOR = "Removed price for: ";
		COULDNTFINDPRICEFOR = "Couldn't find price for: ";
		STORE = "Store ";
		OWNEDBY = " owned by ";
		BUYSFOR = "Buys for ";
		PLAYERSINSTORE = "Players in store:";
		PCNTOFORIGINAL = "% of original price.";
		NOTBUYINGFROMPLAYERS = "Not buying from players.";
		YOUCANTUSEAVALUEBELLOW0 = "You can't use a value bellow 0.";
		YOUCANTUSEAVALUEOF0ORLESS = "You can't use a value of 0 or less.";
		YOUCANTUSEAVALUEOVER100 = "You can't use a value over 100.";
		YOUCANTUSEAVALUEOF100ORMORE = "You can't use a value of 100 or more.";
		NOITEMSARESOLDINTHESTORE = "No items are sold in the store.";
		SALEENDED = "Sale ended.";
		HASA = "Has a ";
		PCNTOFFSALERIGHTNOW = "% off sale right now!";
		PCNTOFF = "% off ";
		ISALREADYBANNEDFROMYOURSTORE = " is already banned from your store.";
		ISNOLONGERBANNEDFROMYOURSTORE = " is no longer banned from your store.";
		WASNTBANNEDFROMYOURSTORE = " wasn't banned from your store.";
		BANNED = "Banned ";
		FROMSTORE = " from store.";
		ISNOTINYOURSTORE = " is not in your store.";
		WASKICKEDFROMYOURSTORE = " was kicked from your store.";
		PLAYER = "Player ";
		ISNTONLINEKICK = " isn't online. You can kick an offline player by adding the -o flag, BUT THEY WON'T BE TELEPORTED OUT OF THE STORE. Only use this if you're about to delete the store or if you know what you're doing.";
		YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE = "You don't have permission to manage that store.";
		NOTHINGTOCOLLECT = "Nothing to collect.";
		FILLEDCHESTWITH = "Filled chest with ";
		DROPPED = "Dropped ";
		YOUHAVEPACKAGESWITHIDS_ = "You have packages with ids from 1 to ";
		YOUDONTHAVEANYPACKAGESTOPICKUP = "You don't have any packages to pick up.";
		TOPICKUP = " to pick up.";
		YOUHAVETOSPECIFYTHEID_ = "You have to specify the id of the package you want to inspect.";
		PACKAGESENT = "Package sent ";
		FROM = " from ";
		INWORLD = " in world ";
		THECONTENTSOFTHEPACKAGEARE = "The contents of the package are: ";
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
		WHITELISTMODEALREADYSET = "Whitelist mode already set.";
		BLACKLISTMODEALREADYSET = "Blacklist mode already set.";
		SETWHITELISTMODE = "Set whitelist mode. Now listing ALLOWED teleport locations.";
		SETBLACKLISTMODE = "Set blacklist mode. Now listing FORBIDDEN teleport locations.";
		REMOVEDONEOFTHE = "Removed one of the ";
		TELEPORTLOCATIONS = " teleport locations.";
		THEREISNO = "There is no ";
		TELEPORTLOCATIONWITHITSCENTERHERE = " teleport location with its center here.";
		OLDRADIUSVALUE = "Old radius value ";
		REPLACEDWITH = " replaced with ";
		TELEPORTLOCATIONWITHARADIUSOF = " teleport location with a radius of ";
		TRIEDTOSTEALFROMTHESTORE = " tried to steal from the store.";
		WASTELEPORTEDTOHELL = " was teleported to hell.";
		CREATINGASTORECOSTS = "Creating a store costs ";
		YOUWEREJAILED = "You were sent to jail. Now wait for someone to unjail you.";
		WASJAILED = " was jailed.";
		YOUARENOLONGERINJAIL = "You are no longer in jail.";
		UNJAILED = "Unjailed ";
		ISNOTJAILED = " is not in jail.";
		ISNOTONLINE = " is not online.";
		ISNOTANINTEGER = " is not an integer.";
		ISNOTAVALIDARGUMENT = " is not a valid argument.";
		YOUARENTALLOWEDTOTELEPORTTHERE = "You aren't allowed to teleport there!";
		SELLINGTOSTORESISNOTENABLEDONTHISSERVER = "Selling to stores in not enabled on this server.";
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
					else if(name.equals("THISCOMMANDCANNOTBEUSEDFROMCONSOLE")) THISCOMMANDCANNOTBEUSEDFROMCONSOLE = el.getAttribute("value");
					else if(name.equals("REALSHOPPINGRELOADED")) REALSHOPPINGRELOADED = el.getAttribute("value");
					else if(name.equals("YOUENTERED")) YOUENTERED = el.getAttribute("value");
					else if(name.equals("YOULEFT")) YOULEFT = el.getAttribute("value");
					else if(name.equals("YOURENOTATTHEENTRANCEOFASTORE")) YOURENOTATTHEENTRANCEOFASTORE = el.getAttribute("value");
					else if(name.equals("YOURENOTATTHEEXITOFASTORE")) YOURENOTATTHEEXITOFASTORE = el.getAttribute("value");
					else if(name.equals("THEREARENOSTORESSET")) THEREARENOSTORESSET = el.getAttribute("value");
					else if(name.equals("YOUHAVENTPAIDFORALLYOURARTICLES")) YOUHAVENTPAIDFORALLYOURARTICLES = el.getAttribute("value");
					else if(name.equals("YOURENOTINSIDEASTORE")) YOURENOTINSIDEASTORE = el.getAttribute("value");
					else if(name.equals("YOUCANTAFFORDTOBUYTHINGSFOR")) YOUCANTAFFORDTOBUYTHINGSFOR = el.getAttribute("value");
					else if(name.equals("YOUBOUGHTSTUFFFOR")) YOUBOUGHTSTUFFFOR = el.getAttribute("value");
					else if(name.equals("THEREARENOPRICESSETFORTHISSTORE")) THEREARENOPRICESSETFORTHISSTORE = el.getAttribute("value");
					else if(name.equals("YOURARTICLESCOST")) YOURARTICLESCOST = el.getAttribute("value");
					else if(name.equals("TRYINGTOCHEATYOURWAYOUT")) TRYINGTOCHEATYOURWAYOUT = el.getAttribute("value");
					else if(name.equals("HAVEFUNINHELL")) HAVEFUNINHELL = el.getAttribute("value");
					else if(name.equals("ENTRANCEVARIABLESETTO")) ENTRANCEVARIABLESETTO = el.getAttribute("value");
					else if(name.equals("EXITVARIABLESETTO")) EXITVARIABLESETTO = el.getAttribute("value");
					else if(name.equals("WASCREATED")) WASCREATED = el.getAttribute("value");
					else if(name.equals("WASREMOVED")) WASREMOVED = el.getAttribute("value");
					else if(name.equals("WASNTFOUND")) WASNTFOUND = el.getAttribute("value");
					else if(name.equals("YOUARENOTTHEOWNEROFTHISSTORE")) YOUARENOTTHEOWNEROFTHISSTORE = el.getAttribute("value");
					else if(name.equals("THERESNOENTRANCESET")) THERESNOENTRANCESET = el.getAttribute("value");
					else if(name.equals("THERSNOEXITSET")) THERSNOEXITSET = el.getAttribute("value");
					else if(name.equals("YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE")) YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE = el.getAttribute("value");
					else if(name.equals("YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT")) YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT = el.getAttribute("value");
					else if(name.equals("THEPAGENUMBERMUSTBE1ORHIGHER")) THEPAGENUMBERMUSTBE1ORHIGHER = el.getAttribute("value");
					else if(name.equals("ISNOTAVALIDPAGENUMBER")) ISNOTAVALIDPAGENUMBER = el.getAttribute("value");
					else if(name.equals("YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS")) YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS = el.getAttribute("value");
					else if(name.equals("YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE")) YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE = el.getAttribute("value");
					else if(name.equals("PRICEFOR")) PRICEFOR = el.getAttribute("value");
					else if(name.equals("SETTO")) SETTO = el.getAttribute("value");
					else if(name.equals("ISNOTAPROPER_FOLLOWEDBYTHEPRICE_")) ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ = el.getAttribute("value");
					else if(name.equals("ISNOTAPROPER_")) ISNOTAPROPER_ = el.getAttribute("value");
					else if(name.equals("REMOVEDPRICEFOR")) REMOVEDPRICEFOR = el.getAttribute("value");
					else if(name.equals("COULDNTFINDPRICEFOR")) COULDNTFINDPRICEFOR = el.getAttribute("value");
					else if(name.equals("YOUARENTPERMITTEDTOEMANAGETHISSTORE")) YOUARENTPERMITTEDTOEMANAGETHISSTORE = el.getAttribute("value");
					else if(name.equals("DOESNTEXIST")) DOESNTEXIST = el.getAttribute("value");
					else if(name.equals("CHESTCREATED")) CHESTCREATED = el.getAttribute("value");
					else if(name.equals("CHESTREMOVED")) CHESTREMOVED = el.getAttribute("value");
					else if(name.equals("ACHESTALREADYEXISTSONTHISLOCATION")) ACHESTALREADYEXISTSONTHISLOCATION = el.getAttribute("value");
					else if(name.equals("COULDNTFINDCHESTONTHISLOCATION")) COULDNTFINDCHESTONTHISLOCATION = el.getAttribute("value");
					else if(name.equals("ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS")) ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS = el.getAttribute("value");
					else if(name.equals("ADDED")) ADDED = el.getAttribute("value");
					else if(name.equals("REMOVED")) REMOVED = el.getAttribute("value");
					else if(name.equals("ITEMS")) ITEMS = el.getAttribute("value");
					else if(name.equals("THISCHESTDOESNTEXIST")) THISCHESTDOESNTEXIST = el.getAttribute("value");
					else if(name.equals("ONEORMOREOFTHEITEMIDSWERENOTINTEGERS")) ONEORMOREOFTHEITEMIDSWERENOTINTEGERS = el.getAttribute("value");
					else if(name.equals("YOUHAVETOBEINASTORETOUSETHISCOMMAND")) YOUHAVETOBEINASTORETOUSETHISCOMMAND = el.getAttribute("value");
					else if(name.equals("MOREITEMSONPAGE")) MOREITEMSONPAGE = el.getAttribute("value");
					else if(name.equals("THEREARENTTHATMANYPAGES")) THEREARENTTHATMANYPAGES = el.getAttribute("value");
					else if(name.equals("TRIEDTOSTEALFROMTHESTORE")) TRIEDTOSTEALFROMTHESTORE = el.getAttribute("value");
					else if(name.equals("WASTELEPORTEDTOHELL")) WASTELEPORTEDTOHELL = el.getAttribute("value");
					else if(name.equals("CREATINGASTORECOSTS")) CREATINGASTORECOSTS = el.getAttribute("value");
					else if(name.equals("YOUWEREJAILED")) YOUWEREJAILED = el.getAttribute("value");
					else if(name.equals("WASJAILED")) WASJAILED = el.getAttribute("value");
					else if(name.equals("YOUARENOLONGERINJAIL")) YOUARENOLONGERINJAIL  = el.getAttribute("value");
					else if(name.equals("UNJAILED")) UNJAILED = el.getAttribute("value");
					else if(name.equals("ISNOTJAILED")) ISNOTJAILED = el.getAttribute("value");
					else if(name.equals("ISNOTONLINE")) ISNOTONLINE = el.getAttribute("value");
					else if(name.equals("YOUAREBANNEDFROM")) YOUAREBANNEDFROM = el.getAttribute("value");
					else if(name.equals("YOUDONTOWNTHEITEMSYOUWANTTOSELL")) YOUDONTOWNTHEITEMSYOUWANTTOSELL = el.getAttribute("value");
					else if(name.equals("ADDEDTOSELLLIST")) ADDEDTOSELLLIST = el.getAttribute("value");
					else if(name.equals("THISSTOREDOESNTBUY")) THISSTOREDOESNTBUY = el.getAttribute("value");
					else if(name.equals("THISSTOREDOESNTBUYANYITEMS")) THISSTOREDOESNTBUYANYITEMS = el.getAttribute("value");
					else if(name.equals("CANCELLEDSELLINGITEMS")) CANCELLEDSELLINGITEMS = el.getAttribute("value");
					else if(name.equals("YOUDONTHAVEALLTHEITEMSYOUWANTEDTOSELL")) YOUDONTHAVEALLTHEITEMSYOUWANTEDTOSELL = el.getAttribute("value");
					else if(name.equals("SOLD")) SOLD = el.getAttribute("value");
					else if(name.equals("ITEMSFOR")) ITEMSFOR = el.getAttribute("value");
					else if(name.equals("YOUCANTSHIPANEMPTYCART")) YOUCANTSHIPANEMPTYCART = el.getAttribute("value");
					else if(name.equals("PACKAGEWAITINGTOBEDELIVERED")) PACKAGEWAITINGTOBEDELIVERED = el.getAttribute("value");
					else if(name.equals("YOUHAVENTBOUGHTANYTHING")) YOUHAVENTBOUGHTANYTHING = el.getAttribute("value");
					else if(name.equals("THERESNOPACKAGEWITHTHEID")) THERESNOPACKAGEWITHTHEID = el.getAttribute("value");
					else if(name.equals("YOUHAVENTGOTANYITEMSWAITINGTOBEDELIVERED")) YOUHAVENTGOTANYITEMSWAITINGTOBEDELIVERED = el.getAttribute("value");
					else if(name.equals("THEBLOCKYOUARESTANDINGONISNTACHEST")) THEBLOCKYOUARESTANDINGONISNTACHEST = el.getAttribute("value");
					else if(name.equals("THEREISA")) THEREISA = el.getAttribute("value");
					else if(name.equals("PCNTOFFSALEAT")) PCNTOFFSALEAT = el.getAttribute("value");
					else if(name.equals("ONSALE")) ONSALE = el.getAttribute("value");
					else if(name.equals("STORE")) STORE = el.getAttribute("value");
					else if(name.equals("OWNEDBY")) OWNEDBY = el.getAttribute("value");
					else if(name.equals("BUYSFOR")) BUYSFOR = el.getAttribute("value");
					else if(name.equals("PCNTOFORIGINAL")) PCNTOFORIGINAL = el.getAttribute("value");
					else if(name.equals("NOTBUYINGFROMPLAYERS")) NOTBUYINGFROMPLAYERS = el.getAttribute("value");
					else if(name.equals("YOUCANTUSEAVALUEBELLOW0")) YOUCANTUSEAVALUEBELLOW0 = el.getAttribute("value");
					else if(name.equals("YOUCANTUSEAVALUEOF0ORLESS")) YOUCANTUSEAVALUEOF0ORLESS = el.getAttribute("value");
					else if(name.equals("YOUCANTUSEAVALUEOVER100")) YOUCANTUSEAVALUEOVER100 = el.getAttribute("value");
					else if(name.equals("YOUCANTUSEAVALUEOF100ORMORE")) YOUCANTUSEAVALUEOF100ORMORE = el.getAttribute("value");
					else if(name.equals("NOITEMSARESOLDINTHESTORE")) NOITEMSARESOLDINTHESTORE = el.getAttribute("value");
					else if(name.equals("SALEENDED")) SALEENDED = el.getAttribute("value");
					else if(name.equals("HASA")) HASA = el.getAttribute("value");
					else if(name.equals("PCNTOFFSALERIGHTNOW")) PCNTOFFSALERIGHTNOW = el.getAttribute("value");
					else if(name.equals("PCNTOFF")) PCNTOFF = el.getAttribute("value");
					else if(name.equals("ISALREADYBANNEDFROMYOURSTORE")) ISALREADYBANNEDFROMYOURSTORE = el.getAttribute("value");
					else if(name.equals("ISNOLONGERBANNEDFROMYOURSTORE")) ISNOLONGERBANNEDFROMYOURSTORE = el.getAttribute("value");
					else if(name.equals("WASNTBANNEDFROMYOURSTORE")) WASNTBANNEDFROMYOURSTORE = el.getAttribute("value");
					else if(name.equals("BANNED")) BANNED = el.getAttribute("value");
					else if(name.equals("FROMSTORE")) FROMSTORE = el.getAttribute("value");
					else if(name.equals("YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE")) YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE = el.getAttribute("value");
					else if(name.equals("NOTHINGTOCOLLECT")) NOTHINGTOCOLLECT = el.getAttribute("value");
					else if(name.equals("FILLEDCHESTWITH")) FILLEDCHESTWITH = el.getAttribute("value");
					else if(name.equals("DROPPED")) DROPPED = el.getAttribute("value");
					else if(name.equals("YOUHAVEPACKAGESWITHIDS_")) YOUHAVEPACKAGESWITHIDS_ = el.getAttribute("value");
					else if(name.equals("YOUDONTHAVEANYPACKAGESTOPICKUP")) YOUDONTHAVEANYPACKAGESTOPICKUP = el.getAttribute("value");
					else if(name.equals("TOPICKUP")) TOPICKUP = el.getAttribute("value");
					else if(name.equals("YOUHAVETOSPECIFYTHEID_")) YOUHAVETOSPECIFYTHEID_ = el.getAttribute("value");
					else if(name.equals("THECONTENTSOFTHEPACKAGEARE")) THECONTENTSOFTHEPACKAGEARE = el.getAttribute("value");
					else if(name.equals("WHITELISTMODEALREADYSET")) WHITELISTMODEALREADYSET = el.getAttribute("value");
					else if(name.equals("BLACKLISTMODEALREADYSET")) BLACKLISTMODEALREADYSET = el.getAttribute("value");
					else if(name.equals("SETWHITELISTMODE")) SETWHITELISTMODE = el.getAttribute("value");
					else if(name.equals("SETBLACKLISTMODE")) SETBLACKLISTMODE = el.getAttribute("value");
					else if(name.equals("REMOVEDONEOFTHE")) REMOVEDONEOFTHE = el.getAttribute("value");
					else if(name.equals("TELEPORTLOCATIONS")) TELEPORTLOCATIONS = el.getAttribute("value");
					else if(name.equals("THEREISNO")) THEREISNO = el.getAttribute("value");
					else if(name.equals("TELEPORTLOCATIONWITHITSCENTERHERE")) TELEPORTLOCATIONWITHITSCENTERHERE = el.getAttribute("value");
					else if(name.equals("OLDRADIUSVALUE")) OLDRADIUSVALUE = el.getAttribute("value");
					else if(name.equals("REPLACEDWITH")) REPLACEDWITH = el.getAttribute("value");
					else if(name.equals("TELEPORTLOCATIONWITHARADIUSOF")) TELEPORTLOCATIONWITHARADIUSOF = el.getAttribute("value");
					else if(name.equals("ISNOTANINTEGER")) ISNOTANINTEGER = el.getAttribute("value");
					else if(name.equals("ISNOTAVALIDARGUMENT")) ISNOTAVALIDARGUMENT = el.getAttribute("value");
					else if(name.equals("YOUARENTALLOWEDTOTELEPORTTHERE")) YOUARENTALLOWEDTOTELEPORTTHERE = el.getAttribute("value");
					else if(name.equals("SELLINGTOSTORESISNOTENABLEDONTHISSERVER")) SELLINGTOSTORESISNOTENABLEDONTHISSERVER = el.getAttribute("value");
					else if(name.equals("ISNOTINYOURSTORE")) ISNOTINYOURSTORE = el.getAttribute("value");
					else if(name.equals("WASKICKEDFROMYOURSTORE")) WASKICKEDFROMYOURSTORE = el.getAttribute("value");
					else if(name.equals("PLAYER")) PLAYER = el.getAttribute("value");
					else if(name.equals("ISNTONLINEKICK")) ISNTONLINEKICK = el.getAttribute("value");
					else if(name.equals("PLAYERSINSTORE")) PLAYERSINSTORE = el.getAttribute("value");
					else if(name.equals("STORENOTEMPTY")) STORENOTEMPTY = el.getAttribute("value");
					else if(name.equals("PACKAGESENT")) PACKAGESENT = el.getAttribute("value");
					else if(name.equals("FROM")) FROM = el.getAttribute("value");
					else if(name.equals("INWORLD")) INWORLD = el.getAttribute("value");
					else if(name.equals("YOUCANTUSETHATITEMINSTORE")) YOUCANTUSETHATITEMINSTORE = el.getAttribute("value");
					else if(name.equals("THISCHESTISPROTECTED")) THISCHESTISPROTECTED = el.getAttribute("value");
					
				}
				RealShopping.log.info("Loaded " + lang + " language pack.");
			}
		} catch (SAXException e) {
			e.printStackTrace();
			RealShopping.log.info("Failed while loading " + lang + " language pack.");
		} catch (IOException e) {
			e.printStackTrace();
			RealShopping.log.info("Failed while loading " + lang + " language pack.");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			RealShopping.log.info("Failed while loading " + lang + " language pack.");
		} catch (Exception e) {
			e.printStackTrace();
			RealShopping.log.info("Failed while loading " + lang + " language pack.");
		}
		else RealShopping.log.info("Loaded default language pack.");
	}
}
