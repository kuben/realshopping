package com.github.kuben.realshopping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class RSUtils {

    private RSUtils(){}//All static

    /*
     * 
     * Utils and formatting functions
     * 
     */
    /**
     * Parses a string and checks for correct use of aliases.
     * If the alias is not found this will throw a Nullpointer exception.
     * The constructed Object is an Object array.
     * @param str
     * @return
     * @throws NullPointerException 
     */
    private static Object[] parseAliases(String str) throws NullPointerException {
        String[] s = str.split(":");
        if(!RealShopping.getAliasesMap().containsKey(s[0])) throw new NullPointerException("No item with this name found.");
        Object[] almap = RealShopping.getAliasesMap().get(s[0]);
        Price p;
        if(almap == null) return new Object[]{null,s[1]};
        if(almap.length>1) 
            p = new Price((Material)almap[0],(MaterialData)almap[1]);
        else {
            p = new Price((Material)almap[0]);
        }
        List<Object> o = new LinkedList<>();
        o.add(p);
        o.add(almap[almap.length -1]);
        return o.toArray(new Object[0]);
    }

    /**
     * Accepts and parses item descriptors (for chests),
     * supports aliases, data values, stack sizes and multiple items.
     * @param str string to parse.
     * @return an array the same size as the amount items (max. 27)
     */
    public static Object[][] pullItems(String str) throws NumberFormatException {
        String[] strs = new String[27];
        int l = 0;
        for(String s:str.split(",")){//Repeat for every item
            int m = 1, i = 0;//m is multiplier, i is how many times the loop has multiplied
            if(s.contains("*")) m = Integer.parseInt(s.split("\\*")[1]);
            while(i < m && l < 27){//Add as many times as requested
                strs[l] = s.split("\\*")[0];
                l++;
                i++;
            }
            if(i >= 27) break;
        }
        Object[][] ids = new Object[l][2];
        for(int i = 0;i < l;i++){
            Object[] id = pullItem(strs[i]);
            ids[i][0] = (Price)id[0];
            if(id.length > 1) ids[i][1] = (Integer)id[1];
            else ids[i][1] = 0; //fullstack
        }
        return ids;
    }
    
    /**
     * Accepts and parses any single item descriptor
     * Supports aliases, data values and stack sizes.
     * Does not parse mulitple items ( , ) or ( * )
     * @param str String to parse.
     * @return An array of the same size as the amount of ' : '
     */
    public static Object[] pullItem(String str) throws NumberFormatException {
        Object[] parsed = null;
        try {
            parsed  = parseAliases(str);
        } catch (NullPointerException ex) {
            return null;
        }
        if(parsed == null) return null;
        for(int i = 1;i<parsed.length;i++) parsed[i] = Integer.parseInt((String)parsed[i]);
        return parsed;
    }

    /**
     * Builds a Price object from a String.
     * @param str String to read
     * @param ply player that called this method
     * @return The correct price for this string. Null if there were problems.
     */
    public static Price pullPrice(String str, Player ply){//No commas on this one
        Object[] parsed = parseAliases(str);
        
        if(parsed != null) {
            Material id = (Material) parsed[0];
            MaterialData data = null;
            if(parsed.length > 1) data = (MaterialData) parsed[1];
            return new Price(id,data);
        } else {
            return new Price(ply.getItemInHand());
        }
    }

    /**
     * Builds an object[] containing Price, cost and eventual minmax.
     * Its structure is o[0] = Price, o[1] = Integer[].
     * The Integer[] contains cost in [0] and, if present, min and max in [1] and [2].
     * 
     * @param str String to parse.
     * @param ply Player that called this method.
     * @return Array object with Price and Integer[] of costs, min and max.
     */
    public static Object[] pullPriceCostMinMax(String str, Player ply){//No commas on this one
        Object[] parsed = parseAliases(str);
        if(parsed[0] == null) parsed[0] = new Price(ply.getItemInHand());
        Integer[] costs = {-1,null,null};
        for(int i=1;i<parsed.length;i++){
            if(i<costs.length || i >= costs.length) break;
            costs[i-1] = Integer.parseInt((String)parsed[i]);
        }
        return new Object[]{parsed[0], costs};
    }

    /**
     * Returns an object containing Price and minmax.
     * The structure is o[0] = Price, o[1] = Integer[] minmax
     * @param str String to parse.
     * @param ply Player that called this method.
     * @return An object[] containing [0] = Price and [1] = Integer[] min and max vals.
     */
    public static Object[] pullPriceMinMax(String str, Player ply){//No commas on this one
            return pullPriceCostMinMax(str, ply);
    }

    public static String formatItemStackToMess(ItemStack[] IS){
            String str = "";
            int newLn = 0;
            for(ItemStack iS:IS){
                    if(iS != null){
                            String tempStr = "[" + ChatColor.RED + iS.getType() + (RealShopping.isTool(iS.getType())
                                                            ?ChatColor.RESET + LangPack.WITH + ChatColor.GREEN + (RealShopping.getMaxDur(iS.getType()) - iS.getDurability())
                                                            + ChatColor.RESET +  "/" + ChatColor.GREEN + RealShopping.getMaxDur(iS.getType()) + ChatColor.RESET + LangPack.USESLEFT + "] "
                                                            :ChatColor.RESET + " * " + ChatColor.GREEN + iS.getAmount() + ChatColor.RESET + "] " + ChatColor.BLACK + ChatColor.RESET);
                            if((str + tempStr).substring(newLn).length() > 96){//84+12
                                    str += "\n";
                                    newLn = str.length();
                                    str += tempStr;
                            } else str += tempStr;
                    }
            }
            return str;
    }

    public static String formatPlayerListToMess(String[] formStr){
            String str = "";
            int newLn = 0;
            for(String s:formStr){
                    boolean online = (Bukkit.getServer().getPlayerExact(s)==null)?false:true;
                    String tempStr = "[" + (online?ChatColor.GREEN:ChatColor.RESET) + s + ChatColor.RESET + "] ";
                    if((str + tempStr).substring(newLn).length() > 88){//84+4
                            if(str.split("\n").length < 7){
                                    str += "\n";
                                    newLn = str.length();
                                    str += tempStr;
                            } else {
                                    str += "...";
                            }
                    } else str += tempStr;
            }
            return str;
    }

    public static String locAsString(Location l){
            return l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
    }

    public static Location stringToLoc(String world, String s){
            return new Location(Bukkit.getServer().getWorld(world), Double.parseDouble(s.split(",")[0].trim()), Double.parseDouble(s.split(",")[1].trim()), Double.parseDouble(s.split(",")[2].trim()));
    }

    /**
     * Formats an integer representing any amount of seconds to a string in the format of "Xd Yh Wm Us"
     * where X is the number of days, Y number of hours, W number of minutes and U number of seconds.
     * If any of those is 0, it is not shown.
     * @param period How many seconds
     * @return a simple representation of the time.
     */
    public static String secsToDHMS(int period){
        int remainder = period;
        String STRING = "";
        if(remainder >= 86_400){//How many days
            int days = (int) Math.floor(remainder / 86_400);
            remainder = remainder % 86_400;
            STRING += days + "d ";
        }
        if(remainder >= 3600){//How many hours
            int hrs = (int) Math.floor(remainder / 3600);
            remainder = remainder % 3600;
            STRING += hrs + "h ";
        }
        if(remainder >= 60){//How many minutes
            int mins = (int) Math.floor(remainder / 60);
            remainder = remainder % 60;
            STRING += mins + "m ";
        }
        if(remainder > 0) STRING += remainder + "s";
        return STRING;
    }
    
    /*
     * 
     * Booleans and gets
     * 
     */
/**
 * Checks if a location is allowed for teleporting while in a store.
 * @param l Location we want to teleport to.
 * @return true if location is not prohibited, false otherwise.
 */
    public static boolean allowTpOutOfStore(Location l){
            Location[] keys = RealShopping.getTpLocsKeysArray();
            boolean allow = RealShopping.isTpLocBlacklist();
            for(Location loc:keys){
                if(l.getWorld() == loc.getWorld()) {
                    int xDif = l.getBlockX() - loc.getBlockX();
                    int yDif = l.getBlockY() - loc.getBlockY();
                    int zDif = l.getBlockZ() - loc.getBlockZ();
                    double temp = Math.sqrt(Math.pow(Math.max(xDif, xDif * -1), 2) + Math.pow(Math.max(yDif, yDif * -1), 2) + Math.pow(Math.max(zDif, zDif * -1), 2));
                    if(temp <= RealShopping.getTpLoc(loc)){//Is in zone
                            allow = !allow;
                            break;
                    }
                } else allow = false;
            }
            return allow;
    }

    public static boolean isChestProtected(Location l){
            for(Shop temp:RealShopping.getShops()){
                    if(temp.isProtectedChest(l)) return true;
            }
            return false;
    }

    public static Location[] getNearestTpLocs(Location loc, int maxAmount){
            Location[] keys = RealShopping.getTpLocsKeysArray();
            Location[] nearest = null;
            Map<Location, Double> locDist = new HashMap<>();
            for(Location l:keys)
                if(loc.getWorld().equals(l.getWorld()))
                    locDist.put(l, loc.distance(l));

            if(!locDist.isEmpty()){
                ValueComparator bvc =  new ValueComparator(locDist);
                TreeMap<Location,Double> sorted_map = new TreeMap<>(bvc);
                sorted_map.putAll(locDist);


                if(sorted_map.size() < maxAmount) nearest = new Location[sorted_map.size()];
                else nearest = new Location[maxAmount];

                keys = sorted_map.keySet().toArray(new Location[0]);
                System.arraycopy(keys, 0, nearest, 0, nearest.length);
            }

            return nearest;
    }

    /*
     * 
     * Cart-related functions
     * 
     */

    public static Shop[] getOwnedShops(String player){
        List<Shop> shopList = new ArrayList<>();
        for(Shop shop:RealShopping.getShops())
            if(shop.getOwner().toLowerCase().equals(player.toLowerCase())) shopList.add(shop);
        return shopList.toArray(new Shop[0]);
    }
    
    /**
     * We check for minecarts near the pay block.
     * @param l Location where to check for shopping carts.
     * @return An array containing all storage carts found.
     */
    public static StorageMinecart[] checkForCarts(Location l){
        Block[] b = new Block[]{  
                                  l.clone().subtract(0, 1, 0).getBlock()
                                , l.clone().subtract(-1, 1, 0).getBlock()
                                , l.clone().subtract(-1, 1, -1).getBlock()
                                , l.clone().subtract(0, 1, -1).getBlock()
                                , l.clone().subtract(1, 1, -1).getBlock()
                                , l.clone().subtract(1, 1, 0).getBlock()
                                , l.clone().subtract(1, 1, 1).getBlock()
                                , l.clone().subtract(0, 1, 1).getBlock()
                                , l.clone().subtract(-1, 1, 1).getBlock()
        };
        List<StorageMinecart> retval = new ArrayList<>();
        if(Config.isAllowFillChests() && Config.isCartEnabledW(l.getWorld().getName())){
            List<StorageMinecart> mcArr = new LinkedList<>(l.getWorld().getEntitiesByClass(StorageMinecart.class));
            for (Block b1 : b) {
                if (b1.getType() == Material.RAILS || b1.getType() == Material.POWERED_RAIL || b1.getType() == Material.DETECTOR_RAIL) {
                    Location blkLoc = b1.getLocation();
                    for(StorageMinecart mcart:mcArr) {
                        if(mcart.getLocation().getBlock().getLocation().equals(blkLoc)) {
                            retval.add(mcart);
                        }
                    }
                }
            }
        }
        return retval.toArray(new StorageMinecart[0]);
    }

    public static boolean shipCartContents(StorageMinecart sM, Player p) {
            List<ItemStack> cartInv = new LinkedList<>();
            for(ItemStack i:sM.getInventory().getContents()){
                if(i!= null) cartInv.add(i);
            }

            RSPlayerInventory pinv = RealShopping.getPInv(p);
            Shop tempshop = pinv.getShop();
            Map<Price, Integer> bought = pinv.getBought();
            //The shipment is invalid when cart is empty.
            if(cartInv.isEmpty() || bought.isEmpty()){
                p.sendMessage(ChatColor.RED + LangPack.YOUCANTSHIPANEMPTYCART);
                return true;
            }
            // I take note on every item bought from the player.
            if(!bought.isEmpty()){
                List<ItemStack> rem = new ArrayList<>();
                for(ItemStack i:cartInv){
                    if(i == null) continue;
                    Price x = new Price(i);
                    if(tempshop.hasPrice(x) && bought.containsKey(x)){ //Player owns this item (bought)
                        int amount = RealShopping.isTool(i.getType())?1:i.getAmount();
                        ItemStack r = i.clone();
                        if(amount > bought.get(x)){
                            r.setAmount(bought.get(x));
                        } else {
                            r.setAmount(amount);
                        }
                        rem.add(r);
                        pinv.delBought(x,amount);
                    }
                }
                cartInv = updateCartInv(cartInv,rem);

                sM.getInventory().setContents(cartInv.toArray(new ItemStack[0]));

                //Ship
                RealShopping.addShippedToCollect(p.getName(), new ShippedPackage(rem.toArray(new ItemStack[0]), 0, sM.getLocation()));
                p.sendMessage(ChatColor.GREEN + LangPack.PACKAGEWAITINGTOBEDELIVERED);

                //Update bought inventory
                for(Price is:bought.keySet()){
                    pinv.delBought(is, bought.get(is));
                }

            } else p.sendMessage(ChatColor.RED + LangPack.YOUHAVENTBOUGHTANYTHING);

            return true;
    }

    private static List<ItemStack> updateCartInv(List<ItemStack> cart,List<ItemStack> rem){
        List<ItemStack> newInv = new LinkedList<>();
 
        for(ItemStack i : cart){
            for(ItemStack f : rem){
                if(f.isSimilar(i))
                    if(f.getAmount() <= i.getAmount()){
                        i.setAmount(i.getAmount() - f.getAmount());
                        if(i.getAmount() == 0)
                            break;
                    }
            }
            if(i.getAmount() == 0) continue;
            newInv.add(i);
        }
        return newInv;
    }
    
    public static boolean collectShipped(Location l, Player p, int id) {
        if(l.getBlock().getState() instanceof Chest){
            if(!RealShopping.hasPInv(p) || RealShopping.getPInv(p).getShop().getOwner().equals(p.getName())){
                if(RealShopping.hasShippedToCollect(p.getName())){
                    if(RealShopping.getShippedToCollectAmount(p.getName()) >= id){
                        boolean cont = false;
                        int cost = 0;
                        if(Config.getZoneArray().length > 0){
                            int i = 0;
                            if(p.getLocation().getWorld().equals(RealShopping.getShippedToCollect(p.getName(), id - 1).getLocationSent().getWorld())){//Same world
                                double dist = p.getLocation().distance(RealShopping.getShippedToCollect(p.getName(), id - 1).getLocationSent());
                                while (i<Config.getZoneArray().length) {
                                    if (Config.getZoneArray().length-i > 1 && dist > Config.getZoneArray()[i].getBounds()) {
                                        i++;
                                        continue;
                                    }
                                    break;
                                }
                            } else {
                                while(i<Config.getZoneArray().length) {
                                    if(Config.getZoneArray().length-i == 1 || Config.getZoneArray()[i].getBounds() < 0){
                                        break;
                                    }
                                    i++;
                                }
                            }

                            if(Config.getZoneArray()[i].getPercent() == -1)
                                    cost = (int) (Config.getZoneArray()[i].getCost()*100);
                            else {
                                    cost = (int) (RealShopping.getShippedToCollect(p.getName(), id - 1).getCost() * Config.getZoneArray()[i].getPercent()/100f);
                            }
                            if(RSEconomy.getBalance(p.getName()) >= cost/100f) cont = true;
                        } else cont = true;

                        if(cont){
                            RSEconomy.withdraw(p.getName(), cost/100f);
                            p.sendMessage(ChatColor.GREEN + "" + cost/100f + LangPack.UNIT + LangPack.WITHDRAWNFROMYOURACCOUNT);
                            ItemStack[] contents = ((Chest)l.getBlock().getState()).getBlockInventory().getContents();
                            for(ItemStack tempIS:contents) if(tempIS != null) p.getWorld().dropItem(p.getLocation(), tempIS);

                            ((Chest)l.getBlock().getState()).getBlockInventory().setContents(RealShopping.getShippedToCollect(p.getName(), id - 1).getContents());
                            p.sendMessage(ChatColor.GREEN + LangPack.FILLEDCHESTWITH);
                            p.sendMessage(formatItemStackToMess(RealShopping.getShippedToCollect(p.getName(), id - 1).getContents()));
                            RealShopping.removeShippedToCollect(p.getName(), id - 1);
                            return true;
                        } else p.sendMessage(ChatColor.RED + LangPack.YOUCANTAFFORDTOPAYTHEDELIVERYFEEOF + cost);
                    } else p.sendMessage(ChatColor.RED + LangPack.THERESNOPACKAGEWITHTHEID + id);
                } else p.sendMessage(ChatColor.RED + LangPack.YOUHAVENTGOTANYITEMSWAITINGTOBEDELIVERED);
            } else p.sendMessage(ChatColor.RED + LangPack.YOUCANTCOLLECT_YOUDONOTOWN);
        } else p.sendMessage(ChatColor.RED + LangPack.THEBLOCKYOUSELECTEDISNTACHEST);
        return false;
    }

    /*
     * 
     * Punishment functions
     * 
     */


    public static void punish(Player p){//TODO add more
        
        p.sendMessage(ChatColor.RED + LangPack.TRYINGTOCHEATYOURWAYOUT);
        
        switch(Config.getPunishment()){
            case "hell":
                if(!Config.isKeepstolen()){
                    RSUtils.returnStolen(p);
                }
                RealShopping.removePInv(p);
                RealShopping.loginfo(p.getName() + LangPack.TRIEDTOSTEALFROMTHESTORE);
                Location dropLoc2 = Config.getDropLoc().clone().add(1, 0, 0);
                if(p.teleport(Config.getHellLoc().clone().add(0.5, 0, 0.5))){
                        RealShopping.loginfo(p.getName() + LangPack.WASTELEPORTEDTOHELL);
                        p.sendMessage(ChatColor.RED + LangPack.HAVEFUNINHELL);
                        Block block = p.getWorld().getBlockAt(Config.getDropLoc());
                        if(block.getType() != Material.CHEST) 
                            block.setType(Material.CHEST);
                        BlockState blockState = block.getState();

                        block = p.getWorld().getBlockAt(dropLoc2);
                        if(block.getType() != Material.CHEST) 
                            block.setType(Material.CHEST);

                        BlockState blockState2 = block.getState();
                        ItemStack[] pS = p.getInventory().getContents();
                        if(blockState instanceof Chest) {
                            Chest chest = (Chest)blockState;
                            ItemStack[] iS = new ItemStack[27];
                            for (int i = 0;i<17;i++) {
                                iS[i] = pS[i+9];
                            }
                            chest.getBlockInventory().clear();
                            chest.getBlockInventory().setContents(iS);
                        }
                        if(blockState2 instanceof Chest) {
                           Chest chest = (Chest)blockState;
                           ItemStack[] iS = new ItemStack[9];
                           System.arraycopy(pS, 0, iS, 0, 9);
                           chest.getBlockInventory().clear();
                           chest.getBlockInventory().setContents(iS);
                           chest.getBlockInventory().addItem(p.getInventory().getArmorContents());
                        }
                        p.getInventory().clear();
                        p.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
                } else {
                        p.getInventory().clear();
                        p.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
                }
                break;
            case "jail":
                if(!Config.isKeepstolen()){
                        RSUtils.returnStolen(p);
                }
                RealShopping.jailPlayer(p);
                RealShopping.removePInv(p);
                RealShopping.loginfo(p.getName() + LangPack.TRIEDTOSTEALFROMTHESTORE);
                if(p.teleport(Config.getJailLoc().clone().add(0.5, 0, 0.5))) {
                        p.sendMessage(LangPack.YOUWEREJAILED);
                        RealShopping.loginfo(p.getName() + LangPack.WASJAILED);
                }
                break;
            default:
                if(!Config.isKeepstolen()){
                    RSUtils.returnStolen(p);
                }
                break;
        }
    }

    public static void returnStolen(Player p){
        Map<Price, Integer> stolen = RealShopping.getPInv(p).getStolen();
        Map<Price, Integer> stolen2 = new HashMap<>(stolen);

        //Remove stolen items from players inventory
        ItemStack[][] playerInv = new ItemStack[][]{p.getInventory().getContents(), p.getInventory().getArmorContents()};
        ItemStack[][] newPlayerInv = new ItemStack[][]{new ItemStack[playerInv[0].length], new ItemStack[playerInv[1].length]};
        for(int j = 0;j < 2;j ++){
            for(int i = 0;i < playerInv[j].length;i++){
                ItemStack x = playerInv[j][i];
                if(x != null){
                    Price tempPI = new Price(x);
                    if(stolen.containsKey(tempPI)){//Has stolen item
                        int diff = stolen.get(tempPI) - (RealShopping.isTool(x.getType())?1:x.getAmount());
                        if(diff >= 0){//If + then even more stolen left
                            stolen.put(tempPI, diff);
                            x = null;
                        } else {//If negative then no more stolen thing in inventory
                            x.setAmount(x.getAmount() - stolen.get(tempPI));
                            stolen.remove(tempPI);
                        }
                    }
                }
                newPlayerInv[j][i] = x;
            }
        }
        p.getInventory().setContents(newPlayerInv[0]);
        p.getInventory().setArmorContents(newPlayerInv[1]);

        String own = RealShopping.getPInv(p).getShop().getOwner();
        if(!own.equals("@admin")){//Return items if player store.
            for(Price key:stolen2.keySet()){
                ItemStack tempIS = key.toItemStack();
                if(RealShopping.isTool(tempIS.getType())){
                    if(stolen2.get(key) > RealShopping.getMaxDur(tempIS.getType()))
                        while(RealShopping.getMaxDur(tempIS.getType()) < stolen2.get(key)){//If more than one stack/full tool
                            RealShopping.getPInv(p).getShop().addToClaim(tempIS.clone());
                            stolen2.put(key, stolen2.get(key) - RealShopping.getMaxDur(tempIS.getType()));
                        }
                    tempIS.setDurability((short) (RealShopping.getMaxDur(tempIS.getType()) - stolen2.get(key)));
                    RealShopping.getPInv(p).getShop().addToClaim(tempIS);
                } else {
                    if(stolen2.get(key) > key.getType().getMaxStackSize())
                        while(key.getType().getMaxStackSize() < stolen2.get(key)){//If more than one stack/full tool
                            ItemStack tempIStemp = tempIS.clone();
                            tempIStemp.setAmount(key.getType().getMaxStackSize());
                            RealShopping.getPInv(p).getShop().addToClaim(tempIStemp);
                            stolen2.put(key, stolen2.get(key) - key.getType().getMaxStackSize());
                        }
                    tempIS.setAmount(stolen2.get(key));
                    RealShopping.getPInv(p).getShop().addToClaim(tempIS);
                }
            }
        }
    }

    public static Map<Price, Integer> joinMaps(Map<Price,Integer> uno, Map<Price,Integer> dos){//Preserves old values
        Map<Price, Integer> retval = new HashMap<>(uno);
        for(Price p:dos.keySet()){
            if(retval.containsKey(p)) retval.put(p, retval.get(p) + dos.get(p));
            else retval.put(p, dos.get(p));
        }
        return retval;
    }
	
    /**
     * Formats an integer to a string with the ordinal number and the correct suffix 
     * @param value The integer which is the ordinal number. Example: 2
     * @return The finished string. Example: "2nd" (when using the English LangPack)
     */
    public static String formatNum(int value) {
        int hunRem = value % 100;
        int tenRem = value % 10;
        if (hunRem - tenRem == 10) {
                return value + LangPack.X_TH;
        }
        switch (tenRem) {
        	case 1:
                return value + LangPack.X_ST;
        	case 2:
                return value + LangPack.X_ND;
        	case 3:
                return value + LangPack.X_RD;
        	default:
                return value + LangPack.X_TH;
        }
    }


    public static int getTimeInt(String s){ //In seconds
        switch (s) {
            case "hour":
                return 3600;
            case "day":
                return 86400;
            case "week":
                return 604800;
            case "month":
                return 2592000;
            default:
                return Integer.parseInt(s);
        }
    }

    public static String getTimeString(int t){
		if(t == 3600) return "hour";
		else if(t == 86400) return "day";
		else if(t == 604800) return "week";
		else if(t == 2592000) return "month";
		else return t + "";
	}
}

class StringLengthComparator implements Comparator<String>{
    public int compare(String o1, String o2) {//Longest string first
      if (o1.length() > o2.length()) {
         return -1;
      } else {
         return 1;
      }
    }
}
