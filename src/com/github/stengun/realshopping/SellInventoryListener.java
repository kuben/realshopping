package com.github.stengun.realshopping;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This class will listen for all Inventory events for Sell to Store invs.
 * @author stengun
 */
public class SellInventoryListener implements Listener{
    private Map<String,SellInventory> Sellinv_map;

    public SellInventoryListener() {
        super();
        this.Sellinv_map = new HashMap<>();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpenInventory(InventoryOpenEvent event) {
        if(event.getInventory().getName().equals(LangPack.SELLTOSTORE)) {
            Player p = (Player) event.getPlayer();
            Sellinv_map.put(p.getName(), new SellInventory(p,event.getInventory()));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if(event.getInventory().getTitle().equals(LangPack.SELLTOSTORE)) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(LangPack.SELLTOSTORE)) {
            if(event.isShiftClick()) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }
            if(event.getCurrentItem() == null && event.getCursor() == null) {
                return;
            }
            SellInventory sellinv = Sellinv_map.get(event.getWhoClicked().getName());
            
            int slot = event.getRawSlot();
            if(slot >= 0 && slot < 36) {
                switch(event.getAction()) {
                    case PICKUP_ALL:
                    case PICKUP_HALF:
                    case PICKUP_ONE:
                    case PICKUP_SOME:
                    case PLACE_ALL:
                    case PLACE_ONE:
                    case PLACE_SOME:
                    case SWAP_WITH_CURSOR:
                        event.setCancelled(true);
                        ItemStack cursor = event.getCursor().clone();
                        ItemStack clicked = sellinv.getItem(slot);
                        if(clicked != null) clicked = sellinv.getItem(slot).clone();
                        if(cursor.getType() == Material.AIR && (clicked != null && clicked.getType() != Material.AIR)) { //se prendo qualcosa a mani vuote
                            sellinv.removeItem(slot);
                            event.setCursor(clicked);
                            return;
                        }
                        if(cursor.getType() != Material.AIR) { //Se ho le mani piene
                            ItemStack prec = new ItemStack(Material.AIR);
                            if(clicked != null && clicked.getType() != Material.AIR) {
                                prec = clicked;
                                sellinv.removeItem(slot);
                            }
                            event.setCursor(prec);
                            sellinv.setItem(slot, cursor);
                        }
                    default:
                        break;
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getInventory().getTitle().equals(LangPack.SELLTOSTORE)) {
            Player player = (Player) event.getPlayer();
            if (RealShopping.hasPInv(player)) {//If player is in store
                Shop.sellToStore(player, Sellinv_map.get(player.getName()).getContents());
                Sellinv_map.remove(player.getName());
            }
        }
    }
}
