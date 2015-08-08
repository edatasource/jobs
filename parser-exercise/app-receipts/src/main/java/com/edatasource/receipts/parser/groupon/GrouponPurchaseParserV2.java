package com.edatasource.receipts.parser.groupon;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edatasource.receipts.helper.ParserUtil;
import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import com.edatasource.receipts.parser.AddressParser;

public class GrouponPurchaseParserV2{

	public static EcommerceReceipt parse(Document doc)  {
		
		// Look for element with "Order Summary"
		Element orderSummayTag = doc.select(":containsOwn(Order Summary)").first();
		int orderSummaryIndex = orderSummayTag.parent().elementSiblingIndex();
		
		// Look for element with "Billed to"
		Element billToTag = doc.select(":containsOwn(Billed to)").first();
		int billToIndex = billToTag.parent().elementSiblingIndex();

		// Look for element with "Shipping address"
		Element shippingHeaderTag = doc.select(":containsOwn(Shipping address)").first();
		
		EcommerceShipment shipment = new EcommerceShipment();
		shipment.setShipping(extractShippingAddress(shippingHeaderTag));

		return generateReceipt(shipment, orderSummayTag, orderSummaryIndex, billToIndex);
		
	}
	
	/*
	 * Generate Receipt based on shipment
	 */
	private static EcommerceReceipt generateReceipt(EcommerceShipment shipment, Element orderSummayTag, int startIndex, int endIndex) {
		
		EcommerceReceipt receipt = new EcommerceReceipt();
		
		Elements parentElement =  orderSummayTag.parent().parent().children();
		for(Element element: parentElement) {
			
			if(element.elementSiblingIndex() <= startIndex) {
				continue;
			}
			
			if(element.elementSiblingIndex() >= endIndex) {
				break;
			}
			
			Element subtotalTag = element.select(":containsOwn(Subtotal)").first();
			// Process the pricing
			if(subtotalTag != null) {
				//System.out.println("Subtotal : " + subtotalTag);
				
				Elements summaryElements =  subtotalTag.parent().parent().children();
				
				// Subtotal
				String subTotalAmount = summaryElements.get(0).children().get(1).ownText();
				receipt.setOrderSubTotal(ParserUtil.dollarsToDouble(subTotalAmount));
				
				// Tax
				String taxAmount = summaryElements.get(1).children().get(1).ownText();
				
				// Shipping and Handling
				String shppingAmount = summaryElements.get(2).children().get(1).ownText();
				
				// Total
				String totalAmount = summaryElements.get(3).children().get(1).ownText();
				receipt.setOrderTotal(ParserUtil.dollarsToDouble(totalAmount));
				
			} else {
				// Handle Item listing
				Element itemLabel = element.children().first();
				if(itemLabel.ownText().startsWith("Order placed")) {
					continue;
				}

				// Extract item quantity
				Element qtyElement = element.children().get(1);
				String qty = qtyElement.ownText().replaceAll("\\D", "");
				
				EcommerceItem item = new EcommerceItem();
				item.setDescription(itemLabel.ownText());
				item.setQuantity(Integer.valueOf(qty));
				
				shipment.addItem(item);

			}
			
		}
		
		receipt.addShipment(shipment);
		
		return receipt;
	}
	
	/*
	 * Extract shipping address
	 */
	private static Address extractShippingAddress(Element shippingHeaderTag) {
		
		Element shippingAddressElement =  shippingHeaderTag.parent().nextElementSibling();
		
		// Look for element with "Address incorrect"
		Element addressCorrectTag = shippingAddressElement.select(":containsOwn(Address incorrect)").first();
		
		// Get the sibling elements 
		Elements addressElements = addressCorrectTag.parent().parent().children();
		
		StringBuilder buff = new StringBuilder();
		String lastName = "";
		String firstName = "";
		for(Element el: addressElements) {
			
			// Skip the element if no children
			if(el.children().isEmpty()) {
				continue;
			}
		
			Element dataElement = el.children().first();

			// Process names
			if(el.siblingIndex() == 0) {
				String fullname = dataElement.ownText();
				int spaceIndex = fullname.indexOf(" ");
				if(spaceIndex <= 0) {
					firstName = fullname;
				} else {
					firstName = fullname.substring(0, spaceIndex);
					lastName = fullname.substring(spaceIndex+1);
				}
				continue;
			}
			
			// Stop when reached "Address incorrect" element
			if(dataElement.ownText().startsWith("Address incorrect") ) {
				break;
			}
			
			// Skip if no data
			if("".equals(dataElement.ownText())) {
				continue;
			}
			
			// Check data ending with ",". Add it if not present.
			// Otherwise AddressParser would fail
			if(dataElement.ownText().trim().endsWith(",")) {
				if(!buff.toString().isEmpty()) {
					buff.append(" ");
				}
			} else {
				if(!buff.toString().isEmpty()) {
					buff.append(", ");
				}
			}
			buff.append(dataElement.ownText());
		}
		
		Address shipping = AddressParser.parse(buff.toString());
		shipping.setFirstName(firstName);
		shipping.setLastName(lastName);
		
		
		return shipping;
	}

}
