package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;
import net.gravitydevelopment.updater.Updater;

class RSUpdate extends RSCommand {

    private RealShopping rs;
    public RSUpdate(CommandSender sender, String[] args, RealShopping rs) {
        super(sender, args);
        this.rs = rs;
    }

    @Override
    protected boolean execute() {
        if(Config.getAutoUpdate() > 0){
            if(args.length != 1) return false;
            switch(args[0].toLowerCase()) {
                case "update":
                    if(!RealShopping.newUpdate.equals("")){
                        if((player != null && Config.getAutoUpdate() == 4) || ( player == null && Config.getAutoUpdate() > 2)){//Permission to update
                            rs.setUpdater(new Updater(rs, RealShopping.PLUGINDEVID, rs.getPFile(), Updater.UpdateType.DEFAULT, true));
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
                    break;
                case "check":
                    if(rs.checkUpdates()) {
                        sender.sendMessage(ChatColor.GREEN + LangPack.THEREISA + " Update available.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "No updates available.");
                    }
                    return true;
                default:
                    return false;
            }
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