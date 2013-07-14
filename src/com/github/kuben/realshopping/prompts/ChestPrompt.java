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

public class ChestPrompt implements Prompt {
	 
    public String getPromptText(ConversationContext context) {
    	String in = (String) context.getSessionData("data");
    	String ID = (String) context.getSessionData("ID");
		Player player = (Player) context.getForWhom();
    	if(ID.equals("first")){
        	Shop tempShop = RealShopping.shopMap.get(RealShopping.getPInv(player).getStore());
        	context.setSessionData("shop",tempShop);
        	String out = "";
    		if(tempShop.getOwner().equals("@admin")){
    			out = LangPack.THIS_PROMPT_WILL_AID_YOU_IN__CHESTS_ + "\n " + LangPack.TO_CONTINUE_TYPE_ANY_OF_THE_PURPLE_KEYWORDS;
    			context.setSessionData("ID", "second");
    		} else {
    			context.setSessionData("ID", "timetoquit");
    			out = ChatColor.RED + LangPack.ONLYADMINSTORESCANHAVESELFREFILLINGCHESTS;
    		}
    		return out;
    	} else if(ID.equals("second")){
    		context.setSessionData("ID", "third");
    		return LangPack.WHAT_DO_YOU_WANT_TO_DO_ + ChatColor.LIGHT_PURPLE + "create" + ChatColor.RESET + LangPack.NEW_CHESTS_OR_
    				+ ChatColor.LIGHT_PURPLE + "delete" + ChatColor.RESET + LangPack.OR_
    				+ ChatColor.LIGHT_PURPLE + "manage"  + ChatColor.RESET + LangPack.OR_
    				+ ChatColor.LIGHT_PURPLE + "freemanage" + ChatColor.RESET + LangPack.EXISTING_ONES;
    	} else if(ID.equals("third")){
    		try {
        		if(in.equalsIgnoreCase("create")){
        			new ChestListener(player, Type.ADD);
        			context.setSessionData("ID", "create");
        			return LangPack.YOU_HAVE_CHOSEN_TO_CREATE_NEW_CHESTS_ + LangPack.TYPE + ChatColor.LIGHT_PURPLE + "done"
        					+ ChatColor.RESET + LangPack.WHEN_DONE_OR_ + ChatColor.LIGHT_PURPLE + "cancel" + ChatColor.RESET + LangPack.TOCANCEL;
        		} else if(in.equalsIgnoreCase("delete")){
        			new ChestListener(player, Type.REMOVE);
        			context.setSessionData("ID", "delete");
        			return LangPack.YOU_HAVE_CHOSEN_TO_DELETE_EXISTING_CHESTS_ + LangPack.TYPE + ChatColor.LIGHT_PURPLE + "done"
        			+ ChatColor.RESET + LangPack.WHEN_DONE_OR_ + ChatColor.LIGHT_PURPLE + "cancel" + ChatColor.RESET + LangPack.TOCANCEL;
        		} else if(in.equalsIgnoreCase("manage")){
        			new ChestManageListener(player);
        			context.setSessionData("ID", "manage");
        			return LangPack.YOU_HAVE_CHOSEN_TO_MANAGE_EXISTING_CHESTS_
        			+ ChatColor.LIGHT_PURPLE + "additems" + ChatColor.DARK_PURPLE + " ITEMS.." + ChatColor.RESET + ", " + ChatColor.LIGHT_PURPLE + "delitems" + ChatColor.DARK_PURPLE + " ITEMS.."
        			+ ChatColor.RESET + LangPack.AND_ + ChatColor.LIGHT_PURPLE + "clear" + ChatColor.RESET + LangPack.APPLY_TO_ALL_CHOSEN_CHESTS_
        			+ LangPack.TYPE + ChatColor.LIGHT_PURPLE + "selclear" + ChatColor.RESET + LangPack.TO_CLEAR_THE_SELECTION_OF_CHESTS_OR_
        			+ ChatColor.LIGHT_PURPLE + "selall" + ChatColor.RESET + LangPack.TO_SELECT_ALL__EXIT_WITH_
        			+ ChatColor.LIGHT_PURPLE + "done" + ChatColor.RESET + ".";
        		} else if(in.equalsIgnoreCase("freemanage")){
        			new ChestFreeManageListener(player);
        			context.setSessionData("ID", "free");
        			return LangPack.YOU_HAVE_CHOSEN_TO_MANAGE_EXISTING_CHESTS_FREELY_ + ChatColor.LIGHT_PURPLE + "done" + ChatColor.RESET + ".";
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
    				return ChatColor.RED + LangPack.YOUHAVETOBEINASTORETOUSETHISCOMMAND;
    			} else e.printStackTrace();
			}
    	} else if(ID.equals("create") || ID.equals("delete")){
    		if(in.equalsIgnoreCase("done")){
    			int res = RSPlayerListener.finishConversationListener(player);
    			context.setSessionData("ID", "second");
    			if(ID.equals("create")) return res + LangPack.CHESTS_CREATED;
    			else return res + " chest(s) removed.";
    		} else if(in.equalsIgnoreCase("cancel")){
    			RSPlayerListener.killConversationListener(player);
    			context.setSessionData("ID", "second");
    			return LangPack.ACTION_ABORTED;
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
						if(in.split(" ")[0].equals("additems")) return ChatColor.GREEN + LangPack.ADDED + ChatColor.DARK_GREEN + r[0]
								+ ChatColor.GREEN + LangPack.ITEMS_TO_ + ChatColor.DARK_GREEN + r[1] + ChatColor.GREEN + LangPack.CHESTS;
						else return ChatColor.GREEN + LangPack.DELETED_ + ChatColor.DARK_GREEN + r[0] + ChatColor.GREEN + LangPack.ITEMS_FROM_
								+ ChatColor.DARK_GREEN + r[1] + ChatColor.GREEN + LangPack.CHESTS;
					}
					throw tempEx;
	    		} else if(in.equals("clear")){
		   			Object o = RSPlayerListener.sendSignalToConversationListener(player, SIGNAL.CLEAR_ITEMS);
					if(o instanceof Integer){
						return ChatColor.GREEN + LangPack.CLEARED_CONTENTS_OF_
								+ ChatColor.DARK_GREEN + o + ChatColor.GREEN + LangPack.CHESTS;
					}
					throw tempEx;
	    		} else if(in.equals("selall")){
	    			Object o = RSPlayerListener.sendSignalToConversationListener(player, SIGNAL.SEL_ALL);
					if(o instanceof Integer){
		    			return ChatColor.GREEN + LangPack.SELECTED_
		    					+ ChatColor.DARK_GREEN + o + ChatColor.GREEN + LangPack.CHESTS;
					}
					throw tempEx;
	    		} else if(in.equals("selclear")){
	    			RSPlayerListener.sendSignalToConversationListener(player, SIGNAL.SEL_CLEAR);
	    			return ChatColor.GREEN + LangPack.CLEARED_SELECTION;
	    		} else if(in.equalsIgnoreCase("done")){
	    			RSPlayerListener.sendSignalToConversationListener(player, SIGNAL.SEL_CLEAR);//Clear selection to reset block changes
	    			RSPlayerListener.killConversationListener(player);
	    			context.setSessionData("ID", "second");
	    			return ChatColor.GREEN + LangPack.QUIT_MANAGING_CHESTS;
	    		}
			} catch (NumberFormatException e){
				return ChatColor.RED + LangPack.ONEORMOREOFTHEITEMIDSWERENOTINTEGERS + in.split(" ")[1];
			} catch (RSListenerException e) {
				e.printStackTrace();
				return ChatColor.RED + "Error 5001, please have the server administrator report the stacktrace in the server console to the plugin creator.";//No translate
			}
    	} else if(ID.equals("free")){
    		 if(in.equalsIgnoreCase("done")){
    			 int res = RSPlayerListener.finishConversationListener(player);
    			 context.setSessionData("ID", "second");
    			 return ChatColor.GREEN + LangPack.UPDATED_CONTENTS_OF
    					 + ChatColor.DARK_GREEN + res + ChatColor.GREEN + LangPack.CHESTS;
    		 } else if(in.equalsIgnoreCase("cancel")){
    			 RSPlayerListener.killConversationListener(player);
    			 context.setSessionData("ID", "second");
    			 return LangPack.ACTION_ABORTED;
    		 }
    	} else if(ID.equals("rollback")){
    		context.setSessionData("ID", context.getSessionData("BACKTO"));
    		return ChatColor.LIGHT_PURPLE + "" + context.getSessionData("COM") + ChatColor.RED + LangPack.IS_NOT_AN_ACCEPTED_ARGUMENT;
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
		if(context.getSessionData("ID").equals("manage") && !in.equalsIgnoreCase("done") && !in.split(" ")[0].equalsIgnoreCase("additems")
				&& !in.split(" ")[0].equalsIgnoreCase("delitems") && !in.equalsIgnoreCase("clear") && !in.equalsIgnoreCase("selall")
				&& !in.equalsIgnoreCase("selclear")){
			context.setSessionData("BACKTO", "manage");
			context.setSessionData("COM", in);
			context.setSessionData("ID", "rollback");
		}
		if(context.getSessionData("ID").equals("free") && !in.equalsIgnoreCase("done") && !in.equalsIgnoreCase("cancel")){
			context.setSessionData("BACKTO", "free");
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