package com.edatasource.receipts.parser.groupon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edatasource.receipts.helper.ParserUtil;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import com.edatasource.receipts.parser.AddressParser;

public class GrouponPurchaseParserV2 {

	public static void main(String[] args) {
		File input = new File("./app-receipts/test-data/groupon/valid_v2/V2_regexUpdate1.html");
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			parse(doc);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

	}

	public static EcommerceReceipt parse(Document doc) throws XPathExpressionException {
		EcommerceShipment shipment = new EcommerceShipment();
		EcommerceItem item = new EcommerceItem();
		shipment.addItem(item);
		List<EcommerceShipment> shipments = new ArrayList<>();
		shipments.add(shipment);
		EcommerceReceipt receipt = new EcommerceReceipt();
		receipt.setShipments(shipments);

		/* Find all td tags */
		Elements tds = doc.getElementsByTag("td");

		for (Element element : tds) {

			/* Take td tags without style attribute. */

			if (!element.hasAttr("style")) {

				/*
				 * Take td tag, which content is beginning from numbers, which
				 * points that this field is street or box info.
				 */

				if (element.text().matches("^[\\d].*")) {
					receipt.getShipping().setStreetOrBoxInfo(element.text());
				}

				/*
				 * Take td tag, which content ends with pattern 5 and
				 * then 4 digit numbers, separated with dash, which points at postal code field. Then
				 * the whole field is separated into three sub-fields, which is
				 * going for city, state, postal code respectively.
				 */

				if (element.text().matches(".*[0-9]{5}(?:-[0-9]{4})$")) {
					String[] elements = element.text().split(",");
					String[] subelements = elements[1].split(" ");
					receipt.getShipping().setCity(elements[0]);
					receipt.getShipping().setState(subelements[1]);
					receipt.getShipping().setPostalCode(subelements[2]);
				}
			}
		}

		/*
		 * Retrieve td tags, which has defined style. It'd be easier and more
		 * accurate to do via xPath, but Jsoup doesn't support it directly.
		 * Totally, in order to retrieve right content, none of the regular
		 * expressions can be applied to these fields.
		 */

		Elements descriptionElement = doc.getElementsByAttributeValueContaining("style",
				"padding: 10px 0 10px 20px; font-weight:bold;");
		String descriptionValue = null;
		for (Element element : descriptionElement) {
			descriptionValue = Jsoup.parse(element.toString()).text();
		}

		receipt.getShipments().get(0).getItems().get(0).setDescription(descriptionValue);

		/* Retrieve td tags with price content via defined tag styles */

		Elements priceElement = doc.getElementsByAttributeValueContaining("style", "text-align:right;");
		Elements totalElements = doc.getElementsByAttributeValue("style", "text-align:right; font-weight:bold;");
		Element totalElement = null;
		for (Element element : totalElements) {
			totalElement = element;
		}

		for (Element element : priceElement) {
			if (Jsoup.parse(element.toString()).text().contains("$")) {

				/*
				 * Retrieving td tags with price content and special symbol $.
				 * Depending on defined style it will be order total value or
				 * sub total value.
				 */

				if (element.equals(totalElement)) {
					receipt.setOrderTotal(ParserUtil.dollarsToDouble(element.text()));
				} else {
					receipt.setOrderSubTotal(ParserUtil.dollarsToDouble(element.text()));
				}
			}
		}
		return receipt;
	}

}
