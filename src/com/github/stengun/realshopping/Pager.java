package com.github.stengun.realshopping;

import com.github.kuben.realshopping.RSPlayerInventory;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import org.bukkit.Bukkit;

/**
 * Pager class that stores the page a player is reading. This thread must be unique for each player and keeps track
 * of the pages a player reads. When the thread is notified before X seconds (default: 5) it will switch page, 
 * otherwise it will go back to page 1.
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
        page = 0;
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
                stop = true;
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