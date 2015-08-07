package com.edatasource.receipts.parser.groupon;

import com.edatasource.receipts.helper.ParserUtil;
import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import org.jsoup.nodes.Document;

import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import com.edatasource.receipts.parser.AddressParser;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GrouponPurchaseParserV2{

	public static EcommerceReceipt parse(Document doc)  {
            EcommerceReceipt ecm = new EcommerceReceipt();
            
            //base selector
            Elements base = doc.select("table table table table table table");
            //Get Order Number
            ecm.setOrderNumber(base.select("strong").text().replaceAll("\\D+", "").trim());            
            
            //Shipping            
            EcommerceShipment ecs = new EcommerceShipment();
           
            /*Parsing Item Information */
            Address ad1 = new Address();
            
            //Address Base element
            Element a_base = base.select("table table tbody").last();
            
            //setting first and last name
            String fullName = a_base.select("td").first().text();            
            ad1.setFirstName(fullName.split(" ")[0].trim());
            ad1.setLastName(fullName.split(" ")[1].trim());
            
            //setting the street address            
            ad1.setStreetOrBoxInfo(a_base.select("tr").get(1).text());
            
            //set city and state
            String remainAdd = a_base.select("tr").get(3).text();           
            
            ad1.setCity(remainAdd.split(",")[0].trim());
            ad1.setState(remainAdd.split(",")[1].trim().split(" ")[0].trim());
            ad1.setPostalCode(remainAdd.split(",")[1].trim().split(" ")[1].trim());
            
            //set Shipping Address
            ecs.setShipping(ad1);
           
            List<EcommerceShipment> l = new ArrayList<>();
            l.add(ecs);
            
            ecm.setShipments(l);
            
            /*Parsing Item Information */
            EcommerceItem item =new EcommerceItem();           
            //item description
            item.setDescription(base.select("table tbody").first().select("td").get(2).text());
            ecs.addItem(item);
            
            //get Orders Subtotal
            
            ecm.setOrderSubTotal(ParserUtil.dollarsToDouble(base.select("table table td[style=text-align:right;]").get(0).text()));
            ecm.setOrderTax(ParserUtil.dollarsToDouble(base.select("table table td[style=text-align:right;]").get(1).text()));
            ecm.setShippingAmount(ParserUtil.dollarsToDouble(base.select("table table td[style=text-align:right;]").get(2).text()));
            
            //getting total
            ecm.setOrderTotal(ParserUtil.dollarsToDouble(base.select("table table td[style=text-align:right; font-weight:bold;]").get(0).text()));
            System.out.println(ecm.getOrderTotal());
		return ecm;
	}


}
