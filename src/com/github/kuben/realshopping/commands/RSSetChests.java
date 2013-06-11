package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RSListenerException;
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
		if(RealShopping.PInvMap.containsKey(player.getName())){
			tempShop = RealShopping.shopMap.get(RealShopping.PInvMap.get(player.getName()).getStore());
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
		} catch (RSListenerException e){
			//TODO
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
		} catch (RSListenerException e){
			//TODO
		}
		return false;
	}
		
	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rssetchests prompt|create|del|additems|delitems [ITEMID[:DATA[:AMOUNT]]][*X][,(more items)]");
				sender.sendMessage("For help for a specific command, type: " + ChatColor.DARK_PURPLE + "/rssetchests help COMMAND");
			} else if(args.length == 1){
				sender.sendMessage(ChatColor.GREEN + "Manages self-refilling chests (admin-stores only). Use the prompt argument for a guide, or the other arguments to manage chests manually. You can get more help about each of these arguments: " + ChatColor.DARK_PURPLE + "prompt, create, del, additems, delitems");
			} else {
				if(args[1].equals("prompt")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "prompt" + ChatColor.RESET + ". Starts an interactive prompt.");
				else if(args[1].equals("create")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "create" + ChatColor.RESET
						+ ". The block you stand on becomes a self-refilling chest. It will update when someone enters the store.");
				else if(args[1].equals("del")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "del" + ChatColor.RESET
						+ ". The block you stand on ceases to be a self-refilling chest. It will update when someone enters the store.");
				else if(args[1].equals("additems")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "additems [ITEMID[:DATA[:AMOUNT]][*X][,ITEMID[:DATA[:AMOUNT]]][*X][,...]]" + ChatColor.RESET
						+ ". Adds items to the chest. Multiple items are separated with commas. Add more of the same item by multiplying it with a number. If you omit the data and/or amount field they will default to 1 and a full stack.");
				else if(args[1].equals("delitems")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "delitems [ITEMID[:DATA]][*X][,ITEMID[:DATA]][*X][,...]" + ChatColor.RESET
						+ ". Deletes the first items from the chest, which match the item IDs and data fields of the arguments. It will not delete more items than specified. Stack size is not taken into consideration.");
			}
			return true;
		}
		return null;
	}
	
}