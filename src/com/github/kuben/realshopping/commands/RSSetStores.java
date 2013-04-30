package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RSEconomy;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;

class RSSetStores extends RSPlayerCommand {

	public RSSetStores(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if (args.length == 1 && args[0].equalsIgnoreCase("entrance")){
			String s = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
			RealShopping.playerEntrances.put(player.getName(),s);
			player.sendMessage(ChatColor.RED + LangPack.ENTRANCEVARIABLESETTO + s);
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("exit")){
			String s = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
			RealShopping.playerExits.put(player.getName(), s);
			player.sendMessage(ChatColor.RED + LangPack.EXITVARIABLESETTO + s);
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("createstore")){
			if(RealShopping.playerEntrances.containsKey(player.getName())){
				if(RealShopping.playerExits.containsKey(player.getName())){
					if(args[1].equals("help")){
						sender.sendMessage(ChatColor.RED + LangPack.YOUCANTNAMEASTORETHAT);
						return true;
					}
			    	if(!RealShopping.shopMap.containsKey(args[1])){//Create
    					if(RSEconomy.getBalance(player.getName()) < Config.getPstorecreate()) {
    						player.sendMessage(ChatColor.RED + LangPack.CREATINGASTORECOSTS + Config.getPstorecreate() + RealShopping.unit);
    						return true;
    					} else {
    						RSEconomy.withdraw(player.getName(), Config.getPstorecreate());
    						RealShopping.shopMap.put(args[1], new Shop(args[1], player.getWorld().getName(), player.getName()));
    					}
			    	}
			    	if(RealShopping.shopMap.get(args[1]).getOwner().equals(player.getName())){
			    		//Add entrance
			    		String[] entr = RealShopping.playerEntrances.get(player.getName()).split(",");
			    		String[] ext = RealShopping.playerExits.get(player.getName()).split(",");
			    		Location en = new Location(player.getServer().getWorld(RealShopping.shopMap.get(args[1]).getWorld()), Integer.parseInt(entr[0]),Integer.parseInt(entr[1]), Integer.parseInt(entr[2]));
			    		Location ex = new Location(player.getServer().getWorld(RealShopping.shopMap.get(args[1]).getWorld()), Integer.parseInt(ext[0]),Integer.parseInt(ext[1]), Integer.parseInt(ext[2]));
			    		RealShopping.shopMap.get(args[1]).addE(en, ex);
			    		RealShopping.updateEntrancesDb();
			    		player.sendMessage(ChatColor.GREEN + args[1] + LangPack.WASCREATED);
			    	} else {
			    		player.sendMessage(ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE);
			    	}
					return true;
				} else {
					player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
				}
			} else player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("delstore")){
			if(RealShopping.shopMap.containsKey(args[1])){
				if(RealShopping.shopMap.get(args[1]).getOwner().equals(player.getName())){
					if(RealShopping.getPlayersInStore(args[1].toLowerCase())[0].equals("")){
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