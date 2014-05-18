/*
 * RealShopping Bukkit plugin for Minecraft
 * Copyright 2013 Jakub Fojt, 2014 Roberto Benfatto
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

package com.github.stengun.realshopping;
import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.EEPair;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RSPlayerInventory;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.RealShopping;
import static com.github.kuben.realshopping.RealShopping.MANDIR;
import static com.github.kuben.realshopping.RealShopping.VERFLOAT;
import static com.github.kuben.realshopping.RealShopping.loginfo;
import static com.github.kuben.realshopping.RealShopping.logsevere;
import static com.github.kuben.realshopping.RealShopping.logwarning;
import com.github.kuben.realshopping.ShippedPackage;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.Statistic;
import com.github.kuben.realshopping.exceptions.RealShoppingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * This class handles the serialization of objects into configuration files formatted with YAML.
 *
 * @author stengun
 */
public class SerializationManager {
    //TODO Test savejailed / loadjailed
    //TODO Test savetplocs / loadtplocs
    //TODO Test savetoclaim / loadtoclaim
    //TODO Test savestats / loadstats
    //TODO Test savenotifications / loadnotifications
    
    /**
     * This method loads the plugin's configuration.
     * If the configuration file is missing, a default configuration is loaded instead.
     */
    public static void loadConfiguration() {
        Config.initialize();
    }
    
    /**
     * Shop database Loader method.
     * This method will load the entire shop database into realshopping.
     * @return true if succeeded, false otherwise.
     */
    public static boolean loadShops() {
        String path = MANDIR + "shops.db";
        FileConfiguration conf = new YamlConfiguration();
        try {
            File f = new File(path);
            if (!f.exists()) {
                f.createNewFile();
            }
            conf.load(f);
            for(String sh:conf.getKeys(false)) {
                if(sh.equals("version")) continue;
                RealShopping.addShop(SerializationManager.loadShop(conf.getConfigurationSection(sh)));
            }
        } catch (IOException ex) {
            logsevere("IO Error while reading " + path);
            return false;
        } catch (InvalidConfigurationException ex) {
            RealShopping.logwarning("Error loading YAML shops.db, replacing with old shops.db converter.");
            return shopsdbConverter(path);
        } catch (RealShoppingException ex) {
            logwarning("Warning: " + ex.getType().toString());
            return false;
        }
        return true;
    }
    
    /**
     * This method saves the entire shop list with their settings.
     */
    public static void saveShops() {
        FileConfiguration conf = new YamlConfiguration();
        long tstamp = System.nanoTime();
        try {
            File f = new File(MANDIR + "shops.db");
            if(!f.exists()) f.createNewFile();
            conf.set("version", VERFLOAT);
            for(Shop shop:RealShopping.getShops()){
                SerializationManager.saveShop(shop, conf.createSection(shop.getName()));
            }
            conf.save(f);
        } catch (IOException e) {
            RealShopping.logsevere("Error while saving shops.db");
            e.printStackTrace();
        }
        if(Config.debug) loginfo("Finished updating shops.db in " + (System.nanoTime() - tstamp) + "ns");
    }
    /**
     * Correctly saves a shop's item stat into a configuration section.
     * @param stat
     * @param destination 
     */
    public static void saveStatistic(Statistic stat, ConfigurationSection destination) {
        destination.set("itemtype", stat.getItem().toString());
        destination.set("amount", stat.getAmount());
        destination.set("bought", stat.isBought());
        destination.set("name", stat.getName());
    }
    /**
     * Loads a Statistic from a YAML parsed document.
     * @param section section where to take stuff
     * @return Statistic loaded
     */
    public static Statistic loadStatistic(ConfigurationSection section) {
        Material mat = Material.getMaterial(section.getString("itemtype"));
        Statistic st = new Statistic(mat, section.getString("name"), section.getInt("amount"), section.getBoolean("bought"));
        return st;
    }
    
