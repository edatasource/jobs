package com.edatasource.receipts.parser.groupon;

import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;

public class GrouponPurchaseParserV2 {

	public static EcommerceReceipt parse(Document doc)  {
		
		String orderNumber = "";
		String description = "";
		int quantity = 1;
		double orderSubTotal = 0.0;
		double orderTax = 0.0;
		double shippingAmount = 0.0;
		double orderTotal = 0.0;
		
		String firstName = "";
		String lastName = "";
		String postalCode = "";
		String streetOrBoxInfo = "";
		String city = "";
		String state = "";
		
		// parse order number, Select all Hyper Link elements from HTML document
		// Find Hyper link which contains order Number using Regex Pattern by matching numbers only.
		Elements links = doc.select("a");
		Pattern pattern = Pattern.compile(".*[^0-9].*");
		for(Element link : links) {
			String linkText = link.html();
			if(!pattern.matcher(linkText).matches()){
				orderNumber = linkText;
			}
		}
		// parse item description, Select HTML TABLE elements.
		// Find TABLE which contains Order Summary
		Element summaryTable = null;
		Elements tables = doc.select("table");
		for(Element table : tables) {
			Elements tds = table.getElementsByTag("td");
			for(Element td : tds) {
				if(td.html().equalsIgnoreCase("Order summary:")) {
					summaryTable = table; // get Summary Table
					break;
				}
			}
		}
		// IF order summary TABLE matches, Extract data from Order Summary Pages.
		// get All TD of the TABLE and Extract Data from each Column.
		if(summaryTable != null) {
			Elements tds = summaryTable.getElementsByTag("td");
			description = tds.get(2).html();
			quantity = Integer.parseInt(tds.get(3).html().replaceAll("[^\\d]", ""));
			orderSubTotal = Double.parseDouble(tds.get(6).html().replaceAll("[^\\d.]", ""));
			orderTax = Double.parseDouble(tds.get(9).html().replaceAll("[^\\d.]", ""));
			shippingAmount = Double.parseDouble(tds.get(12).html().replaceAll("[^\\d.]", ""));
			orderTotal = Double.parseDouble(tds.get(15).html().replaceAll("[^\\d.]", ""));
			
			String firstLastName = tds.get(21).html();
			firstName = firstLastName.split(" ")[0];
			lastName = firstLastName.split(" ")[1];
			
			streetOrBoxInfo = tds.get(22).html();
			
			String address2String = tds.get(24).html();
			String [] address2Array = address2String.split(",");
			city = address2Array[0];
			state = address2Array[1].split(" ")[1];
			postalCode = address2Array[1].split(" ")[2];
		}
		// Now set the values in Object & return
		EcommerceReceipt receipt = new EcommerceReceipt();
		EcommerceShipment shipment = new EcommerceShipment();
		EcommerceItem item = new EcommerceItem();
		
		item.setDescription(description);
		item.setQuantity(quantity);
		shipment.addItem(item);
		
		
		
		receipt.setOrderNumber(orderNumber);
		receipt.setOrderSubTotal(orderSubTotal);
		receipt.setOrderTax(orderTax);
		receipt.setShippingAmount(shippingAmount);
		receipt.setOrderTotal(orderTotal);
		
		Address shippingAddress = new Address();
		shippingAddress.setFirstName(firstName);
		shippingAddress.setLastName(lastName);
		shippingAddress.setPostalCode(postalCode);
		shippingAddress.setStreetOrBoxInfo(streetOrBoxInfo);
		shippingAddress.setCity(city);
		shippingAddress.setState(state);
		
		shipment.setShipping(shippingAddress);
		receipt.setBilling(shippingAddress);
		receipt.addShipment(shipment);
		return receipt;
	}
}
