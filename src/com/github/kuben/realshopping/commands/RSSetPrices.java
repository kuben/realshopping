package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;


class RSSetPrices extends RSCommand {

	private String arg = "";
	private String store = "";
	private Shop shop = null;
	
	public RSSetPrices(CommandSender sender, String[] args) {
		super(sender, args);
	}
	
	private boolean add(){
		try {
			Object[] o = RSUtils.pullPriceCostMinMax(arg);
			Price p = (Price)o[0];
			Integer[] i = (Integer[])o[1];
			String dString = p.getData()>-1?"("+p.getData()+")":"";
			shop.setPrice(p, i[0]);
			sender.sendMessage(ChatColor.GREEN + LangPack.PRICEFOR + Material.getMaterial(p.getType()) + dString + LangPack.SETTO + i[0]/100f + LangPack.UNIT);
			if(i.length > 1){//Also set min max
				shop.setMinMax(p, i[1], i[2]);
				sender.sendMessage(ChatColor.GREEN + LangPack.SETMINIMALANDMAXIMALPRICESFOR + Material.getMaterial(p.getType()) + dString);
			}
			return true;
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + arg + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + LangPack.UNIT);
			if(Config.debug) e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e){
			sender.sendMessage(ChatColor.RED + arg + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + LangPack.UNIT);
			if(Config.debug) e.printStackTrace();
		} catch (ClassCastException e){
			sender.sendMessage(ChatColor.RED + arg + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + LangPack.UNIT);
			if(Config.debug) e.printStackTrace();
		}
		return false;
	}

	private boolean del(){
		try {
			Price p = RSUtils.pullPrice(arg);
			String dString = p.getData()>-1?"("+p.getData()+")":"";
			if(shop.hasPrice(p)){
				shop.removePrice(p);
				sender.sendMessage(ChatColor.RED + LangPack.REMOVEDPRICEFOR + Material.getMaterial(p.getType()) + dString);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + LangPack.COULDNTFINDPRICEFOR + Material.getMaterial(p.getType()) + dString);
			}
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + arg + LangPack.ISNOTAPROPER_);
		}
		return false;
	}
	
	private boolean copy(){
		try {
			if((args.length == 3 && store.equals(args[1])) || (args.length == 2 && !store.equals(args[1]))){//If copy from store
				if(RealShopping.shopMap.containsKey(args[args.length - 1])){
					shop.clonePrices(args[args.length - 1]);
					sender.sendMessage(ChatColor.GREEN + LangPack.OLDPRICESREPLACEDWITHPRICESFROM + args[args.length - 1]);
					return true;
				}
			} else {
				shop.clonePrices(null);
				sender.sendMessage(ChatColor.GREEN + LangPack.OLDPRICESREPLACEDWITHTHELOWEST_);
				return true;
			}
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + arg + LangPack.ISNOTAPROPER_);
		}
		return false;
	}
	
	private boolean clear(){
		shop.clearPrices();
		sender.sendMessage(ChatColor.GREEN + LangPack.CLEAREDALLPRICESFOR + store);
		return true;
	}
	
	private boolean defaults(){
		if(RealShopping.hasDefPrices()){
			shop.setPrices(RealShopping.getDefPrices());
			sender.sendMessage(ChatColor.GREEN + LangPack.SETDEFAULTPRICESFOR + store);
			return true;
		} else sender.sendMessage(ChatColor.RED + LangPack.THEREARENODEFAULTPRICES);
		return false;
	}
	
	private boolean showMinMax(){
		Price p = RSUtils.pullPrice(arg);
		String dString = p.getData()>-1?"("+p.getData()+")":"";
		if(shop.hasMinMax(p)){
			sender.sendMessage(ChatColor.GREEN + LangPack.STORE + store + LangPack.HASAMINIMALPRICEOF + shop.getMin(p)/100f + LangPack.UNIT
				+ LangPack.ANDAMAXIMALPRICEOF + shop.getMax(p)/100f + LangPack.UNIT + LangPack.FOR + Material.getMaterial(p.getType()) + dString);
		} else sender.sendMessage(ChatColor.GREEN + LangPack.STORE + store + LangPack.DOESNTHAVEAMINIMALANDMAXIMALPRICEFOR + Material.getMaterial(p.getType()) + dString);
		return true;
	}
	
	private boolean clearMinMax(){
		Price p = RSUtils.pullPrice(arg);
		String dString = p.getData()>-1?"("+p.getData()+")":"";
		if(shop.hasMinMax(p)){
			shop.clearMinMax(p);
			sender.sendMessage(ChatColor.GREEN + LangPack.CLEAREDMINIMALANDMAXIMALPRICESFOR + Material.getMaterial(p.getType()) + dString);
		} else sender.sendMessage(ChatColor.GREEN + LangPack.STORE + store + LangPack.DIDNTHAVEAMINIMALANDMAXIMALPRICEFOR + Material.getMaterial(p.getType()) + dString);
		return true;
	}
	
	private boolean setMinMax(){
		try {
			Object[] o = RSUtils.pullPriceMinMax(arg);
			Price p = (Price)o[0];
			Integer[] i = (Integer[])o[1];
			shop.setMinMax(p, i[0], i[1]);
			String dString = p.getData()>-1?"("+p.getData()+")":"";
			sender.sendMessage(ChatColor.GREEN + LangPack.SETMINIMALANDMAXIMALPRICESFOR + Material.getMaterial(p.getType()) + dString);
			return true;
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + arg + LangPack.ISNOTAPROPERARGUMENT);
		}
		return false;
	}

	@Override
	protected boolean execute() {
		if(args.length > 0){
			boolean isPlayer = player != null && RealShopping.hasPInv(player);
			if(args[0].equalsIgnoreCase("add")//STORE ID:DATA:COST:MIN:MAX
					|| args[0].equalsIgnoreCase("del")//STORE ID:DATA 
					|| args[0].equalsIgnoreCase("showminmax")//STORE PRICE
					|| args[0].equalsIgnoreCase("clearminmax")//STORE PRICE
					|| args[0].equalsIgnoreCase("setminmax")){//STORE PRICE:MIN:MAX
				if(args.length < 3 && isPlayer){
					store = RealShopping.getPInv(player).getStore();
					arg = args[1];
				} else {
					store = args[1];
					arg = args[2];
				}
			} else if(args[0].equalsIgnoreCase("copy")){//STORE STORE
				if(args.length < 2 && isPlayer){
					store = RealShopping.getPInv(player).getStore();
				} else if(args.length == 2){
					store = args[1];
				} else if(args.length > 2) {
					store = args[1];
					arg = args[2];
				}
			} else if(args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("defaults")){//STORE
				if(args.length == 1 && isPlayer) store = RealShopping.getPInv(player).getStore();
				else if(args.length > 1) store = args[1];
			}
			
    		if(!store.equals("")){
    			if(RealShopping.shopMap.containsKey(store)){
    				shop = RealShopping.shopMap.get(store);
        			if(player == null || (shop.getOwner().equals(player.getName()) || player.hasPermission("realshopping.rsset"))){//If player is owner OR has admin perms
        				if(args[0].equalsIgnoreCase("add")) return add();
        				else if(args[0].equalsIgnoreCase("del")) return del();
        				else if(args[0].equalsIgnoreCase("copy")) return copy();
        				else if(args[0].equalsIgnoreCase("clear")) return clear();
        				else if(args[0].equalsIgnoreCase("defaults")) return defaults();
        				else if(args[0].equalsIgnoreCase("showminmax")) return showMinMax();
        				else if(args[0].equalsIgnoreCase("clearminmax")) return clearMinMax();
        				else if(args[0].equalsIgnoreCase("setminmax")) return setMinMax();
        			} else sender.sendMessage(ChatColor.RED + LangPack.YOUARENTPERMITTEDTOEMANAGETHISSTORE);
				} else {
					sender.sendMessage(ChatColor.RED + store + LangPack.DOESNTEXIST);
				}
    		}
		}
		return false;
	}

	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rssetprices add|del|defaults|copy|clear [STORE] (ITEM_ID[:DATA][:COST][:MIN:MAX])|[COPY_FROM]");
				sender.sendMessage(" OR /rssetprices showminmax|clearminmax|setminmax [STORE] [ITEM_ID[:DATA]:MIN:MAX]]");
				sender.sendMessage("For help for a specific command, type: " + ChatColor.DARK_PURPLE + "/rssetchests help COMMAND");
			} else if(args.length == 1){
				sender.sendMessage(ChatColor.GREEN + "Sets prices for all kinds of stores. When used in the targeted store, the STORE argument is not necessary. You can get more help about each of these arguments: " + ChatColor.DARK_PURPLE + "add, del, defaults, copy, clear, showminmax, clearminmax, setminmax");
			} else {
				if(args[1].equals("add")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "add [STORE] ITEM_ID[:DATA]:COST[:MIN:MAX]" + ChatColor.RESET + ". Sets the price for the specified item to COST. The other commands have more documentation on the MIN and MAX commands.");
				else if(args[1].equals("del")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "del [STORE] ITEM_ID[:DATA]" + ChatColor.RESET + ". Deletes the price of an item.");
				else if(args[1].equals("defaults")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "defaults [STORE]" + ChatColor.RESET + ". Imports the default server prices to your store. Default prices are imported with the /rsimport command.");
				else if(args[1].equals("copy")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "copy [STORE] [COPY_FROM]" + ChatColor.RESET + ". Copies the prices from COPY_FROM to your store. If you skip the COPY_FROM argument, the lowest price for every item in every store is copied.");
				else if(args[1].equals("clear")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "clear [STORE]" + ChatColor.RESET + ". Clears all prices from the store.");
				else if(args[1].equals("showminmax")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "showminmax [STORE] ITEM_ID" + ChatColor.RESET + ". Shows the currently set minimal and maximal price for the item.");
				else if(args[1].equals("clearminmax")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "clearminmax [STORE] ITEM_ID" + ChatColor.RESET + ". Clears the currently set minimal and maximal price for the item. ");
				else if(args[1].equals("setminmax")) sender.sendMessage(LangPack.USAGE + ChatColor.DARK_PURPLE + "setminmax [STORE] ITEM_ID:MIN:MAX" + ChatColor.RESET + ". Sets the minimal and maximal price for the item. Data values are not currently supported. ");
			}
			return true;
		}
		return null;
	}
	
}
