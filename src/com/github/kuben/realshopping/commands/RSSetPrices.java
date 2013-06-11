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

	private String arg = "";//TODO empty check on this one
	private String shop = "";
	private Shop store = null;
	
	public RSSetPrices(CommandSender sender, String[] args) {
		super(sender, args);
	}
	
	private boolean add(){
		try {
			Object[] o = RSUtils.pullPriceCostMinMax(arg);
			Price p = (Price)o[0];
			Integer[] i = (Integer[])o[1];
			String dString = p.getData()>-1?"("+p.getData()+")":"";
			store.setPrice(p, i[0]);
			sender.sendMessage(ChatColor.GREEN + LangPack.PRICEFOR + Material.getMaterial(p.getType()) + dString + LangPack.SETTO + i[0]/100f + LangPack.UNIT);
			if(i.length > 1){//Also set min max
				store.setMinMax(p, i[1], i[2]);
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
			if(store.hasPrice(p)){
				store.removePrice(p);
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
			if((args.length == 3 && shop.equals(args[1])) || (args.length == 2 && !shop.equals(args[1]))){//If copy from store
				if(RealShopping.shopMap.containsKey(args[args.length - 1])){
					store.clonePrices(args[args.length - 1]);
					sender.sendMessage(ChatColor.GREEN + LangPack.OLDPRICESREPLACEDWITHPRICESFROM + args[args.length - 1]);
					return true;
				}
			} else {
				store.clonePrices(null);
				sender.sendMessage(ChatColor.GREEN + LangPack.OLDPRICESREPLACEDWITHTHELOWEST_);
				return true;
			}
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + arg + LangPack.ISNOTAPROPER_);
		}
		return false;
	}
	
	private boolean clear(){
		store.clearPrices();
		sender.sendMessage(ChatColor.GREEN + LangPack.CLEAREDALLPRICESFOR + shop);
		return true;
	}
	
	private boolean defaults(){
		if(RealShopping.hasDefPrices()){
			store.setPrices(RealShopping.getDefPrices());
			sender.sendMessage(ChatColor.GREEN + LangPack.SETDEFAULTPRICESFOR + shop);
			return true;
		} else sender.sendMessage(ChatColor.RED + LangPack.THEREARENODEFAULTPRICES);
		return false;
	}
	
	private boolean showMinMax(){
		Price p = RSUtils.pullPrice(arg);
		String dString = p.getData()>-1?"("+p.getData()+")":"";
		if(store.hasMinMax(p)){
			sender.sendMessage(ChatColor.GREEN + LangPack.STORE + shop + LangPack.HASAMINIMALPRICEOF + store.getMin(p)/100f + LangPack.UNIT
				+ LangPack.ANDAMAXIMALPRICEOF + store.getMax(p)/100f + LangPack.UNIT + LangPack.FOR + Material.getMaterial(p.getType()) + dString);
		} else sender.sendMessage(ChatColor.GREEN + LangPack.STORE + shop + LangPack.DOESNTHAVEAMINIMALANDMAXIMALPRICEFOR + Material.getMaterial(p.getType()) + dString);
		return true;
	}
	
	private boolean clearMinMax(){
		Price p = RSUtils.pullPrice(arg);
		String dString = p.getData()>-1?"("+p.getData()+")":"";
		if(store.hasMinMax(p)){
			store.clearMinMax(p);
			sender.sendMessage(ChatColor.GREEN + LangPack.CLEAREDMINIMALANDMAXIMALPRICESFOR + Material.getMaterial(p.getType()) + dString);
		} else sender.sendMessage(ChatColor.GREEN + LangPack.STORE + shop + LangPack.DIDNTHAVEAMINIMALANDMAXIMALPRICEFOR + Material.getMaterial(p.getType()) + dString);
		return true;
	}
	
	private boolean setMinMax(){
		try {
			Object[] o = RSUtils.pullPriceMinMax(arg);
			Price p = (Price)o[0];
			Integer[] i = (Integer[])o[1];
			store.setMinMax(p, i[0], i[1]);
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
			boolean isPlayer = player != null && RealShopping.PInvMap.containsKey(player.getName());
			if(args[0].equalsIgnoreCase("add")//STORE ID:DATA:COST:MIN:MAX
					|| args[0].equalsIgnoreCase("del")//STORE ID:DATA 
					|| args[0].equalsIgnoreCase("showminmax")//STORE PRICE
					|| args[0].equalsIgnoreCase("clearminmax")//STORE PRICE
					|| args[0].equalsIgnoreCase("setminmax")){//STORE PRICE:MIN:MAX
				if(args.length < 3 && isPlayer){
					shop = RealShopping.PInvMap.get(player.getName()).getStore();
					arg = args[1];
				}
				else {
					shop = args[1];
					arg = args[2];
				}
			} else if(args[0].equalsIgnoreCase("copy")){//STORE STORE
				if(args.length < 2 && isPlayer){
					shop = RealShopping.PInvMap.get(player.getName()).getStore();
				} else if(args.length == 2){
					shop = args[1];
				} else if(args.length > 2) {
					shop = args[1];
					arg = args[2];
				}
			} else if(args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("defaults")){//STORE
				if(args.length == 1 && isPlayer) shop = RealShopping.PInvMap.get(player.getName()).getStore();
				else if(args.length > 1) shop = args[1];
			}
			
    		if(!shop.equals("")){
    			if(RealShopping.shopMap.containsKey(shop)){
    				store = RealShopping.shopMap.get(shop);
        			if(player == null || (store.getOwner().equals(player.getName()) || player.hasPermission("realshopping.rsset"))){//If player is owner OR has admin perms
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
					sender.sendMessage(ChatColor.RED + shop + LangPack.DOESNTEXIST);
				}
    		}
		}
		return false;
	}

	protected Boolean help(){
		//Check if help was asked for TODO add help
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			sender.sendMessage(ChatColor.RED + "No help documentation for this command.");//LANG
			return false;
		}
		return null;
	}
	
}
