package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;

class RSSet extends RSPlayerCommand {

	public RSSet(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {//TODO add help
		if (args.length == 1 && args[0].equalsIgnoreCase("entrance")){
			RealShopping.entrance = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
			player.sendMessage(ChatColor.RED + LangPack.ENTRANCEVARIABLESETTO + RealShopping.entrance);
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("exit")){
			RealShopping.exit = player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
			player.sendMessage(ChatColor.RED + LangPack.EXITVARIABLESETTO + RealShopping.exit);
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("createstore")){
			if(args[1].equals("help")){
				sender.sendMessage(ChatColor.RED + LangPack.YOUCANTNAMEASTORETHAT);
				return true;
			}
			if(!RealShopping.entrance.equals("")){
				if(!RealShopping.exit.equals("")){
			    	if(!RealShopping.shopMap.containsKey(args[1])){//Create
			    		RealShopping.shopMap.put(args[1], new Shop(args[1], player.getWorld().getName(), "@admin"));
			    	}
			    	//Add entrance
			    	Location en = new Location(player.getServer().getWorld(RealShopping.shopMap.get(args[1]).getWorld()), Integer.parseInt(RealShopping.entrance.split(",")[0]),Integer.parseInt(RealShopping.entrance.split(",")[1]), Integer.parseInt(RealShopping.entrance.split(",")[2]));
			    	Location ex = new Location(player.getServer().getWorld(RealShopping.shopMap.get(args[1]).getWorld()), Integer.parseInt(RealShopping.exit.split(",")[0]),Integer.parseInt(RealShopping.exit.split(",")[1]), Integer.parseInt(RealShopping.exit.split(",")[2]));
			    	RealShopping.shopMap.get(args[1]).addE(en, ex);
			    	RealShopping.updateEntrancesDb();
					player.sendMessage(ChatColor.RED + args[1] + LangPack.WASCREATED);
					return true;
				} else player.sendMessage(ChatColor.RED + LangPack.THERSNOEXITSET);
			} else player.sendMessage(ChatColor.RED + LangPack.THERESNOENTRANCESET);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("delstore")){
			if(RealShopping.shopMap.containsKey(args[1])){
				if(RealShopping.getPlayersInStore(args[1].toLowerCase())[0].equals("")){
					RealShopping.shopMap.remove(args[1]);
					player.sendMessage(ChatColor.RED + args[1] + LangPack.WASREMOVED);
					RealShopping.updateEntrancesDb();
				} else player.sendMessage(ChatColor.RED + LangPack.STORENOTEMPTY);
				return true;
			} else player.sendMessage(ChatColor.RED + args[1] + LangPack.WASNTFOUND);
		}
		return false;
	}

}