package com.edatasource.receipts.parser.groupon;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;

/**
 * @author cesaregb
 *
 */
public class GrouponPurchaseParserV2{
	
	static Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+");
	
	public static boolean validateDoc(Document doc){
		if (doc == null){
			return false;
		}
		return true;
	}
	
	public static String getOrderNumber(Document doc){
		/*
		 * Notes: 
		 * Here we can search as well for number format. 
		 * I mean if the id will be a number with or bigger than X its a good search term 
		 * We search first for the strong tag because there are fewer. Optimal search 
		 * */
		final String PARENT_ELEMENT = "strong:contains(Your order number is) a";
		String _method = "getOrderNumber";
		String result = null;
		Elements element = doc.select(PARENT_ELEMENT);
		if (element.size() >= 0){
			result = element.get(0).text();
		}else{
			System.out.println("Throw exception id not found in document");
		}
		
		System.out.println(_method + " result: " + result);
		return result; 
		
	}
	
	public static Address getBilling(Document doc){
		String _method = "getBilling";
		/* 
		 * I couldnt found the billing address. Nor the "bill address the same as shipping.. 
		 * I'm adding the payment billing info in this information. 
		 * Parsing content to wholeAddress value. 
		 * */
		
		final String PARENT_ELEMENT = "td[style^=padding: 0 0 0 20px;]:not(:has(table))";
		/*
		 * The "padding: 0 0 0 20px;" in the style element indicates that is a result field.
		 * With this approach we need to make diference on the results TDs 
		 * */
		Address result = new Address();
		Element summaryTable = getSummaryTable(doc); 
		Elements elements = summaryTable.select(PARENT_ELEMENT);
		String wholeAddress = null;
		for (Element element : elements){
			wholeAddress = element.text();
		}
		result.setWholeAddress(wholeAddress);
		System.out.println(_method + " result: " + result.getWholeAddress());
		return result;
	}
	
	public static Element getSummaryTable(Document doc){
		/*
		 * Getting summary table the best approach I found is by the table color. 
		 * we can search by content in case of needed. 
		 * or iterate thru the tables. 
		 * */
		Element result = doc.select("table[style*=#fcf6d7] table").get(0);
		return result;
	}
	
	public static Address getAddres(Document doc){
		String _method = "getAddres";
		/* 
		 * Taking different approach for Address than billing. 
		 * Approach select all TDs and select the next to the one containing "Shipping address" 
		 * makes the search more efficient instead of doing extensive search on tables. 
		 * Selectors usage in billing example. 
		 * */
		
		final String PARENT_ELEMENT = "td";
		Address result = new Address();
		Element summaryTable = getSummaryTable(doc); 
		Elements elements = summaryTable.select(PARENT_ELEMENT);
		boolean selectMe = false;
		Element addressElement = null; 
		for (Element element : elements){ // we iterate around 20 
			if (selectMe){
				addressElement = element.child(0);
				break;
			}
			if (element.text().toLowerCase().contains("shipping address")){
				selectMe = true;
			}
		}
		String fullName = addressElement.select("td").get(0).text();
		String streetInfo = addressElement.select("td").get(1).text();
		String boxInfo = addressElement.select("td").get(2).text(); //supouse this contains box info.
		String locationInfo = addressElement.select("td").get(3).text();
		String fName = fullName.substring(0, fullName.indexOf(" "));
		String lName = fullName.substring(fullName.indexOf(" ") + 1 , fullName.length());
		result.setFirstName(fName.trim());
		result.setLastName(lName.trim());
		result.setStreetOrBoxInfo(streetInfo + boxInfo);
		//making assumption to match "Sun Valley, California 12345-1234" 
		//no regex can be made if the address format changes. 
		String city = locationInfo.substring(0, locationInfo.indexOf(","));
		result.setCity(city.trim());
		String state = locationInfo.substring(locationInfo.indexOf(",") + 1 , locationInfo.lastIndexOf(" "));
		result.setState(state.trim());
		String cp = locationInfo.substring(locationInfo.lastIndexOf(" "), locationInfo.length());
		result.setPostalCode(cp.trim());
		
		System.out.println(_method + " result: " + result.toString());
		return result;
	}
	
