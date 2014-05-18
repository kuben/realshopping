/*
 * RealShopping Bukkit plugin for Minecraft
 * Copyright 2013 Jakub Fojt, Copyright 2014 Roberto Benfatto
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

import com.github.kuben.realshopping.LangPack;

/**
 *
 * @author Roberto Benfatto
 */
public enum DiscountType {
    ALLITEMS (LangPack.GLOBALDISCOUNT),
    ITEM ( LangPack.ITEMDISCOUNT),
    ITEMTYPE (LangPack.TYPEDISCOUNT),
    QUANTITY (LangPack.QUANTITYDISCOUNT);
    
    String easyname;
    DiscountType(String easyname) {
        this.easyname = easyname;
    }
    
    @Override
    public String toString() {
        return easyname;
    }
}
