package com.github.kuben.realshopping.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;


class RSSetPrices extends RSCommand {

	public RSSetPrices(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	protected boolean execute() {
		if(args.length > 0){
			String shop = "";
			boolean isPlayer = player != null && RealShopping.PInvMap.containsKey(player.getName());
			int ii = 1;//First argument after store, not all commands need this
			if(args[0].equalsIgnoreCase("add")//STORE ID:DATA:COST:MIN:MAX
					|| args[0].equalsIgnoreCase("del")//STORE ID:DATA 
					|| args[0].equalsIgnoreCase("showminmax")//STORE PRICE
					|| args[0].equalsIgnoreCase("clearminmax")//STORE PRICE
					|| args[0].equalsIgnoreCase("setminmax")){//STORE PRICE:MIN:MAX
				if(args.length < 3 && isPlayer) shop = RealShopping.PInvMap.get(player.getName()).getStore();
				else {
					shop = args[1];
					ii = 2;
				}
			} else if(args[0].equalsIgnoreCase("copy")){//STORE STORE
				if(args.length < 2 && isPlayer){
					shop = RealShopping.PInvMap.get(player.getName()).getStore();
				} else if(args.length == 2) shop = args[1];
				else if(args.length > 2) {
					shop = args[1];
					ii = 2;
				}
			} else if(args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("defaults")){//STORE
				if(args.length == 1 && isPlayer) shop = RealShopping.PInvMap.get(player.getName()).getStore();
				else if(args.length > 1) shop = args[1];
			}
			
    		if(!shop.equals("")){
    			if(RealShopping.shopMap.containsKey(shop)){
        			if(player == null || (RealShopping.shopMap.get(shop).getOwner().equals(player.getName()) || player.hasPermission("realshopping.rsset"))){//If player is owner OR has admin perms
        				Shop tempShop = RealShopping.shopMap.get(shop);
        				if(args[0].equalsIgnoreCase("add")){
        					try {
        						int i = Integer.parseInt(args[ii].split(":")[0]);
        						int jj = 1;//First argument after item
        						int d = -1;
        						if(args[ii].split(":").length == 3 || args[ii].split(":").length == 5 ) {
        							d = Integer.parseInt(args[ii].split(":")[1]);
        							jj = 2;
        						}
        						int price = (int) (Float.valueOf(args[ii].split(":")[jj])*100);
        						Price p;
        						if(d == -1) p = new Price(i);
        						else p = new Price(i, d);
        						tempShop.setPrice(p, price);
        						sender.sendMessage(ChatColor.GREEN + LangPack.PRICEFOR + Material.getMaterial(i) + (d>-1?"("+d+") ":"") + LangPack.SETTO + price/100f + RealShopping.unit);
        						if(args[ii].split(":").length > 3){//Also set min max
        							tempShop.setMinMax(p, (int)(Float.parseFloat(args[ii].split(":")[jj+1])*100)
        									,(int)(Float.parseFloat(args[ii].split(":")[jj+2])*100));
                					sender.sendMessage(ChatColor.GREEN + LangPack.SETMINIMALANDMAXIMALPRICESFOR + Material.getMaterial(i));
        						}
        						return true;
        					} catch (NumberFormatException e) {
        						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + RealShopping.unit);
        					} catch (ArrayIndexOutOfBoundsException e){
        						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_FOLLOWEDBYTHEPRICE_ + RealShopping.unit);
        					}
        				} else if(args[0].equalsIgnoreCase("del")){
        					try {
        						Price tempP = new Price(args[ii]);
        						if(tempShop.hasPrice(tempP)){
        							tempShop.removePrice(tempP);
        							sender.sendMessage(ChatColor.RED + LangPack.REMOVEDPRICEFOR + Material.getMaterial(tempP.getType()) + (tempP.getData()>-1?"("+tempP.getData()+") ":""));
        							return true;
        						} else {
       								sender.sendMessage(ChatColor.RED + LangPack.COULDNTFINDPRICEFOR + Material.getMaterial(tempP.getType()) + (tempP.getData()>-1?"("+tempP.getData()+") ":""));
       							}
        					} catch (NumberFormatException e) {
        						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_);
        					}
        				} else if(args[0].equalsIgnoreCase("copy")){
        					try {
        						if((args.length == 3 && shop.equals(args[1])) || (args.length == 2 && !shop.equals(args[1]))){//If copy from store
        							if(RealShopping.shopMap.containsKey(args[args.length - 1])){
        								tempShop.clonePrices(args[args.length - 1]);
        								sender.sendMessage(ChatColor.GREEN + LangPack.OLDPRICESREPLACEDWITHPRICESFROM + args[args.length - 1]);
        								return true;
        							}
        						} else {
        							tempShop.clonePrices(null);
    								sender.sendMessage(ChatColor.GREEN + LangPack.OLDPRICESREPLACEDWITHTHELOWEST_);
    								return true;
        						}
        					} catch (NumberFormatException e) {
        						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPER_);
        					}
        				} else if(args[0].equalsIgnoreCase("clear")){
        					tempShop.clearPrices();
        					sender.sendMessage(ChatColor.GREEN + LangPack.CLEAREDALLPRICESFOR + shop);
        					return true;
        				} else if(args[0].equalsIgnoreCase("defaults")){
        					if(RealShopping.defPrices != null && !RealShopping.defPrices.isEmpty()){
            					tempShop.setPrices(RealShopping.defPrices);
            					sender.sendMessage(ChatColor.GREEN + LangPack.SETDEFAULTPRICESFOR + shop);
        						return true;
        					} else sender.sendMessage(ChatColor.RED + LangPack.THEREARENODEFAULTPRICES);
        				} else if(args[0].equalsIgnoreCase("showminmax")){
        					int item = Integer.parseInt(args[ii]);
        					Price p = new Price(item);
        					if(tempShop.hasMinMax(p)){
        						sender.sendMessage(ChatColor.GREEN + LangPack.STORE + shop + LangPack.HASAMINIMALPRICEOF + tempShop.getMin(p)/100f + LangPack.UNIT
        							+ LangPack.ANDAMAXIMALPRICEOF + tempShop.getMax(p)/100f + LangPack.UNIT + LangPack.FOR + Material.getMaterial(item));
        					} else sender.sendMessage(ChatColor.GREEN + LangPack.STORE + shop + LangPack.DOESNTHAVEAMINIMALANDMAXIMALPRICEFOR + Material.getMaterial(item));
        					return true;
        				} else if(args[0].equalsIgnoreCase("clearminmax")){
        					int item = Integer.parseInt(args[ii]);
        					if(tempShop.hasMinMax(new Price(item))){
        						tempShop.clearMinMax(new Price(item));
        						sender.sendMessage(ChatColor.GREEN + LangPack.CLEAREDMINIMALANDMAXIMALPRICESFOR + Material.getMaterial(item));
        					} else sender.sendMessage(ChatColor.GREEN + LangPack.STORE + shop + LangPack.DIDNTHAVEAMINIMALANDMAXIMALPRICEFOR + Material.getMaterial(item));
        					return true;
        				} else if(args[0].equalsIgnoreCase("setminmax")){
        					try {
        						String[] s = args[ii].split(":");
        						if(s.length == 3){
                					int item = Integer.parseInt(s[0]);
                					tempShop.setMinMax(new Price(item), (int)(Float.valueOf(s[1])*100), (int)(Float.valueOf(s[2])*100));
                					sender.sendMessage(ChatColor.GREEN + LangPack.SETMINIMALANDMAXIMALPRICESFOR + Material.getMaterial(item));
                					return true;
        						} else sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPERARGUMENT);
        					} catch (NumberFormatException e) {
        						sender.sendMessage(ChatColor.RED + args[ii] + LangPack.ISNOTAPROPERARGUMENT);
        					}
        				}
        				
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
