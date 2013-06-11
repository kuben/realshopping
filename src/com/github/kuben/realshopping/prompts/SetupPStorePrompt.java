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
import com.github.kuben.realshopping.listeners.SetupStoreListener;
import com.github.kuben.realshopping.listeners.SetupStoreListener.Type;
import com.github.kuben.realshopping.listeners.RSPlayerListener;

public class SetupPStorePrompt implements Prompt {//TODO fix permissions and search code for breaches LANG
	 
    public String getPromptText(ConversationContext context) {
    	try {
    	String in = (String) context.getSessionData("data");
    	String ID = (String) context.getSessionData("ID");
		Player player = (Player) context.getForWhom();
    	if(ID.equals("first")){
        	String out = "";
    		if(player.hasPermission("realshopping.rssetstores")){
    			out = "This prompt will aid you in creating, extending or deleting your own player store. \n To continue, type any of the purple keywords.";
    			context.setSessionData("ID", "second");//LANG
    		} else {
    			context.setSessionData("ID", "timetoquit");
    			out = ChatColor.RED + "You don't have the right permissions blah blah";
    		}
    		return out;
    	} else if(ID.equals("second")){
    		context.setSessionData("ID", "third");
    		return "What do you want to do, " + ChatColor.LIGHT_PURPLE + "create" + ChatColor.RESET + " a new store "
    				+ ChatColor.LIGHT_PURPLE + "append" + ChatColor.RESET + "/" + ChatColor.LIGHT_PURPLE + "delete"
    				+ ChatColor.RESET + " entrances and exits or "
    				+ ChatColor.LIGHT_PURPLE + "wipeout" + ChatColor.RESET + " an existing store?";
    	} else if(ID.equals("third")){
       		if(in.equalsIgnoreCase("create")){
       			context.setSessionData("ID", "create_name");
       			return "You have chosen to create a new store. What do you want to name it?";
       		} else if(in.equalsIgnoreCase("append")){
       			context.setSessionData("ID", "append_name");
       			return "You have chosen to append entrances and exits to an existing store. Type the name of the store you want to edit.";
       		} else if(in.equalsIgnoreCase("delete")){
       			context.setSessionData("ID", "delete_name");
       			return "You have chosen to delete entrances and exits from an existing store. Type the name of the store you want to edit.";
       		} else if(in.equalsIgnoreCase("wipeout")){
       			context.setSessionData("ID", "wipeout_name");
        		return "You have chosen to wipe out a store. Type the name of the store you want to delete.";
        	}	
    	}
    	
    	/*
    	 * Create
    	 */
    	
    	else if(ID.equals("create_name")){
    		try {
    			if(in.equals("help")){
    				context.setSessionData("ID", "second");
    				return ChatColor.RED + LangPack.YOUCANTNAMEASTORETHAT;
    			}
        		if(!RealShopping.shopMap.containsKey(in)){
        			context.setSessionData("shop", in);
        			context.setSessionData("ID", "create_second");
        			new SetupStoreListener(player, Type.APPEND, in);
        			return "The name " + ChatColor.GREEN + in + ChatColor.RESET + " is avaiable.";
        		} else {
        			context.setSessionData("ID", "second");
        			return ChatColor.RED + "That name is already taken.";
        		}	
    		} catch(RSListenerException e){
    			if(e.getType() == RSListenerException.Type.PLAYER_ALREADY_HAS_LISTENER){
    				context.setSessionData("ID", "timetoquit");
    				return ChatColor.RED + "Error: A listener is already active for you.";
    			} else if(e.getType() == RSListenerException.Type.IN_SHOP){
    				context.setSessionData("ID", "timetoquit");
    				return ChatColor.RED + "You can't use this inside a store.";//TODO maybe reformulate
    			} else e.printStackTrace();
    		}
    	} else if(ID.equals("create_second") && context.getSessionData("shop") != null){
    		context.setSessionData("ID", "create_third");
			return "Right-click a block to select it as entrance/exit, or left-click in the air to use your coordinates."
				+ " Type " + ChatColor.LIGHT_PURPLE + "done" + ChatColor.RESET + " when done, or "
				+ ChatColor.LIGHT_PURPLE + "cancel" + ChatColor.RESET + " to start over (with the entrances).";
    	} else if(ID.equals("create_third") && context.getSessionData("shop") != null){
    		if(in.equalsIgnoreCase("done")){
    			int res = RSPlayerListener.finishConversationListener(player);
    			context.setSessionData("ID", "second");
    			return "Created store " + context.getSessionData("shop") + " with " + res + " entrance/exit pairs.";
    		} else if(in.equalsIgnoreCase("cancel")){
    			RSPlayerListener.killConversationListener(player);
    			context.setSessionData("ID", "create_second");//TODO decide
    			return "Action aborted.";
    		}
    	}
    	
    	/*
    	 * Append
    	 */
    	
    	else if(ID.equals("append_name")){
    		try {
        		String rtrn;
        		if(RealShopping.shopMap.containsKey(in)){
        			if(RealShopping.shopMap.get(in).getOwner().equals(player.getName())){
            			context.setSessionData("shop", in);
            			context.setSessionData("ID", "append_second");
            			new SetupStoreListener(player, Type.APPEND, in);
            			return "You have chosen to append new entrances and exits to " + in + ".";
        			} else rtrn = ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE;
        		} else rtrn = ChatColor.RED + LangPack.STORE + in + LangPack.DOESNTEXIST;
    			context.setSessionData("ID", "second");
    			return rtrn;
    		} catch (RSListenerException e){
    			if(e.getType() == RSListenerException.Type.PLAYER_ALREADY_HAS_LISTENER){
    				context.setSessionData("ID", "timetoquit");
    				return ChatColor.RED + "Error: A listener is already active for you.";
    			} else if(e.getType() == RSListenerException.Type.IN_SHOP){
    				context.setSessionData("ID", "timetoquit");
    				return ChatColor.RED + "You can't use this inside a store.";//TODO maybe reformulate
    			} else e.printStackTrace();
    		}
    	} else if(ID.equals("append_second") && context.getSessionData("shop") != null){
    		context.setSessionData("ID", "append_third");
			return "Right-click a block to select it as entrance/exit, or left-click in the air to use your coordinates."
				+ " Type " + ChatColor.LIGHT_PURPLE + "done" + ChatColor.RESET + " when done, or "
				+ ChatColor.LIGHT_PURPLE + "cancel" + ChatColor.RESET + " to cancel.";
    	} else if(ID.equals("append_third") && context.getSessionData("shop") != null){
    		if(in.equalsIgnoreCase("done")){
    			int res = RSPlayerListener.finishConversationListener(player);
    			context.setSessionData("ID", "second");
    			return "Appended " + res + " entrance/exit pairs to store "  + context.getSessionData("shop") + ".";
    		} else if(in.equalsIgnoreCase("cancel")){
    			RSPlayerListener.killConversationListener(player);
    			context.setSessionData("ID", "second");
    			return "Action aborted.";
    		}
    	}
    	
    	/*
    	 * Delete
    	 */
    	
    	else if(ID.equals("delete_name")){
    		try {
        		String rtrn;
        		if(RealShopping.shopMap.containsKey(in)){
        			if(RealShopping.shopMap.get(in).getOwner().equals(player.getName())){
            			context.setSessionData("shop", in);
            			context.setSessionData("ID", "delete_second");
            			new SetupStoreListener(player, Type.DELETE, in);
            			return "You have chosen to delete entrances and exits from " + in + ".";
        			} else rtrn = ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE;
        		} else rtrn = ChatColor.RED + LangPack.STORE + in + LangPack.DOESNTEXIST;
    			context.setSessionData("ID", "second");
    			return rtrn;
    		} catch (RSListenerException e){
    			if(e.getType() == RSListenerException.Type.PLAYER_ALREADY_HAS_LISTENER){
    				context.setSessionData("ID", "timetoquit");
    				return ChatColor.RED + "Error: A listener is already active for you.";
    			} else if(e.getType() == RSListenerException.Type.IN_SHOP){
    				context.setSessionData("ID", "timetoquit");
    				return ChatColor.RED + "You can't use this inside a store.";//TODO maybe reformulate
    			} else e.printStackTrace();
    		}
    	} else if(ID.equals("delete_second") && context.getSessionData("shop") != null){
    		context.setSessionData("ID", "delete_third");
			return "Right-click a block to select it as entrance/exit, or left-click in the air to use your coordinates."
    			+ " Type " + ChatColor.LIGHT_PURPLE + "done" + ChatColor.RESET + " when done, or "
				+ ChatColor.LIGHT_PURPLE + "cancel" + ChatColor.RESET + " to cancel.";
    	} else if(ID.equals("delete_third") && context.getSessionData("shop") != null){
    		if(in.equalsIgnoreCase("done")){
    			int res = RSPlayerListener.finishConversationListener(player);
    			context.setSessionData("ID", "second");
    			return "Deleted " + res + " entrance/exit pairs from store "  + context.getSessionData("shop") + ".";
    		} else if(in.equalsIgnoreCase("cancel")){
    			RSPlayerListener.killConversationListener(player);
    			context.setSessionData("ID", "second");
    			return "Action aborted.";
    		}
    	}
    	
    	/*
    	 * Wipeout
    	 */
    	
    	else if(ID.equals("wipeout_name")){
    		String rtrn;
    		if(RealShopping.shopMap.containsKey(in)){
    			if(RealShopping.shopMap.get(in).getOwner().equals(player.getName())){
        			context.setSessionData("shop", in);
        			context.setSessionData("ID", "wipeout_second");
        			return "Do you really want to delete " + ChatColor.DARK_AQUA + in + ChatColor.RESET
        					+ " and all its entrances, chests and prices.";
    			} else rtrn = ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE;
    		} else rtrn = ChatColor.RED + LangPack.STORE + in + LangPack.DOESNTEXIST;
			context.setSessionData("ID", "second");
			return rtrn;
    	} else if(ID.equals("wipeout_second") && context.getSessionData("shop") != null){
    		context.setSessionData("ID", "wipeout_confirm");
			return ChatColor.LIGHT_PURPLE + "yes" + ChatColor.RESET + " or " + ChatColor.LIGHT_PURPLE + "no";
    	} else if(ID.equals("wipeout_confirm") && context.getSessionData("shop") != null){
    		if(in.equalsIgnoreCase("yes")){
    			String rtrn = "";
    			Shop tempShop = RealShopping.shopMap.get(context.getSessionData("shop"));
    			if(tempShop != null){
    				if(RSUtils.getPlayersInStore(tempShop.getName())[0].equals("")){
    					RealShopping.shopMap.remove(tempShop.getName());
    					rtrn = ChatColor.GREEN + "Deleted " + ChatColor.DARK_AQUA + context.getSessionData("shop") + ChatColor.GREEN + " .";
    					RealShopping.updateEntrancesDb();
    				} else rtrn = ChatColor.RED + LangPack.STORENOTEMPTY;
    			} else rtrn = ChatColor.DARK_RED + "Error #2001";
    			context.setSessionData("ID", "second");
    			return rtrn;
    		} else if(in.equalsIgnoreCase("no")){
    			context.setSessionData("ID", "second");
    			return "Action aborted.";
    		}
    	}
    	
    	else if(ID.equals("rollback")){
     		context.setSessionData("ID", context.getSessionData("BACKTO"));
     		return ChatColor.DARK_PURPLE + "" + context.getSessionData("COM") + ChatColor.RED + " is not an accepted argument.";
     	}
    	
    	context.setSessionData("ID", "timetoquit");
		} catch (Exception e){
			e.printStackTrace();
		}
    	return "Error #1211";
    }