    /**
     * Correctly saves a location.
     * @param loc Location we need to save.
     * @param destination Configuration section where to write.
     */
    public static void saveLocation(Location loc, ConfigurationSection destination) {
        destination.set("world", loc.getWorld().getName());
        destination.set("location", RSUtils.locAsString(loc));
    }
    
    /**
     * Loads a location from a YAML parsed document.
     * @param section Section where to take stuff.
     * @return Location loaded.
     */
    public static Location loadLocation(ConfigurationSection section) {
        return RSUtils.stringToLoc(section.getString("world"),section.getString("location"));
    }
    
    /**
     * Correctly saves a list of pending notifications to a configuration section of a YAML formatted document.
     * @param notification List of notifications as string.
     * @param destination The configuration section ready for save.
     */
    public static void saveNotifications(Collection<String> notification, ConfigurationSection destination) {
        int i = 0;
        for(String s: notification) {
            destination.set(Integer.toString(i++), s);
        }
    }
    
    /**
     * Loads all notifications saved into a configuration section.
     * @param section Section where we need to load notifications.
     * @return List containing all the notifications.
     */
    public static List<String> loadNotifications(ConfigurationSection section) {
        List<String> retval = new ArrayList<>();
        for(String i:section.getKeys(false)) {
            retval.add(section.getString(i));
        }
        return retval;
    }
    
    /**
     * Correctly saves a list of ItemStack objects into a ConfigurationSection.
     * @param items list of items that needs to be saved.
     * @param destination Section where to save items to.
     */
    public static void saveItemStackList(Collection<ItemStack> items, ConfigurationSection destination) {
        int i = 0;
        for(ItemStack it : items) {
            if(it == null) continue;
            destination.set(Integer.toString(i++), it);
        }
    }
    
    /**
     * Loads a list of ItemStack objects from a ConfigurationSection.
     * @param section Where to read the saved list.
     * @return A correctly build List of Itemstack Objects.
     */
    public static List<ItemStack> loadItemStack(ConfigurationSection section) {
        List<ItemStack> stacks = new LinkedList<>();
        for (String key : section.getKeys(false)) {
            stacks.add((ItemStack) section.get(key));
        }
        return stacks;
    }
    
    /**
     * Gives a YAML formatted string conversion ready to be saved into a file.
     * @param inv Inventory object to convert.
     * @return String YAML formatted of Inventory Object.
     */
    public static String saveInventory(RSPlayerInventory inv) {
        YamlConfiguration config = new YamlConfiguration();
        // Save every element in the list
        saveInventory(inv, config);
        return config.saveToString();
    }

    /**
     * Saves a RSPlayerInventory to a configuration section for YAML parser.
     * @param inv Inventory to save
     * @param destination ConfigurationSection where to save inventory.
     */
    public static void saveInventory(RSPlayerInventory inv, ConfigurationSection destination) {
        destination.set("store", inv.getShop().getName());

        ConfigurationSection bought = destination.createSection("bought");
        int i = 0;
        for (Price p : inv.getBought().keySet()) {
            savePriceMap(p, bought.createSection(Integer.toString(i++)), inv.getBought().get(p));
        }

        ConfigurationSection contents = destination.createSection("contents");
        i = 0;
        for (Price p : inv.getItems().keySet()) {
            savePriceMap(p, contents.createSection(Integer.toString(i++)), inv.getItems().get(p));
        }
    }
    
    /**
     * Loads a RSPlayerInventory from a loaded ConfigurationSection.
     * @param source ConfigurationSection where to read.
     * @return Correctly built RSPlayerInventory object.
     */
    public static RSPlayerInventory loadInventory(ConfigurationSection source) {
        String player = source.getName();
        String store = source.getString("store");

        ConfigurationSection bgt = source.getConfigurationSection("bought");
        Map<Price, Integer> bought = loadPriceMap(bgt);

        ConfigurationSection itms = source.getConfigurationSection("contents");
        Map<Price, Integer> items = loadPriceMap(itms);

        return new RSPlayerInventory(player, RealShopping.getShop(store), bought, items);
    }

