package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RSEconomy;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.exceptions.RealShoppingException;
import com.github.kuben.realshopping.prompts.PromptMaster;
import com.github.kuben.realshopping.prompts.PromptMaster.PromptType;
import com.github.stengun.realshopping.SerializationManager;

class RSSetStores extends RSPlayerCommand {

	public RSSetStores(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if(!RealShopping.hasPInv(player)){
			if (args.length == 1 && args[0].equalsIgnoreCase("prompt")){
				return PromptMaster.createConversation(PromptType.SETUP_PLAYER_STORE, player);
			} else if (args.length == 1 && args[0].equalsIgnoreCase("entrance")){
				RealShopping.addPlayerEntrance(player);
				player.sendMessage(ChatColor.GREEN + LangPack.ENTRANCEVARIABLESETTO + RSUtils.locAsString(RealShopping.getPlayerEntrance(player.getName())));
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("exit")){
				RealShopping.addPlayerExit(player);
				player.sendMessage(ChatColor.GREEN + LangPack.EXITVARIABLESETTO + RSUtils.locAsString(RealShopping.getPlayerExit(player.getName())));
				return true;
			} else if (args.length == 2 && args[0].equalsIgnoreCase("createstore")){
				if(RealShopping.hasPlayerEntrance(player.getName())){
					if(RealShopping.hasPlayerExit(player.getName())){
						if(args[1].equals("help")){
							sender.sendMessage(ChatColor.RED + LangPack.YOUCANTNAMEASTORETHAT);
							return true;
						}
				    	if(!RealShopping.shopExists(args[1])){//Create
	    					if(RSEconomy.getBalance(player.getName()) < Config.getPstorecreate()) {
	    						player.sendMessage(ChatColor.RED + LangPack.CREATINGASTORECOSTS + Config.getPstorecreate() + LangPack.UNIT);
	    						return true;
	    					} else {
	    						RSEconomy.withdraw(player.getName(), Config.getPstorecreate());
	    						RealShopping.addShop(new Shop(args[1], player.getWorld().getName(), player.getName()));
	    					}
				    	}
				    	if(RealShopping.getShop(args[1]).getOwner().equals(player.getName())){
				    		try {
								RealShopping.getShop(args[1]).addEntranceExit(RealShopping.getPlayerEntrance(player.getName())
									, RealShopping.getPlayerExit(player.getName()));
					    		SerializationManager.saveShops();
					    		player.sendMessage(ChatColor.GREEN + args[1] + LangPack.WASCREATED);
							} catch (RealShoppingException e) {
								player.sendMessage(ChatColor.RED + LangPack.THIS_ENTRANCE_AND_EXIT_PAIR_IS_ALREADY_USED);
							}
				    	} else {
				    		player.sendMessage(ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE);
				    	}
						return true;
					} else player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
				} else player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
			} else if (args.length == 2 && args[0].equalsIgnoreCase("delstore")){
				if(RealShopping.shopExists(args[1])){
					if(RealShopping.getShop(args[1]).getOwner().equals(player.getName())){
						if(RealShopping.getPlayersInStore(args[1])[0].equals("")){
							RealShopping.getShop(args[1]).clearEntrancesExits();
							RealShopping.removeShop(args[1]);
							player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
							SerializationManager.saveShops();
						} else player.sendMessage(ChatColor.RED + LangPack.STORENOTEMPTY);
					} else player.sendMessage(ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE);
					return true;
				} else player.sendMessage(ChatColor.RED + args[1] + LangPack.WASNTFOUND);
			}
			return true;
		} else player.sendMessage(ChatColor.RED + LangPack.YOU_CANT_USE_THIS_COMMAND_INSIDE_A_STORE);
		return false;
	}

	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rssetstores prompt|entrance|exit|createstore|delstore [NAME]");
				sender.sendMessage(LangPack.FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ + ChatColor.LIGHT_PURPLE + "/rssetstores help " + ChatColor.DARK_PURPLE + "COMMAND");
			} else if(args.length == 1){
				sender.sendMessage(LangPack.RSSETSTORESHELP + LangPack.YOU_CAN_GET_MORE_HELP_ABOUT_
						+ ChatColor.LIGHT_PURPLE + "prompt, entrance, exit, createstore, delstore, delen");
			} else {
				if(args[1].equals("prompt")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "prompt" + ChatColor.RESET + LangPack.STARTS_AN_INTERACTIVE_PROMPT
						+ LangPack.ALL_CONVERSATIONS_CAN_BE_ABORTED_WITH_ + ChatColor.DARK_PURPLE + "quit");
				else if(args[1].equals("entrance")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "entrance"
						+ ChatColor.RESET + LangPack.RSSETENTRANCEHELP);
				else if(args[1].equals("exit")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "exit"
						+ ChatColor.RESET + LangPack.RSSETEXITHELP);
				else if(args[1].equals("createstore")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "createstore NAME"
						+ ChatColor.RESET + LangPack.RSSETCREATEHELP);
				else if(args[1].equals("delstore")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "delstore NAME"
						+ ChatColor.RESET + LangPack.RSSETDELSTOREHELP);
				else if(args[1].equals("delen")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "delen"
						+ ChatColor.RESET + LangPack.RSSETDELENHELP);
			}
			return true;
		}
		return null;
	}
}