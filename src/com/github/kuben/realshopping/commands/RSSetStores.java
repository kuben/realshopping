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
	protected boolean execute() {//TODO add help
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

}