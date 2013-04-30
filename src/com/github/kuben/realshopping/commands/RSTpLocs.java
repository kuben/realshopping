package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;

class RSTpLocs extends RSPlayerCommand {

	public RSTpLocs(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if (args.length == 1){
			if(args[0].equalsIgnoreCase("setwhitelistmode")){
				if(!RealShopping.tpLocBlacklist){
					player.sendMessage(ChatColor.RED + LangPack.WHITELISTMODEALREADYSET);
				} else {
					RealShopping.tpLocBlacklist = false;
					player.sendMessage(ChatColor.GREEN + LangPack.SETWHITELISTMODE);
					return true;
				}
			} else if(args[0].equalsIgnoreCase("setblacklistmode")){
				if(RealShopping.tpLocBlacklist){
					player.sendMessage(ChatColor.RED + LangPack.BLACKLISTMODEALREADYSET);
				} else {
					RealShopping.tpLocBlacklist = true;
					player.sendMessage(ChatColor.GREEN + LangPack.SETBLACKLISTMODE);
					return true;
				}
			} else if(args[0].equalsIgnoreCase("remove")){
				if(RealShopping.forbiddenTpLocs.containsKey(player.getLocation().getBlock().getLocation())){
					RealShopping.forbiddenTpLocs.remove(player.getLocation().getBlock().getLocation());
					player.sendMessage(ChatColor.GREEN + LangPack.REMOVEDONEOFTHE + ((RealShopping.tpLocBlacklist)?"FORBIDDEN":"ALLOWED") + LangPack.TELEPORTLOCATIONS);
					return true;
				} else {
					player.sendMessage(ChatColor.RED + LangPack.THEREISNO + ((RealShopping.tpLocBlacklist)?"FORBIDDEN":"ALLOWED") + LangPack.TELEPORTLOCATIONWITHITSCENTERHERE);
				}
			} else if(args[0].equalsIgnoreCase("highlight")){
					Location[] toHighlight = RealShopping.getNearestTpLocs(player.getLocation().getBlock().getLocation(), 5);
					if(toHighlight != null){
						for(Location l:toHighlight){
							Byte dB;
							int radius = RealShopping.forbiddenTpLocs.get(l);
							if(radius <= 1) dB = 0;
							else if(radius <= 5) dB = 1;
							else if(radius <= 10) dB = 2;
							else if(radius <= 15) dB = 3;
							else if(radius <= 25) dB = 4;
							else if(radius <= 35) dB = 5;
							else if(radius <= 50) dB = 6;
							else if(radius <= 75) dB = 7;
							else if(radius <= 100) dB = 8;
							else if(radius <= 125) dB = 9;
							else if(radius <= 150) dB = 10;
							else if(radius <= 175) dB = 11;
							else if(radius <= 200) dB = 12;
							else if(radius <= 250) dB = 13;
							else if(radius <= 500) dB = 14;
							else dB = 15;
							player.sendBlockChange(l, Material.WOOL, dB);
							/* White Wool		0	r=1
							 * Light Gray Wool 	1	1<=r<5
							 * Gray Wool 		2	5<=r<10
							 * Black Wool 		3	10<=r<15
							 * Red Wool 		4	15<=r<25
							 * Orange Wool 		5	25<=r<35
							 * Yellow Wool 		6	35<=r<50
							 * Lime Wool 		7	50<=r<75
							 * Green Wool 		8	75<=r<100
							 * Cyan Wool 		9	100<=r<125
							 * Light Blue 		10	125<=r<150
							 * Blue Wool 		11	150<=r<175
							 * Purple Wool 		12	175<=r<200
							 * Magenta Wool 	13	200<=r<250
							 * Pink Wool 		14	250<=r<500
							 * Brown Wool		15	500<=r
							 */
						}
						blockUpdater bU = new blockUpdater(toHighlight, player);
						bU.start();
						
						player.sendMessage(ChatColor.GREEN + LangPack.HIGHLIGHTED5LOCATIONSFOR5SECONDS);
					} else {
						player.sendMessage(ChatColor.RED + LangPack.NOLOCATIONSTOHIGHLIGHT);
					}

					return true;
			}
		} else if(args.length == 2 && args[0].equalsIgnoreCase("add")){
			try {
				int radius = Integer.parseInt(args[1]);
				if(RealShopping.forbiddenTpLocs.containsKey(player.getLocation().getBlock().getLocation())){
					player.sendMessage(ChatColor.GREEN + LangPack.OLDRADIUSVALUE + RealShopping.forbiddenTpLocs.put(player.getLocation().getBlock().getLocation(), radius) + LangPack.REPLACEDWITH + radius);
				} else {
					RealShopping.forbiddenTpLocs.put(player.getLocation().getBlock().getLocation(), radius);
					player.sendMessage(ChatColor.GREEN + LangPack.ADDED + ((RealShopping.tpLocBlacklist)?"FORBIDDEN":"ALLOWED") + LangPack.TELEPORTLOCATIONWITHARADIUSOF + radius);
				}
				return true;
			} catch (NumberFormatException e){
				sender.sendMessage(ChatColor.RED + args[1] + LangPack.ISNOTANINTEGER);
			}
		}
		return true;
	}

	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){//LANG
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rstplocs setwhitelistmode|setblacklistmode|add RADIUS|remove|highlight");
				sender.sendMessage("For help for a specific command, type: " + ChatColor.DARK_PURPLE + "/rstplocs help COMMAND");
			} else if(args.length == 1){
				sender.sendMessage("Makes it possible for users to teleport to certain allowed areas from a store, if they have paid for their items. Either you can whitelist some areas and ban all the others, or blacklist some and allow all other. Only make it possible to teleport to areas where there are no stores! You can get more help about each of these arguments: " + ChatColor.DARK_PURPLE + "setwhitelistmode, setblacklistmode, add, remove, highlight");
			} else {
				if(args[1].equals("setwhitelistmode")){
					sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "setwhitelistmode" + ChatColor.RESET
						+ ". Sets the mode used to whitelist (default). This means that listed areas are allowed to teleport to, and all others are banned.");
					sender.sendMessage("When changing modes, areas are not deleted. They remain in the list and just go from being banned to unbanned and vice versa");
				}
				else if(args[1].equals("setblacklistmode")){
					sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "setblacklistmode" + ChatColor.RESET
						+ ". Sets the mode used to blacklist. This means that listed areas are not allowed to teleport to. Other areas are allowed.");
					sender.sendMessage("When changing modes, areas are not deleted. They remain in the list and just go from being banned to unbanned and vice versa");
				}
				else if(args[1].equals("add")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "add RADIUS" + ChatColor.RESET
						+ ". Creates a spheric area around where you are standing with the radius RADIUS. The area will be added to the white- or blacklist.");
				else if(args[1].equals("remove")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "remove" + ChatColor.RESET
						+ ". Deletes the area with the center on the point where you are standing. If you have trouble finding it, use the highlight option.");
				else if(args[1].equals("highlight")){
					sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "highlight" + ChatColor.RESET
						+ ". Changes the centers of the five nearest areas to wool blocks for five seconds and only you to see. Each wool color corresponds to a radius lenght:");
					sender.sendMessage("White - radius is 1, Light Gray - radius is < 5, Gray - radius is < 10, Black - radius is < 15, Red - radius is < 25, Orange - radius is < 35, Yellow - radius is < 50, Lime - radius is < 75, Green - radius is < 100, Cyan - radius is < 125, Light - radius is < 150, Blue - radius is < 175, Purple - radius is < 200, Magenta - radius is < 250, Pink Wool - is < 500, Brown Wool - is 500 or more");
					/* White - radius is 1, Light Gray - radius is < 5
					 * , Gray - radius is < 10
					 * , Black - radius is < 15
					 * , Red - radius is < 25
					 * , Orange - radius is < 35
					 * , Yellow - radius is < 50
					 * , Lime - radius is < 75
					 * , Green - radius is < 100
					 * , Cyan - radius is < 125
					 * , Light - radius is < 150
					 * , Blue - radius is < 175
					 * , Purple - radius is < 200
					 * , Magenta - radius is < 250
					 * , Pink Wool - is < 500
					 * , Brown Wool - is 500 or more*/
				}
			}
			return true;
		}
		return null;
	}

}