package com.github.kuben.realshopping.commands;

import java.util.Locale;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.kuben.realshopping.PSetting;
import com.github.kuben.realshopping.Config;
import com.github.kuben.realshopping.Shop;
import com.github.kuben.realshopping.RealShopping;
import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.RSUtils;
import com.github.kuben.realshopping.exceptions.RealShoppingException;

/**
 * This class represents the <i>/rsme</i> command.
 * 
 * @author kuben
 */
class RSMe extends RSPlayerCommand {

    private final ChatColor RD = ChatColor.RED;
    private final ChatColor DR = ChatColor.DARK_RED;
	private final ChatColor LP = ChatColor.LIGHT_PURPLE;
	private final ChatColor DP = ChatColor.DARK_PURPLE;
	private final ChatColor GR = ChatColor.GREEN;
	private final ChatColor DG = ChatColor.DARK_GREEN;
	private final ChatColor RESET = ChatColor.RESET;
	
	private PSetting settings = null;
	private final String SETTINGS = ChatColor.RESET + "The settings are: "
			+ LP + "favnots" + ChatColor.RESET + ", "
			+ LP  + "soldnots" + ChatColor.RESET + ", "
			+ LP  + "boughtnots" + ChatColor.RESET + ", "
            + LP  + "reports" + ChatColor.RESET + ", "
            + LP  + "ainots" + ChatColor.RESET + ", "
			+ LP  + "changeonai" + ChatColor.RESET + ".";
	
	public RSMe(CommandSender sender, String[] args) {
		super(sender, args);
	}

	//TODO add settings to rsstores (X:timely spending limit for buying)
	//TODO idea: notify about store favorited (implement this into reports)
	//TODO add clear and list
	//TODO check AI with config
	//TODO add reading notifications command
	//TODO view favorites
	
	@Override
	protected boolean execute() {
		String sett, val = "";
		try {
			//Args are at least 2 (help)
			if(args[0].equalsIgnoreCase("addfav")) {
				if(addFav()) player.sendMessage(GR + "Added store " + DG + args[1] + GR + " to your favorites.");
				else player.sendMessage(ChatColor.RED + "Store " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " was already one of your favorites.");
				return true;
			} else if(args[0].equalsIgnoreCase("delfav")) {
				if(delFav()) player.sendMessage(GR + "Removed store " + DG + args[1] + GR + " from your favorites.");
				else player.sendMessage(ChatColor.RED + "Store " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " wasn't one of your favorites.");
				return true;
			}
			
			else {
				Shop shop = null;
				
				if(args[0].equalsIgnoreCase("set")){
					sett = args[1].toLowerCase(Locale.ENGLISH);
					if(args.length > 2) val = args[2];
				} else if(args[0].equalsIgnoreCase("setone")){
					if(args.length < 3){
						player.sendMessage("Not enought arguments or something");//TODO
						return false;
					}

					sett = args[2].toLowerCase(Locale.ENGLISH);
					if(args.length > 3) val = args[3];
					shop = RealShopping.getShop(args[1]);
					if(shop == null) throw new RealShoppingException(RealShoppingException.Type.SHOP_DOESNT_EXIST, args[1]);
				} else return false;
				
				settings = RealShopping.getPlayerSettings(player.getName());
				
				switch(sett){
					case "favnots":
						player.sendMessage(favNots(val, shop));
						break;
					case "soldnots":
						player.sendMessage(soldNots(val, shop));
						break;
					case "boughtnots":
						player.sendMessage(boughtNots(val, shop));
						break;
					case "reports":
					    player.sendMessage(reports(val, shop));
					    break;
					case "ainots":
						player.sendMessage(aiNots(val, shop));
						break;
					case "changeonai":
						player.sendMessage(changeOnAI(val, shop));
						break;
					default:
						throw new RealShoppingException(RealShoppingException.Type.NOT_VALID_ARGUMENT, "set:" + sett);
				}
				
			}
		} catch (RealShoppingException e){
			if(e.getType() == RealShoppingException.Type.NOT_VALID_ARGUMENT){
				String info = e.getAdditionalInfo();
				if(info != null && info.length() > 4){
					if(info.substring(0, 4).equals("set:")){
						player.sendMessage(ChatColor.DARK_RED + info.substring(4) + " is not one of the settings. " + SETTINGS);
						if(Config.debug == true) e.printStackTrace();
						return true;
					} else if(info.substring(0, 4).equals("val:")){
						player.sendMessage(ChatColor.DARK_RED + info.substring(4) + " is not an accepted value.");
						if(Config.debug == true) e.printStackTrace();
						return true;
					} else e.printStackTrace();
				} else e.printStackTrace();
			} else if(e.getType() == RealShoppingException.Type.CANNOT_FAVORTIE_OWN_SHOP){
				player.sendMessage(ChatColor.RED + "You can't favorite your own store.");
				return true;
			} else if(e.getType() == RealShoppingException.Type.FEATURE_NOT_AVAIABLE_FOR_ADMIN_STORES){
				player.sendMessage(ChatColor.RED + "This feature is not avaiable for admin stores.");
				return true;
			} else if(e.getType() == RealShoppingException.Type.SHOP_DOESNT_EXIST){
				player.sendMessage(ChatColor.RED + LangPack.STORE + ChatColor.DARK_RED + e.getAdditionalInfo() + ChatColor.RED + LangPack.DOESNTEXIST);
				return true;
			} else if(e.getType() == RealShoppingException.Type.SYNTAX_ERROR) return false;
			else e.printStackTrace();
		} catch (NumberFormatException e){
			player.sendMessage(ChatColor.DARK_RED + val + ChatColor.RED + LangPack.ISNOTANINTEGER);
		}
		
		return false;
	}
	
