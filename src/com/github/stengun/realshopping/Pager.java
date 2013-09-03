package com.github.stengun.realshopping;

import com.github.kuben.realshopping.RSPlayerInventory;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import org.bukkit.Bukkit;

/**
 * Pager class that stores the page a player is reading from an ItemFrame with
 * paper. This thread must be unique for each player. Default page time is 5
 * seconds. Be aware that this thread must be "notified" after stop variabile
 * set, because it is in wait() and its action is performed every time you
 * notify this.
 *
 * @author stengun
 */
public class Pager extends Thread {

    private int page;
    private boolean stop;
    private long time, stamp;
    private String player;

    public Pager(String player) {
        super();
        page = 1;
        stop = false;
        time = 3000;
        this.player = player;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                stamp = System.currentTimeMillis();
                waitcheck();
                RSPlayerInventory pinv = RealShopping.getPInv(player);
                if(pinv != null){
                    Shop shop = pinv.getShop();
                    long total = System.currentTimeMillis() - stamp;
                    if (total > time) {
                        page = 1;
                    } else {
                        if(page*9 < shop.getPrices().size()) {
                            page += 1;
                        } else {
                            page = 1;
                        }
                    }
                    Shop.prices(Bukkit.getPlayer(player), page, shop);
                } else stop = true;
            } catch (InterruptedException ex) {
                RealShopping.logsevere(ex.getStackTrace().toString());
            }
        }
    }

    private synchronized void waitcheck() throws InterruptedException {
        wait();
    }

    /**
     * Tells this thread to wake up and check for page flip.
     */
    public synchronized void push() {
        notify();
    }

    /**
     * This method sets the time in milliseconds that we must wait before
     * resetting pages to default.
     *
     * @param time Time in milliseconds.
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Gets the current page we're viewing.
     *
     * @return page number, starting from 1.
     */
    public int getPage() {
        return page;
    }

    /**
     * Tells this tread to stop.
     *
     * @param stop true if we want the thread to stop.
     */
    public void setStop(boolean stop) {
        this.stop = stop;
        push();
    }
}