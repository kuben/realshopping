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
			if (args.length == 1 && args[0].equalsIgnoreCase("prompt")){
				return PromptMaster.createConversation(PromptType.SETUP_STORE, player);
			} else if (args.length == 1 && args[0].equalsIgnoreCase("entrance")){
				RealShopping.setEntrance(player);
				player.sendMessage(ChatColor.GREEN + LangPack.ENTRANCEVARIABLESETTO + RSUtils.locAsString(RealShopping.getEntrance()));
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("exit")){
				RealShopping.setExit(player);
				player.sendMessage(ChatColor.GREEN + LangPack.EXITVARIABLESETTO + RSUtils.locAsString(RealShopping.getExit()));
				return true;
			} else if (args.length == 2 && args[0].equalsIgnoreCase("createstore")){
				if(RealShopping.hasEntrance()){
					if(RealShopping.hasExit()){
						if(args[1].equals("help")){
							sender.sendMessage(ChatColor.RED + LangPack.YOUCANTNAMEASTORETHAT);
							return true;
						}
				    	if(!RealShopping.shopMap.containsKey(args[1])){//Create
				    		RealShopping.shopMap.put(args[1], new Shop(args[1], player.getWorld().getName(), "@admin"));
				    	}
				    	try {
					    	RealShopping.shopMap.get(args[1]).addEntranceExit(RealShopping.getEntrance(), RealShopping.getExit());
					    	RealShopping.updateEntrancesDb();
							player.sendMessage(ChatColor.RED + args[1] + LangPack.WASCREATED);
				    	} catch(RealShoppingException e){
				    		player.sendMessage(ChatColor.RED + LangPack.THIS_ENTRANCE_AND_EXIT_PAIR_IS_ALREADY_USED);
				    	}
						return true;
					} else player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
				} else player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
			} else if (args.length == 2 && args[0].equalsIgnoreCase("delstore")){
				if(RealShopping.shopMap.containsKey(args[1])){
					if(RealShopping.getPlayersInStore(args[1])[0].equals("")){
						RealShopping.shopMap.get(args[1]).clearEntrancesExits();
						RealShopping.shopMap.remove(args[1]);
						player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
						RealShopping.updateEntrancesDb();
					} else player.sendMessage(ChatColor.RED + LangPack.STORENOTEMPTY);
					return true;
				} else player.sendMessage(ChatColor.RED + args[1] + LangPack.WASNTFOUND);
			}
		} else player.sendMessage(ChatColor.RED + LangPack.YOU_CANT_USE_THIS_COMMAND_INSIDE_A_STORE);
		return false;
	}
	
	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rsset prompt|entrance|exit|createstore|delstore [NAME]");
				sender.sendMessage(LangPack.FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ + ChatColor.LIGHT_PURPLE + "/rsset help " + ChatColor.DARK_PURPLE + "COMMAND");
			} else if(args.length == 1){
				sender.sendMessage(LangPack.RSSETHELP + LangPack.YOU_CAN_GET_MORE_HELP_ABOUT_
						+ ChatColor.LIGHT_PURPLE + "prompt, entrance, exit, createstore, delstore, delen");
			} else {
				if(args[1].equals("prompt")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "prompt" + ChatColor.RESET + LangPack.STARTS_AN_INTERACTIVE_PROMPT
						+ LangPack.ALL_CONVERSATIONS_CAN_BE_ABORTED_WITH_ + ChatColor.LIGHT_PURPLE + "quit");
				else if(args[1].equals("entrance")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "entrance"
						+ ChatColor.RESET + LangPack.RSSETENTRANCEHELP);
				else if(args[1].equals("exit")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "exit"
						+ ChatColor.RESET + LangPack.RSSETEXITHELP);
				else if(args[1].equals("createstore")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "createstore NAME"
						+ ChatColor.RESET + LangPack.RSSETCREATEHELP);
				else if(args[1].equals("delstore")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "delstore NAME"
						+ ChatColor.RESET + LangPack.RSSETDELSTOREHELP);
				else if(args[1].equals("delen")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "delen"//FIXME needs to be added
						+ ChatColor.RESET + LangPack.RSSETDELENHELP);
			}
			return true;
		}
		return null;
	}

}