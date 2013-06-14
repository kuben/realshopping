package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.LangPack;

class RealShoppingCommand extends RSCommand {

	public RealShoppingCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		byte pg = 1;
		if(args.length > 0) try {
			pg = Byte.parseByte(args[0]);
			if(pg < 1 || pg > 3){
				sender.sendMessage(ChatColor.RED + LangPack.THEREARENTTHATMANYPAGES);
				return false;
			}
		} catch (NumberFormatException e){
			sender.sendMessage(ChatColor.RED + args[1] + LangPack.ISNOTANINTEGER);
			return false;
		}
		int i = 0;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "RealShopping [" + RealShopping.VERSION + "] - A shop plugin for Bukkit made by kuben0");i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "Loaded config settings:");i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "enable-automatic-updates:"+Config.getAutoUpdateStr(Config.getAutoUpdate()));i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "auto-protect-chests:"+Config.isAutoprotect());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "delivery-cost-zones:"+Config.getDeliveryZones());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "language-pack:"+Config.getLangpack());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "enable-selling-to-stores:"+Config.isEnableSelling());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "enable-doors-as-entrances:"+Config.isEnableDoors());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "disable-item-drop:"+Config.isDisableDrop());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "disable-crafting:"+Config.isDisableCrafting());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "disable-buckets:"+Config.isDisableBuckets());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "disable-ender-chests:"+Config.isDisableEnderchests());i++;
		if(i >= (pg-1)*10 && i < pg*10) {
			String tempStr = "";
			if(Config.getCartEnabledW().contains("@all")) tempStr = "Enabled in all worlds";
			else {
    			boolean j = true;
    			for(String str:Config.getCartEnabledW()){
    				if(j){
   						tempStr += str;
   						j = false;
   					}
    				else tempStr += "," + str;
    			}
			}
			sender.sendMessage(ChatColor.GREEN + "enable-shopping-carts-in-worlds:" + tempStr);i++;
		}
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "player-stores-create-cost:"+Config.getPstorecreate());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "drop-items-at:"+Config.getDropLoc().getWorld().getName()+";"+Config.getDropLoc().getBlockX()+","+Config.getDropLoc().getBlockY()+","+Config.getDropLoc().getBlockZ());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "hell-location:"+Config.getHellLoc().getWorld().getName()+";"+Config.getHellLoc().getBlockX()+","+Config.getHellLoc().getBlockY()+","+Config.getHellLoc().getBlockZ());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "jail-location:"+Config.getJailLoc().getWorld().getName()+";"+Config.getJailLoc().getBlockX()+","+Config.getJailLoc().getBlockY()+","+Config.getJailLoc().getBlockZ());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "keep-stolen-items-after-punish:"+Config.isKeepstolen());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "punishment:"+Config.getPunishment());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "enable-automatic-store-management:"+Config.isEnableAI());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "stat-updater-frequency:"+Config.getTimeString(Config.getUpdateFreq()));i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "statistics-timespan:"+Config.getTimeString(Config.getStatTimespan()));i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "clean-stats-older-than:"+Config.getTimeString(Config.getCleanStatsOld()));i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "allow-filling-chests:"+Config.isAllowFillChests());i++;
		if(i >= (pg-1)*10 && i < pg*10) sender.sendMessage(ChatColor.GREEN + "notificatior-update-frequency:"+Config.getNotTimespan());i++;
		if(pg < 3) sender.sendMessage(ChatColor.DARK_PURPLE + "realshopping " + (pg + 1) + " for more.");
   		return true;
	}

}