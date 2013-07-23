/*
 * RealShopping Bukkit plugin for Minecraft
 * Copyright 2013 Roberto Benfatto
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

import com.github.kuben.realshopping.Price;
import static com.github.kuben.realshopping.RealShopping.MANDIR;
import com.github.kuben.realshopping.Shop;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * 
 * Price.xml parser implemented with DOM xml parser.
 * This class is a replacement for realshopping's SAX parser, implemented for parsing prices.xml.
 * this class uses the same code as original realshopping saver, it's only recoded to fit new save format.
 * @author stengun
 */
public class PriceParser {
    static final String outputEncoding ="UTF-8";
    
    //Static utils
    public static void loadPriceMap(Map<String,Shop> shopmap) throws ParserConfigurationException, SAXException, IOException{
        File f = new File(MANDIR+"prices.xml");
        if(!f.exists()){
            f.createNewFile();
            return;
        }
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(f);
        doc.getDocumentElement().normalize();
        
        NodeList shops = doc.getElementsByTagName("shop");
        for(int i=0;i<shops.getLength();i++){
            if (shops.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            parseShop((Element)shops.item(i),shopmap);
        }
    }
    
    public static void savePriceMap(Map<String, Shop> shopmap) throws ParserConfigurationException, FileNotFoundException, TransformerConfigurationException, TransformerException {
        File f = new File(MANDIR+"prices.xml");//Reset file
        //if(f.exists()) f.delete();

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement("prices");
        doc.appendChild(root);
        doc.appendChild(doc.createComment("If you want to manually edit this file, do it when your server is down. Your changes won't be saved otherwise!"));

        Map<Price, Integer[]> shopPrices;
        Object[] keys = shopmap.keySet().toArray();
        for(Object key:keys){	
            Element shop = doc.createElement("shop");
            shop.setAttribute("name", shopmap.get((String)key).getName());
            root.appendChild(shop);

            shopPrices = shopmap.get((String)key).getPricesMap();
            Object[] ids = shopPrices.keySet().toArray();
            for(Object obj:ids){
                Price obitm = (Price)obj;
                Element item = doc.createElement("item");
                item.setAttribute("id", obitm.toString());
                Integer[] p = shopPrices.get(obitm);
                item.setAttribute("cost", (((float)p[0])/100) + "");//Save as decimal numbers
                if(p.length == 3){
                        item.setAttribute("min", (((float)p[1])/100) + "");
                        item.setAttribute("max", (((float)p[2])/100) + "");
                }
                
                Element metahash = doc.createElement("meta");
                metahash.setTextContent((Integer.toString(obitm.getMetaHash())));
                item.appendChild(metahash);
                
                if(obitm.hasDescription()){
                    Element itemname = doc.createElement("description");
                    itemname.setTextContent(obitm.getDescription());
                    item.appendChild(itemname);
                }
                shop.appendChild(item);

            }
        }

        DOMSource source = new DOMSource(doc);

        PrintStream ps = new PrintStream(MANDIR+"prices.xml");
        StreamResult result = new StreamResult(ps);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(source, result);
    }
    
    // Private members
    private static void parseShop(Element shop, Map<String,Shop> shopmap){
        if(shopmap.get(shop.getAttribute("name")) == null) return;
        NodeList prices = shop.getElementsByTagName("item");
        if(!shop.hasChildNodes()) return;
        for(int i=0;i<prices.getLength();i++){
            if (prices.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Object[] pprice = parsePrices( (Element)prices.item(i));
            shopmap.get(shop.getAttribute("name")).getPricesMap().put((Price)pprice[0], (Integer[])pprice[1]);
        }
    }
    
    private static Object[] parsePrices(Element price) {
        Integer[] itemcost;
        int itemid = 0;
        byte itemdata = -1;
        String desc = null;
        int metahash;
        
        //costs setting
        List<Integer> icos = new ArrayList<>();
        if(price.hasAttribute("cost")) icos.add((int)Float.parseFloat(price.getAttribute("cost"))*100);
        if(price.hasAttribute("min")) icos.add((int)Float.parseFloat(price.getAttribute("min"))*100);
        if(price.hasAttribute("max")) icos.add((int)Float.parseFloat(price.getAttribute("max"))*100);
        itemcost = icos.toArray(new Integer[0]);
        //id setting
        if(price.getAttribute("id") != null){
            itemid = Integer.parseInt(price.getAttribute("id").split(":")[0]);
            itemdata = Byte.parseByte(price.getAttribute("id").split(":")[1]);
        }
        metahash = Integer.parseInt(price.getElementsByTagName("meta").item(0).getTextContent());
        if(price.getElementsByTagName("description").getLength()!=0) desc = price.getElementsByTagName("description").item(0).getTextContent();

        Price p = new Price(itemid,itemdata,metahash);
        p.setDescription(desc);
        return new Object[]{p,itemcost};
    }
}
