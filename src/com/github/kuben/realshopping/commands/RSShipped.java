package com.github.kuben.realshopping.commands;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RSEconomy;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.ShippedPackage;
import com.github.kuben.realshopping.Shop;

class RSShipped extends RSPlayerCommand {

	public RSShipped(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {//TODO add help
		if(args.length == 0){
			if(RealShopping.shippedToCollect.containsKey(player.getName())){
				int toClaim = RealShopping.shippedToCollect.get(player.getName()).size();
				if(toClaim != 0) sender.sendMessage(ChatColor.GREEN + LangPack.YOUHAVEPACKAGESWITHIDS_ + toClaim + LangPack.TOPICKUP);
				else sender.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEANYPACKAGESTOPICKUP);
				return true;
			} else sender.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEANYPACKAGESTOPICKUP);
		} else if(args.length == 1 && args[0].equalsIgnoreCase("collect")){
			Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
			return RealShopping.collectShipped(l, player, 1);
			} else if(args.length == 1 && args[0].equalsIgnoreCase("inspect")){
			sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOSPECIFYTHEID_);
		} else if(args.length == 2 && args[0].equalsIgnoreCase("collect")){
			Location l = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ());
			try {
				return RealShopping.collectShipped(l, player, Integer.parseInt(args[1]));
			} catch (NumberFormatException e){
				sender.sendMessage(ChatColor.RED + args[1] + LangPack.ISNOTANINTEGER);
			}
		} else if(args.length == 2 && args[0].equalsIgnoreCase("inspect")){
			if(RealShopping.shippedToCollect.containsKey(player.getName())){
				try {
					ShippedPackage tempSP = RealShopping.shippedToCollect.get(player.getName()).get(Integer.parseInt(args[1]) - 1);
					sender.sendMessage(ChatColor.GREEN + LangPack.PACKAGESENT + new Date(tempSP.getDateSent()) + LangPack.FROM
							+ tempSP.getLocationSent().getBlockX() + "," + tempSP.getLocationSent().getBlockY() + "," + tempSP.getLocationSent().getBlockZ()
							+ LangPack.INWORLD + tempSP.getLocationSent().getWorld().getName());
    				String str = RealShopping.formatItemStackToMess(tempSP.getContents());
    				sender.sendMessage(ChatColor.GREEN + LangPack.THECONTENTSOFTHEPACKAGEARE + str);
    				return true;
				} catch (ArrayIndexOutOfBoundsException e){
					sender.sendMessage("ArrayIndexOutOfBoundsException");
				} catch (IndexOutOfBoundsException e){
					sender.sendMessage(ChatColor.RED + LangPack.THERESNOPACKAGEWITHTHEID + args[1]);
				} catch (NumberFormatException e){
					sender.sendMessage(ChatColor.RED + args[1] + LangPack.ISNOTANINTEGER);
				}
			} else sender.sendMessage(ChatColor.RED + LangPack.YOUDONTHAVEANYPACKAGESTOPICKUP);
		}
		return true;
	}

}