    /**
     * Saves a shipped package to a YAML ready string.
     *
     * @param pack
     * @return The entire YAML string for this ShippedPackage.
     */
    public static String saveShippedPackage(ShippedPackage pack) {
        YamlConfiguration config = new YamlConfiguration();
        // Save every element in the list
        saveShippedPackage(pack, config);
        return config.saveToString();
    }

    /**
     * Saves a ShippedPackage to a configuration section for YAML parser.
     *
     * @param pack Shipped package to save
     * @param destination The configuration section of config file.
     */
    public static void saveShippedPackage(ShippedPackage pack, ConfigurationSection destination) {
        destination.set("cost", pack.getCost());

        ConfigurationSection location = destination.createSection("location");
        location.set("x", pack.getLocationSent().getX());
        location.set("y", pack.getLocationSent().getY());
        location.set("z", pack.getLocationSent().getZ());
        location.set("world", pack.getLocationSent().getWorld().getName());

        ConfigurationSection contents = destination.createSection("contents");
        // Save every element in the list
        saveItemStackList(Arrays.asList(pack.getContents()), contents);
    }

    /**
     * Loads a ShippedPackage from a loaded configuration section.
     *
     * @param source Configuration section where to load the ShippedPackage.
     * @return A ShippedPackage object built from configuration file.
     */
    public static ShippedPackage loadShippedPackage(ConfigurationSection source) {
        long date = Long.parseLong(source.getName());
        int cost = source.getInt("cost");
        ConfigurationSection location = source.getConfigurationSection("location");
        Location loc = new Location(Bukkit.getWorld(location.getString("world")), location.getDouble("x"), location.getDouble("y"), location.getDouble("z"));

        ConfigurationSection contents = source.getConfigurationSection("contents");
        return new ShippedPackage(loadItemStack(contents).toArray(new ItemStack[0]), cost, loc, date);
    }
    /**
     * Saves a Shop object inside a section of a YAML formatted document.
     * The data we save is composed by shop's name, owner, world, an eventual buyfor,
     * all of its eepairs and banned players.
     * @param shop Shop we want to save.
     * @param destination Section of the document
     */
    public static void saveShop(Shop shop,ConfigurationSection destination) {
        
        destination.set("owner",shop.getOwner());
        destination.set("buyfor",shop.getBuyFor());
        destination.set("world",shop.getWorld());
        
        ConfigurationSection banned = destination.createSection("banned");
        String temp = "";
        for(String b : shop.getBanned()) {
            temp = temp + (temp.equals("")?b:";" + b);
        }
        banned.set("names", temp);
        int i=0;
        
        ConfigurationSection eepairs = destination.createSection("eepairs");
        for(EEPair ee:shop.getEEpairSet()){
            ConfigurationSection tmp = eepairs.createSection(Integer.toString(i++));
            saveEEPair(ee, tmp);
        }
        
        ConfigurationSection chests = destination.createSection("protectedchests");
        i=0;
        for(Location lo:shop.getProtectedChests()) {
            saveLocation(lo, chests.createSection(Integer.toString(i++)));
        }
        
        ConfigurationSection refill = destination.createSection("refillchests");
        i=0;
        for(Location lo : shop.getChests().keySet()) {
            ConfigurationSection tmp = refill.createSection(Integer.toString(i++));
            saveLocation(lo, tmp.createSection("location"));
            saveItemStackList(shop.getChests().get(lo), tmp.createSection("contents"));
        }
    }
    /**
     * Given a Configuration section, this method will try to load a Shop object from it.
     * @param section Section where whe want to load the shop object.
     * @return Returns a correctly built shop object.
     * @throws RealShoppingException If EEPairs are duplicated when inserting, this exception is thrown.
     */
    public static Shop loadShop(ConfigurationSection section) throws RealShoppingException {
        String name = section.getName();
        String owner = section.getString("owner");
        String world = section.getString("world");
        Shop shop = new Shop(name, world, owner);
        
        int buyfor = section.getInt("buyfor");
        shop.setBuyFor(buyfor);
        
        ConfigurationSection banlist = section.getConfigurationSection("banned");
        for(String banned:banlist.getString("names").split(";")) {
            shop.addBanned(banned);
        }
        
        ConfigurationSection eepairs = section.getConfigurationSection("eepairs");
        for(String key : eepairs.getKeys(false)) {
            shop.addEEPair(loadEEPair(eepairs.getConfigurationSection(key)));
        }
        
        ConfigurationSection chests = section.getConfigurationSection("protectedchests");
        for(String chs:chests.getKeys(false)) {
            shop.addProtectedChest(loadLocation(chests.getConfigurationSection(chs)));
        }
        
        ConfigurationSection refill = section.getConfigurationSection("refillchests");
        for(String in : refill.getKeys(false)){
            ConfigurationSection tmp = refill.getConfigurationSection(in);
            Location l = loadLocation(tmp.getConfigurationSection("location"));
            shop.addChest(l);
            shop.addChestItem(l, loadItemStack(tmp.getConfigurationSection("contents")));
        }
        return shop;
    }
    
