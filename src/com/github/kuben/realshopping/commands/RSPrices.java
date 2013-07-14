package com.github.kuben.realshopping.commands;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;

class RSPrices extends RSCommand {
	
	private String shop;
	private int page = 1;
	public RSPrices(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if(args.length == 0){
			if(player != null){
				if(RealShopping.getPInv(player) != null) {
					shop = RealShopping.getPInv(player).getStore();
				} else {
					sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
					return false;
				}
			} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
		} else if(args.length == 1){//May be STORE or PAGE 
			if(args[0].matches("[0-9]+")){
				if(player != null){
					if(RealShopping.hasPInv(player)){
						int i = Integer.parseInt(args[0]);
						if(i > 0) {
							page = i;
							shop = RealShopping.getPInv(player).getStore();
						}
						else sender.sendMessage(ChatColor.RED + LangPack.THEPAGENUMBERMUSTBE1ORHIGHER);
					} else {
						sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT);
					}
				} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
			} else if(args[0].equalsIgnoreCase("search")){
				sender.sendMessage(ChatColor.RED + LangPack.YOU_HAVE_TO_SEARCH_FOR_A_SPECIFIC_ITEM);
				return false;
			} else {
				shop = args[0];
			}
		} else if(args.length == 2){//May be STORE PAGE or search ITEM
			if(args[0].equalsIgnoreCase("search")){
				if(player != null){
					if(RealShopping.getPInv(player) != null) {
						return searchItem(RealShopping.shopMap.get(RealShopping.getPInv(player).getStore())
								, RSUtils.pullPrice(args[1]));
					} else {
						sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
						return false;
					}
				} else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
			} else {
				if(args[1].matches("[0-9]+")){
					int i = Integer.parseInt(args[1]);
					if(i > 0){
						shop = args[0];
						page = i;
					} else sender.sendMessage(ChatColor.RED + LangPack.THEPAGENUMBERMUSTBE1ORHIGHER);
				} else {
					sender.sendMessage(ChatColor.RED + "" + args[1] + LangPack.ISNOTAVALIDPAGENUMBER);
				}	
			}
		} else if (args.length > 2 && args[1].equalsIgnoreCase("search")){//Has to be STORE search ITEM
			return searchItem(RealShopping.shopMap.get(args[0]), RSUtils.pullPrice(args[2]));
		}
		
		return Shop.prices(sender, page, shop);
	}
	
	private boolean searchItem(Shop shop, Price price){
		boolean noMatches = true;
		if(price.getData() != -1){//Item with specific data value requested
			int cost = shop.getPrice(price);
			String onSlStr = "";
			if(shop.hasSale(price)){//There is a sale on that item.
				int pcnt = 100 - shop.getSale(price);
				cost *= pcnt/100f;
				onSlStr = ChatColor.GREEN + LangPack.ONSALE;
			}
			noMatches = false;
			sender.sendMessage(ChatColor.BLUE + "" + price + " " + Material.getMaterial(price.getType()) + ChatColor.BLACK + " - " + ChatColor.RED + cost/100f + LangPack.UNIT + onSlStr);
			return true;
		}
		Map<Price, Integer> tempMap = shop.getPrices();
		for(Price p:tempMap.keySet()){
			if(p.getType() == price.getType()){//Match
				int cost = tempMap.get(p);
				String onSlStr = "";
					if(shop.hasSale(p.stripOffData()) || shop.hasSale(p)){//There is a sale on that item.
						int pcnt = -1;
						if(shop.hasSale(p.stripOffData())) pcnt = 100 - shop.getSale(p.stripOffData());
						if(shop.hasSale(p))  pcnt = 100 - shop.getSale(p);
						cost *= pcnt/100f;
					onSlStr = ChatColor.GREEN + LangPack.ONSALE;
				}
				noMatches = false;
				sender.sendMessage(ChatColor.BLUE + "" + p + " " + Material.getMaterial(p.getType()) + ChatColor.BLACK + " - " + ChatColor.RED + cost/100f + LangPack.UNIT + onSlStr);
			}
		}
		if(noMatches){
			Material m = Material.getMaterial(price.getType());
			sender.sendMessage(ChatColor.RED + LangPack.NO_MATCHES_FOR_ + ChatColor.DARK_RED + (m == null?price.getType():m));
		}
		return true;
	}
	
	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length > 0 && args[0].equalsIgnoreCase("help")){
			sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rsprices [STORE] [PAGE|search ITEM]");
			sender.sendMessage(LangPack.RSPRICESHELP + ChatColor.DARK_PURPLE + "STORE"
					+ ChatColor.RESET + LangPack.RSPRICESHELP2 + ChatColor.LIGHT_PURPLE + "PAGE"
					+ ChatColor.RESET + LangPack.RSPRICESHELP3);
			return true;
		}
		return null;
	}
}