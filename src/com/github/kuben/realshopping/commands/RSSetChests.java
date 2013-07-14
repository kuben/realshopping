package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.prompts.PromptMaster;
import com.github.kuben.realshopping.prompts.PromptMaster.PromptType;

class RSSetChests extends RSPlayerCommand {

	private Shop tempShop;
	private Location l;
	public RSSetChests(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if(RealShopping.hasPInv(player)){
			tempShop = RealShopping.shopMap.get(RealShopping.getPInv(player).getStore());
			if(tempShop.getOwner().equals("@admin")){
				if (args.length == 1 && args[0].equalsIgnoreCase("prompt")){
					return PromptMaster.createConversation(PromptType.CHOOSE_CHESTS, player);
    			} else {
    				l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().subtract(0, 0.875, 0).getBlockY(), player.getLocation().getBlockZ());
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
        				return additems();
        			} else if (args.length == 2 && args[0].equalsIgnoreCase("delitems")){
        				return delitems();
        			}
    			}
			} else sender.sendMessage(ChatColor.RED + LangPack.ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS);
		} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTORETOUSETHISCOMMAND);
		return false;
	}

	private boolean additems(){
		try {
			int j = tempShop.addChestItem(l, RSUtils.pullItems(args[1]));
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
		return false;
	}
	
	private boolean delitems(){
		try {
			int[][] ids = RSUtils.pullItems(args[1]);
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
		return false;
	}
		
	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rssetchests prompt|create|del|additems|delitems [ITEMID[:DATA[:AMOUNT]]][*X][,(more items)]");
				sender.sendMessage(LangPack.FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ + ChatColor.LIGHT_PURPLE + "/rssetchests help " + ChatColor.DARK_PURPLE + "COMMAND");
			} else if(args.length == 1){
				sender.sendMessage(LangPack.RSSETCHESTSHELP + LangPack.YOU_CAN_GET_MORE_HELP_ABOUT_
						+ ChatColor.LIGHT_PURPLE + "prompt, create, del, additems, delitems");
			} else {
				if(args[1].equals("prompt")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "prompt" + ChatColor.RESET + LangPack.RSSETCHESTSHELP
						+ LangPack.ALL_CONVERSATIONS_CAN_BE_ABORTED_WITH_ + ChatColor.DARK_PURPLE + "quit");
				else if(args[1].equals("create")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "create"
						+ ChatColor.RESET + LangPack.RSSETCHESTSCREATEHELP);
				else if(args[1].equals("del")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "del"
						+ ChatColor.RESET + LangPack.RSSETCHESTSDELHELP);
				else if(args[1].equals("additems")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "additems [ITEMID[:DATA[:AMOUNT]][*X] [,MORE_ITEMS.. [,...]]"
						+ ChatColor.RESET + LangPack.RSSETCHESTSADDITEMSHELP);
				else if(args[1].equals("delitems")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "delitems [ITEMID[:DATA]][*X] [,MORE_ITEMS.. [,...]]"
						+ ChatColor.RESET + LangPack.RSSETCHESTSDELITEMSHELP);
			}
			return true;
		}
		return null;
	}
	
}