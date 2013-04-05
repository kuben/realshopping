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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.h31ix.updater.Updater;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RSEconomy;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.ShippedPackage;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.commands.*;

public class RSCommandExecutor implements CommandExecutor {
	RealShopping rs;
	
	public RSCommandExecutor(RealShopping rs){
		this.rs = rs;
	}
	
	@SuppressWarnings("static-access")
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

class ImportPrompt extends ValidatingPrompt {
 
    public String getPromptText(ConversationContext context) {
    	String in = (String) context.getSessionData("data");
    	if(in.equals("first")){
    		String out = LangPack.WHICHFILEDOYOUWANTTOIMPORT_;
    		File dirP = new File(RealShopping.MANDIR);
    		File[] Mlist = null, Plist = null;
    		File dirM = new File("./");
    		if(dirP.isDirectory()){
    			Mlist = dirM.listFiles(new FilenameFilter(){
    			    public boolean accept(File dir, String name) {
    			        return (name.endsWith(".xlsx"));
    			    }
    			});
    		}
    		if(dirP.isDirectory()){
    			Plist = dirP.listFiles(new FilenameFilter(){
    			    public boolean accept(File dir, String name) {
    			        return (name.endsWith(".xlsx"));
    			    }
    			});
    		}
    		if((Mlist == null || Mlist.length == 0) && (Plist == null || Plist.length == 0))
    			return LangPack.ERROR_NO_XLSX_;
    		if(Mlist != null && Mlist.length > 0){
    			out += ChatColor.DARK_GREEN + LangPack.INTHEMAINDIRECTORY;
    			for(int i = 1;i <= Mlist.length;i++){
    				out += " " + ChatColor.LIGHT_PURPLE + i + ")" + ChatColor.WHITE + Mlist[i-1].getName() + " ";
    	    	}
    		}
    		if(Plist != null && Plist.length > 0){
    			out += ChatColor.DARK_GREEN + LangPack.INTHEREALSHOPPINGDIRECTORY;
    			for(int i = 1;i <= Plist.length;i++){
        			out += " " + ChatColor.LIGHT_PURPLE + i + ")" + ChatColor.WHITE + Plist[i-1].getName() + " ";
        		}
    		}
    		out += LangPack.TYPETHECORRESPONDINGNUMBER_ + ChatColor.LIGHT_PURPLE + "c" + ChatColor.WHITE + LangPack.TOCANCEL;
    		context.setSessionData("mlist", Mlist);
    		context.setSessionData("plist", Plist);
    		return out;
    	} else {
    		int num = -1;
    		try{
    			num = Integer.parseInt((String)context.getSessionData("data"));
    		} catch (NumberFormatException e){
    			return LangPack.ERROR_INPUTISNOTAVALIDINTEGER;
    		}
    		if(num > 0){
        		if(context.getSessionData("mlist") != null && context.getSessionData("plist") != null){
        			File[] Mlist = (File[]) context.getSessionData("mlist");
        			File[] Plist = (File[]) context.getSessionData("plist");
        			if(num <= Mlist.length + Plist.length){
        				String chosen = "";
        				if(num <= Mlist.length) chosen = Mlist[num - 1].getPath();
        				else chosen = Plist[num -1 - Mlist.length].getPath();
        				context.setSessionData("file", chosen);
    				    context.setSessionData("final", true);
        				return ChatColor.GREEN + "Chosen file " + chosen + ". "
        						+ ChatColor.WHITE + "Type " + ChatColor.LIGHT_PURPLE + "u" + ChatColor.WHITE + LangPack.TOIMPORT_USERDEFINED_ +
        						ChatColor.LIGHT_PURPLE + "p" + ChatColor.WHITE + LangPack.TOIMPORT_PROPOSITION_;
        			} else return LangPack.WRONGFILECHOSEN;
        		}
    		} else return LangPack.ERROR_INPUTISNOTAVALIDINTEGER;
    	}
        return "Error #1201";
    }
 
    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String in) {
    	if(context.getSessionData("final") != null && context.getSessionData("final").equals(true)){
    		context.setSessionData("data", in);
    		return new FinalPrompt();
    	}
        if(in.equalsIgnoreCase("stop") || in.equalsIgnoreCase("end") || in.equalsIgnoreCase("quit") || in.equalsIgnoreCase("c"))
            return END_OF_CONVERSATION;
        else context.setSessionData("data", in);
        return this;
    }
 
    @Override
    protected boolean isInputValid(ConversationContext context, String in) {
    	if(in.equalsIgnoreCase("stop") || in.equalsIgnoreCase("end") || in.equalsIgnoreCase("quit") || in.equalsIgnoreCase("c")) return true;

    	if(context.getSessionData("final") != null && context.getSessionData("final").equals(true)){
    		if(!in.equalsIgnoreCase("p") && !in.equalsIgnoreCase("u")) return false;
    	}
    	
        return true;
    }
 
}

class FinalPrompt extends MessagePrompt{

	public String getPromptText(ConversationContext context) {
    	String in = (String) context.getSessionData("data");
		if(in.equalsIgnoreCase("u") || in.equalsIgnoreCase("p")){
			if(context.getSessionData("file") != null){
	    		try {
				    InputStream inp = new FileInputStream((String)context.getSessionData("file"));
				    XSSFWorkbook wb;

					wb = new XSSFWorkbook(inp);
				    XSSFSheet sheet = wb.getSheetAt(in.equalsIgnoreCase("u")?0:2);
				    Iterator rowIter = sheet.rowIterator();
				    
				    RealShopping.defPrices.clear();
				    wb.getCreationHelper().createFormulaEvaluator().evaluateAll();
				    while(rowIter.hasNext()){
				    	try {
					    	XSSFRow row = (XSSFRow) rowIter.next();
					    	XSSFCell firstC = row.getCell(0);
					    	int ID = -1;
					    	byte data = 0;
					    	if(firstC != null) if (firstC.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){//ID's are numeric
					    		ID = (int) firstC.getNumericCellValue();
					    	} else if (firstC.getCellType() == XSSFCell.CELL_TYPE_STRING){
					    		ID = Integer.parseInt(firstC.getStringCellValue().split(";")[0]);
					    		data = Byte.parseByte(firstC.getStringCellValue().split(";")[1]);
					    	}
					    	if(ID >= 0){
					    		XSSFCell costC = row.getCell(4);
					    		try{
					    			if(costC != null && costC.getCellType() == XSSFCell.CELL_TYPE_FORMULA){
					    				Price p;
					    				if(data == 0) p = new Price(ID);
					    				else p = new Price(ID, data);
	            						DecimalFormat twoDForm = new DecimalFormat("#.##");
	            						float cost = Float.valueOf(twoDForm.format((float) costC.getNumericCellValue()).replaceAll(",", "."));
					    				Float[] f = new Float[]{cost};
					    				RealShopping.defPrices.put(p, f);
					    			}
					    		} catch (Exception e) {}
					    	}
				    	} catch (NumberFormatException e){}//Skip
			        }
					if(RealShopping.defPrices.size() > 0) return ChatColor.GREEN + LangPack.IMPORTED + RealShopping.defPrices.size() + LangPack.PRICESASDEFAULT;
					else return LangPack.ERRORCOULDNTIMPORTPRICES;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
    	}
		return null;
	}

	@Override
	protected Prompt getNextPrompt(ConversationContext context) {
		return END_OF_CONVERSATION;
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