package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;

class RSPrices extends RSCommand {
	
	public RSPrices(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {//TODO add help
		if(args.length == 0){
			if(player != null){
				if(RealShopping.PInvMap.get(player.getName()) != null) {
					return RealShopping.prices(sender, 0, RealShopping.PInvMap.get(player.getName()).getStore());
				} else {
					sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
					return false;
				}
			} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
		} else if(args.length == 1){
			if(args[0].matches("[0-9]+")){
				if(player != null){
					if(RealShopping.PInvMap.containsKey(player.getName())){
						int i = Integer.parseInt(args[0]);
						if(i > 0) return RealShopping.prices(sender, i - 1, RealShopping.PInvMap.get(player.getName()).getStore());
						else sender.sendMessage(ChatColor.RED + LangPack.THEPAGENUMBERMUSTBE1ORHIGHER);
					} else {
						sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT);
					}
				} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
			} else {
				return RealShopping.prices(sender, 0, args[0]);
			}
		} else if(args.length == 2){
			if(args[1].matches("[0-9]+")){
				return RealShopping.prices(sender, Integer.parseInt(args[1]), args[0]);
			} else {
				sender.sendMessage(ChatColor.RED + "" + args[1] + LangPack.ISNOTAVALIDPAGENUMBER);
			}
		}
		return false;
	}
}