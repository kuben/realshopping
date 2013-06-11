package com.github.kuben.realshopping.prompts;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RSListenerException;
import com.github.kuben.realshopping.listeners.ChestListener;
import com.github.kuben.realshopping.listeners.ChestListener.Type;
import com.github.kuben.realshopping.listeners.ChestFreeManageListener;
import com.github.kuben.realshopping.listeners.ChestManageListener;
import com.github.kuben.realshopping.listeners.ChestManageListener.SIGNAL;
import com.github.kuben.realshopping.listeners.RSPlayerListener;

public class ChestPrompt implements Prompt {//TODO fix permissions and search code for breaches 
	 
    public String getPromptText(ConversationContext context) {
    	String in = (String) context.getSessionData("data");
    	String ID = (String) context.getSessionData("ID");
		Player player = (Player) context.getForWhom();
    	if(ID.equals("first")){
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
        			new ChestListener(player, Type.ADD);
        			context.setSessionData("ID", "create");
        			return "You have chosen to create new chests. Right-click a block to make it a chest, or left-click to cancel. Chosen blocks will appear as gold blocks. " + LangPack.TYPE
        					+ ChatColor.DARK_PURPLE + "done" + ChatColor.RESET + "when done or " + ChatColor.DARK_PURPLE + "cancel" + ChatColor.RESET + " to cancel.";
        		} else if(in.equalsIgnoreCase("delete")){
        			new ChestListener(player, Type.REMOVE);
        			context.setSessionData("ID", "delete");
        			return "You have chosen to delete existing chests. Right-click a chest to remove it, or left-click to cancel. Chosen blocks will appear as iron blocks. " + LangPack.TYPE
        					+ ChatColor.DARK_PURPLE + "done" + ChatColor.RESET + "when done or " + ChatColor.DARK_PURPLE + "cancel" + ChatColor.RESET + " to cancel.";
        		} else if(in.equalsIgnoreCase("manage")){
        			new ChestManageListener(player);
        			context.setSessionData("ID", "manage");
        			return "You have chosen to manage existing chests. Left-click a chest to choose it. Commands "
        			+ ChatColor.DARK_PURPLE + "additems" + ChatColor.RESET + ", " + ChatColor.DARK_PURPLE + "delitems"
        			+ ChatColor.RESET + " and " + ChatColor.DARK_PURPLE + "clear" + ChatColor.RESET + " apply to all chosen chests. "
        			+ LangPack.TYPE + ChatColor.DARK_PURPLE + "selclear" + ChatColor.RESET + " to clear the selection of chests or "
        			+ ChatColor.DARK_PURPLE + "selall" + ChatColor.RESET + " to select all. Exit with "
        			+ ChatColor.DARK_PURPLE + "done" + ChatColor.RESET + ".";
        		} else if(in.equalsIgnoreCase("freemanage")){
        			new ChestFreeManageListener(player);
        			context.setSessionData("ID", "free");
        			return "You have chosen to manage existing chests freely. Open a chest, put items in, and close to update it's contents. Exit with "
        			+ ChatColor.DARK_PURPLE + "done" + ChatColor.RESET + ".";
        		}	
    		} catch (RSListenerException e) {
    			if(e.getType() == RSListenerException.Type.PLAYER_ALREADY_HAS_LISTENER){
    				context.setSessionData("ID", "timetoquit");
    				return ChatColor.RED + "Error: A listener is already active for you.";
    			} else if(e.getType() == RSListenerException.Type.NOT_ALLOWED_MANAGE){
    				context.setSessionData("ID", "timetoquit");
    				return ChatColor.RED + LangPack.YOUDONTHAVEPERMISSIONTOMANAGETHATSTORE;
    			} else if(e.getType() == RSListenerException.Type.NOT_IN_SHOP){
    				context.setSessionData("ID", "timetoquit");
    				return ChatColor.RED + LangPack.YOUHAVETOBEINASTORETOUSETHISCOMMAND;//TODO maybe reformulate
    			} else e.printStackTrace();
			}
    	} else if(ID.equals("create") || ID.equals("delete")){
    		if(in.equalsIgnoreCase("done")){
    			int res = RSPlayerListener.finishConversationListener(player);
    			context.setSessionData("ID", "second");
    			if(ID.equals("create")) return res + " chest(s) created.";
    			else return res + " chest(s) removed.";
    		} else if(in.equalsIgnoreCase("cancel")){
    			RSPlayerListener.killConversationListener(player);
    			context.setSessionData("ID", "second");
    			return "Action aborted.";
    		}
    	} else if(ID.equals("manage")){
			try {
				RSListenerException tempEx = new RSListenerException(player, com.github.kuben.realshopping.exceptions.RSListenerException.Type.RETURN_VALUE_MISMATCH);
	    		if((in.split(" ")[0].equals("additems") || (in.split(" ")[0].equals("delitems"))) && in.split(" ").length > 1){
	    			Object o;
	    			if(in.split(" ")[0].equals("additems")) o = RSPlayerListener.sendSignalToConversationListener(player, new Object[]{SIGNAL.ADD_ITEMS, RSUtils.pullItems(in.split(" ")[1])});
	    			else o = RSPlayerListener.sendSignalToConversationListener(player, new Object[]{SIGNAL.DEL_ITEMS, RSUtils.pullItems(in.split(" ")[1])});
					if(o instanceof int[] && ((int[])o).length == 2){
						int[] r = (int[])o;
						if(in.split(" ")[0].equals("additems")) return ChatColor.GREEN + "Added " + ChatColor.DARK_GREEN + r[0]
								+ ChatColor.GREEN + " items to " + ChatColor.DARK_GREEN + r[1] + ChatColor.GREEN + " chests.";
						else return ChatColor.GREEN + "Deleted " + ChatColor.DARK_GREEN + r[0] + ChatColor.GREEN + " items from "
								+ ChatColor.DARK_GREEN + r[1] + ChatColor.GREEN + " chests.";
					}
					throw tempEx;
	    		} else if(in.equals("clear")){
		   			Object o = RSPlayerListener.sendSignalToConversationListener(player, SIGNAL.CLEAR_ITEMS);
					if(o instanceof Integer){
						return "Cleared contents of " + o + " chests.";
					}
					throw tempEx;
	    		} else if(in.equals("selall")){
	    			Object o = RSPlayerListener.sendSignalToConversationListener(player, SIGNAL.SEL_ALL);
					if(o instanceof Integer){
		    			return "Selected " + o + " chests.";
					}
					throw tempEx;
	    		} else if(in.equals("selclear")){
	    			RSPlayerListener.sendSignalToConversationListener(player, SIGNAL.SEL_CLEAR);
	    			return "Cleared selection.";
	    		} else if(in.equalsIgnoreCase("done")){
	    			RSPlayerListener.killConversationListener(player);
	    			context.setSessionData("ID", "second");
	    			return "Quit managing chests.";
	    		}
			} catch (NumberFormatException e){
				return ChatColor.RED + LangPack.ONEORMOREOFTHEITEMIDSWERENOTINTEGERS + in.split(" ")[1];
			} catch (RSListenerException e) {
				e.printStackTrace();
				return ChatColor.RED + "Error 5001, please have the server administrator report the stacktrace in the server console to the plugin creator.";//TODO reformulate? No translate
			}
    	} else if(ID.equals("free")){
    		 if(in.equalsIgnoreCase("done")){
    			 int res = RSPlayerListener.finishConversationListener(player);
    			 context.setSessionData("ID", "second");
    			 return "Updated contents of " + res + " chests.";
    		 } else if(in.equalsIgnoreCase("abort")){//TODO cancel?  TODO add to acceptInput
    			 RSPlayerListener.killConversationListener(player);
    			 context.setSessionData("ID", "second");
    			 return "Action aborted";
    		 }
    	} else if(ID.equals("rollback")){
    		context.setSessionData("ID", context.getSessionData("BACKTO"));
    		return ChatColor.DARK_PURPLE + "" + context.getSessionData("COM") + ChatColor.RED + " is not an accepted argument.";
    	}
    	context.setSessionData("ID", "timetoquit");
    	return "Error #1201";
    }

	public Prompt acceptInput(ConversationContext context, String in) {
		if(context.getSessionData("ID").equals("timetoquit")){
			return END_OF_CONVERSATION;
		}
		if(context.getSessionData("ID").equals("third") && !in.equalsIgnoreCase("create") && !in.equalsIgnoreCase("manage")
				&& !in.equalsIgnoreCase("freemanage") && !in.equalsIgnoreCase("delete")){
			context.setSessionData("BACKTO", "second");
			context.setSessionData("COM", in);
			context.setSessionData("ID", "rollback");
		}
		if(context.getSessionData("ID").equals("create") && !in.equalsIgnoreCase("done") && !in.equalsIgnoreCase("cancel")){
			context.setSessionData("BACKTO", "create");
			context.setSessionData("COM", in);
			context.setSessionData("ID", "rollback");
		}
		if(context.getSessionData("ID").equals("delete") && !in.equalsIgnoreCase("done") && !in.equalsIgnoreCase("cancel")){
			context.setSessionData("BACKTO", "delete");
			context.setSessionData("COM", in);
			context.setSessionData("ID", "rollback");
		}
		if(context.getSessionData("ID").equals("manage") && !in.equalsIgnoreCase("done") && !in.equalsIgnoreCase("additems") && !in.equalsIgnoreCase("delitems")
				 && !in.equalsIgnoreCase("clear") && !in.equalsIgnoreCase("selall") && !in.equalsIgnoreCase("selclear")){
			context.setSessionData("BACKTO", "manage");
			context.setSessionData("COM", in);
			context.setSessionData("ID", "rollback");
		}
		context.setSessionData("data", in);
		return this;
	}

	public boolean blocksForInput(ConversationContext context) {
		Object ID = context.getSessionData("ID");
		if(ID.equals("timetoquit") || ID.equals("rollback") || ID.equals("second")) return false;
		return true;
	}
 
}