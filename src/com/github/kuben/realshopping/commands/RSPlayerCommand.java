package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.github.kuben.realshopping.LangPack;

public abstract class RSPlayerCommand extends RSCommand {

	public RSPlayerCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	public boolean exec(){
		Boolean res = help();
		if(res != null) return res;
		if(player != null){
			return execute();
		}
		else sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
		return false;
	}
}
