package com.github.kuben.realshopping.commands;

import net.h31ix.updater.Updater;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;

class RSUpdate extends RSCommand {

	private RealShopping rs;
	public RSUpdate(CommandSender sender, String[] args, RealShopping rs) {
		super(sender, args);
		this.rs = rs;
	}

	@Override
	protected boolean execute() {
		if(Config.getAutoUpdate() > 0){
			if(args.length == 1 && args[0].equals("info")){
				if(!RealShopping.newUpdate.equals("")){
    				if((player != null && Config.getAutoUpdate() > 1) || player == null){//Permission to get info
    					String mess = rs.getUpdater().getLatestVersionDescription();
    					mess = mess.replace("<li>", "• ");
    					mess = mess.replace("</li>", "\n");
    					mess = mess.replace("<p>", "");
    					mess = mess.replace("</p>", "\n"); 
    					mess = mess.replace("<ul>", "");
    					mess = mess.replace("</ul>", "");
    					mess = mess.replace("<em>", "");
    					mess = mess.replace("</em>", "");
    					mess = mess.replace("<strong>", "");
    					mess = mess.replace("</strong>", "");
    					mess = mess.replace("</span>", "");
    					mess = mess.replace("</a>", "");
    					while(mess.contains("<span") && mess.contains(">")){
    						String temp1 = mess.substring(0, mess.indexOf("<span"));
    						String temp2 = mess.substring(mess.indexOf(">") + 1, mess.length());
    						mess = temp1 + temp2;
    					}
    					while(mess.contains("<a") && mess.contains(">")){
    						String temp1 = mess.substring(0, mess.indexOf("<a"));
    						String temp2 = mess.substring(mess.indexOf(">") + 1, mess.length());
    						mess = temp1 + temp2;
    					}
    					if(player == null) sender.sendMessage(mess);
    					else {
    						player.sendMessage(ChatColor.GREEN + LangPack.READINGDESCRIPTION);
							messageSender mS = new messageSender(player, mess.split("\\n"), 2000);
							mS.start();
    					}
    					return true;
    				}
				} else {
					sender.sendMessage(ChatColor.RED + LangPack.THISISTHENEWESTVERSION);
					return true;
				}
			} else if(args.length == 1 && args[0].equals("update")){
				if(!RealShopping.newUpdate.equals("")){
    				if((player != null && Config.getAutoUpdate() == 4) || ( player == null && Config.getAutoUpdate() > 2)){//Permission to update
    					rs.setUpdater(new Updater(rs, "realshopping", rs.getPFile(), Updater.UpdateType.DEFAULT, true));
    					if(rs.getUpdater().getResult() == Updater.UpdateResult.SUCCESS)
    						sender.sendMessage(ChatColor.GREEN + LangPack.SUCCESSFULUPDATE);
    					else
    						sender.sendMessage(ChatColor.RED + LangPack.UPDATEFAILED);
    					return true;
    				}
				} else{
					sender.sendMessage(ChatColor.RED + LangPack.THISISTHENEWESTVERSION);
					return true;
				}
			} else return false;
		}
		sender.sendMessage(ChatColor.RED + LangPack.YOUARENTPERMITTEDTOUSETHISCOMMAND);
		return false;
	}

	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rsupdate update|info");
				sender.sendMessage(LangPack.FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ + ChatColor.LIGHT_PURPLE
						+ "/rsupdate help " + ChatColor.DARK_PURPLE + "COMMAND");
			} else if(args.length == 1){
				sender.sendMessage(LangPack.RSUPDATEHELP + ChatColor.LIGHT_PURPLE + "update, info");
			} else {
				if(args[1].equals("update")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "update"
						+ ChatColor.RESET + LangPack.RSUPDATEUPDATEHELP);
				else if(args[1].equals("info")) sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "info"
						+ ChatColor.RESET + LangPack.RSUPDATEINFOHELP + "RealShopping.");
			}
			return true;
		}
		return null;
	}
}