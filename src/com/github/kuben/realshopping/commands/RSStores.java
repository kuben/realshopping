package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.listeners.RSPlayerListener;
import com.github.kuben.realshopping.prompts.PromptMaster;

class RSStores extends RSCommand {

	private boolean isOwner;
	//TODO color stuff
	public RSStores(CommandSender sender, String[] args) {
		super(sender, args);
		isOwner = false;
	}

	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rsstores store [buyfor ...|collect ...|ban ...|unban ...|kick ...|startsale ...|endsale");
				sender.sendMessage(LangPack.FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ + ChatColor.LIGHT_PURPLE + "/rsstores help " + ChatColor.DARK_PURPLE + "COMMAND");
			} else if(args.length == 1){
				sender.sendMessage(ChatColor.GREEN + LangPack.RSSTORESHELP + LangPack.YOU_CAN_GET_MORE_HELP_ABOUT_
						+ ChatColor.LIGHT_PURPLE + "buyfor, collect, ban, unban, kick, startsale, endsale");
			} else {
				if(args[1].equals("buyfor")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "buyfor %_OF_SELL_PRICE" + ChatColor.RESET + LangPack.BUYFORHELP);
				else if(args[1].equals("collect")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "collect [-c] [" + ChatColor.DARK_PURPLE + "AMOUNT" + ChatColor.LIGHT_PURPLE + "]" + ChatColor.RESET
						+ LangPack.COLLECTHELP);
				else if(args[1].equals("ban")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "ban " + ChatColor.DARK_PURPLE + "PLAYER" + ChatColor.RESET
						+ LangPack.BANHELP);
				else if(args[1].equals("unban")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "unban " + ChatColor.DARK_PURPLE + "PLAYER" + ChatColor.RESET
						+ LangPack.UNBANHELP);
				else if(args[1].equals("kick")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "kick [-o] " + ChatColor.DARK_PURPLE + "PLAYER" + ChatColor.RESET
						+ LangPack.KICKHELP);
				else if(args[1].equals("startsale")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "startsale " + ChatColor.DARK_PURPLE + "%_OFF" + ChatColor.LIGHT_PURPLE
						+ " [" + ChatColor.DARK_PURPLE + "ITEM(S)" + ChatColor.LIGHT_PURPLE + "]" + ChatColor.RESET + LangPack.STARTSALEHELP
						+ ChatColor.DARK_PURPLE + 	"ITEMID" + ChatColor.LIGHT_PURPLE + "[:" + ChatColor.DARK_PURPLE + "DATA" + ChatColor.LIGHT_PURPLE +"]"
						+ ChatColor.RESET + LangPack.STARTSALEHELP2);
				else if(args[1].equals("endsale")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "endsale" + ChatColor.RESET
						+ LangPack.ENDSALEHELP);
			}
			return true;
		}
		return null;
	}
	
	/**
	 * Checks if player is allowed to manage the store named in args[0] 
	 * , that is (if player owned store) if they are the owner or 
	 * (if admin store) if they have the rsset permission.
	 * @return True if player is allowed to manage the store, false otherwise.
	 */
	private boolean determineIfOwner(){
		if(RealShopping.shopExists(args[0])){
			if(RealShopping.getShop(args[0]).getOwner().equalsIgnoreCase("@admin")){
				if(player == null){
					isOwner = true;
				} else if(player.hasPermission("realshopping.rsset")){
					isOwner = true;
				}
			} else {
				if(player != null){
					if(RealShopping.getShop(args[0]).getOwner().equalsIgnoreCase(player.getName())){
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
		Shop tempShop = RealShopping.getShop(args[0]);
		if(player != null){
			if(!tempShop.getOwner().equalsIgnoreCase("@admin")){
				if(cFlag){
					if(Config.isAllowFillChests()){
						if(!RealShopping.hasPInv(player) || tempShop.getOwner().equals(player.getName())){
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
							} else sender.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUSELECTEDISNTACHEST);
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
						RealShopping.getShop(args[0]).setBuyFor(pcnt);
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
		if(RealShopping.getShop(args[0]).isBanned(args[2].toLowerCase())) sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISALREADYBANNEDFROMYOURSTORE);
		else {
			RealShopping.getShop(args[0]).addBanned(args[2].toLowerCase());
			sender.sendMessage(ChatColor.GREEN + LangPack.BANNED + args[2] + LangPack.FROMSTORE);
		}
		RealShopping.updateEntrancesDb();
		return true;
	}
	
	private boolean unban(){
		if(args.length != 3) return false;
			if(RealShopping.getShop(args[0]).isBanned(args[2].toLowerCase())){
 				RealShopping.getShop(args[0]).removeBanned(args[2].toLowerCase());
 				sender.sendMessage(ChatColor.GREEN + args[2] + LangPack.ISNOLONGERBANNEDFROMYOURSTORE);
 			} else sender.sendMessage(ChatColor.RED + args[2] + LangPack.WASNTBANNEDFROMYOURSTORE);
 			RealShopping.updateEntrancesDb();
 			return true;
	}
	
	private boolean kick(){
		if(args.length == 3){
 			if(!RealShopping.getPlayersInStore(args[0].toLowerCase())[0].equals("")){
 				boolean cont = false;
 				for(String tempP:RealShopping.getPlayersInStore(args[0].toLowerCase()))//TODO better way
 					if(tempP.toLowerCase().equals(args[2].toLowerCase())){
 						cont = true;
 						break;
 					}
 				if(cont){
 					if(sender.getServer().getPlayerExact(args[2]) != null){
 						//Abandon conversations
 						if(RSPlayerListener.hasConversationListener(player)) RSPlayerListener.killConversationListener(player);
 						if(PromptMaster.isConversing(player)) PromptMaster.abandonConversation(player);
 						
 						RSUtils.returnStolen(sender.getServer().getPlayerExact(args[2]));
 						Location l = RealShopping.getShop(args[0]).getFirstE();
 						RealShopping.removePInv(sender.getServer().getPlayerExact(args[2]));
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
     					RealShopping.removePInv(sender.getServer().getOfflinePlayer(args[3]).getName());
 						sender.sendMessage(ChatColor.GREEN + args[3] + LangPack.WASKICKEDFROMYOURSTORE);
     				} else sender.sendMessage(ChatColor.RED + LangPack.PLAYER + args[3] + LangPack.DOESNTEXIST);
     			} else sender.sendMessage(ChatColor.RED + LangPack.PLAYER + args[3] + LangPack.DOESNTEXIST);
 			} else sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTINYOURSTORE);
 			return true;
		} else return false;
	}
	
	private boolean startsale(){
        try {
            int pcnt = Integer.parseInt(args[2]);
            if(pcnt < 100){
                if(pcnt > 0){
                    Shop tempShop = RealShopping.getShop(args[0]);
                    if(tempShop != null){
                        if(tempShop.hasPrices()){
                            tempShop.clearSales();
                            if(args.length == 3){
                                Price[] keys = tempShop.getPrices().keySet().toArray(new Price[0]);
                                int i = 0;
                                for(;i < keys.length;i++){
                                    tempShop.addSale(keys[i], pcnt);
                                }
                                if(pcnt > 0) sender.sendMessage(ChatColor.GREEN + "" + pcnt + LangPack.PCNTOFF + i + LangPack.ITEMS);
                                else sender.sendMessage(ChatColor.RED + LangPack.NOITEMSARESOLDINTHESTORE);
                                return true;
                            } else {//If args.length > 3
                                String[] keys = args[3].split(",");
                                if(keys.length > 0){
                                    int i = 0, j = 0;
                                    for(;i < keys.length;i++){
                                        Price tempP = RSUtils.pullPrice(keys[i],this.player);
                                        if(tempShop.hasPrice(tempP) || tempShop.hasPrice(tempP.stripOffData())){
                                            tempShop.addSale(tempP, pcnt);
                                            j++;
                                        }
                                    }
                                    if(pcnt > 0) sender.sendMessage(ChatColor.GREEN + "" + pcnt + LangPack.PCNTOFF + j + LangPack.ITEMS);
                                    else sender.sendMessage(ChatColor.RED + LangPack.NOITEMSARESOLDINTHESTORE);
                                    return true;
                                } else sender.sendMessage(ChatColor.RED + args[3] + LangPack.ISNOTAVALIDARGUMENT);
                            }
                        } else sender.sendMessage(ChatColor.RED + LangPack.NOITEMSARESOLDINTHESTORE);	
                    } else sender.sendMessage(ChatColor.RED + LangPack.STORE + args[0] + LangPack.DOESNTEXIST);
                } else sender.sendMessage(ChatColor.RED + LangPack.YOUCANTUSEAVALUEOF0ORLESS);
            } else  sender.sendMessage(ChatColor.RED + LangPack.YOUCANTUSEAVALUEOF100ORMORE);
        } catch(NumberFormatException e){
            sender.sendMessage(ChatColor.RED + args[2] + LangPack.ISNOTANINTEGER);
        }
        return false;
	}
	
	private boolean endsale(){
		RealShopping.getShop(args[0]).clearSales();
		sender.sendMessage(ChatColor.GREEN + LangPack.SALEENDED);
		return true;
	}
	
	@Override
	protected boolean execute() {
		if(!determineIfOwner()) return false;
		
		if(args.length == 1){//First argument MUST be store
			sender.sendMessage(ChatColor.GREEN + LangPack.STORE + args[0] + ((RealShopping.getShop(args[0]).getOwner().equalsIgnoreCase("@admin"))?"":LangPack.OWNEDBY + RealShopping.getShop(args[0]).getOwner()));
			if(RealShopping.getShop(args[0]).getBuyFor() > 0) sender.sendMessage(ChatColor.GREEN + LangPack.BUYSFOR + RealShopping.getShop(args[0]).getBuyFor() + LangPack.PCNTOFORIGINAL);
			if(RealShopping.getShop(args[0]).hasSales()) sender.sendMessage(ChatColor.GREEN + LangPack.HASA + RealShopping.getShop(args[0]).getFirstSale() + LangPack.PCNTOFFSALERIGHTNOW);
			if(!RealShopping.getPlayersInStore(args[0])[0].equals("")){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.PLAYERSINSTORE + "\n" + ChatColor.RESET + RSUtils.formatPlayerListToMess(RealShopping.getPlayersInStore(args[0])));
			}
			sender.sendMessage(ChatColor.GREEN + LangPack.FORHELPTYPE + ChatColor.LIGHT_PURPLE + "/rsstores help");
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
    		} else if(args.length > 2 && args[1].equalsIgnoreCase("startsale")){
    			return startsale();
    		} else if(args.length == 2 && args[1].equalsIgnoreCase("endsale")){
    			return endsale();
    		}
		} else sender.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE);
		return false;
	}

}