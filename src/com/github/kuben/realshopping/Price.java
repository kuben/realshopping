/*
 * RealShopping Bukkit plugin for Minecraft
 * Copyright 2013 Jakub Fojt
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

package com.github.kuben.realshopping;

public final class Price {
	private int type;
	private int data;
	
	public Price(int type){
		this.type = type;
		this.data = -1;
	}
	
	public Price(int type, int data){
		this.type = type;
		this.data = data;
	}
	
	public Price(String s){
		this.type = Integer.parseInt(s.split(":")[0]);
		this.data = s.split(":").length==1?-1:Integer.parseInt(s.split(":")[1]);
	}

	public int getType() {
		return type;
	}

	public int getData() {
		return data;
	}
	
	@Override
	public String toString() {
		return type + (data > -1?":"+data:"");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Price other = (Price) obj;
		if (data != other.data)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}