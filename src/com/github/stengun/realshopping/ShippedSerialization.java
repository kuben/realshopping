package com.github.stengun.realshopping;

import com.github.kuben.realshopping.ShippedPackage;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
/**
 * This class helps with ShippedPackage serialization.
 * All of this work is based upon aadnk's work located at https://gist.github.com/aadnk/4593947
 * @author stengun
 */
public class ShippedSerialization {
    /**
     * Saves a shipped package to a YAML ready string.
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
     * Saves a ShippedPackage to a configuration section of a YAML file.
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
        ItemStack[] inventory = pack.getContents();
        // Save every element in the list
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            // Don't store NULL entries
            if (item != null) {
                contents.set(Integer.toString(i), item);
            }
        }
    }
    /**
     * Loads a ShippedPackage from a loaded configuration section.
     * @param source Configuration section where to load the ShippedPackage.
     * @return A ShippedPackage object built from configuration file.
     * @throws InvalidConfigurationException 
     */
    public static ShippedPackage loadShippedPackage(ConfigurationSection source) throws InvalidConfigurationException {
        long date = Long.parseLong(source.getName());
        int cost = source.getInt("cost");
        ConfigurationSection location = source.getConfigurationSection("location");
        Location loc = new Location(Bukkit.getWorld(location.getString("world")), location.getDouble("x"), location.getDouble("y"), location.getDouble("z"));
        
        ConfigurationSection contents = source.getConfigurationSection("contents");
        List<ItemStack> stacks = new LinkedList<>();
        for (String key : contents.getKeys(false)) {                
            stacks.add((ItemStack)contents.get(key));
        }
        
        return new ShippedPackage(stacks.toArray(new ItemStack[0]), cost, loc, date);
    }    
}
