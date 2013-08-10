package com.github.kuben.realshopping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.kuben.realshopping.exceptions.RealShoppingException;
import com.github.kuben.realshopping.exceptions.RealShoppingException.Type;

public class PSetting {

	public enum FavNots {
		SALES("sales")
		, BROADCASTS("broadcasts")
		, BOTH("both");

		private final String string;
		
		private FavNots(String string){
			this.string = string;
		}
		
		@Override
		public String toString(){
			return string;
		}
	}

	private Set<Shop> favShops;
	
	//Global
	private FavNots favNots;
//	private int getReports;//0 means off, X means X hours interval?
	private int getSoldNots;  //-1 means no, 0 for all items
	private int getBoughtNots;//X means for items over X cost
	private int getAINots;//0 is no, X is TRESHOLD
	private int changeOnAI;//(Ignored if getAINots == 0) 0 means no, X means X%
	
	//Store specific
	private Map<Shop, FavNots> favNotsMap;
//	private Map<Shop, Integer> getReportsMap;
	private Map<Shop, Integer> getSoldNotsMap;
	private Map<Shop, Integer> getBoughtNotsMap;
	private Map<Shop, Integer> getAINotsMap;
	private Map<Shop, Integer> changeOnAIMap;
	
	public PSetting(){
		System.out.println("Creating new PSetting");
		favShops = new HashSet<Shop>();
		
		favNots = FavNots.BOTH;
//		getReports = 0;
		getSoldNots = -1;
		getBoughtNots = 0;
		getAINots = 0;
		changeOnAI = 0;
		
		favNotsMap = new HashMap<Shop, FavNots>();
//		getReportsMap = new HashMap<Shop, Integer>();
		getSoldNotsMap = new HashMap<Shop, Integer>();
		getBoughtNotsMap = new HashMap<Shop, Integer>();
		getAINotsMap = new HashMap<Shop, Integer>();
		changeOnAIMap = new HashMap<Shop, Integer>();
	}
	
	public PSetting(String importStr){
		/*
		 * AAA=sal bro both
		 * ;player-fav1,fav2:AAA:BBB:
		 * 
		 */
	}

	/*
	 * 
	 * Favorite Stores
	 * 
	 */
	
	public boolean addFavoriteStore(Shop shop) throws RealShoppingException {
		if(shop.getOwner().equals(getPlayer())) throw new RealShoppingException(RealShoppingException.Type.CANNOT_FAVORTIE_OWN_SHOP);
		if(favShops.contains(shop)) return false;
		favShops.add(shop);
		return true;
	}
	
	public boolean delFavoriteStore(Shop shop){
		if(!favShops.contains(shop)) return false;
		favShops.remove(shop);
		return true;
	}

	public boolean isFavortite(Shop shop){
		return favShops.contains(shop);
	}
	
	/*
	 * 
	 * Getters directly usable in code
	 * 
	 */
	
	public boolean getSalesNotifications(Shop shop) {
		FavNots o;
		if(favNotsMap.containsKey(shop)) o = favNotsMap.get(shop);
		else o = favNots;
		return o == FavNots.SALES || o == FavNots.BOTH;
	}
	
	public boolean getBroadcastNotifications(Shop shop) {
		FavNots o;
		if(favNotsMap.containsKey(shop)) o = favNotsMap.get(shop);
		else o = favNots;
		return o == FavNots.SALES || o == FavNots.BOTH;
	}
	
	public boolean getSoldNotifications(Shop shop) {
		if(getSoldNotsMap.containsKey(shop)) return getSoldNotsMap.get(shop) >= 0;
		return getSoldNots >= 0;
	}

	public boolean getBoughtNotifications(Shop shop) {
		if(getBoughtNotsMap.containsKey(shop)) return getBoughtNotsMap.get(shop) >= 0;
		return getBoughtNots >= 0;
	}

	public int soldNotificationsOver(Shop shop) {
		if(getSoldNotsMap.containsKey(shop)) return getSoldNotsMap.get(shop);
		return getSoldNots;
	}

	public int boughtNotificationsOver(Shop shop) {
		if(getBoughtNotsMap.containsKey(shop)) return getBoughtNotsMap.get(shop);
		return getBoughtNots;
	}

