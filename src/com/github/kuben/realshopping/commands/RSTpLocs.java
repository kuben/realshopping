package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RSEconomy;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;

class RSTpLocs extends RSPlayerCommand {

	public RSTpLocs(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {//TODO add help
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

}