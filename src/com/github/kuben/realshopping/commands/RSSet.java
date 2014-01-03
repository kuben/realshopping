package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.exceptions.RealShoppingException;
import com.github.kuben.realshopping.prompts.PromptMaster;
import com.github.kuben.realshopping.prompts.PromptMaster.PromptType;

class RSSet extends RSPlayerCommand {

	public RSSet(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if(!RealShopping.hasPInv(player)){
                    if(args.length == 1) {
                        switch(args[0].toLowerCase()) {
                            case "prompt":
                                return PromptMaster.createConversation(PromptType.SETUP_STORE, player);
                            case "entrance":
                                RealShopping.setEntrance(player);
				player.sendMessage(ChatColor.GREEN + LangPack.ENTRANCEVARIABLESETTO + RSUtils.locAsString(RealShopping.getEntrance()));
				return true;
                            case "exit":
                                RealShopping.setExit(player);
				player.sendMessage(ChatColor.GREEN + LangPack.EXITVARIABLESETTO + RSUtils.locAsString(RealShopping.getExit()));
				return true;
                            default:
                                return false;
                        }
                    }
                    if(args.length == 2) {
                        switch(args[0].toLowerCase()) {
                            case "createstore":
                                if(!RealShopping.hasEntrance()) {
                                    player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
                                    return true;
                                }
                                if(!RealShopping.hasExit()) {
                                    player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
                                    return true;
                                }
                                if(args[1].equals("help")){
                                    sender.sendMessage(ChatColor.RED + LangPack.YOUCANTNAMEASTORETHAT);
                                    return true;
                                }
                                if(!RealShopping.shopExists(args[1])){//Create
                                    RealShopping.addShop(new Shop(args[1], player.getWorld().getName(), "@admin"));
                                    player.sendMessage(ChatColor.RED + args[1] + LangPack.WASCREATED);
                                }
                            case "addeepair":
                                if(RealShopping.hasEntrance()){
                                    if(RealShopping.hasExit()){
                                        if(RealShopping.shopExists(args[1])){//Create
                                            try {
                                                RealShopping.getShop(args[1]).addEntranceExit(RealShopping.getEntrance(), RealShopping.getExit());
                                                RealShopping.updateShopsDb();
                                                player.sendMessage(ChatColor.GREEN + "Entrance/exit pair was added successfully");
                                            } catch(RealShoppingException e){
                                                player.sendMessage(ChatColor.RED + LangPack.THIS_ENTRANCE_AND_EXIT_PAIR_IS_ALREADY_USED);
                                            }
                                                return true;
                                            }
                                        } else player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
				} else player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
                                return false;
                            case "delstore":
                                if(RealShopping.shopExists(args[1])){
                                    if(RealShopping.getPlayersInStore(args[1])[0].equals("")){
                                        RealShopping.getShop(args[1]).clearEntrancesExits();
                                        RealShopping.removeShop(args[1]);
                                        player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
                                        RealShopping.updateShopsDb();
                                    } else player.sendMessage(ChatColor.RED + LangPack.STORENOTEMPTY);
                                    return true;
				} else player.sendMessage(ChatColor.RED + args[1] + LangPack.WASNTFOUND);
                            default:
                                return false;
                        }
                    }
                    if(args.length == 3) {
                        switch(args[0].toLowerCase()){
                            case "deleepair":
                                if(RealShopping.shopExists(args[1])) {
                                    try {
                                        int index = Integer.parseInt(args[2]);
                                        Shop shop = RealShopping.getShop(args[1]);
                                        shop.removeEEPair(player,index);
                                        RealShopping.updateShopsDb();
                                    } catch (NumberFormatException e) {
                                        player.sendMessage("Index is not a number, aborting.");
                                        return false;
                                    }
                                    return true;
                                }
                                return false;
                            case "listeepairs":
                                if(RealShopping.shopExists(args[1])) {
                                    int page = 1;
                                    try {
                                        page = Integer.parseInt(args[2]);
                                    } catch (NumberFormatException e) {
                                        player.sendMessage("Page is not a number, printing the first page.");
                                    } finally {
                                        Shop.listEEPairs(sender, page, RealShopping.getShop(args[1]));
                                    }
                                    return true;
                                }
                                return false;
                            default:
                                return false;
                        }
                    }
		} else player.sendMessage(ChatColor.RED + LangPack.YOU_CANT_USE_THIS_COMMAND_INSIDE_A_STORE);
		return false;
	}
	
	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE
                                        + ChatColor.RESET + "/rsset prompt|entrance|exit|createstore|delstore|addeepair|deleepair|listeepairs [NAME]");
				sender.sendMessage(LangPack.FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ + ChatColor.LIGHT_PURPLE + "/rsset help " + ChatColor.DARK_PURPLE + "COMMAND");
			} else if(args.length == 1){
				sender.sendMessage(LangPack.RSSETHELP + LangPack.YOU_CAN_GET_MORE_HELP_ABOUT_
						+ ChatColor.LIGHT_PURPLE + "prompt, entrance, exit, createstore, delstore, addeepair, deleepair, listeepairs");
			} else {
                            switch(args[1].toLowerCase()) {
                                case "prompt":
                                    sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "prompt" + ChatColor.RESET + LangPack.STARTS_AN_INTERACTIVE_PROMPT
                                            + LangPack.ALL_CONVERSATIONS_CAN_BE_ABORTED_WITH_ + ChatColor.LIGHT_PURPLE + "quit");
                                    return true;
                                case "entrance":
                                    sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "entrance"
                                            + ChatColor.RESET + LangPack.RSSETENTRANCEHELP);
                                    return true;
                                case "exit":
                                    sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "exit"
                                            + ChatColor.RESET + LangPack.RSSETEXITHELP);
                                    return true;
                                case "createstore":
                                    sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "createstore NAME"
                                            + ChatColor.RESET + LangPack.RSSETCREATEHELP);
                                    return true;
                                case "delstore":
                                    sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "delstore NAME"
                                            + ChatColor.RESET + LangPack.RSSETDELSTOREHELP);
                                    return true;
                                case "addeepair":
                                    sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "addeepair NAME"
						+ ChatColor.RESET + LangPack.RSSETDELENHELP);
                                    return true;
                                case "deleepair":
                                    sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "deleepair NAME INDEX"
						+ ChatColor.RESET + LangPack.RSSETDELENHELP);
                                    return true;
                                case "listeepairs":
                                    sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "listeepairs NAME PAGE"
						+ ChatColor.RESET + LangPack.RSSETDELENHELP);
                                default:
                                    return true;
                            }
			}
			return true;
		}
		return null;
	}

}