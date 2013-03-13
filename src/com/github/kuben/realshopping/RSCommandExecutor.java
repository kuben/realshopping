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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.h31ix.updater.Updater;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RSCommandExecutor implements CommandExecutor {
	RealShopping rs;
	
	public RSCommandExecutor(RealShopping rs){
		this.rs = rs;
	}
	@SuppressWarnings("static-access")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	
		if(!RealShopping.working.equals("")){
			sender.sendMessage(ChatColor.RED + RealShopping.working);
			return false;
		}
		
		try {
    	Player player = null;
    	if (sender instanceof Player) {
    		player = (Player) sender;
    	}
    	if(cmd.getName().equalsIgnoreCase("rsreload")){
    		rs.reload();
    		sender.sendMessage(ChatColor.GREEN + LangPack.REALSHOPPINGRELOADED);
    		return true;
    	} else if(cmd.getName().equalsIgnoreCase("rsenter")){
    		if(player != null){
    			return rs.enter(player, true);
    		}
    		else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rspay")){
    		if(player != null){
    			return rs.pay(player, null);
    		}
    		else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rscost")){
    		if(player != null){
    			player.sendMessage(ChatColor.RED + LangPack.YOURARTICLESCOST + rs.PInvMap.get(player.getName()).toPay());
    			return true;
    		}
    		else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rsexit")){
    		if(player != null){
    			return rs.exit(player, true);
    		}
    		else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if (cmd.getName().equalsIgnoreCase("rsprices")){
			if(args.length == 0){
				if(player != null){
					if(rs.PInvMap.get(player.getName()) != null) {
						return prices(sender, 0, rs.PInvMap.get(player.getName()).getStore(), true);
					} else {
						sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
					}
				} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
			} else if(args.length == 1){
				if(args[0].matches("[0-9]+")){
					if(player != null){
						if(rs.PInvMap.containsKey(player.getName())){
							int i = Integer.parseInt(args[0]);
							if(i > 0) return prices(sender, i - 1, rs.PInvMap.get(player.getName()).getStore(), true);
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
    	} else if(cmd.getName().equalsIgnoreCase("rssell")){
    		if(player != null){
    			if(rs.PInvMap.containsKey(player.getName())){
					if(Config.isEnableSelling()){
	    				Inventory tempInv = Bukkit.createInventory(null, 36, "Sell to store");//TODO langpack
						player.openInventory(tempInv);
						return true;
					} else player.sendMessage(ChatColor.RED + LangPack.SELLINGTOSTORESISNOTENABLEDONTHISSERVER);
    			} else sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
    		}
    		else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rsstores")) {
    		if(args.length > 0){
    			if(args[0].equals("help")){//TODO langpack all of this
    				if(args.length == 1){
    					sender.sendMessage(ChatColor.GREEN + "Use rsstores with only the name of the store as argument to get some information about the store. "
    							+ " For help, type any of these arguments: " + ChatColor.DARK_PURPLE + "buyfor, collect, ban, unban, kick, startsale, endsale, notifications, onchange");
    				} else {
    					if(args[1].equals("buyfor")) sender.sendMessage("Usage: " + ChatColor.DARK_PURPLE + "buyfor %_OF_SELL_PRICE" + ChatColor.RESET
    							+ ". Sets if and for how much of the sell price your store will buy items from players. 0 is default and means selling to your store is disabled.");
    					else if(args[1].equals("collect")) sender.sendMessage("Usage: " + ChatColor.DARK_PURPLE + "collect [-c] [AMOUNT]" + ChatColor.RESET
    							+ ". Collects items that have been stolen from (and then returned to) or sold to your store. If using the -c flag the items " + 
    							"will spawn in a chast which you are standing on. You can limit the number of items returned by writing an number.");
    					else if(args[1].equals("ban")) sender.sendMessage("Usage: " + ChatColor.DARK_PURPLE + "ban PLAYER" + ChatColor.RESET
    							+ ". Banishes a player from your store forever. ");
    					else if(args[1].equals("unban")) sender.sendMessage("Usage: " + ChatColor.DARK_PURPLE + "unban PLAYER" + ChatColor.RESET
    							+ ". Unbanishes a previously banned player. ");
    					else if(args[1].equals("kick")) sender.sendMessage("Usage: " + ChatColor.DARK_PURPLE + "kick [-o] PLAYER" + ChatColor.RESET
    							+ ". Kicks a player out of your store. You can use the -o flag to kick an offline player but do ONLY use it if you're " +
    							"about to remove the store, as the player won't be teleported out.");
    					else if(args[1].equals("startsale")) sender.sendMessage("Usage: " + ChatColor.DARK_PURPLE + "startsale %_OFF [ITEM(S)]" + ChatColor.RESET
    							+ ". Starts a sale on the all or given items. Also cancels the last sale. Write items in this format: " + ChatColor.DARK_PURPLE + 
    							"ITEMID[:DATA]" + ChatColor.RESET + " and separate multiple items with commas. The percent argument can be any integer between 1 and 99");
    					else if(args[1].equals("endsale")) sender.sendMessage("Usage: " + ChatColor.DARK_PURPLE + "endsale" + ChatColor.RESET
    							+ ". Ends all sales.");
    					else if(args[1].equals("notifications")) sender.sendMessage("Usage: " + ChatColor.DARK_PURPLE + "notifications [on|off]" + ChatColor.RESET
    	    							+ ". Sets if notifications are enabled or disabled for this store. Use without arguments to check current status.");
    					else if(args[1].equals("onchange")) sender.sendMessage("Usage: " + ChatColor.DARK_PURPLE + "onchange [nothing|notify TRESHOLD|changeprices TRESHOLD PERCENT]"
    	    							+ ChatColor.RESET + ". Sets if this store should notify you about changes in how well this store sells compared to others.");
    				}
    				return true;
    			}
    			boolean isOwner = false;
    			if(rs.shopMap.containsKey(args[0]))
    				if(rs.shopMap.get(args[0]).getOwner().equalsIgnoreCase("@admin")){
    					if(player == null){
    						isOwner = true;
    					} else if(player.hasPermission("realshopping.rsset")){
    						isOwner = true;
    				}
    				} else {
    					if(player != null){
    						if(rs.shopMap.get(args[0]).getOwner().equalsIgnoreCase(player.getName())){
    							isOwner = true;
    						}
    					}
    				}
    			else {
    				sender.sendMessage(args[0] + LangPack.DOESNTEXIST);
    				return false;
    			}
    			
    			if(args.length == 1){
    				sender.sendMessage(ChatColor.GREEN + LangPack.STORE + args[0] + ((rs.shopMap.get(args[0]).getOwner().equalsIgnoreCase("@admin"))?"":LangPack.OWNEDBY + rs.shopMap.get(args[0]).getOwner()));
    				if(rs.shopMap.get(args[0]).getBuyFor() > 0) sender.sendMessage(ChatColor.GREEN + LangPack.BUYSFOR + rs.shopMap.get(args[0]).getBuyFor() + LangPack.PCNTOFORIGINAL);
    				if(rs.shopMap.get(args[0]).hasSales()) sender.sendMessage(ChatColor.GREEN + LangPack.HASA + rs.shopMap.get(args[0]).getFirstSale() + LangPack.PCNTOFFSALERIGHTNOW);
    				if(!rs.getPlayersInStore(args[0].toLowerCase())[0].equals("")){
    					sender.sendMessage(ChatColor.DARK_GREEN + LangPack.PLAYERSINSTORE + "\n" + ChatColor.RESET + rs.formatPlayerListToMess(rs.getPlayersInStore(args[0].toLowerCase())));
    				}
    				sender.sendMessage(ChatColor.GREEN + "For help, type " + ChatColor.DARK_PURPLE + "/rsstores help");
    				return true;
    			} else
    			
    			if(isOwner || player == null){
    				if(args[1].equalsIgnoreCase("collect") && player != null){
        				int amount = 0;
        				boolean cFlag = false;
        				
        				if(args.length == 3){
	        				if(args[2].equalsIgnoreCase("-c")){
	        					cFlag = true;
	        				} else {
	        					try {
	    	        				amount = Integer.parseInt(args[2]);
	        					} catch(NumberFormatException e){
	        						sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTANINTEGER);
	        					}
	        				}
        				} else if(args.length == 4){
	        				if(!args[2].equalsIgnoreCase("-c")) return false;
	        					try {
    	    	        					amount = Integer.parseInt(args[3]);
    	    	        					cFlag = true;
	        					} catch(NumberFormatException e){
	        						sender.sendMessage(ChatColor.RED + args[3] + LangPack.ISNOTANINTEGER);
	        					}
        				}
        				return collect(sender, player, rs.shopMap.get(args[0]), cFlag, amount);
    				}
    				
    	    		else if(args.length == 3 && args[1].equalsIgnoreCase("buyfor")){
    	    			if(Config.isEnableSelling()){
    	    				try {
    	    					int pcnt = Integer.parseInt(args[2]);
    	    					if(pcnt <= 100){
    	    						if(pcnt >= 0){
    	    							rs.shopMap.get(args[0]).setBuyFor(pcnt);
    	    							if(pcnt > 0) sender.sendMessage(ChatColor.GREEN + LangPack.BUYSFOR + pcnt + LangPack.PCNTOFORIGINAL);
    	    							else sender.sendMessage(ChatColor.RED + LangPack.NOTBUYINGFROMPLAYERS);
    	    							rs.updateEntrancesDb();
    	    							return true;
    	    						} else sender.sendMessage(ChatColor.RED + LangPack.YOUCANTUSEAVALUEBELLOW0);
    	    					} else  sender.sendMessage(ChatColor.RED + LangPack.YOUCANTUSEAVALUEOVER100);
    	    				} catch(NumberFormatException e){
    	    					sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTANINTEGER);
    	    				}
    	    			} else sender.sendMessage(ChatColor.RED + LangPack.SELLINGTOSTORESISNOTENABLEDONTHISSERVER);
    	     		}
    	    		
    	    		else if(args.length == 3 && args[1].equalsIgnoreCase("ban")){
    	    			if(rs.shopMap.get(args[0]).isBanned(args[2].toLowerCase())) sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISALREADYBANNEDFROMYOURSTORE);
    	    			else {
    	    				rs.shopMap.get(args[0]).addBanned(args[2].toLowerCase());
    	    				sender.sendMessage(ChatColor.GREEN + LangPack.BANNED + args[2] + LangPack.FROMSTORE);
    	    			}
    	    			rs.updateEntrancesDb();
    	    			return true;
    	     		} else if(args.length == 3 && args[1].equalsIgnoreCase("unban")){
    	     			if(rs.shopMap.get(args[0]).isBanned(args[2].toLowerCase())){
    	     				rs.shopMap.get(args[0]).removeBanned(args[2].toLowerCase());
    	     				sender.sendMessage(ChatColor.GREEN + args[2] + LangPack.ISNOLONGERBANNEDFROMYOURSTORE);
    	     			} else sender.sendMessage(ChatColor.RED + args[2] + LangPack.WASNTBANNEDFROMYOURSTORE);
    	     			rs.updateEntrancesDb();
    	     			return true;
    	    		} else if(args.length == 3 && args[1].equalsIgnoreCase("kick")){
    	     			if(!rs.getPlayersInStore(args[0].toLowerCase())[0].equals("")){
    	     				boolean cont = false;
    	     				for(String tempP:rs.getPlayersInStore(args[0].toLowerCase()))
    	     					if(tempP.toLowerCase().equals(args[2].toLowerCase())){
    	     						cont = true;
    	     						break;
    	     					}
    	     				if(cont){
    	     					if(rs.getServer().getPlayerExact(args[2]) != null){
    	     						rs.returnStolen(rs.getServer().getPlayerExact(args[2]));
    	     						Location l = rs.shopMap.get(args[0]).getFirstE();
    	     						rs.PInvMap.remove(rs.getServer().getPlayerExact(args[2]).getName());
    	     						rs.getServer().getPlayerExact(args[2]).teleport(l.add(0.5, 0, 0.5));
    	     						sender.sendMessage(ChatColor.GREEN + args[2] + LangPack.WASKICKEDFROMYOURSTORE);
    	     					} else sender.sendMessage(ChatColor.RED + LangPack.PLAYER + args[2] + LangPack.ISNTONLINEKICK);
    	     				} else sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTINYOURSTORE);
    	     			} else sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTINYOURSTORE);
    	     			return true;
    	    		} else if(args.length == 4 && args[1].equalsIgnoreCase("kick") && args[2].equalsIgnoreCase("-o")){
    	     			if(!rs.getPlayersInStore(args[0].toLowerCase())[0].equals("")){
    	     				if(rs.getServer().getOfflinePlayer(args[3]) != null){
        	     				boolean cont = false;
        	     				for(String tempP:rs.getPlayersInStore(args[0].toLowerCase()))
        	     					if(tempP.toLowerCase().equals(args[3].toLowerCase())){
        	     						cont = true;
        	     						break;
        	     					}
        	     				if(cont){
        	     					rs.PInvMap.remove(rs.getServer().getOfflinePlayer(args[3]).getName());
    	     						sender.sendMessage(ChatColor.GREEN + args[3] + LangPack.WASKICKEDFROMYOURSTORE);
        	     				} else sender.sendMessage(ChatColor.RED + LangPack.PLAYER + args[3] + LangPack.DOESNTEXIST);
        	     			} else sender.sendMessage(ChatColor.RED + LangPack.PLAYER + args[3] + LangPack.DOESNTEXIST);
    	     			} else sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTINYOURSTORE);
    	     			return true;
    	    		}
    	    		
    	    		else if(args.length == 3 && args[1].equalsIgnoreCase("startsale")){
    					try {
	    					int pcnt = Integer.parseInt(args[2]);
	    					if(pcnt < 100){
	    						if(pcnt > 0){
	    							if(rs.shopMap.containsKey(args[0]) && rs.shopMap.get(args[0]).hasPrices()){
	    								rs.shopMap.get(args[0]).clearSales();
	    								Price[] keys = rs.shopMap.get(args[0]).getPrices().keySet().toArray(new Price[0]);
	    								int i = 0;
	    								for(;i < keys.length;i++){
	    									rs.shopMap.get(args[0]).addSale(keys[i], pcnt);
	    								}
	    								if(pcnt > 0) sender.sendMessage(ChatColor.GREEN + "" + pcnt + LangPack.PCNTOFF + i + LangPack.ITEMS);
	    								else sender.sendMessage(ChatColor.RED + LangPack.NOITEMSARESOLDINTHESTORE);
	    								return true;
	    							} else sender.sendMessage(ChatColor.RED + LangPack.NOITEMSARESOLDINTHESTORE);
	    						} else sender.sendMessage(ChatColor.RED + LangPack.YOUCANTUSEAVALUEOF0ORLESS);
	    					} else  sender.sendMessage(ChatColor.RED + LangPack.YOUCANTUSEAVALUEOF100ORMORE);
    					} catch(NumberFormatException e){
    						sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTANINTEGER);
    					}
    	    		} else if(args.length == 4 && args[1].equalsIgnoreCase("startsale")){
    					try {
	    					int pcnt = Integer.parseInt(args[2]);
	    					if(pcnt < 100){
	    						if(pcnt > 0){
	    							String[] keys = args[3].split(",");
	    							if(keys.length > 0){
	    								rs.shopMap.get(args[0]).clearSales();
	    								int i = 0;
	    								int j = 0;
	    								for(;i < keys.length;i++){
	    									Price tempP = new Price(keys[i]);
	    									if(rs.shopMap.get(args[0]).hasPrice(tempP) || rs.shopMap.get(args[0]).hasPrice(new Price(keys[i].split(":")[0]))){
	    										rs.shopMap.get(args[0]).addSale(tempP, pcnt);
	    										j++;
	    									}
	    								}
	    								if(pcnt > 0) sender.sendMessage(ChatColor.GREEN + "" + pcnt + LangPack.PCNTOFF + j + LangPack.ITEMS);
	    								else sender.sendMessage(ChatColor.RED + LangPack.NOITEMSARESOLDINTHESTORE);
	    								return true;
	    							} else sender.sendMessage(ChatColor.RED + args[3] + LangPack.ISNOTAVALIDARGUMENT);
	    						} else sender.sendMessage(ChatColor.RED + LangPack.YOUCANTUSEAVALUEOF0ORLESS); 
	    					} else  sender.sendMessage(ChatColor.RED + LangPack.YOUCANTUSEAVALUEOF100ORMORE);
    					} catch(NumberFormatException e){
    						sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTANINTEGER);
    					}
    	    		} else if(args.length == 2 && args[1].equalsIgnoreCase("endsale")){
    	    			rs.shopMap.get(args[0]).clearSales();
    	    			sender.sendMessage(ChatColor.GREEN + LangPack.SALEENDED);
    	    			return true;
    	    		} else if(args[1].equals("notifications")){
    	    			if(args.length > 2){
    	    				if(args[2].equals("on")){
    	    					rs.shopMap.get(args[0]).setAllowNotifications(true);
    	    					sender.sendMessage(ChatColor.GREEN + "Enabled notifications for " + args[0]);//TODO langpack
    	    				} else if(args[2].equals("off")){
    	    					rs.shopMap.get(args[0]).setAllowNotifications(false);
    	    					sender.sendMessage(ChatColor.GREEN + "Disabled notifications for " + args[0]);//TODO langpack
    	    				} else {
    	    					sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTAVALIDARGUMENT);
    	    					sender.sendMessage("Usage:" + ChatColor.DARK_PURPLE + "[...] notifications [on|off]");//TODO langpack
    	    				}
    	    				return true;
    	    			} else {
    	    				sender.sendMessage(ChatColor.GREEN + "Notifications are " + (rs.shopMap.get(args[0]).allowsNotifications()?"on":"off") + ".");//TODO langpack
    	    				return true;
    	    			}
    	    		} else if(args[1].equals("onchange")){
    	    			if(Config.isEnableAI()){
    	    				if(args.length == 2){
        	    				if(rs.shopMap.get(args[0]).getNotifyChanges() == 1) sender.sendMessage(ChatColor.GREEN + "You will be notified if " + args[0] + " loses/gains "
        	    						+ rs.shopMap.get(args[0]).getChangeTreshold() + " place(s).");
        	    				else if(rs.shopMap.get(args[0]).getNotifyChanges() == 2) sender.sendMessage(ChatColor.GREEN + "The price will be lowered/increased by" + rs.shopMap.get(args[0]).getChangePercent()
        	    						+ "% if " + args[0] + " loses/gains " + rs.shopMap.get(args[0]).getChangeTreshold() + " place(s).");
        	    				else sender.sendMessage(ChatColor.GREEN + args[0] + " won't notify you about changes.");
        	    				return true;
        	    			}
        					if(args.length > 2){
        						int tresh = -1;
        						int pcnt = -1;
            					if(args.length > 3) try {
            						tresh = Integer.parseInt(args[3]);
            					} catch(NumberFormatException e){
            					sender.sendMessage(ChatColor.RED + args[3] + LangPack.ISNOTANINTEGER);
            					} if(args.length > 4) try {
            						pcnt = Integer.parseInt(args[4]);
            					} catch(NumberFormatException e) {
            						sender.sendMessage(ChatColor.RED + args[4] + LangPack.ISNOTANINTEGER);
            						if(args[4].contains("%")) sender.sendMessage("(Skip the %-sign)");//TODO langpack
            					}
            					if(args[2].equals("nothing")){
            						rs.shopMap.get(args[0]).setNotifyChanges((byte)0);
            						sender.sendMessage(ChatColor.GREEN + "You won't get notified when your store " + args[0] + " becomes more or less popular.");//TODO langpack
            						rs.updateEntrancesDb();
            						return true;
            					} else if(args[2].equals("notify")){
            						if(tresh > -1){
            							rs.shopMap.get(args[0]).setNotifyChanges((byte)1);
            							rs.shopMap.get(args[0]).setChangeTreshold(tresh);
                						sender.sendMessage(ChatColor.GREEN + "You will get notified when your store " + args[0] + " becomes at least " + tresh + " places more or less popular.");//TODO langpack
                						rs.updateEntrancesDb();
            						} else
            							sender.sendMessage("Usage:" + ChatColor.DARK_PURPLE + "[...] onchange notify TRESHOLD " + ChatColor.RESET//TODO langpack
            									+ " where TRESHOLD is how many places your store needs to lose or gain for you to be notified.");//TODO langpack
            						return true;
        						} else if(args[2].equals("changeprices")){
        							if(tresh > -1 && pcnt > -1){
        								rs.shopMap.get(args[0]).setNotifyChanges((byte)2);
        								rs.shopMap.get(args[0]).setChangeTreshold(tresh);
        								rs.shopMap.get(args[0]).setChangePercent(pcnt);
                						sender.sendMessage(ChatColor.GREEN + "You will get notified when your store " + args[0] + " becomes at least " + ChatColor.DARK_PURPLE + tresh//TODO langpack
                								+ " places more or less popular. And the prices will be lowered or increased by " + ChatColor.DARK_PURPLE + pcnt + "%.");//TODO langpack
                						rs.updateEntrancesDb();
                						return true;
        							} else
            							sender.sendMessage("Usage:" + ChatColor.DARK_PURPLE + "[...] onchange changepries TRESHOLD PERCENT" + ChatColor.RESET//TODO langpack
            									+ " where TRESHOLD is how many places your store needs to lose or gain for you to be for the changes to happen, "//TODO langpack
            									+ "and PERCENT is how many percent the prices will be lowered or increased.");//TODO langpack
            						return true;
        						}
            				}
    	    			} else {
    	    				sender.sendMessage(ChatColor.RED + "Automatic store management is not enabled on this server.");//TODO langpack
    	    			}     					
    	    		}
    			} else sender.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE);
    		}
    	} else if(cmd.getName().equalsIgnoreCase("rssetprices")) {
    		if(args.length > 0){
    			String shop = "";
    			boolean isPlayer = player != null && rs.PInvMap.containsKey(player.getName());
    			int ii = 1;//First argument after store, not all commands need this
    			if(args[0].equalsIgnoreCase("add")//STORE ID:DATA:COST:MIN:MAX
    					|| args[0].equalsIgnoreCase("del")//STORE ID:DATA 
    					|| args[0].equalsIgnoreCase("showminmax")//STORE PRICE
    					|| args[0].equalsIgnoreCase("clearminmax")//STORE PRICE
    					|| args[0].equalsIgnoreCase("setminmax")){//STORE PRICE:MIN:MAX
    				if(args.length < 3 && isPlayer) shop = rs.PInvMap.get(player.getName()).getStore();
    				else {
    					shop = args[1];
    					ii = 2;
    				}
    			} else if(args[0].equalsIgnoreCase("copy")){//STORE STORE
    				if(args.length < 2 && isPlayer){
    					shop = rs.PInvMap.get(player.getName()).getStore();
    				} else if(args.length == 2) shop = args[1];
    				else if(args.length > 2) {
    					shop = args[1];
    					ii = 2;
    				}
    			} else if(args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("defaults")){//STORE
    				if(args.length == 1 && isPlayer) shop = rs.PInvMap.get(player.getName()).getStore();
    				else if(args.length > 1) shop = args[1];
    			}
    			
        		if(!shop.equals("")){
        			if(rs.shopMap.containsKey(shop)){
            			if(player == null || (rs.shopMap.get(shop).getOwner().equals(player.getName()) || player.hasPermission("realshopping.rsset"))){//If player is owner OR has admin perms
            				Shop tempShop = rs.shopMap.get(shop);
            				if(args[0].equalsIgnoreCase("add")){
            					try {
            						int i = Integer.parseInt(args[ii].split(":")[0]);
            						int jj = 1;//First argument after item
            						int d = -1;
            						if(args[ii].split(":").length == 3 || args[ii].split(":").length == 5 ) {
            							d = Integer.parseInt(args[ii].split(":")[1]);
            							jj = 2;
            						}
            						float price = Float.valueOf(args[ii].split(":")[jj]);
            						DecimalFormat twoDForm = new DecimalFormat("#.##");
            						float j = Float.valueOf(twoDForm.format(price).replaceAll(",", "."));
            						Price p;
            						if(d == -1) p = new Price(i);
            						else p = new Price(i, d);
            						tempShop.setPrice(p, j);
            						sender.sendMessage(ChatColor.GREEN + LangPack.PRICEFOR + Material.getMaterial(i) + (d>-1?"("+d+") ":"") + LangPack.SETTO + j + rs.unit);
            						if(args[ii].split(":").length > 3){//Also set min max
            							String m[] = new String[]{twoDForm.format(Float.parseFloat(args[ii].split(":")[jj+1])).replaceAll(",", ".")
            									,twoDForm.format(Float.parseFloat(args[ii].split(":")[jj+2])).replaceAll(",", ".")};
            							tempShop.setMinMax(p, Float.parseFloat(m[0]), Float.parseFloat(m[1]));
                    					sender.sendMessage(ChatColor.GREEN + "Set minimal and maximal prices for " + Material.getMaterial(i));//TODO langpack
            						}
            						return true;
            					} catch (NumberFormatException e) {
            						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + rs.unit);
            					} catch (ArrayIndexOutOfBoundsException e){
            						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + rs.unit);
            					}
            				} else if(args[0].equalsIgnoreCase("del")){
            					try {
            						Price tempP = new Price(args[ii]);
            						if(tempShop.hasPrice(tempP)){
            							tempShop.removePrice(tempP);
            							sender.sendMessage(ChatColor.RED + LangPack.REMOVEDPRICEFOR + Material.getMaterial(tempP.getType()) + (tempP.getData()>-1?"("+tempP.getData()+") ":""));
            							return true;
            						} else {
           								sender.sendMessage(ChatColor.RED + LangPack.COULDNTFINDPRICEFOR + Material.getMaterial(tempP.getType()) + (tempP.getData()>-1?"("+tempP.getData()+") ":""));
           							}
            					} catch (NumberFormatException e) {
            						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_);
            					}
            				} else if(args[0].equalsIgnoreCase("copy")){
            					try {
            						if((args.length == 3 && shop.equals(args[1])) || (args.length == 2 && !shop.equals(args[1]))){//If copy from store
            							if(rs.shopMap.containsKey(args[args.length - 1])){
            								tempShop.clonePrices(args[args.length - 1]);
            								sender.sendMessage(ChatColor.GREEN + "Old prices replaced with prices from " + args[args.length - 1]);//TODO langpack
            								return true;
            							}
            						} else {
            							tempShop.clonePrices(null);
        								sender.sendMessage(ChatColor.GREEN + "Old prices replaced with the lowest price of every item in every store.");//TODO langpack
        								return true;
            						}
            					} catch (NumberFormatException e) {
            						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_);
            					}
            				} else if(args[0].equalsIgnoreCase("clear")){
            					tempShop.clearPrices();
            					sender.sendMessage(ChatColor.GREEN + "Cleared all prices for " + shop);//TODO langpack
            					return true;
            				} else if(args[0].equalsIgnoreCase("defaults")){
            					if(RealShopping.defPrices != null && !RealShopping.defPrices.isEmpty()){
                					tempShop.setPrices(RealShopping.defPrices);
                					sender.sendMessage(ChatColor.GREEN + "Set default prices for " + shop);//TODO new langpack
            						return true;
            					} else sender.sendMessage(ChatColor.RED + "There are no default prices. Use /rsimport to import them, or see the plugin page for help.");//TODO new langpack
            				} else if(args[0].equalsIgnoreCase("showminmax")){
            					int item = Integer.parseInt(args[ii]);
            					Price p = new Price(item);
            					if(tempShop.hasMinMax(p)){
            						sender.sendMessage(ChatColor.GREEN + "Store " + shop + " has a minimal price of " + tempShop.getMin(p) + LangPack.UNIT//TODO langpack
            							+ " and a maximal price of " + tempShop.getMax(p) + LangPack.UNIT + " for " + Material.getMaterial(item));//TODO langpack
            					} else sender.sendMessage(ChatColor.GREEN + "Store " + shop + " doesn't have a minimal and maximal price for " + Material.getMaterial(item));//TODO langpack
            					return true;
            				} else if(args[0].equalsIgnoreCase("clearminmax")){
            					int item = Integer.parseInt(args[ii]);
            					if(tempShop.hasMinMax(new Price(item))){
            						tempShop.clearMinMax(new Price(item));
            						sender.sendMessage(ChatColor.GREEN + "Cleared minimal and maximal prices for " + Material.getMaterial(item));//TODO langpack
            					} else sender.sendMessage(ChatColor.GREEN + "Store " + shop + " didn't have a minimal and maximal price for " + Material.getMaterial(item));//TODO langpack
            					return true;
            				} else if(args[0].equalsIgnoreCase("setminmax")){
            					try {
            						String[] s = args[ii].split(":");
            						if(s.length == 3){
                    					int item = Integer.parseInt(s[0]);
                    					DecimalFormat twoDForm = new DecimalFormat("#.##");
                    					tempShop.setMinMax(new Price(item), Float.valueOf(twoDForm.format(Float.valueOf(s[1])).replaceAll(",", ".")), Float.valueOf(twoDForm.format(Float.valueOf(s[2])).replaceAll(",", ".")));
                    					sender.sendMessage(ChatColor.GREEN + "Set minimal and maximal prices for " + Material.getMaterial(item));//TODO langpack
                    					return true;
            						} else sender.sendMessage(ChatColor.RED + args[ii] + " is not a proper argument.");//TODO langpack
            					} catch (NumberFormatException e) {
            						sender.sendMessage(ChatColor.RED + args[ii] + " is not a proper argument.");//TODO langpack
            					}
            				}
            				
            			} else sender.sendMessage(ChatColor.RED + LangPack.YOUARENTPERMITTEDTOEMANAGETHISSTORE);
    				} else {
    					sender.sendMessage(ChatColor.RED + shop + LangPack.DOESNTEXIST);
    				}
        		}
    		}
    	} else if(cmd.getName().equalsIgnoreCase("rssetchests")){
    		if(player != null){
    			if(rs.PInvMap.containsKey(player.getName())){
    				Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
    				Shop tempShop = rs.shopMap.get(rs.PInvMap.get(player.getName()).getStore());
    				if(tempShop.getOwner().equals("@admin")){
            			if (args.length == 1 && args[0].equalsIgnoreCase("create")){
            				if(tempShop.addChest(l)){
            					player.sendMessage(ChatColor.RED + LangPack.CHESTCREATED);
            					rs.updateEntrancesDb();
            				}
            				else player.sendMessage(ChatColor.RED + LangPack.ACHESTALREADYEXISTSONTHISLOCATION);
            				return true;
            			} else if (args.length == 1 && args[0].equalsIgnoreCase("del")){
            				if(tempShop.delChest(l)){
            					player.sendMessage(ChatColor.RED + LangPack.CHESTREMOVED);
            					rs.updateEntrancesDb();
            				}
            				else player.sendMessage(ChatColor.RED + LangPack.COULDNTFINDCHESTONTHISLOCATION);
            				return true;
            			} else if (args.length == 2 && args[0].equalsIgnoreCase("additems")){
            				try {
            					int[][] ids = new int[args[1].split(",").length][2];
            					for(int i = 0;i < args[1].split(",").length && i < 27;i++){
    								if(args[1].split(",")[i].contains(":")){
    									ids[i][0] = Integer.parseInt(args[1].split(",")[i].split(":")[0]);
    									ids[i][1] = Integer.parseInt(args[1].split(",")[i].split(":")[1].trim());
    								} else {
    									ids[i][0] = Integer.parseInt(args[1].split(",")[i]);
    									ids[i][1] = 0;
    								}
            					}
            					int j = tempShop.addChestItem(l, ids);
            					if(j > -1){
            						sender.sendMessage(ChatColor.RED + LangPack.ADDED + j + LangPack.ITEMS);
            						rs.updateEntrancesDb();
            						return true;
            					} else {
            						sender.sendMessage(ChatColor.RED + LangPack.THISCHESTDOESNTEXIST);
            					}
            				} catch (NumberFormatException e){
            					sender.sendMessage(ChatColor.RED + LangPack.ONEORMOREOFTHEITEMIDSWERENOTINTEGERS + args[1]);
            				}
            			} else if (args.length == 2 && args[0].equalsIgnoreCase("delitems")){
            				try {
            					int[][] ids = new int[args[1].split(",").length][2];
            					for(int i = 0;i < args[1].split(",").length;i++){
    								if(args[1].split(",")[i].contains(":")){
    									ids[i][0] = Integer.parseInt(args[1].split(",")[i].split(":")[0].trim());
    									ids[i][1] = Integer.parseInt(args[1].split(",")[i].split(":")[1].trim());
    								} else {
    									ids[i][0] = Integer.parseInt(args[1].split(",")[i].trim());
    									ids[i][1] = 0;
    								}
            					}
            					int j = tempShop.delChestItem(l, ids);
            					if(j > -1){
            						sender.sendMessage(ChatColor.RED + LangPack.REMOVED + j + LangPack.ITEMS);
            						rs.updateEntrancesDb();
            						return true;
            					} else {
            						sender.sendMessage(ChatColor.RED + LangPack.THISCHESTDOESNTEXIST);
            					}
            				} catch (NumberFormatException e){
            					sender.sendMessage(ChatColor.RED + LangPack.ONEORMOREOFTHEITEMIDSWERENOTINTEGERS + args[1]);
            				}
            			}
    				} else sender.sendMessage(ChatColor.RED + LangPack.ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS);
    			} else  sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTORETOUSETHISCOMMAND);
    		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rssetstores")){//Set player owned stores
    		if(player != null){
    			if (args.length == 1 && args[0].equalsIgnoreCase("entrance")){
    				String s = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
    				rs.playerEntrances.put(player.getName(),s);
    				player.sendMessage(ChatColor.RED + LangPack.ENTRANCEVARIABLESETTO + s);
    				return true;
    			} else if (args.length == 1 && args[0].equalsIgnoreCase("exit")){
    				String s = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
    				rs.playerExits.put(player.getName(), s);
    				player.sendMessage(ChatColor.RED + LangPack.EXITVARIABLESETTO + s);
    				return true;
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("createstore")){
    				if(rs.playerEntrances.containsKey(player.getName())){
        				if(rs.playerExits.containsKey(player.getName())){
        					if(args[1].equals("help")){
        						sender.sendMessage(ChatColor.RED + "You can't name a store that.");//TODO langpack
        						return true;
        					}
        			    	if(!rs.shopMap.containsKey(args[1])){//Create
            					if(RSEconomy.getBalance(player.getName()) < Config.getPstorecreate()) {
            						player.sendMessage(ChatColor.RED + LangPack.CREATINGASTORECOSTS + Config.getPstorecreate() + rs.unit);
            						return true;
            					} else {
            						RSEconomy.withdraw(player.getName(), Config.getPstorecreate());
            						rs.shopMap.put(args[1], new Shop(args[1], player.getWorld().getName(), player.getName()));
            					}
        			    	}
        			    	if(rs.shopMap.get(args[1]).getOwner().equals(player.getName())){
        			    		//Add entrance
        			    		String[] entr = rs.playerEntrances.get(player.getName()).split(",");
        			    		String[] ext = rs.playerExits.get(player.getName()).split(",");
        			    		Location en = new Location(rs.getServer().getWorld(rs.shopMap.get(args[1]).getWorld()), Integer.parseInt(entr[0]),Integer.parseInt(entr[1]), Integer.parseInt(entr[2]));
        			    		Location ex = new Location(rs.getServer().getWorld(rs.shopMap.get(args[1]).getWorld()), Integer.parseInt(ext[0]),Integer.parseInt(ext[1]), Integer.parseInt(ext[2]));
        			    		rs.shopMap.get(args[1]).addE(en, ex);
        			    		rs.updateEntrancesDb();
        			    		player.sendMessage(ChatColor.GREEN + args[1] + LangPack.WASCREATED);
        			    	} else {
        			    		player.sendMessage(ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE);
        			    	}
        					return true;
        				} else {
        					player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
        				}
    				} else player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("delstore")){
    				if(rs.shopMap.containsKey(args[1])){
    					if(rs.shopMap.get(args[1]).getOwner().equals(player.getName())){
    						if(rs.getPlayersInStore(args[1].toLowerCase())[0].equals("")){
    							rs.shopMap.remove(args[1]);
        						player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
        						rs.updateEntrancesDb();
    						} else player.sendMessage(ChatColor.RED + LangPack.STORENOTEMPTY);
    					} else {
    						player.sendMessage(ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE);
    					}
    					return true;
    				} else player.sendMessage(ChatColor.RED + args[1] + LangPack.WASNTFOUND);
    			}
    		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rsset")){
    		if(player != null){
    			if (args.length == 1 && args[0].equalsIgnoreCase("entrance")){
    				rs.entrance = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
    				player.sendMessage(ChatColor.RED + LangPack.ENTRANCEVARIABLESETTO + rs.entrance);
    				return true;
    			} else if (args.length == 1 && args[0].equalsIgnoreCase("exit")){
    				rs.exit = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
    				player.sendMessage(ChatColor.RED + LangPack.EXITVARIABLESETTO + rs.exit);
    				return true;
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("createstore")){
					if(args[1].equals("help")){
						sender.sendMessage(ChatColor.RED + "You can't name a store that.");//TODO langpack
						return true;
					}
    				if(!rs.entrance.equals("")){
        				if(!rs.exit.equals("")){
        			    	if(!rs.shopMap.containsKey(args[1])){//Create
        			    		rs.shopMap.put(args[1], new Shop(args[1], player.getWorld().getName(), "@admin"));
        			    	}
        			    	//Add entrance
        			    	Location en = new Location(rs.getServer().getWorld(rs.shopMap.get(args[1]).getWorld()), Integer.parseInt(rs.entrance.split(",")[0]),Integer.parseInt(rs.entrance.split(",")[1]), Integer.parseInt(rs.entrance.split(",")[2]));
        			    	Location ex = new Location(rs.getServer().getWorld(rs.shopMap.get(args[1]).getWorld()), Integer.parseInt(rs.exit.split(",")[0]),Integer.parseInt(rs.exit.split(",")[1]), Integer.parseInt(rs.exit.split(",")[2]));
        			    	rs.shopMap.get(args[1]).addE(en, ex);
        			    	rs.updateEntrancesDb();
        					player.sendMessage(ChatColor.RED + args[1] + LangPack.WASCREATED);
        					return true;
        				} else player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
    				} else player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("delstore")){
    				if(rs.shopMap.containsKey(args[1])){
						if(rs.getPlayersInStore(args[1].toLowerCase())[0].equals("")){
							rs.shopMap.remove(args[1]);
    						player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
    						rs.updateEntrancesDb();
						} else player.sendMessage(ChatColor.RED + LangPack.STORENOTEMPTY);
    					return true;
    				} else player.sendMessage(ChatColor.RED + args[1] + LangPack.WASNTFOUND);
    			}
    		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rsshipped")){
    		if(player != null){
    			if(args.length == 0){
        			if(rs.shippedToCollect.containsKey(player.getName())){
        				int toClaim = rs.shippedToCollect.get(player.getName()).size();
        				if(toClaim != 0) sender.sendMessage(ChatColor.GREEN + LangPack.YOUHAVEPACKAGESWITHIDS_ + toClaim + LangPack.TOPICKUP);
        				else sender.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEANYPACKAGESTOPICKUP);
        				return true;
        			} else sender.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEANYPACKAGESTOPICKUP);
    			} else if(args.length == 1 && args[0].equalsIgnoreCase("collect")){
    				Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
    				return rs.collectShipped(l, player, 1);
   				} else if(args.length == 1 && args[0].equalsIgnoreCase("inspect")){
    				sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOSPECIFYTHEID_);
    			} else if(args.length == 2 && args[0].equalsIgnoreCase("collect")){
    				Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
    				try {
    					return rs.collectShipped(l, player, Integer.parseInt(args[1]));
    				} catch (NumberFormatException e){
    					sender.sendMessage(ChatColor.RED + args[1] + LangPack.ISNOTANINTEGER);
    				}
    			} else if(args.length == 2 && args[0].equalsIgnoreCase("inspect")){
        			if(rs.shippedToCollect.containsKey(player.getName())){
        				try {
        					ShippedPackage tempSP = rs.shippedToCollect.get(player.getName()).get(Integer.parseInt(args[1]) - 1);
        					sender.sendMessage(ChatColor.GREEN + LangPack.PACKAGESENT + new Date(tempSP.getDateSent()) + LangPack.FROM
        							+ tempSP.getLocationSent().getBlockX() + "," + tempSP.getLocationSent().getBlockY() + "," + tempSP.getLocationSent().getBlockZ()
        							+ LangPack.INWORLD + tempSP.getLocationSent().getWorld().getName());
            				String str = rs.formatItemStackToMess(tempSP.getContents());
            				sender.sendMessage(ChatColor.GREEN + LangPack.THECONTENTSOFTHEPACKAGEARE + str);
            				return true;
        				} catch (ArrayIndexOutOfBoundsException e){
        					sender.sendMessage("ArrayIndexOutOfBoundsException");
        				} catch (IndexOutOfBoundsException e){
        					sender.sendMessage(ChatColor.RED + LangPack.THERESNOPACKAGEWITHTHEID + args[1]);
        				} catch (NumberFormatException e){
        					sender.sendMessage(ChatColor.RED + args[1] + LangPack.ISNOTANINTEGER);
        				}
        			} else sender.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEANYPACKAGESTOPICKUP);
    			}
    		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rstplocs")){
    		if(player != null){
    			if (args.length == 1){
    				if(args[0].equalsIgnoreCase("setwhitelistmode")){
    					if(!rs.tpLocBlacklist){
    						player.sendMessage(ChatColor.RED + LangPack.WHITELISTMODEALREADYSET);
    					} else {
    						rs.tpLocBlacklist = false;
    						player.sendMessage(ChatColor.GREEN + LangPack.SETWHITELISTMODE);
    						return true;
    					}
    				} else if(args[0].equalsIgnoreCase("setblacklistmode")){
    					if(rs.tpLocBlacklist){
    						player.sendMessage(ChatColor.RED + LangPack.BLACKLISTMODEALREADYSET);
    					} else {
    						rs.tpLocBlacklist = true;
    						player.sendMessage(ChatColor.GREEN + LangPack.SETBLACKLISTMODE);
    						return true;
    					}
    				} else if(args[0].equalsIgnoreCase("remove")){
    					if(rs.forbiddenTpLocs.containsKey(player.getLocation().getBlock().getLocation())){
    						rs.forbiddenTpLocs.remove(player.getLocation().getBlock().getLocation());
    						player.sendMessage(ChatColor.GREEN + LangPack.REMOVEDONEOFTHE + ((rs.tpLocBlacklist)?"FORBIDDEN":"ALLOWED") + LangPack.TELEPORTLOCATIONS);
    						return true;
    					} else {
    						player.sendMessage(ChatColor.RED + LangPack.THEREISNO + ((rs.tpLocBlacklist)?"FORBIDDEN":"ALLOWED") + LangPack.TELEPORTLOCATIONWITHITSCENTERHERE);
    					}
    				} else if(args[0].equalsIgnoreCase("highlight")){
    						Location[] toHighlight = rs.getNearestTpLocs(player.getLocation().getBlock().getLocation(), 5);
    						if(toHighlight != null){
    							for(Location l:toHighlight){
    								Byte dB;
    								int radius = rs.forbiddenTpLocs.get(l);
    								if(radius <= 1) dB = 0;
    								else if(radius <= 5) dB = 1;
    								else if(radius <= 10) dB = 2;
    								else if(radius <= 15) dB = 3;
    								else if(radius <= 25) dB = 4;
    								else if(radius <= 35) dB = 5;
    								else if(radius <= 50) dB = 6;
    								else if(radius <= 75) dB = 7;
    								else if(radius <= 100) dB = 8;
    								else if(radius <= 125) dB = 9;
    								else if(radius <= 150) dB = 10;
    								else if(radius <= 175) dB = 11;
    								else if(radius <= 200) dB = 12;
    								else if(radius <= 250) dB = 13;
    								else if(radius <= 500) dB = 14;
    								else dB = 15;
    								player.sendBlockChange(l, Material.WOOL, dB);
    								/* White Wool		0	r=1
    								 * Light Gray Wool 	1	1<=r<5
    								 * Gray Wool 		2	5<=r<10
    								 * Black Wool 		3	10<=r<15
    								 * Red Wool 		4	15<=r<25
    								 * Orange Wool 		5	25<=r<35
    								 * Yellow Wool 		6	35<=r<50
    								 * Lime Wool 		7	50<=r<75
    								 * Green Wool 		8	75<=r<100
    								 * Cyan Wool 		9	100<=r<125
    								 * Light Blue 		10	125<=r<150
    								 * Blue Wool 		11	150<=r<175
    								 * Purple Wool 		12	175<=r<200
    								 * Magenta Wool 	13	200<=r<250
    								 * Pink Wool 		14	250<=r<500
    								 * Brown Wool		15	500<=r
    								 */
    							}
    							blockUpdater bU = new blockUpdater(toHighlight, player);
    							bU.start();
    							
    							player.sendMessage(ChatColor.GREEN + "Highlighted 5 locations for 5 seconds.");//TODO langpack
    						} else {
    							player.sendMessage(ChatColor.RED + "No locations to highlight.");//TODO langpack
    						}

    						return true;
    				}
    			} else if(args.length == 2 && args[0].equalsIgnoreCase("add")){
    				try {
    					int radius = Integer.parseInt(args[1]);
    					if(rs.forbiddenTpLocs.containsKey(player.getLocation().getBlock().getLocation())){
    						player.sendMessage(ChatColor.GREEN + LangPack.OLDRADIUSVALUE + rs.forbiddenTpLocs.put(player.getLocation().getBlock().getLocation(), radius) + LangPack.REPLACEDWITH + radius);
    					} else {
    						rs.forbiddenTpLocs.put(player.getLocation().getBlock().getLocation(), radius);
    						player.sendMessage(ChatColor.GREEN + LangPack.ADDED + ((rs.tpLocBlacklist)?"FORBIDDEN":"ALLOWED") + LangPack.TELEPORTLOCATIONWITHARADIUSOF + radius);
    					}
    					return true;
    				} catch (NumberFormatException e){
    					sender.sendMessage(ChatColor.RED + args[1] + LangPack.ISNOTANINTEGER);
    				}
    			}
    		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE); 
    	} else if(cmd.getName().equalsIgnoreCase("rsprotect")){
    		if(player != null){
    			if(rs.PInvMap.containsKey(player.getName())){
    				if(args.length == 1 & args[0].equalsIgnoreCase("add")){				
   						Shop tempShop = rs.shopMap.get(rs.PInvMap.get(player.getName()).getStore());
       					BlockState bs = player.getLocation().getBlock().getState();
       					if(bs instanceof Chest | bs instanceof DoubleChest){
       						if(tempShop.isProtectedChest(bs.getLocation())){
       							player.sendMessage(ChatColor.GREEN + "This chest is already protected.");//TODO langpack
       							return true;
       						} else {
       							tempShop.addProtectedChest(bs.getLocation());
       							player.sendMessage(ChatColor.GREEN + "Made chest protected.");//TODO langpack
       							return true;
        					}
        				} else {
        					player.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUARESTANDINGONISNTACHEST);
        				}
    				} else if(args.length == 1 & args[0].equalsIgnoreCase("remove")){
   						Shop tempShop = rs.shopMap.get(rs.PInvMap.get(player.getName()).getStore());
       					BlockState bs = player.getLocation().getBlock().getState();
       					if(tempShop.isProtectedChest(bs.getLocation())){
       						tempShop.removeProtectedChest(bs.getLocation());
       						player.sendMessage(ChatColor.GREEN + "Unprotected chest.");//TODO langpack
       						return true;
    					} else {
       						player.sendMessage(ChatColor.RED + "This chest isn't protected.");//TODO langpack
       						return true;
    					}
    				}
    			} else player.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
    		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE); 
    	} else if(cmd.getName().equalsIgnoreCase("rsunjail")){
    		if (args.length == 1){
    			if(rs.jailedPlayers.containsKey(args[0])){
    				Player[] pla = rs.getServer().getOnlinePlayers();
    				Player jailee = null;
    				for(Player p:pla){
    					if(p.getName().equals(args[0])){
    						jailee = p;
    						break;
    					}
    				}
    				if(jailee != null){
        				jailee.teleport(rs.jailedPlayers.get(args[0])); 
        				rs.jailedPlayers.remove(args[0]);
        				jailee.sendMessage(LangPack.YOUARENOLONGERINJAIL);
        				sender.sendMessage(LangPack.UNJAILED + args[0]);
        				return true;
    				} else {
    					sender.sendMessage(args[0] + LangPack.ISNOTONLINE);
    				}
    			} else sender.sendMessage(args[0] + LangPack.ISNOTJAILED);
    		}
    	} else if(cmd.getName().equalsIgnoreCase("rsupdate")){
    		if(Config.getAutoUpdate() > 0){
    			if(args.length == 1 && args[0].equals("info")){
    				if(!rs.newUpdate.equals("")){
        				if((player != null && Config.getAutoUpdate() > 1) || player == null){//Permission to get info
        					String mess = rs.updater.getLatestVersionDescription();
        					mess = mess.replace("<li>", "• ");
        					mess = mess.replace("</li>", "\n");
        					mess = mess.replace("<p>", "");
        					mess = mess.replace("</p>", "\n"); 
        					mess = mess.replace("<ul>", "");
        					mess = mess.replace("</ul>", "");
        					mess = mess.replace("<em>", "");
        					mess = mess.replace("</em>", "");
        					mess = mess.replace("<strong>", "");
        					mess = mess.replace("</strong>", "");
        					mess = mess.replace("</span>", "");
        					mess = mess.replace("</a>", "");
        					while(mess.contains("<span") && mess.contains(">")){
        						String temp1 = mess.substring(0, mess.indexOf("<span"));
        						String temp2 = mess.substring(mess.indexOf(">") + 1, mess.length());
        						mess = temp1 + temp2;
        					}
        					while(mess.contains("<a") && mess.contains(">")){
        						String temp1 = mess.substring(0, mess.indexOf("<a"));
        						String temp2 = mess.substring(mess.indexOf(">") + 1, mess.length());
        						mess = temp1 + temp2;
        					}
        					if(player == null) sender.sendMessage(mess);
        					else {
        						player.sendMessage(ChatColor.GREEN + "Reading description...");//TODO langpack
    							messageSender mS = new messageSender(player, mess.split("\\n"), 2000);
    							mS.start();
        					}
        					return true;
        				}
    				} else {
    					sender.sendMessage(ChatColor.RED + "This is the newest version.");//TODO langpack
    					return true;
    				}
    			} else if(args.length == 1 && args[0].equals("update")){
    				if(!rs.newUpdate.equals("")){
        				if((player != null && Config.getAutoUpdate() == 4) || ( player == null && Config.getAutoUpdate() > 2)){//Permission to update
        					rs.updater = new Updater(rs, "realshopping", rs.getPFile(), Updater.UpdateType.DEFAULT, true);
        					if(rs.updater.getResult() == Updater.UpdateResult.SUCCESS)
        						sender.sendMessage(ChatColor.GREEN + "Successful update!");//TODO langpack
        					else
        						sender.sendMessage(ChatColor.RED + "Update failed.");//TODO langpack
        					return true;
        				}
    				} else{
    					sender.sendMessage(ChatColor.RED + "This is the newest version.");//TODO langpack
    					return true;
    				}
    			} else return false;
    		}
    		sender.sendMessage(ChatColor.RED + "You aren't permitted to use this command.");//TODO langpack
    	} else if(cmd.getName().equalsIgnoreCase("realshopping")){
    		byte pg = 1;
    		if(args.length > 0) try {
    			pg = Byte.parseByte(args[0]);
    			if(pg < 1 || pg > 3){
    				sender.sendMessage(ChatColor.RED + LangPack.THEREARENTTHATMANYPAGES);
    				return false;
    			}
			} catch (NumberFormatException e){
				sender.sendMessage(ChatColor.RED + args[1] + LangPack.ISNOTANINTEGER);
				return false;
			}
    		int i = 0;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "RealShopping [v0.42] - A shop plugin for Bukkit made by kuben0");i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "Loaded config settings:");i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "enable-automatic-updates:"+Config.getAutoUpdateStr(Config.getAutoUpdate()));i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "auto-protect-chests:"+Config.isAutoprotect());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "delivery-cost-zones:"+Config.getDeliveryZones());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "language-pack:"+Config.getLangpack());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "enable-selling-to-stores:"+Config.isEnableSelling());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "disable-item-drop:"+Config.isDisableDrop());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "disable-crafting:"+Config.isDisableCrafting());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "disable-buckets:"+Config.isDisableBuckets());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "disable-ender-chests:"+Config.isDisableEnderchests());i++;
    		if(i >= (pg-1)*10 && i < pg*10) {
    			String tempStr = "";
    			if(Config.getCartEnabledW().contains("@all")) tempStr = "Enabled in all worlds";
    			else {
        			boolean j = true;
        			for(String str:Config.getCartEnabledW()){
        				if(j){
       						tempStr += str;
       						j = false;
       					}
        				else tempStr += "," + str;
        			}
    			}
    			sender.sendMessage(ChatColor.GREEN + "enable-shopping-carts-in-worlds:" + tempStr);i++;
    		}
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "player-stores-create-cost:"+Config.getPstorecreate());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "drop-items-at:"+Config.getDropLoc().getWorld().getName()+";"+Config.getDropLoc().getBlockX()+","+Config.getDropLoc().getBlockY()+","+Config.getDropLoc().getBlockZ());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "hell-location:"+Config.getHellLoc().getWorld().getName()+";"+Config.getHellLoc().getBlockX()+","+Config.getHellLoc().getBlockY()+","+Config.getHellLoc().getBlockZ());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "jail-location:"+Config.getJailLoc().getWorld().getName()+";"+Config.getJailLoc().getBlockX()+","+Config.getJailLoc().getBlockY()+","+Config.getJailLoc().getBlockZ());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "keep-stolen-items-after-punish:"+Config.isKeepstolen());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "punishment:"+Config.getPunishment());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "enable-automatic-store-management:"+Config.isEnableAI());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "stat-updater-frequency:"+Config.getTimeString(Config.getUpdateFreq()));i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "statistics-timespan:"+Config.getTimeString(Config.getStatTimespan()));i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "clean-stats-older-than:"+Config.getTimeString(Config.getCleanStatsOld()));i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "allow-filling-chests:"+Config.isAllowFillChests());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "notificatior-update-frequency:"+Config.getNotTimespan());i++;
    		if(pg < 3) sender.sendMessage(ChatColor.DARK_PURPLE + "realshopping " + (pg + 1) + " for more.");
       		return true;
    	} else if(cmd.getName().equalsIgnoreCase("rsimport")){
			importPrices(player);
			return true;
		}
		} catch(Exception e){
			//Nothing
		}
    	return false;
	}
	
    public static boolean prices(CommandSender sender, int page, String store, boolean cmd){
    	if(RealShopping.shopMap.get(store).hasPrices()){
    		Map<Price, Float> tempMap = RealShopping.shopMap.get(store).getPrices();
 			if(!tempMap.isEmpty()){
 				Price[] keys = tempMap.keySet().toArray(new Price[0]);
 				if(page*9 < keys.length){//If page exists
// 					boolean SL = false;
 					if(RealShopping.shopMap.get(store).hasSales()){
 						sender.sendMessage(ChatColor.GREEN + LangPack.THEREISA + RealShopping.shopMap.get(store).getFirstSale() + LangPack.PCNTOFFSALEAT + store);
// 						SL = true;
 					}
 					if((page+1)*9 < keys.length){//Not last
 		 				for(int i = 9*page;i < 9*(page+1);i++){
 		 					float cost = tempMap.get(keys[i]);
 		 					String onSlStr = "";
 		 					if(RealShopping.shopMap.get(store).hasSale(keys[i])){//There is a sale on that item.
 		 						int pcnt = 100 - RealShopping.shopMap.get(store).getSale(keys[i]);
 		 						cost *= pcnt;
 		 						cost = Math.round(cost);
 		 						cost /= 100;
 		 						onSlStr = ChatColor.GREEN + LangPack.ONSALE;
 		 					}
 		 					sender.sendMessage(ChatColor.BLUE + "" + keys[i] + " " + Material.getMaterial(keys[i].getType()) + ChatColor.BLACK + " - " + ChatColor.RED + cost + LangPack.UNIT + onSlStr);
 		 				}
 		 				sender.sendMessage(ChatColor.RED + LangPack.MOREITEMSONPAGE + (page + 2));
 					} else {//Last page
 		 				for(int i = 9*page;i < keys.length;i++){
 		 					float cost = tempMap.get(keys[i]);
 		 					String onSlStr = "";
 		 					if(RealShopping.shopMap.get(store).hasSale(keys[i])){//There is a sale on that item.
 		 						int pcnt = 100 - RealShopping.shopMap.get(store).getSale(keys[i]);
 		 						cost *= pcnt;
 		 						cost = Math.round(cost);
 		 						cost /= 100;
 		 						onSlStr = ChatColor.GREEN + LangPack.ONSALE;
 		 					}
 		 					sender.sendMessage(ChatColor.BLUE + "" + keys[i] + " " + Material.getMaterial(keys[i].getType()) + ChatColor.BLACK + " - " + ChatColor.RED + cost + LangPack.UNIT + onSlStr);
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
	
	private boolean collect(CommandSender sender, Player player, Shop tempShop, boolean cFlag, int amount){
		if(player != null){
			if(!tempShop.getOwner().equalsIgnoreCase("@admin")){
				if(cFlag){
					if(Config.isAllowFillChests()){
						if(!rs.PInvMap.containsKey(player.getName()) || tempShop.getOwner().equals(player.getName())){
							if(player.getLocation().getBlock().getState() instanceof Chest){
								if(tempShop.hasStolenToClaim()){
									if(amount == 0 || amount > 27) amount = 27;
									ItemStack[] tempIs = new ItemStack[27];
									int i = 0;
									for(;i < amount;i++){
										tempIs[i] = tempShop.claimStolenToClaim();
										if(tempIs[i] == null) break;
									}
									ItemStack[] oldCont = ((Chest)player.getLocation().getBlock().getState()).getBlockInventory().getContents();
									for(ItemStack tempIS:oldCont) if(tempIS != null) player.getWorld().dropItem(player.getLocation(), tempIS);
									((Chest)player.getLocation().getBlock().getState()).getBlockInventory().setContents(tempIs);
									player.sendMessage(ChatColor.GREEN + LangPack.FILLEDCHESTWITH + i + LangPack.ITEMS);
									return true;
								} else sender.sendMessage(ChatColor.RED + LangPack.NOTHINGTOCOLLECT);
							} else sender.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUARESTANDINGONISNTACHEST);
						} else sender.sendMessage(ChatColor.RED + "You can't collect your items to a chest in a store you do not own.");//TODO langpack
					} else {
						sender.sendMessage(ChatColor.RED + "You can't collect your items to a chest on this server.");//TODO langpack
						return true;
					}
				} else {
					int i = 0;
					for(;amount == 0 || i < amount;i++){
						ItemStack tempIs = tempShop.claimStolenToClaim();
						if(tempIs != null) player.getWorld().dropItem(player.getLocation(), tempIs);
						else break;
					}
					player.sendMessage(ChatColor.GREEN + LangPack.DROPPED + i + LangPack.ITEMS);
				}
			}
		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
		return false; 
	}

	private void importPrices(Player player){
	    final Map<Object, Object> convMap = new HashMap<Object, Object>();
	    convMap.put("data", "first");
	    Conversation conv = RealShopping.convF.withFirstPrompt(new ImportPrompt()).withPrefix(
	    	new ConversationPrefix() {
	    		public String getPrefix(ConversationContext arg0){
	    			return ChatColor.LIGHT_PURPLE + "[RealShopping]" + ChatColor.WHITE + " ";
            	}
            }).withTimeout(30).withInitialSessionData(convMap).withLocalEcho(false).buildConversation(player);
	    conv.addConversationAbandonedListener(
	    		new ConversationAbandonedListener() {
	    			public void conversationAbandoned(ConversationAbandonedEvent event){
	    				if (event.gracefulExit()){
	    					((Player)((Conversation)event.getSource()).getForWhom()).sendRawMessage(ChatColor.LIGHT_PURPLE + "[RealShopping] " + ChatColor.WHITE + "Quit conversation.");//TODO new langpack
	    				}
	    			}
	    		});
	    conv.begin();
	}
}

class ImportPrompt extends ValidatingPrompt {
 
    public String getPromptText(ConversationContext context) {
    	String in = (String) context.getSessionData("data");
    	if(in.equals("first")){
    		context.setSessionData("file", "murzyn");
    		String out = "Which file do you want to import a set of default prices from?";//TODO new langpack
    		File dirP = new File(RealShopping.mandir);
    		File[] Mlist = null, Plist = null;
    		File dirM = new File("./");
    		if(dirP.isDirectory()){
    			Mlist = dirM.listFiles(new FilenameFilter(){
    			    public boolean accept(File dir, String name) {
    			        return (name.endsWith(".xlsx"));
    			    }
    			});
    		}
    		if(dirP.isDirectory()){
    			Plist = dirP.listFiles(new FilenameFilter(){
    			    public boolean accept(File dir, String name) {
    			        return (name.endsWith(".xlsx"));
    			    }
    			});
    		}
    		if((Mlist == null || Mlist.length == 0) && (Plist == null || Plist.length == 0))
    			return "Error: no files with the .xlsx extension found in the main directory or the RealShopping directory.";//TODO new langpack
    		if(Mlist != null && Mlist.length > 0){
    			out += ChatColor.DARK_GREEN + " In the main directory:";//TODO new langpack
    			for(int i = 1;i <= Mlist.length;i++){
    				out += " " + ChatColor.LIGHT_PURPLE + i + ")" + ChatColor.WHITE + Mlist[i-1].getName() + " ";
    	    	}
    		}
    		if(Plist != null && Plist.length > 0){
    			out += ChatColor.DARK_GREEN + " In the RealShopping directory:";//TODO new langpack
    			for(int i = 1;i <= Plist.length;i++){
        			out += " " + ChatColor.LIGHT_PURPLE + i + ")" + ChatColor.WHITE + Plist[i-1].getName() + " ";
        		}
    		}
    		out += "Type the corresponding number to choose a file or " + ChatColor.LIGHT_PURPLE + "c" + ChatColor.WHITE + " to cancel.";//TODO new langpack
    		context.setSessionData("mlist", Mlist);
    		context.setSessionData("plist", Plist);
    		return out;
    	} else {
    		int num = -1;
    		try{
    			num = Integer.parseInt((String)context.getSessionData("data"));
    		} catch (NumberFormatException e){
    			return "Error: Input is not a valid integer.";//TODO new langpack
    		}
    		if(num > 0){
        		if(context.getSessionData("mlist") != null && context.getSessionData("plist") != null){
        			File[] Mlist = (File[]) context.getSessionData("mlist");
        			File[] Plist = (File[]) context.getSessionData("plist");
        			if(num <= Mlist.length + Plist.length){
        				String chosen = "";
        				if(num <= Mlist.length) chosen = Mlist[num - 1].getPath();
        				else chosen = Plist[num -1 - Mlist.length].getPath();
        				context.setSessionData("file", chosen);
    				    context.setSessionData("final", true);
        				return ChatColor.GREEN + "Chosen file " + chosen + ". "
        						+ ChatColor.WHITE + "Type " + ChatColor.LIGHT_PURPLE + "u" + ChatColor.WHITE + " to import from the user defined prices, or " +
        						ChatColor.LIGHT_PURPLE + "p" + ChatColor.WHITE + " to import from the proposition prices.";//TODO new langpack
        			} else return "Wrong file chosen";//TODO new langpack
        		}	
    		}
    	}
        return "Error #1201";
    }
 
    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String in) {
    	if(context.getSessionData("final") != null && context.getSessionData("final").equals(true)){
    		context.setSessionData("data", in);
    		return new FinalPrompt();
    	}
        if(in.equalsIgnoreCase("stop") || in.equalsIgnoreCase("end") || in.equalsIgnoreCase("quit") || in.equalsIgnoreCase("c"))
            return END_OF_CONVERSATION;
        else context.setSessionData("data", in);
        return this;
    }
 
    @Override
    protected boolean isInputValid(ConversationContext context, String in) {
    	if(in.equalsIgnoreCase("stop") || in.equalsIgnoreCase("end") || in.equalsIgnoreCase("quit") || in.equalsIgnoreCase("c")) return true;

    	if(context.getSessionData("final") != null && context.getSessionData("final").equals(true)){
    		if(!in.equalsIgnoreCase("p") && !in.equalsIgnoreCase("u")) return false;
    	}
    	
        return true;
    }
 
}

class FinalPrompt extends MessagePrompt{

	public String getPromptText(ConversationContext context) {
    	String in = (String) context.getSessionData("data");
		if(in.equalsIgnoreCase("u") || in.equalsIgnoreCase("p")){
			if(context.getSessionData("file") != null){
	    		try {
				    InputStream inp = new FileInputStream((String)context.getSessionData("file"));
				    XSSFWorkbook wb;

					wb = new XSSFWorkbook(inp);
				    XSSFSheet sheet = wb.getSheetAt(in.equalsIgnoreCase("u")?0:2);
				    Iterator rowIter = sheet.rowIterator();
				    
				    RealShopping.defPrices.clear();
				    wb.getCreationHelper().createFormulaEvaluator().evaluateAll();
				    while(rowIter.hasNext()){
				    	try {
					    	XSSFRow row = (XSSFRow) rowIter.next();
					    	XSSFCell firstC = row.getCell(0);
					    	int ID = -1;
					    	byte data = 0;
					    	if(firstC != null) if (firstC.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){//ID's are numeric
					    		ID = (int) firstC.getNumericCellValue();
					    	} else if (firstC.getCellType() == XSSFCell.CELL_TYPE_STRING){
					    		ID = Integer.parseInt(firstC.getStringCellValue().split(";")[0]);
					    		data = Byte.parseByte(firstC.getStringCellValue().split(";")[1]);
					    	}
					    	if(ID >= 0){
					    		XSSFCell costC = row.getCell(4);
					    		try{
					    			if(costC != null && costC.getCellType() == XSSFCell.CELL_TYPE_FORMULA){
					    				Price p;
					    				if(data == 0) p = new Price(ID);
					    				else p = new Price(ID, data);
	            						DecimalFormat twoDForm = new DecimalFormat("#.##");
	            						float cost = Float.valueOf(twoDForm.format((float) costC.getNumericCellValue()).replaceAll(",", "."));
					    				Float[] f = new Float[]{cost};
					    				RealShopping.defPrices.put(p, f);
					    			}
					    		} catch (Exception e) {}
					    	}
				    	} catch (NumberFormatException e){}//Skip
			        }
					if(RealShopping.defPrices.size() > 0) return ChatColor.GREEN + "Imported " + RealShopping.defPrices.size() + " prices as default.";//TODO new langpack
					else return "error #1202";
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
    	}
		return null;
	}

	@Override
	protected Prompt getNextPrompt(ConversationContext context) {
		return END_OF_CONVERSATION;
	}
	
}

class blockUpdater extends Thread {
	private Location[] blocks;
	private Player player;

	public blockUpdater(Location[] blocks, Player player){
		this.blocks = blocks;
		this.player = player;
	}
	
	public void run(){
		try {
			Thread.sleep(5000);
			for(Location l:blocks){
				Block b = l.getWorld().getBlockAt(l);
				player.sendBlockChange(l, b.getType(), b.getData());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}

class messageSender extends Thread {
	private Player player;
	private String[] message;
	private int millis;

	public messageSender(Player player, String[] message, int millis){
		this.player = player;
		this.message = message;
		this.millis = millis;
	}
	
	public void run(){
		try {
			for(String s:message){
				Thread.sleep(millis);
				player.sendMessage(s);
			}
			player.sendMessage(ChatColor.GREEN + "Done!");//TODO new langpack
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}