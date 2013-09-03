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

    private final ChatColor LP = ChatColor.LIGHT_PURPLE;
    private final ChatColor DP = ChatColor.DARK_PURPLE;
    private final ChatColor GR = ChatColor.GREEN;
    private final ChatColor DG = ChatColor.DARK_GREEN;
    private final ChatColor RD = ChatColor.RED;
    private final ChatColor DR = ChatColor.DARK_RED;
    private final ChatColor RESET = ChatColor.RESET;
    
    private final String MORE_HELP = LP + "add"
            + RESET + ", " + LP + "del"
            + RESET + ", " + LP + "defaults"
            + RESET + ", " + LP + "copy"
            + RESET + ", " + LP + "clear"
            + RESET + ", " + LP + "showminmax"
            + RESET + ", " + LP + "clearminmax"
            + RESET + ", " + LP + "setminmax";
    
    private String[] comArgs;//An array with all the arguments to the command, that is without the keyword and the store. 
    private Shop shop = null;
    
    public RSSetPrices(CommandSender sender, String[] args) {
        super(sender, args);
    }

    private boolean add(){
        Price itmprice = null;
        Integer[] priminmax = null;
        int amount = 1;
        boolean handheld = comArgs[0].split(":")[0].equals("hand"); //This is horrible, sorry.
        String description = "";
        
        switch(comArgs.length) {
            default: // More than 1 argument: can be -b, description.
                if(comArgs.length > 1) {
                    int strt = 1;
                    if(comArgs[1].equals("-b")) {
                        try {
                            amount = Integer.parseInt(comArgs[2]);
                            strt = 3;
                        } catch(Exception ex) {
                            if(handheld) {
                                amount = player.getItemInHand().getAmount();
                                strt = 2;
                            }
                            else {
                                sender.sendMessage(RD + "You must provide a stack number if the item is not handheld.");
                                return false;
                            }
                        }
                    }
                    for(int j = strt;j<comArgs.length;j++) {
                        description += comArgs[j] + (comArgs.length - j == 1?"":" ");
                    }
                }
            case 1:
                Object[] o = RSUtils.pullPriceCostMinMax(comArgs[0],this.player);
                if(o == null || o.length < 2) return false;
                itmprice = (Price)o[0];
                priminmax = (Integer[])o[1];
                if(priminmax[0] < 0) return false;
            case 0:
                break;
        }
        if(itmprice == null || priminmax == null) return false;
        itmprice.setDescription(description);
        itmprice.setAmount(amount);
        shop.setPrice(itmprice, priminmax[0]);
        sender.sendMessage(GR + LangPack.PRICEFOR + itmprice.formattedString() + LangPack.SETTO + priminmax[0]/100f + LangPack.UNIT);
        if(priminmax.length > 1) {
            shop.setMinMax(itmprice, priminmax[1], priminmax[2]);
            sender.sendMessage(GR + LangPack.SETMINIMALANDMAXIMALPRICESFOR + itmprice.formattedString());
        }
        return true;
    }

    private boolean del(){
        
        try {
            Price p = RSUtils.pullPrice(comArgs[0],this.player);
            String dString = p.getData()>-1?"("+p.getData()+")":"";
            if(shop.hasPrice(p)){
                shop.removePrice(p);
                sender.sendMessage(RD + LangPack.REMOVEDPRICEFOR + DR + p.formattedString() + dString);
                    return true;
            } else {
                sender.sendMessage(RD + LangPack.COULDNTFINDPRICEFOR + DR + Material.getMaterial(p.getType()) + dString);
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(DR + comArgs[0] + RD + LangPack.ISNOTAPROPER_);
        }
        return false;
    }

    private boolean copy(){
        if(comArgs.length > 0){//If copy from store
            if(RealShopping.shopExists(comArgs[0])){
                shop.clonePrices(comArgs[0]);
                sender.sendMessage(GR + LangPack.OLDPRICESREPLACEDWITHPRICESFROM + DG + comArgs[0]);
                return true;
            }
        } else {
            shop.clonePrices(null);
            sender.sendMessage(GR + LangPack.OLDPRICESREPLACEDWITHTHELOWEST_);
            return true;
        }
        return false;
    }

    private boolean clear(){
        shop.clearPrices();
        sender.sendMessage(GR + LangPack.CLEAREDALLPRICESFOR + DG + shop.getName() + GR + ".");
        return true;
    }

    private boolean defaults(){
        if(RealShopping.hasDefPrices()){
            shop.setPrices(RealShopping.getDefPrices());
            sender.sendMessage(GR + LangPack.SETDEFAULTPRICESFOR + DG + shop.getName() + GR + ".");
            return true;
        } else sender.sendMessage(RD + LangPack.THEREARENODEFAULTPRICES);
        return false;
    }

    private boolean showMinMax(){
        Price p = RSUtils.pullPrice(comArgs[0],this.player);
        String name = p.formattedString();
        if(shop.hasMinMax(p)){
            sender.sendMessage(GR + LangPack.STORE + DG + shop.getName() + GR + LangPack.HASAMINIMALPRICEOF + DG + shop.getMin(p)/100f + GR + LangPack.UNIT
                    + LangPack.ANDAMAXIMALPRICEOF + DG + shop.getMax(p)/100f + GR + LangPack.UNIT+ LangPack.FOR + ChatColor.BLUE + name);
        } else sender.sendMessage(GR + LangPack.STORE + DG + shop.getName() + GR + LangPack.DOESNTHAVEAMINIMALANDMAXIMALPRICEFOR + ChatColor.BLUE + name);
        return true;
    }

    private boolean clearMinMax(){
        Price p = RSUtils.pullPrice(comArgs[0],this.player);
        String name = p.formattedString();
        if(shop.hasMinMax(p)){
            shop.clearMinMax(p);
            sender.sendMessage(GR + LangPack.CLEAREDMINIMALANDMAXIMALPRICESFOR + ChatColor.BLUE + name);
        } else sender.sendMessage(GR + LangPack.STORE + DG + shop.getName() + GR + LangPack.DIDNTHAVEAMINIMALANDMAXIMALPRICEFOR + ChatColor.BLUE + name);
        return true;
    }

    private boolean setMinMax(){
        try {
            Object[] o = RSUtils.pullPriceMinMax(comArgs[0],this.player);
            Price p = (Price)o[0];
            Integer[] i = (Integer[])o[1];
            shop.setMinMax(p, i[0], i[1]);
            String name = p.formattedString();
            sender.sendMessage(GR + LangPack.SETMINIMALANDMAXIMALPRICESFOR + ChatColor.BLUE + name);
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage(DR + comArgs[0] + RD + LangPack.ISNOTAPROPERARGUMENT);
        }
        return false;
    }

    @Override
    protected boolean execute() {
        String com;
        String store = null;

        //Syntax check
        switch(args[0].toLowerCase()){
            case "add": case "del": case "copy": case "clear": case "defaults": case "showminmax": case "clearminmax": case "setminmax":
                //If the first argument is the command
                com = args[0];
                comArgs = cutBeginning(args, 1);//The first argument to the command is args[1]
                break;
            default:
                switch(args[1].toLowerCase()){
                    case "add": case "del": case "copy": case "clear": case "defaults": case "showminmax": case "clearminmax": case "setminmax":
                        //If the second argument is the command
                        store = args[0];//The first argument has to be the store                            
                        com = args[1];
                        comArgs = cutBeginning(args, 2);//The first argument to the command is args[2]
                        break;
                    default:
                        //Wrong syntax
                        return false;
                }
        }

        //Now set the shop variable
        if(store != null){//Store was specified
            if(RealShopping.shopExists(store))//All clear
                shop = RealShopping.getShop(store);
            else {
                sender.sendMessage(RD + LangPack.STORE + DR + store + RD + LangPack.DOESNTEXIST);
                return true;
            }
        } else {//Use the store which the player is in
            if(player == null){
                sender.sendMessage(RD + LangPack.YOUHAVETOUSETHESTOREARGUMENTWHENEXECUTINGTHISCOMMANDFROMCONSOLE);
                return false;
            }
            if(!RealShopping.hasPInv(player)){
                sender.sendMessage(RD + LangPack.YOUHAVETOBEINASTOREIFNOTUSINGTHESTOREARGUMENT);
                return false;
            }
            shop = RealShopping.getPInv(player).getShop();
        }
            
        //Check for permission. This also means that players with rsset permission can manage ANY store
        if((!shop.getOwner().equals(player.getName()) && !player.hasPermission("realshopping.rsset"))){
            sender.sendMessage(RD + LangPack.YOUARENTPERMITTEDTOEMANAGETHISSTORE);
            return false;
        }
        
        //Call the right method. The methods will have to parse the arguments correctly.
        //Since the shop is already set, the methods should use comArgs[] instead of args[].
        switch(com.toLowerCase()){
            case "add":
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
                return copy();
            case "clear":
                return clear();
            case "defaults":
                return defaults();
            default:
                break;
        }
        return false;
    }
@Override
    protected Boolean help(){
        //Check if help was asked for
        if(args.length == 0 || args[0].equalsIgnoreCase("help")){
            if(args.length == 0){
                sender.sendMessage(DG + LangPack.USAGE + RESET + "/rssetprices [STORE] COMMAND [ARGS..]");
                sender.sendMessage("Avaiable commands are: " + MORE_HELP);//LANG
                sender.sendMessage(LangPack.FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ + LP + "/rssetprices help " + DP + "COMMAND");
            } else if(args.length == 1){
                sender.sendMessage(LangPack.RSSETPRICESHELP + DP + "STORE" + RESET + LangPack.RSSETPRICESHELP2
                                    + LangPack.YOU_CAN_GET_MORE_HELP_ABOUT_ + LP + MORE_HELP);
            } else {
                switch(args[1].toLowerCase()){
                    case "add":
                        sender.sendMessage(LangPack.USAGE + LP + "add " + DP + "ITEM_ID" + LP + "[:" + DP + "DATA" + LP + "]:" + DP + "COST"
                                    + LP + "[:" + DP + "MIN" + LP + ":" + DP + "MAX" + LP +"]"
                                    + RESET + LangPack.RSSETPRICESADDHELP + DP + "COST" + RESET + LangPack.RSSETPRICESADDHELP2
                                    + DP + "MAX" + RESET + LangPack.AND_ + DP + "MIN" + RESET + LangPack.ARGUMENTS);
                        break;
                    case "del":
                        sender.sendMessage(LangPack.USAGE + LP + "del " + DP + "ITEM_ID" + LP + "[:" + DP + "DATA" + LP + "]" + RESET + LangPack.RSSETPRICESDELHELP);
                        break;
                    case "defaults":
                        sender.sendMessage(LangPack.USAGE + LP + "defaults" + RESET + LangPack.RSSETPRICESDEFAUTLSHELP + LP + "/rsimport");
                        break;
                    case "copy":
                        sender.sendMessage(LangPack.USAGE + LP + "copy [" + DP + "COPY_FROM" + LP + "]"
                                    + RESET + LangPack.RSSETPRICESCOPYHELP + DP + "COPY_FROM" + RESET + LangPack.RSSETPRICESCOPYHELP2
                                    + DP + "COPY_FROM" + RESET + LangPack.RSSETPRICESCOPYHELP3);
                        break;
                    case "clear":
                        sender.sendMessage(LangPack.USAGE + LP + "clear" + RESET + LangPack.RSSETPRICESCLEARHELP);
                        break;
                    case "showminmax":
                        sender.sendMessage(LangPack.USAGE + LP + "showminmax " + DP + "ITEM_ID" + RESET + LangPack.RSSETPRICESSHOWMMHELP);
                        break;
                    case "clearminmax":
                        sender.sendMessage(LangPack.USAGE +LP + "clearminmax " + DP + "ITEM_ID" + RESET + LangPack.RSSETPRICESCLEARMMHELP);
                        break;
                    case "setminmax":
                        sender.sendMessage(LangPack.USAGE + LP + "setminmax " + DP + "ITEM_ID" + LP + ":" + DP + "MIN" + LP + ":" + DP + "MAX" + RESET + LangPack.RSSETPRICESSETMMHELP);
                    default:
                        break;
                }
            }
            return true;
        }
        return null;
    }

    /**
     * Used to isolate the latter part of a array. More specifically the arguments array.
     * @param orig The original array
     * @param shift How many places the values should be shifted forwards.
     * @return A new, cut array.
     */
    private String[] cutBeginning(String[] orig, int shift){
        String[] res = new String[orig.length - shift];
        for(int i = 0;i < orig.length - shift;i++)
            res[i] = orig[i + shift];
        return res;
    }
}
