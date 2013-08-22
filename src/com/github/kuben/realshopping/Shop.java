/*
 * RealShopping Bukkit plugin for Minecraft
 * Copyright 2013 Jakub Fojt
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */
package com.github.kuben.realshopping;

import com.github.kuben.realshopping.exceptions.RealShoppingException;
import com.github.kuben.realshopping.listeners.RSPlayerListener;
import com.github.kuben.realshopping.prompts.PromptMaster;
import com.github.stengun.realshopping.Pager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Shop {//TODO add load/save interface

    public Shop(String name, String world, String owner) {
        super();
        this.name = name;
        this.world = world;
        this.owner = owner;
    }

    /*
     * 
     * Vars
     * 
     */
    private String name, world, owner;//Admin stores: owner = @admin
    private int buyFor = 0;

    /*
     * 
     * Getters and Setters
     * 
     */

    public String getName(){ return name; }
    public String getWorld(){ return world; }
    public String getOwner(){ return owner; }

    public int getBuyFor(){ return buyFor; }
    public void setBuyFor(int buyFor){ this.buyFor = buyFor; }

    /*
     * 
     * Entrance/Exit
     * 
     */
    public void addEntranceExit(Location en, Location ex) throws RealShoppingException {
        if (!RealShopping.addEntranceExit(new EEPair(en, ex), this)) {
            throw new RealShoppingException(RealShoppingException.Type.EEPAIR_ALREADY_EXISTS);
        }
    }

    public boolean removeEntranceExit(Location en, Location ex) {
        return RealShopping.removeEntranceExit(this, en, ex);
    }//TODO add to rsset and rssetstores

    public int clearEntrancesExits() {
        return RealShopping.clearEntrancesExits(this);
    }

    public boolean hasEntrance(Location en) {
        return RealShopping.hasEntrance(this, en);
    }

    public boolean hasExit(Location ex) {
        return RealShopping.hasExit(this, ex);
    }

    public Location getFirstE() {
        return RealShopping.getRandomEntrance(this);
    }

    public Location getCorrEntrance(Location ex) {
        return RealShopping.getEntrance(this, ex);
    }

    public Location getCorrExit(Location en) {
        return RealShopping.getExit(this, en);
    }

    /*
     * 
     * Chest functions
     * [0] is ID, [1] is data, [2] is amount(0 if full stack)
     */
    private Map<Location, ArrayList<Integer[]>> chests = new HashMap<Location, ArrayList<Integer[]>>();

    public Map<Location, ArrayList<Integer[]>> getChests() {
        return chests;
    }

    public boolean addChest(Location l) {
        if (!chests.containsKey(l)) {
            chests.put(l, new ArrayList<Integer[]>());
            if (Config.isAutoprotect()) {
                protectedChests.add(l);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean delChest(Location l) {
        if (chests.containsKey(l)) {
            chests.remove(l);
        } else {
            return false;
        }
        protectedChests.remove(l);
        return true;
    }

    public boolean isChest(Location l) {
        return chests.containsKey(l);
    }

    public int addChestItem(Location l, int[][] id) {
        int j = -1;
        if (chests.containsKey(l)) {
            j++;
            for (int[] i : id) {
                if (chests.get(l).size() < 27) {
                    if (Material.getMaterial(i[0]) != null) {
                        chests.get(l).add(ArrayUtils.toObject(i));
                        j++;
                    }
                }
            }
        }
        return j;
    }

    public boolean setChestContents(Location l, Inventory i) {
        if (chests.containsKey(l)) {
            if (i != null) {
                chests.get(l).clear();
                for (ItemStack iS : i.getContents()) {
                    if (iS != null) {
                        int am = iS.getAmount();
                        if (am == iS.getType().getMaxStackSize()) {
                            am = 0;
                        }
                        chests.get(l).add(new Integer[]{iS.getTypeId(), (int) iS.getData().getData(), am});
                    } else {
                        chests.get(l).add(new Integer[]{0, 0, 0});
                    }
                }
            }
        }
        return false;
    }

    public int delChestItem(Location l, int[][] id) {
        int j = -1;
        if (chests.containsKey(l)) {
            j++;
            for (int[] i : id) {
                boolean match = false;
                int k = 0;
                for (; k < chests.get(l).size(); k++) {
                    if (chests.get(l).get(k)[0] == i[0] && chests.get(l).get(k)[1] == i[1]) {
                        match = true;
                        break;
                    }
                }
                if (match) {
                    chests.get(l).remove(k);
                    j++;
                }
            }
        }
        return j;
    }

    public int clearChestItems(Location l) {
        int j = -1;
        if (chests.containsKey(l)) {
            j = chests.get(l).size();
            chests.get(l).clear();
        }
        return j;
    }

    /*
     * 
     * Prices
     * Map stores pennies from 0.44 on
     */
    private Map<Price, Integer[]> prices = new HashMap<>();//Price array [0] is price, [1] is min and [2] is maxprice

    public boolean hasPrices() {
        return !prices.isEmpty();
    }

    public boolean hasPrice(Price p) {
        return prices.containsKey(p);
    }

    public Integer getPrice(Price p) {
        Integer[] r = prices.get(p);
        return (r == null ? 0 : r[0]);
    }

    public Map<Price, Integer> getPrices() {
        Map<Price, Integer> temp = new HashMap<>();
        for (Price p : prices.keySet().toArray(new Price[0])) {
            temp.put(p, prices.get(p)[0]);
        }
        return temp;
    }

    public Map<Price, Integer[]> getPricesMap() {
        return prices;
    }

    public Integer setPrice(Price p, Integer i) {
        Integer[] r = prices.put(p, new Integer[]{i});
        return (r == null ? null : r[0]);
    }

    public boolean removePrice(Price p) {
        return prices.remove(p) != null;
    }

    public Integer getMin(Price p) {
        if (prices.containsKey(p) && prices.get(p).length == 3) {
            return prices.get(p)[1];
        }
        return null;
    }

    public Integer getMax(Price p) {
        if (prices.containsKey(p) && prices.get(p).length == 3) {
            return prices.get(p)[2];
        }
        return null;
    }

    public boolean hasMinMax(Price p) {
        return (prices.containsKey(p) && prices.get(p).length == 3);
    }

    public boolean setMinMax(Price p, Integer min, Integer max) {
        if (prices.containsKey(p)) {
            prices.put(p, new Integer[]{getPrice(p), min, max});
            return true;
        }
        return false;
    }

    public void clearMinMax(Price p) {
        setPrice(p, getPrice(p));
    }

    public void clearPrices() {
        prices.clear();
    }

    public boolean clonePrices(String store) {
            if(store == null){
                    prices = getLowestPrices();
                    return true;
            }
            if(!RealShopping.shopExists(store)) return false;
            prices = new HashMap<>(RealShopping.getShop(store).prices);
            return true;
    }

    public void setPrices(Map<Price, Integer[]> prices) {
        this.prices = prices;
    }

    /*
     * 
     * Sales
     * 
     */
    private Map<Price, Integer> sale = new HashMap<>();

    public boolean hasSales() {
        return !sale.isEmpty();
    }

    public boolean hasSale(Price p) {
        return sale.containsKey(p);
    }

    public void clearSales() {
        sale.clear();
    }

    public Integer getFirstSale() {
        return (Integer) sale.values().toArray()[0];
    }

    public Integer getSale(Price p) {
        if (hasSale(p)) {
            return sale.get(p);
        }
        return 0;
    }

    public void addSale(Price p, int pcnt) {
        sale.put(p, pcnt);
    }

    public void setSale(Map<Price, Integer> sale) {
        this.sale = sale;
    }

    /*
     * 
     * Statistics
     * 
     */
    private Set<Statistic> stats = new HashSet<>();

    public Set<Statistic> getStats() {
        return stats;
    }

    public void addStat(Statistic stat) {
        if (stat.getAmount() > 0) {
            stats.add(stat);
        }
    }

    public void removeStat(Statistic stat) {
        stats.remove(stat);
    }

    /*
     * 
     * Stolen, banned, and protected
     * 
     */
    private List<ItemStack> stolenToClaim = new ArrayList<>();
    private Set<String> banned = new HashSet<>();
    private Set<Location> protectedChests = new HashSet<>();

    public List<ItemStack> getStolenToClaim() {
        return stolenToClaim;
    }

    public boolean hasStolenToClaim() {
        return !stolenToClaim.isEmpty();
    }

    public void clearStolenToClaim() {
        stolenToClaim.clear();
    }
    
    public void setStolenToClaim(List<ItemStack> stolen) {
        this.stolenToClaim = stolen;
    }
    
    public void addStolenToClaim(ItemStack stolenItem) {
        stolenToClaim.add(stolenItem);
    }

    public ItemStack claimStolenToClaim() {
        if (!stolenToClaim.isEmpty()) {
            ItemStack tempIs = stolenToClaim.get(0);
            stolenToClaim.remove(tempIs);
            return tempIs;
        }
        return null;
    }

    public Set<String> getBanned() {
        return banned;
    }

    public boolean isBanned(String p) {
        return banned.contains(p);
    }

    public void addBanned(String p) {
        banned.add(p);
    }

    public void removeBanned(String p) {
        banned.remove(p);
    }

    public boolean isProtectedChest(Location chest) {
        return protectedChests.contains(chest);
    }

    public boolean addProtectedChest(Location chest) {
        return protectedChests.add(chest);
    }

    public boolean removeProtectedChest(Location chest) {
        return protectedChests.remove(chest);
    }
    /*
     * 
     * Player timer threads for page flipping.
     * 
     */
    private static Map<String, Pager> timers = new HashMap<>();

    public static Pager getPager(String player) {
        return timers.get(player);
    }
    
    public static void resetPagers(){
        for(String pl:timers.keySet()){
            removePager(pl);
        }
    }
    
    public static void removePager(String player) {
        Pager pg = timers.get(player);
        if (pg == null) {
            return;
        }
        pg.setStop(true);
        try {
            pg.join(5000);
        } catch (InterruptedException ex) {
            RealShopping.logsevere(ex.getStackTrace().toString());
        }
        timers.remove(player);
    }

    /**
     * This method allows for safe pager add. If a pager is present it will be
     * stopped and replaced with the new pager.
     *
     * @param player
     * @param pager
     */
    public static void addPager(String player) {
        if (timers.containsKey(player)) {
            Pager pg = timers.get(player);
            pg.setStop(true);
            try {
                pg.join(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Pager pager = new Pager();
        pager.start();
        timers.put(player, pager);
    }
    // ------- MISC

    /**
     * Exports all protected chests and their location to a string.
     *
     * @return a string with world and location of protected chest.
     */
    public String exportProtectedToString() {
        if (!protectedChests.isEmpty()) {
            String tempS = "";
            for (Location tempL : protectedChests) {
                if (!chests.containsKey(tempL)) {
                    tempS += ";" + tempL.getWorld().getName() + "," + (int) tempL.getX() + "," + (int) tempL.getY() + "," + (int) tempL.getZ();
                }
            }
            return (tempS.length() > 0) ? tempS.substring(1) : "";
        } else {
            return "";
        }
    }

    /**
     * Exports a string with all stolen items that are waiting to be collected.
     *
     * @return
     * @Deprecated
     */
    @Deprecated
    public String exportToClaim() {
        String s = "";
        for (ItemStack tempIS : stolenToClaim) {
            if (tempIS != null) {
                s += "," + tempIS.getTypeId() + ":" + tempIS.getAmount() + ":" + tempIS.getDurability() + ":" + tempIS.getData().getData();
                Price p = new Price(tempIS);
                for (Price tmp : prices.keySet()) { //TODO maybe a better way to do this?
                    if (tmp.equals(p) && tmp.hasDescription()) {
                        s += ":" + tmp.getDescription();
                    }
                }
            }
        }
        return (s.length() > 0) ? s.substring(1) : "";
    }

    /**
     * Export all stats to String. This method is used to export stats when
     * deactivating the plugin.
     *
     * @return All stats converted to string.
     */
    public String exportStats() {
        String s = "";
        for (Statistic stat : stats) {
            s += ";" + stat.getTime() + ":" + stat.isBought() + ":" + stat.getItem().toString(stat.getAmount());
        }
        return s;
    }

    @Override
    public String toString() {
        return "Shop " + name + (owner.equals("@admin") ? "" : " owned by " + owner) + " Prices: " + prices.toString();
    }

    private Map<Price, Integer[]> getLowestPrices() {
        Map<Price, Integer[]> tempMap = new HashMap<>();
        for(Shop shop : RealShopping.getShops()) {
            if(!shop.getName().equals(name)) {
                Price[] keys2 = shop.getPrices().keySet().toArray(new Price[0]);
                for(Price p : keys2) {
                    if(tempMap.containsKey(p)){
                        if(tempMap.get(p)[0] > shop.getPrice(p))
                            tempMap.put(p, new Integer[] { shop.getPrice(p) });
                        else
                            tempMap.put(p, new Integer[] { shop.getPrice(p) });
                    }
                }
            }
        }
        return tempMap;
    }

    /*
     * 
     * Static Methods
     * 
     */
    public static boolean sellToStore(Player p, ItemStack[] iS) {
        Shop shop = RealShopping.getPInv(p).getShop();
        if (Config.isEnableSelling() && RealShopping.hasPInv(p) && shop.getBuyFor() > 0) {
            int payment = 0;
            List<ItemStack> sold = new ArrayList<>();
            for (ItemStack ist : iS) {//Calculate cost and check if player owns items
                if (ist != null) {
                    Price itm = new Price(ist);
                    if (shop.hasPrice(itm)) {//Something in inventory has a price
                        int amount = ((RealShopping.isTool(ist.getTypeId())) ? RealShopping.getMaxDur(ist.getTypeId()) - ist.getDurability() : ist.getAmount());

                        int soldAm = amount;
                        for (ItemStack tempSld : sold) {
                            if (tempSld.getTypeId() == itm.getType()) {
                                soldAm += ((RealShopping.isTool(itm.getType())) ? RealShopping.getMaxDur(itm.getType()) - ist.getDurability() : ist.getAmount());
                            }
                        }

                        if (RealShopping.getPInv(p).getAmount(ist) >= soldAm) {
                            int cost = 0;
                            if (shop.hasPrice(itm)) {
                                cost = shop.getPrice(itm);
                            }
                            //There is a sale on that item.
                            int pcnt = 0;
                            if (shop.hasSale(itm)) {
                                pcnt = 100 - shop.getSale(itm);
                                cost *= pcnt / 100f;
                            }
                            cost *= shop.getBuyFor() / 100f;

                            sold.add(ist);
                            payment += cost * (RealShopping.isTool(itm.getType()) ? (double) amount / (double) RealShopping.getMaxDur(itm.getType()) : amount);//Convert items durability to item amount
                        }
                    }
                }
            }
            boolean cont = false;
            String own = shop.getOwner();
            if (!own.equals("@admin")) {
                if (RSEconomy.getBalance(own) >= payment / 100f) {
                    RSEconomy.deposit(p.getName(), payment / 100f);
                    RSEconomy.withdraw(own, payment / 100f);//If player owned store, withdraw from owner
                    if (!sold.isEmpty()) {
                        p.sendMessage(ChatColor.GREEN + LangPack.SOLD + sold.size() + LangPack.ITEMSFOR + payment / 100f + LangPack.UNIT);
                    }
                    RealShopping.sendNotification(own, LangPack.YOURSTORE + shop.getName() 
                            + LangPack.BOUGHTSTUFFFOR 
                            + payment / 100f + LangPack.UNIT 
                            + LangPack.FROM + p.getName());
                    for (ItemStack key : sold) {
                        if (Config.isEnableAI()) {
                            shop.addStat(new Statistic(new Price(key), key.getAmount(), false));
                        }
                        RealShopping.getPInv(p).removeItem(key, key.getAmount());
                    }
                    cont = true;
                } else {
                    p.sendMessage(ChatColor.RED + LangPack.OWNER + own + LangPack.CANTAFFORDTOBUYITEMSFROMYOUFOR + payment / 100f + LangPack.UNIT);
                }
            } else {
                RSEconomy.deposit(p.getName(), payment / 100f);
                p.sendMessage(ChatColor.GREEN + LangPack.SOLD + sold.size() + LangPack.ITEMSFOR + payment / 100f + LangPack.UNIT);
                for (ItemStack key : sold) {
                    RealShopping.getPInv(p).removeItem(key, key.getAmount());
                }
                cont = true;
            }
            if (cont) {
                if (!own.equals("@admin")) {//Return items if player store.
                    for (int i = 0; i < sold.size(); i++) {
                        shop.addStolenToClaim(sold.get(i));
                    }
                }
                ItemStack[] newInv = p.getInventory().getContents();
                boolean skip = false;//To save CPU
                for (int i = 0; i < iS.length; i++) {
                    if (sold.contains(iS[i])) {//Item is sold, do not return to player
                        sold.remove(iS[i]);
                    } else {
                        if (!skip) {
                            for (int j = 0; j < newInv.length; j++) {
                                if (newInv[j] == null) {
                                    newInv[j] = iS[i];
                                    iS[i] = null;
                                    break;
                                }
                            }
                        }
                        if (iS[i] != null) {//Item hasn't been returned
                            skip = true;
                            p.getWorld().dropItem(p.getLocation(), iS[i]);
                        }
                    }
                }
                p.getInventory().setContents(newInv);
                return true;
            }
        }
        return false;
    }

    public static boolean prices(CommandSender sender, int page, Shop shop){//In 0.50+ pages start from 1
        if(shop.hasPrices()){
            Map<Price, Integer> tempMap = shop.getPrices();
            if(!tempMap.isEmpty()){
                Price[] keys = tempMap.keySet().toArray(new Price[0]);
                if((page-1)*9 < keys.length){//If page exists
                    if(shop.hasSales()){
                        sender.sendMessage(ChatColor.GREEN + LangPack.THEREISA + shop.getFirstSale() + LangPack.PCNTOFFSALEAT + shop.getName());
                    }
                    if(page*9 < keys.length){//Not last
                        for(int i = 9*(page-1);i < 9*page;i++){
                            int cost = tempMap.get(keys[i]);
                            String onSlStr = "";
                            if(shop.hasSale(keys[i].stripOffData()) || shop.hasSale(keys[i])){//There is a sale on that item.
                                int pcnt = -1;
                                if(shop.hasSale(keys[i].stripOffData())) pcnt = 100 - shop.getSale(keys[i].stripOffData());
                                if(shop.hasSale(keys[i]))  pcnt = 100 - shop.getSale(keys[i]);
                                cost *= pcnt/100f;
                                onSlStr = ChatColor.GREEN + LangPack.ONSALE;
                            }
                            sender.sendMessage(ChatColor.BLUE + "" + keys[i].formattedString() + ChatColor.BLACK + " - " + ChatColor.RED + cost/100f + LangPack.UNIT + onSlStr);
                        }
                        sender.sendMessage(ChatColor.RED + LangPack.MOREITEMSONPAGE + (page + 1));
                    } else {//Last page
                        for(int i = 9*(page-1);i < keys.length;i++){
                            int cost = tempMap.get(keys[i]);
                            String onSlStr = "";
                            if(shop.hasSale(keys[i].stripOffData()) || shop.hasSale(keys[i])){//There is a sale on that item.
                                int pcnt = -1;
                                if(shop.hasSale(keys[i].stripOffData())) pcnt = 100 - shop.getSale(keys[i].stripOffData());
                                if(shop.hasSale(keys[i]))  pcnt = 100 - shop.getSale(keys[i]);
                                cost *= pcnt/100f;
                                onSlStr = ChatColor.GREEN + LangPack.ONSALE;
                            }
                            sender.sendMessage(ChatColor.BLUE + "" + keys[i].formattedString() + ChatColor.BLACK + " - " + ChatColor.RED + cost/100f + LangPack.UNIT + onSlStr);
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + LangPack.THEREARENTTHATMANYPAGES);
                }
            } else {
                sender.sendMessage(ChatColor.RED + LangPack.THEREARENOPRICESSETFORTHISSTORE);
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + LangPack.THEREARENOPRICESSETFORTHISSTORE);
            return true;
        }
        return true;
    }

    public static boolean pay(Player player, Inventory[] invs) {
        if (RealShopping.hasPInv(player)) {
            RSPlayerInventory pinv = RealShopping.getPInv(player);
            Shop shop = pinv.getShop();
            if (shop.hasPrices()) {
                int toPay = pinv.toPay(invs);
                if (toPay == 0) {
                    return false;
                }
                if (RSEconomy.getBalance(player.getName()) < toPay / 100f) {
                    player.sendMessage(ChatColor.RED + LangPack.YOUCANTAFFORDTOBUYTHINGSFOR + toPay / 100f + LangPack.UNIT);
                    return true;
                }
                RSEconomy.withdraw(player.getName(), toPay/100f);
                if(!shop.getOwner().equals("@admin")){
                    RSEconomy.deposit(shop.getOwner(), toPay/100f);//If player owned store, pay player
                    if(RealShopping.getPlayerSettings(player.getName()).getSoldNotifications(shop, toPay/100))//And send a notification perhaps
                        RealShopping.sendNotification(shop.getOwner(), player.getName() + LangPack.BOUGHTSTUFFFOR + toPay/100f + LangPack.UNIT + LangPack.FROMYOURSTORE + shop.getName() + ".");
                }
                Map<Price, Integer> bought = pinv.getBoughtWait(invs);
                for (Price p : bought.keySet()) {
                    pinv.addBought(p, bought.get(p));
                }

                if (Config.isEnableAI()) {
                    for (Price key : bought.keySet()) {
                        shop.addStat(new Statistic(key, bought.get(key), true));
                    }
                }

//                if(invs != null) pinv.update(invs);
//                else pinv.update();
                player.sendMessage(ChatColor.GREEN + LangPack.YOUBOUGHTSTUFFFOR + toPay / 100f + LangPack.UNIT);
                return true;
            } else {
                player.sendMessage(ChatColor.RED + LangPack.THEREARENOPRICESSETFORTHISSTORE);
                return true;
            }
        } else player.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
        return false;
    }

    public static boolean exit(Player player, boolean cmd){
        if(RealShopping.hasPInv(player)){
            if(!PromptMaster.isConversing(player) && !RSPlayerListener.hasConversationListener(player)){
                if(RealShopping.getPInv(player).hasPaid() || player.getGameMode() == GameMode.CREATIVE){
                    Shop tempShop = RealShopping.getPInv(player).getShop();
                    Location l = player.getLocation().getBlock().getLocation().clone();
                    if(tempShop.hasExit(l)){
                        l = tempShop.getCorrEntrance(l);
                        RealShopping.removePInv(player);
                        removePager(player.getName());
                        player.teleport(l.add(0.5, 0, 0.5));
                        player.sendMessage(ChatColor.GREEN + LangPack.YOULEFT + ChatColor.DARK_GREEN + tempShop.getName());
                        return true;
                    } else if(cmd)player.sendMessage(ChatColor.RED + LangPack.YOURENOTATTHEEXITOFASTORE);
                } else player.sendMessage(ChatColor.RED + LangPack.YOUHAVENTPAIDFORALLYOURARTICLES);
            } else {
                player.sendRawMessage(ChatColor.RED + LangPack.YOU_CANT_DO_THIS_WHILE_IN_A_CONVERSATION);
                player.sendRawMessage(LangPack.ALL_CONVERSATIONS_CAN_BE_ABORTED_WITH_ + ChatColor.DARK_PURPLE + "quit");
            }
        } else player.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
        return false;
    }

    public static boolean enter(Player player, boolean cmd){
        if(!PromptMaster.isConversing(player) && !RSPlayerListener.hasConversationListener(player)){
            Location l = player.getLocation().getBlock().getLocation().clone(); 
            Shop tempShop = RealShopping.isEntranceTo(l);
            if(tempShop != null){//Enter shop
                Location ex = tempShop.getCorrExit(l);
                if(!tempShop.isBanned(player.getName().toLowerCase())){
                    player.teleport(ex.add(0.5, 0, 0.5));

                    RealShopping.addPInv(new RSPlayerInventory(player, tempShop));
                    
                    player.sendMessage(ChatColor.GREEN + LangPack.YOUENTERED + tempShop.getName());

                    //Refill chests
                    Location[] chestArr = tempShop.getChests().keySet().toArray(new Location[0]);
                    for(int i = 0;i < chestArr.length;i++){
                        Block tempChest = player.getWorld().getBlockAt(chestArr[i]);
                        if(tempChest.getType() != Material.CHEST) tempChest.setType(Material.CHEST);
                        BlockState blockState = tempChest.getState();
                        if(blockState instanceof Chest){
                            Chest chest = (Chest)blockState;
                            chest.getBlockInventory().clear();
                            ItemStack[] itemStack = new ItemStack[27];
                            int k = 0;
                            for(Integer[] j:tempShop.getChests().get(chestArr[i])){
                                itemStack[k] = new MaterialData(j[0],j[1].byteValue())
                                .toItemStack((j[2]==0)?Material.getMaterial(j[0]).getMaxStackSize():j[2]);
                                k++;
                            }
                            chest.getBlockInventory().setContents(itemStack);
                        }
                    }
                        addPager(player.getName());
                    return true;
                } else player.sendMessage(ChatColor.RED + LangPack.YOUAREBANNEDFROM + tempShop.getName());
            } else if(cmd) player.sendMessage(ChatColor.RED + LangPack.YOURENOTATTHEENTRANCEOFASTORE);
        } else {
            player.sendRawMessage(ChatColor.RED + LangPack.YOU_CANT_DO_THIS_WHILE_IN_A_CONVERSATION);
            player.sendRawMessage(LangPack.ALL_CONVERSATIONS_CAN_BE_ABORTED_WITH_ + ChatColor.DARK_PURPLE + "quit");
        }
        return false;
    }
}
