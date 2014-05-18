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
	public static String THEBLOCKYOUSELECTEDISNTACHEST;
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
	//v0.43
	public static String SELLTOSTORE;
	public static String ENABLEDNOTIFICATIONSFOR;
	public static String DISABLEDNOTIFICATIONSFOR;
	public static String USAGE;
	public static String NOTIFICATIONSARE;
	public static String ON;
	public static String OFF;
	public static String SKIPPSIGN;
	public static String YOUWONTGETNOTIFIEDWHENYOURSTORE;
	public static String BECOMESMOREORLESSPOPULAR;
	public static String YOUWILLGETNOTIFIEDWHENYOURSTORE;
	public static String BECOMESATLEAST;
	public static String PLACESMOREORLESSPOPULAR;
	public static String WHERETRESHOLDIS_;
	public static String ANDTHEPRICESWILLBE_;
	public static String WHERETRESHOLDIS_CHANGES_;
	public static String ANDPERCENTIS_;
	public static String AI_ISNOTENABLED_;
	public static String SETMINIMALANDMAXIMALPRICESFOR;
	public static String OLDPRICESREPLACEDWITHPRICESFROM;
	public static String OLDPRICESREPLACEDWITHTHELOWEST_;
	public static String CLEAREDALLPRICESFOR;
	public static String HASAMINIMALPRICEOF;
	public static String ANDAMAXIMALPRICEOF;
	public static String FOR;
	public static String DOESNTHAVEAMINIMALANDMAXIMALPRICEFOR;
	public static String CLEAREDMINIMALANDMAXIMALPRICESFOR;
	public static String DIDNTHAVEAMINIMALANDMAXIMALPRICEFOR;
	public static String ISNOTAPROPERARGUMENT;
	public static String YOUCANTNAMEASTORETHAT;
	public static String HIGHLIGHTED5LOCATIONSFOR5SECONDS;
	public static String NOLOCATIONSTOHIGHLIGHT;
	public static String THISCHESTISALREADYPROTECTED;
	public static String MADECHESTPROTECTED;
	public static String UNPROTECTEDCHEST;
	public static String THISCHESTISNTPROTECTED;
	public static String READINGDESCRIPTION;
	public static String THISISTHENEWESTVERSION;
	public static String SUCCESSFULUPDATE;
	public static String UPDATEFAILED;
	public static String YOUARENTPERMITTEDTOUSETHISCOMMAND;
	public static String YOUCANTCOLLECT_YOUDONOTOWN;
	public static String YOUCANTCOLLECT_SERVER;
	public static String THELASTHOUR;
	public static String YESTERDAY;
	public static String LASTWEEK;
	public static String LASTMONTH;
	public static String YOURSTORE;
	public static String ISNOWTHE;
	public static String SINCE;
	public static String PROVIDEROF;
	public static String RAISEDTHEPRICEFOR;
	public static String LOWEREDTHEPRICEFOR;
	public static String BY;
	public static String TO;
	public static String WENTFROMBEINGTHE;
	public static String TONOTSELLINGANY;
	public static String SHIPPEDPACKAGESENT;
	public static String WITH;
	public static String YOUCANNOTDROPITEMS_;
	public static String YOUCANNOTEMPTYBUCKETS_;
	public static String YOUCANNOTCRAFTITEMS_;
	public static String SHOPPINGCARTSARENOTENABLED_;
	public static String SHIPPINGISNOTENABLED_;
	public static String YOUCANNOTOPENENDERCHESTS_;
	public static String RESTARTTHESERVER_VERSION;
	public static String OFRE_UPDATECOMMAND;
	public static String OFRE_UPDATEINFO;
	public static String REALSHOPPINGINITIALIZED;
	public static String REALSHOPPINGDISABLED;
	public static String BOUGHTSTUFFFOR;
	public static String FROMYOURSTORE;
	public static String OWNER;
	public static String CANTAFFORDTOBUYITEMSFROMYOUFOR;
	public static String WITHDRAWNFROMYOURACCOUNT;
	public static String YOUCANTAFFORDTOPAYTHEDELIVERYFEEOF;
	public static String USESLEFT;
	public static String PRICES;
	public static String RSSTORESHELP;
	public static String BUYFORHELP;
	public static String COLLECTHELP;
	public static String BANHELP;
	public static String UNBANHELP;
	public static String KICKHELP;
	public static String STARTSALEHELP;
	public static String STARTSALEHELP2;
	public static String ENDSALEHELP;
	public static String NOTIFICATIONSHELP;
	public static String ONCHANGEHELP;
	public static String SETDEFAULTPRICESFOR;
	public static String THEREARENODEFAULTPRICES;
	public static String QUITCONVERSATION;
	public static String WHICHFILEDOYOUWANTTOIMPORT_;
	public static String ERROR_NO_XLSX_;
	public static String INTHEMAINDIRECTORY;
	public static String INTHEREALSHOPPINGDIRECTORY;
	public static String TYPETHECORRESPONDINGNUMBER_;
	public static String TOCANCEL;
	public static String ERROR_INPUTISNOTAVALIDINTEGER;
	public static String CHOSENFILE;
	public static String TYPE;
	public static String TOIMPORT_USERDEFINED_;
	public static String TOIMPORT_PROPOSITION_;
	public static String WRONGFILECHOSEN;
	public static String IMPORTED;
	public static String PRICESASDEFAULT;
	public static String ERRORCOULDNTIMPORTPRICES;
	public static String DONE;
	public static String REALSHOPPINGUPDATEDTO;
	public static String X_ST;
	public static String X_ND;
	public static String X_RD;
	public static String X_TH;
	//v0.44
	public static String YOUWILLBENOTIFIEDIF;
	public static String LOSESGAINS;
	public static String PLACES;
	public static String THEPRICEWILLBELOWEREDINCREASED_;
	public static String PCNTIF;
	public static String WONTNOTIFY_;
	//v0.51
	public static String NO_HELP_DOCUMENTATION_;
	public static String FORHELPTYPE;
	public static String BLOCK_ADDED_TO_SELECTION;
	public static String BLOCK_ALREADY_SELECTED;
	public static String BLOCK_REMOVED_FROM_SELECTION;
	public static String BLOCK_WASNT_SELECTED;
	public static String CHANGES_UNDONE;
	public static String PLEASE_SELECT_EXIT_;
	public static String PLEASE_SELECT_THE_EXIT_LINKED_TO_ENTRANCE_;
	public static String PLEASE_SELECT_ENTRANCE_;
	public static String TO_EXITS_LIST;
	public static String IS_NOT_AN_EXIT_TO_THE_LAST_ENTRANCE;
	public static String TO_ENTRANCES_LIST;
	public static String IS_NOT_AN_ENTRANCE_TO_;
	public static String ABANDONING_CONVERSATIONS;
	public static String CONVERSATIONS_ABANDONED;
	public static String QUIT_CONVERSATION_FOR_UNKNOWN_REASON;
	public static String IS_NOT_IN_A_STORE;
	public static String IS_NOT_ALLOWED_TO_MANAGE_STORE_;
	public static String YOU_CANT_TELEPORT_WHILE_IN_A_CONVERSATION;
	public static String YOU_CANT_DO_THIS_WHILE_IN_A_CONVERSATION;
	public static String ALL_CONVERSATIONS_CAN_BE_ABORTED_WITH_;
	public static String YOU_HAVE_TO_SEARCH_FOR_A_SPECIFIC_ITEM;
	public static String NO_MATCHES_FOR_;
	public static String RSPRICESHELP;
	public static String RSPRICESHELP2;
	public static String RSPRICESHELP3;
	public static String RSPROTECTHELP;
	public static String RSPROTECTHELP2;
	public static String RSPROTECTHELP3;
	public static String FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_;
	public static String YOU_CAN_GET_MORE_HELP_ABOUT_;
	public static String STARTS_AN_INTERACTIVE_PROMPT;
	public static String RSSETHELP;
	public static String RSSETSTORESHELP;
	public static String RSSETENTRANCEHELP;
	public static String RSSETEXITHELP;
	public static String RSSETCREATEHELP;
	public static String RSSETDELSTOREHELP;
	public static String RSSETDELENHELP;
	public static String RSSETCHESTSHELP;
	public static String RSSETCHESTSCREATEHELP;
	public static String RSSETCHESTSDELHELP;
	public static String RSSETCHESTSADDITEMSHELP;
	public static String RSSETCHESTSDELITEMSHELP;
	public static String RSSETPRICESHELP;
	public static String RSSETPRICESHELP2;
	public static String RSSETPRICESADDHELP;
	public static String RSSETPRICESADDHELP2;
	public static String AND_;
	public static String ARGUMENTS;
	public static String RSSETPRICESDELHELP;
	public static String RSSETPRICESDEFAUTLSHELP;
	public static String RSSETPRICESCOPYHELP;
	public static String RSSETPRICESCOPYHELP2;
	public static String RSSETPRICESCOPYHELP3;
	public static String RSSETPRICESCLEARHELP;
	public static String RSSETPRICESSHOWMMHELP;
	public static String RSSETPRICESCLEARMMHELP;
	public static String RSSETPRICESSETMMHELP;
	public static String RSSHIPPEDHELP;
	public static String RSSHIPPEDHELP2;
	public static String RSSHIPPEDHELP3;
	public static String RSTPLOCSHELP;
	public static String RSTPLOCSWHITEHELP;
	public static String RSTPLOCSBLACKHELP;
	public static String RSTPLOCSLISTHELP;
	public static String RSTPLOCSADDHELP;
	public static String RSTPLOCSADDHELP2;
	public static String RSTPLOCSDELHELP;
	public static String RSTPLOCSHIGHHELP;
	public static String RSUPDATEHELP;
	public static String RSUPDATEUPDATEHELP;
	public static String RSUPDATEINFOHELP;
	public static String THIS_ENTRANCE_AND_EXIT_PAIR_IS_ALREADY_USED;
	public static String YOU_CANT_USE_THIS_COMMAND_INSIDE_A_STORE;
	public static String LOADED_CONFIG_SETTINGS;
	public static String ENABLED_IN_ALL_WORLDS;
	public static String FOR_MORE;
	public static String IS_NOT_AN_ACCEPTED_ARGUMENT;
	public static String TO_CONTINUE_TYPE_ANY_OF_THE_PURPLE_KEYWORDS;
	public static String WHAT_DO_YOU_WANT_TO_DO_;
	public static String THIS_PROMPT_WILL_AID_YOU_IN__CHESTS_;
	public static String NEW_CHESTS_OR_;
	public static String OR_;
	public static String EXISTING_ONES;
	public static String YOU_HAVE_CHOSEN_TO_CREATE_NEW_CHESTS_;
	public static String YOU_HAVE_CHOSEN_TO_DELETE_EXISTING_CHESTS_;
	public static String YOU_HAVE_CHOSEN_TO_MANAGE_EXISTING_CHESTS_;
	public static String APPLY_TO_ALL_CHOSEN_CHESTS_;
	public static String TO_CLEAR_THE_SELECTION_OF_CHESTS_OR_;
	public static String TO_SELECT_ALL__EXIT_WITH_;
	public static String YOU_HAVE_CHOSEN_TO_MANAGE_EXISTING_CHESTS_FREELY_;
	public static String CHESTS_CREATED;
	public static String CHESTS_REMOVED;
	public static String ACTION_ABORTED;
	public static String ITEMS_TO_;
	public static String CHESTS;
	public static String DELETED_;
	public static String ITEMS_FROM_;
	public static String CLEARED_CONTENTS_OF_;
	public static String UPDATED_CONTENTS_OF;
	public static String SELECTED_;
	public static String CLEARED_SELECTION;
	public static String QUIT_MANAGING_CHESTS;
	public static String THIS_PROMPT_WILL_AID_YOU_IN__STORE_;
	public static String THIS_PROMPT_WILL_AID_YOU_IN__PLAYER_STORE_;
	public static String YOU_DONT_HAVE_THE_RIGHT_PERMISSIONS_TO_DO_THIS;
	public static String A_NEW_STORE_;
	public static String ENTRANCES_AND_EXITS_OR_;
	public static String AN_EXISTING_STORE;
	public static String YOU_HAVE_CHOSEN_TO_CREATE_A_NEW_STORE_;
	public static String YOU_HAVE_CHOSEN_TO_APPEND__STORE_;
	public static String YOU_HAVE_CHOSEN_TO_DELETE__STORE_;
	public static String YOU_HAVE_CHOSEN_TO_WIPE_OUT_A_STORE_;
	public static String THE_NAME_;
	public static String IS_AVAILABLE;
	public static String THAT_NAME_IS_ALREADY_TAKEN;
	public static String YOU_CANT_USE_THIS_INSIDE_A_STORE;
	public static String RIGHTCLICK_A_BLOCK_TO_SELECT_IT_AS_EE_;
	public static String WHEN_DONE_OR_;
	public static String TO_START_OVER_;
	public static String CREATED_STORE_;
	public static String EE_PAIRS;
	public static String NO_EE_SELECTED;
	public static String YOU_HAVE_CHOSEN_TO_APPEND_NEW_EE_TO_;
	public static String APPENDED_;
	public static String EE_PAIRS_TO_STORE_;
	public static String YOU_HAVE_CHOSEN_TO_DELETE_EE_FROM_;
	public static String EE_PAIRS_FROM_STORE_;
	public static String DO_YOU_REALLY_WANT_TO_DELETE_;
	public static String AND_ALL_ITS_;
	public static String BTN_CONFIRM;
        public static String BTN_CANCEL;
        public static String BTN_PAYMENTINFO;
        public static String BTN_SELLINFO;
        public static String PAYTOSTORE;
        public static String COUPONNAME;
	public static String DISCOUNTAMOUNT;
        public static String GLOBALDISCOUNT;
        public static String ITEMDISCOUNT;
        public static String TYPEDISCOUNT;
        public static String QUANTITYDISCOUNT;
        
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
		THEBLOCKYOUSELECTEDISNTACHEST = "The block you selected isn't a chest.";//TODO change xmls
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
		//v0.43
		SELLTOSTORE = "Sell to store";
		ENABLEDNOTIFICATIONSFOR = "Enabled notifications for ";
		DISABLEDNOTIFICATIONSFOR = "Disabled notifications for ";
		USAGE = "Usage: ";
		NOTIFICATIONSARE = "Notifications are ";
		ON = "on";
		OFF = "off";
		SKIPPSIGN = "(Skip the %-sign)";
		YOUWONTGETNOTIFIEDWHENYOURSTORE = "You won't get notified when your store ";
		BECOMESMOREORLESSPOPULAR = " becomes more or less popular.";
		YOUWILLGETNOTIFIEDWHENYOURSTORE = "You will get notified when your store ";
		BECOMESATLEAST = " becomes at least ";
		PLACESMOREORLESSPOPULAR = " places more or less popular.";
		WHERETRESHOLDIS_ = " where TRESHOLD is how many places your store needs to lose or gain for you to be notified.";
		ANDTHEPRICESWILLBE_ = " And the prices will be lowered or increased by ";
		WHERETRESHOLDIS_CHANGES_ = " where TRESHOLD is how many places your store needs to lose or gain for you to be for the changes to happen, ";
		ANDPERCENTIS_ = "and PERCENT is how many percent the prices will be lowered or increased.";
		AI_ISNOTENABLED_ = "Automatic store management is not enabled on this server.";
		SETMINIMALANDMAXIMALPRICESFOR = "Set minimal and maximal prices for ";
		OLDPRICESREPLACEDWITHPRICESFROM = "Old prices replaced with prices from ";
		OLDPRICESREPLACEDWITHTHELOWEST_ = "Old prices replaced with the lowest price of every item in every store.";
		CLEAREDALLPRICESFOR = "Cleared all prices for ";
		HASAMINIMALPRICEOF = " has a minimal price of ";
		ANDAMAXIMALPRICEOF = " and a maximal price of ";
		FOR = " for ";
		DOESNTHAVEAMINIMALANDMAXIMALPRICEFOR = " doesn't have a minimal and maximal price for ";
		CLEAREDMINIMALANDMAXIMALPRICESFOR = "Cleared minimal and maximal prices for ";
		DIDNTHAVEAMINIMALANDMAXIMALPRICEFOR = " didn't have a minimal and maximal price for ";
		ISNOTAPROPERARGUMENT = " is not a proper argument.";
		YOUCANTNAMEASTORETHAT = "You can't name a store that.";
		HIGHLIGHTED5LOCATIONSFOR5SECONDS = "Highlighted 5 locations for 5 seconds.";
		NOLOCATIONSTOHIGHLIGHT = "No locations to highlight.";
		THISCHESTISALREADYPROTECTED = "This chest is already protected.";
		MADECHESTPROTECTED = "Made chest protected.";
		UNPROTECTEDCHEST = "Unprotected chest.";
		THISCHESTISNTPROTECTED = "This chest isn't protected.";
		READINGDESCRIPTION = "Reading description...";
		THISISTHENEWESTVERSION = "This is the newest version.";
		SUCCESSFULUPDATE = "Successful update!";
		UPDATEFAILED = "Update failed.";
		YOUARENTPERMITTEDTOUSETHISCOMMAND = "You aren't permitted to use this command.";
		YOUCANTCOLLECT_YOUDONOTOWN = "You can't collect your items to a chest in a store you do not own.";
		YOUCANTCOLLECT_SERVER = "You can't collect your items to a chest on this server.";
		THELASTHOUR = "the last hour";
		YESTERDAY = "yesterday";
		LASTWEEK = "last week";
		LASTMONTH = "last month";
		YOURSTORE = "Your store ";
		ISNOWTHE = " is now the ";
		SINCE = " since ";
		PROVIDEROF = ") provider of ";
		RAISEDTHEPRICEFOR = "Raised the price for ";
		LOWEREDTHEPRICEFOR = "Lowered the price for ";
		BY = " by ";
		TO = " to ";
		WENTFROMBEINGTHE = " went from being the  ";
		TONOTSELLINGANY = " to not selling any.";
		SHIPPEDPACKAGESENT = "Package sent ";
		WITH = " with ";
		YOUCANNOTDROPITEMS_ = "You cannot drop items while in a store.";
		YOUCANNOTEMPTYBUCKETS_ = "You cannot empty buckets while in a store.";
		YOUCANNOTCRAFTITEMS_ = "You cannot craft items while in a store.";
		SHOPPINGCARTSARENOTENABLED_ = "Shopping carts are not enabled in this world.";
		SHIPPINGISNOTENABLED_ = "Shipping is not enabled on this server.";
		YOUCANNOTOPENENDERCHESTS_ = "You cannot open Ender Chests while in a store.";
		RESTARTTHESERVER_VERSION = ". Restart the server to load the new version.";
		OFRE_UPDATECOMMAND = " of RealShopping is available for download. Update for new features and/or bugfixes with the rsupdate command.";
		OFRE_UPDATEINFO = " of RealShopping is available for download. Update for new features and/or bugfixes. You can get information about the new version with ";
		REALSHOPPINGINITIALIZED = "RealShopping initialized";
		REALSHOPPINGDISABLED = "RealShopping disabled";
		BOUGHTSTUFFFOR = " bought stuff for ";
		FROMYOURSTORE = " from your store ";
		OWNER = "Owner ";
		CANTAFFORDTOBUYITEMSFROMYOUFOR = " can't afford to buy items from you for ";
		WITHDRAWNFROMYOURACCOUNT = " withdrawn from your account.";
		YOUCANTAFFORDTOPAYTHEDELIVERYFEEOF = "You can't afford to pay the delivery fee of ";
		USESLEFT = " uses left";
		PRICES = " Prices: ";
		RSSTORESHELP = "Use rsstores with only the name of the store as argument to get some information about the store. ";
		BUYFORHELP = ". Sets if and for how much of the sell price your store will buy items from players. 0 is default and means selling to your store is disabled.";
		COLLECTHELP = ". Collects items that have been stolen from (and then returned to) or sold to your store. If using the -c flag the items will spawn in a chast which you are standing on. You can limit the number of items returned by writing an number.";
		BANHELP = ". Banishes a player from your store forever. ";
		UNBANHELP = ". Unbanishes a previously banned player. ";
		KICKHELP = ". Kicks a player out of your store. You can use the -o flag to kick an offline player but do ONLY use it if you're about to remove the store, as the player won't be teleported out.";
		STARTSALEHELP = ". Starts a sale on the all or given items. Also cancels the last sale. Write items in this format: ";
		STARTSALEHELP2 = " and separate multiple items with commas. The percent argument can be any integer between 1 and 99";
		ENDSALEHELP = ". Ends all sales.";
		NOTIFICATIONSHELP = ". Sets if notifications are enabled or disabled for this store. Use without arguments to check current status.";
		ONCHANGEHELP = ". Sets if this store should notify you about changes in how well this store sells compared to others.";
		SETDEFAULTPRICESFOR = "Set default prices for ";
		THEREARENODEFAULTPRICES = "There are no default prices. Use /rsimport to import them, or see the plugin page for help.";
		QUITCONVERSATION = "Quit conversation.";
		WHICHFILEDOYOUWANTTOIMPORT_ = "Which file do you want to import a set of default prices from?";  
		ERROR_NO_XLSX_ = "Error: no files with the .xlsx extension found in the main directory or the RealShopping directory.";
		INTHEMAINDIRECTORY = " In the main directory:";
		INTHEREALSHOPPINGDIRECTORY = " In the RealShopping directory:";
		TYPETHECORRESPONDINGNUMBER_ = "Type the corresponding number to choose a file or ";
		TOCANCEL = " to cancel.";
		ERROR_INPUTISNOTAVALIDINTEGER = "Error: Input is not a valid integer.";
		CHOSENFILE = "Chosen file ";
		TYPE = "Type ";
		TOIMPORT_USERDEFINED_ = " to import from the user defined prices, or ";
		TOIMPORT_PROPOSITION_ = " to import from the proposition prices.";
		WRONGFILECHOSEN = "Wrong file chosen";
		IMPORTED = "Imported ";
		PRICESASDEFAULT = " prices as default.";
		ERRORCOULDNTIMPORTPRICES = "Error: Couldn't import prices.";
		DONE = "Done!";
		REALSHOPPINGUPDATEDTO = "RealShopping updated to ";
		X_ST = "st";
		X_ND = "nd";
		X_RD = "rd";
		X_TH = "th";
		//v0.44
		YOUWILLBENOTIFIEDIF = "You will be notified if ";
		LOSESGAINS = " loses/gains ";
		PLACES = " place(s).";
		THEPRICEWILLBELOWEREDINCREASED_ = "The price will be lowered/increased by ";
		PCNTIF = "% if ";
		WONTNOTIFY_ = " won't notify you about changes.";
		//v0.51
		NO_HELP_DOCUMENTATION_ = "No help documentation for this command.";
		BLOCK_ADDED_TO_SELECTION = "Block added to selection.";
		BLOCK_ALREADY_SELECTED = "Block already selected.";
		BLOCK_REMOVED_FROM_SELECTION = "Block removed from selection.";
		BLOCK_WASNT_SELECTED = "Block wasn't selected.";
		CHANGES_UNDONE = "Changes undone.";
		PLEASE_SELECT_EXIT_ = "Please select exit ";
		PLEASE_SELECT_THE_EXIT_LINKED_TO_ENTRANCE_ = "Please select the exit linked to entrance ";
		PLEASE_SELECT_ENTRANCE_ = "Please select entrance ";
		TO_EXITS_LIST = " to exits list.";
		IS_NOT_AN_EXIT_TO_THE_LAST_ENTRANCE = " is not an exit to the last entrance.";
		TO_ENTRANCES_LIST = " to entrances list.";
		IS_NOT_AN_ENTRANCE_TO_ = " is not an entrance to ";
		ABANDONING_CONVERSATIONS = "Abandoning conversations..";
		CONVERSATIONS_ABANDONED = "Conversations abandoned.";
		QUIT_CONVERSATION_FOR_UNKNOWN_REASON = "Quit conversation for unknown reason.";
		IS_NOT_IN_A_STORE = " is not in a store.";
		IS_NOT_ALLOWED_TO_MANAGE_STORE_ = " is not allowed to manage store ";
		YOU_CANT_TELEPORT_WHILE_IN_A_CONVERSATION = "You can't teleport while in a conversation.";
		YOU_CANT_DO_THIS_WHILE_IN_A_CONVERSATION = "You can't do this while in a conversation. ";
		ALL_CONVERSATIONS_CAN_BE_ABORTED_WITH_ = "All conversations can be aborted with ";
		YOU_HAVE_TO_SEARCH_FOR_A_SPECIFIC_ITEM = "You have to search for a specific item.";
		NO_MATCHES_FOR_ = "No matches for ";
		RSPRICESHELP = "Displays a list of prices in a store, or searches for the given price. The ";
		RSPRICESHELP2 = " argument is optional when inside a store, and ";
		RSPRICESHELP3 = " is only needed when not displaying the first page.";
		RSPROTECTHELP = "Makes chests protected, so they cannot be opened from outside the store. Use when neccecary. ";
		RSPROTECTHELP2 = " makes a chest protected, and ";
		RSPROTECTHELP3 = " unmakes it.";
		FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ = "For help for a specific command, type: ";
		YOU_CAN_GET_MORE_HELP_ABOUT_ = "You can get more help about each of these arguments: ";
		STARTS_AN_INTERACTIVE_PROMPT = ". Starts an interactive prompt. ";
		RSSETHELP = "Creates or deletes admin stores, as well as entrances/exits to them. Use the prompt argument for a guide, or the other arguments to create stores manually. ";
		RSSETSTORESHELP = "Creates or deletes player owned stores, as well as entrances/exits to them. Use the prompt argument for a guide, or the other arguments to create stores manually. ";
		FORHELPTYPE = "For help, type ";
		RSSETENTRANCEHELP = ". Stores the location of the block you stand on to an entrance variable.";
		RSSETEXITHELP = ". Stores the location of the block you stand on to an exit variable.";
		RSSETCREATEHELP = ". If no store by that name exists, this command creates it with the entrance and exit set. If a store already exists then the entrance and exit pair get appended to it.";
		RSSETDELSTOREHELP = ". Wipes the named store off the face of the earth, along with settings and prices. Use with care.";
		RSSETDELENHELP = ". Deletes the entrance and exit pair which you most recently have set with entrance and exit. You can only remove matching entrances and exits.";
		RSSETCHESTSHELP = "Manages self-refilling chests (admin-stores only). Use the prompt argument for a guide, or the other arguments to manage chests manually. ";
		RSSETCHESTSCREATEHELP = ". The block you stand on becomes a self-refilling chest. It will update when someone enters the store.";
		RSSETCHESTSDELHELP = ". The block you stand on ceases to be a self-refilling chest.";
		RSSETCHESTSADDITEMSHELP = ". Adds items to the chest. Multiple items are separated with commas. Add more of the same item by multiplying it with a number. If you omit the data and/or amount field they will default to 1 and a full stack.";
		RSSETCHESTSDELITEMSHELP = ". Deletes the first items from the chest, which match the item IDs and data fields of the arguments. It will not delete more items than specified. Stack size is not taken into consideration.";
		RSSETPRICESHELP = "Sets prices for all kinds of stores. When used in the targeted store, the ";
		RSSETPRICESHELP2 = " argument is not necessary. ";
		RSSETPRICESADDHELP = ". Sets the price for the specified item to ";
		RSSETPRICESADDHELP2 = ". The other commands have more documentation on the ";
		AND_ = " and ";
		ARGUMENTS = " commands.";
		RSSETPRICESDELHELP = ". Deletes the price of an item.";
		RSSETPRICESDEFAUTLSHELP = ". Imports the default server prices to your store. Default prices are imported with ";
		RSSETPRICESCOPYHELP = ". Copies the prices from ";
		RSSETPRICESCOPYHELP2 = " to your store. If you skip the ";
		RSSETPRICESCOPYHELP3 = " argument, the lowest price for every item in every store is copied.";
		RSSETPRICESCLEARHELP = ". Clears all prices from the store.";
		RSSETPRICESSHOWMMHELP = ". Shows the currently set minimal and maximal price for the item.";
		RSSETPRICESCLEARMMHELP = ". Clears the currently set minimal and maximal price for the item. ";
		RSSETPRICESSETMMHELP = ". Sets the minimal and maximal price for the item. Data values are not currently supported. ";
		RSSHIPPEDHELP = "Manages shipped items. Use without arguments to see what packages you have to collect. Use with the ";
		RSSHIPPEDHELP2 = " argument to inspect a package, and use ";
		RSSHIPPEDHELP3 = " while standing on a chest to collect the items.";
		RSTPLOCSHELP = "Makes it possible for players to teleport to certain allowed areas from a store, if they have paid for their items. Either you can whitelist some areas and ban all the others, or blacklist some and allow all other. Only make it possible to teleport to areas where there are no stores!";
		RSTPLOCSWHITEHELP = ". Sets the mode used to whitelist (default). This means that listed areas are allowed to teleport to, and all others are banned.";
		RSTPLOCSBLACKHELP = ". Sets the mode used to blacklist. This means that listed areas are not allowed to teleport to. Other areas are allowed.";
		RSTPLOCSLISTHELP = "When changing modes, areas are not deleted. They remain in the list and just go from being banned to unbanned and vice versa. ";
		RSTPLOCSADDHELP = ". Creates a spheric area around where you are standing with the radius ";
		RSTPLOCSADDHELP2 = ". The area will be added to the white- or blacklist.";
		RSTPLOCSDELHELP = ". Deletes the area with the center on the point where you are standing. If you have trouble finding it, use the highlight option.";
		RSTPLOCSHIGHHELP = ". Changes the centers of the five nearest areas to wool blocks for five seconds and only you to see. Each wool color corresponds to a radius lenght:";
		RSUPDATEHELP = "Updates RealShopping to the latest version, if such permissions have been given you in the config. ";
		RSUPDATEUPDATEHELP = ". Updates to the newest version. You need to reload the server after this command.";
		RSUPDATEINFOHELP = ". Prints the description of the newest version of ";
		THIS_ENTRANCE_AND_EXIT_PAIR_IS_ALREADY_USED = "This entrance and exit pair is already used.";
		YOU_CANT_USE_THIS_COMMAND_INSIDE_A_STORE = "You can't use this command inside a store.";
		LOADED_CONFIG_SETTINGS = "Loaded config settings:";
		ENABLED_IN_ALL_WORLDS = "Enabled in all worlds";
		FOR_MORE = " for more.";
		IS_NOT_AN_ACCEPTED_ARGUMENT = " is not an accepted argument.";
		TO_CONTINUE_TYPE_ANY_OF_THE_PURPLE_KEYWORDS = "To continue, type any of the purple keywords.";
		WHAT_DO_YOU_WANT_TO_DO_ = "What do you want to do, ";
		THIS_PROMPT_WILL_AID_YOU_IN__CHESTS_ = "This prompt will aid you in creating and managing self-refilling chests. ";
		NEW_CHESTS_OR_ = " new chests or ";
		OR_ = " or ";
		EXISTING_ONES = " existing ones?";
		YOU_HAVE_CHOSEN_TO_CREATE_NEW_CHESTS_ = "You have chosen to create new chests. Right-click a block to make it a chest, or left-click to cancel. Chosen blocks will appear as gold blocks. ";
		YOU_HAVE_CHOSEN_TO_DELETE_EXISTING_CHESTS_ = "You have chosen to delete existing chests. Right-click a chest to remove it, or left-click to cancel. Chosen blocks will appear as iron blocks. ";
		YOU_HAVE_CHOSEN_TO_MANAGE_EXISTING_CHESTS_ = "You have chosen to manage existing chests. Left-click a chest to choose it. Commands ";
		APPLY_TO_ALL_CHOSEN_CHESTS_ = " apply to all chosen chests. ";
		TO_CLEAR_THE_SELECTION_OF_CHESTS_OR_ = " to clear the selection of chests or ";
		TO_SELECT_ALL__EXIT_WITH_ = " to select all. Exit with ";
		YOU_HAVE_CHOSEN_TO_MANAGE_EXISTING_CHESTS_FREELY_ = "You have chosen to manage existing chests freely. Open a chest, put items in, and close to update it's contents. Exit with ";
		CHESTS_CREATED = " chest(s) created.";
		CHESTS_REMOVED = " chest(s) removed.";
		ACTION_ABORTED = "Action aborted.";
		ITEMS_TO_ = " items to ";
		CHESTS = " chests.";
		DELETED_ = "Deleted ";
		ITEMS_FROM_ = " items from ";
		CLEARED_CONTENTS_OF_ = "Cleared contents of ";
		UPDATED_CONTENTS_OF = "Updated contents of ";
		SELECTED_ = "Selected ";
		CLEARED_SELECTION = "Cleared selection.";
		QUIT_MANAGING_CHESTS = "Quit managing chests.";
		THIS_PROMPT_WILL_AID_YOU_IN__STORE_ = "This prompt will aid you in creating, extending or deleting admin stores. ";
		THIS_PROMPT_WILL_AID_YOU_IN__PLAYER_STORE_ = "This prompt will aid you in creating, extending or deleting your own player store. ";
		YOU_DONT_HAVE_THE_RIGHT_PERMISSIONS_TO_DO_THIS = "You don't have the right permissions to do this.";
		A_NEW_STORE_ = " a new store ";
		ENTRANCES_AND_EXITS_OR_ = " entrances and exits or ";
		AN_EXISTING_STORE = " an existing store?";
		YOU_HAVE_CHOSEN_TO_CREATE_A_NEW_STORE_ = "You have chosen to create a new store. What do you want to name it?";
		YOU_HAVE_CHOSEN_TO_APPEND__STORE_ = "You have chosen to append entrances and exits to an existing store. Type the name of the store you want to edit.";
		YOU_HAVE_CHOSEN_TO_DELETE__STORE_ = "You have chosen to delete entrances and exits from an existing store. Type the name of the store you want to edit.";
		YOU_HAVE_CHOSEN_TO_WIPE_OUT_A_STORE_ = "You have chosen to wipe out a store. Type the name of the store you want to delete.";
		THE_NAME_ = "The name ";
		IS_AVAILABLE = " is available.";
		THAT_NAME_IS_ALREADY_TAKEN = "That name is already taken.";
		YOU_CANT_USE_THIS_INSIDE_A_STORE = "You can't use this inside a store.";
		RIGHTCLICK_A_BLOCK_TO_SELECT_IT_AS_EE_ = "Right-click a block to select it as entrance/exit, or left-click in the air to use your coordinates.";
		WHEN_DONE_OR_ = " when done, or ";
		TO_START_OVER_ = " to start over (with the entrances).";
		CREATED_STORE_ = "Created store ";
		EE_PAIRS = " entrance/exit pairs.";
		NO_EE_SELECTED = "No entrances/exits selected.";
		YOU_HAVE_CHOSEN_TO_APPEND_NEW_EE_TO_ = "You have chosen to append new entrances and exits to ";
		APPENDED_ = "Appended ";
		EE_PAIRS_TO_STORE_ = " entrance/exit pairs to store ";
		YOU_HAVE_CHOSEN_TO_DELETE_EE_FROM_ = "You have chosen to delete entrances and exits from ";
		EE_PAIRS_FROM_STORE_ = " entrance/exit pairs from store ";
		DO_YOU_REALLY_WANT_TO_DELETE_ = "Do you really want to delete ";
		AND_ALL_ITS_ = " and all its entrances, chests and prices.";
                BTN_CANCEL = "Cancel";
                BTN_CONFIRM = "Confirm";
                BTN_PAYMENTINFO = "Payment Information";
                BTN_SELLINFO = "Sell Informations";
                PAYTOSTORE = "Payment Panel";
		COUPONNAME = "Discount Coupon";
                DISCOUNTAMOUNT = "Discount";
                GLOBALDISCOUNT = "One-Time, single purchase discount";
                ITEMDISCOUNT = "One-Time, single item discount";
                TYPEDISCOUNT = "One-Time, item type discount";
                QUANTITYDISCOUNT = "One-Time, quantity driven discount";
		UNIT = "$";
		
		File f = new File(RealShopping.MANDIR + "langpacks/" + lang + ".xml");
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
                                        switch(name) {
                                            case "UNIT":
                                                UNIT = el.getAttribute("value");
                                                break;
                                            case "THISCOMMANDCANNOTBEUSEDFROMCONSOLE":
                                                THISCOMMANDCANNOTBEUSEDFROMCONSOLE = el.getAttribute("value");
                                                break;
                                            case "REALSHOPPINGRELOADED":
                                                REALSHOPPINGRELOADED = el.getAttribute("value");
                                                break;
                                            case "YOUENTERED":
                                                YOUENTERED = el.getAttribute("value");
                                                break;
                                            case "YOULEFT":
                                                YOULEFT = el.getAttribute("value");
                                                break;
                                            case "YOURENOTATTHEENTRANCEOFASTORE":
                                                YOURENOTATTHEENTRANCEOFASTORE = el.getAttribute("value");
                                                break;
                                            case "YOURENOTATTHEEXITOFASTORE":
                                                YOURENOTATTHEEXITOFASTORE = el.getAttribute("value");
                                                break;
                                            case "THEREARENOSTORESSET":
                                                THEREARENOSTORESSET = el.getAttribute("value");
                                                break;
                                            case "YOUHAVENTPAIDFORALLYOURARTICLES":
                                                YOUHAVENTPAIDFORALLYOURARTICLES = el.getAttribute("value");
                                                break;
                                            case "YOURENOTINSIDEASTORE":
                                                YOURENOTINSIDEASTORE = el.getAttribute("value");
                                                break;
                                            case "YOUCANTAFFORDTOBUYTHINGSFOR":
                                                YOUCANTAFFORDTOBUYTHINGSFOR = el.getAttribute("value");
                                                break;
                                            case "YOUBOUGHTSTUFFFOR":
                                                YOUBOUGHTSTUFFFOR = el.getAttribute("value");
                                                break;
                                            case "THEREARENOPRICESSETFORTHISSTORE":
                                                THEREARENOPRICESSETFORTHISSTORE = el.getAttribute("value");
                                                break;
                                            case "YOURARTICLESCOST":
                                                YOURARTICLESCOST = el.getAttribute("value");
                                                break;
                                            case "TRYINGTOCHEATYOURWAYOUT":
                                                TRYINGTOCHEATYOURWAYOUT = el.getAttribute("value");
                                                break;
                                            case "HAVEFUNINHELL":
                                                HAVEFUNINHELL = el.getAttribute("value");
                                                break;
                                            case "ENTRANCEVARIABLESETTO":
                                                ENTRANCEVARIABLESETTO = el.getAttribute("value");
                                                break;
                                            case "EXITVARIABLESETTO":
                                                EXITVARIABLESETTO = el.getAttribute("value");
                                                break;
                                            case "WASCREATED":
                                                WASCREATED = el.getAttribute("value");
                                                break;
                                            case "WASREMOVED":
                                                WASREMOVED = el.getAttribute("value");
                                                break;
                                            case "WASNTFOUND":
                                                WASNTFOUND = el.getAttribute("value");
                                                break;
                                            case "YOUARENOTTHEOWNEROFTHISSTORE":
                                                YOUARENOTTHEOWNEROFTHISSTORE = el.getAttribute("value");
                                                break;
                                            case "THERESNOENTRANCESET":
                                                THERESNOENTRANCESET = el.getAttribute("value");
                                                break;
                                            case "THERSNOEXITSET":
                                                THERSNOEXITSET = el.getAttribute("value");
                                                break;
                                            case "YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE":
                                                YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE = el.getAttribute("value");
                                                break;
                                            case "YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT":
                                                YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT = el.getAttribute("value");
                                                break;
                                            case "THEPAGENUMBERMUSTBE1ORHIGHER":
                                                THEPAGENUMBERMUSTBE1ORHIGHER = el.getAttribute("value");
                                                break;
                                            case "ISNOTAVALIDPAGENUMBER":
                                                ISNOTAVALIDPAGENUMBER = el.getAttribute("value");
                                                break;
                                            case "YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS":
                                                YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS = el.getAttribute("value");
                                                break;
                                            case "YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE":
                                                YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE = el.getAttribute("value");
                                                break;
                                            case "PRICEFOR":
                                                PRICEFOR = el.getAttribute("value");
                                                break;
                                            case "SETTO":
                                                SETTO = el.getAttribute("value");
                                                break;
                                            case "ISNOTAPROPER_FOLLOWEDBYTHEPRICE_":
                                                ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ = el.getAttribute("value");
                                                break;
                                            case "ISNOTAPROPER_":
                                                ISNOTAPROPER_ = el.getAttribute("value");
                                                break;
                                            case "REMOVEDPRICEFOR":
                                                REMOVEDPRICEFOR = el.getAttribute("value");
                                                break;
                                            case "COULDNTFINDPRICEFOR":
                                                COULDNTFINDPRICEFOR = el.getAttribute("value");
                                                break;
                                            case "YOUARENTPERMITTEDTOEMANAGETHISSTORE":
                                                YOUARENTPERMITTEDTOEMANAGETHISSTORE = el.getAttribute("value");
                                                break;
                                            case "DOESNTEXIST":
                                                DOESNTEXIST = el.getAttribute("value");
                                                break;
                                            case "CHESTCREATED":
                                                CHESTCREATED = el.getAttribute("value");
                                                break;
                                            case "CHESTREMOVED":
                                                CHESTREMOVED = el.getAttribute("value");
                                                break;
                                            case "ACHESTALREADYEXISTSONTHISLOCATION":
                                                ACHESTALREADYEXISTSONTHISLOCATION = el.getAttribute("value");
                                                break;
                                            case "COULDNTFINDCHESTONTHISLOCATION":
                                                COULDNTFINDCHESTONTHISLOCATION = el.getAttribute("value");
                                                break;
                                            case "ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS":
                                                ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS = el.getAttribute("value");
                                                break;
                                            case "ADDED":
                                                ADDED = el.getAttribute("value");
                                                break;
                                            case "REMOVED":
                                                REMOVED = el.getAttribute("value");
                                                break;
                                            case "ITEMS":
                                                ITEMS = el.getAttribute("value");
                                                break;
                                            case "THISCHESTDOESNTEXIST":
                                                THISCHESTDOESNTEXIST = el.getAttribute("value");
                                                break;
                                            case "ONEORMOREOFTHEITEMIDSWERENOTINTEGERS":
                                                ONEORMOREOFTHEITEMIDSWERENOTINTEGERS = el.getAttribute("value");
                                                break;
                                            case "YOUHAVETOBEINASTORETOUSETHISCOMMAND":
                                                YOUHAVETOBEINASTORETOUSETHISCOMMAND = el.getAttribute("value");
                                                break;
                                            case "MOREITEMSONPAGE":
                                                MOREITEMSONPAGE = el.getAttribute("value");
                                                break;
                                            case "THEREARENTTHATMANYPAGES":
                                                THEREARENTTHATMANYPAGES = el.getAttribute("value");
                                                break;
                                            case "TRIEDTOSTEALFROMTHESTORE":
                                                TRIEDTOSTEALFROMTHESTORE = el.getAttribute("value");
                                                break;
                                            case "WASTELEPORTEDTOHELL":
                                                WASTELEPORTEDTOHELL = el.getAttribute("value");
                                                break;
                                            case "CREATINGASTORECOSTS":
                                                CREATINGASTORECOSTS = el.getAttribute("value");
                                                break;
                                            case "YOUWEREJAILED":
                                                YOUWEREJAILED = el.getAttribute("value");
                                                break;
                                            case "WASJAILED":
                                                WASJAILED = el.getAttribute("value");
                                                break;
                                            case "YOUARENOLONGERINJAIL":
                                                YOUARENOLONGERINJAIL  = el.getAttribute("value");
                                                break;
                                            case "UNJAILED":
                                                UNJAILED = el.getAttribute("value");
                                                break;
                                            case "ISNOTJAILED":
                                                ISNOTJAILED = el.getAttribute("value");
                                                break;
                                            case "ISNOTONLINE":
                                                ISNOTONLINE = el.getAttribute("value");
                                                break;
                                            case "YOUAREBANNEDFROM":
                                                YOUAREBANNEDFROM = el.getAttribute("value");
                                                break;
                                            case "YOUDONTOWNTHEITEMSYOUWANTTOSELL":
                                                YOUDONTOWNTHEITEMSYOUWANTTOSELL = el.getAttribute("value");
                                                break;
                                            case "ADDEDTOSELLLIST":
                                                ADDEDTOSELLLIST = el.getAttribute("value");
                                                break;
                                            case "THISSTOREDOESNTBUY":
                                                THISSTOREDOESNTBUY = el.getAttribute("value");
                                                break;
                                            case "THISSTOREDOESNTBUYANYITEMS":
                                                THISSTOREDOESNTBUYANYITEMS = el.getAttribute("value");
                                                break;
                                            case "CANCELLEDSELLINGITEMS":
                                                CANCELLEDSELLINGITEMS = el.getAttribute("value");
                                                break;
                                            case "YOUDONTHAVEALLTHEITEMSYOUWANTEDTOSELL":
                                                YOUDONTHAVEALLTHEITEMSYOUWANTEDTOSELL = el.getAttribute("value");
                                                break;
                                            case "SOLD":
                                                SOLD = el.getAttribute("value");
                                                break;
                                            case "ITEMSFOR":
                                                ITEMSFOR = el.getAttribute("value");
                                                break;
                                            case "YOUCANTSHIPANEMPTYCART":
                                                YOUCANTSHIPANEMPTYCART = el.getAttribute("value");
                                                break;
                                            case "PACKAGEWAITINGTOBEDELIVERED":
                                                PACKAGEWAITINGTOBEDELIVERED = el.getAttribute("value");
                                                break;
                                            case "YOUHAVENTBOUGHTANYTHING":
                                                YOUHAVENTBOUGHTANYTHING = el.getAttribute("value");
                                                break;
                                            case "THERESNOPACKAGEWITHTHEID":
                                                THERESNOPACKAGEWITHTHEID = el.getAttribute("value");
                                                break;
                                            case "YOUHAVENTGOTANYITEMSWAITINGTOBEDELIVERED":
                                                YOUHAVENTGOTANYITEMSWAITINGTOBEDELIVERED = el.getAttribute("value");
                                                break;
                                            case "THEBLOCKYOUSELECTEDISNTACHEST":
                                                THEBLOCKYOUSELECTEDISNTACHEST = el.getAttribute("value");
                                                break;
                                            case "THEREISA":
                                                THEREISA = el.getAttribute("value");
                                                break;
                                            case "PCNTOFFSALEAT":
                                                PCNTOFFSALEAT = el.getAttribute("value");
                                                break;
                                            case "ONSALE":
                                                ONSALE = el.getAttribute("value");
                                                break;
                                            case "STORE":
                                                STORE = el.getAttribute("value");
                                                break;
                                            case "OWNEDBY":
                                                OWNEDBY = el.getAttribute("value");
                                                break;
                                            case "BUYSFOR":
                                                BUYSFOR = el.getAttribute("value");
                                                break;
                                            case "PCNTOFORIGINAL":
                                                PCNTOFORIGINAL = el.getAttribute("value");
                                                break;
                                            case "NOTBUYINGFROMPLAYERS":
                                                NOTBUYINGFROMPLAYERS = el.getAttribute("value");
                                                break;
                                            case "YOUCANTUSEAVALUEBELLOW0":
                                                YOUCANTUSEAVALUEBELLOW0 = el.getAttribute("value");
                                                break;
                                            case "YOUCANTUSEAVALUEOF0ORLESS":
                                                YOUCANTUSEAVALUEOF0ORLESS = el.getAttribute("value");
                                                break;
                                            case "YOUCANTUSEAVALUEOVER100":
                                                YOUCANTUSEAVALUEOVER100 = el.getAttribute("value");
                                                break;
                                            case "YOUCANTUSEAVALUEOF100ORMORE":
                                                YOUCANTUSEAVALUEOF100ORMORE = el.getAttribute("value");
                                                break;
                                            case "NOITEMSARESOLDINTHESTORE":
                                                NOITEMSARESOLDINTHESTORE = el.getAttribute("value");
                                                break;
                                            case "SALEENDED":
                                                SALEENDED = el.getAttribute("value");
                                                break;
                                            case "HASA":
                                                HASA = el.getAttribute("value");
                                                break;
                                            case "PCNTOFFSALERIGHTNOW":
                                                PCNTOFFSALERIGHTNOW = el.getAttribute("value");
                                                break;
                                            case "PCNTOFF":
                                                PCNTOFF = el.getAttribute("value");
                                                break;
                                            case "ISALREADYBANNEDFROMYOURSTORE":
                                                ISALREADYBANNEDFROMYOURSTORE = el.getAttribute("value");
                                                break;
                                            case "ISNOLONGERBANNEDFROMYOURSTORE":
                                                ISNOLONGERBANNEDFROMYOURSTORE = el.getAttribute("value");
                                                break;
                                            case "WASNTBANNEDFROMYOURSTORE":
                                                WASNTBANNEDFROMYOURSTORE = el.getAttribute("value");
                                                break;
                                            case "BANNED":
                                                BANNED = el.getAttribute("value");
                                                break;
                                            case "FROMSTORE":
                                                FROMSTORE = el.getAttribute("value");
                                                break;
                                            case "YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE":
                                                YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE = el.getAttribute("value");
                                                break;
                                            case "NOTHINGTOCOLLECT":
                                                NOTHINGTOCOLLECT = el.getAttribute("value");
                                                break;
                                            case "FILLEDCHESTWITH":
                                                FILLEDCHESTWITH = el.getAttribute("value");
                                                break;
                                            case "DROPPED":
                                                DROPPED = el.getAttribute("value");
                                                break;
                                            case "YOUHAVEPACKAGESWITHIDS_":
                                                YOUHAVEPACKAGESWITHIDS_ = el.getAttribute("value");
                                                break;
                                            case "YOUDONTHAVEANYPACKAGESTOPICKUP":
                                                YOUDONTHAVEANYPACKAGESTOPICKUP = el.getAttribute("value");
                                                break;
                                            case "TOPICKUP":
                                                TOPICKUP = el.getAttribute("value");
                                                break;
                                            case "YOUHAVETOSPECIFYTHEID_":
                                                YOUHAVETOSPECIFYTHEID_ = el.getAttribute("value");
                                                break;
                                            case "THECONTENTSOFTHEPACKAGEARE":
                                                THECONTENTSOFTHEPACKAGEARE = el.getAttribute("value");
                                                break;
                                            case "WHITELISTMODEALREADYSET":
                                                WHITELISTMODEALREADYSET = el.getAttribute("value");
                                                break;
                                            case "BLACKLISTMODEALREADYSET":
                                                BLACKLISTMODEALREADYSET = el.getAttribute("value");
                                                break;
                                            case "SETWHITELISTMODE":
                                                SETWHITELISTMODE = el.getAttribute("value");
                                                break;
                                            case "SETBLACKLISTMODE":
                                                SETBLACKLISTMODE = el.getAttribute("value");
                                                break;
                                            case "REMOVEDONEOFTHE":
                                                REMOVEDONEOFTHE = el.getAttribute("value");
                                                break;
                                            case "TELEPORTLOCATIONS":
                                                TELEPORTLOCATIONS = el.getAttribute("value");
                                                break;
                                            case "THEREISNO":
                                                THEREISNO = el.getAttribute("value");
                                                break;
                                            case "TELEPORTLOCATIONWITHITSCENTERHERE":
                                                TELEPORTLOCATIONWITHITSCENTERHERE = el.getAttribute("value");
                                                break;
                                            case "OLDRADIUSVALUE":
                                                OLDRADIUSVALUE = el.getAttribute("value");
                                                break;
                                            case "REPLACEDWITH":
                                                REPLACEDWITH = el.getAttribute("value");
                                                break;
                                            case "TELEPORTLOCATIONWITHARADIUSOF":
                                                TELEPORTLOCATIONWITHARADIUSOF = el.getAttribute("value");
                                                break;
                                            case "ISNOTANINTEGER":
                                                ISNOTANINTEGER = el.getAttribute("value");
                                                break;
                                            case "ISNOTAVALIDARGUMENT":
                                                ISNOTAVALIDARGUMENT = el.getAttribute("value");
                                                break;
                                            case "YOUARENTALLOWEDTOTELEPORTTHERE":
                                                YOUARENTALLOWEDTOTELEPORTTHERE = el.getAttribute("value");
                                                break;
                                            case "SELLINGTOSTORESISNOTENABLEDONTHISSERVER":
                                                SELLINGTOSTORESISNOTENABLEDONTHISSERVER = el.getAttribute("value");
                                                break;
                                            case "ISNOTINYOURSTORE":
                                                ISNOTINYOURSTORE = el.getAttribute("value");
                                                break;
                                            case "WASKICKEDFROMYOURSTORE":
                                                WASKICKEDFROMYOURSTORE = el.getAttribute("value");
                                                break;
                                            case "PLAYER":
                                                PLAYER = el.getAttribute("value");
                                                break;
                                            case "ISNTONLINEKICK":
                                                ISNTONLINEKICK = el.getAttribute("value");
                                                break;
                                            case "PLAYERSINSTORE":
                                                PLAYERSINSTORE = el.getAttribute("value");
                                                break;
                                            case "STORENOTEMPTY":
                                                STORENOTEMPTY = el.getAttribute("value");
                                                break;
                                            case "PACKAGESENT":
                                                PACKAGESENT = el.getAttribute("value");
                                                break;
                                            case "FROM":
                                                FROM = el.getAttribute("value");
                                                break;
                                            case "INWORLD":
                                                INWORLD = el.getAttribute("value");
                                                break;
                                            case "YOUCANTUSETHATITEMINSTORE":
                                                YOUCANTUSETHATITEMINSTORE = el.getAttribute("value");
                                                break;
                                            case "THISCHESTISPROTECTED":
                                                THISCHESTISPROTECTED = el.getAttribute("value");
                                                break;
                                            case "SELLTOSTORE":
                                                SELLTOSTORE = el.getAttribute("value");
                                                break;
                                            case "ENABLEDNOTIFICATIONSFOR":
                                                ENABLEDNOTIFICATIONSFOR = el.getAttribute("value");
                                                break;
                                            case "DISABLEDNOTIFICATIONSFOR":
                                                DISABLEDNOTIFICATIONSFOR = el.getAttribute("value");
                                                break;
                                            case "USAGE":
                                                USAGE = el.getAttribute("value");
                                                break;
                                            case "NOTIFICATIONSARE":
                                                NOTIFICATIONSARE = el.getAttribute("value");
                                                break;
                                            case "ON":
                                                ON = el.getAttribute("value");
                                                break;
                                            case "OFF":
                                                OFF = el.getAttribute("value");
                                                break;
                                            case "SKIPPSIGN":
                                                SKIPPSIGN = el.getAttribute("value");
                                                break;
                                            case "YOUWONTGETNOTIFIEDWHENYOURSTORE":
                                                YOUWONTGETNOTIFIEDWHENYOURSTORE = el.getAttribute("value");
                                                break;
                                            case "BECOMESMOREORLESSPOPULAR":
                                                BECOMESMOREORLESSPOPULAR = el.getAttribute("value");
                                                break;
                                            case "YOUWILLGETNOTIFIEDWHENYOURSTORE":
                                                YOUWILLGETNOTIFIEDWHENYOURSTORE = el.getAttribute("value");
                                                break;
                                            case "BECOMESATLEAST":
                                                BECOMESATLEAST = el.getAttribute("value");
                                                break;
                                            case "PLACESMOREORLESSPOPULAR":
                                                PLACESMOREORLESSPOPULAR = el.getAttribute("value");
                                                break;
                                            case "WHERETRESHOLDIS_":
                                                WHERETRESHOLDIS_ = el.getAttribute("value");
                                                break;
                                            case "ANDTHEPRICESWILLBE_":
                                                ANDTHEPRICESWILLBE_ = el.getAttribute("value");
                                                break;
                                            case "WHERETRESHOLDIS_CHANGES_":
                                                WHERETRESHOLDIS_CHANGES_ = el.getAttribute("value");
                                                break;
                                            case "ANDPERCENTIS_":
                                                ANDPERCENTIS_ = el.getAttribute("value");
                                                break;
                                            case "AI_ISNOTENABLED_":
                                                AI_ISNOTENABLED_ = el.getAttribute("value");
                                                break;
                                            case "SETMINIMALANDMAXIMALPRICESFOR":
                                                SETMINIMALANDMAXIMALPRICESFOR = el.getAttribute("value");
                                                break;
                                            case "OLDPRICESREPLACEDWITHPRICESFROM":
                                                OLDPRICESREPLACEDWITHPRICESFROM = el.getAttribute("value");
                                                break;
                                            case "OLDPRICESREPLACEDWITHTHELOWEST_":
                                                OLDPRICESREPLACEDWITHTHELOWEST_ = el.getAttribute("value");
                                                break;
                                            case "CLEAREDALLPRICESFOR":
                                                CLEAREDALLPRICESFOR = el.getAttribute("value");
                                                break;
                                            case "HASAMINIMALPRICEOF":
                                                HASAMINIMALPRICEOF = el.getAttribute("value");
                                                break;
                                            case "ANDAMAXIMALPRICEOF":
                                                ANDAMAXIMALPRICEOF = el.getAttribute("value");
                                                break;
                                            case "FOR":
                                                FOR = el.getAttribute("value");
                                                break;
                                            case "DOESNTHAVEAMINIMALANDMAXIMALPRICEFOR":
                                                DOESNTHAVEAMINIMALANDMAXIMALPRICEFOR = el.getAttribute("value");
                                                break;
                                            case "CLEAREDMINIMALANDMAXIMALPRICESFOR":
                                                CLEAREDMINIMALANDMAXIMALPRICESFOR = el.getAttribute("value");
                                                break;
                                            case "DIDNTHAVEAMINIMALANDMAXIMALPRICEFOR":
                                                DIDNTHAVEAMINIMALANDMAXIMALPRICEFOR = el.getAttribute("value");
                                                break;
                                            case "ISNOTAPROPERARGUMENT":
                                                ISNOTAPROPERARGUMENT = el.getAttribute("value");
                                                break;
                                            case "YOUCANTNAMEASTORETHAT":
                                                YOUCANTNAMEASTORETHAT = el.getAttribute("value");
                                                break;
                                            case "HIGHLIGHTED5LOCATIONSFOR5SECONDS":
                                                HIGHLIGHTED5LOCATIONSFOR5SECONDS = el.getAttribute("value");
                                                break;
                                            case "NOLOCATIONSTOHIGHLIGHT":
                                                NOLOCATIONSTOHIGHLIGHT = el.getAttribute("value");
                                                break;
                                            case "THISCHESTISALREADYPROTECTED":
                                                THISCHESTISALREADYPROTECTED = el.getAttribute("value");
                                                break;
                                            case "MADECHESTPROTECTED":
                                                MADECHESTPROTECTED = el.getAttribute("value");
                                                break;
                                            case "UNPROTECTEDCHEST":
                                                UNPROTECTEDCHEST = el.getAttribute("value");
                                                break;
                                            case "THISCHESTISNTPROTECTED":
                                                THISCHESTISNTPROTECTED = el.getAttribute("value");
                                                break;
                                            case "READINGDESCRIPTION":
                                                READINGDESCRIPTION = el.getAttribute("value");
                                                break;
                                            case "THISISTHENEWESTVERSION":
                                                THISISTHENEWESTVERSION = el.getAttribute("value");
                                                break;
                                            case "SUCCESSFULUPDATE":
                                                SUCCESSFULUPDATE = el.getAttribute("value");
                                                break;
                                            case "UPDATEFAILED":
                                                UPDATEFAILED = el.getAttribute("value");
                                                break;
                                            case "YOUARENTPERMITTEDTOUSETHISCOMMAND":
                                                YOUARENTPERMITTEDTOUSETHISCOMMAND = el.getAttribute("value");
                                                break;
                                            case "YOUCANTCOLLECT_YOUDONOTOWN":
                                                YOUCANTCOLLECT_YOUDONOTOWN = el.getAttribute("value");
                                                break;
                                            case "YOUCANTCOLLECT_SERVER":
                                                YOUCANTCOLLECT_SERVER = el.getAttribute("value");
                                                break;
                                            case "THELASTHOUR":
                                                THELASTHOUR = el.getAttribute("value");
                                                break;
                                            case "YESTERDAY":
                                                YESTERDAY = el.getAttribute("value");
                                                break;
                                            case "LASTWEEK":
                                                LASTWEEK = el.getAttribute("value");
                                                break;
                                            case "LASTMONTH":
                                                LASTMONTH = el.getAttribute("value");
                                                break;
                                            case "YOURSTORE":
                                                YOURSTORE = el.getAttribute("value");
                                                break;
                                            case "ISNOWTHE":
                                                ISNOWTHE = el.getAttribute("value");
                                                break;
                                            case "SINCE":
                                                SINCE = el.getAttribute("value");
                                                break;
                                            case "PROVIDEROF":
                                                PROVIDEROF = el.getAttribute("value");
                                                break;
                                            case "RAISEDTHEPRICEFOR":
                                                RAISEDTHEPRICEFOR = el.getAttribute("value");
                                                break;
                                            case "LOWEREDTHEPRICEFOR":
                                                LOWEREDTHEPRICEFOR = el.getAttribute("value");
                                                break;
                                            case "BY":
                                                BY = el.getAttribute("value");
                                                break;
                                            case "TO":
                                                TO = el.getAttribute("value");
                                                break;
                                            case "WENTFROMBEINGTHE":
                                                WENTFROMBEINGTHE = el.getAttribute("value");
                                                break;
                                            case "TONOTSELLINGANY":
                                                TONOTSELLINGANY = el.getAttribute("value");
                                                break;
                                            case "SHIPPEDPACKAGESENT":
                                                SHIPPEDPACKAGESENT = el.getAttribute("value");
                                                break;
                                            case "WITH":
                                                WITH = el.getAttribute("value");
                                                break;
                                            case "YOUCANNOTDROPITEMS_":
                                                YOUCANNOTDROPITEMS_ = el.getAttribute("value");
                                                break;
                                            case "YOUCANNOTEMPTYBUCKETS_":
                                                YOUCANNOTEMPTYBUCKETS_ = el.getAttribute("value");
                                                break;
                                            case "YOUCANNOTCRAFTITEMS_":
                                                YOUCANNOTCRAFTITEMS_ = el.getAttribute("value");
                                                break;
                                            case "SHOPPINGCARTSARENOTENABLED_":
                                                SHOPPINGCARTSARENOTENABLED_ = el.getAttribute("value");
                                                break;
                                            case "SHIPPINGISNOTENABLED_":
                                                SHIPPINGISNOTENABLED_ = el.getAttribute("value");
                                                break;
                                            case "YOUCANNOTOPENENDERCHESTS_":
                                                YOUCANNOTOPENENDERCHESTS_ = el.getAttribute("value");
                                                break;
                                            case "RESTARTTHESERVER_VERSION":
                                                RESTARTTHESERVER_VERSION = el.getAttribute("value");
                                                break;
                                            case "OFRE_UPDATECOMMAND":
                                                OFRE_UPDATECOMMAND = el.getAttribute("value");
                                                break;
                                            case "OFRE_UPDATEINFO":
                                                OFRE_UPDATEINFO = el.getAttribute("value");
                                                break;
                                            case "REALSHOPPINGINITIALIZED":
                                                REALSHOPPINGINITIALIZED = el.getAttribute("value");
                                                break;
                                            case "REALSHOPPINGDISABLED":
                                                REALSHOPPINGDISABLED = el.getAttribute("value");
                                                break;
                                            case "BOUGHTSTUFFFOR":
                                                BOUGHTSTUFFFOR = el.getAttribute("value");
                                                break;
                                            case "FROMYOURSTORE":
                                                FROMYOURSTORE = el.getAttribute("value");
                                                break;
                                            case "OWNER":
                                                OWNER = el.getAttribute("value");
                                                break;
                                            case "CANTAFFORDTOBUYITEMSFROMYOUFOR":
                                                CANTAFFORDTOBUYITEMSFROMYOUFOR = el.getAttribute("value");
                                                break;
                                            case "WITHDRAWNFROMYOURACCOUNT":
                                                WITHDRAWNFROMYOURACCOUNT = el.getAttribute("value");
                                                break;
                                            case "YOUCANTAFFORDTOPAYTHEDELIVERYFEEOF":
                                                YOUCANTAFFORDTOPAYTHEDELIVERYFEEOF = el.getAttribute("value");
                                                break;
                                            case "USESLEFT":
                                                USESLEFT = el.getAttribute("value");
                                                break;
                                            case "PRICES":
                                                PRICES = el.getAttribute("value");
                                                break;
                                            case "RSSTORESHELP":
                                                RSSTORESHELP = el.getAttribute("value");
                                                break;
                                            case "BUYFORHELP":
                                                BUYFORHELP = el.getAttribute("value");
                                                break;
                                            case "COLLECTHELP":
                                                COLLECTHELP = el.getAttribute("value");
                                                break;
                                            case "BANHELP":
                                                BANHELP = el.getAttribute("value");
                                                break;
                                            case "UNBANHELP":
                                                UNBANHELP = el.getAttribute("value");
                                                break;
                                            case "KICKHELP":
                                                KICKHELP = el.getAttribute("value");
                                                break;
                                            case "STARTSALEHELP":
                                                STARTSALEHELP = el.getAttribute("value");
                                                break;
                                            case "STARTSALEHELP2":
                                                STARTSALEHELP2 = el.getAttribute("value");
                                                break;
                                            case "ENDSALEHELP":
                                                ENDSALEHELP = el.getAttribute("value");
                                                break;
                                            case "NOTIFICATIONSHELP":
                                                NOTIFICATIONSHELP = el.getAttribute("value");
                                                break;
                                            case "ONCHANGEHELP":
                                                ONCHANGEHELP = el.getAttribute("value");
                                                break;
                                            case "SETDEFAULTPRICESFOR":
                                                SETDEFAULTPRICESFOR = el.getAttribute("value");
                                                break;
                                            case "THEREARENODEFAULTPRICES":
                                                THEREARENODEFAULTPRICES = el.getAttribute("value");
                                                break;
                                            case "QUITCONVERSATION":
                                                QUITCONVERSATION = el.getAttribute("value");
                                                break;
                                            case "WHICHFILEDOYOUWANTTOIMPORT_":
                                                WHICHFILEDOYOUWANTTOIMPORT_ = el.getAttribute("value");
                                                break;
                                            case "ERROR_NO_XLSX_":
                                                ERROR_NO_XLSX_ = el.getAttribute("value");
                                                break;
                                            case "INTHEMAINDIRECTORY":
                                                INTHEMAINDIRECTORY = el.getAttribute("value");
                                                break;
                                            case "INTHEREALSHOPPINGDIRECTORY":
                                                INTHEREALSHOPPINGDIRECTORY = el.getAttribute("value");
                                                break;
                                            case "TYPETHECORRESPONDINGNUMBER_":
                                                TYPETHECORRESPONDINGNUMBER_ = el.getAttribute("value");
                                                break;
                                            case "TOCANCEL":
                                                TOCANCEL = el.getAttribute("value");
                                                break;
                                            case "ERROR_INPUTISNOTAVALIDINTEGER":
                                                ERROR_INPUTISNOTAVALIDINTEGER = el.getAttribute("value");
                                                break;
                                            case "CHOSENFILE":
                                                CHOSENFILE = el.getAttribute("value");
                                                break;
                                            case "TYPE":
                                                TYPE = el.getAttribute("value");
                                                break;
                                            case "TOIMPORT_USERDEFINED_":
                                                TOIMPORT_USERDEFINED_ = el.getAttribute("value");
                                                break;
                                            case "TOIMPORT_PROPOSITION_":
                                                TOIMPORT_PROPOSITION_ = el.getAttribute("value");
                                                break;
                                            case "WRONGFILECHOSEN":
                                                WRONGFILECHOSEN = el.getAttribute("value");
                                                break;
                                            case "IMPORTED":
                                                IMPORTED = el.getAttribute("value");
                                                break;
                                            case "PRICESASDEFAULT":
                                                PRICESASDEFAULT = el.getAttribute("value");
                                                break;
                                            case "ERRORCOULDNTIMPORTPRICES":
                                                ERRORCOULDNTIMPORTPRICES = el.getAttribute("value");
                                                break;
                                            case "DONE":
                                                DONE = el.getAttribute("value");
                                                break;
                                            case "REALSHOPPINGUPDATEDTO":
                                                REALSHOPPINGUPDATEDTO = el.getAttribute("value");
                                                break;
                                            case "X_ST":
                                                X_ST = el.getAttribute("value");
                                                break;
                                            case "X_ND":
                                                X_ND = el.getAttribute("value");
                                                break;
                                            case "X_RD":
                                                X_RD = el.getAttribute("value");
                                                break;
                                            case "X_TH":
                                                X_TH = el.getAttribute("value");
                                                break;
                                            case "YOUWILLBENOTIFIEDIF":
                                                YOUWILLBENOTIFIEDIF = el.getAttribute("value");
                                                break;
                                            case "LOSESGAINS":
                                                LOSESGAINS = el.getAttribute("value");
                                                break;
                                            case "PLACES":
                                                PLACES = el.getAttribute("value");
                                                break;
                                            case "THEPRICEWILLBELOWEREDINCREASED_":
                                                THEPRICEWILLBELOWEREDINCREASED_ = el.getAttribute("value");
                                                break;
                                            case "PCNTIF":
                                                PCNTIF = el.getAttribute("value");
                                                break;
                                            case "WONTNOTIFY_":
                                                WONTNOTIFY_ = el.getAttribute("value");
                                                break;
                                            case "NO_HELP_DOCUMENTATION_":
                                                NO_HELP_DOCUMENTATION_ = el.getAttribute("value");
                                                break;
                                            case "FORHELPTYPE":
                                                FORHELPTYPE = el.getAttribute("value");
                                                break;
                                            case "BLOCK_ADDED_TO_SELECTION":
                                                BLOCK_ADDED_TO_SELECTION = el.getAttribute("value");
                                                break;
                                            case "BLOCK_ALREADY_SELECTED":
                                                BLOCK_ALREADY_SELECTED = el.getAttribute("value");
                                                break;
                                            case "BLOCK_REMOVED_FROM_SELECTION":
                                                BLOCK_REMOVED_FROM_SELECTION = el.getAttribute("value");
                                                break;
                                            case "BLOCK_WASNT_SELECTED":
                                                BLOCK_WASNT_SELECTED = el.getAttribute("value");
                                                break;
                                            case "CHANGES_UNDONE":
                                                CHANGES_UNDONE = el.getAttribute("value");
                                                break;
                                            case "PLEASE_SELECT_EXIT_":
                                                PLEASE_SELECT_EXIT_ = el.getAttribute("value");
                                                break;
                                            case "PLEASE_SELECT_THE_EXIT_LINKED_TO_ENTRANCE_":
                                                PLEASE_SELECT_THE_EXIT_LINKED_TO_ENTRANCE_ = el.getAttribute("value");
                                                break;
                                            case "PLEASE_SELECT_ENTRANCE_":
                                                PLEASE_SELECT_ENTRANCE_ = el.getAttribute("value");
                                                break;
                                            case "TO_EXITS_LIST":
                                                TO_EXITS_LIST = el.getAttribute("value");
                                                break;
                                            case "IS_NOT_AN_EXIT_TO_THE_LAST_ENTRANCE":
                                                IS_NOT_AN_EXIT_TO_THE_LAST_ENTRANCE = el.getAttribute("value");
                                                break;
                                            case "TO_ENTRANCES_LIST":
                                                TO_ENTRANCES_LIST = el.getAttribute("value");
                                                break;
                                            case "IS_NOT_AN_ENTRANCE_TO_":
                                                IS_NOT_AN_ENTRANCE_TO_ = el.getAttribute("value");
                                                break;
                                            case "ABANDONING_CONVERSATIONS":
                                                ABANDONING_CONVERSATIONS = el.getAttribute("value");
                                                break;
                                            case "CONVERSATIONS_ABANDONED":
                                                CONVERSATIONS_ABANDONED = el.getAttribute("value");
                                                break;
                                            case "QUIT_CONVERSATION_FOR_UNKNOWN_REASON":
                                                QUIT_CONVERSATION_FOR_UNKNOWN_REASON = el.getAttribute("value");
                                                break;
                                            case "IS_NOT_IN_A_STORE":
                                                IS_NOT_IN_A_STORE = el.getAttribute("value");
                                                break;
                                            case "IS_NOT_ALLOWED_TO_MANAGE_STORE_":
                                                IS_NOT_ALLOWED_TO_MANAGE_STORE_ = el.getAttribute("value");
                                                break;
                                            case "YOU_CANT_TELEPORT_WHILE_IN_A_CONVERSATION":
                                                YOU_CANT_TELEPORT_WHILE_IN_A_CONVERSATION = el.getAttribute("value");
                                                break;
                                            case "YOU_CANT_DO_THIS_WHILE_IN_A_CONVERSATION":
                                                YOU_CANT_DO_THIS_WHILE_IN_A_CONVERSATION = el.getAttribute("value");
                                                break;
                                            case "ALL_CONVERSATIONS_CAN_BE_ABORTED_WITH_":
                                                ALL_CONVERSATIONS_CAN_BE_ABORTED_WITH_ = el.getAttribute("value");
                                                break;
                                            case "YOU_HAVE_TO_SEARCH_FOR_A_SPECIFIC_ITEM":
                                                YOU_HAVE_TO_SEARCH_FOR_A_SPECIFIC_ITEM = el.getAttribute("value");
                                                break;
                                            case "NO_MATCHES_FOR_":
                                                NO_MATCHES_FOR_ = el.getAttribute("value");
                                                break;
                                            case "RSPRICESHELP":
                                                RSPRICESHELP = el.getAttribute("value");
                                                break;
                                            case "RSPRICESHELP2":
                                                RSPRICESHELP2 = el.getAttribute("value");
                                                break;
                                            case "RSPRICESHELP3":
                                                RSPRICESHELP3 = el.getAttribute("value");
                                                break;
                                            case "RSPROTECTHELP":
                                                RSPROTECTHELP = el.getAttribute("value");
                                                break;
                                            case "RSPROTECTHELP2":
                                                RSPROTECTHELP2 = el.getAttribute("value");
                                                break;
                                            case "RSPROTECTHELP3":
                                                RSPROTECTHELP3 = el.getAttribute("value");
                                                break;
                                            case "FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_":
                                                FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ = el.getAttribute("value");
                                                break;
                                            case "YOU_CAN_GET_MORE_HELP_ABOUT_":
                                                YOU_CAN_GET_MORE_HELP_ABOUT_ = el.getAttribute("value");
                                                break;
                                            case "STARTS_AN_INTERACTIVE_PROMPT":
                                                STARTS_AN_INTERACTIVE_PROMPT = el.getAttribute("value");
                                                break;
                                            case "RSSETHELP":
                                                RSSETHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETSTORESHELP":
                                                RSSETSTORESHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETENTRANCEHELP":
                                                RSSETENTRANCEHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETEXITHELP":
                                                RSSETEXITHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETCREATEHELP":
                                                RSSETCREATEHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETDELSTOREHELP":
                                                RSSETDELSTOREHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETDELENHELP":
                                                RSSETDELENHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETCHESTSHELP":
                                                RSSETCHESTSHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETCHESTSCREATEHELP":
                                                RSSETCHESTSCREATEHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETCHESTSDELHELP":
                                                RSSETCHESTSDELHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETCHESTSADDITEMSHELP":
                                                RSSETCHESTSADDITEMSHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETCHESTSDELITEMSHELP":
                                                RSSETCHESTSDELITEMSHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESHELP":
                                                RSSETPRICESHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESHELP2":
                                                RSSETPRICESHELP2 = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESADDHELP":
                                                RSSETPRICESADDHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESADDHELP2":
                                                RSSETPRICESADDHELP2 = el.getAttribute("value");
                                                break;
                                            case "AND_":
                                                AND_ = el.getAttribute("value");
                                                break;
                                            case "ARGUMENTS":
                                                ARGUMENTS = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESDELHELP":
                                                RSSETPRICESDELHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESDEFAUTLSHELP":
                                                RSSETPRICESDEFAUTLSHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESCOPYHELP":
                                                RSSETPRICESCOPYHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESCOPYHELP2":
                                                RSSETPRICESCOPYHELP2 = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESCOPYHELP3":
                                                RSSETPRICESCOPYHELP3 = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESCLEARHELP":
                                                RSSETPRICESCLEARHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESSHOWMMHELP":
                                                RSSETPRICESSHOWMMHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESCLEARMMHELP":
                                                RSSETPRICESCLEARMMHELP = el.getAttribute("value");
                                                break;
                                            case "RSSETPRICESSETMMHELP":
                                                RSSETPRICESSETMMHELP = el.getAttribute("value");
                                                break;
                                            case "RSSHIPPEDHELP":
                                                RSSHIPPEDHELP = el.getAttribute("value");
                                                break;
                                            case "RSSHIPPEDHELP2":
                                                RSSHIPPEDHELP2 = el.getAttribute("value");
                                                break;
                                            case "RSSHIPPEDHELP3":
                                                RSSHIPPEDHELP3 = el.getAttribute("value");
                                                break;
                                            case "RSTPLOCSHELP":
                                                RSTPLOCSHELP = el.getAttribute("value");
                                                break;
                                            case "RSTPLOCSWHITEHELP":
                                                RSTPLOCSWHITEHELP = el.getAttribute("value");
                                                break;
                                            case "RSTPLOCSBLACKHELP":
                                                RSTPLOCSBLACKHELP = el.getAttribute("value");
                                                break;
                                            case "RSTPLOCSLISTHELP":
                                                RSTPLOCSLISTHELP = el.getAttribute("value");
                                                break;
                                            case "RSTPLOCSADDHELP":
                                                RSTPLOCSADDHELP = el.getAttribute("value");
                                                break;
                                            case "RSTPLOCSADDHELP2":
                                                RSTPLOCSADDHELP2 = el.getAttribute("value");
                                                break;
                                            case "RSTPLOCSDELHELP":
                                                RSTPLOCSDELHELP = el.getAttribute("value");
                                                break;
                                            case "RSTPLOCSHIGHHELP":
                                                RSTPLOCSHIGHHELP = el.getAttribute("value");
                                                break;
                                            case "RSUPDATEHELP":
                                                RSUPDATEHELP = el.getAttribute("value");
                                                break;
                                            case "RSUPDATEUPDATEHELP":
                                                RSUPDATEUPDATEHELP = el.getAttribute("value");
                                                break;
                                            case "RSUPDATEINFOHELP":
                                                RSUPDATEINFOHELP = el.getAttribute("value");
                                                break;
                                            case "THIS_ENTRANCE_AND_EXIT_PAIR_IS_ALREADY_USED":
                                                THIS_ENTRANCE_AND_EXIT_PAIR_IS_ALREADY_USED = el.getAttribute("value");
                                                break;
                                            case "YOU_CANT_USE_THIS_COMMAND_INSIDE_A_STORE":
                                                YOU_CANT_USE_THIS_COMMAND_INSIDE_A_STORE = el.getAttribute("value");
                                                break;
                                            case "LOADED_CONFIG_SETTINGS":
                                                LOADED_CONFIG_SETTINGS = el.getAttribute("value");
                                                break;
                                            case "ENABLED_IN_ALL_WORLDS":
                                                ENABLED_IN_ALL_WORLDS = el.getAttribute("value");
                                                break;
                                            case "FOR_MORE":
                                                FOR_MORE = el.getAttribute("value");
                                                break;
                                            case "IS_NOT_AN_ACCEPTED_ARGUMENT":
                                                IS_NOT_AN_ACCEPTED_ARGUMENT = el.getAttribute("value");
                                                break;
                                            case "TO_CONTINUE_TYPE_ANY_OF_THE_PURPLE_KEYWORDS":
                                                TO_CONTINUE_TYPE_ANY_OF_THE_PURPLE_KEYWORDS = el.getAttribute("value");
                                                break;
                                            case "WHAT_DO_YOU_WANT_TO_DO_":
                                                WHAT_DO_YOU_WANT_TO_DO_ = el.getAttribute("value");
                                                break;
                                            case "THIS_PROMPT_WILL_AID_YOU_IN__CHESTS_":
                                                THIS_PROMPT_WILL_AID_YOU_IN__CHESTS_ = el.getAttribute("value");
                                                break;
                                            case "NEW_CHESTS_OR_":
                                                NEW_CHESTS_OR_ = el.getAttribute("value");
                                                break;
                                            case "OR_":
                                                OR_ = el.getAttribute("value");
                                                break;
                                            case "EXISTING_ONES":
                                                EXISTING_ONES = el.getAttribute("value");
                                                break;
                                            case "YOU_HAVE_CHOSEN_TO_CREATE_NEW_CHESTS_":
                                                YOU_HAVE_CHOSEN_TO_CREATE_NEW_CHESTS_ = el.getAttribute("value");
                                                break;
                                            case "YOU_HAVE_CHOSEN_TO_DELETE_EXISTING_CHESTS_":
                                                YOU_HAVE_CHOSEN_TO_DELETE_EXISTING_CHESTS_ = el.getAttribute("value");
                                                break;
                                            case "YOU_HAVE_CHOSEN_TO_MANAGE_EXISTING_CHESTS_":
                                                YOU_HAVE_CHOSEN_TO_MANAGE_EXISTING_CHESTS_ = el.getAttribute("value");
                                                break;
                                            case "APPLY_TO_ALL_CHOSEN_CHESTS_":
                                                APPLY_TO_ALL_CHOSEN_CHESTS_ = el.getAttribute("value");
                                                break;
                                            case "TO_CLEAR_THE_SELECTION_OF_CHESTS_OR_":
                                                TO_CLEAR_THE_SELECTION_OF_CHESTS_OR_ = el.getAttribute("value");
                                                break;
                                            case "TO_SELECT_ALL__EXIT_WITH_":
                                                TO_SELECT_ALL__EXIT_WITH_ = el.getAttribute("value");
                                                break;
                                            case "YOU_HAVE_CHOSEN_TO_MANAGE_EXISTING_CHESTS_FREELY_":
                                                YOU_HAVE_CHOSEN_TO_MANAGE_EXISTING_CHESTS_FREELY_ = el.getAttribute("value");
                                                break;
                                            case "CHESTS_CREATED":
                                                CHESTS_CREATED = el.getAttribute("value");
                                                break;
                                            case "CHESTS_REMOVED":
                                                CHESTS_REMOVED = el.getAttribute("value");
                                                break;
                                            case "ACTION_ABORTED":
                                                ACTION_ABORTED = el.getAttribute("value");
                                                break;
                                            case "ITEMS_TO_":
                                                ITEMS_TO_ = el.getAttribute("value");
                                                break;
                                            case "CHESTS":
                                                CHESTS = el.getAttribute("value");
                                                break;
                                            case "DELETED_":
                                                DELETED_ = el.getAttribute("value");
                                                break;
                                            case "ITEMS_FROM_":
                                                ITEMS_FROM_ = el.getAttribute("value");
                                                break;
                                            case "CLEARED_CONTENTS_OF_":
                                                CLEARED_CONTENTS_OF_ = el.getAttribute("value");
                                                break;
                                            case "UPDATED_CONTENTS_OF":
                                                UPDATED_CONTENTS_OF = el.getAttribute("value");
                                                break;
                                            case "SELECTED_":
                                                SELECTED_ = el.getAttribute("value");
                                                break;
                                            case "CLEARED_SELECTION":
                                                CLEARED_SELECTION = el.getAttribute("value");
                                                break;
                                            case "QUIT_MANAGING_CHESTS":
                                                QUIT_MANAGING_CHESTS = el.getAttribute("value");
                                                break;
                                            case "THIS_PROMPT_WILL_AID_YOU_IN__STORE_":
                                                THIS_PROMPT_WILL_AID_YOU_IN__STORE_ = el.getAttribute("value");
                                                break;
                                            case "THIS_PROMPT_WILL_AID_YOU_IN__PLAYER_STORE_":
                                                THIS_PROMPT_WILL_AID_YOU_IN__PLAYER_STORE_ = el.getAttribute("value");
                                                break;
                                            case "YOU_DONT_HAVE_THE_RIGHT_PERMISSIONS_TO_DO_THIS":
                                                YOU_DONT_HAVE_THE_RIGHT_PERMISSIONS_TO_DO_THIS = el.getAttribute("value");
                                                break;
                                            case "A_NEW_STORE_":
                                                A_NEW_STORE_ = el.getAttribute("value");
                                                break;
                                            case "ENTRANCES_AND_EXITS_OR_":
                                                ENTRANCES_AND_EXITS_OR_ = el.getAttribute("value");
                                                break;
                                            case "AN_EXISTING_STORE":
                                                AN_EXISTING_STORE = el.getAttribute("value");
                                                break;
                                            case "YOU_HAVE_CHOSEN_TO_CREATE_A_NEW_STORE_":
                                                YOU_HAVE_CHOSEN_TO_CREATE_A_NEW_STORE_ = el.getAttribute("value");
                                                break;
                                            case "YOU_HAVE_CHOSEN_TO_APPEND__STORE_":
                                                YOU_HAVE_CHOSEN_TO_APPEND__STORE_ = el.getAttribute("value");
                                                break;
                                            case "YOU_HAVE_CHOSEN_TO_DELETE__STORE_":
                                                YOU_HAVE_CHOSEN_TO_DELETE__STORE_ = el.getAttribute("value");
                                                break;
                                            case "YOU_HAVE_CHOSEN_TO_WIPE_OUT_A_STORE_":
                                                YOU_HAVE_CHOSEN_TO_WIPE_OUT_A_STORE_ = el.getAttribute("value");
                                                break;
                                            case "THE_NAME_":
                                                THE_NAME_ = el.getAttribute("value");
                                                break;
                                            case "IS_AVAILABLE":
                                                IS_AVAILABLE = el.getAttribute("value");
                                                break;
                                            case "THAT_NAME_IS_ALREADY_TAKEN":
                                                THAT_NAME_IS_ALREADY_TAKEN = el.getAttribute("value");
                                                break;
                                            case "YOU_CANT_USE_THIS_INSIDE_A_STORE":
                                                YOU_CANT_USE_THIS_INSIDE_A_STORE = el.getAttribute("value");
                                                break;
                                            case "RIGHTCLICK_A_BLOCK_TO_SELECT_IT_AS_EE_":
                                                RIGHTCLICK_A_BLOCK_TO_SELECT_IT_AS_EE_ = el.getAttribute("value");
                                                break;
                                            case "WHEN_DONE_OR_":
                                                WHEN_DONE_OR_ = el.getAttribute("value");
                                                break;
                                            case "TO_START_OVER_":
                                                TO_START_OVER_ = el.getAttribute("value");
                                                break;
                                            case "CREATED_STORE_":
                                                CREATED_STORE_ = el.getAttribute("value");
                                                break;
                                            case "EE_PAIRS":
                                                EE_PAIRS = el.getAttribute("value");
                                                break;
                                            case "NO_EE_SELECTED":
                                                NO_EE_SELECTED = el.getAttribute("value");
                                                break;
                                            case "YOU_HAVE_CHOSEN_TO_APPEND_NEW_EE_TO_":
                                                YOU_HAVE_CHOSEN_TO_APPEND_NEW_EE_TO_ = el.getAttribute("value");
                                                break;
                                            case "APPENDED_":
                                                APPENDED_ = el.getAttribute("value");
                                                break;
                                            case "EE_PAIRS_TO_STORE_":
                                                EE_PAIRS_TO_STORE_ = el.getAttribute("value");
                                                break;
                                            case "YOU_HAVE_CHOSEN_TO_DELETE_EE_FROM_":
                                                YOU_HAVE_CHOSEN_TO_DELETE_EE_FROM_ = el.getAttribute("value");
                                                break;
                                            case "EE_PAIRS_FROM_STORE_":
                                                EE_PAIRS_FROM_STORE_ = el.getAttribute("value");
                                                break;
                                            case "DO_YOU_REALLY_WANT_TO_DELETE_":
                                                DO_YOU_REALLY_WANT_TO_DELETE_ = el.getAttribute("value");
                                                break;
                                            case "AND_ALL_ITS_":
                                                AND_ALL_ITS_ = el.getAttribute("value");
                                                break;
                                            //0.51 inventory buttons
                                            case "BTN_CANCEL":
                                                BTN_CANCEL = el.getAttribute("value");
                                                break;
                                            case "BTN_CONFIRM":
                                                BTN_CONFIRM = el.getAttribute("value");
                                                break;
                                            case "BTN_PAYMENTINFO":
                                                BTN_PAYMENTINFO = el.getAttribute("value");
                                                break;
                                            case "BTN_SELLINFO":
                                                BTN_SELLINFO = el.getAttribute("value");
                                                break;
                                            case "PAYTOSTORE":
                                                PAYTOSTORE = el.getAttribute("value");
                                                break;
                                            case "COUPONNAME":
                                                COUPONNAME = el.getAttribute("value");
                                            default:
                                                break;
                                        }
				}
				RealShopping.loginfo("Loaded " + lang + " language pack.");
			}
		} catch (SAXException e) {
			e.printStackTrace();
			RealShopping.loginfo("Failed while loading " + lang + " language pack.");
		} catch (IOException e) {
			e.printStackTrace();
			RealShopping.loginfo("Failed while loading " + lang + " language pack.");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			RealShopping.loginfo("Failed while loading " + lang + " language pack.");
		} catch (Exception e) {
			e.printStackTrace();
			RealShopping.loginfo("Failed while loading " + lang + " language pack.");
		}
		else RealShopping.loginfo("Loaded default language pack.");
	}
}