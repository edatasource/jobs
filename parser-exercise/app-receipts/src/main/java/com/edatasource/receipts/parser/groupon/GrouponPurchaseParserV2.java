package com.edatasource.receipts.parser.groupon;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import com.edatasource.receipts.parser.AddressParser;

public class GrouponPurchaseParserV2 {

	public static EcommerceReceipt parse(Document doc) {

		EcommerceReceipt receipt = new EcommerceReceipt();

		// order number from strong tag
		String orderNumber = doc.getElementsByTag("strong").get(0).text().toLowerCase()
				.replace("your order number is", "").trim();
		receipt.setOrderNumber(orderNumber);

		// all td elements of document
		Elements elements = doc.getElementsByTag("td");

		// extraction of td elements with no child only
		ArrayList<String> data = new ArrayList<String>();
		for (Element element : elements) {
			if (element.childNodes().size() == 1)
				data.add(element.text().trim());
		}

		EcommerceShipment shipment = new EcommerceShipment();
		EcommerceItem item = new EcommerceItem();
		Address address = new Address();

		// setting all values of receipt
		for (int i = 0; i < data.size(); i++) {

			// item description and quantity
			if (data.get(i).toLowerCase().contains("order placed on")) {
				item.setDescription(data.get(i + 1));
				int itemQuantity = Integer.valueOf(data.get(i + 2).replaceAll("[(x)]", ""));
				item.setQuantity(itemQuantity);
			}

			// receipt subtotal
			else if (data.get(i).toLowerCase().contains("subtotal")) {
				receipt.setOrderSubTotal(Double.valueOf(data.get(i + 1).replace("$", "")));
			}

			// receipt shippment charges
			else if (data.get(i).toLowerCase().contains("tax")) // receipt tax
			{
				receipt.setOrderTax(Double.valueOf(data.get(i + 1).replace("$", "")));
			}

			// receipt shipment charges
			else if (data.get(i).toLowerCase().contains("shipping & handling")) {
				receipt.setShippingAmount(Double.valueOf(data.get(i + 1).replace("$", "")));
			}

			// receipt total amount
			else if (data.get(i).toLowerCase().equals("total")) {
				receipt.setOrderTotal(Double.valueOf(data.get(i + 1).replace("$", "")));
			}

			// shipping address
			else if (data.get(i).toLowerCase().contains("shipping address")) {
				address = AddressParser.parse(data.get(i+2)+ ", " + data.get(i+3));
			}
		} // end of for-loop

		shipment.addItem(item);
		shipment.setShipping(address);
		receipt.addShipment(shipment);
		return receipt;

	} // end of parse

}
