package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class RSCommand {

	protected final Player player;
	protected final CommandSender sender;
	protected final String[] args;
	
	public RSCommand(CommandSender sender, String[] args){
		this.sender = sender;
		this.args = args;
		if(sender instanceof Player) player = (Player) sender;
		else player = null;
	}
	
	protected abstract boolean execute();
	
	public boolean exec(){
		if(help()) return true;
		return execute();
	}
	
	protected boolean help(){//TODO make Boolean and return null. Maybe
		//Check if help was asked for
		if(args.length > 0 && args[0].equalsIgnoreCase("help")){
			sender.sendMessage(ChatColor.RED + "No help documentation for this command.");//LANG
			return true;
		}
		return false;
	}
}
