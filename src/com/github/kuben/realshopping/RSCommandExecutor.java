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
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
    			player.sendMessage(ChatColor.RED + LangPack.YOURARTICLESCOST + rs.cost(player, null));
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
					if(rs.playerMap.get(player.getName()) != null) {
						return rs.prices(sender, 0, rs.playerMap.get(player.getName()), true);
					} else {
						sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
					}
				} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
			} else if(args.length == 1){
				if(args[0].matches("[0-9]+")){
					if(player != null){
						if(rs.playerMap.containsKey(player.getName())){
							int i = Integer.parseInt(args[0]);
							if(i > 0) return rs.prices(sender, i - 1, rs.playerMap.get(player.getName()), true);
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
    	} else if(cmd.getName().equalsIgnoreCase("rssetprices")){
    		String shop = "";
    		int ii = 1;
    		if(args.length == 2){
    			if(player != null){
    				if(rs.playerMap.containsKey(player.getName())){
    					shop = rs.playerMap.get(player.getName());
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
    						if(!rs.prices.containsKey(shop)) rs.prices.put(shop, new HashMap());
    						rs.prices.get(shop).put(i, j);
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
    						if(rs.prices.containsKey(shop)){
    							if(rs.prices.get(shop).containsKey(i)){
    								rs.prices.get(shop).remove(i);
    								sender.sendMessage(ChatColor.RED + LangPack.REMOVEDPRICEFOR + Material.getMaterial(i));
    								return true;
    							} else {
    								sender.sendMessage(ChatColor.RED + LangPack.COULDNTFINDPRICEFOR + i + " - " + Material.getMaterial(i));
    							}
    						} else {
    							sender.sendMessage(shop + LangPack.DOESNTEXIST);
    						}
    					} catch (NumberFormatException e) {
    						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_);
    					}
    				}
    			} else sender.sendMessage(ChatColor.RED + LangPack.YOUARENTPERMITTEDTOEMANAGETHISSTORE);
    		}
    	} else if(cmd.getName().equalsIgnoreCase("rssetchests")){
    		if(player != null){
    			if(rs.playerMap.containsKey(player.getName())){
    				Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
    				Shop tempShop = rs.shopMap.get(rs.playerMap.get(player.getName()));
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
            					if(rs.econ.getBalance(player.getName()) < rs.pstorecreate) {
            						player.sendMessage(ChatColor.RED + LangPack.CREATINGASTORECOSTS + rs.pstorecreate + rs.unit);
            						return true;
            					} else {
            						rs.econ.withdrawPlayer(player.getName(), rs.pstorecreate);
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
    						rs.shopMap.remove(args[1]);
    						player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
    						rs.updateEntrancesDb();
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
    					rs.shopMap.remove(args[1]);
    					player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
    					rs.updateEntrancesDb();
    					return true;
    				} else player.sendMessage(ChatColor.RED + args[1] + LangPack.WASNTFOUND);
    			}
    		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rsclaim")){
    		if(player != null){
    			if(rs.stolenToClaim.containsKey(player.getName())){
    				for(ItemStack iS:rs.stolenToClaim.get(player.getName())){
    					player.getWorld().dropItem(player.getLocation(), iS);
    				}
    				rs.stolenToClaim.remove(player.getName());
    				return true;
    			} else sender.sendMessage(ChatColor.RED + "Nothing to claim.");
    		} else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
    	} else if(cmd.getName().equalsIgnoreCase("rsshipped")){
    		if(player != null){
    			if(args.length == 0){
        			if(rs.shippedToCollect.containsKey(player.getName())){
        				int toClaim = rs.shippedToCollect.get(player.getName()).size();
        				if(toClaim != 0) sender.sendMessage(ChatColor.GREEN + "You have packages with ids from 1 to " + toClaim + " to pick up.");
        				else sender.sendMessage(ChatColor.GREEN + "You don't have any packages to pick up.");
        				return true;
        			} else sender.sendMessage(ChatColor.RED + "There are no packages to pick up.");
    			} else if(args.length == 1 && args[0].equalsIgnoreCase("collect")){
    				Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
    				return rs.collectShipped(l, player, 1);
   				} else if(args.length == 1 && args[0].equalsIgnoreCase("inspect")){
    				sender.sendMessage("You have to specify the id of the package you want to inspect.");
    			} else if(args.length == 2 && args[0].equalsIgnoreCase("collect")){
    				Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
    				try {
    					return rs.collectShipped(l, player, Integer.parseInt(args[1]));
    				} catch (NumberFormatException e){
    					sender.sendMessage(args[1] + " is not an integer.");
    				}
    			} else if(args.length == 2 && args[0].equalsIgnoreCase("inspect")){
        			if(rs.shippedToCollect.containsKey(player.getName())){
        				try {
            				String str = rs.formatItemStackToMess(rs.shippedToCollect.get(player.getName()).get(Integer.parseInt(args[1]) - 1));
            				sender.sendMessage("The contents of the package are: " + str);
            				return true;
        				} catch (ArrayIndexOutOfBoundsException e){
        					sender.sendMessage("ArrayIndexOutOfBoundsException");
        				} catch (IndexOutOfBoundsException e){
        					sender.sendMessage("There's no package with the id " + args[1]);
        				} catch (NumberFormatException e){
        					sender.sendMessage(args[1] + " is not an integer.");
        				}
        			} else sender.sendMessage(ChatColor.RED + "There are no packages to pick up.");
    			}
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
    	}
    	return false;
	}

}