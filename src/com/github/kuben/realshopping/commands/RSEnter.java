package com.github.kuben.realshopping.commands;

import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.Shop;

class RSEnter extends RSPlayerCommand {

	public RSEnter(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		return Shop.enter(player, true);
	}

}
