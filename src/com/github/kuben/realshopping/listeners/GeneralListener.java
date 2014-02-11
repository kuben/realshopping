package com.github.kuben.realshopping.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.exceptions.RSListenerException;
import com.github.kuben.realshopping.exceptions.RSListenerException.Type;
import org.bukkit.Material;

abstract class GeneralListener{
    private Shop shop;
    private Player player;

    public GeneralListener(Player player) throws RSListenerException{
        if(RealShopping.hasPInv(player)){
            shop = RealShopping.getPInv(player).getShop();
            if((shop.getOwner().equals("@admin") && player.hasPermission("realshopping.rsset"))
                        || shop.getOwner().equals(player.getName()))
            {
                this.player = player;
                //Now adds itself to set, or throws exception if failing
                if(!RSPlayerListener.addConversationListener(this)) throw new RSListenerException(player, Type.PLAYER_ALREADY_HAS_LISTENER);
            } else throw new RSListenerException(player, Type.NOT_ALLOWED_MANAGE);
        } else throw new RSListenerException(player, Type.NOT_IN_SHOP);
    }

    public GeneralListener(Player player, String store) throws RSListenerException{//Isn't and shouldn't be in store
        if(!RealShopping.hasPInv(player))
        {
            shop = RealShopping.getShop(store);//Can be null at this point
            //Doesn't check for permissions
            this.player = player;
            //Now adds itself to set, or throws exception if failing
            if(!RSPlayerListener.addConversationListener(this)) throw new RSListenerException(player, Type.PLAYER_ALREADY_HAS_LISTENER);
        } else throw new RSListenerException(player, Type.IN_SHOP);
    }

    //Only for chest listeners as of 0.50
    protected void blockChange(Location block, Material mat) {
        Thread t = new Thread() {	
            private Location block;
            private Material mat;

            public void run(){
                try {
                    Thread.sleep(20);
                    getPlayer().sendBlockChange(block, mat, (byte) 0);
                    Thread.sleep(1000);//To be sure
                    getPlayer().sendBlockChange(block, mat, (byte) 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            private Thread init(Location block, Material mat){
                this.block = block;
                this.mat = mat;
                return this;
            }
        }.init(block,mat);
        t.start();
    }

    Shop getShop(){ return shop; }
    Player getPlayer(){ return player; }

    abstract void onEvent(Event event);
}

interface SignalReceiver {
    Object receiveSignal(Object sig) throws RSListenerException;
}

interface Appliable {
    int apply();
}