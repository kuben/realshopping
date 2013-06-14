package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;

class RSCost extends RSPlayerCommand {

	public RSCost(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		player.sendMessage(ChatColor.RED + LangPack.YOURARTICLESCOST + RealShopping.getPInv(player).toPay()/100f + LangPack.UNIT);
		return true;
	}

}