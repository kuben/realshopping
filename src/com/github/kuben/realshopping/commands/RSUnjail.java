package com.github.kuben.realshopping.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;

class RSUnjail extends RSCommand {

	public RSUnjail(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {//TODO add help
	    if (args.length == 1){
	        if(RealShopping.jailedPlayers.containsKey(args[0])){
	        Player[] pla = sender.getServer().getOnlinePlayers();
	        Player jailee = null;
	        for(Player p:pla){
	        if(p.getName().equals(args[0])){
	        jailee = p;
	        break;
	        }
	        }
	        if(jailee != null){
	            jailee.teleport(RealShopping.jailedPlayers.get(args[0]));
	            RealShopping.jailedPlayers.remove(args[0]);
	            jailee.sendMessage(LangPack.YOUARENOLONGERINJAIL);
	            sender.sendMessage(LangPack.UNJAILED + args[0]);
	            return true;
	        } else {
	        sender.sendMessage(args[0] + LangPack.ISNOTONLINE);
	        }
	        } else sender.sendMessage(args[0] + LangPack.ISNOTJAILED);
	    }
		return true;
	}

}