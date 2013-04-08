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
	protected boolean execute() {//TODO add help
		if(RealShopping.PInvMap.containsKey(player.getName())){
			if(args.length == 1 & args[0].equalsIgnoreCase("add")){				
					Shop tempShop = RealShopping.shopMap.get(RealShopping.PInvMap.get(player.getName()).getStore());
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
					player.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUARESTANDINGONISNTACHEST);
				}
			} else if(args.length == 1 & args[0].equalsIgnoreCase("remove")){
					Shop tempShop = RealShopping.shopMap.get(RealShopping.PInvMap.get(player.getName()).getStore());
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

}