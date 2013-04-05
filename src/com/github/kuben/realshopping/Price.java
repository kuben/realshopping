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