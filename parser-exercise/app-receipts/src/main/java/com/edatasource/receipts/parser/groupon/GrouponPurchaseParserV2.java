package com.edatasource.receipts.parser.groupon;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;

public class GrouponPurchaseParserV2{

	public static EcommerceReceipt parse(Document doc)  {
		
		EcommerceReceipt receipt = new EcommerceReceipt();
		
	        // Order Number
	         for ( Element orderNumberP : doc.select("strong")  ) {
	             for ( Element orderNumber : orderNumberP.select("a")) {
	             	receipt.setOrderNumber(orderNumber.text());
	        	 }
	         }

	         // Product
	         Elements orderSummbaryTable  = doc.getElementsContainingText("Order summary:");
	         for ( Element temp : orderSummbaryTable.select("tr") ) {
				  
				  if ( temp.text().equals("Order summary:") )   {

					  Element temp2 = temp.nextElementSibling();
					  temp2 = temp2.nextElementSibling();
					  temp2 = temp2.select("td").first();
					  System.out.println("Product: "+temp2.tagName()+"  "+temp2.text());
					  
					  EcommerceShipment shippment = new EcommerceShipment();
					  EcommerceItem item = new  EcommerceItem();
					  ArrayList<EcommerceShipment> list= new ArrayList<EcommerceShipment>();
					  
					  item.setDescription(temp2.text());
					  shippment.addItem(item);
					  list.add(shippment);
					  receipt.setShipments(list);
				  }
				  
			  }

	          // Order Details
	          Element subtotalTable2 = null;
	          mainloop:
	          for ( Element temp : orderSummbaryTable.select("tr") ) {
				  if ( temp.text().equals("Order summary:") )   {
			
					  for ( Element temp2 : temp.parent().parent().parent().select("table")) {
						  for ( Element trTemp : temp2.select("tr") )  {
							  for ( Element tdTemp : trTemp.select("td") ) {
								  for ( Element subtotalTable : trTemp.select("table") ) {
	                                  subtotalTable2 = subtotalTable;
	                                  break mainloop;
								  }							  
							  }
						  }
					  }
				  }
			  }
	          
	          for ( Element sutotalTR : subtotalTable2.select("tr") )  {
	        	  for ( Element subtotalTD : sutotalTR.select("td") ) {
	        		  if ( subtotalTD.text().equals("Subtotal") ) {
	        			  Double subtotal = Double.valueOf((subtotalTD.nextElementSibling().text()).substring(1));
	        			  receipt.setOrderSubTotal(subtotal);
	        		  }
	        		  if ( subtotalTD.text().equals("Total") ) {
	        			  Double total = Double.valueOf((subtotalTD.nextElementSibling().text()).substring(1));
	        			  receipt.setOrderTotal(total);
	        		  }
	        	  }
	           }
	        
	        // Shipping Details
	        Elements shippingAddresssTable = doc.getElementsContainingText("Shipping address:");
	        for ( Element trs: shippingAddresssTable.select("tr") ) {
	        	if ( trs.text().equals("Shipping address:") ) {
	        		  Element trTemp = trs.nextElementSibling();
	        		  
	        		  Element shippingDetails = trTemp.select("table").first();
	        		  Elements detailsTemp = shippingDetails.select("tr");
	        		 
	        		  Element name = detailsTemp.first();
	        		  Element street = name.nextElementSibling();
	        		  Element address = street.nextElementSibling();
	        		  address = address.nextElementSibling();
	        		  
	        		  String location = address.text();
	        		  
	        		  String city = location.substring(0,(location.indexOf(",")));
	        		  String stateZip = location.substring(location.indexOf(","));
	        		  String state = stateZip.split(" ")[1];
	        		  String zip = stateZip.split(" ")[2];
	        		  
	        		  Address shipmentAddsress = receipt.getShipping();
            		          shipmentAddsress.setStreetOrBoxInfo(street.text());
	        		  shipmentAddsress.setCity(city);
	        		  shipmentAddsress.setState(state);
	        		  shipmentAddsress.setPostalCode(zip);
	        		  
	        	}
	        }
		return receipt;
	}

   
}
