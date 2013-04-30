package com.github.kuben.realshopping.commands;

import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.Shop;

class RSExit extends RSPlayerCommand {

	public RSExit(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		return Shop.exit(player, true);
	}

}
