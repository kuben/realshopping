package com.github.kuben.realshopping.prompts;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ExactMatchConversationCanceller;
import org.bukkit.conversations.PluginNameConversationPrefix;
import org.bukkit.entity.Player;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.listeners.RSPlayerListener;

public class PromptMaster {

	private static ConversationFactory convF;
	private static PluginNameConversationPrefix prefix;
	private static ConversationAbandonedListener abandonL;
	
	public static enum PromptType{ IMPORT_PRICES,CHOOSE_CHESTS,SETUP_STORE,SETUP_PLAYER_STORE }
	
	private PromptMaster(){ }//no..
	
	public static void initialize(RealShopping rs){
		convF = new ConversationFactory(rs);
		prefix = new PluginNameConversationPrefix(rs);
		abandonL = new RSConversationAbandonedListener();
	}
	
	private static boolean isInitialized(){ return convF != null && prefix != null && abandonL != null; }
	
	public static boolean createConversation(PromptType type, Conversable c){
		if(isInitialized())
	  	switch(type){
	  		case IMPORT_PRICES:
	  			return createIPConv(c);
	  		case CHOOSE_CHESTS:
	  			if(c instanceof Player) return createChestConv((Player) c);
	  			else return false;
	  		case SETUP_PLAYER_STORE:
	  			if(c instanceof Player) return createSetupPStoreConv((Player) c);
	  			else return false;
	  		case SETUP_STORE:
	  			c.sendRawMessage("Not yet implemented.");
	  	}
		return false;
	}
	
	private static boolean createIPConv(Conversable c){
	    final Map<Object, Object> convMap = new HashMap<Object, Object>();
	    convMap.put("data", "first");
	    Conversation conv = convF.withFirstPrompt(new ImportPrompt()).withPrefix(prefix).withTimeout(30)
	    		.withEscapeSequence("cancel").withEscapeSequence("quit").withEscapeSequence("exit")//Can also be done with a custom ConversationCanceller instead. 
	    		.withInitialSessionData(convMap).withLocalEcho(false).buildConversation(c);
	    conv.addConversationAbandonedListener(abandonL);
	    conv.begin();
	    return true;
	}
	
	private static boolean createChestConv(Player p){
	    final Map<Object, Object> convMap = new HashMap<Object, Object>();
	    convMap.put("ID", "first");
	    Conversation conv = convF.withFirstPrompt(new ChestPrompt()).withPrefix(prefix)
	    		.withEscapeSequence("quit").withEscapeSequence("exit") 
	    		.withInitialSessionData(convMap).withLocalEcho(false).buildConversation(p);
	    conv.addConversationAbandonedListener(abandonL);
	    conv.begin();
	    return true;
	}
	
	private static boolean createSetupPStoreConv(Player p){
	    final Map<Object, Object> convMap = new HashMap<Object, Object>();
	    convMap.put("ID", "first");
	    Conversation conv = convF.withFirstPrompt(new SetupPStorePrompt()).withPrefix(prefix)
	    		.withEscapeSequence("quit").withEscapeSequence("exit") 
	    		.withInitialSessionData(convMap).withLocalEcho(false).buildConversation(p);
	    conv.addConversationAbandonedListener(abandonL);
	    conv.begin();
	    return true;
	}
}

class RSConversationAbandonedListener implements ConversationAbandonedListener {
	public void conversationAbandoned(ConversationAbandonedEvent event){
		if(event.getContext().getForWhom() instanceof Player)
			RSPlayerListener.killConversationListener((Player)event.getContext().getForWhom());
		
		if (event.gracefulExit()){//if getNextPrompt returned END_OF_CONVERSATION
			((Conversable)((Conversation)event.getSource()).getForWhom()).sendRawMessage(ChatColor.LIGHT_PURPLE
					+ "[RealShopping] " + ChatColor.WHITE + LangPack.QUITCONVERSATION);
		} else {
			ConversationCanceller c = event.getCanceller();
			if(c != null){
				if(c instanceof ExactMatchConversationCanceller)
					((Conversable)((Conversation)event.getSource()).getForWhom()).sendRawMessage(ChatColor.LIGHT_PURPLE
							+ "[RealShopping] " + ChatColor.WHITE + "Conversation aborted.");//LANG
				else {
					((Conversable)((Conversation)event.getSource()).getForWhom()).sendRawMessage(ChatColor.LIGHT_PURPLE
							+ "[RealShopping] " + ChatColor.WHITE + "Quit conversation for unknown reason.");//LANG
				}
			}
		}
	}
}