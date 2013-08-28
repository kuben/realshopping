package com.github.kuben.realshopping.commands;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

class RSPrices extends RSCommand {
	
	private Shop shop;
	private int page = 1;
	public RSPrices(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
	    if(args.length == 2 && args[0].equalsIgnoreCase("search")){
	        if(player != null)
	            if(RealShopping.getPInv(player) != null){
	                return searchItem(RealShopping.getPInv(player).getShop(), RSUtils.pullPrice(args[1], this.player));
	            } else sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
	        else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
	        return false;
	    } else if (args.length == 3 && args[1].equalsIgnoreCase("search")){
	        return searchItem(RealShopping.getShop(args[0]), RSUtils.pullPrice(args[1], this.player));
	    }
	    
	    if(setVars() == false) return false;
		
		return Shop.prices(sender, page, shop);
	}
	
	
	/**
	 * Sets the <i>shop</i> and <i>page</i> variables.
	 * 
	 * Is to be called before Shop.prices
	 * @return True if everything went well and Shop.prices can be called, false if not and false should be returned by {@link #execute()}.
	 */
	private boolean setVars(){
	    //No arguments
	    if(args.length == 0)
	        if(player != null)
	            if(RealShopping.getPInv(player) != null){
	                shop = RealShopping.getPInv(player).getShop();
	                return true;
	            } else sender.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
	        else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);

	    //args[0] may be STORE or PAGE
	    else if(args.length == 1)
	        
	        //args[0] is PAGE
	        if(args[0].matches("[0-9]+"))
	            if(player != null)
	                if(RealShopping.hasPInv(player)){
	                    int i = Integer.parseInt(args[0]);
	                    if(i > 0) {
	                        page = i;
	                        shop = RealShopping.getPInv(player).getShop();
	                        return true;
	                    } else sender.sendMessage(ChatColor.RED + LangPack.THEPAGENUMBERMUSTBE1ORHIGHER);
	                } else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT);
	            else sender.sendMessage(ChatColor.RED + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);

	        //args[0] is STORE
	        else {
	            shop = RealShopping.getShop(args[0]);
	            if(shop != null) return true;
	            sender.sendMessage(ChatColor.RED + LangPack.STORE + ChatColor.DARK_RED + args[0] + ChatColor.RED + LangPack.DOESNTEXIST);
	        }

	    //args have to be STORE PAGE
	    else if(args.length == 2)
	        if(args[1].matches("[0-9]+")){
	            int i = Integer.parseInt(args[1]);
	            if(i > 0){
	                page = i;
	                shop = RealShopping.getShop(args[0]);
	                if(shop != null) return true;
	                else sender.sendMessage(ChatColor.RED + LangPack.STORE + ChatColor.DARK_RED + args[0] + ChatColor.RED + LangPack.DOESNTEXIST);
	            } else sender.sendMessage(ChatColor.RED + LangPack.THEPAGENUMBERMUSTBE1ORHIGHER);
	        } else sender.sendMessage(ChatColor.RED + "" + args[1] + LangPack.ISNOTAVALIDPAGENUMBER);
	    
	    return false;
	}
	
	private boolean searchItem(Shop shop, Price price){
		boolean noMatches = true;
		if(price.getData() != -1){//Item with specific data value requested
			double cost = shop.getPrice(price);
			String onSlStr = "";
			if(shop.hasSale(price)){//There is a sale on that item.
				int pcnt = 100 - shop.getSale(price);
				cost *= pcnt/100f;
				onSlStr = ChatColor.GREEN + LangPack.ONSALE;
			}
			sender.sendMessage(ChatColor.BLUE + "" + price.formattedString() + ChatColor.BLACK + " - " + ChatColor.RED + cost/100f + LangPack.UNIT + onSlStr);
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
                    sender.sendMessage(ChatColor.RED + "No matches for " + ChatColor.DARK_RED + price.formattedString());
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