	public boolean getAINotifications(Shop shop) {
		if(getAINotsMap.containsKey(shop)) return getAINotsMap.get(shop) > 0;
		return getAINots > 0;
	}

	public int AINotsMinStep(Shop shop) {
		if(getAINotsMap.containsKey(shop)) return getAINotsMap.get(shop);
		return getAINots;
	}

	public boolean getChangeAIPriceChange(Shop shop) {
		if(changeOnAIMap.containsKey(shop)) return changeOnAIMap.get(shop) > 0;
		return changeOnAI > 0;
	}

	public int changeOnAIPercentage(Shop shop) {
		if(changeOnAIMap.containsKey(shop)) return changeOnAIMap.get(shop);
		return changeOnAI;
	}

	/*
	 * 
	 * Regular Getters
	 * Map getters have to be Integer, so they can return null
	 */
	
	public FavNots getFavNots() { return favNots; }
//	public int getReports() { return getReports; }
	public int getSoldNots() { return getSoldNots; }
	public int getBoughtNots() { return getBoughtNots; }
	public int getAINots() { return getAINots; }
	public int getChangeOnAI() { return changeOnAI; }
	
	public FavNots getFavNots(Shop shop) { return favNotsMap.get(shop); }
//	public Integer getReports(Shop shop) { return getReportsMap.get(shop); }
	public Integer getSoldNots(Shop shop) { return getSoldNotsMap.get(shop); }
	public Integer getBoughtNots(Shop shop) { return getBoughtNotsMap.get(shop); }
	public Integer getAINots(Shop shop) { return getAINotsMap.get(shop); }
	public Integer getChangeOnAI(Shop shop) { return changeOnAIMap.get(shop); }
	
	/*
	 * 
	 * Setters
	 * 
	 */
	
	public void setFavNots(FavNots favNots) { this.favNots = favNots; }
	public void setFavNots(FavNots favNots, Shop shop) { favNotsMap.put(shop, favNots); }
	public void defaultFavNots(Shop shop){ favNotsMap.remove(shop); }
	
/*	public void setGetReports(int getReports){ this.getReports = getReports; }
	public void setGetReports(int getReports, Shop shop) throws RealShoppingException {
		if(shop.getOwner().equals("@admin")) throw new RealShoppingException(Type.FEATURE_NOT_AVAIABLE_FOR_ADMIN_STORES);
		getReportsMap.put(shop, getReports);
	}
	public void defaultGetReports(Shop shop){ getReportsMap.remove(shop); }*/
	
	public void setGetSoldNots(int getSoldNots) { this.getSoldNots = getSoldNots; }
	public void setGetSoldNots(int getSoldNots, Shop shop) throws RealShoppingException {
		if(shop.getOwner().equals("@admin")) throw new RealShoppingException(Type.FEATURE_NOT_AVAIABLE_FOR_ADMIN_STORES);
		getSoldNotsMap.put(shop, getSoldNots);
	}
	public void defaultGetSoldNots(Shop shop){ getSoldNotsMap.remove(shop); }
	
	public void setGetBoughtNots(int getBoughtNots) { this.getBoughtNots = getBoughtNots; }
	public void setGetBoughtNots(int getBoughtNots, Shop shop) throws RealShoppingException {
		if(shop.getOwner().equals("@admin")) throw new RealShoppingException(Type.FEATURE_NOT_AVAIABLE_FOR_ADMIN_STORES);
		getBoughtNotsMap.put(shop, getBoughtNots);
	}
	public void defaultGetBoughtNots(Shop shop){ getBoughtNotsMap.remove(shop); }
	
	public void setGetAINots(int getAINots) { this.getAINots = getAINots; }
	public void setGetAINots(int getAINots, Shop shop){ getAINotsMap.put(shop, getAINots); }
	public void defaultGetAINots(Shop shop){ getAINotsMap.remove(shop); }
	
	public void setChangeOnAI(int changeOnAI) { this.changeOnAI = changeOnAI; }
	public void setChangeOnAI(int changeOnAI, Shop shop) { changeOnAIMap.put(shop, changeOnAI); }
	public void defaultChangeOnAI(Shop shop){ changeOnAIMap.remove(shop); }
	
	private String getPlayer(){ return RealShopping.getPSettingPlayer(this); }
	
}