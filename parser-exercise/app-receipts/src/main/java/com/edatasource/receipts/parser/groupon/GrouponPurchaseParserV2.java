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

import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;


public class GrouponPurchaseParserV2{
	
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

	public static EcommerceReceipt parse(Document doc) throws XPathExpressionException{
		EcommerceShipment shipment = new EcommerceShipment();
		EcommerceItem item = new EcommerceItem();
		shipment.addItem(item);
		List<EcommerceShipment> shipments = new ArrayList<>();
		shipments.add(shipment);
		EcommerceReceipt receipt = new EcommerceReceipt();
		receipt.setShipments(shipments);
			
		Elements tds = doc.getElementsByTag("td");
		
		for (Element element : tds) {
			if(!element.hasAttr("style") && element.text().contains(" ")){
				if(element.text().matches("^[\\d].*")){
					receipt.getShipping().setStreetOrBoxInfo(element.text());
				}
				if(element.text().matches(".*[0-9]{5}(?:-[0-9]{4})$")){
					String[] elements = element.text().split(",");
					String[] subelements = elements[1].split(" ");
					receipt.getShipping().setCity(elements[0]);
					receipt.getShipping().setState(subelements[1]);
					receipt.getShipping().setPostalCode(subelements[2]);
				}
			}
		}
		
		Elements descriptionElement = doc.getElementsByAttributeValueContaining("style", "padding: 10px 0 10px 20px; font-weight:bold;");
		String descriptionValue = null;
		for (Element element : descriptionElement) {
			descriptionValue = Jsoup.parse(element.toString()).text();
		}
		
		receipt.getShipments().get(0).getItems().get(0).setDescription(descriptionValue);
		
		Elements priceElement = doc.getElementsByAttributeValueContaining("style", "text-align:right;");
		Elements totalElements = doc.getElementsByAttributeValue("style", "text-align:right; font-weight:bold;");
		Element totalElement = null;
		for (Element element : totalElements) {
			totalElement = element;
		}
		
		for (Element element : priceElement) {
			if(Jsoup.parse(element.toString()).text().contains("$")){
				if(element.equals(totalElement)){
					receipt.setOrderTotal(Double.parseDouble(element.text().replace("$", "")));
				} else {
					receipt.setOrderSubTotal(Double.parseDouble(element.text().replace("$", "")));
				}
			}
		}
		return receipt;
	}

}