	/**
	 * Finds the shop named in args[1] and adds it to the favorite shops set of the player.
	 *
	 * @return true if shop wasn't present in the set before, false otherwise.
	 * @throws RealShoppingException type SHOP_DOESNT_EXIST if the shop doesn't exist in RealShopping.shopMap.
	 * @throws RealShoppingException type CANNOT_FAVORITE_OWN_SHOP if the player tries to favorite their own store.
	 */
	private boolean addFav() throws RealShoppingException {
		Shop shop = RealShopping.getShop(args[1]);
		if(shop == null) throw new RealShoppingException(RealShoppingException.Type.SHOP_DOESNT_EXIST, args[1]);
		return RealShopping.getPlayerSettings(player.getName()).addFavoriteStore(shop);
	}
	
	/**
	 * Finds the shop named in args[1] and removes it from the favorite shops set of the player.
	 *
	 * @return true if shop was present and was removed from the set, false otherwise.
	 * @throws RealShoppingException type SHOP_DOESNT_EXIST if the shop doesn't exist in RealShopping.shopMap.
	 */
	private boolean delFav() throws RealShoppingException {
		Shop shop = RealShopping.getShop(args[1]);
		if(shop == null) throw new RealShoppingException(RealShoppingException.Type.SHOP_DOESNT_EXIST, args[1]);
		return RealShopping.getPlayerSettings(player.getName()).delFavoriteStore(shop);
	}
	
