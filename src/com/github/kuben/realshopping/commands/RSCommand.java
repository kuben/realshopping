package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.kuben.realshopping.LangPack;

/**
 * A dummy class which is to be extended by every command.
 * 
 * @author kuben
 */
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
	
	
	/**
	 * This is the method which actually executes the command.
	 * <p>
	 * It is to be called by {@link #exec()}.
	 *
	 * @return true if the command was executed successfully, false if not and the command help text needs to be shown.
	 */
	protected abstract boolean execute();
	
	/**
	 * This is the method which is to be called by {@link RSCommandExcecutor#onCommand()}.
	 * <p>
	 * It first calls {@link #help()} to see if help was requested, then {@link #execute()} to execute the command.
	 *
	 * @return true if the command was executed successfully, false if not and the command help text needs to be shown.
	 */
	public boolean exec(){
		Boolean res = help();
		if(res != null) return res;
		return execute();
	}
	
	/**
	 * Is the first method called by {@link #exec()} and checks if help was asked for, and displays help if so.
	 * 
	 * @return null if help was not asked for, otherwise true or false. False is returned if the command help text is to be displayed, true otherwise.
	 */
	protected Boolean help(){
		if(args.length > 0 && args[0].equalsIgnoreCase("help")){
			sender.sendMessage(ChatColor.RED + LangPack.NO_HELP_DOCUMENTATION_);
			return false;
		}
		return null;
	}
}
