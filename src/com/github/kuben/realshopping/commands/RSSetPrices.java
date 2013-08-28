package com.github.kuben.realshopping.commands;

import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;


class RSSetPrices extends RSCommand {
    private String arg = "";
    private String description = "";
    private int amount = 1;
    private String store = "";
    private Shop shop = null;

    public RSSetPrices(CommandSender sender, String[] args) {
        super(sender, args);
    }

    private boolean add(){
        try {
            Object[] o = RSUtils.pullPriceCostMinMax(arg,this.player);
            if(o == null || o.length < 2) return false;
            Price p = (Price)o[0];
            Integer[] i = (Integer[])o[1];
            if(i[0] < 0 ) return false;
            p.setDescription(this.description);
            p.setAmount(amount);
            String name = p.formattedString();
            shop.setPrice(p, i[0]);
            sender.sendMessage(ChatColor.GREEN + LangPack.PRICEFOR + name + LangPack.SETTO + i[0]/100f + LangPack.UNIT);
            if(i.length > 1){//Also set min max
                shop.setMinMax(p, i[1], i[2]);
                sender.sendMessage(ChatColor.GREEN + LangPack.SETMINIMALANDMAXIMALPRICESFOR + name);
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
            Price p = RSUtils.pullPrice(arg,this.player);
            String dString = p.getData()>-1?"("+p.getData()+")":"";
            if(shop.hasPrice(p)){
                shop.removePrice(p);
                sender.sendMessage(ChatColor.RED + LangPack.REMOVEDPRICEFOR + p.formattedString() + dString);
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
                if(RealShopping.shopExists(args[args.length - 1])){
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
        Price p = RSUtils.pullPrice(arg,this.player);
        String name = p.formattedString();
        if(shop.hasMinMax(p)){
            sender.sendMessage(ChatColor.GREEN + LangPack.STORE + store + LangPack.HASAMINIMALPRICEOF + shop.getMin(p)/100f + LangPack.UNIT
                    + LangPack.ANDAMAXIMALPRICEOF + shop.getMax(p)/100f + LangPack.UNIT + LangPack.FOR + name);
        } else sender.sendMessage(ChatColor.GREEN + LangPack.STORE + store + LangPack.DOESNTHAVEAMINIMALANDMAXIMALPRICEFOR + name);
        return true;
    }

    private boolean clearMinMax(){
        Price p = RSUtils.pullPrice(arg,this.player);
        String name = p.formattedString();
        if(shop.hasMinMax(p)){
            shop.clearMinMax(p);
            sender.sendMessage(ChatColor.GREEN + LangPack.CLEAREDMINIMALANDMAXIMALPRICESFOR + name);
        } else sender.sendMessage(ChatColor.GREEN + LangPack.STORE + store + LangPack.DIDNTHAVEAMINIMALANDMAXIMALPRICEFOR + name);
        return true;
    }

    private boolean setMinMax(){
        try {
            Object[] o = RSUtils.pullPriceMinMax(arg,this.player);
            Price p = (Price)o[0];
            Integer[] i = (Integer[])o[1];
            shop.setMinMax(p, i[0], i[1]);
            String name = p.formattedString();
            sender.sendMessage(ChatColor.GREEN + LangPack.SETMINIMALANDMAXIMALPRICESFOR + name);
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
            int startargs = 1;
            //preliminary control of arguments. We must say if setprices contains the store argument.
            // and the command will be in the form STORE ARGS, where args can be colon separated or single words.
            // we need to know where to pick args if the store is specified.
            if(argsContainStore(args)){
                store = args[0];
                startargs = 2;
            }
            else if(isPlayer) {
                store = RealShopping.getPInv(player).getShop().getName();
            } else {
                store = "";
            }

            if(!store.equals("") && RealShopping.shopExists(store)){
                shop = RealShopping.getShop(store);
                arg = args[startargs];
                //This trick will avoid the use of a second switch case
                if(shop.getOwner().equals("@admin") && (!isPlayer || player.hasPermission("realshopping.rsset")) || shop.getOwner().equals(player.getName())){
                    switch(args[startargs-1].toLowerCase()){
                        case "add":
                            if(startargs < args.length-1){
                                if(args[startargs+1].equals("bulk")) {
                                    startargs +=1;
                                    amount = player.getItemInHand().getAmount();
                                    if(startargs+1 <= args.length-1) {
                                        startargs +=1;
                                        try {
                                            amount = Integer.parseInt(args[startargs]);
                                        } catch ( NumberFormatException ex) {
                                            amount = player.getItemInHand().getAmount();
                                        }
                                    } 
                                }
                                for(int i = startargs+1;i<args.length;i++){
                                    if(i != 0) this.description += " ";
                                    this.description += args[i];
                                }
                            }
                            return add();
                        case "del":
                            return del();
                        case "showminmax":
                            return showMinMax();
                        case "setminmax":
                            return setMinMax();
                        case "clearminmax":
                            return clearMinMax();
                        case "copy":
                            if(args.length > 2) return copy();
                            break;
                        case "clear":
                            return clear();
                        case "defaults":
                            return defaults();
                        default:
                            break;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + (!isPlayer?LangPack.THISCOMMANDCANNOTBEUSEDFROMCONSOLE:store + LangPack.DOESNTEXIST));
                return false;
            }
        }
        sender.sendMessage(ChatColor.RED + LangPack.YOUARENTPERMITTEDTOEMANAGETHISSTORE);
        return false;
    }

    protected Boolean help(){
        //Check if help was asked for
        if(args.length == 0 || args[0].equalsIgnoreCase("help")){
            if(args.length == 0){
                sender.sendMessage(ChatColor.DARK_GREEN + LangPack.USAGE + ChatColor.RESET + "/rssetprices add|del|defaults|copy|clear [STORE] (ITEM_ID[:DATA][:COST][:MIN:MAX])|[COPY_FROM]");
                sender.sendMessage(" OR /rssetprices showminmax|clearminmax|setminmax [STORE] [ITEM_ID[:DATA]:MIN:MAX]]");
                sender.sendMessage(LangPack.FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ + ChatColor.LIGHT_PURPLE + "/rssetprices help " + ChatColor.DARK_PURPLE + "COMMAND");
            } else if(args.length == 1){
                sender.sendMessage(LangPack.RSSETHELP + ChatColor.DARK_PURPLE + "STORE" + ChatColor.RESET + LangPack.RSSETPRICESHELP2
                                    + LangPack.YOU_CAN_GET_MORE_HELP_ABOUT_ + ChatColor.LIGHT_PURPLE + "add, del, defaults, copy, clear, showminmax, clearminmax, setminmax");
            } else {
                switch(args[1].toLowerCase()){
                    case "add":
                        sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "add [STORE] ITEM_ID[:DATA]:COST[:MIN:MAX]"
                                    + ChatColor.RESET + LangPack.RSSETPRICESADDHELP + ChatColor.DARK_PURPLE + "COST" + ChatColor.RESET + LangPack.RSSETPRICESADDHELP2
                                    + ChatColor.DARK_PURPLE + "MAX" + ChatColor.RESET + LangPack.AND_ + ChatColor.DARK_PURPLE + LangPack.ARGUMENTS);
                        break;
                    case "del":
                        sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "del [STORE] ITEM_ID[:DATA]"
                                    + ChatColor.RESET + LangPack.RSSETPRICESDELHELP);
                        break;
                    case "defaults":
                        sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "defaults [STORE]"
                                    + ChatColor.RESET + LangPack.RSSETPRICESDEFAUTLSHELP + ChatColor.LIGHT_PURPLE + "/rsimport");
                        break;
                    case "copy":
                        sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "copy [STORE] [COPY_FROM]"
                                    + ChatColor.RESET + LangPack.RSSETPRICESCOPYHELP + ChatColor.DARK_PURPLE + "COPY_FROM" + ChatColor.RESET + LangPack.RSSETPRICESCOPYHELP2
                                    + ChatColor.DARK_PURPLE + "COPY_FROM" + ChatColor.RESET + LangPack.RSSETPRICESCOPYHELP3);
                        break;
                    case "clear":
                        sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "clear [STORE]"
                                    + ChatColor.RESET + LangPack.RSSETPRICESCLEARHELP);
                        break;
                    case "showminmax":
                        sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "showminmax [STORE] ITEM_ID"
                                    + ChatColor.RESET + LangPack.RSSETPRICESSHOWMMHELP);
                        break;
                    case "clearminmax":
                        sender.sendMessage(LangPack.USAGE +ChatColor.LIGHT_PURPLE + "clearminmax [STORE] ITEM_ID"
                                    + ChatColor.RESET + LangPack.RSSETPRICESCLEARMMHELP);
                        break;
                    case "setminmax":
                        sender.sendMessage(LangPack.USAGE + ChatColor.LIGHT_PURPLE + "setminmax [STORE] ITEM_ID:MIN:MAX"
                                    + ChatColor.RESET + LangPack.RSSETPRICESSETMMHELP);
                    default:
                        break;
                }
            }
            return true;
        }
        return null;
    }

    private boolean argsContainStore(String[] args) {
        //simply, if there are more than 2 args and the first is not a command, then I think is a store.
        if(args.length > 2){ // Maybe
            return !(args[0].equals("add") 
                    || args[0].equals("del") 
                    || args[0].contains("setminmax") 
                    || args[0].contains("showminmax") 
                    || args[0].contains("clearminmax")
                    || args[0].contains("copy")
                    || args[0].contains("clear")
                    || args[0].contains("defaults"));
        }
        return false;
    }
	
}
