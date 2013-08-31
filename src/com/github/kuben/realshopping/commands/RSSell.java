package com.github.kuben.realshopping.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;

class RSSell extends RSPlayerCommand {

	public RSSell(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if(RealShopping.hasPInv(player)){
			if(Config.isEnableSelling()){
				if(RealShopping.getPInv(player).getShop().getBuyFor() > 0){
					Inventory tempInv = Bukkit.createInventory(null, 36, LangPack.SELLTOSTORE);
					player.openInventory(tempInv);
					return true;	
				} else player.sendMessage(ChatColor.RED + LangPack.NOTBUYINGFROMPLAYERS);
			} else player.sendMessage(ChatColor.RED + LangPack.SELLINGTOSTORESISNOTENABLEDONTHISSERVER);
		} else sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
		return false;
	}

}
