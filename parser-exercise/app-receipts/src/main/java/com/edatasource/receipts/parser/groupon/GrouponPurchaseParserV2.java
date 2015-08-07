package com.edatasource.receipts.parser.groupon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.edatasource.receipts.helper.ParserUtil;
import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import com.edatasource.receipts.parser.AddressParser;

/**
 * This is the sample email template parser for {@link V2_regexUpdate1.html}.
 * 
 * @author VishalZanzrukia
 *
 */
public class GrouponPurchaseParserV2 {

	public static final String FILE_PATH = "./app-receipts/test-data/groupon/valid_v2/V2_regexUpdate1.html";
	
	/** regex used in this example */
	public static final String ORDER_DATE_REGEX = "(January|February|March|April|May|June|July|August|September|October|November|December)[-  .](0*[1-9]|[12][0-9]|3[01])[- ,.](\\ *)(19|20)\\d\\d";
	public static final String ORDER_ITEM_REGEX = "(.*?)(\\(x)(\\d{1,3})(\\))";
	
	/** selectors used in this example */
	public static final String ORDER_SUMMARY_SELECTOR = "table tbody tr td:eq(1) table tbody tr:eq(2) td table tbody tr td table tbody tr td table:eq(1) tbody tr td table tbody tr td table tbody";
	
	/**below selectors are relative to {@link ORDER_SUMMARY_SELECTOR}*/
	public static final String ORDER_NUMBER_SELECTOR = "a[style]";
	public static final String ORDER_DATE_SELECTOR = "tr:eq(1) td";
	public static final String ORDER_ITEMS_SELECTOR = "tr + tr + tr";
	public static final String ORDER_BILLING_SELECTOR = "td:matches(%s) + td";
	
	/** string constants used in this example */
	public static final String SHIPPING_ADDRESS = "Shipping address:";
	public static final String SUBTOTAL = "Subtotal";
	public static final String TAX = "Tax";
	public static final String SHIPPING_AND_HANDLING = "Shipping & Handling";
	public static final String TOTAL = "Total";

	/**
	 * Parsing the order number
	 * 
	 * @param doc
	 *            document
	 * @return order number
	 */
	private static Optional<String> parseOrderTotalValue(Document doc) {
		Element element;
		String text;
		/** handling for null scenario */
		if ((element = doc.select(ORDER_NUMBER_SELECTOR).first()) != null && (text = element.text()) != null) {
			return Optional.of(text);
		}
		return Optional.empty();
	}

	/**
	 * Parsing the order date
	 * 
	 * @param desc
	 * @return
	 */
	private static Optional<String> parseOrderDate(Element orderParentElement) {
		Element orderPlaceDateElement = orderParentElement.select(ORDER_DATE_SELECTOR).first();
		Matcher m = Pattern.compile(ORDER_DATE_REGEX).matcher(orderPlaceDateElement.text());
		if (m.find()) {
			return Optional.of(m.group());
		}
		return Optional.empty();
	}

	/**
	 * Parsing the shipping address
	 * 
	 * @param orderParentElement
	 * @return
	 */
	private static Address parseShippingAddress(Element orderParentElement) {

		/** getting the address string */
		String adddressString = orderParentElement.getElementsContainingOwnText(SHIPPING_ADDRESS)
				.first()
				.parent()
				.nextElementSibling()
				.select("tbody")
				.first()
				.getElementsByTag("td")
				.parallelStream()
				.filter(e -> !e.hasAttr("style"))
				.filter(e -> !e.text().isEmpty())
				.map(Element::text)
				.collect(Collectors.joining("\n"));

		/** using the AddressParser for getting Address domain */
		return AddressParser.parse(adddressString);
	}

	/**
	 * parsing order items
	 * 
	 * @param orderParentElement
	 *            root element
	 * @return order items
	 */
	private static List<EcommerceItem> parseOrderItems(Element orderParentElement) {
		List<EcommerceItem> items = new ArrayList<>();
		Pattern orderItemPattern = Pattern.compile(ORDER_ITEM_REGEX);
		Matcher matcher = orderItemPattern.matcher(orderParentElement.select(ORDER_ITEMS_SELECTOR).first().text());
		if (matcher.find()) {
			EcommerceItem item = new EcommerceItem();
			item.setDescription(matcher.group(1).trim());
			item.setQuantity(Integer.parseInt(matcher.group(3)));
			items.add(item);
		}
		return items;
	}

	/**
	 * get the shipments
	 * 
	 * @param doc
	 * @return shipments
	 */
	private static List<EcommerceShipment> getShipments(Element orderParentElement) {
		List<EcommerceShipment> shipments = new ArrayList<>();
		EcommerceShipment shipment = new EcommerceShipment();
		shipment.setItems(parseOrderItems(orderParentElement));
		shipment.setShipping(parseShippingAddress(orderParentElement));
		shipments.add(shipment);
		return shipments;
	}
	
	
	
	/**
	 * @param orderParentElement
	 * @param selector
	 * @return
	 */
	private static Optional<Double> parseOrderBillingInfo(Element orderParentElement, String type) {
		String text;
		if ((text = orderParentElement.select(String.format(ORDER_BILLING_SELECTOR, type)).first().text()) != null) {
			return Optional.of(ParserUtil.dollarsToDouble(text));
		}
		return Optional.empty();
	}
	

	/**
	 * Parsing the document.
	 * 
	 * @param doc
	 * @return
	 */
	public static EcommerceReceipt parse(Document doc) {

		EcommerceReceipt ecommerceReceipt = new EcommerceReceipt();

		/** extracting the order number */
		ecommerceReceipt.setOrderNumber(parseOrderTotalValue(doc).get());

		/** getting the base element for all the order details element */
		Element orderParentElement = doc.select(ORDER_SUMMARY_SELECTOR).first();

		/** getting the order date */
		//TODO need to set order place date in any domain, but not getting date field
		System.out.println("Order date :: " + parseOrderDate(orderParentElement).get());

		/** parsing and setting the shipping options */
		ecommerceReceipt.setShipments(getShipments(orderParentElement));

		/** parsing billing related info */
		ecommerceReceipt.setOrderTax(parseOrderBillingInfo(orderParentElement, TAX).get());
		ecommerceReceipt.setOrderTotal(parseOrderBillingInfo(orderParentElement, TOTAL).get());
		ecommerceReceipt.setOrderSubTotal(parseOrderBillingInfo(orderParentElement, SUBTOTAL).get());
		ecommerceReceipt.setShippingAmount(parseOrderBillingInfo(orderParentElement, SHIPPING_AND_HANDLING).get());

		return ecommerceReceipt;
	}

}
