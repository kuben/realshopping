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
	private static Map<Player, Conversation> convs;
	
	public static enum PromptType{ IMPORT_PRICES,CHOOSE_CHESTS,SETUP_STORE,SETUP_PLAYER_STORE }
	
	private PromptMaster(){ }//no..
	
	public static void initialize(RealShopping rs){
		convF = new ConversationFactory(rs);
		prefix = new PluginNameConversationPrefix(rs);
		abandonL = new RSConversationAbandonedListener();
		convs = new HashMap<Player, Conversation>();
	}
	
	private static boolean isInitialized(){ return convF != null && prefix != null && abandonL != null && convs != null; }
	
	public static boolean isConversing(Player player){ return convs.containsKey(player); }
	protected static void removeConversationFromMap(Player player){ convs.remove(player); }//Used by AbandonedListener
	public static boolean abandonConversation(Player player){//Used by /rsstores kick
		if(convs.containsKey(player)){
			convs.get(player).abandon();
			convs.remove(player);
			return true;
		}
		return false;
	}
	public static void abandonAllConversations(){//Used by onDisable()
		if(!convs.isEmpty()){
			RealShopping.log(LangPack.ABANDONING_CONVERSATIONS);
			for(Player p:convs.keySet()){
				convs.get(p).abandon();
			}
			convs.clear();
			RealShopping.log(LangPack.CONVERSATIONS_ABANDONED);
		}
	}
	
	public static boolean createConversation(PromptType type, Conversable c){
		if(isInitialized()){
			if(!c.isConversing()){
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
		  				if(c instanceof Player) return createSetupStoreConv((Player) c);
		  				else return false;
			  	}
			}
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
	    if(c instanceof Player) convs.put((Player)c, conv);
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
	    convs.put(p, conv);
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
	    convs.put(p, conv);
	    return true;
	}
	
	private static boolean createSetupStoreConv(Player p){
	    final Map<Object, Object> convMap = new HashMap<Object, Object>();
	    convMap.put("ID", "first");
	    Conversation conv = convF.withFirstPrompt(new SetupStorePrompt()).withPrefix(prefix)
	    		.withEscapeSequence("quit").withEscapeSequence("exit") 
	    		.withInitialSessionData(convMap).withLocalEcho(false).buildConversation(p);
	    conv.addConversationAbandonedListener(abandonL);
	    conv.begin();
	    convs.put(p, conv);
	    return true;
	}
}

class RSConversationAbandonedListener implements ConversationAbandonedListener {
	public void conversationAbandoned(ConversationAbandonedEvent event){
		if(event.getContext().getForWhom() instanceof Player){
			PromptMaster.removeConversationFromMap((Player)event.getContext().getForWhom());
			RSPlayerListener.killConversationListener((Player)event.getContext().getForWhom());
		}
		
		if (event.gracefulExit()){//if getNextPrompt returned END_OF_CONVERSATION
			((Conversable)((Conversation)event.getSource()).getForWhom()).sendRawMessage(ChatColor.LIGHT_PURPLE
					+ "[RealShopping] " + ChatColor.WHITE + LangPack.QUITCONVERSATION);
		} else {
			ConversationCanceller c = event.getCanceller();
			if(c != null){
				if(c instanceof ExactMatchConversationCanceller)
					((Conversable)((Conversation)event.getSource()).getForWhom()).sendRawMessage(ChatColor.LIGHT_PURPLE
							+ "[RealShopping] " + ChatColor.WHITE + LangPack.QUITCONVERSATION);
				else {
					((Conversable)((Conversation)event.getSource()).getForWhom()).sendRawMessage(ChatColor.LIGHT_PURPLE
							+ "[RealShopping] " + ChatColor.WHITE + LangPack.QUIT_CONVERSATION_FOR_UNKNOWN_REASON);
				}
			}
		}
	}
}