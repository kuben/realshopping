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
import com.github.stengun.realshopping.Coupon;
import com.github.stengun.realshopping.Pager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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

public class Shop {

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
    private final String name, world, owner;//Admin stores: owner = @admin
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
    public void addEntranceExit(Location en, Location ex) throws RealShoppingException{ 
        EEPair ep = new EEPair(en, ex);
        this.addEEPair(ep);
    }
    
    public void addEEPair(EEPair ee) throws RealShoppingException{
        RealShopping.addEntranceExit(ee, this);
    }
    
    public boolean removeEntranceExit(Location en, Location ex){ return RealShopping.removeEntranceExit(this, en, ex); }
    public boolean removeEEPair(CommandSender player,int index) {
        boolean retval = false;
        EEPair[] pairs = getEEpairSet().toArray(new EEPair[0]);
        if(index >= pairs.length || index < 0) return false;
        retval = RealShopping.removeEntranceExit(this, pairs[index]);
        if(retval) player.sendMessage("EEPair removed: "+ pairs[index].toString());
        return retval;
    }
    public Set<EEPair> getEEpairSet() {
        return RealShopping.getEEPairSet(this);
    }
    public int clearEntrancesExits(){ return RealShopping.clearEntrancesExits(this); }
    public boolean hasEntrance(Location en){ return RealShopping.hasEntrance(this, en); }
    public boolean hasExit(Location ex){ return RealShopping.hasExit(this, ex); }
    public Location getFirstE(){ return RealShopping.getRandomEntrance(this); }
    public Location getCorrEntrance(Location ex) { return RealShopping.getEntrance(this, ex); }
    public Location getCorrExit(Location en) { return RealShopping.getExit(this, en); }

    /*
     * 
     * Chest functions
     *
     */
    private Map<Location, Collection<ItemStack>> refillingchests = new HashMap<>();

    public Map<Location, Collection<ItemStack>> getChests() {
        return refillingchests;
    }