	/**
	 * Defines what kind of notifications to get from favorite stores.
	 *
	 * @param val The string the player typed as an argument which is to be parsed to a value, or an empty string if the <b>VALUE</b> argument was omitted.
	 * @param shop The shop which the setting is for (when using <i>/rsme setone</i>) or null when it is a global setting.
	 * @return the string which is to be sent in a message to the player, informing them of the result.
	 * @throws RealShoppingException type SYNTAX_ERROR a wrong keyword is used as value.
	 * @throws RealShoppingException type NOT_VALID_ARGUMENT if the value is not valid.
	 */
	private String favNots(String val, Shop shop) throws RealShoppingException {
		switch(val){
			case "":
				String status;
				if(shop == null) status = ". Currently set to " + LP + settings.getFavNots() + RESET + ".";
				else {
					String s = settings.getFavNots(shop) == null?"default":settings.getFavNots(shop).toString();
					status = "." + DG + " Currently set to " + LP + s + DG + " for store " + ChatColor.YELLOW + shop.getName() + DG + ".";
				}
				return "Sets what kind of notifications to get from the stores you favorited; " + LP + "sales" + RESET +" for notifications about sales, "
					+ LP + "broadcasts" + RESET + " for messages from the store owner, or " + LP + "both" + RESET + status;
			case "default":
				if(shop == null) throw new RealShoppingException(RealShoppingException.Type.SYNTAX_ERROR);
				settings.defaultFavNots(shop);
				return GR + LangPack.STORE + DG + shop.getName() + GR + " now using default value.";
			default:
				String suffix;
				if(shop == null) suffix = "your favorite stores.";
				else suffix = "store " + DG + shop.getName() + GR + ".";

				switch(val.toLowerCase(Locale.ENGLISH)){
					case "sales":
						if(shop == null) settings.setFavNots(PSetting.FavNots.SALES);
						else settings.setFavNots(PSetting.FavNots.SALES, shop);
						return GR + "Now receiving notifications of sales in " + suffix;
					case "broadcasts":
						if(shop == null) settings.setFavNots(PSetting.FavNots.BROADCASTS);
						else settings.setFavNots(PSetting.FavNots.BROADCASTS, shop);
						return GR + "Now receiving broadcasts from " + suffix;
					case "both":
						if(shop == null) settings.setFavNots(PSetting.FavNots.BOTH);
						else settings.setFavNots(PSetting.FavNots.BOTH);
						return GR + "Now receiving broadcasts and notifications of sales in " + suffix;
					default:
						throw new RealShoppingException(RealShoppingException.Type.NOT_VALID_ARGUMENT, "val:" + val);
				}
		}
	}
		
	/**
	 * Sets whether or not the player will get notifications of sold items from his store(s), and if for all items or only items over a certain cost.
	 *
	 * @param val The string the player typed as an argument which is to be parsed to a value, or an empty string if the <b>VALUE</b> argument was omitted.
	 * @param shop The shop which the setting is for (when using <i>/rsme setone</i>) or null when it is a global setting.
	 * @return the string which is to be sent in a message to the player, informing them of the result.
	 * @throws RealShoppingException type SYNTAX_ERROR a wrong keyword is used as value.
	 * @throws NumberFormatException when val is expected to be an integer, but isn't.
	 */
	private String soldNots(String val, Shop shop) throws NumberFormatException, RealShoppingException {
		switch(val){
			case "":
				String status;
				if(shop == null){
					String s = settings.getSoldNots() == -1?"no":(settings.getSoldNots() == 0?"all":settings.getSoldNots()+"");
					status = "." + DG + " Currently set to " + LP + s + RESET + DG + ".";
				} else {
					Integer i = settings.getSoldNots(shop);
					String s = (String)((i == null)?"default":(i == -1?"no":(i == 0?"all":i)));
					status = "." + DG + " Currently set to " + LP + s + DG + " for store " + ChatColor.YELLOW + shop.getName() + DG + ".";
				}
				return "Sets if you will get notifications about sold items. Options are: " + LP + "all" + RESET +", "
					+ LP + "no" + RESET + ", and any integer for notifications only over a certain cost" + status;
			case "default":
				if(shop == null) throw new RealShoppingException(RealShoppingException.Type.SYNTAX_ERROR);
				settings.defaultGetSoldNots(shop);
				return LangPack.STORE + DG + shop.getName() + GR + " now using default value.";
			default:
				String suffix;
				if(shop == null) suffix = "your stores.";
				else suffix = "store " + DG + shop.getName() + GR + ".";
			
				switch(val.toLowerCase(Locale.ENGLISH)){
					case "all":
						if(shop == null) settings.setGetSoldNots(0);
						else settings.setGetSoldNots(0, shop);
						return GR + "Now receiving notifications about all items sold by " + suffix;
					case "no":
						if(shop == null) settings.setGetSoldNots(-1);
						else settings.setGetSoldNots(-1, shop);
						return GR + "No longer receiving notifications about items sold by " + suffix;
					default:
						int i;
						if(shop == null) settings.setGetSoldNots(i = Integer.parseInt(val));
						else settings.setGetSoldNots(i = Integer.parseInt(val), shop);
						return GR + "Now receiving notifications about items over " + i + LangPack.UNIT + " sold by " + suffix;
				}
		}
	}
	
