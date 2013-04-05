package com.github.kuben.realshopping.commands;

import org.bukkit.command.CommandSender;
import com.github.kuben.realshopping.RealShopping;

class RSExit extends RSPlayerCommand {

	public RSExit(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		return RealShopping.exit(player, true);
	}

}
