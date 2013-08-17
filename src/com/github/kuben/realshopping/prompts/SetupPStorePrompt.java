package com.github.kuben.realshopping.prompts;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RSListenerException;
import com.github.kuben.realshopping.listeners.SetupStoreListener;
import com.github.kuben.realshopping.listeners.SetupStoreListener.Type;
import com.github.kuben.realshopping.listeners.RSPlayerListener;

public class SetupPStorePrompt implements Prompt {
	 
    public String getPromptText(ConversationContext context) {
    	try {
        	String in = (String) context.getSessionData("data");
        	String ID = (String) context.getSessionData("ID");
    		Player player = (Player) context.getForWhom();
        	if(ID.equals("first")){
            	String out = "";
        		if(player.hasPermission("realshopping.rssetstores")){
        			out = LangPack.THIS_PROMPT_WILL_AID_YOU_IN__PLAYER_STORE_ + "\n" + LangPack.TO_CONTINUE_TYPE_ANY_OF_THE_PURPLE_KEYWORDS;
        			context.setSessionData("ID", "second");
        		} else {
        			context.setSessionData("ID", "timetoquit");
        			out = ChatColor.RED + LangPack.YOU_DONT_HAVE_THE_RIGHT_PERMISSIONS_TO_DO_THIS;
        		}
        		return out;
        	} else if(ID.equals("second")){
        		context.setSessionData("ID", "third");
        		return LangPack.WHAT_DO_YOU_WANT_TO_DO_ + ChatColor.LIGHT_PURPLE + "create" + ChatColor.RESET + LangPack.A_NEW_STORE_
        				+ ChatColor.LIGHT_PURPLE + "append" + ChatColor.RESET + "/" + ChatColor.LIGHT_PURPLE + "delete"
        				+ ChatColor.RESET + LangPack.ENTRANCES_AND_EXITS_OR_
        				+ ChatColor.LIGHT_PURPLE + "wipeout" + ChatColor.RESET + LangPack.AN_EXISTING_STORE;
        	} else if(ID.equals("third")){
           		if(in.equalsIgnoreCase("create")){
           			context.setSessionData("ID", "create_name");
           			return LangPack.YOU_HAVE_CHOSEN_TO_CREATE_A_NEW_STORE_;
           		} else if(in.equalsIgnoreCase("append")){
           			context.setSessionData("ID", "append_name");
           			return LangPack.YOU_HAVE_CHOSEN_TO_APPEND_NEW_EE_TO_;
           		} else if(in.equalsIgnoreCase("delete")){
           			context.setSessionData("ID", "delete_name");
           			return LangPack.YOU_HAVE_CHOSEN_TO_DELETE_EE_FROM_;
           		} else if(in.equalsIgnoreCase("wipeout")){
           			context.setSessionData("ID", "wipeout_name");
            		return LangPack.YOU_HAVE_CHOSEN_TO_WIPE_OUT_A_STORE_;
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
            		if(!RealShopping.shopExists(in)){
            			context.setSessionData("shop", in);
            			context.setSessionData("ID", "create_second");
            			new SetupStoreListener(player, Type.APPEND, in, false);
            			return LangPack.THE_NAME_ + ChatColor.GREEN + in + ChatColor.RESET + LangPack.IS_AVAILABLE;
            		} else {
            			context.setSessionData("ID", "second");
            			return ChatColor.RED + LangPack.THAT_NAME_IS_ALREADY_TAKEN;
            		}	
        		} catch(RSListenerException e){
        			if(e.getType() == RSListenerException.Type.PLAYER_ALREADY_HAS_LISTENER){
        				context.setSessionData("ID", "timetoquit");
        				return ChatColor.RED + "Error: A listener is already active for you.";
        			} else if(e.getType() == RSListenerException.Type.IN_SHOP){
        				context.setSessionData("ID", "timetoquit");
        				return ChatColor.RED + LangPack.YOU_CANT_USE_THIS_INSIDE_A_STORE;
        			} else e.printStackTrace();
        		}
        	} else if(ID.equals("create_second") && context.getSessionData("shop") != null){
        		context.setSessionData("ID", "create_third");
    			return LangPack.RIGHTCLICK_A_BLOCK_TO_SELECT_IT_AS_EE_
    				+ LangPack.TYPE + ChatColor.LIGHT_PURPLE + "done" + ChatColor.RESET + LangPack.WHEN_DONE_OR_
    				+ ChatColor.LIGHT_PURPLE + "cancel" + ChatColor.RESET + LangPack.TO_START_OVER_;
        	} else if(ID.equals("create_third") && context.getSessionData("shop") != null){
        		if(in.equalsIgnoreCase("done")){
        			int res = RSPlayerListener.finishConversationListener(player);
        			context.setSessionData("ID", "second");
        			if(res > 0) return LangPack.CREATED_STORE_ + context.getSessionData("shop") + LangPack.WITH + res + LangPack.EE_PAIRS;
        			else return ChatColor.RED + LangPack.NO_EE_SELECTED;
        		} else if(in.equalsIgnoreCase("cancel")){
        			RSPlayerListener.killConversationListener(player);
        			new SetupStoreListener(player, Type.APPEND, (String)context.getSessionData("shop"), false);
        			context.setSessionData("ID", "create_second");
        			return LangPack.ACTION_ABORTED;
        		}
        	}
        	
        	/*
        	 * Append
        	 */
        	
        	else if(ID.equals("append_name")){
        		try {
            		String rtrn;
            		if(RealShopping.shopExists(in)){
            			if(RealShopping.getShop(in).getOwner().equals(player.getName())){
                			context.setSessionData("shop", in);
                			context.setSessionData("ID", "append_second");
                			new SetupStoreListener(player, Type.APPEND, in, false);
                			return LangPack.YOU_HAVE_CHOSEN_TO_APPEND_NEW_EE_TO_ + in + ".";
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
        				return ChatColor.RED + LangPack.YOU_CANT_USE_THIS_INSIDE_A_STORE;
        			} else e.printStackTrace();
        		}
        	} else if(ID.equals("append_second") && context.getSessionData("shop") != null){
        		context.setSessionData("ID", "append_third");
    			return LangPack.RIGHTCLICK_A_BLOCK_TO_SELECT_IT_AS_EE_
    				+ LangPack.TYPE + ChatColor.LIGHT_PURPLE + "done" + ChatColor.RESET + LangPack.WHEN_DONE_OR_
    				+ ChatColor.LIGHT_PURPLE + "cancel" + ChatColor.RESET + LangPack.TOCANCEL;
        	} else if(ID.equals("append_third") && context.getSessionData("shop") != null){
        		if(in.equalsIgnoreCase("done")){
        			int res = RSPlayerListener.finishConversationListener(player);
        			context.setSessionData("ID", "second");
        			if(res > 0) return LangPack.APPENDED_ + res + LangPack.EE_PAIRS_TO_STORE_  + context.getSessionData("shop") + ".";
        			else return ChatColor.RED + LangPack.NO_EE_SELECTED;
        		} else if(in.equalsIgnoreCase("cancel")){
        			RSPlayerListener.killConversationListener(player);
        			context.setSessionData("ID", "second");
        			return LangPack.ACTION_ABORTED;
        		}
        	}
        	
        	/*
        	 * Delete
        	 */
        	
        	else if(ID.equals("delete_name")){
        		try {
            		String rtrn;
            		if(RealShopping.shopExists(in)){
            			if(RealShopping.getShop(in).getOwner().equals(player.getName())){
                			context.setSessionData("shop", in);
                			context.setSessionData("ID", "delete_second");
                			new SetupStoreListener(player, Type.DELETE, in, false);
                			return LangPack.YOU_HAVE_CHOSEN_TO_DELETE_EE_FROM_ + in + ".";
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
        				return ChatColor.RED + LangPack.YOU_CANT_USE_THIS_INSIDE_A_STORE;
        			} else e.printStackTrace();
        		}
        	} else if(ID.equals("delete_second") && context.getSessionData("shop") != null){
        		context.setSessionData("ID", "delete_third");
    			return LangPack.RIGHTCLICK_A_BLOCK_TO_SELECT_IT_AS_EE_
        			+ " Type " + ChatColor.LIGHT_PURPLE + "done" + ChatColor.RESET + LangPack.WHEN_DONE_OR_
    				+ ChatColor.LIGHT_PURPLE + "cancel" + ChatColor.RESET + LangPack.TOCANCEL;
        	} else if(ID.equals("delete_third") && context.getSessionData("shop") != null){
        		if(in.equalsIgnoreCase("done")){
        			int res = RSPlayerListener.finishConversationListener(player);
        			context.setSessionData("ID", "second");
        			if(res > 0) return LangPack.DELETED_ + res + LangPack.EE_PAIRS_FROM_STORE_ + context.getSessionData("shop") + ".";
        			else return ChatColor.RED + LangPack.NO_EE_SELECTED;
        		} else if(in.equalsIgnoreCase("cancel")){
        			RSPlayerListener.killConversationListener(player);
        			context.setSessionData("ID", "second");
        			return LangPack.ACTION_ABORTED;
        		}
        	}
        	
        	/*
        	 * Wipeout
        	 */
        	
        	else if(ID.equals("wipeout_name")){
        		String rtrn;
        		if(RealShopping.shopExists(in)){
        			if(RealShopping.getShop(in).getOwner().equals(player.getName())){
            			context.setSessionData("shop", in);
            			context.setSessionData("ID", "wipeout_second");
            			return LangPack.DO_YOU_REALLY_WANT_TO_DELETE_ + ChatColor.DARK_AQUA + in + ChatColor.RESET
            					+ LangPack.AND_ALL_ITS_;
        			} else rtrn = ChatColor.RED + LangPack.YOUARENOTTHEOWNEROFTHISSTORE;
        		} else rtrn = ChatColor.RED + LangPack.STORE + in + LangPack.DOESNTEXIST;
    			context.setSessionData("ID", "second");
    			return rtrn;
        	} else if(ID.equals("wipeout_second") && context.getSessionData("shop") != null){
        		context.setSessionData("ID", "wipeout_confirm");
    			return ChatColor.LIGHT_PURPLE + "yes" + ChatColor.RESET + LangPack.OR_ + ChatColor.LIGHT_PURPLE + "no";
        	} else if(ID.equals("wipeout_confirm") && context.getSessionData("shop") != null){
        		if(in.equalsIgnoreCase("yes")){
        			String rtrn = "";
        			Shop tempShop = RealShopping.getShop((String) context.getSessionData("shop"));
        			if(tempShop != null){
        				if(RealShopping.getPlayersInStore(tempShop.getName())[0].equals("")){
        					tempShop.clearEntrancesExits();
        					RealShopping.removeShop(tempShop.getName());
        					rtrn = ChatColor.GREEN + LangPack.DELETED_ + ChatColor.DARK_AQUA + context.getSessionData("shop") + ChatColor.GREEN + " .";
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
         		return ChatColor.LIGHT_PURPLE + "" + context.getSessionData("COM") + ChatColor.RED + LangPack.IS_NOT_AN_ACCEPTED_ARGUMENT;
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