	/**
	 * Sets whether or not the player will get notifications of bought items from his store(s), and if for all items or only items over a certain cost.
	 *
	 * @param val The string the player typed as an argument which is to be parsed to a value, or an empty string if the <b>VALUE</b> argument was omitted.
	 * @param shop The shop which the setting is for (when using <i>/rsme setone</i>) or null when it is a global setting.
	 * @return the string which is to be sent in a message to the player, informing them of the result.
	 * @throws RealShoppingException type SYNTAX_ERROR a wrong keyword is used as value.
	 * @throws NumberFormatException when val is expected to be an integer, but isn't.
	 */
	private String boughtNots(String val, Shop shop) throws NumberFormatException, RealShoppingException {
		switch(val){
			case "":
				String status;
				if(shop == null){
					String s = settings.getBoughtNots() == -1?"no":(settings.getBoughtNots() == 0?"all":settings.getBoughtNots()+"");
					status = "." + DG + " Currently set to " + LP + s + RESET + DG + ".";
				} else {
					Integer i = settings.getBoughtNots(shop);
					String s = (String)((i == null)?"default":(i == -1?"no":(i == 0?"all":i)));
					status = "." + DG + " Currently set to " + LP + s + DG + " for store " + ChatColor.YELLOW + shop.getName() + DG + ".";
				}
				return "Sets if you will get notifications about bought items. Options are: " + LP + "all" + RESET +", "
					+ LP + "no" + RESET + ", and any integer for notifications only over a certain cost" + status;
			case "default":
				if(shop == null) throw new RealShoppingException(RealShoppingException.Type.SYNTAX_ERROR);
				settings.defaultGetBoughtNots(shop);
				return LangPack.STORE + DG + shop.getName() + GR + " now using default value.";
			default:
				String suffix;
				if(shop == null) suffix = "your stores.";
				else suffix = "store " + DG + shop.getName() + GR + ".";
			
				switch(val.toLowerCase(Locale.ENGLISH)){
					case "all":
						if(shop == null) settings.setGetBoughtNots(0);
						else settings.setGetBoughtNots(0, shop);
						return GR + "Now receiving notifications about all items bought by " + suffix;
					case "no":
						if(shop == null) settings.setGetBoughtNots(-1);
						else settings.setGetBoughtNots(-1, shop);
						return GR + "No longer receiving notifications about items bought by " + suffix;
					default:
						int i;
						if(shop == null) settings.setGetBoughtNots(i = Integer.parseInt(val));
						else settings.setGetBoughtNots(i = Integer.parseInt(val), shop);
						return GR + "Now receiving notifications about items over " + i + LangPack.UNIT + " bought by " + suffix;
				}
		}
	}
	
    private String reports(String val, Shop shop) throws NumberFormatException, RealShoppingException {
        switch(val){
            case "":
                String status;
                if(shop == null){
                    String s = settings.getReports() == 0?"no":settings.getReports()+"";
                    status = "." + DG + " Currently set to " + LP + s + RESET + DG + ".";
                } else {
                    Integer i = settings.getReports(shop);
                    String s = (String)((i == null)?"default":(i == 0?"no":i));
                    status = "." + DG + " Currently set to " + LP + s + DG + " for store " + ChatColor.YELLOW + shop.getName() + DG + ".";
                }
                String INTERVAL = RSUtils.secsToDHMS(Config.getReporterPeriod());
                return "Sets if Reports are enabled, and how often reports should be sent. The standard interval for this server is "
                    + INTERVAL + " and reports for your store can be sent at any multiplier of the standard integer."
                    + "Options are: " + LP + "no" + RESET +", or any integer to enable and interval to the integer." + status;
            case "default":
                if(shop == null) throw new RealShoppingException(RealShoppingException.Type.SYNTAX_ERROR);
                settings.defaultGetAINots(shop);
                return LangPack.STORE + DG + shop.getName() + GR + " now using default value.";
            default:
                String suffix;//Technically not a suffix
                if(shop == null) suffix = "your stores";
                else suffix = "store " + DG + shop.getName() + GR;
                
                switch(val.toLowerCase(Locale.ENGLISH)){
                    case "no":
                        if(shop == null) settings.setGetReports(0);
                        else settings.setGetReports(0, shop);
                        return GR + "Reports disabled for " + suffix + ".";
                    default:
                        int i = Integer.parseInt(val);
                        if(i <= 0) return RD + "Error: The interval has to be an integer higher than zero.";
                        if(shop == null) settings.setGetReports(i);
                        else settings.setGetReports(i, shop);
                        return GR + "Reports enabled, and will run every " + RSUtils.formatNum(i) + " interval for " + suffix + ".";
                }
        }
    }
	
