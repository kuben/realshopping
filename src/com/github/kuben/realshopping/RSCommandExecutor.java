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

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.h31ix.updater.Updater;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RSCommandExecutor implements CommandExecutor {
	RealShopping rs;
	
	public RSCommandExecutor(RealShopping rs){
		this.rs = rs;
	}
	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	
		if(!RealShopping.working.equals("")){
			sender.sendMessage(ChatColor.RED + RealShopping.working);
			return false;
		}
		
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
						return rs.prices(sender, 0, rs.PInvMap.get(player.getName()).getStore(), true);
					} else {
						sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
					}
				} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
			} else if(args.length == 1){
				if(args[0].matches("[0-9]+")){
					if(player != null){
						if(rs.PInvMap.containsKey(player.getName())){
							int i = Integer.parseInt(args[0]);
							if(i > 0) return rs.prices(sender, i - 1, rs.PInvMap.get(player.getName()).getStore(), true);
							else sender.sendMessage(ChatColor.RED + LangPack.THEPAGENUMBERMUSTBE1ORHIGHER);
						} else {
							sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT);
							return true;
						}
					} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
				} else {
					return rs.prices(sender, 0, args[0], true);
				}
			} else if(args.length == 2){
				if(args[1].matches("[0-9]+")){
					return rs.prices(sender, Integer.parseInt(args[1]), args[0], true);
				} else {
					sender.sendMessage(ChatColor.RED + "" + args[1] + LangPack.ISNOTAVALIDPAGENUMBER);
				}
			}
    	} else if(cmd.getName().equalsIgnoreCase("rssell")){
    		if(player != null){
    			if(rs.PInvMap.containsKey(player.getName())){
					if(Config.enableSelling){
	    				Inventory tempInv = Bukkit.createInventory(null, 36, "Sell to store");
						player.openInventory(tempInv);
						return true;
					} else player.sendMessage(ChatColor.RED + LangPack.SELLINGTOSTORESISNOTENABLEDONTHISSERVER);
    			} else sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
    		}
    		else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rsstores")) {
    		if(args.length > 0){
    			if(args[0].equals("help")){
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
    				if(rs.shopMap.get(args[0]).owner.equalsIgnoreCase("@admin")){
    					if(player == null){
    						isOwner = true;
    					} else if(player.hasPermission("realshopping.rsset")){
    						isOwner = true;
    				}
    				} else {
    					if(player != null){
    						if(rs.shopMap.get(args[0]).owner.equalsIgnoreCase(player.getName())){
    							isOwner = true;
    						}
    					}
    				}
    			else {
    				sender.sendMessage(args[0] + LangPack.DOESNTEXIST);
    				return false;
    			}
    			
    			if(args.length == 1){
    				sender.sendMessage(ChatColor.GREEN + LangPack.STORE + args[0] + ((rs.shopMap.get(args[0]).owner.equalsIgnoreCase("@admin"))?"":LangPack.OWNEDBY + rs.shopMap.get(args[0]).owner));
    				if(rs.shopMap.get(args[0]).buyFor > 0) sender.sendMessage(ChatColor.GREEN + LangPack.BUYSFOR + rs.shopMap.get(args[0]).buyFor + LangPack.PCNTOFORIGINAL);
    				if(!rs.shopMap.get(args[0]).sale.isEmpty()) sender.sendMessage(ChatColor.GREEN + LangPack.HASA + rs.shopMap.get(args[0]).sale.values().toArray()[0] + LangPack.PCNTOFFSALERIGHTNOW);
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
    	    			if(Config.enableSelling){
    	    				try {
    	    					int pcnt = Integer.parseInt(args[2]);
    	    					if(pcnt <= 100){
    	    						if(pcnt >= 0){
    	    							rs.shopMap.get(args[0]).buyFor = pcnt;
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
    	    			if(rs.shopMap.get(args[0]).banned.contains(args[2].toLowerCase())) sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISALREADYBANNEDFROMYOURSTORE);
    	    			else {
    	    				rs.shopMap.get(args[0]).banned.add(args[2].toLowerCase());
    	    				sender.sendMessage(ChatColor.GREEN + LangPack.BANNED + args[2] + LangPack.FROMSTORE);
    	    			}
    	    			rs.updateEntrancesDb();
    	    			return true;
    	     		} else if(args.length == 3 && args[1].equalsIgnoreCase("unban")){
    	     			if(rs.shopMap.get(args[0]).banned.contains(args[2].toLowerCase())){
    	     				rs.shopMap.get(args[0]).banned.remove(args[2].toLowerCase());
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
    	     						Location l = ((Location) rs.shopMap.get(args[0]).entrance.get(0)).clone();
    	     						if(rs.shopMap.get(args[0]).sellToStore.containsKey(rs.getServer().getPlayerExact(args[2]).getName()))
    	     							rs.shopMap.get(args[0]).sellToStore.remove(rs.getServer().getPlayerExact(args[2]).getName());
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
        	     					if(rs.shopMap.get(args[0]).sellToStore.containsKey(rs.getServer().getOfflinePlayer(args[3]).getName()))
        	     						rs.shopMap.get(args[0]).sellToStore.remove(rs.getServer().getOfflinePlayer(args[3]).getName());
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
	    							if(rs.shopMap.containsKey(args[0]) && !rs.shopMap.get(args[0]).prices.isEmpty()){
	    								rs.shopMap.get(args[0]).sale.clear();
	    								Price[] keys = rs.shopMap.get(args[0]).prices.keySet().toArray(new Price[0]);
	    								int i = 0;
	    								for(;i < keys.length;i++){
	    									rs.shopMap.get(args[0]).sale.put(keys[i], pcnt);
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
	    								rs.shopMap.get(args[0]).sale.clear();
	    								int i = 0;
	    								int j = 0;
	    								for(;i < keys.length;i++){
	    									Price tempP = new Price(keys[i]);
	    									if(rs.shopMap.get(args[0]).prices.containsKey(tempP) || rs.shopMap.get(args[0]).prices.containsKey(new Price(keys[i].split(":")[0]))){
	    										rs.shopMap.get(args[0]).sale.put(tempP, pcnt);
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
    	    			rs.shopMap.get(args[0]).sale.clear();
    	    			sender.sendMessage(ChatColor.GREEN + LangPack.SALEENDED);
    	    			return true;
    	    		} else if(args[1].equals("notifications")){
    	    			if(args.length > 2){
    	    				if(args[2].equals("on")){
    	    					rs.shopMap.get(args[0]).allowNotifications = true;
    	    					sender.sendMessage(ChatColor.GREEN + "Enabled notifications for " + args[0]);
    	    				} else if(args[2].equals("off")){
    	    					rs.shopMap.get(args[0]).allowNotifications = false;
    	    					sender.sendMessage(ChatColor.GREEN + "Disabled notifications for " + args[0]);
    	    				} else {
    	    					sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTAVALIDARGUMENT);
    	    					sender.sendMessage("Usage:" + ChatColor.DARK_PURPLE + "[...] notifications [on|off]");
    	    				}
    	    				return true;
    	    			} else {
    	    				sender.sendMessage(ChatColor.GREEN + "Notifications are " + (rs.shopMap.get(args[0]).allowNotifications?"on":"off") + ".");
    	    				return true;
    	    			}
    	    		} else if(args[1].equals("onchange")){
    	    			if(Config.enableAI){
    	    				if(args.length == 2){
        	    				if(rs.shopMap.get(args[0]).notifyChanges == 1) sender.sendMessage(ChatColor.GREEN + "You will be notified if " + args[0] + " loses/gains "
        	    						+ rs.shopMap.get(args[0]).changeTreshold + " place(s).");
        	    				else if(rs.shopMap.get(args[0]).notifyChanges == 2) sender.sendMessage(ChatColor.GREEN + "The price will be lowered/increased by" + rs.shopMap.get(args[0]).changePercent
        	    						+ "% if " + args[0] + " loses/gains " + rs.shopMap.get(args[0]).changeTreshold + " place(s).");
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
            						if(args[4].contains("%")) sender.sendMessage("(Skip the %-sign)");
            					}
            					if(args[2].equals("nothing")){
            						rs.shopMap.get(args[0]).notifyChanges = 0;
            						sender.sendMessage(ChatColor.GREEN + "You won't get notified when your store " + args[0] + " becomes more or less popular.");
            						rs.updateEntrancesDb();
            						return true;
            					} else if(args[2].equals("notify")){
            						if(tresh > -1){
            							rs.shopMap.get(args[0]).notifyChanges = 1;
            							rs.shopMap.get(args[0]).changeTreshold = tresh;
                						sender.sendMessage(ChatColor.GREEN + "You will get notified when your store " + args[0] + " becomes at least " + tresh + " places more or less popular.");
                						rs.updateEntrancesDb();
            						} else
            							sender.sendMessage("Usage:" + ChatColor.DARK_PURPLE + "[...] onchange notify TRESHOLD " + ChatColor.RESET
            									+ " where TRESHOLD is how many places your store needs to lose or gain for you to be notified.");
            						return true;
        						} else if(args[2].equals("changeprices")){
        							if(tresh > -1 && pcnt > -1){
        								rs.shopMap.get(args[0]).notifyChanges = 2;
        								rs.shopMap.get(args[0]).changeTreshold = tresh;
        								rs.shopMap.get(args[0]).changePercent = pcnt;
                						sender.sendMessage(ChatColor.GREEN + "You will get notified when your store " + args[0] + " becomes at least " + ChatColor.DARK_PURPLE + tresh
                								+ " places more or less popular. And the prices will be lowered or increased by " + ChatColor.DARK_PURPLE + pcnt + "%.");
                						rs.updateEntrancesDb();
                						return true;
        							} else
            							sender.sendMessage("Usage:" + ChatColor.DARK_PURPLE + "[...] onchange changepries TRESHOLD PERCENT" + ChatColor.RESET
            									+ " where TRESHOLD is how many places your store needs to lose or gain for you to be for the changes to happen, "
            									+ "and PERCENT is how many percent the prices will be lowered or increased.");
            						return true;
        						}
            				}
    	    			} else {
    	    				sender.sendMessage(ChatColor.RED + "Automatic store management is not enabled on this server.");
    	    			}     					
    	    		}
    			} else sender.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE);
    		}
    	} else if(cmd.getName().equalsIgnoreCase("rssetprices")){
    		String shop = "";
    		int ii = 1;
    		if(args.length == 1 && player != null && rs.PInvMap.containsKey(player.getName())){
    			if(args[0].equals("copy") || args[0].equals("clear")){
    				shop = rs.PInvMap.get(player.getName()).getStore();
    			}
    		} else if(args.length == 2){
    			if(player != null){
    				if(rs.PInvMap.containsKey(player.getName())){
    					shop = rs.PInvMap.get(player.getName()).getStore();
    				} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS);
    			} else {
    				if(args[0].equals("copy") || args[0].equals("clear")){
    					shop = args[1];
    				} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE);//TODO
    			}
    		} else if(args.length == 3){
    			shop = args[1];
    			ii = 2;
    		}
    		if(!shop.equals("")){
    			if(rs.shopMap.containsKey(shop)){
        			if(player == null || (rs.shopMap.get(shop).owner.equals(player.getName()) || player.hasPermission("realshopping.rsset"))){//If player is owner OR has admin perms
        				if(args[0].equalsIgnoreCase("add")){
        					try {
        						int i = Integer.parseInt(args[ii].split(":")[0]);
        						int d = -1;
        						if(args[ii].split(":").length > 2) d = Integer.parseInt(args[ii].split(":")[1]);
        						float price = Float.parseFloat(d>-1?args[ii].split(":")[2]:args[ii].split(":")[1]);
        						DecimalFormat twoDForm = new DecimalFormat("#.##");
        						float j = Float.parseFloat(twoDForm.format(price));
        						if(d == -1) rs.shopMap.get(shop).prices.put(new Price(i), j);
        						else rs.shopMap.get(shop).prices.put(new Price(i, d), j);
        						sender.sendMessage(ChatColor.RED + LangPack.PRICEFOR + Material.getMaterial(i) + (d>-1?"("+d+") ":"") + LangPack.SETTO + j + rs.unit);
        						return true;
        					} catch (NumberFormatException e) {
        						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + rs.unit);
        					} catch (ArrayIndexOutOfBoundsException e){
        						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + rs.unit);
        					}
        				} else if(args[0].equalsIgnoreCase("del")){
        					try {
        						Price tempP = new Price(args[ii]);
        						if(rs.shopMap.get(shop).prices.containsKey(tempP)){
        							rs.shopMap.get(shop).prices.remove(tempP);
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
        								rs.shopMap.get(shop).prices = new HashMap<Price, Float>(rs.shopMap.get(args[args.length - 1]).prices);
        								sender.sendMessage(ChatColor.GREEN + "Old prices replaced with prices from " + args[args.length - 1]);
        								return true;
        							}
        						} else {
        							rs.shopMap.get(shop).prices = getLowestPrices(shop);
    								sender.sendMessage(ChatColor.GREEN + "Old prices replaced with the lowest price of every item in every store.");
    								return true;
        						}
        					} catch (NumberFormatException e) {
        						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_);
        					}
        				} else if(args[0].equalsIgnoreCase("clear")){
        					rs.shopMap.get(shop).prices.clear();
        					sender.sendMessage(ChatColor.GREEN + "Cleared all prices for " + shop);
        					return true;
        				}
        				
        			} else sender.sendMessage(ChatColor.RED + LangPack.YOUARENTPERMITTEDTOEMANAGETHISSTORE);
				} else {
					sender.sendMessage(ChatColor.RED + shop + LangPack.DOESNTEXIST);
				}
    		}
    	} else if(cmd.getName().equalsIgnoreCase("rssetchests")){
    		if(player != null){
    			if(rs.PInvMap.containsKey(player.getName())){
    				Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
    				Shop tempShop = rs.shopMap.get(rs.PInvMap.get(player.getName()).getStore());
    				if(tempShop.owner.equals("@admin")){
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
        						sender.sendMessage(ChatColor.RED + "You can't name a store that.");
        						return true;
        					}
        			    	if(!rs.shopMap.containsKey(args[1])){//Create
            					if(RSEconomy.getBalance(player.getName()) < Config.pstorecreate) {
            						player.sendMessage(ChatColor.RED + LangPack.CREATINGASTORECOSTS + Config.pstorecreate + rs.unit);
            						return true;
            					} else {
            						RSEconomy.withdraw(player.getName(), Config.pstorecreate);
            						rs.shopMap.put(args[1], new Shop(args[1], player.getWorld().getName(), player.getName()));
            					}
        			    	}
        			    	if(rs.shopMap.get(args[1]).owner.equals(player.getName())){
        			    		//Add entrance
        			    		String[] entr = rs.playerEntrances.get(player.getName()).split(",");
        			    		String[] ext = rs.playerExits.get(player.getName()).split(",");
        			    		Location en = new Location(rs.getServer().getWorld(rs.shopMap.get(args[1]).world), Integer.parseInt(entr[0]),Integer.parseInt(entr[1]), Integer.parseInt(entr[2]));
        			    		Location ex = new Location(rs.getServer().getWorld(rs.shopMap.get(args[1]).world), Integer.parseInt(ext[0]),Integer.parseInt(ext[1]), Integer.parseInt(ext[2]));
        			    		rs.shopMap.get(args[1]).addE(en, ex);
        			    		rs.updateEntrancesDb();
        			    		player.sendMessage(ChatColor.RED + args[1] + LangPack.WASCREATED);
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
    					if(rs.shopMap.get(args[1]).owner.equals(player.getName())){
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
						sender.sendMessage(ChatColor.RED + "You can't name a store that.");
						return true;
					}
    				if(!rs.entrance.equals("")){
        				if(!rs.exit.equals("")){
        			    	if(!rs.shopMap.containsKey(args[1])){//Create
        			    		rs.shopMap.put(args[1], new Shop(args[1], player.getWorld().getName(), "@admin"));
        			    	}
        			    	//Add entrance
        			    	Location en = new Location(rs.getServer().getWorld(rs.shopMap.get(args[1]).world), Integer.parseInt(rs.entrance.split(",")[0]),Integer.parseInt(rs.entrance.split(",")[1]), Integer.parseInt(rs.entrance.split(",")[2]));
        			    	Location ex = new Location(rs.getServer().getWorld(rs.shopMap.get(args[1]).world), Integer.parseInt(rs.exit.split(",")[0]),Integer.parseInt(rs.exit.split(",")[1]), Integer.parseInt(rs.exit.split(",")[2]));
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
    							
    							player.sendMessage(ChatColor.GREEN + "Highlighted 5 locations for 5 seconds.");
    						} else {
    							player.sendMessage(ChatColor.RED + "No locations to highlight.");
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
       					BlockState bs = player.getLocation().subtract(0, 1, 0).getBlock().getState();
       					if(bs instanceof Chest | bs instanceof DoubleChest){
       						if(tempShop.protectedChests.contains(bs.getLocation())){
       							player.sendMessage(ChatColor.GREEN + "This chest is already protected.");
       							return true;
       						} else {
       							tempShop.protectedChests.add(bs.getLocation());
       							player.sendMessage(ChatColor.GREEN + "Made chest protected.");
       							return true;
        					}
        				} else {
        					player.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUARESTANDINGONISNTACHEST);
        				}
    				} else if(args.length == 1 & args[0].equalsIgnoreCase("remove")){
   						Shop tempShop = rs.shopMap.get(rs.PInvMap.get(player.getName()).getStore());
       					BlockState bs = player.getLocation().subtract(0, 1, 0).getBlock().getState();
       					if(tempShop.protectedChests.contains(bs.getLocation())){
       						tempShop.protectedChests.remove(bs.getLocation());
       						player.sendMessage(ChatColor.GREEN + "Unprotected chest.");
       						return true;
    					} else {
       						player.sendMessage(ChatColor.RED + "This chest isn't protected.");
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
    		if(Config.autoUpdate > 0){
    			if(args.length == 1 && args[0].equals("info")){
    				if(!rs.newUpdate.equals("")){
        				if((player != null && Config.autoUpdate > 1) || player == null){//Permission to get info
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
        						player.sendMessage(ChatColor.GREEN + "Reading description...");
    							messageSender mS = new messageSender(player, mess.split("\\n"), 2000);
    							mS.start();
        					}
        					return true;
        				}
    				} else {
    					sender.sendMessage(ChatColor.RED + "This is the newest version.");
    					return true;
    				}
    			} else if(args.length == 1 && args[0].equals("update")){
    				if(!rs.newUpdate.equals("")){
        				if((player != null && Config.autoUpdate == 4) || ( player == null && Config.autoUpdate > 2)){//Permission to update
        					rs.updater = new Updater(rs, "realshopping", rs.getPFile(), Updater.UpdateType.DEFAULT, true);
        					if(rs.updater.getResult() == Updater.UpdateResult.SUCCESS)
        						sender.sendMessage(ChatColor.GREEN + "Successful update!");
        					else
        						sender.sendMessage(ChatColor.RED + "Update failed.");
        					return true;
        				}
    				} else{
    					sender.sendMessage(ChatColor.RED + "This is the newest version.");
    					return true;
    				}
    			} else return false;
    		}
    		sender.sendMessage(ChatColor.RED + "You aren't permitted to use this command.");
    	} else if(cmd.getName().equalsIgnoreCase("realshopping")){
    		byte pg = 1;
    		if(args.length > 0) try {
    			pg = Byte.parseByte(args[0]);
    			if(pg < 1 || pg > 2){
    				sender.sendMessage(ChatColor.RED + LangPack.THEREARENTTHATMANYPAGES);
    				return false;
    			}
			} catch (NumberFormatException e){
				sender.sendMessage(ChatColor.RED + args[1] + LangPack.ISNOTANINTEGER);
				return false;
			}
    		int i = 0;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "RealShopping [v0.40] - A shop plugin for Bukkit made by kuben0");i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "Loaded config settings:");i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "enable-automatic-updates:"+Config.getAutoUpdateStr(Config.autoUpdate));i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "auto-protect-chests:"+Config.autoprotect);i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "delivery-cost-zones:"+Config.deliveryZones);i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "language-pack:"+Config.langpack);i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "enable-selling-to-stores:"+Config.enableSelling);i++;
    		if(i >= (pg-1)*10 && i < pg*10) {
    			String tempStr = "";
    			if(Config.cartEnabledW.contains("@all")) tempStr = "Enabled in all worlds";
    			else {
        			boolean j = true;
        			for(String str:Config.cartEnabledW){
        				if(j){
       						tempStr += str;
       						j = false;
       					}
        				else tempStr += "," + str;
        			}
    			}
    			sender.sendMessage(ChatColor.GREEN + "enable-shopping-carts-in-worlds:" + tempStr);i++;
    		}
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "player-stores-create-cost:"+Config.pstorecreate);i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "drop-items-at:"+Config.dropLoc.getWorld().getName()+";"+Config.dropLoc.getBlockX()+","+Config.dropLoc.getBlockY()+","+Config.dropLoc.getBlockZ());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "hell-location:"+Config.hellLoc.getWorld().getName()+";"+Config.hellLoc.getBlockX()+","+Config.hellLoc.getBlockY()+","+Config.hellLoc.getBlockZ());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "jail-location:"+Config.jailLoc.getWorld().getName()+";"+Config.jailLoc.getBlockX()+","+Config.jailLoc.getBlockY()+","+Config.jailLoc.getBlockZ());i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "keep-stolen-items-after-punish:"+Config.keepstolen);i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "punishment:"+Config.punishment);i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "enable-automatic-store-management:"+Config.enableAI);i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "stat-updater-frequency:"+Config.getTimeString(Config.updateFreq));i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "statistics-timespan:"+Config.getTimeString(Config.statTimespan));i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "clean-stats-older-than:"+Config.getTimeString(Config.cleanStatsOld));i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "allow-filling-chests:"+Config.allowFillChests);i++;
    		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "notificatior-update-frequency:"+Config.notTimespan);i++;
    		if(pg < 2) sender.sendMessage(ChatColor.DARK_PURPLE + "realshopping " + (pg + 1) + " for more.");
       		return true;
    	}
    	return false;
	}
	
	private boolean collect(CommandSender sender, Player player, Shop tempShop, boolean cFlag, int amount){
		if(player != null){
			if(!tempShop.owner.equalsIgnoreCase("@admin")){
				if(cFlag){
					if(Config.cartEnabledW.contains(player.getWorld().toString())){
						if(player.getLocation().subtract(0, 1, 0).getBlock().getState() instanceof Chest){
							if(!tempShop.stolenToClaim.isEmpty()){
								if(amount == 0) amount = 27;
								ItemStack[] origIs = tempShop.stolenToClaim.toArray(new ItemStack[0]);
								ItemStack[] tempIs = new ItemStack[Math.min(Math.min(27, amount),origIs.length)];
								int i = 0;
								for(;i < 27 && i < origIs.length && i < amount;i++){
									tempIs[i] = origIs[i];
								}
								ItemStack[] oldCont = ((Chest)player.getLocation().subtract(0, 1, 0).getBlock().getState()).getBlockInventory().getContents();
								for(ItemStack tempIS:oldCont) if(tempIS != null) player.getWorld().dropItem(player.getLocation(), tempIS);
								((Chest)player.getLocation().subtract(0, 1, 0).getBlock().getState()).getBlockInventory().setContents(tempIs);
								player.sendMessage(ChatColor.GREEN + LangPack.FILLEDCHESTWITH + i + LangPack.ITEMS);
								if(origIs.length <= Math.min(27, amount)) tempShop.stolenToClaim.clear();
								else {
									for(i = 0;i < 27 && i < origIs.length && i < amount;i++){
										tempShop.stolenToClaim.remove(tempIs[i]);
									}
								}
								return true;
							} else sender.sendMessage(ChatColor.RED + LangPack.NOTHINGTOCOLLECT);
						} else sender.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUARESTANDINGONISNTACHEST);
					} else {
						sender.sendMessage(ChatColor.RED + "You can't collect your items to a chest on this server.");
						return true;
					}
				} else {
					ItemStack[] tempIs = tempShop.stolenToClaim.toArray(new ItemStack[0]);
					int i = 0;
					for(;(i>0)?i < amount:true && i < tempIs.length;i++){
						player.getWorld().dropItem(player.getLocation(), tempIs[i]);
					}
					player.sendMessage(ChatColor.GREEN + LangPack.DROPPED + i + LangPack.ITEMS);
					if(amount == 0 || tempIs.length <= amount) tempShop.stolenToClaim.clear();
					else {
						for(i = 0;(i>0)?i < amount:true && i < tempIs.length;i++){
							tempShop.stolenToClaim.remove(tempIs[i]);
						}
					}
					return true;
				}
			}
		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
		return false; 
	}
	
	@SuppressWarnings("static-access")
	private Map<Price, Float> getLowestPrices(String shop){
		Map<Price, Float> tempMap = new HashMap<Price, Float>();
		String[] keys = rs.shopMap.keySet().toArray(new String[0]);
		for(String s:keys){
			if(!s.equals(shop)){
				Price[] keys2 = rs.shopMap.get(s).prices.keySet().toArray(new Price[0]);
				for(Price p:keys2){
					if(tempMap.containsKey(p)){
						if(tempMap.get(p) > rs.shopMap.get(s).prices.get(p)) tempMap.put(p, rs.shopMap.get(s).prices.get(p));
					} else
						tempMap.put(p, rs.shopMap.get(s).prices.get(p));
				}
			}
		}
		return tempMap;
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
			player.sendMessage(ChatColor.GREEN + "Done!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}