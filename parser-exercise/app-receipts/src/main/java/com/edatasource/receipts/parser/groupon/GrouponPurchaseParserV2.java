package com.edatasource.receipts.parser.groupon;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;

public class GrouponPurchaseParserV2 {

	public static EcommerceReceipt parse(Document doc) {
		String desc = doc.select("tr:matches(Order summary:) + tr + tr > td:eq(0)").outerHtml();
		desc = desc.substring(desc.indexOf(">") + 1, desc.indexOf("</td>")).trim();

		EcommerceReceipt receipt = new EcommerceReceipt();
		
		// set description
		EcommerceShipment shipment = new EcommerceShipment();
		List<EcommerceItem> items = new ArrayList<EcommerceItem>();
		shipment.setItems(items);
		EcommerceItem item = new EcommerceItem();
		item.setDescription(desc);
		items.add(item);
		List<EcommerceShipment> shipmentList = new ArrayList<EcommerceShipment>();
		shipmentList.add(shipment);
		receipt.setShipments(shipmentList);
		
		// set subtotal
		String subtotal = doc.select("td:matchesOwn(Subtotal) + td").text().replace("$", "");
		receipt.setOrderSubTotal(Double.parseDouble(subtotal));
		
		// set total
		String total = doc.select("td:matchesOwn(Total) + td").text().replace("$", "");
		receipt.setOrderTotal(Double.parseDouble(total));
		
		// set address
		Elements wholeAddress = doc.select("tr:matches(Shipping address:) + tr");
		Address address = MyAddressParser.parse(wholeAddress);
		shipment.setShipping(address);
		
		return receipt;
	}

	public static void main(String[] args) {
		try {
			byte[] encoded = Files
					.readAllBytes(Paths
							.get("./app-receipts/test-data/groupon/valid_v2/V2_regexUpdate1.html"));
			String HTML = new String(encoded, Charset.defaultCharset());
			Document doc = Jsoup.parse(HTML);
			EcommerceReceipt receipt = GrouponPurchaseParserV2.parse(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
