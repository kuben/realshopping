package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RealShoppingException;

class RSSet extends RSPlayerCommand {

	public RSSet(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if (args.length == 1 && args[0].equalsIgnoreCase("entrance")){
			RealShopping.setEntrance(player);
			player.sendMessage(ChatColor.RED + LangPack.ENTRANCEVARIABLESETTO + RealShopping.getEntrance());
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("exit")){
			RealShopping.setExit(player);
			player.sendMessage(ChatColor.RED + LangPack.EXITVARIABLESETTO + RealShopping.getExit());
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("createstore")){
			if(args[1].equals("help")){
				sender.sendMessage(ChatColor.RED + LangPack.YOUCANTNAMEASTORETHAT);
				return true;
			}
			if(RealShopping.hasEntrance()){
				if(RealShopping.hasExit()){//TODO test if blank store doesn't cause crash
			    	if(!RealShopping.shopMap.containsKey(args[1])){//Create
			    		RealShopping.shopMap.put(args[1], new Shop(args[1], player.getWorld().getName(), "@admin"));
			    	}
			    	try {
				    	RealShopping.shopMap.get(args[1]).addEntranceExit(RealShopping.getEntrance(), RealShopping.getExit());
				    	RealShopping.updateEntrancesDb();
						player.sendMessage(ChatColor.RED + args[1] + LangPack.WASCREATED);
			    	} catch(RealShoppingException e){
			    		player.sendMessage(ChatColor.RED + "This entrance and exit pair is already used.");//LANG
			    	}
					return true;
				} else player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
			} else player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("delstore")){
			if(RealShopping.shopMap.containsKey(args[1])){
				if(RSUtils.getPlayersInStore(args[1].toLowerCase())[0].equals("")){
					RealShopping.shopMap.remove(args[1]);
					player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
					RealShopping.updateEntrancesDb();
				} else player.sendMessage(ChatColor.RED + LangPack.STORENOTEMPTY);
				return true;
			} else player.sendMessage(ChatColor.RED + args[1] + LangPack.WASNTFOUND);
		}
		return false;
	}
	
	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){//LANG
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rsset prompt|entrance|exit|createstore|delstore [NAME]");
				sender.sendMessage("For help for a specific command, type: " + ChatColor.DARK_PURPLE + "/rsset help COMMAND");
			} else if(args.length == 1){
				sender.sendMessage("Creates or deletes player owned stores, as well as entrances/exits to them. Use the prompt argument for a guide, or the other arguments to create stores manually. You can get more help about each of these arguments: " + ChatColor.DARK_PURPLE + "prompt, entrance, exit, createstore, delstore, delen");
			} else {
				if(args[1].equals("prompt")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "prompt" + ChatColor.RESET + ". Starts an interactive prompt.");
				else if(args[1].equals("entrance")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "entrance" + ChatColor.RESET
						+ ". Stores the location of the block you stand on to an entrance variable.");
				else if(args[1].equals("exit")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "exit" + ChatColor.RESET
						+ ". Stores the location of the block you stand on to an exit variable.");
				else if(args[1].equals("createstore")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "createstore NAME" + ChatColor.RESET
						+ ". If no store by that name exists, this command creates it with the entrance and exit set. If a store already exists (and is an admin store) then the entrance and exit pair get appended to it.");
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