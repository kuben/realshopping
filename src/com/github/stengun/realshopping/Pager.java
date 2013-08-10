package com.github.stengun.realshopping;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pager class that stores the page a player is reading from an ItemFrame with paper.
 * This thread must be unique for each player. Default page time is 5 seconds.
 * Be aware that this thread must be "notified" after stop variabile set, because it
 * is in wait() and its action is performed every time you notify this.
 * @author stengun
 */
public class Pager extends Thread{
    
    private int page;
    private boolean stop;
    private long time,stamp;
    
    public Pager(){
        super();
        page = 1;
        stop = false;
        time = 3000;
    }
    
    @Override
    public void run(){
        while(!stop){
            try {
                stamp = System.currentTimeMillis();
                waitcheck();
                long total = System.currentTimeMillis() - stamp;
                if( total > time)
                    page = 1;
                else
                    page +=1;
            } catch (InterruptedException ex) {
                Logger.getLogger(Pager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private synchronized void waitcheck() throws InterruptedException{
        wait();
    }
    
    /**
     * Tells this thread to wake up and check for page flip.
     */
    public synchronized void push(){
        notify();
    }
    
    /**
     * This method sets the time in milliseconds that we must wait before resetting pages to default.
     * @param time Time in milliseconds.
     */
    public void setTime(long time){
        this.time = time;
    }
    
    /**
     * Gets the current page we're viewing.
     * @return page number, starting from 1.
     */
    public int getPage(){
        return page;
    }
    /**
     * Tells this tread to stop.
     * @param stop true if we want the thread to stop.
     */
    public void setStop(boolean stop){
        this.stop = stop;
        push();
    }
}