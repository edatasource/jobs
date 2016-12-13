package com.edatasource.receipts.parser.groupon;

import com.edatasource.receipts.helper.ParserUtil;
import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import org.jsoup.nodes.Document;

import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import org.jsoup.nodes.Element;

public class GrouponPurchaseParserV2{

	public static EcommerceReceipt parse(Document doc)  {
		EcommerceReceipt ecommerceReceipt = new EcommerceReceipt();
		EcommerceShipment ecommerceShipment = new EcommerceShipment();
		EcommerceItem ecommerceItem = new EcommerceItem();
		Address address = new Address();
		String ecommerceItemDescription = null;
		Double orderSubTotal = null;
		Double orderTotal = null;

		Element contentTable = doc
				.select("table table table table > tbody > tr > td").first()
				.getElementsByTag("table").get(2)
				.select("tr > td table table > tbody").first();

		boolean addressCell = false;
		int rowNum = 0;
		for (Element e : contentTable.children()) {
			rowNum++;
			try {
				if (rowNum == 3) {
					ecommerceItemDescription = e.children().first().text();
				}
				if (rowNum == 4) {
					orderSubTotal = ParserUtil.dollarsToDouble(e.select("td table tr").get(0).children().get(1).text());
					orderTotal = ParserUtil.dollarsToDouble(e.select("td table tr").get(3).children().get(1).text());
				}
				if (e.html().matches("<td.*?>Shipping address:</td>")) {
					addressCell = true;
				}
				if (addressCell) {
					String streetOrBoxInfo = e.select("td table tr").get(1).children().first().text();
					String cityStatePostal = e.select("td table tr").get(3).children().first().text();
					address.setStreetOrBoxInfo(streetOrBoxInfo);
					address.setCity(cityStatePostal.split(",")[0]);
					address.setState(cityStatePostal.replaceAll("(?:.*?,\\s)(.*?)(?:\\s\\d{5}-\\d{4}).?", "$1"));
					address.setPostalCode(cityStatePostal.replaceAll(".*(\\d{5}-\\d{4}).?", "$1").trim());
					addressCell = false;
				}
			} catch (NullPointerException npe) {
			}
		}
		ecommerceShipment.setShipping(address);
		ecommerceItem.setDescription(ecommerceItemDescription);
		ecommerceShipment.addItem(ecommerceItem);
		ecommerceReceipt.addShipment(ecommerceShipment);
		ecommerceReceipt.setOrderSubTotal(orderSubTotal);
		ecommerceReceipt.setOrderTotal(orderTotal);
		return ecommerceReceipt;
	}
}