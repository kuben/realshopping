package com.github.kuben.realshopping.prompts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RealShoppingException;
import com.github.kuben.realshopping.listeners.ChestListener;

public class ChestPrompt implements Prompt {//TODO fix permissions and search code for breaches 
	 
    public String getPromptText(ConversationContext context) {
    	String in = (String) context.getSessionData("data");
    	String ID = (String) context.getSessionData("ID");
    	if(ID.equals("first")){
        	Player player = (Player) context.getForWhom();
        	Shop tempShop = RealShopping.shopMap.get(RealShopping.PInvMap.get(player.getName()).getStore());
        	context.setSessionData("shop",tempShop);
        	String out = "";
    		if(tempShop.getOwner().equals("@admin")){
    			out = "This prompt will aid you in creating self-refilling chests. \n To continue, type any of the purple keywords.";
    			context.setSessionData("ID", "second");
    		} else {
    			context.setSessionData("ID", "timetoquit");
    			out = ChatColor.RED + LangPack.ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS;
    		}
    		return out;
    	} else if(ID.equals("second")){
    		context.setSessionData("ID", "third");
    		return "What do you want to do, " + ChatColor.LIGHT_PURPLE + "create" + ChatColor.RESET + " new chests or "
    				+ ChatColor.LIGHT_PURPLE + "delete" + ChatColor.RESET + " or "
    				+ ChatColor.LIGHT_PURPLE + "manage" + ChatColor.RESET + " existing ones?";
    	} else if(ID.equals("third")){
    		try {
        		if(in.equalsIgnoreCase("create")){
        			new ChestListener((Player) context.getForWhom());//FIXME add type
        			context.setSessionData("ID", "create");
        			return "You have chosen to create new chests. Right-click a block to make it a chest, or left-click to cancel. Chosen blocks will appear as gold blocks. Type done when done or cancel to cancel.";
        		} else if(in.equalsIgnoreCase("delete")){
        			//Start super listener
        			context.setSessionData("ID", "delete");
        			return "You have chosen to delete existing chests. Right-click a chest to remove it, or left-click to cancel. Chosen blocks will appear as iron blocks. Type done when done or cancel to cancel.";
        		} else if(in.equalsIgnoreCase("manage")){
        			//Start different listener
        			context.setSessionData("ID", "manage");
        			return "You have chosen to manage existing chests. Left-click a chest to choose it. Commands additems, delitems and clear apply to all chosen chests. Type selclear to clear the selection of chests or selall to select all. Exit with done.";
        		} else if(in.equalsIgnoreCase("freemanage")){
        			//Start even yet different listener
        			context.setSessionData("ID", "free");
        			return "You have chosen to manage existing chests in free-mode. Open a chest, put items in, and close to update it's contents. Exit with done.";
        		}	
    		} catch (RealShoppingException e) {
    			//FIXME yeah
				e.printStackTrace();
			}
    	} else if(ID.equals("create") || ID.equals("delete")){
    		if(in.equalsIgnoreCase("done")){
    			int res = 0;
    			//Kill super listener
    			context.setSessionData("ID", "second");
    			if(ID.equals("create")) return res + LangPack.CHESTCREATED;
    			else return res + LangPack.CHESTREMOVED;
    		} else if(in.equalsIgnoreCase("cancel")){
    			//Kill super listener
    			context.setSessionData("ID", "second");
    			return "Action aborted.";
    		}
    	} else if(ID.equals("manage")){
    		if(in.equals("additems")){
    			//Send additems to different listener
    			return "Added X items to Y chests.";
    		} else if(in.equals("delitems")){
    			//Send delitems to different listener
    			return "Deleted X items from Y chests.";
    		} else if(in.equals("clear")){
    			//Send clear to different listener
    			return "Cleared Y chests.";
    		} else if(in.equals("selall")){
    			//Send selall to different listener
    			return "Selected all.";
    		} else if(in.equals("selclear")){
    			//Send selclear to different listener
    			return "Cleared selection.";
    		} else if(in.equalsIgnoreCase("done")){
    			//Kill different listener
    			context.setSessionData("ID", "second");
    			return "Quit managing chests.";
    		}
    	} else if(ID.equals("free") && in.equalsIgnoreCase("done")){
    		//Kill yet different listener
			context.setSessionData("ID", "second");
			return "Quit free-managing chests.";
    	}
    	context.setSessionData("ID", "timetoquit");
    	return "Error #1201";
    }

	public Prompt acceptInput(ConversationContext context, String in) {
		if(context.getSessionData("ID").equals("timetoquit")){
			return END_OF_CONVERSATION;
		}
		if(context.getSessionData("ID").equals("third") && !in.equalsIgnoreCase("create") && !in.equalsIgnoreCase("manage")
				&& !in.equalsIgnoreCase("delete")){
			context.getForWhom().sendRawMessage("Wrong answer");
		}
		if(context.getSessionData("ID").equals("create") && !in.equalsIgnoreCase("done") && !in.equalsIgnoreCase("cancel")){
			context.getForWhom().sendRawMessage("Wrong answer");
		}
		context.setSessionData("data", in);
		return this;
	}

	public boolean blocksForInput(ConversationContext context) {
		Object ID = context.getSessionData("ID");
		if(ID.equals("timetoquit") || ID.equals("second")) return false;
		return true;
	}
 
}