	/**
	 * Sets whether or not the Automatic Store Management is enabled, and how many places a store needs to gain or lose in popularity for the player to be notified.
	 *
	 * @param val The string the player typed as an argument which is to be parsed to a value, or an empty string if the <b>VALUE</b> argument was omitted.
	 * @param shop The shop which the setting is for (when using <i>/rsme setone</i>) or null when it is a global setting.
	 * @return the string which is to be sent in a message to the player, informing them of the result.
	 * @throws RealShoppingException type SYNTAX_ERROR a wrong keyword is used as value.
	 * @throws NumberFormatException when val is expected to be an integer, but isn't.
	 */
	private String aiNots(String val, Shop shop) throws NumberFormatException, RealShoppingException {
		switch(val){
			case "":
				String status;
				if(shop == null){
					String s = settings.getBoughtNots() == 0?"no":settings.getBoughtNots()+"";
					status = "." + DG + " Currently set to " + LP + s + RESET + DG + ".";
				} else {
					Integer i = settings.getBoughtNots(shop);
					String s = (String)((i == null)?"default":(i == 0?"no":i));
					status = "." + DG + " Currently set to " + LP + s + DG + " for store " + ChatColor.YELLOW + shop.getName() + DG + ".";
				}
				return "Sets if Automatic Store Management is enabled, and how many places your store has to become more/less popular for you to get notified. " +
					"Options are: " + LP + "no" + RESET +", or any integer to enable and set the treshold to the integer" + status;
			case "default":
				if(shop == null) throw new RealShoppingException(RealShoppingException.Type.SYNTAX_ERROR);
				settings.defaultGetAINots(shop);
				return LangPack.STORE + DG + shop.getName() + GR + " now using default value.";
			default:
				String suffix;//Technically not a suffix
				if(shop == null) suffix = "your stores";
				else suffix = "store " + DG + shop.getName() + GR;
				
				switch(val.toLowerCase(Locale.ENGLISH)){
					case "no":
						if(shop == null) settings.setGetAINots(0);
						else settings.setGetAINots(0, shop);
						return GR + "Automatic store management disabled for " + suffix + ".";
					default:
						int i;
						if(shop == null) settings.setGetAINots(i = Integer.parseInt(val));
						else settings.setGetAINots(i = Integer.parseInt(val), shop);
						return GR + "Automatic store management enabled, and will notify you when " + suffix + " become(s) " + i + " places more or less popular.";
				}
		}
	}
	