	public static List<EcommerceShipment> getShipments(Document doc){
		// here is not clear if we are going to get an array of shippment tables.. 
		// im considereing only one at the moment but this can be expended. 

		/* 
		 * Maybe some EcommerceShipment information missing I couldnt decompile the EcommerceShipment class
		 * Added as much as the api indicated. 
		 * */
		List<EcommerceShipment> shipments = new ArrayList<EcommerceShipment>();
		EcommerceShipment es = new EcommerceShipment();
		es.setItems(getOrderItems(doc));
		es.setShipping(getAddres(doc));
		shipments.add(es);
		return shipments;
	}
	
	public static List<EcommerceItem> getOrderItems(Document doc){
		String _method = "getOrderItems";
		List<EcommerceItem> list = new ArrayList<>();
		final String PARENT_ELEMENT = "td[style^=padding: 10px 0 10px 20px;]";
		Element summaryTable = getOrderSummaryTable(doc); 
		Elements elements = summaryTable.select(PARENT_ELEMENT);
		for (Element e : elements){
			EcommerceItem item = new EcommerceItem();
			item.setDescription(e.text());
			list.add(item);
		}
		System.out.println(_method + " # Items:: " + list.size());
		return list;
	}
	
	
	public static Element getOrderSummaryTable(Document doc){
		final String PARENT_ELEMENT = "table:contains(Order summary)";
		Element summaryTable = getSummaryTable(doc); 
		Element element = summaryTable.select(PARENT_ELEMENT).get(0);
		return element;
	}
	
	/**
	 * Method used for subtotal, total, taxes of the table.. 
	 * @param doc
	 * @param type
	 * @return Double
	 */
	public static Double getAmountByType(Document doc, String type){
		String _method = "getAmountByType";
		Element summaryTable = getOrderSummaryTable(doc).select("td:has(table:matches((?i)"+type+")").first();
		summaryTable = summaryTable.select("table:matches((?i)"+type+")").first();
		Element element = summaryTable.select("td:matches((?i)\\b"+type+")").first().nextElementSibling();
		Double result = 0d;
		Double d = Double.valueOf(element.text().replaceAll("[^\\d.,]",""));
		result = d.doubleValue();
		System.out.println(_method + " result : " + result);
		return result;
	}
	
	public static int NumberOfItems(Document doc){
		String _method = "NumberOfItems";
		final String PARENT_ELEMENT = "td:matchesOwn(\\((.*?)\\))"; // search for  (X#) pattern
		Element summaryTable = getOrderSummaryTable(doc); 
		Elements elements = summaryTable.select(PARENT_ELEMENT);
		int total = 0;
		for (Element e : elements){ // we dont know how many products we have.. we iterate them 
			Matcher m = NUMBER_PATTERN.matcher(e.text());
			while (m.find()) { // from the string we expect only one number.. 
				total += Integer.parseInt(m.group());
			}
		}
		System.out.println(_method + " total: " + total);
		return total;
	}
	
	public static EcommerceReceipt parse(Document doc)  {
		EcommerceReceipt result = null;
		if (validateDoc(doc)){
			result = new EcommerceReceipt();
			result.setOrderNumber(getOrderNumber(doc));
			result.setBilling(getBilling(doc));
			result.setShipments(getShipments(doc));
			result.setNumberOfItems(NumberOfItems(doc));
			result.setOrderSubTotal(getAmountByType(doc, "Subtotal"));
			result.setOrderTax(getAmountByType(doc, "Tax"));
			result.setShippingAmount(getAmountByType(doc, "Shipping & Handling"));
			result.setOrderTotal(getAmountByType(doc, "Total"));
		}
		return result;
	}

	
	public static void main(String... args) throws Exception {
		byte[] encoded = Files.readAllBytes(Paths.get("./app-receipts/test-data/groupon/valid_v2/V2_regexUpdate1.html"));
		String HTML = new String(encoded, Charset.defaultCharset());
		Document doc = Jsoup.parse(HTML);
		GrouponPurchaseParserV2.parse(doc);
	}

}
