package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;

import com.github.kuben.realshopping.prompts.PromptMaster;
import com.github.kuben.realshopping.prompts.PromptMaster.PromptType;

class RSImport extends RSCommand {

	private final Conversable converser;
	public RSImport(CommandSender sender, String[] args) {
		super(sender, args);
		converser = (Conversable)((sender instanceof Conversable)?sender:null);
	}

	@Override
	protected boolean execute() {
		if(converser != null){
			return PromptMaster.createConversation(PromptType.IMPORT_PRICES,converser);
		}
		sender.sendMessage(ChatColor.RED + "Error: Cannot begin conversation.");
		return true;
	}

}