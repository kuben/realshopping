package com.github.kuben.realshopping.commands;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.PSetting;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.listeners.RSPlayerListener;
import com.github.kuben.realshopping.prompts.PromptMaster;
import java.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

class RSStores extends RSCommand {

    private final ChatColor LP = ChatColor.LIGHT_PURPLE;
    private final ChatColor DP = ChatColor.DARK_PURPLE;
    private final ChatColor GR = ChatColor.GREEN;
    private final ChatColor DG = ChatColor.DARK_GREEN;
    private final ChatColor RD = ChatColor.RED;
    private final ChatColor DR = ChatColor.DARK_RED;
    private final ChatColor RESET = ChatColor.RESET;
    
    private final String MORE_HELP = LP + "buyfor"
                    + RESET + ", " + LP + "collect"
                    + RESET + ", " + LP + "ban"
                    + RESET + ", " + LP + "unban"
                    + RESET + ", " + LP + "kick"
                    + RESET + ", " + LP + "startsale"
                    + RESET + ", " + LP + "endsale"
                    + RESET + ", " + LP + "broadcast";
    
    private Shop shop;
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
				sender.sendMessage(DG + LangPack.USAGE + RESET + "/rsstores STORE [buyfor ...|collect ...|ban ...|unban ...|kick ...|startsale ...|endsale|broadcast ...");
				sender.sendMessage(LangPack.FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ + LP + "/rsstores help " + DP + "COMMAND");
			} else if(args.length == 1){
				sender.sendMessage(GR + LangPack.RSSTORESHELP + LangPack.YOU_CAN_GET_MORE_HELP_ABOUT_ + MORE_HELP);
			} else {
			    switch(args[1].toLowerCase(Locale.ENGLISH)){
			        case "buyfor":
			            sender.sendMessage(LangPack.USAGE + LP + "buyfor " + DP + "%_OF_SELL_PRICE" + RESET + LangPack.BUYFORHELP);
			            break;
			        case "collect":
			            sender.sendMessage(LangPack.USAGE + LP + "collect [-c] [" + DP + "AMOUNT" + LP + "]" + RESET + LangPack.COLLECTHELP);
			            break;
			        case "ban":
			            sender.sendMessage(LangPack.USAGE + LP + "ban " + DP + "PLAYER" + RESET + LangPack.BANHELP);
			            break;
			        case "unban":
			            sender.sendMessage(LangPack.USAGE + LP + "unban " + DP + "PLAYER" + RESET + LangPack.UNBANHELP);
			            break;
			        case "kick":
			            sender.sendMessage(LangPack.USAGE + LP + "kick [-o] " + DP + "PLAYER" + RESET + LangPack.KICKHELP);
			            break;
			        case "startsale":
			            sender.sendMessage(LangPack.USAGE + LP + "startsale " + DP + "%_OFF" + LP  + " [" + DP + "ITEM(S)" + LP + "]" + RESET
			                    + LangPack.STARTSALEHELP + DP + "ITEMID" + LP + "[:" + DP + "DATA" + LP +"]" + RESET + LangPack.STARTSALEHELP2);
			            break;
			        case "endsale":
			            sender.sendMessage(LangPack.USAGE + LP + "endsale" + RESET + LangPack.ENDSALEHELP);
			            break;
			        case "broadcast":
			            sender.sendMessage(LangPack.USAGE + LP + "broadcast (-c [" + DP + "AMOUNT" + LP + "])|" + DP + "MESSAGE" + RESET + ". Help text to be added..");//TODO add help text
			            break;
			    }
			}
			return true;
		}
		return null;
	}
	
	/**
	 * Checks if player is allowed to manage the store named in args[0] \
	 * , that is (if player owned store) if they are the owner or \
	 * (if admin store) if they have the rsset permission.
	 * 
	 * Also sets the necessary variable.
	 * @return True if player is allowed to manage the store, false otherwise.
	 */
	private boolean determineIfOwner(){
		if(RealShopping.shopExists(args[0])){
		    shop = RealShopping.getShop(args[0]);
			if(shop.getOwner().equalsIgnoreCase("@admin")){
				if(player == null){
					isOwner = true;
				} else if(player.hasPermission("realshopping.rsset")){
					isOwner = true;
				}
			} else {
				if(player != null){
					if(shop.getOwner().equalsIgnoreCase(player.getName())){
						isOwner = true;
					}
				}
			}
		} else {
			sender.sendMessage(DR + args[0] + RD + LangPack.DOESNTEXIST);
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
					sender.sendMessage(DR + args[2] + RD + LangPack.ISNOTANINTEGER);
				}
			}
		} else if(args.length == 4){
			if(!args[2].equalsIgnoreCase("-c")) return false;
				try {
        					amount = Integer.parseInt(args[3]);
        					cFlag = true;
				} catch(NumberFormatException e){
					sender.sendMessage(DR + args[3] + RD + LangPack.ISNOTANINTEGER);
				}
		}
		if(player != null){
			if(!shop.getOwner().equalsIgnoreCase("@admin")){
				if(cFlag){
					if(Config.isAllowFillChests()){
						if(!RealShopping.hasPInv(player) || shop.getOwner().equals(player.getName())){
							if(player.getLocation().getBlock().getState() instanceof Chest){
								if(shop.hasStolenToClaim()){
									if(amount == 0 || amount > 27) amount = 27;
									ItemStack[] tempIs = new ItemStack[27];
									int i = 0;
									for(;i < amount;i++){
										tempIs[i] = shop.claimStolenToClaim();
										if(tempIs[i] == null) break;
									}
									ItemStack[] oldCont = ((Chest)player.getLocation().getBlock().getState()).getBlockInventory().getContents();
									for(ItemStack tempIS:oldCont) if(tempIS != null) player.getWorld().dropItem(player.getLocation(), tempIS);
									((Chest)player.getLocation().getBlock().getState()).getBlockInventory().setContents(tempIs);
									player.sendMessage(GR + LangPack.FILLEDCHESTWITH + i + LangPack.ITEMS);
									return true;
								} else sender.sendMessage(RD + LangPack.NOTHINGTOCOLLECT);
							} else sender.sendMessage(RD + LangPack.THEBLOCKYOUSELECTEDISNTACHEST);
						} else sender.sendMessage(RD + LangPack.YOUCANTCOLLECT_YOUDONOTOWN);
					} else {
						sender.sendMessage(RD + LangPack.YOUCANTCOLLECT_SERVER);
						return true;
					}
				} else {
					int i = 0;
					for(;amount == 0 || i < amount;i++){
						ItemStack tempIs = shop.claimStolenToClaim();
						if(tempIs != null) player.getWorld().dropItem(player.getLocation(), tempIs);
						else break;
					}
					player.sendMessage(GR + LangPack.DROPPED + i + LangPack.ITEMS);
				}
			}
		} else sender.sendMessage(RD + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
		return false;
	}
	
	private boolean buyfor(){
		if(args.length != 3) return false; 
		if(Config.isEnableSelling()){
			try {
				int pcnt = Integer.parseInt(args[2]);
				if(pcnt <= 100){
					if(pcnt >= 0){
						shop.setBuyFor(pcnt);
						if(pcnt > 0) sender.sendMessage(GR + LangPack.BUYSFOR + DG + pcnt + GR + LangPack.PCNTOFORIGINAL);
						else sender.sendMessage(RD + LangPack.NOTBUYINGFROMPLAYERS);
						RealShopping.updateEntrancesDb();
						return true;
					} else sender.sendMessage(RD + LangPack.YOUCANTUSEAVALUEBELLOW0);
				} else sender.sendMessage(RD + LangPack.YOUCANTUSEAVALUEOVER100);
			} catch(NumberFormatException e){
				sender.sendMessage(DR + args[2] + RD + LangPack.ISNOTANINTEGER);
			}
		} else sender.sendMessage(RD + LangPack.SELLINGTOSTORESISNOTENABLEDONTHISSERVER);
		return false;
	}
	
	private boolean ban(){
		if(args.length != 3) return false;
		if(shop.isBanned(args[2].toLowerCase())) sender.sendMessage(DR + args[2] + RD + LangPack.ISALREADYBANNEDFROMYOURSTORE);
		else {
			shop.addBanned(args[2].toLowerCase());
			sender.sendMessage(GR + LangPack.BANNED + DG + args[2] + GR + LangPack.FROMSTORE);
		}
		RealShopping.updateEntrancesDb();
		return true;
	}
	
	private boolean unban(){
		if(args.length != 3) return false;
			if(shop.isBanned(args[2].toLowerCase())){
 				shop.removeBanned(args[2].toLowerCase());
 				sender.sendMessage(DG + args[2] + GR + LangPack.ISNOLONGERBANNEDFROMYOURSTORE);
 			} else sender.sendMessage(DR + args[2] + RD + LangPack.WASNTBANNEDFROMYOURSTORE);
 			RealShopping.updateEntrancesDb();
 			return true;
	}
	
	private boolean kick(){
	    if(args.length == 3){
	        if(RealShopping.hasPInv(args[2]) && RealShopping.getPInv(args[2]).getShop().getName().equals(args[0])){
	            if(sender.getServer().getPlayerExact(args[2]) != null){
	                //Abandon conversations
	                if(RSPlayerListener.hasConversationListener(player)) RSPlayerListener.killConversationListener(player);
	                if(PromptMaster.isConversing(player)) PromptMaster.abandonConversation(player);

	                RSUtils.returnStolen(sender.getServer().getPlayerExact(args[2]));
	                Location l = shop.getFirstE();
	                RealShopping.removePInv(sender.getServer().getPlayerExact(args[2]));
	                sender.getServer().getPlayerExact(args[2]).teleport(l.add(0.5, 0, 0.5));
	                sender.sendMessage(DG + args[2] + GR + LangPack.WASKICKEDFROMYOURSTORE);
	            } else sender.sendMessage(RD + LangPack.PLAYER + DR + args[2] + RD + LangPack.ISNTONLINEKICK);
	        } else sender.sendMessage(DR + args[2] + RD + LangPack.ISNOTINYOURSTORE);
	        return true;
	    } else if(args.length == 4 && args[2].equalsIgnoreCase("-o")){
	        if(RealShopping.hasPInv(args[3]) && RealShopping.getPInv(args[3]).getShop().getName().equals(args[0])){
	            if(sender.getServer().getOfflinePlayer(args[3]) != null){
	                RealShopping.removePInv(sender.getServer().getOfflinePlayer(args[3]).getName());
	                sender.sendMessage(DG + args[3] + GR + LangPack.WASKICKEDFROMYOURSTORE);
	            } else sender.sendMessage(RD + LangPack.PLAYER + DR + args[3] + RD + LangPack.DOESNTEXIST);
	        } else sender.sendMessage(DR + args[3] + RD + LangPack.ISNOTINYOURSTORE);
	        return true;
	    } else return false;
	}
	
	private boolean startsale(){
        try {
            int pcnt = Integer.parseInt(args[2]);
            if(pcnt < 100){
                if(pcnt > 0){
                    if(shop != null){
                        if(shop.hasPrices()){
                            shop.clearSales();
                            if(args.length == 3){
                                Price[] keys = shop.getPrices().keySet().toArray(new Price[0]);
                                int i = 0;
                                for(;i < keys.length;i++){
                                    shop.addSale(keys[i], pcnt);
                                }
                                if(i > 0){
                                    sender.sendMessage(DG + "" + pcnt + GR + LangPack.PCNTOFF + DG + i + GR + LangPack.ITEMS);
                                    int subs = 0;
                                    for(PSetting pS:RealShopping.getPlayerSettings()){
                                        if(pS.getSalesNotifications(shop) && RealShopping.sendNotification(pS.getPlayer(), 
                                               GR + LangPack.STORE + DG + shop.getName() + GR + " now has a " + DG + pcnt + GR + "% off sale for " + DG + i + GR + LangPack.ITEMS))//LANG
                                        subs++;
                                    }
                                    if(subs > 0) sender.sendMessage(GR + "Informed " + DG + subs + GR + " subscribers about the sale.");//LANG
                                }
                                else sender.sendMessage(RD + LangPack.NOITEMSARESOLDINTHESTORE);
                                return true;
                            } else {//If args.length > 3
                                String[] keys = args[3].split(",");
                                if(keys.length > 0){
                                    int i = 0, j = 0;
                                    for(;i < keys.length;i++){
                                        Price tempP = RSUtils.pullPrice(keys[i],this.player);
                                        if(shop.hasPrice(tempP)){
                                            shop.addSale(tempP, pcnt);
                                            j++;
                                        }
                                    }
                                    if(j > 0){
                                        sender.sendMessage(DG + "" + pcnt + GR + LangPack.PCNTOFF + DG + j + GR + LangPack.ITEMS);
                                        int subs = 0;
                                        for(PSetting pS:RealShopping.getPlayerSettings()){
                                            if(pS.getSalesNotifications(shop) && RealShopping.sendNotification(pS.getPlayer(), 
                                                   GR + LangPack.STORE + DG + shop.getName() + GR + " now has a " + DG + pcnt + GR + "% off sale for " + DG + i + GR + LangPack.ITEMS))
                                            subs++;
                                        }
                                        if(subs > 0) sender.sendMessage(GR + "Informed " + DG + subs + GR + " subscribers about the sale.");
                                    }
                                    else sender.sendMessage(RD + LangPack.NOITEMSARESOLDINTHESTORE);
                                    return true;
                                } else sender.sendMessage(DR + args[3] + RD + LangPack.ISNOTAVALIDARGUMENT);
                            }
                        } else sender.sendMessage(RD + LangPack.NOITEMSARESOLDINTHESTORE);	
                    } else sender.sendMessage(RD + LangPack.STORE + DR + args[0] + RD + LangPack.DOESNTEXIST);
                } else sender.sendMessage(RD + LangPack.YOUCANTUSEAVALUEOF0ORLESS);
            } else  sender.sendMessage(RD + LangPack.YOUCANTUSEAVALUEOF100ORMORE);
        } catch(NumberFormatException e){
            sender.sendMessage(DR + args[2] + RD + LangPack.ISNOTANINTEGER);
        }
        return false;
	}
	
	private boolean endsale(){
	    RealShopping.cancelSaleNotification(shop.getName());
		shop.clearSales();
		sender.sendMessage(GR + LangPack.SALEENDED);
		return true;
	}
	
	private boolean broadcast(){
	    if(args[2].equals("-c")){//Check for the -c flag
	        if(args.length > 3 && args[3].matches("[0-9]+")){
	            int i = Integer.parseInt(args[3]);
	            RealShopping.cancelBroadcasts(shop.getName(), i);//Cancel X most recent broadcasts
	            sender.sendMessage(GR + "Cancelled the " + DG + i + GR + " most recent pending broadcasts from " + DG + shop.getName() + GR + ".");
	        } else {
	            RealShopping.cancelBroadcasts(shop.getName());//Cancel all broadcasts.
	            sender.sendMessage(GR + "Cancelled all pending broadcasts from " + DG + shop.getName() + GR + ".");
	        }
	        return true;
	    }
	    
	    final int MAX = 60;//I think this is a good length for a broadcast.
	    String msg = "";
	    
	    for(int i = 2;i < args.length;i++){
	        if(i > 2) msg += " ";
	        msg += args[i];
	    }
	    
	    if(msg.length() <= MAX){
	        int i = 0;
	        for(PSetting pS:RealShopping.getPlayerSettings()){
	            if(pS.getBroadcastNotifications(shop) && RealShopping.sendNotification(pS.getPlayer(), 
	                    LP + "[" + DP + shop.getName() + LP + "] " + RESET + msg))
	            i++;
	        }
	        if(i == 0) sender.sendMessage(RD + "I'm sorry, but your store doesn't have any subscribers. This broadcast won't reach anybody.");//LANG
	        else {
	            sender.sendMessage(GR + "Successfully broadcasted message to " + DG + i + GR + " subscribers.");//LANG
	            sender.sendMessage(GR + "The message was: " + RESET + msg);//LANG
	        }
	    } else sender.sendMessage(RD + "The broadcast cannot be longer than " + DR + MAX + RD + " characters.");//LANG
	    return true;
	}
	
	@Override
	protected boolean execute() {
		if(!determineIfOwner()) return false;
		
		if(args.length == 1){//First argument MUST be store
			sender.sendMessage(GR + LangPack.STORE + ChatColor.YELLOW + args[0] + ((shop.getOwner().equalsIgnoreCase("@admin"))?"":GR + LangPack.OWNEDBY + DG + shop.getOwner()));
			if(shop.getBuyFor() > 0) sender.sendMessage(GR + LangPack.BUYSFOR + DG + shop.getBuyFor() + GR + LangPack.PCNTOFORIGINAL);
			if(shop.hasSales()) sender.sendMessage(GR + LangPack.HASA + DG + shop.getFirstSale() + GR + LangPack.PCNTOFFSALERIGHTNOW);
			if(!RealShopping.getPlayersInStore(args[0])[0].equals("")){
				sender.sendMessage(DG + LangPack.PLAYERSINSTORE + "\n" + RESET + RSUtils.formatPlayerListToMess(RealShopping.getPlayersInStore(args[0])));
			}
			sender.sendMessage(GR + LangPack.FORHELPTYPE + LP + "/rsstores help");
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
            } else if(args.length > 2 && args[1].equalsIgnoreCase("broadcast")){
                return broadcast();
            }
		} else sender.sendMessage(RD + LangPack.YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE);
		return false;
	}

}
