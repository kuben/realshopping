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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.bukkit.inventory.ItemStack;

public class RSCommandExecutor implements CommandExecutor {
	RealShopping rs;
	
	public RSCommandExecutor(RealShopping rs){
		this.rs = rs;
	}
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
    	} else if(cmd.getName().equalsIgnoreCase("rsstores")) {
    		if(args.length > 0){
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
    			}
    			
    			if(isOwner){
    	    		if(args.length == 2 && args[1].equalsIgnoreCase("collect")){
    	        		if(player != null){
    	        			if(!rs.shopMap.get(args[0]).owner.equalsIgnoreCase("@admin")){
    	        				if(rs.shopMap.get(args[0]).stolenToClaim.containsKey(player.getName())){
    	        					for(ItemStack iS:rs.shopMap.get(args[0]).stolenToClaim.get(player.getName())){
    	        						player.getWorld().dropItem(player.getLocation(), iS);
    	        					}
    	        					rs.shopMap.get(args[0]).stolenToClaim.remove(player.getName());
    	        					return true;
    	        				} else sender.sendMessage(ChatColor.RED + LangPack.NOTHINGTOCOLLECT);
    	        			}
    	        		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE); 
    	    		} else if(args.length == 3 && args[1].equalsIgnoreCase("collect")){
    	        		if(player != null){
    	        			if(!rs.shopMap.get(args[0]).owner.equalsIgnoreCase("@admin")){
    	        				if(args[2].equalsIgnoreCase("-c")){
    	        					if(player.getLocation().subtract(0, 1, 0).getBlock().getState() instanceof Chest){
    	    	        				if(rs.shopMap.get(args[0]).stolenToClaim.containsKey(player.getName())){
    	    	        					ItemStack[] tempIs = new ItemStack[27];
    	    	        					ItemStack[] origIs = rs.shopMap.get(args[0]).stolenToClaim.get(player.getName()).toArray(new ItemStack[0]);
    	    	        					int i = 0;
    	    	        					for(;i < 27 && i < origIs.length;i++){
    	    	        						tempIs[i] = origIs[i];
    	    	        					}
    	    	        					ItemStack[] oldCont = ((Chest)player.getLocation().subtract(0, 1, 0).getBlock().getState()).getBlockInventory().getContents();
    	    	        					for(ItemStack tempIS:oldCont) if(tempIS != null) player.getWorld().dropItem(player.getLocation(), tempIS);
    	    	        					((Chest)player.getLocation().subtract(0, 1, 0).getBlock().getState()).getBlockInventory().setContents(tempIs);
    	    	        					player.sendMessage(ChatColor.GREEN + LangPack.FILLEDCHESTWITH + i + LangPack.ITEMS);
    	    	        					if(origIs.length < 28) rs.shopMap.get(args[0]).stolenToClaim.remove(player.getName());
    	    	        					else {
    	    	        						List newIs = new ArrayList();
    	    	        						for(;i < origIs.length;i++){
    	    	        							newIs.add(origIs[i]);
   	    	        							}
    	    	        						rs.shopMap.get(args[0]).stolenToClaim.put(player.getName(), newIs);
    	    	        					}
    	        							return true;
    	    	        				} else sender.sendMessage(ChatColor.RED + LangPack.NOTHINGTOCOLLECT);
    	        					} else sender.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUARESTANDINGONISNTACHEST);
    	        				} else {
    	        					try {
    	    	        				if(rs.shopMap.get(args[0]).stolenToClaim.containsKey(player.getName())){
    	    	        					int amount = Integer.parseInt(args[2]);
    	    	        					ItemStack[] tempIs = (ItemStack[])rs.shopMap.get(args[0]).stolenToClaim.get(player.getName()).toArray();
    	    	        					int i = 0;
    	    	        					for(;i < amount && i < tempIs.length;i++){
    	    	        						player.getWorld().dropItem(player.getLocation(), tempIs[i]);
    	    	        					}
    	    	        					player.sendMessage(ChatColor.GREEN + LangPack.DROPPED + i + LangPack.ITEMS);
    	    	        					if(tempIs.length < 28) rs.shopMap.get(args[0]).stolenToClaim.remove(player.getName());
    	    	        					else {
    	    	        						List newIs = new ArrayList();
    	    	        						for(;i < tempIs.length;i++){
    	    	        							newIs.add(tempIs[i]);
   	    	        							}
    	    	        						rs.shopMap.get(args[0]).stolenToClaim.put(player.getName(), newIs);
    	    	        					}
    	    	        					rs.shopMap.get(args[0]).stolenToClaim.remove(player.getName());
    	    	        					return true;
    	    	        				} else sender.sendMessage(ChatColor.RED + LangPack.NOTHINGTOCOLLECT);
    	        					} catch(NumberFormatException e){
    	        						sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTANINTEGER);
    	        					}
    	        				}
    	        			}
    	        		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE); 
    	    		} else if(args.length == 4 && args[1].equalsIgnoreCase("collect")){
    	        		if(player != null){
    	        			if(!rs.shopMap.get(args[0]).owner.equalsIgnoreCase("@admin")){
    	        				if(args[2].equalsIgnoreCase("-c")){
    	        					try {
        	        					if(player.getLocation().subtract(0, 1, 0).getBlock().getState() instanceof Chest){
        	    	        				if(rs.shopMap.get(args[0]).stolenToClaim.containsKey(player.getName())){
        	    	        					int amount = Integer.parseInt(args[2]);
        	    	        					ItemStack[] origIs = (ItemStack[]) rs.shopMap.get(args[0]).stolenToClaim.get(player.getName()).toArray();
        	    	        					ItemStack[] tempIs = new ItemStack[Math.min(Math.min(27, amount),origIs.length)];
        	    	        					int i = 0;
        	    	        					for(;i < tempIs.length;i++){
        	    	        						tempIs[i] = origIs[i];
        	    	        					}
        	    	        					ItemStack[] oldCont = ((Chest)player.getLocation().subtract(0, 1, 0).getBlock().getState()).getBlockInventory().getContents();
        	    	        					for(ItemStack tempIS:oldCont) if(tempIS != null) player.getWorld().dropItem(player.getLocation(), tempIS);
        	    	        					((Chest)player.getLocation().subtract(0, 1, 0).getBlock().getState()).getBlockInventory().setContents(tempIs);
        	    	        					player.sendMessage(ChatColor.GREEN + LangPack.FILLEDCHESTWITH + i + LangPack.ITEMS);
        	    	        					if(origIs.length <= Math.min(27, amount)) rs.shopMap.get(args[0]).stolenToClaim.remove(player.getName());
        	    	        					else {
        	    	        						List newIs = new ArrayList();
        	    	        						for(;i < origIs.length;i++){
        	    	        							newIs.add(origIs[i]);
       	    	        							}
        	    	        						rs.shopMap.get(args[0]).stolenToClaim.put(player.getName(), newIs);
        	    	        					}
        	        							return true;
        	    	        				} else sender.sendMessage(ChatColor.RED + LangPack.NOTHINGTOCOLLECT);
        	        					} else sender.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUARESTANDINGONISNTACHEST);
    	        					} catch(NumberFormatException e){
    	        						sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTANINTEGER);
    	        					}
    	        				}
    	        			}
    	        		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE); 
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
    	    			for(String str:rs.getPlayersInStore(args[0].toLowerCase())) System.out.println(str);
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
	    								Object[] keys = rs.shopMap.get(args[0]).prices.keySet().toArray();
	    								int i = 0;
	    								for(;i < keys.length;i++){
	    									rs.shopMap.get(args[0]).sale.put((Integer)keys[i], pcnt);
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
	    									int type = Integer.parseInt(keys[i]);
	    									if(rs.shopMap.get(args[0]).prices.containsKey(type)){
	    										rs.shopMap.get(args[0]).sale.put(type, pcnt);
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
    	    		}
    			} else sender.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE);
    		}
    	} else if(cmd.getName().equalsIgnoreCase("rssetprices")){
    		String shop = "";
    		int ii = 1;
    		if(args.length == 2){
    			if(player != null){
    				if(rs.PInvMap.containsKey(player.getName())){
    					shop = rs.PInvMap.get(player.getName()).getStore();
    				} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTORETOUSETHISCOMMANDWITHTWOARGUENTS);
    			} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSEALLTHREEARGUMENTSWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
    		} else if(args.length == 3){
    			shop = args[1];
    			ii = 2;
    		}
    		if(!shop.equals("")){
    			if(rs.shopMap.get(shop).owner.equals(player.getName()) || player.hasPermission("realshopping.rsset")){//If player is owner OR has admin perms
    				if(args[0].equalsIgnoreCase("add")){
    					try {
    						int i = Integer.parseInt(args[ii].split(":")[0]);
    						float k = Float.parseFloat(args[ii].split(":")[1]);
    						DecimalFormat twoDForm = new DecimalFormat("#.##");
    						float j = Float.parseFloat(twoDForm.format(k));
    						rs.shopMap.get(shop).prices.put(i, j);
    						sender.sendMessage(ChatColor.RED + LangPack.PRICEFOR + Material.getMaterial(i) + LangPack.SETTO + j + rs.unit);
    						return true;
    					} catch (NumberFormatException e) {
    						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + rs.unit);
    					} catch (ArrayIndexOutOfBoundsException e){
    						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + rs.unit);
    					}
    				} else if(args[0].equalsIgnoreCase("del")){
    					try {
    						int i = Integer.parseInt(args[ii]);
    						if(rs.shopMap.containsKey(shop)){
    							if(rs.shopMap.get(shop).prices.containsKey(i)){
    								rs.shopMap.get(shop).prices.remove(i);
    								sender.sendMessage(ChatColor.RED + LangPack.REMOVEDPRICEFOR + Material.getMaterial(i));
    								return true;
    							} else {
    								sender.sendMessage(ChatColor.RED + LangPack.COULDNTFINDPRICEFOR + i + " - " + Material.getMaterial(i));
    							}
    						} else {
    							sender.sendMessage(ChatColor.RED + shop + LangPack.DOESNTEXIST);
    						}
    					} catch (NumberFormatException e) {
    						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_);
    					}
    				}
    			} else sender.sendMessage(ChatColor.RED + LangPack.YOUARENTPERMITTEDTOEMANAGETHISSTORE);
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
        			    	if(!rs.shopMap.containsKey(args[1])){//Create
            					if(rs.econ.getBalance(player.getName()) < Config.pstorecreate) {
            						player.sendMessage(ChatColor.RED + LangPack.CREATINGASTORECOSTS + Config.pstorecreate + rs.unit);
            						return true;
            					} else {
            						rs.econ.withdrawPlayer(player.getName(), Config.pstorecreate);
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
    	} else if(cmd.getName().equalsIgnoreCase("realshopping")){
    		sender.sendMessage("rsenter");
    		sender.sendMessage("rsexit");
    		sender.sendMessage("rspay");
    		sender.sendMessage("rscost");
    		sender.sendMessage("rsprices");
    		sender.sendMessage("rsshipped");
    		sender.sendMessage("rsstores");
    		sender.sendMessage("rssetstores");
    		sender.sendMessage("rssetprices");
    		sender.sendMessage("rsset");
    		sender.sendMessage("rssetchests");
    		sender.sendMessage("rsunjail");
    		sender.sendMessage("rstplocs");
    		sender.sendMessage("rsreload");
    		}
    	return false;
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