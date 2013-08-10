package com.github.stengun.realshopping;

import java.util.concurrent.Semaphore;
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
    
    public synchronized void push(){
        notify();
    }
    
    public void setTime(long time){
        this.time = time;
    }
    
    public int getPage(){
        return page;
    }
    
    public void setStop(boolean stop){
        this.stop = stop;
    }
}