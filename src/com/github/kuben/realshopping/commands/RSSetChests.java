package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;

class RSSetChests extends RSPlayerCommand {

	public RSSetChests(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {//TODO add help
		if(RealShopping.PInvMap.containsKey(player.getName())){
			Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
			Shop tempShop = RealShopping.shopMap.get(RealShopping.PInvMap.get(player.getName()).getStore());
			if(tempShop.getOwner().equals("@admin")){
    			if (args.length == 1 && args[0].equalsIgnoreCase("create")){
    				if(tempShop.addChest(l)){
    					player.sendMessage(ChatColor.RED + LangPack.CHESTCREATED);
    					RealShopping.updateEntrancesDb();
    				}
    				else player.sendMessage(ChatColor.RED + LangPack.ACHESTALREADYEXISTSONTHISLOCATION);
    				return true;
    			} else if (args.length == 1 && args[0].equalsIgnoreCase("del")){
    				if(tempShop.delChest(l)){
    					player.sendMessage(ChatColor.RED + LangPack.CHESTREMOVED);
    					RealShopping.updateEntrancesDb();
    				}
    				else player.sendMessage(ChatColor.RED + LangPack.COULDNTFINDCHESTONTHISLOCATION);
    				return true;
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("additems")){
    				try {
    					int[][] ids = new int[args[1].split(",").length][3];
    					for(int i = 0;i < args[1].split(",").length && i < 27;i++){
							if(args[1].split(",")[i].contains(":")){
								ids[i][0] = Integer.parseInt(args[1].split(",")[i].split(":")[0].trim());
								ids[i][1] = Integer.parseInt(args[1].split(",")[i].split(":")[1].trim());
								if(args[1].split(",")[i].split(":").length > 2)
									ids[i][2] = Integer.parseInt(args[1].split(",")[i].split(":")[2].trim());
								else ids[i][2] = 0;
							} else {
								ids[i][0] = Integer.parseInt(args[1].split(",")[i]);
								ids[i][1] = 0;
								ids[i][2] = 0;
							}
    					}
    					int j = tempShop.addChestItem(l, ids);
    					if(j > -1){
    						sender.sendMessage(ChatColor.RED + LangPack.ADDED + j + LangPack.ITEMS);
    						RealShopping.updateEntrancesDb();
    						return true;
    					} else {
    						sender.sendMessage(ChatColor.RED + LangPack.THISCHESTDOESNTEXIST);
    					}
    				} catch (NumberFormatException e){
    					sender.sendMessage(ChatColor.RED + LangPack.ONEORMOREOFTHEITEMIDSWERENOTINTEGERS + args[1]);
    				}
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("delitems")){
    				try {
    					int[][] ids = new int[args[1].split(",").length][2];
    					for(int i = 0;i < args[1].split(",").length;i++){
							if(args[1].split(",")[i].contains(":")){
								ids[i][0] = Integer.parseInt(args[1].split(",")[i].split(":")[0].trim());
								ids[i][1] = Integer.parseInt(args[1].split(",")[i].split(":")[1].trim());
							} else {
								ids[i][0] = Integer.parseInt(args[1].split(",")[i].trim());
								ids[i][1] = 0;
							}
    					}
    					int j = tempShop.delChestItem(l, ids);
    					if(j > -1){
    						sender.sendMessage(ChatColor.RED + LangPack.REMOVED + j + LangPack.ITEMS);
    						RealShopping.updateEntrancesDb();
    						return true;
    					} else {
    						sender.sendMessage(ChatColor.RED + LangPack.THISCHESTDOESNTEXIST);
    					}
    				} catch (NumberFormatException e){
    					sender.sendMessage(ChatColor.RED + LangPack.ONEORMOREOFTHEITEMIDSWERENOTINTEGERS + args[1]);
    				}
    			}
			} else sender.sendMessage(ChatColor.RED + LangPack.ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS);
		} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTORETOUSETHISCOMMAND);
		return false;
	}

}