package com.github.stengun.realshopping;

import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RSPlayerInventory;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.ShippedPackage;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * This class helps with serialization of specific objects that cannot be saved because
 * they don't implement Serialization interface. 
 *
 * @author stengun
 */
public class ClassSerialization {
    
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
     * @throws InvalidConfigurationException
     */
    public static ShippedPackage loadShippedPackage(ConfigurationSection source) {
        long date = Long.parseLong(source.getName());
        int cost = source.getInt("cost");
        ConfigurationSection location = source.getConfigurationSection("location");
        Location loc = new Location(Bukkit.getWorld(location.getString("world")), location.getDouble("x"), location.getDouble("y"), location.getDouble("z"));

        ConfigurationSection contents = source.getConfigurationSection("contents");
        return new ShippedPackage(loadItemStack(contents).toArray(new ItemStack[0]), cost, loc, date);
    }

    
    // -----------------------  Private static methods.
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
}
