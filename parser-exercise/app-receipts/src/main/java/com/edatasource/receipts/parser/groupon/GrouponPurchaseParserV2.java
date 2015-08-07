package com.edatasource.receipts.parser.groupon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edatasource.receipts.helper.ParserUtil;
import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import com.edatasource.receipts.parser.AddressParser;

/**
 * 
 * @author ciri-cuervo
 *
 */
public class GrouponPurchaseParserV2 {

	private static final Logger LOG = LoggerFactory.getLogger(GrouponPurchaseParserV2.class);
	private static final Pattern ITEM_AMOUNT_CLEANER = Pattern.compile("\\([xX](.*?)\\)");

	public static EcommerceReceipt parse(Document doc)
	{
		long startMillis = System.currentTimeMillis();

		EcommerceReceipt receipt = new EcommerceReceipt();
		EcommerceShipment shipment = new EcommerceShipment();
		List<EcommerceShipment> shipments = new ArrayList<>();
		shipments.add(shipment);

		/*
		 * NOTE
		 * 
		 * DomTraversing.nthDescendant("table", 4, doc.body())
		 * 
		 * is 10 times faster than
		 * 
		 * doc.select("body table table table table").first()
		 * 
		 * because the traverse of the DOM stops at the first occurrence, instead of traversing the
		 * complete DOM and then return the first result.
		 */

		// get the container table, then the 3 main tables
		Element cantainerTable = DomTraversalUtil.nthDescendant("table", 4, doc.body());
		Elements mainTables = DomTraversalUtil.first("td", cantainerTable).children();

		// get the order number
		String orderNumber = DomTraversalUtil.nthDescendant("tbody", 2, mainTables.get(0)).child(2).select("a").text();

		// get the rows of the 2nd main table, the one with the order info
		Elements orderInfoRows = DomTraversalUtil.nthDescendant("tbody", 3, mainTables.get(1)).children();

		// as there might be many items in the order, we look for the first after the last item
		// it is the row where the subtotals table is
		Element orderSubTotalTable = DomTraversalUtil.first("table", orderInfoRows);
		int orderSubTotalRowIndex = orderSubTotalTable.parent().parent().elementSiblingIndex();

		// iterate from row 2 to orderSubTotalRowIndex-1 and get the items
		int numberOfItems = 0;
		List<EcommerceItem> ecommerceItems = new ArrayList<>(orderSubTotalRowIndex - 2);
		for (int i = 2; i < orderSubTotalRowIndex; i++)
		{
			String itemDescription = orderInfoRows.get(i).child(0).text();
			String itemQuantityStr = orderInfoRows.get(i).child(1).text();

			EcommerceItem item = new EcommerceItem();
			item.setDescription(itemDescription);
			item.setShipment(shipment);

			// parse the number of items
			Matcher matcher = ITEM_AMOUNT_CLEANER.matcher(itemQuantityStr);
			if (matcher.find())
			{
				Integer itemQuantity = ParserUtil.getInteger(matcher.group(1));
				item.setQuantity(itemQuantity);
				numberOfItems += itemQuantity;
			}

			ecommerceItems.add(item);
		}

		Elements orderSubTotalRows = orderSubTotalTable.child(0).children();

		String orderSubTotalStr = orderSubTotalRows.get(0).child(1).text();
		Double orderSubTotal = ParserUtil.parseCurrency(orderSubTotalStr, Locale.ENGLISH).doubleValue();

		String orderTaxStr = orderSubTotalRows.get(1).child(1).text();
		Double orderTax = ParserUtil.parseCurrency(orderTaxStr, Locale.ENGLISH).doubleValue();

		String shippingAmountStr = orderSubTotalRows.get(2).child(1).text();
		Double shippingAmount = ParserUtil.parseCurrency(shippingAmountStr, Locale.ENGLISH).doubleValue();

		String orderTotalStr = orderSubTotalRows.get(3).child(1).text();
		Double orderTotal = ParserUtil.parseCurrency(orderTotalStr, Locale.ENGLISH).doubleValue();

		// 4 rows under the orderSubTotalRowIndex we find the shipping address table
		Elements shippingAddressRows = orderInfoRows.get(orderSubTotalRowIndex + 4).select("table tr");
		String shippingName = shippingAddressRows.get(0).text();
		String shippingStreet = shippingAddressRows.get(1).text();
		String shippingBox = shippingAddressRows.get(2).text();
		String shippingCity = shippingAddressRows.get(3).text();

		Address shippingAddress = AddressParser.parse(shippingName + "\n" + shippingStreet + "\n"
				+ shippingBox + "\n" + shippingCity);
		shipment.setItems(ecommerceItems);
		shipment.setShipping(shippingAddress);
		shipment.setShippingAmount(shippingAmount);
		shipment.setReceipt(receipt);

		// set all receipt information
		receipt.setNumberOfItems(numberOfItems);
		receipt.setOrderNumber(orderNumber);
		receipt.setOrderSubTotal(orderSubTotal);
		receipt.setOrderTax(orderTax);
		receipt.setOrderTotal(orderTotal);
		receipt.setShipments(shipments);
		receipt.setShippingAmount(shippingAmount);

		long endMillis = System.currentTimeMillis();
		LOG.debug("Parsing time: {} ms", (endMillis - startMillis));

		return receipt;
	}

}