    // -----------------------  Private static methods.
    private static EEPair loadEEPair(ConfigurationSection pairs) {
        Location entrance = loadLocation(pairs.getConfigurationSection("entrance"));
        Location exit = loadLocation(pairs.getConfigurationSection("exit"));
        return new EEPair(entrance, exit);
    }
    
    private static void saveEEPair(EEPair ee,ConfigurationSection destination) {
        saveLocation(ee.getEntrance(),destination.createSection("entrance"));
        saveLocation(ee.getExit(), destination.createSection("exit"));
    }
    
    private static void savePriceMap(Price p, ConfigurationSection bought, Integer amount) {
        bought.set("type", p.getType());
        bought.set("data", p.getData());
        bought.set("iamount", p.getAmount());
        bought.set("amount", amount);
        bought.set("metahash", p.getMetaHash());
        if (p.hasDescription()) {
            bought.set("description", p.getDescription());
        }
    }

    private static Map<Price, Integer> loadPriceMap(ConfigurationSection itms) {
        Map<Price, Integer> retval = new HashMap<>();

        for (String s : itms.getKeys(false)) {
            int type = itms.getConfigurationSection(s).getInt("type");
            byte data = (byte) itms.getConfigurationSection(s).getInt("data");
            int iamount = itms.getConfigurationSection(s).getInt("iamount");
            int amount = itms.getConfigurationSection(s).getInt("amount");
            int metahash = itms.getConfigurationSection(s).getInt("metahash");
            String description = itms.getConfigurationSection(s).getString("description");
            Price p = new Price(type, data, metahash);
            p.setDescription(description);
            p.setAmount(iamount);
            retval.put(p, amount);
        }

        return retval;
    }

