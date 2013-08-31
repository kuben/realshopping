package com.github.kuben.realshopping.commands;

import com.github.kuben.realshopping.Shop;
import org.bukkit.command.CommandSender;

class RSPay extends RSPlayerCommand {

    public RSPay(CommandSender sender, String[] args) {
        super(sender, args);
    }

    @Override
    protected boolean execute() {
        return Shop.pay(player, null);
    }
}