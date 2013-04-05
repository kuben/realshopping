package com.github.kuben.realshopping.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.entity.Player;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;

class RSImport extends RSPlayerCommand {

	public RSImport(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if(player != null){
		    final Map<Object, Object> convMap = new HashMap<Object, Object>();
		    convMap.put("data", "first");
		    Conversation conv = RealShopping.convF.withFirstPrompt(new ImportPrompt()).withPrefix(
		    	new ConversationPrefix() {
		    		public String getPrefix(ConversationContext arg0){
		    			return ChatColor.LIGHT_PURPLE + "[RealShopping]" + ChatColor.WHITE + " ";
	            	}
	            }).withTimeout(30).withInitialSessionData(convMap).withLocalEcho(false).buildConversation(player);
		    conv.addConversationAbandonedListener(
		    		new ConversationAbandonedListener() {
		    			public void conversationAbandoned(ConversationAbandonedEvent event){
		    				if (event.gracefulExit()){
		    					((Player)((Conversation)event.getSource()).getForWhom()).sendRawMessage(ChatColor.LIGHT_PURPLE + "[RealShopping] " + ChatColor.WHITE + LangPack.QUITCONVERSATION);
		    				}
		    			}
		    		});
		    conv.begin();
		}
		sender.sendMessage(ChatColor.RED + LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE);
		return true;
	}

}