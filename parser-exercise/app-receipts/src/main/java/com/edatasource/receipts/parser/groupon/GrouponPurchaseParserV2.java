package com.edatasource.receipts.parser.groupon;

import java.math.BigDecimal;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;

public class GrouponPurchaseParserV2 {

	public static EcommerceReceipt parse(Document doc) {

		if (doc == null) {
			return null;
		}

		EcommerceReceipt ecommerceReceipt = new EcommerceReceipt();
		EcommerceShipment ecommerceShipment = new EcommerceShipment();
		EcommerceItem item = new EcommerceItem();
		

		ecommerceShipment.addItem(item);

		Elements elItemDesc = doc.select(
				"body > table > tbody > tr > td:nth-child(2) > table > tbody > tr:nth-child(3) > td > table > tbody > tr > td > table > tbody > tr > td > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table > tbody > tr:nth-child(3) > td:nth-child(1)");

		Elements elSubTotal = doc.select(
				"body > table > tbody > tr > td:nth-child(2) > table > tbody > tr:nth-child(3) > td > table > tbody > tr > td > table > tbody > tr > td > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table > tbody > tr:nth-child(4) > td > table > tbody > tr:nth-child(1) > td:nth-child(2)");

		Elements elTotal = doc.select(
				"body > table > tbody > tr > td:nth-child(2) > table > tbody > tr:nth-child(3) > td > table > tbody > tr > td > table > tbody > tr > td > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table > tbody > tr:nth-child(4) > td > table > tbody > tr:nth-child(4) > td:nth-child(2)");

		Elements elStreetBox = doc.select(
				"body > table > tbody > tr > td:nth-child(2) > table > tbody > tr:nth-child(3) > td > table > tbody > tr > td > table > tbody > tr > td > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table > tbody > tr:nth-child(8) > td > table > tbody > tr:nth-child(2) > td");

		Elements elCityZip = doc.select(
				"body > table > tbody > tr > td:nth-child(2) > table > tbody > tr:nth-child(3) > td > table > tbody > tr > td > table > tbody > tr > td > table:nth-child(2) > tbody > tr > td > table > tbody > tr > td > table > tbody > tr:nth-child(8) > td > table > tbody > tr:nth-child(4) > td");

		String itemDescription = null;
		Double subTotal = null;
		Double total = null;
		String streetBox = null;
		String city = null;
		String state = null;
		String zip = null;

		if (elItemDesc.isEmpty() == false) {
			itemDescription = elItemDesc.first().ownText();
		}

		if (elSubTotal.isEmpty() == false) {
			subTotal = new Double(elSubTotal.first().ownText().replace("$", ""));
		}

		if (elTotal.isEmpty() == false) {
			total = new Double(elTotal.first().ownText().replace("$", ""));
		}

		if (elStreetBox.isEmpty() == false) {
			streetBox = elStreetBox.first().ownText();
		}

		if (elCityZip.isEmpty() == false) {
			String temp = elCityZip.first().ownText();

			int pos = temp.indexOf(", ");

			if (pos != -1) {
				city = temp.substring(0, pos);

				temp = temp.substring(pos + 2);

				pos = temp.indexOf(" ");

				if (pos != -1) {
					state = temp.substring(0, pos);
					zip = temp.substring(pos + 1);

				}
			}

		}
		item.setDescription(itemDescription);
		item.setShipment(ecommerceShipment);

		ecommerceReceipt.setOrderSubTotal(subTotal);
		ecommerceReceipt.setOrderTotal(total);

		Address address = ecommerceReceipt.getShipping();

		address.setStreetOrBoxInfo(streetBox);
		address.setCity(city);
		address.setState(state);
		address.setPostalCode(zip);


		ecommerceReceipt.addShipment(ecommerceShipment);

		return ecommerceReceipt;
	}

}
