package com.github.kuben.realshopping.commands;

import org.bukkit.command.CommandSender;
import com.github.kuben.realshopping.RealShopping;

class RSEnter extends RSPlayerCommand {

	public RSEnter(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		return RealShopping.enter(player, true);
	}

}