    public boolean addChest(Location l) {
        if (!refillingchests.containsKey(l)) {
            refillingchests.put(l, new ArrayList<ItemStack>());
            if (Config.isAutoprotect()) {
                protectedChests.add(l);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean delChest(Location l) {
        if (refillingchests.containsKey(l)) {
            refillingchests.remove(l);
        } else {
            return false;
        }
        protectedChests.remove(l);
        return true;
    }

    public boolean isChest(Location l) {
        return refillingchests.containsKey(l);
    }

    public int addChestItem(Location l, Collection<ItemStack> items) {
        int added = 0;
        if (refillingchests.containsKey(l)) { //Se esiste una protected chest in quella locazione
            if (items.size() + refillingchests.get(l).size() < 27) {
                refillingchests.get(l).addAll(items);
                added = items.size();
            }
        }
        return added;
    }

    public boolean setChestContents(Location l, Inventory i) {
        if (refillingchests.containsKey(l)) {
            if (i != null) {
                refillingchests.get(l).clear();
                for (ItemStack iS : i.getContents()) {
                    if (iS != null) {
                        refillingchests.get(l).add(iS);
                    }
                }
            }
        }
        return false;
    }

    public int delChestItem(Location l, Collection<ItemStack> items) {
        int retval = 0;
        List<ItemStack> newContent = new ArrayList<>(refillingchests.get(l));
        if (refillingchests.containsKey(l)) {
            for (ItemStack itm : refillingchests.get(l)) {
                for(ItemStack is : items) {
                    if(is.isSimilar(itm)) {
                        newContent.remove(itm);
                        break;
                    }
                }
            }
            retval = refillingchests.get(l).size() - newContent.size();
            refillingchests.put(l, newContent);
        }
        return retval;
    }

    public int clearChestItems(Location l) {
        int j = -1;
        if (refillingchests.containsKey(l)) {
            j = refillingchests.get(l).size();
            refillingchests.get(l).clear();
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
    
    public boolean hasSimilarPrice(Price p) {
        if(prices.containsKey(p)) {
            return true;
        }
        for(Price pr:prices.keySet()) {
            if(pr.similar(p)) return true;
        }
        return false;        
    }
    
    /**
     * Tells me if this store have a price for an item (not considering its amount).
     * @param p Price object to check.
     * @return true if the price is present, false otherwise.
     */
    public boolean hasPrice(Price p) {
        if(prices.containsKey(p)) {
            return true;
        }
        for(Price pr:prices.keySet()) {
            if(pr.similarData(p)) return true;
        }
        return false;
    }

    /**
     * Gets, if present, the cost for given item.
     * The returned price amount will be less or equal than given one, and if not present
     * this method will return 0.
     * @param p Price to check.
     * @return The correct price for the item, 0.0 if not present.
     */
    public double getCostPerUnit(Price p) {
        Price found = new Price(p.getType());
        found.setAmount(0);
        for(Price pr:prices.keySet()) {
            if(p.compatible(pr)) {
                if(found.getAmount() < pr.getAmount()) {
                    found = pr;
                }
            }
        }
        Integer[] r = prices.get(found);
        if(r == null) return 0.0;
        double retval = ((double)r[0])/found.getAmount();
        return retval;
    }

    public Map<Price, Integer> getCosts() {
        Map<Price, Integer> temp = new HashMap<>();
        for (Price p : prices.keySet().toArray(new Price[0])) {
            temp.put(p, prices.get(p)[0]);
        }
        return temp;
    }

    public Map<Price, Integer[]> getPricesMap() {
        return prices;
    }
    
/**
 * Sets a price for an item.
 * @param p Price to set.
 * @param i Integer object with new price.
 * @return null if the item was not present, otherwise will set the price and return the old one.
 */
    public Integer setCost(Price p, Integer i) {
        Integer retval = null;
        if(prices.containsKey(p)) {
            retval = prices.get(p)[0];
            prices.remove(p);
        }
        prices.put(p, new Integer[]{i});
        return retval;
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
            prices.put(p, new Integer[]{(int)getCostPerUnit(p), min, max});
            return true;
        }
        return false;
    }

    public void clearMinMax(Price p) {
        setCost(p, (int)getCostPerUnit(p));
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

    public boolean hasSimilarSale(Price p) {
        for(Price pri:sale.keySet()){
            if(pri.similar(p)) return true;
        }
        return false;
    }
    public Integer hasSale(Price p) {
        for(Price salpri:sale.keySet()) {
            if((salpri.isIsgeneric() && p.similarButHash(salpri)) || salpri.equals(p) ||salpri.similarData(p)) {
                return sale.get(salpri);
            }
        }
        return null;
    }

    public void clearSales() {
        sale.clear();
    }

    public Integer getFirstSale() {
        return (Integer) sale.values().toArray()[0];
    }

    public Integer getSale(Price p) {
        Integer saleval = hasSale(p);
        if (saleval != null) {
            return saleval;
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
     * Stolen (to claim), banned, and protected
     * 
     */
    private List<ItemStack> toClaim = new ArrayList<>();
    private Set<String> banned = new HashSet<>();
    private Set<Location> protectedChests = new HashSet<>();

    /**
     * Gets the list of items that wait to be claimed.
     * @return List of ItemStack ready to be used.
     */
    public List<ItemStack> getToClaim() {
        return toClaim;
    }

    /**
     * Checks if a shop has items to claim.
     * @return true if there are some, false otherwise.
     */
    public boolean hasToClaim() {
        return !toClaim.isEmpty();
    }

    /**
     * Removes all items that waits to be claimed.
     */
    public void clearToClaim() {
        toClaim.clear();
    }
    
    /**
     * Manually sets a list of items
     * @param list_of_to_claim The list of items that we want to be in the shop's claim list.
     */
    public void setToClaim(List<ItemStack> list_of_to_claim) {
        this.toClaim = list_of_to_claim;
    }
    
    /**
     * Adds items to claim list.
     * @param item item(s) to add.
     */
    public void addToClaim(ItemStack...item) {
        toClaim.addAll(Arrays.asList(item));
    }
    public void addToClaim(List<ItemStack> items) {
        toClaim.addAll(items);
    }

    /**
     * Retrieves the first item in the claim list, removing it.
     * Calling this method in a for statement is the best way to use it.
     * If the list is empty, this method will return null.
     * @return The first item in the claim list, null if there are no items.
     */
    public ItemStack pullFirstToClaim() {
        if (!toClaim.isEmpty()) {
            ItemStack tempIs = toClaim.get(0);
            toClaim.remove(tempIs);
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
     * Player timer threads for page flipping.
     */
    
    private static Map<String, Pager> timers = new HashMap<>();

    /**
     * Retrieves a pager of a player.
     * @param player Player with the pager we want to retrieve.
     * @return The correspondent pager, null if a player has no pager.
     */
    public static Pager getPager(String player) {
        return timers.get(player);
    }
    
    /**
     * Safely cleans all pagers.
     */
    public static void resetPagers(){
        for(String pl:timers.keySet()){
            removePager(pl);
        }
    }

    /**
     * Safely deletes a player's pager.
     * If a pager is not present it will do nothing.
     * @param player Player who's pager is going to be removed.
     */
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
     * @param player Player to add pager to.
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
        Pager pager = new Pager(player);
        pager.start();
        timers.put(player, pager);
    }
    
    
    // ------- UTILS
    
    public Set<Location> getProtectedChests() {
        return protectedChests;
    }

    @Override
    public String toString() {
        return "Shop " + name + (owner.equals("@admin") ? "" : " owned by " + owner) + " Prices: " + prices.toString();
    }

    private Map<Price, Integer[]> getLowestPrices() {
        Map<Price, Integer[]> tempMap = new HashMap<>();
        for(Shop shop : RealShopping.getShops()) {
            if(!shop.getName().equals(name)) {
                for(Price p : shop.getCosts().keySet()) {
                    if(tempMap.containsKey(p)){
                        if(tempMap.get(p)[0] > shop.getCostPerUnit(p))
                            tempMap.put(p, new Integer[] { (int)shop.getCostPerUnit(p) });
                        else
                            tempMap.put(p, new Integer[] { (int)shop.getCostPerUnit(p) });
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
    
    /**
     * Lists all Entrance - Exit pairs of a given shop.
     * The list is ordered with numbers, you can use these numbers for delete purposes.
     * @param sender Player who wrote the command.
     * @param page Page we want to see.
     * @param shop Shop wich we need to list the pairs.
     * @return 
     */
    public static boolean listEEPairs(CommandSender sender, int page, Shop shop) {
        if(shop.getEEpairSet().isEmpty()) return false;
        EEPair[] pairs = shop.getEEpairSet().toArray(new EEPair[0]);
        if((page-1)*9 >= pairs.length) {
            sender.sendMessage(ChatColor.RED + LangPack.THEREARENTTHATMANYPAGES);
            return false;
        }
        for(int i = 9*(page-1);i<9*page;i++) {
            if(i >= pairs.length) break;
            EEPair ee = pairs[i];
            sender.sendMessage(i + " -----------------\n" + 
                    ee.toString() + "\nFor shop "+ ChatColor.GREEN + shop.getName());
        }
        sender.sendMessage("  -----------------");
        if(page*9 < pairs.length){//Not last
            sender.sendMessage(LangPack.MOREITEMSONPAGE + ChatColor.YELLOW + (page + 1));
        }
        return true;
    }
   /**
    * Gets the price for a single item and its amount.
    * @param shop Shop where to bring the price.
    * @param ist Item we want to check price to.
    * @return The price for that item. If not present, the price will be 0.0
    */
    public static float sellPrice(Shop shop, ItemStack ist) {
        int payment = 0;
        if (ist != null) {
            Price itm = new Price(ist);
            if (shop.hasPrice(itm)) {
                int amount = ((RealShopping.isTool(ist.getType())) ? 1 : ist.getAmount());
                itm.setAmount(amount);
                double cost = shop.getCostPerUnit(itm);
                if (cost > 0.0) {
                    int pcnt;
                    if (shop.hasSale(itm)!=null) {
                        pcnt = 100 - shop.getSale(itm);
                        cost *= pcnt / 100f;
                    }
                    cost *= shop.getBuyFor() / 100f;
                    payment = (int)(cost * (RealShopping.isTool(ist.getType()) ? (RealShopping.getMaxDur(ist.getType()) - ist.getDurability())/RealShopping.getMaxDur(ist.getType()) : amount));// Durability is treated as Malus for PrintPrices.
                }
            }
        }
        return payment/100f;
    }
    
    
    /**
     * Sells items to the store.
     * In order to work, this method must be called from a player that actually <i>is</i>
     * in a store.
     * @param p Player that is going to sell an item.
     * @param iS Item that's going to be sold.
     * @return true if the item is sold, false if not or if the player is not in a store.
     */
    
    public static List<ItemStack> sellToStore(Player p, List<ItemStack> iS) {
        List<ItemStack> cannotsell = new ArrayList<>();
        if ( !Config.isEnableSelling() 
                || !RealShopping.hasPInv(p)
                || !RealShopping.getPInv(p).getShop().hasPrices() 
                || RealShopping.getPInv(p).getShop().getBuyFor() < 1) 
        {
            return cannotsell;
        }
        boolean sold = true;
        RSPlayerInventory pinv = RealShopping.getPInv(p);
        Shop shop = pinv.getShop();
        float payment = 0.0f;
        List<ItemStack> cansell = new ArrayList<>();
        //List<ItemStack> cannotsell = new ArrayList<>();
        // Separate objects that I can sell from objects that I can't
        for(ItemStack itm : iS){
            if(pinv.getAmount(itm) == 0 || sellPrice(shop,itm) == 0.0f) {
                if (itm != null) cannotsell.add(itm);
                continue;
            }
            int excess = itm.getAmount() - pinv.getAmount(itm);
            itm.setAmount(itm.getAmount() - excess);
            pinv.removeItem(itm, itm.getAmount());
            cansell.add(itm);
            payment += sellPrice(shop, itm);
            if(excess >0) {
                ItemStack tmp = new ItemStack(itm);
                tmp.setAmount(excess);
                cannotsell.add(tmp);
            }            
        }
        // Calculate prices for items that can be sold. If this price cannot be afforded
        // by shopkeeper the objects will be returned to the player.
        if(!cansell.isEmpty()){
            String owner = shop.getOwner();
            if(owner.equals(p.getName())){
                p.sendMessage(ChatColor.RED + "You can't sell to your own shop!");
                cannotsell.addAll(cansell);
                sold = false;
            } else if(!owner.equals("@admin")){
                if(RSEconomy.getBalance(owner) >= payment) {
                    RSEconomy.withdraw(owner, payment);
                    for (ItemStack itm : cansell) {
                        if (Config.isEnableAI()) {
                            shop.addStat(new Statistic(itm.getType(), "", itm.getAmount(), false));
                        }
                    }
                    shop.addToClaim(cansell);
                    if (RealShopping.getPlayerSettings(owner).getBoughtNotifications(shop, (int) (payment)))
                        RealShopping.sendNotification(owner, LangPack.YOURSTORE
                                + shop.getName() + LangPack.BOUGHTSTUFFFOR
                                + payment + LangPack.UNIT
                                + LangPack.FROM + p.getName());
                } else {
                    p.sendMessage(ChatColor.RED + LangPack.OWNER + owner + LangPack.CANTAFFORDTOBUYITEMSFROMYOUFOR + payment + LangPack.UNIT);
                    cannotsell.addAll(cansell);
                    sold = false;
                }
            }
            if(sold) {
                RSEconomy.deposit(p.getName(), payment);
                p.sendMessage(ChatColor.GREEN + LangPack.SOLD + ChatColor.DARK_GREEN + cansell.size() + ChatColor.GREEN + LangPack.ITEMSFOR
                        + ChatColor.DARK_GREEN + payment + ChatColor.GREEN + LangPack.UNIT);
            }
        }
        //p.getInventory().addItem(cannotsell.toArray(new ItemStack[0]));
        //p.updateInventory();
        //return sold;
        return cannotsell;
    }


    /**
     * Prints on screen the shop's PrintPrices.
     * Prices are divided by pages, every page is composed of 9 lines. With this
     * method you must choose the page you want to see.
     * @param sender Player who wrote the command.
     * @param page Requested page.
     * @param shop Shop where to bring the price list.
     * @return true if the command was executed without problems. False if the store has no PrintPrices.
     */
    public static boolean PrintPrices(CommandSender sender, int page, Shop shop){//In 0.50+ pages start from 1
        if(shop.hasPrices()){
            int maxitems = 6; //how many items we want to show
            Map<Price, Integer> tempMap = shop.getCosts();
            if(!tempMap.isEmpty()){
                Price[] keys = tempMap.keySet().toArray(new Price[0]);
                if((page-1)*maxitems < keys.length){//If page exists
                    if(shop.hasSales()){
                        sender.sendMessage(ChatColor.GREEN + LangPack.THEREISA + ChatColor.DARK_GREEN + shop.getFirstSale()
                                + ChatColor.GREEN + LangPack.PCNTOFFSALEAT + ChatColor.DARK_GREEN + shop.getName());
                    }
                    for(int i = maxitems*(page-1);i < maxitems*page;i++){
                        if(i>=tempMap.size()) break;
                        int cost = tempMap.get(keys[i]);
                        if(shop.hasSale(keys[i])!=null){//There is a sale on that item.
                            int pcnt = 100 - shop.getSale(keys[i]);
                            cost *= pcnt/100f;
                        }
                        sender.sendMessage(i + " - "+ keys[i].formattedString(cost/100f,shop.hasSale(keys[i])));
                    }
                    if(page*maxitems < keys.length){//Not last
                        sender.sendMessage(LangPack.MOREITEMSONPAGE + ChatColor.YELLOW + (page + 1));
                    }
                        
                } else {
                    sender.sendMessage(ChatColor.RED + LangPack.THEREARENTTHATMANYPAGES);
                }
            } else {
                sender.sendMessage(ChatColor.RED + LangPack.THEREARENOPRICESSETFORTHISSTORE);
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + LangPack.THEREARENOPRICESSETFORTHISSTORE);
            return false;
        }
        return true;
    }
    
    /**
     * Performs cart checkout and payment for purchased items.
     * @param player The player that is going to pay for purchased items.
     * @param invs All inventories a player have.
     * @param discounts List of discount coupons
     * @return true if this command was executed correctly.
     */
    public static boolean pay(Player player, Inventory[] invs, List<Coupon> discounts) {
        if (RealShopping.hasPInv(player)) {
            RSPlayerInventory pinv = RealShopping.getPInv(player);
            Shop shop = pinv.getShop();
            if (shop.hasPrices()) {
                double toPay = pinv.toPay(invs);
                if (toPay == 0) {
                    return false;
                }
                //insert coupon discounts
                int discount_percent = 0;
                double discount = 0.;
                if(discounts != null && discounts.size() > 0) {
                    discount = 0;
                }
                toPay = (toPay - (toPay*discount_percent/100d));
                toPay = (toPay > discount ? toPay - discount : 0);
                if (RSEconomy.getBalance(player.getName()) < toPay / 100d) {
                    player.sendMessage(ChatColor.RED + LangPack.YOUCANTAFFORDTOBUYTHINGSFOR + toPay / 100d + LangPack.UNIT);
                    return true;
                }
                RSEconomy.withdraw(player.getName(), toPay/100d);
                if(!shop.getOwner().equals("@admin")){
                    RSEconomy.deposit(shop.getOwner(), toPay/100d);//If player owned store, pay player
                    if(RealShopping.getPlayerSettings(player.getName()).getSoldNotifications(shop, toPay/100d))//And send a notification perhaps
                        RealShopping.sendNotification(shop.getOwner(), player.getName() + LangPack.BOUGHTSTUFFFOR + toPay/100d + LangPack.UNIT + LangPack.FROMYOURSTORE + shop.getName() + ".");
                }
                Map<Price, Integer> bought = pinv.getBoughtWait(invs);
                for (Price p : bought.keySet()) {
                    pinv.addBought(p, bought.get(p));
                }

                if (Config.isEnableAI()) {
                    for (Price key : bought.keySet()) {
                        shop.addStat(new Statistic(new MaterialData(key.getType(), key.getData()).getItemType(), key.getName(), bought.get(key), true)); 
                    }
                }

                player.sendMessage(ChatColor.GREEN + LangPack.YOUBOUGHTSTUFFFOR + toPay / 100f + LangPack.UNIT);
                return true;
            } else {
                player.sendMessage(ChatColor.RED + LangPack.THEREARENOPRICESSETFORTHISSTORE);
                return true;
            }
        } else player.sendMessage(ChatColor.RED + LangPack.YOURENOTINSIDEASTORE);
        return false;
    }
        /**
     * Performs cart checkout and payment for purchased items.
     * @param p The player that is going to pay for purchased items.
     * @param invs All inventories a player have.
     * @return true if this command was executed correctly.
     * @deprecated 
     */
    public static boolean pay(Player p, Inventory[] invs){
        return pay(p, invs, null);
    }
    /**
     * Correctly exits a player from a store.
     * This command can be executed only if the player is on a tile marked for "store exit",
     * otherwise it will return false.
     * @param player Player that executed this command.
     * @param cmd True if this command was called from the command line and not from a Listener.
     * @return true when exit is correctly performed, false if a player can't exit.
     */
    public static boolean exit(Player player, boolean cmd){
        if(RealShopping.hasPInv(player)){
            if(!PromptMaster.isConversing(player) && !RSPlayerListener.hasConversationListener(player)){
                Shop tempShop = RealShopping.getPInv(player).getShop();
                if(RealShopping.getPInv(player).hasPaid() || player.getGameMode() == GameMode.CREATIVE || player.getName().equals(tempShop.getOwner())){
                    Location l = player.getLocation().getBlock().getLocation().clone();
                    if(tempShop.hasExit(l)){
                        l = tempShop.getCorrEntrance(l);
                        RealShopping.removePInv(player);
                        removePager(player.getName());
                        l.setPitch(player.getLocation().getPitch());
                        l.setYaw(player.getLocation().getYaw());
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

    /**
     * Brings the player into the store correctly.
     * This command succedes only when the player is ina tile marked as "store entrance", 
     * otherwise it will return false.
     * @param player Player that executed this command.
     * @param cmd True if this command was called from the command line, false if not.
     * @return True when this command executed correctly, false if a player can't perform this action.
     */
    public static boolean enter(Player player, boolean cmd){
        if(!PromptMaster.isConversing(player) && !RSPlayerListener.hasConversationListener(player)){
            Location l = player.getLocation().getBlock().getLocation().clone(); 
            Shop tempShop = RealShopping.isEntranceTo(l);
            if(tempShop != null){//Enter shop
                Location ex = tempShop.getCorrExit(l);
                if(!tempShop.isBanned(player.getName().toLowerCase())){
                    ex.setPitch(player.getLocation().getPitch());
                    ex.setYaw(player.getLocation().getYaw());
                    player.teleport(ex.add(0.5, 0, 0.5));
                    RealShopping.addPInv(new RSPlayerInventory(player, tempShop));
                    player.sendMessage(ChatColor.GREEN + LangPack.YOUENTERED + ChatColor.DARK_GREEN + tempShop.getName());

                    //Refill refillingchests
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
                            for(ItemStack j:tempShop.getChests().get(chestArr[i])){
                                itemStack[k] = j;
                                k++;
                            }
                            chest.getBlockInventory().setContents(itemStack);
                        }
                    }
                        addPager(player.getName());
                    return true;
                } else player.sendMessage(ChatColor.RED + LangPack.YOUAREBANNEDFROM + ChatColor.DARK_RED + tempShop.getName());
            } else if(cmd) player.sendMessage(ChatColor.RED + LangPack.YOURENOTATTHEENTRANCEOFASTORE);
        } else {
            player.sendRawMessage(ChatColor.RED + LangPack.YOU_CANT_DO_THIS_WHILE_IN_A_CONVERSATION);
            player.sendRawMessage(LangPack.ALL_CONVERSATIONS_CAN_BE_ABORTED_WITH_ + ChatColor.DARK_PURPLE + "quit");
        }
        return false;
    }
}
