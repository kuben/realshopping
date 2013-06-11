package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RSEconomy;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RealShoppingException;
import com.github.kuben.realshopping.prompts.PromptMaster;
import com.github.kuben.realshopping.prompts.PromptMaster.PromptType;

class RSSetStores extends RSPlayerCommand {

	public RSSetStores(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if (args.length == 1 && args[0].equalsIgnoreCase("prompt")){//TODO add this to help
			return PromptMaster.createConversation(PromptType.SETUP_PLAYER_STORE, player);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("entrance")){
			RealShopping.addPlayerEntrance(player);
			player.sendMessage(ChatColor.RED + LangPack.ENTRANCEVARIABLESETTO + RealShopping.getPlayerEntrance(player.getName()));
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("exit")){
			RealShopping.addPlayerExit(player);
			player.sendMessage(ChatColor.RED + LangPack.EXITVARIABLESETTO + RealShopping.getPlayerExit(player.getName()));
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("createstore")){
			if(RealShopping.hasPlayerEntrance(player.getName())){
				if(RealShopping.hasPlayerExit(player.getName())){
					if(args[1].equals("help")){
						sender.sendMessage(ChatColor.RED + LangPack.YOUCANTNAMEASTORETHAT);
						return true;
					}
			    	if(!RealShopping.shopMap.containsKey(args[1])){//Create
    					if(RSEconomy.getBalance(player.getName()) < Config.getPstorecreate()) {
    						player.sendMessage(ChatColor.RED + LangPack.CREATINGASTORECOSTS + Config.getPstorecreate() + LangPack.UNIT);
    						return true;
    					} else {
    						RSEconomy.withdraw(player.getName(), Config.getPstorecreate());
    						RealShopping.shopMap.put(args[1], new Shop(args[1], player.getWorld().getName(), player.getName()));
    					}
			    	}
			    	if(RealShopping.shopMap.get(args[1]).getOwner().equals(player.getName())){
			    		try {
							RealShopping.shopMap.get(args[1]).addEntranceExit(RealShopping.getPlayerEntrance(player.getName())
								, RealShopping.getPlayerExit(player.getName()));
				    		RealShopping.updateEntrancesDb();
				    		player.sendMessage(ChatColor.GREEN + args[1] + LangPack.WASCREATED);
						} catch (RealShoppingException e) {
							player.sendMessage(ChatColor.RED + "This entrance and exit pair is already used.");//LANG
						}
			    	} else {
			    		player.sendMessage(ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE);
			    	}
					return true;
				} else player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
			} else player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("delstore")){
			if(RealShopping.shopMap.containsKey(args[1])){
				if(RealShopping.shopMap.get(args[1]).getOwner().equals(player.getName())){
					if(RSUtils.getPlayersInStore(args[1].toLowerCase())[0].equals("")){
						RealShopping.shopMap.remove(args[1]);
						player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
						RealShopping.updateEntrancesDb();
					} else player.sendMessage(ChatColor.RED + LangPack.STORENOTEMPTY);
				} else {
					player.sendMessage(ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE);
				}
				return true;
			} else player.sendMessage(ChatColor.RED + args[1] + LangPack.WASNTFOUND);
		}
		return true;
	}

	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){//LANG
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rssetstores prompt|entrance|exit|createstore|delstore [NAME]");
				sender.sendMessage("For help for a specific command, type: " + ChatColor.DARK_PURPLE + "/rssetstores help COMMAND");
			} else if(args.length == 1){
				sender.sendMessage("Creates or deletes player owned stores, as well as entrances/exits to them. Use the prompt argument for a guide, or the other arguments to create stores manually. You can get more help about each of these arguments: " + ChatColor.DARK_PURPLE + "prompt, entrance, exit, createstore, delstore, delen");
			} else {
				if(args[1].equals("prompt")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "prompt" + ChatColor.RESET + ". Starts an interactive prompt.");
				else if(args[1].equals("entrance")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "entrance" + ChatColor.RESET
						+ ". Stores the location of the block you stand on to an entrance variable.");
				else if(args[1].equals("exit")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "exit" + ChatColor.RESET
						+ ". Stores the location of the block you stand on to an exit variable.");
				else if(args[1].equals("createstore")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "createstore NAME" + ChatColor.RESET
						+ ". If no store by that name exists, this command creates it with the entrance and exit set. If a store already exists (and belongs to you) then the entrance and exit pair get appended to it.");
				else if(args[1].equals("delstore")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "delstore NAME" + ChatColor.RESET
						+ ". Wipes the named store off the face of the earth, along with settings and prices. Use with care.");
				else if(args[1].equals("delen")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "delen" + ChatColor.RESET
						+ ". Deletes the entrance and exit pair which you most recently have set with entrance and exit. You can only remove matching entrances and exits.");
			}
			return true;
		}
		return null;
	}
}