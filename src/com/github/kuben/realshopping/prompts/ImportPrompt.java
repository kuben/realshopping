package com.github.kuben.realshopping.prompts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

import com.github.kuben.realshopping.LangPack;
import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RealShopping;

public class ImportPrompt extends ValidatingPrompt {
	 
    public String getPromptText(ConversationContext context) {
    	String in = (String) context.getSessionData("data");
    	if(in.equals("first")){
    		String out = LangPack.WHICHFILEDOYOUWANTTOIMPORT_;
    		File dirP = new File(RealShopping.MANDIR);
    		File[] Mlist = null, Plist = null;
    		File dirM = new File("./");
    		if(dirP.isDirectory()){
    			Mlist = dirM.listFiles(new FilenameFilter(){
    			    public boolean accept(File dir, String name) {
    			        return (name.endsWith(".xlsx"));
    			    }
    			});
    		}
    		if(dirP.isDirectory()){
    			Plist = dirP.listFiles(new FilenameFilter(){
    			    public boolean accept(File dir, String name) {
    			        return (name.endsWith(".xlsx"));
    			    }
    			});
    		}
    		if((Mlist == null || Mlist.length == 0) && (Plist == null || Plist.length == 0))
    			return LangPack.ERROR_NO_XLSX_;
    		if(Mlist != null && Mlist.length > 0){
    			out += ChatColor.DARK_GREEN + LangPack.INTHEMAINDIRECTORY;
    			for(int i = 1;i <= Mlist.length;i++){
    				out += " " + ChatColor.LIGHT_PURPLE + i + ")" + ChatColor.WHITE + Mlist[i-1].getName() + " ";
    	    	}
    		}
    		if(Plist != null && Plist.length > 0){
    			out += ChatColor.DARK_GREEN + LangPack.INTHEREALSHOPPINGDIRECTORY;
    			for(int i = 1;i <= Plist.length;i++){
        			out += " " + ChatColor.LIGHT_PURPLE + i + ")" + ChatColor.WHITE + Plist[i-1].getName() + " ";
        		}
    		}
    		out += LangPack.TYPETHECORRESPONDINGNUMBER_ + ChatColor.LIGHT_PURPLE + "c" + ChatColor.WHITE + LangPack.TOCANCEL;
    		context.setSessionData("mlist", Mlist);
    		context.setSessionData("plist", Plist);
    		return out;
    	} else {
    		int num = -1;
    		try{
    			num = Integer.parseInt((String)context.getSessionData("data"));
    		} catch (NumberFormatException e){
    			return LangPack.ERROR_INPUTISNOTAVALIDINTEGER;
    		}
    		if(num > 0){
        		if(context.getSessionData("mlist") != null && context.getSessionData("plist") != null){
        			File[] Mlist = (File[]) context.getSessionData("mlist");
        			File[] Plist = (File[]) context.getSessionData("plist");
        			if(num <= Mlist.length + Plist.length){
        				String chosen = "";
        				if(num <= Mlist.length) chosen = Mlist[num - 1].getPath();
        				else chosen = Plist[num -1 - Mlist.length].getPath();
        				context.setSessionData("file", chosen);
    				    context.setSessionData("final", true);
        				return ChatColor.GREEN + "Chosen file " + chosen + ". "
        						+ ChatColor.WHITE + "Type " + ChatColor.LIGHT_PURPLE + "u" + ChatColor.WHITE + LangPack.TOIMPORT_USERDEFINED_ +
        						ChatColor.LIGHT_PURPLE + "p" + ChatColor.WHITE + LangPack.TOIMPORT_PROPOSITION_;
        			} else return LangPack.WRONGFILECHOSEN;
        		}
    		} else return LangPack.ERROR_INPUTISNOTAVALIDINTEGER;
    	}
        return "Error #1201";
    }
 
    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String in) {
    	if(context.getSessionData("final") != null && context.getSessionData("final").equals(true)){
    		context.setSessionData("data", in);
    		return new FinalPrompt();
    	}
        else context.setSessionData("data", in);
        return this;
    }
 
    @Override
    protected boolean isInputValid(ConversationContext context, String in) {
    	if(context.getSessionData("final") != null && context.getSessionData("final").equals(true)){
    		if(!in.equalsIgnoreCase("p") && !in.equalsIgnoreCase("u")) return false;
    	}
    	
        return true;
    }
 
}

class FinalPrompt extends MessagePrompt{

	public String getPromptText(ConversationContext context) {
    	String in = (String) context.getSessionData("data");
		if(in.equalsIgnoreCase("u") || in.equalsIgnoreCase("p")){
			if(context.getSessionData("file") != null){
	    		try {
				    InputStream inp = new FileInputStream((String)context.getSessionData("file"));
				    XSSFWorkbook wb;

					wb = new XSSFWorkbook(inp);
				    XSSFSheet sheet = wb.getSheetAt(in.equalsIgnoreCase("u")?0:2);
				    Iterator<Row> rowIter = sheet.rowIterator();
				    
				    RealShopping.defPrices.clear();
				    wb.getCreationHelper().createFormulaEvaluator().evaluateAll();
				    while(rowIter.hasNext()){
				    	try {
					    	XSSFRow row = (XSSFRow) rowIter.next();
					    	XSSFCell firstC = row.getCell(0);
					    	int ID = -1;
					    	byte data = 0;
					    	if(firstC != null) if (firstC.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){//ID's are numeric
					    		ID = (int) firstC.getNumericCellValue();
					    	} else if (firstC.getCellType() == XSSFCell.CELL_TYPE_STRING){
					    		ID = Integer.parseInt(firstC.getStringCellValue().split(";")[0]);
					    		data = Byte.parseByte(firstC.getStringCellValue().split(";")[1]);
					    	}
					    	if(ID >= 0){
					    		XSSFCell costC = row.getCell(4);
					    		try{
					    			if(costC != null && costC.getCellType() == XSSFCell.CELL_TYPE_FORMULA){
					    				Price p;
					    				if(data == 0) p = new Price(ID);
					    				else p = new Price(ID, data);
	            						int cost = Float.valueOf(costC.getNumericCellValue() + "").intValue();
					    				Integer[] f = new Integer[]{cost};
					    				RealShopping.defPrices.put(p, f);
					    			}
					    		} catch (Exception e) {}
					    	}
				    	} catch (NumberFormatException e){}//Skip
			        }
					if(RealShopping.defPrices.size() > 0) return ChatColor.GREEN + LangPack.IMPORTED + RealShopping.defPrices.size() + LangPack.PRICESASDEFAULT;
					else return LangPack.ERRORCOULDNTIMPORTPRICES;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
    	}
		return null;
	}

	@Override
	protected Prompt getNextPrompt(ConversationContext context) {
		return END_OF_CONVERSATION;
	}
	
}