	public Prompt acceptInput(ConversationContext context, String in) {
		Object ID = context.getSessionData("ID");
		if(ID.equals("timetoquit")){
			return END_OF_CONVERSATION;
		}
		if(ID.equals("third") && !in.equalsIgnoreCase("create") && !in.equalsIgnoreCase("append")
				&& !in.equalsIgnoreCase("delete") && !in.equalsIgnoreCase("wipeout")){
			context.setSessionData("BACKTO", "second");
			context.setSessionData("COM", in);
			context.setSessionData("ID", "rollback");
		} else
		if(ID.equals("create_third") && !in.equalsIgnoreCase("done") && !in.equalsIgnoreCase("cancel")){
			context.setSessionData("BACKTO", "create_second");
			context.setSessionData("COM", in);
			context.setSessionData("ID", "rollback");
		} else
		if(ID.equals("append_third") && !in.equalsIgnoreCase("done") && !in.equalsIgnoreCase("cancel")){
			context.setSessionData("BACKTO", "append_second");
			context.setSessionData("COM", in);
			context.setSessionData("ID", "rollback");
		} else
		if(ID.equals("delete_third") && !in.equalsIgnoreCase("done") && !in.equalsIgnoreCase("cancel")){
			context.setSessionData("BACKTO", "delete_second");
			context.setSessionData("COM", in);
			context.setSessionData("ID", "rollback");
		} else
		if(ID.equals("wipeout_confirm") && !in.equalsIgnoreCase("yes") && !in.equalsIgnoreCase("no")){
			context.setSessionData("BACKTO", "wipeout_second");
			context.setSessionData("COM", in);
			context.setSessionData("ID", "rollback");
		}
		
		context.setSessionData("data", in);
		return this;
	}

	public boolean blocksForInput(ConversationContext context) {
		Object ID = context.getSessionData("ID");
		if(ID.equals("timetoquit") || ID.equals("second") || ID.equals("create_second") || ID.equals("append_second") || ID.equals("delete_second") || ID.equals("wipeout_second")) return false;
		return true;
	}
 
}