	/**
	 * Sets whether or not the Automatic Store Management will change PrintPrices automatically, and if yes - how many percent.
	 *
	 * @param val The string the player typed as an argument which is to be parsed to a value, or an empty string if the <b>VALUE</b> argument was omitted.
	 * @param shop The shop which the setting is for (when using <i>/rsme setone</i>) or null when it is a global setting.
	 * @return the string which is to be sent in a message to the player, informing them of the result.
	 * @throws RealShoppingException type SYNTAX_ERROR a wrong keyword is used as value.
	 * @throws NumberFormatException when val is expected to be an integer, but isn't.
	 * @see #aiNots(String, Shop)
	 */
	private String changeOnAI(String val, Shop shop) throws NumberFormatException, RealShoppingException {
		switch(val){
			case "":
				String status;
				if(shop == null){
					String s = settings.getBoughtNots() == 0?"no":settings.getBoughtNots()+"";
					status = "." + DG + " Currently set to " + LP + s + DG + ".";
				} else {
					Integer i = settings.getBoughtNots(shop);
					String s = (String)((i == null)?"default":(i == 0?"no":i));
					status = "." + DG + " Currently set to " + LP + s + DG + " for store " + ChatColor.YELLOW + shop.getName() + DG + ".";
				}
				return "(also see " + LP + "ainots" + RESET + ") Sets if the Automatic Store Management (if enabled), will automaticly change your prices instead of just notifying you. " +
					"Options are: " + LP + "no" + RESET + ", or any integer to change the prices that many percent" + status;
			case "default":
				if(shop == null) throw new RealShoppingException(RealShoppingException.Type.SYNTAX_ERROR);
				settings.defaultChangeOnAI(shop);
				return LangPack.STORE + DG + shop.getName() + GR + " now using default value.";
			default:
				String suffix;
				if(shop == null) suffix = "your stores.";
				else suffix = "store " + DG + shop.getName() + GR + ".";
				
				switch(val.toLowerCase(Locale.ENGLISH)){
					case "no":
						if(shop == null) settings.setChangeOnAI(0);
						else settings.setChangeOnAI(0, shop);
						return GR + "Automatic store management no longer changing prices for " + suffix;
					default:
						int i;
						if(shop == null) settings.setChangeOnAI(i = Integer.parseInt(val));
						else settings.setChangeOnAI(i = Integer.parseInt(val), shop);
						return GR + "Automatic store management set to lower/raise prices by " + i + "% for ";
				}
		}
	}

	@Override
	protected Boolean help(){
		//Check if help was asked for
		if(args.length == 0){
			sender.sendMessage(DG + LangPack.USAGE + RESET + "/rsme addfav|delfav STORE");
			sender.sendMessage("OR /rsme set SETTING [VALUE]");
			sender.sendMessage("OR /rsme setone STORE SETTING [VALUE]");
			sender.sendMessage(LangPack.FORHELPTYPE + LP + "/rsme help ");
			return true;
		} else {
			if(args[0].equalsIgnoreCase("help"))
				if(args.length == 1){
					sender.sendMessage("Manages your favorite and your own stores and settings for them.");
					sender.sendMessage(DG + LangPack.USAGE + RESET + "/rsme addfav|delfav STORE");
					sender.sendMessage("OR /rsme set SETTING [VALUE]");
					sender.sendMessage("OR /rsme setone STORE SETTING [VALUE]");
					sender.sendMessage(LangPack.FOR_HELP_FOR_A_SPECIFIC_COMMAND_TYPE_ + LP + "/rsme help " + DP + "COMMAND");
					return true;
				} else {
					switch(args[1].toLowerCase(Locale.ENGLISH)){
						case "addfav":
							sender.sendMessage(DG + LangPack.USAGE + LP + "/rsme addfav " + DP + "STORE" + RESET + ". Adds " + DP
									+ "STORE" + RESET + " to your favorite stores. You can't favorite your own store.");
							break;
						case "delfav":
							sender.sendMessage(DG + LangPack.USAGE + LP + "/rsme delfav " + DP + "STORE" + RESET + ". Removes " + DP + "STORE" + RESET + " from your favorite stores.");
							break;
						case "set":
							sender.sendMessage(DG + LangPack.USAGE + LP + "/rsme set " + DP + "SETTING " + LP + "[" + DP + "VALUE" + LP + "]" + RESET + ". Sets the " + DP + "SETTING" + RESET + " to " + DP + "VALUE"
									+ RESET + " or shows the currently set value when " + DP + "VALUE" + RESET + " is omitted.");
							sender.sendMessage(SETTINGS);
							break;
						case "setone":
							sender.sendMessage(DG + LangPack.USAGE + LP + "/rsme setone " + DP + "STORE SETTING " + LP + "[" + DP + "VALUE" + LP + "]" + RESET + ". Same as " + LP + "/rsset set" + RESET + " but for one store only.");
							sender.sendMessage(SETTINGS);
							break;
						default:
							return false;
					}
					return true;
				}
			else if(args.length > 1) return null;
			else return false;
		}
	}
	
}