package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;

class RSStores extends RSCommand {

	private boolean isOwner;
	
	public RSStores(CommandSender sender, String[] args) {
		super(sender, args);
		isOwner = false;
	}

	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rsstores store [buyfor %_OF_SELL_PRICE|collect [-c] [AMOUNT]|ban PLAYER|unban PLAYER|kick [-o] PLAYER|startsale %_OFF [ITEMID[:DATA][,ITEMID[:DATA][,ITEMID[:DATA]...]]]|endsale|notifications [on|off]|onchange [nothing|notify|changeprices] [TRESHOLD] [PERCENT]]");
				sender.sendMessage("For help for a specific command, type: " + ChatColor.DARK_PURPLE + "/rsstores help COMMAND");
				return true;
			} if(args.length == 1){
				sender.sendMessage(ChatColor.GREEN + LangPack.RSSTORESHELP + ChatColor.DARK_PURPLE + "buyfor, collect, ban, unban, kick, startsale, endsale, notifications, onchange");
			} else {
				if(args[1].equals("buyfor")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "buyfor %_OF_SELL_PRICE" + ChatColor.RESET + LangPack.BUYFORHELP);
				else if(args[1].equals("collect")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "collect [-c] [AMOUNT]" + ChatColor.RESET
						+ LangPack.COLLECTHELP);
				else if(args[1].equals("ban")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "ban PLAYER" + ChatColor.RESET
						+ LangPack.BANHELP);
				else if(args[1].equals("unban")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "unban PLAYER" + ChatColor.RESET
						+ LangPack.UNBANHELP);
				else if(args[1].equals("kick")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "kick [-o] PLAYER" + ChatColor.RESET
						+ LangPack.KICKHELP);
				else if(args[1].equals("startsale")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "startsale %_OFF [ITEM(S)]" + ChatColor.RESET
						+ LangPack.STARTSALEHELP + ChatColor.DARK_PURPLE + 
						"ITEMID[:DATA]" + ChatColor.RESET + LangPack.STARTSALEHELP2);
				else if(args[1].equals("endsale")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "endsale" + ChatColor.RESET
						+ LangPack.ENDSALEHELP);
				else if(args[1].equals("notifications")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "notifications [on|off]" + ChatColor.RESET
    							+ LangPack.NOTIFICATIONSHELP);
				else if(args[1].equals("onchange")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "onchange [nothing|notify TRESHOLD|changeprices TRESHOLD PERCENT]"
    							+ ChatColor.RESET + LangPack.ONCHANGEHELP);
			}
			return true;
		}
		return null;
	}
	
	private boolean determineIfOwner(){
		if(RealShopping.shopMap.containsKey(args[0])){
			if(RealShopping.shopMap.get(args[0]).getOwner().equalsIgnoreCase("@admin")){
				if(player == null){
					isOwner = true;
				} else if(player.hasPermission("realshopping.rsset")){
					isOwner = true;
				}
			} else {
				if(player != null){
					if(RealShopping.shopMap.get(args[0]).getOwner().equalsIgnoreCase(player.getName())){
						isOwner = true;
					}
				}
			}
		} else {
			sender.sendMessage(args[0] + LangPack.DOESNTEXIST);
			return false;
		}
		return true;
	}
	
	private boolean collect(){
		if(player == null) return false;
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
		Shop tempShop = RealShopping.shopMap.get(args[0]);
		if(player != null){
			if(!tempShop.getOwner().equalsIgnoreCase("@admin")){
				if(cFlag){
					if(Config.isAllowFillChests()){
						if(!RealShopping.PInvMap.containsKey(player.getName()) || tempShop.getOwner().equals(player.getName())){
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
						} else sender.sendMessage(ChatColor.RED + LangPack.YOUCANTCOLLECT_YOUDONOTOWN);
					} else {
						sender.sendMessage(ChatColor.RED + LangPack.YOUCANTCOLLECT_SERVER);
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
	
	private boolean buyfor(){
		if(args.length != 3) return false; 
		if(Config.isEnableSelling()){
			try {
				int pcnt = Integer.parseInt(args[2]);
				if(pcnt <= 100){
					if(pcnt >= 0){
						RealShopping.shopMap.get(args[0]).setBuyFor(pcnt);
						if(pcnt > 0) sender.sendMessage(ChatColor.GREEN + LangPack.BUYSFOR + pcnt + LangPack.PCNTOFORIGINAL);
						else sender.sendMessage(ChatColor.RED + LangPack.NOTBUYINGFROMPLAYERS);
						RealShopping.updateEntrancesDb();
						return true;
					} else sender.sendMessage(ChatColor.RED + LangPack.YOUCANTUSEAVALUEBELLOW0);
				} else sender.sendMessage(ChatColor.RED + LangPack.YOUCANTUSEAVALUEOVER100);
			} catch(NumberFormatException e){
				sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTANINTEGER);
			}
		} else sender.sendMessage(ChatColor.RED + LangPack.SELLINGTOSTORESISNOTENABLEDONTHISSERVER);
		return false;
	}
	
	private boolean ban(){
		if(args.length != 3) return false;
		if(RealShopping.shopMap.get(args[0]).isBanned(args[2].toLowerCase())) sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISALREADYBANNEDFROMYOURSTORE);
		else {
			RealShopping.shopMap.get(args[0]).addBanned(args[2].toLowerCase());
			sender.sendMessage(ChatColor.GREEN + LangPack.BANNED + args[2] + LangPack.FROMSTORE);
		}
		RealShopping.updateEntrancesDb();
		return true;
	}
	
	private boolean unban(){
		if(args.length != 3) return false;
			if(RealShopping.shopMap.get(args[0]).isBanned(args[2].toLowerCase())){
 				RealShopping.shopMap.get(args[0]).removeBanned(args[2].toLowerCase());
 				sender.sendMessage(ChatColor.GREEN + args[2] + LangPack.ISNOLONGERBANNEDFROMYOURSTORE);
 			} else sender.sendMessage(ChatColor.RED + args[2] + LangPack.WASNTBANNEDFROMYOURSTORE);
 			RealShopping.updateEntrancesDb();
 			return true;
	}
	
	private boolean kick(){
		if(args.length == 3){
 			if(!RealShopping.getPlayersInStore(args[0].toLowerCase())[0].equals("")){
 				boolean cont = false;
 				for(String tempP:RealShopping.getPlayersInStore(args[0].toLowerCase()))
 					if(tempP.toLowerCase().equals(args[2].toLowerCase())){
 						cont = true;
 						break;
 					}
 				if(cont){
 					if(sender.getServer().getPlayerExact(args[2]) != null){
 						RealShopping.returnStolen(sender.getServer().getPlayerExact(args[2]));
 						Location l = RealShopping.shopMap.get(args[0]).getFirstE();
 						RealShopping.PInvMap.remove(sender.getServer().getPlayerExact(args[2]).getName());
 						sender.getServer().getPlayerExact(args[2]).teleport(l.add(0.5, 0, 0.5));
 						sender.sendMessage(ChatColor.GREEN + args[2] + LangPack.WASKICKEDFROMYOURSTORE);
 					} else sender.sendMessage(ChatColor.RED + LangPack.PLAYER + args[2] + LangPack.ISNTONLINEKICK);
 				} else sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTINYOURSTORE);
 			} else sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTINYOURSTORE);
 			return true;
		} else if(args.length == 4 && args[2].equalsIgnoreCase("-o")){
 			if(!RealShopping.getPlayersInStore(args[0].toLowerCase())[0].equals("")){
 				if(sender.getServer().getOfflinePlayer(args[3]) != null){
     				boolean cont = false;
     				for(String tempP:RealShopping.getPlayersInStore(args[0].toLowerCase()))
     					if(tempP.toLowerCase().equals(args[3].toLowerCase())){
     						cont = true;
     						break;
     					}
     				if(cont){
     					RealShopping.PInvMap.remove(sender.getServer().getOfflinePlayer(args[3]).getName());
 						sender.sendMessage(ChatColor.GREEN + args[3] + LangPack.WASKICKEDFROMYOURSTORE);
     				} else sender.sendMessage(ChatColor.RED + LangPack.PLAYER + args[3] + LangPack.DOESNTEXIST);
     			} else sender.sendMessage(ChatColor.RED + LangPack.PLAYER + args[3] + LangPack.DOESNTEXIST);
 			} else sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTINYOURSTORE);
 			return true;
		} else return false;
	}
	
	private boolean startsale(){
		if(args.length == 3){
			try {
				int pcnt = Integer.parseInt(args[2]);
				if(pcnt < 100){
					if(pcnt > 0){
						if(RealShopping.shopMap.containsKey(args[0]) && RealShopping.shopMap.get(args[0]).hasPrices()){
							RealShopping.shopMap.get(args[0]).clearSales();
							Price[] keys = RealShopping.shopMap.get(args[0]).getPrices().keySet().toArray(new Price[0]);
							int i = 0;
							for(;i < keys.length;i++){
								RealShopping.shopMap.get(args[0]).addSale(keys[i], pcnt);
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
		} else if(args.length == 4){
			try {
				int pcnt = Integer.parseInt(args[2]);
				if(pcnt < 100){
					if(pcnt > 0){
						String[] keys = args[3].split(",");
						if(keys.length > 0){
							RealShopping.shopMap.get(args[0]).clearSales();
							int i = 0;
							int j = 0;
							for(;i < keys.length;i++){
								Price tempP = new Price(keys[i]);
								if(RealShopping.shopMap.get(args[0]).hasPrice(tempP) || RealShopping.shopMap.get(args[0]).hasPrice(new Price(keys[i].split(":")[0]))){
									RealShopping.shopMap.get(args[0]).addSale(tempP, pcnt);
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
		}
		return false;
	}
	
	private boolean endsale(){
		RealShopping.shopMap.get(args[0]).clearSales();
		sender.sendMessage(ChatColor.GREEN + LangPack.SALEENDED);
		return true;
	}
	
	private boolean notifications(){
		if(args.length > 2){
			if(args[2].equals("on")){
				RealShopping.shopMap.get(args[0]).setAllowNotifications(true);
				sender.sendMessage(ChatColor.GREEN + LangPack.ENABLEDNOTIFICATIONSFOR + args[0]);
			} else if(args[2].equals("off")){
				RealShopping.shopMap.get(args[0]).setAllowNotifications(false);
				sender.sendMessage(ChatColor.GREEN + LangPack.DISABLEDNOTIFICATIONSFOR + args[0]);
			} else {
				sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTAVALIDARGUMENT);
				sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "[...] notifications [on|off]");
			}
			return true;
		} else {
			sender.sendMessage(ChatColor.GREEN + LangPack.NOTIFICATIONSARE + (RealShopping.shopMap.get(args[0]).allowsNotifications()?"on":"off") + ".");
			return true;
		}
	}
	
	private boolean onchange(){
		if(Config.isEnableAI()){
			if(args.length == 2){
				if(RealShopping.shopMap.get(args[0]).getNotifyChanges() == 1) sender.sendMessage(ChatColor.GREEN + LangPack.YOUWILLBENOTIFIEDIF + args[0] + LangPack.LOSESGAINS
						+ RealShopping.shopMap.get(args[0]).getChangeTreshold() + LangPack.PLACES);
				else if(RealShopping.shopMap.get(args[0]).getNotifyChanges() == 2) sender.sendMessage(ChatColor.GREEN + LangPack.THEPRICEWILLBELOWEREDINCREASED_ + RealShopping.shopMap.get(args[0]).getChangePercent()
						+ LangPack.PCNTIF + args[0] + LangPack.LOSESGAINS + RealShopping.shopMap.get(args[0]).getChangeTreshold() + LangPack.PLACES);
				else sender.sendMessage(ChatColor.GREEN + args[0] + LangPack.WONTNOTIFY_);
				return true;
			} else if(args.length > 2){
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
					if(args[4].contains("%")) sender.sendMessage(LangPack.SKIPPSIGN);
				}
				if(args[2].equals("nothing")){
					RealShopping.shopMap.get(args[0]).setNotifyChanges((byte)0);
					sender.sendMessage(ChatColor.GREEN + LangPack.YOUWONTGETNOTIFIEDWHENYOURSTORE + args[0] + LangPack.BECOMESMOREORLESSPOPULAR);
					RealShopping.updateEntrancesDb();
					return true;
				} else if(args[2].equals("notify")){
					if(tresh > -1){
						RealShopping.shopMap.get(args[0]).setNotifyChanges((byte)1);
						RealShopping.shopMap.get(args[0]).setChangeTreshold(tresh);
						sender.sendMessage(ChatColor.GREEN + LangPack.YOUWILLGETNOTIFIEDWHENYOURSTORE + args[0] + LangPack.BECOMESATLEAST + tresh + LangPack.PLACESMOREORLESSPOPULAR);
						RealShopping.updateEntrancesDb();
					} else
						sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "[...] onchange notify TRESHOLD " + ChatColor.RESET
								+ LangPack.WHERETRESHOLDIS_);
					return true;
				} else if(args[2].equals("changeprices")){
					if(tresh > -1 && pcnt > -1){
						RealShopping.shopMap.get(args[0]).setNotifyChanges((byte)2);
						RealShopping.shopMap.get(args[0]).setChangeTreshold(tresh);
						RealShopping.shopMap.get(args[0]).setChangePercent(pcnt);
						sender.sendMessage(ChatColor.GREEN + LangPack.YOUWILLGETNOTIFIEDWHENYOURSTORE + args[0] + LangPack.BECOMESATLEAST + ChatColor.DARK_PURPLE + tresh
								+ LangPack.PLACESMOREORLESSPOPULAR + LangPack.ANDTHEPRICESWILLBE_ + ChatColor.DARK_PURPLE + pcnt + "%.");
						RealShopping.updateEntrancesDb();
						return true;
					} else
						sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "[...] onchange changepries TRESHOLD PERCENT" + ChatColor.RESET
								+ LangPack.WHERETRESHOLDIS_CHANGES_
								+ LangPack.ANDPERCENTIS_);
					return true;
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + LangPack.AI_ISNOTENABLED_);
		}
		return false;
	}
	
	@Override
	protected boolean execute() {
		if(!determineIfOwner()) return false;
		
		if(args.length == 1){//First argument MUST be store
			sender.sendMessage(ChatColor.GREEN + LangPack.STORE + args[0] + ((RealShopping.shopMap.get(args[0]).getOwner().equalsIgnoreCase("@admin"))?"":LangPack.OWNEDBY + RealShopping.shopMap.get(args[0]).getOwner()));
			if(RealShopping.shopMap.get(args[0]).getBuyFor() > 0) sender.sendMessage(ChatColor.GREEN + LangPack.BUYSFOR + RealShopping.shopMap.get(args[0]).getBuyFor() + LangPack.PCNTOFORIGINAL);
			if(RealShopping.shopMap.get(args[0]).hasSales()) sender.sendMessage(ChatColor.GREEN + LangPack.HASA + RealShopping.shopMap.get(args[0]).getFirstSale() + LangPack.PCNTOFFSALERIGHTNOW);
			if(!RealShopping.getPlayersInStore(args[0].toLowerCase())[0].equals("")){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.PLAYERSINSTORE + "\n" + ChatColor.RESET + RealShopping.formatPlayerListToMess(RealShopping.getPlayersInStore(args[0].toLowerCase())));
			}
			sender.sendMessage(ChatColor.GREEN + "For help, type " + ChatColor.DARK_PURPLE + "/rsstores help");//LANG
			return true;
		} else if(isOwner || player == null){
			if(args[1].equalsIgnoreCase("collect")){
				return collect();
			} else if(args[1].equalsIgnoreCase("buyfor")){
				return buyfor();
     		} else if(args[1].equalsIgnoreCase("ban")){
    			return ban();
     		} else if(args.length == 3 && args[1].equalsIgnoreCase("unban")){
     			return unban();
    		} else if(args[1].equalsIgnoreCase("kick")){
    			return kick();
    		} else if(args.length == 3 && args[1].equalsIgnoreCase("startsale")){
    			return startsale();
    		} else if(args.length == 2 && args[1].equalsIgnoreCase("endsale")){
    			return endsale();
    		} else if(args[1].equals("notifications")){
    			return notifications();
    		} else if(args[1].equals("onchange")){
    			return onchange();
    		}
		} else sender.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE);
		return false;
	}

}