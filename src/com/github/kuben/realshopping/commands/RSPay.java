package com.github.kuben.realshopping.commands;

import org.bukkit.command.CommandSender;
import com.github.kuben.realshopping.RealShopping;

class RSPay extends RSPlayerCommand {

	public RSPay(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		return RealShopping.pay(player, null);
	}

}