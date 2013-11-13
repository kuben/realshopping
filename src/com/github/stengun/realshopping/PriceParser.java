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
package com.github.stengun.realshopping;

import com.github.kuben.realshopping.Price;
import com.github.kuben.realshopping.RealShopping;
import static com.github.kuben.realshopping.RealShopping.MANDIR;
import com.github.kuben.realshopping.Shop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Price.xml parser implemented with DOM xml parser. This class is a replacement
 * for realshopping's SAX parser, implemented for parsing prices.xml. The save
 * format is created to give the ability to save the new Price structure.
 *
 * @author stengun
 */
public class PriceParser {

    static final String outputEncoding = "UTF-8";

    //Static utils
    /**
     * Loads a saved shop/price map from a file.
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static void loadPriceMap() throws ParserConfigurationException, SAXException, IOException {
        File f = new File(MANDIR + "prices.xml");
        if (!f.exists()) {
            f.createNewFile();
            return;
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(f);
        doc.getDocumentElement().normalize();
        if(doc.getDocumentElement().getAttribute("version").equals("") || 
            Float.parseFloat(doc.getDocumentElement().getAttribute("version")) < RealShopping.VERFLOAT) doc = convertPrices(doc);

        NodeList shops = doc.getElementsByTagName("shop");
        for (int i = 0; i < shops.getLength(); i++) {
            if (shops.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            parseShop((Element) shops.item(i));
        }
    }

    /**
     * Saves to file the entire shop/price map.
     *
     * @param shopset Map where to take shops and prices.
     * @throws ParserConfigurationException
     * @throws FileNotFoundException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public static void savePriceMap(Set<Shop> shopset) throws ParserConfigurationException, FileNotFoundException, TransformerConfigurationException, TransformerException {
        //File f = new File(MANDIR + "prices.xml");//Reset file
        //if(f.exists()) f.delete();

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement("prices");
        root.setAttribute("version", Float.toString(RealShopping.VERFLOAT));
        doc.appendChild(root);
        doc.appendChild(doc.createComment("If you want to manually edit this file, do it when your server is down. Your changes won't be saved otherwise!"));

        Map<Price, Integer[]> shopPrices;
        for (Shop s : shopset) {
            Element shop = doc.createElement("shop");
            shop.setAttribute("name", s.getName());
            root.appendChild(shop);

            shopPrices = s.getPricesMap();
            Object[] ids = shopPrices.keySet().toArray();
            for (Object obj : ids) {
                Price obitm = (Price) obj;
                Element item = doc.createElement("item");
                item.setAttribute("id", obitm.getType() + ":" + obitm.getData());
                Integer[] p = shopPrices.get(obitm);
                item.setAttribute("cost", (((float) p[0]) / 100) + "");//Save as decimal numbers
                if (p.length == 3) {
                    item.setAttribute("min", (((float) p[1]) / 100) + "");
                    item.setAttribute("max", (((float) p[2]) / 100) + "");
                }

                Element metahash = doc.createElement("meta");
                metahash.setTextContent((Integer.toString(obitm.getMetaHash())));
                item.appendChild(metahash);
                
                Element amount = doc.createElement("amount");
                amount.setTextContent((Integer.toString(obitm.getAmount())));
                item.appendChild(amount);
                
                if (obitm.hasDescription()) {
                    Element itemname = doc.createElement("description");
                    itemname.setTextContent(obitm.getDescription());
                    item.appendChild(itemname);
                }
                shop.appendChild(item);

            }
        }

        DOMSource source = new DOMSource(doc);

        PrintStream ps = new PrintStream(MANDIR + "prices.xml");
        StreamResult result = new StreamResult(ps);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(source, result);
    }

    // Private membes
    private static void parseShop(Element shop) {
        if (!RealShopping.shopExists(shop.getAttribute("name"))) {
            return;
        }
        NodeList prices = shop.getElementsByTagName("item");
        if (!shop.hasChildNodes()) {
            return;
        }
        for (int i = 0; i < prices.getLength(); i++) {
            if (prices.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Object[] pprice = parsePrices((Element) prices.item(i));
            RealShopping.getShop(shop.getAttribute("name")).getPricesMap().put((Price) pprice[0], (Integer[]) pprice[1]);
        }
    }

    private static Object[] parsePrices(Element price) {
        Integer[] itemcost;
        int itemid = 0;
        byte itemdata = -1;
        String desc = null;
        int metahash;
        int amount = 1;
        //costs setting
        List<Integer> icos = new ArrayList<>();
        if (price.hasAttribute("cost")) {
            icos.add((int) Float.parseFloat(price.getAttribute("cost")) * 100);
        }
        if (price.hasAttribute("min")) {
            icos.add((int) Float.parseFloat(price.getAttribute("min")) * 100);
        }
        if (price.hasAttribute("max")) {
            icos.add((int) Float.parseFloat(price.getAttribute("max")) * 100);
        }
        itemcost = icos.toArray(new Integer[0]);
        //id setting
        if (price.getAttribute("id") != null) {
            itemid = Integer.parseInt(price.getAttribute("id").split(":")[0]);
            if(price.getAttribute("id").split(":").length > 1) {
                itemdata = Byte.parseByte(price.getAttribute("id").split(":")[1]);
            } else {
                itemdata = 0;
            }
        }
        metahash = Integer.parseInt(price.getElementsByTagName("meta").item(0).getTextContent());
        if(price.getElementsByTagName("amount").getLength() > 0){
            amount = Integer.parseInt(price.getElementsByTagName("amount").item(0).getTextContent());
        }
        
        if (price.getElementsByTagName("description").getLength() != 0) {
            desc = price.getElementsByTagName("description").item(0).getTextContent();
        }

        Price p = new Price(itemid, itemdata, metahash);
        p.setDescription(desc);
        p.setAmount(amount);
        return new Object[]{p, itemcost};
    }
    
    private static Document convertPrices(Document actual) throws ParserConfigurationException {
        Element root = actual.getDocumentElement();
        root.setAttribute("version", Float.toString(RealShopping.VERFLOAT));
        
        NodeList shops = actual.getElementsByTagName("shop");
        for(int i = 0; i<shops.getLength();i++) {
            if (shops.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            NodeList prices = ((Element)shops.item(i)).getChildNodes();
            for( int j=0;j<prices.getLength();j++) {
                if (prices.item(j).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                
                Element price = (Element)prices.item(j);
                String id = price.getAttribute("id");
                if(!id.contains(":")) price.setAttribute("id", id+":0");
                
                Element meta = actual.createElement("meta");
                meta.setTextContent("0");
                Element amount = actual.createElement("amount");
                amount.setTextContent("1");
                price.appendChild(meta);
                price.appendChild(amount);
            }
        }
        return actual;
    }
}
