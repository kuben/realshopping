package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;

class RSProtect extends RSPlayerCommand {

	public RSProtect(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if(RealShopping.hasPInv(player)){
			if(args.length == 1 & args[0].equalsIgnoreCase("add")){				
					Shop tempShop = RealShopping.shopMap.get(RealShopping.getPInv(player).getStore());
					BlockState bs = player.getLocation().getBlock().getState();
					if(bs instanceof Chest | bs instanceof DoubleChest){
						if(tempShop.isProtectedChest(bs.getLocation())){
							player.sendMessage(ChatColor.GREEN + LangPack.THISCHESTISALREADYPROTECTED);
							return true;
						} else {
							tempShop.addProtectedChest(bs.getLocation());
							player.sendMessage(ChatColor.GREEN + LangPack.MADECHESTPROTECTED);
							return true;
					}
				} else {
					player.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUSELECTEDISNTACHEST);
				}
			} else if(args.length == 1 & args[0].equalsIgnoreCase("remove")){
					Shop tempShop = RealShopping.shopMap.get(RealShopping.getPInv(player).getStore());
					BlockState bs = player.getLocation().getBlock().getState();
					if(tempShop.isProtectedChest(bs.getLocation())){
						tempShop.removeProtectedChest(bs.getLocation());
						player.sendMessage(ChatColor.GREEN + LangPack.UNPROTECTEDCHEST);
						return true;
				} else {
						player.sendMessage(ChatColor.RED + LangPack.THISCHESTISNTPROTECTED);
						return true;
				}
			}
		} else player.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
		return false;
	}

	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rsprotect add|remove");
			sender.sendMessage(ChatColor.GREEN + "Makes chests protected, so they cannot be opened from outside the store. Use when neccecary. Add makes a chest protected, and remove unmakes it.");
			return true;
		}
		return null;
	}
}