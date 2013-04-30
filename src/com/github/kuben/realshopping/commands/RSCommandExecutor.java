/*
 * RealShopping Bukkit plugin for Minecraft
 * Copyright 2013 Jakub Fojt
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;

public class RSCommandExecutor implements CommandExecutor {
	RealShopping rs;
	
	public RSCommandExecutor(RealShopping rs){
		this.rs = rs;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!RealShopping.working.equals("")){
			sender.sendMessage(ChatColor.RED + RealShopping.working);
			return false;
		}
		
		try {
			if(cmd.getName().equalsIgnoreCase("rsreload")){
				rs.reload();
				sender.sendMessage(ChatColor.GREEN + LangPack.REALSHOPPINGRELOADED);
				return true;
			}
			else if(cmd.getName().equalsIgnoreCase("rsenter")) return new RSEnter(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rspay")) return new RSPay(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rscost")) return new RSCost(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rsexit")) return new RSExit(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rsprices")) return new RSPrices(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rssell")) return new RSSell(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rsstores")) return new RSStores(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rssetprices")) return new RSSetPrices(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rssetchests")) return new RSSetChests(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rssetstores")) return new RSSetStores(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rsset")) return new RSSet(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rsshipped")) return new RSShipped(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rstplocs")) return new RSTpLocs(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rsprotect")) return new RSProtect(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rsunjail")) return new RSUnjail(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rsupdate")) return new RSUpdate(sender, args, rs).exec();
			else if(cmd.getName().equalsIgnoreCase("realshopping")) return new RealShoppingCommand(sender, args).exec();
			else if(cmd.getName().equalsIgnoreCase("rsimport")) return new RSImport(sender, args).exec();
		} catch(Exception e){
			//Nothing
		}
    	return false;
	}
	

}

class blockUpdater extends Thread {
	private Location[] blocks;
	private Player player;

	public blockUpdater(Location[] blocks, Player player){
		this.blocks = blocks;
		this.player = player;
	}
	
	public void run(){
		try {
			Thread.sleep(5000);
			for(Location l:blocks){
				Block b = l.getWorld().getBlockAt(l);
				player.sendBlockChange(l, b.getType(), b.getData());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}

class messageSender extends Thread {
	private Player player;
	private String[] message;
	private int millis;

	public messageSender(Player player, String[] message, int millis){
		this.player = player;
		this.message = message;
		this.millis = millis;
	}
	
	public void run(){
		try {
			for(String s:message){
				Thread.sleep(millis);
				player.sendMessage(s);
			}
			player.sendMessage(ChatColor.GREEN + LangPack.DONE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}