    private static boolean shopsdbConverter(String path) {
        try {
            File f = new File(path);
            if(!f.exists()) f.createNewFile();
            BufferedReader br;
            Float version;
            try (FileInputStream fstream = new FileInputStream(f)) {
                br = new BufferedReader(new InputStreamReader(fstream));
                String s;
                String header = "Shops database for RealShopping v";
                version = 0f;
                boolean notHeader = false;
                while ((s = br.readLine()) != null){// Read shops.db
                    if(version == 0 && s.substring(0, header.length()).equals(header)){
                        version = Float.parseFloat(s.substring(header.length()));
                        notHeader = version.equals(VERFLOAT);
                        continue;
                    }
                    if(notHeader) {
                        String[] tS = s.split(";")[0].split(":");
                        Shop tempShop = new Shop(tS[0], tS[1], (version >= 0.20)?tS[2]:"@admin");
                        if(version >= 0.30) tempShop.setBuyFor(Integer.parseInt(tS[3]));
                        /*
                        * For versions 0.40 through 0.50 notifications and AI settings for stores are ignored.
                        * The shop owner will just have to set them again, using /rsme.
                        */
                        for(int i =
                                (version >= 0.51f)?4:
                                (version >= 0.40f)?8:
                                (version >= 0.30f)?4:
                                (version >= 0.20f)?3:2
                                ;i < tS.length;i++){//The entrances + exits
                            String[] tSS = tS[i].split(",");
                            Location en = new Location(Bukkit.getServer().getWorld(tS[1]), Integer.parseInt(tSS[0]),Integer.parseInt(tSS[1]), Integer.parseInt(tSS[2]));
                            Location ex = new Location(Bukkit.getServer().getWorld(tS[1]), Integer.parseInt(tSS[3]),Integer.parseInt(tSS[4]), Integer.parseInt(tSS[5]));
                            try {
                                tempShop.addEntranceExit(en, ex);
                            } catch (RealShoppingException e) {
                                if(e.getType() == RealShoppingException.Type.EEPAIR_ALREADY_EXISTS){
                                    loginfo("Duplicate entrance/exit pair, skipping (entrance: "
                                            + RSUtils.locAsString(en) + ", exit: "
                                            + RSUtils.locAsString(ex) + " in store " + tS[1] + ".");
                                    if(Config.debug) e.printStackTrace();
                                } else e.printStackTrace();
                            }
                        }
                        for(int i = 1;i < s.split(";").length;i++){//There are chests
                            Location l = new Location(Bukkit.getServer().getWorld(tS[1]), Integer.parseInt(s.split(";")[i].split("\\[")[0].split(",")[0])
                                    , Integer.parseInt(s.split(";")[i].split("\\[")[0].split(",")[1])
                                    , Integer.parseInt(s.split(";")[i].split("\\[")[0].split(",")[2]));
                            tempShop.addChest(l);
                            String idS = s.split(";")[i].split("\\[")[1].split("\\]")[0];
                            if(!idS.split(",")[0].trim().equals("")){
                                int[][] ids = new int[idS.split(",").length][3];
                                for(int j = 0;j < ids.length;j++){//The chests
                                    if(idS.split(",")[j].contains(":")){
                                        ids[j][0] = Integer.parseInt(idS.split(",")[j].split(":")[0].trim());
                                        ids[j][1] = Integer.parseInt(idS.split(",")[j].split(":")[1].trim());
                                        if(idS.split(",")[j].split(":").length > 2)
                                            ids[j][2] = Integer.parseInt(idS.split(",")[j].split(":")[2].trim());
                                        else ids[j][2] = 0;
                                    } else {
                                        ids[j][0] = Integer.parseInt(idS.split(",")[j].trim());
                                        ids[j][1] = 0;
                                        ids[j][2] = 0;
                                    }
                                }
                                List<ItemStack> ret = new ArrayList<>();
                                for(int[] zot : ids) {
                                    ItemStack tmp = new ItemStack((new MaterialData(zot[0], (byte)zot[1])).getItemType(), zot[3]);
                                    ret.add(tmp);
                                }
                                tempShop.addChestItem(l, ret);
                            }
                        }
                        int bIdx = s.indexOf("BANNED_");
                        if(bIdx > -1){//There are banned players
                            String[] banned = s.substring(bIdx + 7).split(",");
                            for (String banned1 : banned) {
                                tempShop.addBanned(banned1);
                            }
                        }
                        RealShopping.addShop(tempShop);
                    }
                }
            }
            br.close();
            saveShops();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            loginfo("Failed while reading/converting shops.db, File not found.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            loginfo("Failed while reading/converting shops.db, IO error.");
            return false;
        }
        return true;
    }
}
